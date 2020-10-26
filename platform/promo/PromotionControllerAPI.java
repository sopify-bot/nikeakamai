package nike.platform.promo;

import nike.platform.persion.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
public class PromotionControllerAPI {

    @Autowired
    ImportService importService;

    @Autowired
    PromotionRedisDao promotionRedisDao;

    @RequestMapping("/promotionUpload")
    public String promotionUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("上传文件....>"+file.getName()+">"+file.getOriginalFilename());
        try{
            InputStream inputStream = file.getInputStream();
            List<String> list = importService.readFileLine(inputStream);
            if(list!=null&&list.size()>0){
                list.forEach(item->{
                   try{
                       promotionRedisDao.addPromotion(item);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                });
            }
            System.out.println("================================");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(file);
        return "上传文件成功!";
    }

}
