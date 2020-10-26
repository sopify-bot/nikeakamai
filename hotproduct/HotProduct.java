package nike.hotproduct;

import nike.common.tool.SslUtils;
import nike.common.tool.Time;
import nike.platform.monitor.Monitor;
import nike.platform.monitor.MonitorDao;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 热品
 */
@Component
public class HotProduct {


    private Logger logger = LoggerFactory.getLogger(HotProduct.class);


    @Autowired
    HotProductDao hotProductDao;

    @Autowired
    MonitorDao monitorDao;

    public  void go(){
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                int id=nike.common.tool.Math.romdom(-1,(int)hotProductDao.count());
                while (true){
                    try {
                        id++;
                        Monitor monitor= hotProductDao.findNextOneById(id, Time.createTime(0));
                        if(monitor==null)
                            id=0;
                        retryReplenish(monitor);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            logger.error("补货监控的时候出错",e1);
                        }
                    }catch (Throwable e){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            logger.error("补货监控的时候出错",e1);
                        }
                        logger.error("补货监控-出现错误!",e);
                    }
                }
            }
        });
    }

    /***
     * get 请求方法
     * @return
     * @throws IOException
     */
    private String getAPI(String urlPath) throws IOException {
        MyIp myIp= IProxy.getIp();
        if(myIp==null)
        {
            logger.info("获取IP为null");
            return null;
        }
        HttpURLConnection connection=null;
        StringBuilder result = null;
        try {
            SslUtils.ignoreSsl();
            result = new StringBuilder();
            URL url = new URL(urlPath);
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
        if(result==null)
            return null;
        return result.toString();
    }

    /**
     * 获取接口的数据.
     * @return
     * @throws IOException
     */
    private  String getResult(String idsT) throws IOException {
        StringBuilder url=new StringBuilder();
        url.append("https://api.nike.com/product_feed/threads/v2?filter=exclusiveAccess(true,false))");
        url.append("&filter=channelId(d9a5bc42-4b9c-4976-858a-f159cf99c647)&filter=marketplace(CN)&filter=language(zh-Hans)");
        url.append("&filter=publishedContent.subType(soldier,officer,nikeid_soldier,nikeid_officer)");
        url.append("&filter=id("+idsT+")");
        url.append("&filter=productInfo.merchProduct.channels(NikeApp)");
        return getAPI(url.toString());
    }

    /**
     * @param result
     * @throws JSONException
     */
    private   boolean parserJson(String result) throws JSONException {
        JSONObject jsonObject=new JSONObject(result.toString());
        JSONObject pages=jsonObject.getJSONObject("pages");
        JSONArray objects=jsonObject.getJSONArray("objects");
        JSONObject object = objects.getJSONObject(0);
        //有库存的尺码
        Set<String> inStock=new HashSet<>();
        Set<String> allStock=new HashSet<>();
        //-----------------------------------------------------------
        JSONArray productInfo = object.getJSONArray("productInfo");
        JSONObject firstProductInfo = productInfo.getJSONObject(0);
        HashMap<String,String> map=new HashMap<>();
        if(!firstProductInfo.isNull("skus")){
            JSONArray skus=firstProductInfo.getJSONArray("skus");
            for(int i1=0;i1<skus.length();i1++){
                JSONObject sku=skus.getJSONObject(i1).getJSONArray("countrySpecifications").getJSONObject(0);
                map.put(skus.getJSONObject(i1).get("id").toString(),sku.get("localizedSize").toString());
            }
        }

        if(!firstProductInfo.isNull("skus")){
            if(firstProductInfo.isNull("availableSkus"))
            for(int i2=0;i2<firstProductInfo.getJSONArray("availableSkus").length();i2++){
                JSONObject temp=firstProductInfo.getJSONArray("availableSkus").getJSONObject(i2);
                String  id= temp.get("id").toString();
                String  size=map.get(id);
                boolean available=temp.getBoolean("available");
                String  level=temp.getString("level");
                System.out.println("");
                if(available){
                    inStock.add(size);
                }
                allStock.add(size);
            }
        }
        double inStocksize=inStock.size();
        double allStocksize=allStock.size();
        if(inStocksize/allStocksize<0.3){
            return true;
        }
        return false;
    }

    /**
     * @param result
     * @throws JSONException
     */
    private   boolean parserJsonPromo(String result) throws JSONException {
        JSONObject jsonObject=new JSONObject(result.toString());
        JSONArray objects=jsonObject.getJSONArray("objects");
        JSONObject object = objects.getJSONObject(0);
        //-----------------------------------------------------------
        JSONArray productInfo = object.getJSONArray("productInfo");
        JSONObject firstProductInfo = productInfo.getJSONObject(0);
        boolean ispromo=false;
        if(!firstProductInfo.isNull("merchPrice")){
            JSONObject merchPrice=firstProductInfo.getJSONObject("merchPrice");
            if(!merchPrice.isNull("promoExclusions")){
                if(merchPrice.get("promoExclusions").toString().contains("TRUE")){
                    //是不能使用优惠券，没有ture的才是能用优惠券的。

                }else{
                    //这个是能使用优惠券的
                    ispromo=true;
                }
            }
        }

        return ispromo;
    }

    /***
     * 根据给定的信息,来查看到底哪些
     */
    private void retryReplenish(Monitor monitor){
        for(int i=0;i<2;i++){
            try {
                //这个证明页面能打得开
                boolean ishave=isHavePage(monitor.getStyleColor());
                if(!ishave)
                    return;
                //下面这个证明 尺码 确实是断码的;
                String result=getResult(monitor.getDetailUrlId());
                boolean bool=parserJson(result);
                if(ishave&&bool){
                    //所以要更新监控的内容;
                    logger.info("开始更新内容...这个是热品"+monitor.getStyleColor());
                    monitorDao.save(monitor.setReplenishStatus(1));
                }else {
                    System.out.println("非热品...."+monitor.getStyleColor());
                }
                boolean ispromo=parserJsonPromo(result);
                if(ispromo)
                    monitorDao.save(monitor.setPromo(1));
                else
                    monitorDao.save(monitor.setPromo(0));
                logger.info("更新折扣信息"+monitor.getStyleColor()+ispromo);
                break;
            } catch (Exception e) {
                logger.error("检测热品-出错!",e);
            }
        }
    }



    public boolean  isHavePage(String style_color) throws IOException {
        MyIp myIp= IProxy.getIp();
        if(myIp==null)
            logger.info("获取IP为null");
        HttpURLConnection connection=null;
        int  responseCode = 0;
        try {
            SslUtils.ignoreSsl();
            URL url = new URL("https://www.nike.com/cn/t/234234/"+style_color);
            connection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(myIp.getIp(),myIp.getPort())));//new Proxy(Proxy.Type.HTTP,new InetSocketAddress(myIp.getIp(),myIp.getPort()))
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true); // 设置该连接是可以输出的
            connection.setRequestMethod("GET"); // 设置请求方式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("cache-control", "no-store,no-cache,must-revalidate,proxy-revalidate,max-age=0");
            connection.setRequestProperty("pragma", "no-cache");
            responseCode=connection.getResponseCode();
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
        if(responseCode==200)
            return true;
        return false;
    }






}
