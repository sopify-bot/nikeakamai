package nike.platform.order;

import nike.common.tool.Time;
import nike.platform.promo.PromotionRedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderResultControllerAPI {

    private Logger logger = LoggerFactory.getLogger(OrderResultControllerAPI.class);

    @Autowired
    OrderDao orderDao;

    @Autowired
    PromotionRedisDao promotionRedisDao;

    @RequestMapping("orderresult")
    public Integer orderresult(
                               @RequestParam(name = "orderNum" ) String orderNum,
                               @RequestParam(name = "email") String email,
                               @RequestParam(name = "styleColor") String styleColor,
                               @RequestParam(name = "mess" ) String mess,
                               @RequestParam(name = "size" ) String size,
                               @RequestParam(name = "promo" ) String promo
    ) {
        System.out.println("开始回收...."+promo);
        try {
            if(orderNum!=null&&orderNum.length()>3){
                orderDao.saveAndFlush(new Orders()
                        .setOrderNum(orderNum)
                        .setMess(mess)
                        .setOrderStatus(1)
                        .setPayStatus(0)
                        .setEmail(email)
                        .setStyleColor(styleColor)
                        .setSize(size)
                        .setOrderCreateTime(Time.createTime(0)));
                System.out.println("添加订单.");
                logger.info("将折扣码 设置为无效:"+promo);
                promotionRedisDao.addInvalidpromotion(promo);
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
