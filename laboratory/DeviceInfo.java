package nike.laboratory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

import static nike.akamai.Utils.md5;

public class DeviceInfo {

    final static String[] screenSizes = new String[]{"854,480",
            "800,480",
            "1280,800",
            "2560,1600",
            "1920,1080",
            "2560,1440",
            "1280,768",
            "1920,1200",
            "1920,1032"};
    final static Map<String, String> ANDROID_VERSION = new HashMap<String, String>() {{
        put("6.0", "23");
        put("7.0", "24");
        put("7.1", "25");
        put("8.0.0", "26");
        put("8.1.0", "27");
        put("5.1", "22");
    }};
    final static String[] ANDROID_BRAND = new String[]{"samsung",
            "Huawei",
            "TCL",
            "MEIZU",
            "Sony",
            "Lenovo",
            "OPPO",
            "vivo",
            "A7",
            "Acer",
            "Asus",
            "LG",
            "Sony",
            "HTC",
            "ZTE",
            "HTC"};

    private static String[] ANDROID_MODEL = new String[]{
            "X820",
            "P20",
            "S10",
            "p30",
            "S8",
            "NOTE7",
            "Pixel",
            "Pixel 2",
            "Pixel 2XL",
            "Pixel 3",
    };

    /************************/

    public static String[] product_names =new String[]{"gmini", "shamu", "aosp", "ota","muid", "U8860"};
    public static String[] devices_names =new String[]{"gmini", "shamu", "aosp", "user", "U8860"};

    public static String clientType = "com.nike.commerce.snkrs.droid";
    public static String clientId ="qG9fJbnMcBPAMGibPRGI72Zr89l8CD4R";
    public static List<Integer> aesKey=Arrays.asList(16,
            -59,
            20,
            -5,
            -54,
            -85,
            110,
            61,
            -51,
            -99,
            70,
            -78,
            11,
            -44,
            3,
            5,
            -120,
            58,
            -14,
            74,
            13,
            -122,
            35,
            120,
            14,
            -60,
            67,
            73,
            -58,
            -90,
            42,
            112);
    /************************/

    public String brand;
    public String model;
    public String androidVersion;
    public String SDKINT;
    private String username;

    public DeviceInfo(String username) {
        this.username=UUID.randomUUID().toString();
        //this.username = username;
        this.brand = randomANDROIDBRAND();
        this.model = randomANDROIDMODEL();
        this.androidVersion = randomANDROIDVERSION();
        this.SDKINT = ANDROID_VERSION.get(this.androidVersion);
    }

    private static String E(Map<String, String> paramMap) {
        StringBuilder stringBuilder = new StringBuilder();
        if (paramMap == null) {
            stringBuilder.append("0");
        } else {
            Formatter formatter = new Formatter(stringBuilder);
            formatter.format("%04x", new Object[]{Integer.valueOf(paramMap.size())});
            for (Map.Entry entry : paramMap.entrySet()) {
                a(formatter, (String) entry.getKey());
                a(formatter, (String) entry.getValue());
            }
        }
        return stringBuilder.toString();
    }

    private String getBluetoothAddress() {
        String blue = md5("blue" + username).substring(0, 12);
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (char c : blue.toCharArray()) {
            if (temp.length() < 2) {
                temp.append(c);
            } else {
                sb.append(temp);
                temp.delete(0, temp.length());
            }
        }
        return sb.toString();
    }

    public String getClientInfo() {
        StringBuilder stringBuilder = new StringBuilder("0500");
        HashMap hashMap = new HashMap();
        b(hashMap, "BBSC", "Android");
        b(hashMap, "CLIENT_TIME", c());
        b(hashMap, "AFPID", getFinger());
        b(hashMap, "MACA", getMac(username));
        b(hashMap, "BMACA", getBluetoothAddress());
        b(hashMap, "ASL", SDKINT);
        b(hashMap, "ABN", model);
//            a(hashMap);
        b(hashMap, "ANID", getImei());
        b(hashMap, "AMID", getAndroidId());
        b(hashMap, "ADSV", androidVersion);
        b(hashMap, "ADM", model);
        b(hashMap, "ADLO", getISO3Country());
        stringBuilder.append(a(E(hashMap)));
        return stringBuilder.toString();
    }

    private static String a(String paramString) {
        if (paramString != null)
            try {
                return ac(paramString.getBytes("utf-8"));
            } catch (IOException e) {
            }
        return "";
    }

    private static String a(byte[] paramArrayOfByte) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(paramArrayOfByte);
            paramArrayOfByte = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b = 0; b < paramArrayOfByte.length; b++)
                stringBuffer.append(Integer.toHexString(paramArrayOfByte[b] & 0xFF));
            return stringBuffer.toString();
        } catch (Throwable a) {
            return null;
        }
    }

    private static void a(Formatter paramFormatter, String paramString) {
        paramFormatter.format("%04x", new Object[]{Integer.valueOf(paramString.length())});
        paramFormatter.format("%s", new Object[]{paramString});
    }

    private static String ac(byte[] paramArrayOfByte) {
        try {
            int i;
            byte[] arrayOfByte2 = new byte[16];
            (new Random()).nextBytes(arrayOfByte2);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write("0500".getBytes());
            byteArrayOutputStream.write(paramArrayOfByte);
            if (byteArrayOutputStream.size() % 16 == 0) {
                i = 0;
            } else {
                i = 16 - byteArrayOutputStream.size() % 16;
            }
            byteArrayOutputStream.write(new byte[]{
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0}, 0, i);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(arrayOfByte2);
            cipher.init(1, new SecretKeySpec(new byte[]{
                    16, -59, 20, -5, -54, -85, 110, 61, -51, -99,
                    70, -78, 11, -44, 3, 5, -120, 58, -14, 74,
                    13, -122, 35, 120, 14, -60, 67, 73, -58, -90,
                    42, 112}, "AES"), ivParameterSpec);
            byte[] arrayOfByte1 = cipher.doFinal(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(arrayOfByte2);
            byteArrayOutputStream.write(arrayOfByte1);
            return new String(ad(byteArrayOutputStream.toByteArray()));
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] ad(byte[] bArr) {
        byte[] bArr2 = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
        if (bArr != null) {
            int length = bArr.length;
            if (length != 0) {
                int i;
                int i2 = length / 3;
                int i3 = 0;
                byte[] bArr3 = new byte[(((length % 3 != 0 ? 1 : 0) + i2) * 4)];
                int i4 = 0;
                int i5 = 0;
                while (i3 < i2) {
                    i = i4 + 1;
                    int i6 = i + 1;
                    i4 = ((bArr[i4] & 255) << 16) | ((bArr[i] & 255) << 8);
                    i = i6 + 1;
                    i4 |= bArr[i6] & 255;
                    i6 = i5 + 1;
                    bArr3[i5] = bArr2[(i4 >> 18) & 63];
                    i5 = i6 + 1;
                    bArr3[i6] = bArr2[(i4 >> 12) & 63];
                    i6 = i5 + 1;
                    bArr3[i5] = bArr2[(i4 >> 6) & 63];
                    i5 = i6 + 1;
                    bArr3[i6] = bArr2[i4 & 63];
                    i3++;
                    i4 = i;
                }
                i2 *= 3;
                if (i2 < length) {
                    i3 = i4 + 1;
                    i4 = (bArr[i4] & 255) << 16;
                    i = i5 + 1;
                    bArr3[i5] = bArr2[(i4 >> 18) & 63];
                    int i7;
                    if (i2 + 1 < length) {
                        i7 = ((bArr[i3] & 255) << 8) | i4;
                        length = i + 1;
                        bArr3[i] = bArr2[(i7 >> 12) & 63];
                        i2 = length + 1;
                        bArr3[length] = bArr2[(i7 >> 6) & 63];
                    } else {
                        i7 = i + 1;
                        bArr3[i] = bArr2[(i4 >> 12) & 63];
                        i2 = i7 + 1;
                        bArr3[i7] = (byte) 61;
                    }
                    bArr3[i2] = (byte) 61;
                }
                return bArr3;
            }
        }
        return null;
    }

    private String getFinger() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(brand).append("/").append(brand).append("/").append(brand)
                    .append(":").append(androidVersion).append("/").append(model).append("/").append(208)
                    .append(":user/release-keys");
            return md5(sb.toString());
        } catch (Throwable throwable) {
            return null;
        }
    }

    private String getISO3Country() {
        return "CHN";
    }

    private static void b(Map paramMap, String paramString1, String paramString2) {
        if (paramString1 != null && paramString2 != null && paramString2.length() > 0)
            paramMap.put(paramString1, paramString2);
    }

    private static String c() {
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            return String.format("%04d %02d %02d %02d:%02d:%02d.%03d", new Object[]{Integer.valueOf(calendar.get(1)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12)), Integer.valueOf(calendar.get(13)), Integer.valueOf(calendar.get(14))});
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static String getMac(String data) {
        String mac = md5("mac" + data).substring(0, 12);
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (char c : mac.toCharArray()) {
            if (temp.length() < 2) {
                temp.append(c);
            } else {
                sb.append(temp);
                temp.delete(0, temp.length());
            }
        }
        return sb.toString();
    }


    private String getImei() {
        return md5("imei" + username).substring(0, 16);
    }

    private String getAndroidId() {
        return md5("android" + username).substring(0, 16);
    }

    public static String randomScreenSize() {

        return screenSizes[(int) (Math.random() * screenSizes.length)];
    }

    public static String randomANDROIDVERSION() {
        Object[] versions = ANDROID_VERSION.keySet().toArray();

        return versions[(int) (Math.random() * versions.length)].toString();
    }

    public static String randomANDROIDBRAND() {

        return ANDROID_BRAND[(int) (Math.random() * ANDROID_BRAND.length)];
    }

    public static String randomANDROIDMODEL() {

        return ANDROID_MODEL[(int) (Math.random() * ANDROID_MODEL.length)];
    }
}
