package nike.common.tool;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * @author jhy
 * @version 1.0
 *
 */
public class RegexParse {
    public static String baseParse(String content ,String regex,int group) {
        if(content==null)
            throw new  NullPointerException();
        if(regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        if(m.find())
            return	m.group(group);
        return null;
    }

    public static String baseParse(String content ,String regex) {
        if(content==null||regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        if(m.find())
            return	m.group();
        return null;
    }

    /***
     *  用于带组号  多返回匹配
     *  i代表层号，sum代表id号。也就是第几个的意思。用于标识此值是属于那层的第几个
     *  group=0 代表都要 ，跟没写一样。
     *  sequence =0 就是要第一个;
     */
    public static String baseParse(String content ,String regex,int group,int sequence) {
        if(content==null||regex==null)
            throw new NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        while(m.find())
        {
            String str=m.group(group);
            if(str!=null&&str.length()>0)
                strList.add(str);
        }
        return strList.get(sequence);
    }

    public static boolean ismatching(String content ,String regex) {
        if(content==null||regex==null)
            throw new  NullPointerException("regex:"+regex+"content:"+content);
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        if(m.find())
            return	true;
        if(content.contains(regex)||regex.contains(content)||content.equals(regex))
            return true;
        return false;
    }

    public static boolean isMatchingMore(String content ,String regex) {
        if(content==null||regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        int sum=0;
        while(m.find())
            sum++;
        if(sum>1)
            return true;
        return false;
    }




    public static List<String> baseParseList(String content ,String regex,int group,int i) {
        if(content==null||regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        int sum=0;
        while(m.find())
            strList.add(m.group(group)+"["+i+","+((++sum))+"]");
        return strList;
    }
    public static List<String> baseParseList(String content ,String regex,int group) {
        if(content==null||regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        while(m.find())
            strList.add(m.group(group));
        return strList;
    }

    public static void main(String[] args) {
        //System.out.println(RegexParse.isMatchingMore("123123123123", "123123123123"));
    }

    public static List<String> baseParseList(String content ,String regex) {
        if(content==null||regex==null)
            throw new  NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        while(m.find())
            strList.add(m.group());
        return strList;
    }


    /***
     *  用于带组号  多返回匹配
     *  i代表层号，sum代表id号。也就是第几个的意思。用于标识此值是属于那层的第几个
     *  group=0 代表都要 ，跟没写一样。
     *  sequence =-1 ，代表 都要；=1 就是第一个
     */
    public static List<String> getParseList(String content ,String regex,int group,int sequence) {
        if(content==null||regex==null)
            throw new NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        while(m.find())
        {
            String str=m.group(group);
            if(str!=null&&str.length()>0)
                strList.add(str);
        }
        if(sequence!=-1&&strList.size()>=sequence)
        {
            List<String> temp=new LinkedList<String>();
            temp.add(strList.get(sequence-1));
            strList=temp;
        }
        return strList;
    }



    /***
     *  用于带组号  多返回匹配
     *  i代表层号，sum代表id号。也就是第几个的意思。用于标识此值是属于那层的第几个
     *  group=0 代表都要 ，跟没写一样。
     *  sequence =-1 ，代表 都要；=1 就是第一个
     */
    public static List<String> getParseList(String content ,String regex,int group) {
        if(content==null||regex==null)
            throw new NullPointerException();
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        List<String> strList=new LinkedList<String>();
        while(m.find())
        {
            String str=m.group(group);
            if(str!=null&&str.length()>0)
                strList.add(str);
        }
        return strList;
    }

    public static List<String> fatherAndSon(String contentF ,String regexF,int groupF,String regexS,int groupS) {
        List<String> result=new ArrayList<>();
        List<String>  first=getParseList(contentF, regexF, groupF);
        if(first==null||first.size()==0)
        {
            throw new  NullPointerException("父节点未匹配出东西");
        }
        for(String strSon: first)
        {
            List<String>  second=getParseList(strSon, regexS, groupS);
            result.addAll(second);
        }
        return result;
    }
    public static List<String> fatherAndSon(String contentF ,String regexF,int groupF,int sequenceF ,String regexS,int groupS,int sequenceS) {
        List<String> result=new ArrayList<>();
        List<String>  first=getParseList(contentF, regexF, groupF, sequenceF);
        if(first==null||first.size()==0)
        {
            throw new  NullPointerException("父节点未匹配出东西");
        }
        for(String strSon: first)
        {
            List<String>  second=getParseList(strSon, regexS, groupS, sequenceS);
            result.addAll(second);
        }
        return result;
    }
    public static List<String> fatherAndSon(String contentF ,String regexF,int groupF,int sequenceF ,int groupS,int sequenceS,String... regexS) {

        List<String> result=new ArrayList<>();
        List<String>  first=getParseList(contentF, regexF, groupF, sequenceF);
        if(first==null||first.size()==0)
        {
            throw new  NullPointerException("父节点未匹配出东西");
        }
        for(String strSon: first)
        {
            for(String regex:regexS){
                List<String>  second=getParseList(strSon, regex, groupS, sequenceS);
                result.addAll(second);
            }
        }
        return result;
    }

}
