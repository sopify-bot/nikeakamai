package nike.persioninit;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import nike.common.tool.RegexParse;
import nike.platform.persion.Persion;
import nike.proxy.IProxy;
import nike.proxy.MyIp;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

/***
 * 账号初始化\也就是登录,然后获取人员信息；r
 * 然后同步到数据库;
 */
@Component
public class PersionInit {

    @Autowired
    PersionInitDao persionInitDao;

    private CloseableHttpClient  httpclient =null;

    private Logger logger = LoggerFactory.getLogger(PersionInit.class);

    public void go(){
        List<Persion> persionList=persionInitDao.findPersionsByPhoneNumbercAndClient_id();
        for(Persion persion:persionList){
            try {
                String Refresh_token=persion.getRefresh_token();
                String Client_id=persion.getClient_id();
                MyIp myIp= IProxy.getIp();
                httpclient = HttpClients.custom().setProxy(new HttpHost(myIp.getIp(),myIp.getPort())).build();
                String authorization=login(Refresh_token,Client_id);
                if(authorization.length()<50)
                    continue;
                InnerAccount innerAccount=getAccount(authorization);
                persionInitDao.save(
                        persion.setFirstName(innerAccount.getFirstName())
                                .setLastName(innerAccount.getLastName())
                                .setPhoneNumber(innerAccount.getPhone())
                );
                System.out.println("更新");
            }catch (Throwable e){
                logger.error("",e);
            }
        }
    }


    @Accessors(chain = true)
    public  static  class InnerAccount{
        @Getter @Setter
        private String firstName;
        @Getter @Setter
        private String lastName;
        @Getter @Setter
        private String phone;
    }


    private String login(String Refresh_token,String Client_id) throws IOException {
        CloseableHttpResponse response=null;
        String result=null;
        String url="https://unite.nike.com/tokenRefresh?appVersion=786&experienceVersion=786&uxid=com.nike.commerce.omega.ios.2.111&locale=zh_CN&backendEnvironment=identity&browser=Apple%20Computer%2C%20Inc.&os=undefined&mobile=true&native=true&visit=1";
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"refresh_token\": \""+Refresh_token+"\",");
        stringBuilder.append("\"client_id\": \""+Client_id+"\",");
        stringBuilder.append("\"grant_type\": \"refresh_token\"");
        stringBuilder.append("}");
        HttpPost httpPost=new HttpPost(url);
        httpPost.setHeader("user-agent", "NikePlus/2.119.1 (com.nike.omega; build:2007030030; iOS 13.5.1) Alamofire/5.1.0");
        httpPost.setHeader("referer","http://www.nike.com");
        httpPost.setHeader("accept","application/json");
        httpPost.setHeader("nike-api-caller-id","nike:com.nike.omega:ios:2.119");
        httpPost.setHeader("accept-language","zh-Hans-CN;q=1.0, en-CN;q=0.9");
        httpPost.setHeader("content-type","application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(stringBuilder.toString(),"utf-8"));
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)//设置连接超时时间,单位毫秒
                .setSocketTimeout(3000)//设置读取超时时间,单位毫秒
                .setConnectionRequestTimeout(3000)
                .build();
        httpPost.setConfig(requestConfig);
        response = httpclient.execute(httpPost);
        result= EntityUtils.toString(response.getEntity());
        response.close();
        String authorization="Bearer "+ RegexParse.baseParse(result,"access_token\":\"([\\S]*?)\"",1);
        return authorization;
    }


    InnerAccount getAccount(String authorization) throws IOException, JSONException {
        CloseableHttpResponse response=null;
        String result=null;
        HttpGet httpget = new HttpGet("https://api.nike.com/user/sharedprofile");
        httpget.setHeader("user-agent", "NikePlus/2.119.1 (com.nike.omega; build:2007030030; iOS 13.5.1) Alamofire/5.1.0");
        httpget.setHeader("authorization",authorization);
        httpget.setHeader("referer","http://www.nike.com");
        httpget.setHeader("accept","application/json");
        httpget.setHeader("nike-api-caller-id","nike:com.nike.omega:ios:2.120");
        httpget.setHeader("accept-encoding","br;q=1.0, gzip;q=0.9, deflate;q=0.8");
        httpget.setHeader("accept-language","zh-Hans-CN;q=1.0, en-CN;q=0.9");
        httpget.setHeader("content-type","application/json; charset=utf-8");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)//设置连接超时时间,单位毫秒
                .setSocketTimeout(3000)//设置读取超时时间,单位毫秒
                .setConnectionRequestTimeout(3000)
                .build();
        httpget.setConfig(requestConfig);
        response = httpclient.execute(httpget);
        result= EntityUtils.toString(response.getEntity());
        response.close();
        System.out.println(result);
        JSONObject jsonObject=new JSONObject(result);
        String fistName=jsonObject.getJSONObject("name").getJSONObject("latin").getString("given");
        String lastName=jsonObject.getJSONObject("name").getJSONObject("latin").getString("family");
        String phone=RegexParse.baseParse(result,"verifiedphone\":[\\s]*?\"\\+86([\\d]*?)\"",1);
        System.out.println(phone);
        return new InnerAccount().setFirstName(fistName).setLastName(lastName).setPhone(phone);
    }
}
