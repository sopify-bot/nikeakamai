package nike.platform.promo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PromotionController {

    @Autowired
    PromotionRedisDao promotionRedisDao;

    @RequestMapping("promotion")
    public String promotion(Model model) {
        model.addAttribute("promotions",promotionRedisDao.getpromotions());
        return "promotion";
    }

    @RequestMapping("promotionInvalid")
    public String promotionInvalid(Model model) {
        model.addAttribute("promotions",promotionRedisDao.getinvalidpromotions());
        return "promotion";
    }



    @RequestMapping("delpromotion")
    public String delpromotion(Model model) {
        promotionRedisDao.clearPromotion();
        return "redirect:/promotion";
    }






}
