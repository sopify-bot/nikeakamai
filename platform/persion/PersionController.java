package nike.platform.persion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersionController {

    @Autowired
    PersionDao persionDao;


    @RequestMapping("persion")
    public String persion(Model model, @RequestParam(name = "page",defaultValue ="0" ) Integer page) {
        page=page==null?0:page;
        model.addAttribute("persions",persionDao.findAll(PageRequest.of(page, 100) ));
        return "persion";
    }

}
