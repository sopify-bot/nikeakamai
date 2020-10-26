package nike.platform.task;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

/****
 *  人员管理;
 *  就是购买的账号;
 *  这里的主题设计思路就是凡是标记可用的，那么就一定要可用。
        ** /**
 *  *  * 多个字段 key2 和key3 建立唯一索引，需要 这两个字段都nullable = false，才能创建成功
 *  *  * 也可以创建普通索引，例如user_name 或者password
 *  *  */
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
@Getter@Setter
//三个的联合唯一索引;
@Table(
        uniqueConstraints= @UniqueConstraint(columnNames={"style_color"}),
        indexes = {
                @Index(columnList = "style_color"),
                @Index(columnList = "task_status")
        }
)
@Data
public class Task {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "style_color")
    private String styleColor;

    @Getter
    @Setter
    @Column(name = "size")
    private String size;

    /***
     * 购买数量
     * 比如42尺码买2双;
     */
    @Getter
    @Setter
    private Integer buyTotal;


    //产品的url-id
    @Getter @Setter
    private String detailUrlId;

    @Getter
    @Setter
    private Integer monitorId;

    @Getter
    @Setter
    private Double currentPrice;

    /***\
     * -1 异常
     * 0 是初始化
     * 1 是生成完任务....
     */
    @Getter
    @Setter
    @Column(name = "task_status")
    private Integer taskStatus=0;


    //产品用的 产品id
    @Getter @Setter
    private String productId;


    //产品用的 产品id
    @Getter @Setter
    private String mess;

}
