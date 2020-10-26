package nike.platform.promo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class PromotionRedisDao {



    @Autowired
    RedisTemplate<String,String> redisTemplate;


    /***
     * 如果是无效中已经有了。那么不在使用...
     * @param promotionCode
     */
    public void addPromotion(String  promotionCode){
        if(promotionCode.equals("promotion"))
            return;
        String key="promotion";
        String invalidKey="invalidpromotion";
        if(!redisTemplate.boundSetOps(invalidKey).isMember(promotionCode))
        redisTemplate.boundSetOps(key).add(key,promotionCode);
    }


    /**
     * 只能清空 有效的，不能清空无效的；
     */
    public void clearPromotion(){
        String key="promotion";
        String invalidKey="invalidpromotion";
        redisTemplate.delete(key);
    }

    public long promotionSize(){
        String key="promotion";
        return  redisTemplate.boundSetOps(key).size();
    }


    /***
     * 返回有效的和无效的差集；也就是只显示有效的
     * @return
     */
    public Set<String> getpromotions(){
        String key="promotion";
        String invalidKey="invalidpromotion";
        return  redisTemplate.boundSetOps(key).diff(invalidKey);
    }

    /***
     * 返回无效
     * @return
     */
    public Set<String> getinvalidpromotions(){
        String key="invalidpromotion";
        return  redisTemplate.boundSetOps(key).members();
    }





    /***
     * 如果是无效中已经有了。那么不在使用...
     * @param promotionCode
     */
    public void addInvalidpromotion(String  promotionCode){
        String invalidKey="invalidpromotion";
        redisTemplate.boundSetOps(invalidKey).add(promotionCode);
    }

    public List<String> getPromotions(int sum){
        List<String> promos=new ArrayList<>();
        String key="promotion";
        String invalidKey="invalidpromotion";
        long size=redisTemplate.boundSetOps(key).diff(invalidKey).size();
        if(size>0){
                int index=0;
                for(String promo:redisTemplate.boundSetOps(key).diff(invalidKey)){
                    if(!redisTemplate.hasKey("lockpromo"+promo)){
                        if(promo.equals("promotion"))
                            continue;
                        promos.add(promo);
                        redisTemplate.boundValueOps("lockpromo"+promo).set(promo,60, TimeUnit.SECONDS);
                        index++;
                    }
                    if(index>=sum)
                        break;
                }
        }
        return promos;
    }

    public String getPromotion(){
        String key="promotion";
        String invalidKey="invalidpromotion";
        long size=redisTemplate.boundSetOps(key).diff(invalidKey).size();
        if(size>0){
            for(String promo:redisTemplate.boundSetOps(key).diff(invalidKey)){
                if(!redisTemplate.hasKey("lockpromo"+promo)){
                    if(promo.equals("promotion"))
                        continue;
                    redisTemplate.boundValueOps("lockpromo"+promo).set(promo,60, TimeUnit.SECONDS);
                    return  promo;
                }
            }
        }
        return null;
    }




}
