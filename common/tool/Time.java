package nike.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Time {
    private static final Logger logger = LoggerFactory.getLogger(Time.class);
    public static String UTCToCST(String UTCStr)  {
        try {
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            date = sdf.parse(UTCStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
            return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendar.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "-";
    }

    /***
     * after如果写0，就是当前时间，如果是2就是2天后的时间
     * @param after
     * @return
     */
    public static String createTime(int after){
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        calendar2.add(Calendar.DATE, after);
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendar2.getTime());
    }
    /***
     * after如果写0，就是当前时间，如果是2就是2天后的时间

     * @return
     */
    public static String createTimeSecond(int second){
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        calendar2.add(Calendar.SECOND, second);
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendar2.getTime());
    }


    /***
     * after如果写0，就是当前时间，如果是2就是2天后的时间

     * @return
     */
    public static String createTimeMinute(int Minute){
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        calendar2.add(Calendar.MINUTE, Minute);
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendar2.getTime());
    }


    /***
     * 传入给定时间,判断当前时间是不是已经超过了给定时间
     * @param endTime
     * @return
     */
    public  static  boolean afterTime(String endTime){
        if(endTime==null)
        {
            logger.error("启动时间是null>....没办法参考;所以认为直接到时启动..");
            return true;
        }
        if((createTime(0)+"").equals(endTime))
            return true;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sd1= null;
        Date sd2=null;
        try {
            sd1 = df.parse(createTime(0)+"");
            sd2=df.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  sd1.after(sd2);
    }


    public  static String trunTime(String inTime){
        SimpleDateFormat sdf1= new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        try
        {
            Date date=sdf1.parse(inTime);
            SimpleDateFormat sdf=new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss z", Locale.US);
            String sDate=sdf.format(date);
            return  sDate;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) throws ParseException {
        String x = "Mon Mar 02 13:57:49 CST 2015";

    }

    /***
     * @return
     */
    public static Map<String,String> createTime(int start_entry_date,int end_entry_date){
        Map<String,String> map=new HashMap<>();
        Calendar calendarStart = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatStart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        calendarStart.add(Calendar.SECOND,start_entry_date);
        map.put("start_entry_date",new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendarStart.getTime()));

        Calendar calendarEnd = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatEnd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        calendarEnd.add(Calendar.SECOND,end_entry_date);
        map.put("end_entry_date",new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(calendarEnd.getTime()));
        return map;
    }







}
