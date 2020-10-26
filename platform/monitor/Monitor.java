package nike.platform.monitor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

/****
 *大监控
 * /**
 *  * 多个字段 key2 和key3 建立唯一索引，需要 这两个字段都nullable = false，才能创建成功
 *  * 也可以创建普通索引，例如user_name 或者password
 *  */
// *@Entity
// *@Table(name = "model_entity",
//        *uniqueConstraints = {
//        *@UniqueConstraint(columnNames = {"key2", "key3"})
//        *},
//        *indexes = {
//        *@Index(columnList = "user_name"),
//        *@Index(columnList = "password")
//        *})
// */
@Accessors(chain = true)
@Entity
@Table(name = "monitor",
        indexes = {
                @Index(columnList="style_color", unique = true),
                @Index(columnList="view_start_date"),
                @Index(columnList="full_title")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"id", "view_start_date"})
)
@Getter@Setter
public class Monitor {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    //标题
    @Column(name = "full_title")
    private String fullTitle;

    //货号
    @Getter
    @Setter
    @Column(name = "style_color")
    private String styleColor;

    //发售状态Active
    @Getter
    @Setter
    private String saleStatus;

    //适合男/女
    @Getter
    @Setter
    private String genders;

    //发售频道
    @Getter
    @Setter
    private String channels;

    //定点发售时间
    @Getter
    @Setter
    private String startEntryDate;

    //页面可见时间
    @Getter
    @Setter
    @Column(name = "view_start_date")
    private String viewStartDate;

    //程序最后扫描时间
    @Getter
    @Setter
    private String scanDate;
    // 当前价格
    @Getter
    @Setter
    private Double currentPrice;

    //原价
    @Getter
    @Setter
    private Double fullPrice;

    //图片
    @Getter
    @Setter
    @Column(length =1024)
    private String img;

    //产品用的 产品id
    @Getter @Setter
    private String productId;

    //限购数量d
    @Getter @Setter
    private Integer quantityLimit;

    //公布方式;FLOW Launch
    @Getter @Setter
    private String publishType;

    //是否折扣 能使用优惠券是1,不能使用优惠券是0
    @Getter @Setter
    private Integer promo;

    //产品的url-id
    @Getter @Setter
    private String detailUrlId;

    //LEO
    @Getter @Setter
    private String method;

    //是否是可能补货的 0是默认的，1是可能补货的
    @Getter @Setter
    private Integer replenishStatus=0;

}
