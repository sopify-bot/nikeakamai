package nike.monitor;

import nike.common.tool.SslUtils;
import nike.common.tool.Time;
import nike.platform.monitor.Monitor;
import nike.proxy.IProxy;
import nike.proxy.MyIp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 大监控 不需要实时性很高;
 * 对异常也可以很包容;
 * 基本上能一个稳定IP即可;
 */
@Component
public class BIGMonitor {

    private static final Logger logger = LoggerFactory.getLogger(BIGMonitor.class);

    @Autowired
    BIGMonitorDao bigMonitorDao;

    /****
     * 启动监控
     */
    public void excute(){
        List<String> urls= Arrays.asList( "https://api.nike.com/product_feed/rollup_threads/v2?consumerChannelId=82a74ac1-c527-4470-b7b0-fb5f3ef3c2e2&filter=marketplace(CN)&filter=language(zh-Hans)&t="+System.currentTimeMillis());
        System.out.println("监控....");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        for(String url:urls){
                            long STARTTIME=System.currentTimeMillis();
                            int total= monitor(url);
                            logger.info("单次总共总耗时monitor:"+((System.currentTimeMillis()-STARTTIME)/1000)+"秒,更新总条数:"+total);
                        }
                    }catch (Throwable e){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        logger.error("监控出现错误!",e);
                    }

                }
            }
        });
    }

    /***
     * 获取大接口的数据...
     * @return
     * @throws IOException
     */
    private String getPageResult(String urlPath) throws IOException {
        MyIp myIp=IProxy.getIp();
        if(myIp==null){
            logger.info("获取的ip是null的....");
        }else {
            System.out.println(myIp.getIp()+"采用的IP");
        }
        HttpURLConnection connection=null;
        StringBuilder result = null;
        try {
            result = new StringBuilder();
            SslUtils.ignoreSsl();
            URL url = new URL(urlPath);
            if(myIp==null)
                connection = (HttpURLConnection) url.openConnection();//new Proxy(Proxy.Type.HTTP,new InetSocketAddress(myIp.getIp(),myIp.getPort()))
            else
                connection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(myIp.getIp(),myIp.getPort())));//new Proxy(Proxy.Type.HTTP,new InetSocketAddress(myIp.getIp(),myIp.getPort()))
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setDoOutput(true); // 设置该连接是可以输出的
            connection.setRequestMethod("GET"); // 设置请求方式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("cache-control", "no-store,no-cache,must-revalidate,proxy-revalidate,max-age=0");
            connection.setRequestProperty("pragma", "no-cache");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) { // 读取数据
                result.append(line + "\n");
            }
            connection.disconnect();
        } catch (Exception e) {
            logger.error("基础请求失败",e);
        }finally {
            try {
                if(connection!=null)
                    connection.disconnect();
            }catch (Exception e){
                logger.error("基础请求失败-关闭连接时候...",e);
            }
        }
        return result.toString();
    }

    private int monitor(String url) throws InterruptedException {
        //记录更新的次数
        int totalNum=0;
        out:
        while (true) {
            try {
                try{
                    String result = getPageResult(url);
                    JSONObject jsonObject = new JSONObject(result.toString());
                    JSONObject pages = jsonObject.getJSONObject("pages");
                    //System.out.println("本页总共:"+pages.get("totalPages")+"条");
                    JSONArray objects = jsonObject.getJSONArray("objects");
                    for (int i = 0; i < objects.length(); i++) {
                        try {
                            List<Monitor> monitors=new ArrayList<>();
                            JSONObject object = objects.getJSONObject(i);
                            if (object.isNull("productInfo")) {
                                continue;
                            }
                            monitors.add(parserPage(object));
                            if (!object.isNull("rollup")) {
                                if(!object.getJSONObject("rollup").isNull("threads"))
                                    if (object.getJSONObject("rollup").get("threads") instanceof JSONArray){
                                        for (int j = 0; j < object.getJSONObject("rollup").getJSONArray("threads").length(); j++) {
                                            JSONObject objectR = object.getJSONObject("rollup").getJSONArray("threads").getJSONObject(j);
                                            monitors.add(parserPage(objectR));
                                        }
                                    }
                            }
                            totalNum+=flushDB(monitors);
                        } catch (Exception e) {
                            logger.error("单个的出现错误忽略,本轮错误数字:");
                        }
                    }
                    String nextPage = "https://api.nike.com" + jsonObject.getJSONObject("pages").get("next");
                    url = nextPage;
                }catch (Exception e){
                    Thread.sleep(1000);
                    logger.error("个别页面出错:",e);
                }
                if (url == null || "https://api.nike.com".equals(url.trim()))
                    break out;
            } catch (Exception e) {
                Thread.sleep(5000);
                logger.error("循环一个大监控接口时候出错,错误数:",e);
                break out;
            }
        }
        return totalNum;
    }

    /***
     * 将数据更新到数据库
     * @param monitors
     */
    private  int flushDB(List<Monitor> monitors){
        //记录更新的条数
        int total=0;
        for(Monitor monitor:monitors){
            try {
                //数据库中的数据
                Monitor monitorTemp=bigMonitorDao.findOneByStyleColor(monitor.getStyleColor());
                if(monitorTemp==null){
                    //这个是解析的出来的数据....
                    System.out.println("新增数据"+monitor.getStyleColor());
                    bigMonitorDao.save(monitor.setScanDate(Time.createTime(0)));
                }else{
                    //这里列出来要更新的数据....
                    System.out.println("更新数据"+monitor.getStyleColor());
                    if(monitor.getCurrentPrice()!=null)
                        monitorTemp.setCurrentPrice(monitor.getCurrentPrice());
                    if(monitor.getMethod()!=null)
                        monitorTemp.setMethod(monitor.getMethod());
                    if(monitor.getQuantityLimit()!=null)
                        monitorTemp.setQuantityLimit(monitor.getQuantityLimit());
                    if(monitor.getSaleStatus()!=null)
                        monitorTemp.setSaleStatus(monitor.getSaleStatus());
                    if(monitor.getStartEntryDate()!=null)
                        monitorTemp.setStartEntryDate(monitor.getStartEntryDate());
                    if(monitor.getStartEntryDate()!=null)
                        monitorTemp.setStartEntryDate(monitor.getStartEntryDate());
                    if(monitor.getPublishType()!=null)
                        monitorTemp.setPublishType(monitor.getPublishType());
                    //生成程序更新时间;
                        monitorTemp.setScanDate(Time.createTime(0));
                    if(monitor.getSaleStatus()!=null)
                        monitorTemp.setSaleStatus(monitor.getSaleStatus());
                    //更新...
                    bigMonitorDao.save(monitorTemp);
                }
                total++;
            }catch (Exception e){
                logger.error("大监控-更新数据到数据库-出错!如果不是太连续出错,问题不大!",e);
            }
        }
        return total;
    }

    /***
     * 解析大接口....数据...
     * @param object
     * @return
     * @throws JSONException
     */
    private Monitor parserPage(JSONObject object) throws JSONException {
        JSONArray productInfo = object.getJSONArray("productInfo");
        JSONObject firstProductInfo = productInfo.getJSONObject(0);
        JSONObject merchProduct = firstProductInfo.getJSONObject("merchProduct");
        Monitor monitor = new Monitor();
        if (!object.isNull("id")) {
            monitor.setDetailUrlId(object.getString("id"));
        }
        if (!merchProduct.isNull("styleColor")) {
            monitor.setStyleColor(merchProduct.getString("styleColor"));
        }
        if (!merchProduct.isNull("id")) {
            monitor.setProductId(merchProduct.getString("id"));
        }
        if (!merchProduct.isNull("quantityLimit")) {
            monitor.setQuantityLimit(merchProduct.getInt("quantityLimit"));
        }
        if (!merchProduct.isNull("publishType")) {
            monitor.setPublishType(merchProduct.getString("publishType"));
        }
        if (!firstProductInfo.isNull("productContent")) {
            JSONObject productContent = firstProductInfo.getJSONObject("productContent");
            if (!productContent.isNull("fullTitle")) {
                monitor.setFullTitle(productContent.getString("fullTitle"));
            }else if (!productContent.isNull("slug")) {
                monitor.setFullTitle(productContent.getString("slug"));
            }
        }
        if (!firstProductInfo.isNull("imageUrls")) {
            JSONObject imageUrls = firstProductInfo.getJSONObject("imageUrls");
            if (!imageUrls.isNull("productImageUrl")) {
                monitor.setImg(imageUrls.getString("productImageUrl"));
            }
        }
        if (!merchProduct.isNull("status")) {
            monitor.setSaleStatus(merchProduct.getString("status"));
        }
        if (!merchProduct.isNull("genders")) {
            monitor.setGenders(merchProduct.get("genders").toString());
        }
        if (!merchProduct.isNull("channels")) {
            monitor.setChannels(merchProduct.get("channels").toString());
        }
        if (!firstProductInfo.isNull("launchView")) {
            JSONObject launchView = firstProductInfo.getJSONObject("launchView");
            if (!launchView.isNull("startEntryDate")) {
                monitor.setStartEntryDate(Time.UTCToCST(launchView.get("startEntryDate").toString()));
            }
            if (!launchView.isNull("method")) {
                monitor.setMethod(launchView.getString("method"));
            }
        }
        if (!object.isNull("publishedContent")) {
            JSONObject publishedContent = object.getJSONObject("publishedContent");
            if (!publishedContent.isNull("viewStartDate")) {
                monitor.setViewStartDate(Time.UTCToCST(publishedContent.get("viewStartDate").toString()));
            }
        }
        JSONObject merchPrice = firstProductInfo.getJSONObject("merchPrice");
        if (!merchPrice.isNull("currentPrice")) {
            monitor.setCurrentPrice(merchPrice.getDouble("currentPrice"));
        }
        if (!merchPrice.isNull("fullPrice")) {
            monitor.setFullPrice(merchPrice.getDouble("fullPrice"));
        }
        return monitor;
    }

    public static void main(String[] args) {

    }

}
