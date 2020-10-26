package nike.platform.order;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    @Autowired
    OrderDao orderDao;

    @RequestMapping("order")
    public String order(Model model, @RequestParam(name = "page",defaultValue ="0" ) Integer page, @RequestParam(name = "style_color",defaultValue ="" ) String style_color) {
        page=page==null?0:page;
        if(style_color!=null&&!"".equals(style_color))
            model.addAttribute("orders",orderDao.findAllByStyleColor(PageRequest.of(page, 100),style_color));
        else
            model.addAttribute("orders",orderDao.findAll(PageRequest.of(page, 100) ));
        model.addAttribute("style_color",style_color);
        return "order";
    }


    /***
     */
    @RequestMapping("orderdelete")
    public String orderdelete(Model model, @RequestParam(name = "style_color",defaultValue ="" ) String style_color) {
        if(style_color!=null)
        orderDao.deleteByStyleColor(style_color);
        return  "redirect:/order";
    }



}
