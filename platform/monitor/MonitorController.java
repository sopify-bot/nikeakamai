package nike.platform.monitor;

import nike.common.tool.RegexParse;
import nike.common.tool.Time;
import nike.monitor.BIGMonitorDao;
import nike.platform.persion.PersionDao;
import nike.platform.task.Task;
import nike.platform.task.TaskDao;
import nike.proxy.IProxy;
import nike.proxy.MyIp;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MonitorController {

    @Autowired
    MonitorDao monitorDao;

    @Autowired
    PersionDao persionDao;

    @Autowired
    TaskDao taskDao;




    @Autowired
    BIGMonitorDao bigMonitorDao;

    private Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @RequestMapping("monitor")
    public String monitor(Model model, @RequestParam(name = "page",defaultValue ="0") Integer page
            , @RequestParam(name = "search",defaultValue ="") String search, @RequestParam(name = "tap",defaultValue ="all") String tap
    ) {
        model.addAttribute("monitor",logic(page,search,tap));
        model.addAttribute("isize",getSize());
        model.addAttribute("search",search);
        model.addAttribute("tap",tap);
        return "monitor";

    }

    private Page<Monitor> logic(Integer page, String search, String tap){
        search=search==null?null:search.trim();
        Page<Monitor> pages= null;
        if(search==null||"".equals(search))
            pages=basic(page,tap);
        else if(search.length()==10&&search.contains("-")&& RegexParse.ismatching(search,"[A-Z\\d]{6}-[\\d]{3}")){
            search=search.trim();
            //能进来这里，说明搜索的是货号；
            if(monitorDao.findOneByStyleColor(search)==null)
            {
                //进入这里是说明,库中没有这个货号,临时去采集;
                logger.info("库存没有.临时采集");
                retryNoProduct(search);
            }
            pages=monitorDao.findAllByStyleColor(PageRequest.of(page, 200),search);
        }else{
            search=search.trim();
            pages=searchByStyleColor(page,search);
        }
        return pages;
    }

    private Page<Monitor> basic(Integer page, String tap){
        if("all".equals(tap))
            return monitorDao.findAll(PageRequest.of(page, 200) );
        if("replenishStatus".equals(tap))
            return monitorDao.findAllByReplenishStatus(PageRequest.of(page, 200),1);
        if("exactTime".equals(tap))
            return monitorDao.findAllByExactTime(PageRequest.of(page, 200),1);
        if("alreadyInvolved".equals(tap))
            return monitorDao.findAllAlreadyInvolved(PageRequest.of(page, 200));
        return monitorDao.findAll(PageRequest.of(page, 200) );
    }

    private Page<Monitor> searchByStyleColor(Integer page, String search){
        return monitorDao.findAllByFullTitleOrStyleColor(PageRequest.of(page, 200),search,search);
    }



    @RequestMapping("/saveTask")
    public String saveTask(RushPurchaseConfiguration rushPurchaseConfiguration, Model model)  {
        Monitor monitor=monitorDao.findOneById(rushPurchaseConfiguration.getMonitorId());
        rushPurchaseConfiguration.getChooseSize().forEach(size-> {
            Integer monitorId =rushPurchaseConfiguration.getMonitorId();
            String detailUrlId=rushPurchaseConfiguration.getDetailUrlId();
            Integer limit=monitorDao.findOneById(monitorId).getQuantityLimit();
            System.out.println(monitorId+"|style_code"+monitor.getStyleColor()+"|size:"+size+"|"+rushPurchaseConfiguration.getDetailUrlId());
            try{
                taskDao.save(new Task()
                        .setMonitorId(monitorId)
                        .setBuyTotal(Integer.valueOf(limit))
                        .setDetailUrlId(rushPurchaseConfiguration.getDetailUrlId())
                        .setSize(size)
                        .setStyleColor(monitor.getStyleColor())
                        .setCurrentPrice(monitor.getCurrentPrice())
                        .setProductId(monitor.getProductId())
                        .setTaskStatus(1)
                );
            }catch (Exception e){
                logger.error("添加任务的时候违反唯一性约束,也就是同一个货号，尺码，人群购买!");
            }
        });
        return  "redirect:/monitor";
    }

    private  static List<List<String>> getSize(){
        List<String> list1= Arrays.asList(
                "35.5",
                "36",
                "36.5",
                "37.5",
                "38",
                "38.5"
        );
        List<String> list2= Arrays.asList(
                "39",
                "40",
                "40.5",
                "41",
                "42",
                "42.5"
        );
        List<String> list3= Arrays.asList(
                "43",
                "44",
                "44.5",
                "45",
                "45.5",
                "46"
        );
        List<String> list4= Arrays.asList(
                "46.5",
                "47.5",
                "48",
                "XXS",
                "XS",
                "S"
        );
        List<String> list5= Arrays.asList(
                "M",
                "L",
                "XL",
                "2XL",
                "均码"
        );
        ArrayList arrayList=new ArrayList();
        arrayList.add(list1);
        arrayList.add(list2);
        arrayList.add(list3);
        arrayList.add(list4);
        arrayList.add(list5);
        return  arrayList;
    }


    private void retryNoProduct(String style_color){
        for(int i=0;i<3;i++){
            try {
                //excuteNoProduct(style_color);
                excuteNoProductByStyleColor(style_color);
                break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /***
     * 下面是补充采集货号的
     */

    public Monitor excuteNoProduct(String style_color) throws IOException, JSONException {
            String deatilUrlId=getDeatilUrlId(style_color);
            String result=getDeatiPage(deatilUrlId);
            parserPageHTML(result);
            return  null;
    }

    private  String  getDeatilUrlId(String style_color) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        MyIp myIp= IProxy.getIp();
        HttpGet httpget = new HttpGet("https://www.nike.com/cn/t/234234/"+style_color);
        httpget.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        httpget.setHeader("referer", "http://www.nike.com");
        httpget.setHeader("accept", "application/json");
        httpget.setHeader("nike-api-caller-id", "nike:com.nike.omega:ios:2.120");
        httpget.setHeader("accept-encoding", "br;q=1.0, gzip;q=0.9, deflate;q=0.8");
        httpget.setHeader("accept-language", "zh-Hans-CN;q=1.0, en-CN;q=0.9");
        httpget.setHeader("content-type", "application/json; charset=utf-8");
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(new HttpHost(myIp.getIp(),myIp.getPort()))//设置代理
                .setConnectTimeout(5000)//设置连接超时时间,单位毫秒
                .setSocketTimeout(5000)//设置读取超时时间,单位毫秒
                .setConnectionRequestTimeout(5000)
                .build();
        httpget.setConfig(requestConfig);
        CloseableHttpResponse response = httpclient.execute(httpget);
        String deatilUrlId= RegexParse.baseParse(EntityUtils.toString(response.getEntity()),"\""+style_color+"\"\\s*?:\\s*?\\{[^\\{]*?\"threadId\"\\s*?:\\s*?\"([\\s\\S]*?)\"",1);
        return deatilUrlId;
    }

    private  String  getDeatiPage(String detailUrlId) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url= "https://api.nike.com/product_feed/threads/v2?filter=exclusiveAccess(true,false)&filter=channelId(d9a5bc42-4b9c-4976-858a-f159cf99c647)&filter=marketplace(CN)&filter=language(zh-Hans)&filter=publishedContent.subType(soldier,officer,nikeid_soldier,nikeid_officer)&filter=id("+detailUrlId+")&filter=productInfo.merchProduct.channels(NikeApp)";
        MyIp myIp=IProxy.getIp();
        CloseableHttpResponse response=null;
        String result=null;
        try{
        HttpGet httpget = new HttpGet(url);
        httpget.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        httpget.setHeader("referer", "http://www.nike.com");
        httpget.setHeader("accept", "application/json");
        httpget.setHeader("nike-api-caller-id", "nike:com.nike.omega:ios:2.120");
        httpget.setHeader("accept-encoding", "br;q=1.0, gzip;q=0.9, deflate;q=0.8");
        httpget.setHeader("accept-language", "zh-Hans-CN;q=1.0, en-CN;q=0.9");
        httpget.setHeader("content-type", "application/json; charset=utf-8");
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(new HttpHost(myIp.getIp(),myIp.getPort()))//设置代理
                .setConnectTimeout(5000)//设置连接超时时间,单位毫秒
                .setSocketTimeout(5000)//设置读取超时时间,单位毫秒
                .setConnectionRequestTimeout(5000)
                .build();
        httpget.setConfig(requestConfig);
        response = httpclient.execute(httpget);
        result= EntityUtils.toString(response.getEntity());
        }finally {
            response.close();
        }
        return result ;
    }

    public Monitor excuteNoProductByStyleColor(String style_color) throws IOException, JSONException {
        String result=getDeatiPageByStyleColor(style_color);
        parserPageHTMLByStyleColor(result,style_color);
        return  null;
    }

    private  String  getDeatiPageByStyleColor(String style_color) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url="https://api.nike.com/product_feed/threads/v2?filter=exclusiveAccess(true,false))&filter=channelId(d9a5bc42-4b9c-4976-858a-f159cf99c647)&filter=marketplace(CN)&filter=language(zh-Hans)&searchTerms="+style_color;
        MyIp myIp=IProxy.getIp();
        CloseableHttpResponse response=null;
        String result=null;
        try{
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            httpget.setHeader("referer", "http://www.nike.com");
            httpget.setHeader("accept", "application/json");
            httpget.setHeader("nike-api-caller-id", "nike:com.nike.omega:ios:2.120");
            httpget.setHeader("accept-encoding", "br;q=1.0, gzip;q=0.9, deflate;q=0.8");
            httpget.setHeader("accept-language", "zh-Hans-CN;q=1.0, en-CN;q=0.9");
            httpget.setHeader("content-type", "application/json; charset=utf-8");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(new HttpHost(myIp.getIp(),myIp.getPort()))//设置代理
                    .setConnectTimeout(5000)//设置连接超时时间,单位毫秒
                    .setSocketTimeout(5000)//设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(5000)
                    .build();
            httpget.setConfig(requestConfig);
            response = httpclient.execute(httpget);
            result= EntityUtils.toString(response.getEntity());
        }finally {
            response.close();
        }
        return result ;
    }

    private void parserPageHTMLByStyleColor(String result,String style_color) throws JSONException {
        JSONObject jsonObject = new JSONObject(result.toString());
        JSONObject pages = jsonObject.getJSONObject("pages");
        //System.out.println("本页总共:"+pages.get("totalPages")+"条");
        JSONArray objects = jsonObject.getJSONArray("objects");
        Monitor monitor=null;
        for (int i = 0; i < objects.length(); i++) {
            JSONArray productInfo =objects.getJSONObject(i).getJSONArray("productInfo");
            if (productInfo.getJSONObject(0).getJSONObject("merchProduct").getString("styleColor").equalsIgnoreCase(style_color)) {
                monitor=parserPage(objects.getJSONObject(i));
                break;
            }
        }
        if(monitor!=null)
        flushDB(monitor);
    }

    private void parserPageHTML(String result) throws JSONException {
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
                flushDB(monitors);
            } catch (Exception e) {
                logger.error("单个的出现错误忽略,本轮错误数字:");
            }
        }
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


    /***
     * 将数据更新到数据库
     */
    private  int flushDB(Monitor monitor){
        //记录更新的条数
        int total=0;
            try {
                //数据库中的数据
                Monitor monitorTemp=bigMonitorDao.findOneByStyleColor(monitor.getStyleColor());
                if(monitorTemp==null){
                    //这个是解析的出来的数据....
                    bigMonitorDao.save(monitor.setScanDate(Time.createTime(0)));
                }else{
                    //这里列出来要更新的数据....
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
                    //更新...
                    bigMonitorDao.save(monitorTemp);
                }
                total++;
            }catch (Exception e){
                logger.error("大监控-更新数据到数据库-出错!如果不是太连续出错,问题不大!",e);
            }
        return total;
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
                    bigMonitorDao.save(monitor.setScanDate(Time.createTime(0)));
                }else{
                    //这里列出来要更新的数据....
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


}
