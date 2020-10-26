package nike.platform.persion;

import nike.platform.monitor.MonitorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;


@RestController
public class PersionControllerAPI {

    @Autowired
    MonitorDao monitorDao;

    @Autowired
    PersionDao persionDao;


    @Autowired
    ImportService importService;

    @RequestMapping("/persionUpload")
    public String persionUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("上传文件....>"+file.getName()+">"+file.getOriginalFilename());
        try{
            InputStream inputStream = file.getInputStream();
            List<List<Object>> list = importService.getBankListByExcel(inputStream, file.getOriginalFilename());
            inputStream.close();
            /****/
            HashMap<String,Persion> idEmail=new HashMap();
            persionDao.findAll().forEach(persion->{
                if(persion.getEmail()!=null)
                idEmail.put(persion.getEmail(),persion);
            });
            for (int i = 0; i < list.size(); i++) {
                List<Object> lo = list.get(i);
                lo.forEach(cell->{
                    System.out.print(cell+"\t");
                });
                Persion persion= new Persion()
                        .setId(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())==null?null:idEmail.get(lo.get(0)==null?null:lo.get(0).toString()).getId())
                        .setUsername(lo.get(0)==null?null:lo.get(0).toString())
                        .setEmail(lo.get(0)==null?null:lo.get(0).toString())
                        .setPassword(lo.get(1)==null?null:lo.get(1).toString())
                        //.setPromoExclusions(lo.get(3)==null?null:lo.get(3).toString())
                        .setCity(lo.get(6)==null?null:lo.get(6).toString())
                        .setCounty(lo.get(7)==null?null:lo.get(7).toString())
                        .setAddress1(lo.get(8)==null?null:lo.get(8).toString())
                        .setAddress2(lo.get(9)==null?null:lo.get(9).toString())
                        .setPostalCode(lo.get(10)==null?null:lo.get(10).toString().replaceFirst("\\.[\\d]*",""))
                        .setState(lo.get(12)==null?null:lo.get(12).toString())
                        .setType(lo.get(13)==null?null:lo.get(13).toString())
                        .setIgroup(lo.get(14)==null?null:lo.get(14).toString())
                        //-----
                        .setClient_id(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())==null?null:idEmail.get(lo.get(0)==null?null:lo.get(0).toString()).getClient_id())
                        .setRefresh_token(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())==null?null:idEmail.get(lo.get(0)==null?null:lo.get(0).toString()).getRefresh_token())
                        .setState(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())==null?null:idEmail.get(lo.get(0)==null?null:lo.get(0).toString()).getState());
                        if(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())!=null)
                            persion.setLoginStatus(idEmail.get(lo.get(0)==null?null:lo.get(0).toString())==null?null:idEmail.get(lo.get(0)==null?null:lo.get(0).toString()).getLoginStatus())
                        ;

                persionDao.save(persion);
            }
            System.out.println("================================");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(file);
        return "上传文件成功!";
    }


}
