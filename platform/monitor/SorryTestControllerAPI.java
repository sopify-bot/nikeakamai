package nike.platform.monitor;

import nike.proxy.BotProxyRedisDao;
import nike.proxy.IProxy;
import nike.proxy.MyIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SorryTestControllerAPI {

    @Autowired
    BotProxyRedisDao botProxyRedisDao;

    /***
     *1 测试 监控的IP
     */
    @RequestMapping("test")
    public String quantityLimit() {
        botProxyRedisDao.getIPs(0,3);
        return "";
    }

}
