package nike.laboratory;


import org.apache.poi.ss.formula.functions.T;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static nike.akamai.DeviceInfo.randomScreenSize;

public class AKAMAI {
    static  long uptime=0;
    static long startTime=System.currentTimeMillis();
    public  AKAMAI(){
        uptime=(nike.common.tool.Math.romdom(0,12))*36;
    }

    public long getUptime(){
        Long S=uptime+(System.currentTimeMillis()-startTime);
        return 0;
    }

    //模拟手机信息
   public DeviceInfo mDeviceInfo=new DeviceInfo("");

    SecretKey aesKey = null;
    SecretKey hmacKey = null;
    String aesKeyEncrypted = null;
    String hamcKeyEncryped = null;

    public String getClientInfo(){
       return mDeviceInfo.getClientInfo();
    }

    private String appUserAgent() {
        return "NikePlus/2.130.0 (com.nike.omega; build:2009171834; iOS 14.0.0) Alamofire/5.1.0";
    }





    public String getSensorData(String email) {
//        String model = mDeviceInfo.model;
//        String androidVersion = mDeviceInfo.androidVersion;
//        String brand = mDeviceInfo.brand;
//        Random random = new Random();
        StringBuilder stringBuilder6 = new StringBuilder();
        stringBuilder6.append("2.2.1-1,2,-94,-100,")
                .append("-1,uaend,-1,")
                .append(randomScreenSize())
                .append(",1,100,1,zh,")
                .append(DeviceInfo.randomANDROIDVERSION()).append(",0,")
                .append(DeviceInfo.randomANDROIDBRAND())
                .append(",unknown,qcom,-1,com.nike.snkrs,-1,-1,")
                .append(UUID.randomUUID().toString())
                .append(",-1,0,1,REL,")
                .append(208).append(",23,")
                .append(DeviceInfo.randomANDROIDBRAND()).append(",").append(DeviceInfo.randomANDROIDBRAND()).append(",release-keys,user,sysop,")
                .append(DeviceInfo.randomANDROIDMODEL()).append(",").append(DeviceInfo.randomANDROIDBRAND()).append(",").append(DeviceInfo.randomANDROIDBRAND()).append(",").append(DeviceInfo.randomANDROIDBRAND()).append(",").append(DeviceInfo.randomANDROIDBRAND()).append("/").append(DeviceInfo.randomANDROIDBRAND()).append("/").append(DeviceInfo.randomANDROIDBRAND())
                .append(":").append(DeviceInfo.randomANDROIDVERSION()).append("/").append(DeviceInfo.randomANDROIDMODEL()).append("/").append(208)
                .append(":user/release-keys,")
                .append("builder02,").append(DeviceInfo.randomANDROIDMODEL());
        int length = chrplus(stringBuilder6.toString());
        stringBuilder6.append(",").append(length).append(",").append(Math.max(1, new Random().nextInt(9999))).append(",").append(System.currentTimeMillis() / 2)
                .append("-1,2,-94,-101,")
                .append("do_unr,dm_en,t_en")
                .append("-1,2,-94,-102,")
                .append("-1,2,-94,-108,")
                .append("-1,2,-94,-117,")
                .append("-1,2,-94,-111,")
                .append("488,251.16,36.5,-1.78,1;237,258.35,44.35,-13.11,1;197,266.15,47.71,-23.78,1;198,278.47,49.24,-40.3,1;196,290.74,48.53,-55.62,1;200,303.92,43.47,-73.12,1;196,319.91,45.4,-85.75,1;197,320.26,43.32,-87.43,1;234,316.89,44.01,-84.71,1;160,324.08,46.81,-88.62,1;")
                .append("-1,2,-94,-109,")
                .append("330,-0.09,-2.19,-2.98,-0.23,-5.95,-8.1,16.29,-4.44,1.62,1;157,-0.17,-1.17,-1.08,-0.54,-6.44,-7.61,52.57,-37,27.78,1;237,-0.64,-0.65,-0.08,-1.74,-6.67,-6.7,111.33,-104.75,38.67,1;197,-0.83,-0.74,1.39,-2.94,-7.65,-3.57,23.57,-13.33,63.47,1;198,-1.2,-0.56,0.01,-4.66,-8.1,-4.94,16.88,-110.1,17.13,1;197,-0.6,-0.01,0.72,-4.71,-7.57,-3.45,18.99,-90.13,41.01,1;200,-1.38,0.94,0.99,-7.25,-6.14,-1.01,9.08,-110.71,70.58,1;196,-1.03,0.96,1.31,-7.95,-5.15,0.64,19.63,-87.92,-29.64,1;196,0.7,-0.13,1.23,-5.52,-6.38,1.8,-21.57,-14.16,22.13,1;235,-0.3,0.16,0.53,-6.81,-5.93,1.62,-52.49,-9.56,-12.3,1;")
                .append("-1,2,-94,-144,")
                .append("2;160.00;488.00;2291790394;}O3GH2GNAGHGH3GHGH4G2HGH2GH2GH2GHGH8GHGH7G2H3GHG")
                .append("-1,2,-94,-142,")
                .append("2;251.16;350.86;3976058770;AEJQX`2jhlo3q2rt2vw3xy2z11{14|}4|z_HECGTm:2;26.95;63.23;2779607165;P]ced._.]ade3f2g2h2i4k11j19k2jeLAMo}{:2;-105.99;-1.78;1915754872;}vpf]SLKMJI4HGF2E3D2C6B26ABVotyugO")
                .append("-1,2,-94,-145,")
                .append("2;157.00;330.00;3702715838;}A.NONO2N.BNONO2N2ON2O3N2ONONMP2NO2NON2ON2ONO2NOMPNO2NO2N2ONONO")
                .append("-1,2,-94,-143,")
                .append("2;-1.38;0.82;4185390251;daUOEVAJy^nmkigf2gfghehkfghefge7f4g15fgfw}tyo:2;-2.19;1.08;1351463476;AS][^i2zflcf2g7hi4h13ihih16inx}wZK:2;-4.96;2.41;87824826;Q`htioqtsmiklj7i2hihikj30ioJAUj}:2;-7.95;-0.23;756849891;}zqg2ZFASIL2N4O3PQPRTS2T7S3R4STST14SU^fgsr:2;-8.11;-3.97;2797807175;`YUGAH]kZ`2ONM4L2KJK2H2GHIH10I11H7IJRev}aA:2;-10.25;3.34;48988008;JLP^X_iqvuprt3sr4s2rsrsu13t18u}YABJ]:2;-210.32;192.99;3109801715;bhpcbcac]X2a2`_7`_2`2a30`aUAWl}o:2;-110.71;232.15;2476552013;SMBRADADQRO2SU5TUVR3SUTS11TS17T]}{beUF:2;-29.64;70.58;2886463249;Scix.k}A_KAJ2PRQP2Q3PKT2RS2RT4S2R3Q6RS2RSRS2R4SV_FXgfi")
                .append("-1,2,-94,-115,")
                .append("0,0,8671479250,17701151970,26372631220,15386,0,0,64,64,18000,116000,1,699084045103471958,1568251393817,0")
                .append("-1,2,-94,-106,")
                .append("-1,0")
                .append("-1,2,-94,-120,")
                .append("-1,2,-94,-112,")
                //o.n
                .append("19,2175,59,1068,288800,2896,22100,220,7846")
                .append("-1,2,-94,-103,");
        //System.out.println("oriSensorData:" + stringBuilder6);



        StringBuilder sensorData=new StringBuilder();
         sensorData.append(random(version))//系统的版本号;
                    .append("-1,2,-94,-100,")//设备信息
                    .append("-1,uaend,-1")//设备信息
                    .append(randomScreenSize())
                    .append(",1,100,1,zh,")
                    .append(random(Arrays.asList(",1,"+nike.common.tool.Math.romdom(1,95)+",1,zh,",",0,100,1,zh,")))
                    .append(DeviceInfo.randomANDROIDVERSION()).append(",0,")
                    .append(DeviceInfo.randomANDROIDBRAND())
                    .append(",unknown,"+random(Arrays.asList("gmini","shamu","aosp","ota","muid","U8860"))+",-1,com.nike.snkrs,-1,-1,")
                    .append(UUID.randomUUID().toString())
                    .append(",-1,0,1,REL,")
                    .append(nike.common.tool.Math.romdom(4072753, 5072753))//基带版本
                    .append(","+nike.common.tool.Math.romdom(23,27)+",")//sdkINT
                    .append(DeviceInfo.randomANDROIDBRAND()).append(",")
                    .append(random(Arrays.asList("gmini","shamu","aosp","ota","muid","U8860")))
                    .append(",release-keys,user,"+generateString(6)+",")
                    .append(DeviceInfo.randomANDROIDMODEL()).append(",")
                    .append(DeviceInfo.randomANDROIDBRAND()).append(",")
                    .append(DeviceInfo.randomANDROIDBRAND()).append(",")
                    .append(random(Arrays.asList("gmini", "shamu", "aosp", "user", "U8860"))).append(",")
                    .append("ubuntu").append(",")//host
                    .append(generateString(6)).append(",")//host
                     .append("/").append(DeviceInfo.randomANDROIDBRAND()).append("/").append(DeviceInfo.randomANDROIDBRAND())
                     .append(":").append(DeviceInfo.randomANDROIDVERSION()).append("/").append(DeviceInfo.randomANDROIDMODEL()).append("/").append(208)
                     .append(":user/release-keys,")
                     .append("builder02,").append(DeviceInfo.randomANDROIDMODEL());
                    int length1 = chrplus(sensorData.toString());
                    sensorData.append(",").append(length1).append(",").append(Math.max(1, new Random().nextInt(9999))).append(",").append(System.currentTimeMillis() / 2)
                    .append("-1,2,-94,-101,")//TAG
                    .append("do_en,do_en,t_en")//传感器是否开关
                    .append("-1,2,-94,-102,")
                    .append("-1,2,-94,-108,")
                    .append("-1,2,-94,-117,")
                    .append("-1,2,-94,-111,")
                     .append("488,251.16,36.5,-1.78,1;237,258.35,44.35,-13.11,1;197,266.15,47.71,-23.78,1;198,278.47,49.24,-40.3,1;196,290.74,48.53,-55.62,1;200,303.92,43.47,-73.12,1;196,319.91,45.4,-85.75,1;197,320.26,43.32,-87.43,1;234,316.89,44.01,-84.71,1;160,324.08,46.81,-88.62,1;")
                     .append("-1,2,-94,-109,")
                     .append("330,-0.09,-2.19,-2.98,-0.23,-5.95,-8.1,16.29,-4.44,1.62,1;157,-0.17,-1.17,-1.08,-0.54,-6.44,-7.61,52.57,-37,27.78,1;237,-0.64,-0.65,-0.08,-1.74,-6.67,-6.7,111.33,-104.75,38.67,1;197,-0.83,-0.74,1.39,-2.94,-7.65,-3.57,23.57,-13.33,63.47,1;198,-1.2,-0.56,0.01,-4.66,-8.1,-4.94,16.88,-110.1,17.13,1;197,-0.6,-0.01,0.72,-4.71,-7.57,-3.45,18.99,-90.13,41.01,1;200,-1.38,0.94,0.99,-7.25,-6.14,-1.01,9.08,-110.71,70.58,1;196,-1.03,0.96,1.31,-7.95,-5.15,0.64,19.63,-87.92,-29.64,1;196,0.7,-0.13,1.23,-5.52,-6.38,1.8,-21.57,-14.16,22.13,1;235,-0.3,0.16,0.53,-6.81,-5.93,1.62,-52.49,-9.56,-12.3,1;")
                     .append("-1,2,-94,-144,")
                     .append("2;160.00;488.00;2291790394;}O3GH2GNAGHGH3GHGH4G2HGH2GH2GH2GHGH8GHGH7G2H3GHG")
                     .append("-1,2,-94,-142,")
                     .append("2;251.16;350.86;3976058770;AEJQX`2jhlo3q2rt2vw3xy2z11{14|}4|z_HECGTm:2;26.95;63.23;2779607165;P]ced._.]ade3f2g2h2i4k11j19k2jeLAMo}{:2;-105.99;-1.78;1915754872;}vpf]SLKMJI4HGF2E3D2C6B26ABVotyugO")
                     .append("-1,2,-94,-145,")
                     .append("2;157.00;330.00;3702715838;}A.NONO2N.BNONO2N2ON2O3N2ONONMP2NO2NON2ON2ONO2NOMPNO2NO2N2ONONO")
                     .append("-1,2,-94,-143,")
                     .append("2;-1.38;0.82;4185390251;daUOEVAJy^nmkigf2gfghehkfghefge7f4g15fgfw}tyo:2;-2.19;1.08;1351463476;AS][^i2zflcf2g7hi4h13ihih16inx}wZK:2;-4.96;2.41;87824826;Q`htioqtsmiklj7i2hihikj30ioJAUj}:2;-7.95;-0.23;756849891;}zqg2ZFASIL2N4O3PQPRTS2T7S3R4STST14SU^fgsr:2;-8.11;-3.97;2797807175;`YUGAH]kZ`2ONM4L2KJK2H2GHIH10I11H7IJRev}aA:2;-10.25;3.34;48988008;JLP^X_iqvuprt3sr4s2rsrsu13t18u}YABJ]:2;-210.32;192.99;3109801715;bhpcbcac]X2a2`_7`_2`2a30`aUAWl}o:2;-110.71;232.15;2476552013;SMBRADADQRO2SU5TUVR3SUTS11TS17T]}{beUF:2;-29.64;70.58;2886463249;Scix.k}A_KAJ2PRQP2Q3PKT2RS2RT4S2R3Q6RS2RSRS2R4SV_FXgfi")
                     .append("-1,2,-94,-115,")
                     .append("0,0,8671479250,17701151970,26372631220,15386,0,0,64,64,18000,116000,1,699084045103471958,1568251393817,0")
                     .append("-1,2,-94,-106,")
                     .append("-1,0")
                     .append("-1,2,-94,-120,")
                     .append("-1,2,-94,-112,")
                     .append("19,2175,59,1068,288800,2896,22100,220,7846")
                     .append("-1,2,-94,-103,");
                    //System.out.println("oriSensorData:" + sensorData);
                    String sensor = encryptSensor(stringBuilder6.toString());
        return sensor;
    }

    List<String> version= Arrays.asList(
            "2.0",
            "2.0.1",
            "2.1",
            "2.2",
            "2.2.1",
            "2.3",
            "3.0",
            "3.1",
            "3.2",
            "4.0",
            "4.0.3",
            "4.1.2",
            "4.2",
            "4.3"
    );

    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }





    public  static String random(List<String> list){
        return list.get(nike.common.tool.Math.romdom(0,(list.size()-1)));
    }


    private String  getSensor(){
        StringBuilder stringBuilder=new StringBuilder();
        return stringBuilder.toString();
    }



    public static int chrplus(String paramString) {
        if (paramString != null && !paramString.trim().equalsIgnoreCase("")) {
            int b = 0;
            int c = 0;
            try {
                while (b < paramString.length()) {
                    char c2 = paramString.charAt(b);
                    if (c2 < 128)
                        c = c + c2;
                    b++;
                }
                return c;
            } catch (Exception e) {
                return -2;
            }
        }
        return -1;
    }

    public String encryptSensor(String str) {
        String result = null;
        try {
            initEncryptKey();
            long uptimeMillis = getUptime();
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(1, aesKey);
            byte[] doFinal = instance.doFinal(str.getBytes());
            long aesUptime = (getUptime() - uptimeMillis) * 1000;
            byte[] iv = instance.getIV();
            byte[] obj = new byte[(doFinal.length + iv.length)];
            System.arraycopy(iv, 0, obj, 0, iv.length);
            System.arraycopy(doFinal, 0, obj, iv.length, doFinal.length);
            uptimeMillis = getUptime();
            Key secretKeySpec = new SecretKeySpec(hmacKey.getEncoded(), "HmacSHA256");
            Mac instance2 = Mac.getInstance("HmacSHA256");
            instance2.init(secretKeySpec);
            iv = instance2.doFinal(obj);
            doFinal = new byte[(obj.length + iv.length)];
            long hmackUptime = (getUptime() - uptimeMillis) * 1000;
            System.arraycopy(obj, 0, doFinal, 0, obj.length);
            System.arraycopy(iv, 0, doFinal, obj.length, iv.length);
            uptimeMillis = getUptime();
            String encryptedData = Base64.encodeToString(doFinal, 2);
            long b64uptime = 1000 * (getUptime() - uptimeMillis);

            StringBuilder sb = new StringBuilder();
            sb.append("1,a,");
            sb.append(aesKeyEncrypted);
            sb.append(",");
            sb.append(hamcKeyEncryped);
            sb.append("$");
            sb.append(encryptedData);
            sb.append("$");
            sb.append(aesUptime).append(",").append(hmackUptime).append(",").append(b64uptime);
            result = sb.toString();
        } catch (Exception e) {
        }
        return result;
    }


    private void initEncryptKey() {
        if (aesKey != null) {
            return;
        }
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            aesKey = keyGen.generateKey();

            KeyGenerator hmacKeyGen = KeyGenerator.getInstance("HmacSHA256");
            hmacKey = hmacKeyGen.generateKey();

            X509EncodedKeySpec keySpec =new X509EncodedKeySpec(Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4sA7vA7N/t1SRBS8tugM2X4bByl0jaCZLqxPOql+qZ3sP4UFayqJTvXjd7eTjMwg1T70PnmPWyh1hfQr4s12oSVphTKAjPiWmEBvcpnPPMjr5fGgv0w6+KM9DLTxcktThPZAGoVcoyM/cTO/YsAMIxlmTzpXBaxddHRwi8S2NvwIDAQAB",0));

            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey rsaKey = factory.generatePublic(keySpec);

            Cipher rsaInstance = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            rsaInstance.init(1, rsaKey);
            aesKeyEncrypted = Base64.encodeToString(rsaInstance.doFinal(aesKey.getEncoded()), 2);
            hamcKeyEncryped = Base64.encodeToString(rsaInstance.doFinal(hmacKey.getEncoded()), 2);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new AKAMAI().getSensorData(""));;
    }


}
