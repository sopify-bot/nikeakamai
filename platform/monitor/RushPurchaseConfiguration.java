package nike.platform.monitor;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/***
 * 这个类没有什么特别意义
 * 就是在监控页面添加任务的时候,作为对象传进来而已;
 */
public class RushPurchaseConfiguration {
    @Getter
    @Setter
    private String detailUrlId;
    @Getter @Setter
    private Integer monitorId;
    @Getter @Setter
    private List<String> chooseSize;

    @Override
    public String toString() {
        return "RushPurchaseConfiguration{" +
                "detailUrlId='" + detailUrlId + '\'' +
                ", monitorId=" + monitorId +
                ", chooseSize=" + chooseSize +
                '}';
    }
}
