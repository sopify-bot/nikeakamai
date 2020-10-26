package nike.platform.monitor;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MonitorControllerAPI {


    @Autowired
    MonitorDao monitorDao;


    @RequestMapping("quantityLimit/{monitorId}")
    public String quantityLimit(@PathVariable(name = "monitorId") Integer monitorId) {
        Integer limit=monitorDao.findOneById(monitorId).getQuantityLimit();
        JSONArray jsonArray=new JSONArray();
        for(int i=1;i<(limit+1);i++)
        jsonArray.put(i);
        System.out.println("查询到的限购是"+limit);
        return jsonArray.toString();
    }

}
