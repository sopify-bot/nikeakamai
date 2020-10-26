package nike.platform.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

/****
 *  人员管理;
 *  就是购买的账号;
 *  这里的主题设计思路就是凡是标记可用的，那么就一定要可用。
 *
 *
 * */
@Accessors(chain = true)
@Entity
@Getter@Setter
public class Orders {


    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /***
     * 货号；
     */
    @Getter @Setter
    private String styleColor;


    /**
     * 邮箱,这个是准备付款的时候用的。
     */
    @Getter @Setter
    private String email;


    /**
     * 订单创建时间
     */
    @Getter @Setter
    private String orderCreateTime;
    /**
     * order号；
     */
    @Getter @Setter
    private String orderNum;

    /**
     * 订单状态；
     */
    @Getter @Setter
    private Integer orderStatus=0;

    /**
     * 支付状态；
     */
    @Getter @Setter
    private Integer payStatus;

    /**
     * 支付连接；
     */
    @Getter @Setter
    private String payLink;

    /***
     * 机器用的；
     */
    @Column(columnDefinition = "text")
    @Getter @Setter
    private String mess;

    /**
     * 支付连接；
     */
    @Getter @Setter
    private String size;



}
