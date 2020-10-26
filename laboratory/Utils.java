package nike.laboratory;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static JSONObject simpleJSON(Object... kvs) {
        if (kvs == null || kvs.length % 2 != 0) {
            return null;
        }
        JSONObject json = new JSONObject();
        String key = null;
        for (int i = 0; i < kvs.length; i++) {
            Object data = kvs[i];
            if (i % 2 == 0) {
                key = data.toString();
            } else {
                json.put(key, data);
            }
        }
        return json;
    }

    public static JSONArray simpleJSONArray(Object... objs) {
        JSONArray arr = new JSONArray();
        for (Object obj : objs) {
            arr.add(obj);
        }
        return arr;
    }

    public static String md5(String data) {
        return md5(data.getBytes());
    }

    public static String md5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(data);
        byte[] digest = md.digest();
        BigInteger no = new BigInteger(1, digest);

        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static byte[] consumeInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = inputStream.read(buffer)) != -1; ) {
            byteArrayOutputStream.write(buffer, 0, count);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void simulateTouchEvent(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
        MotionEvent upEvent = MotionEvent.obtain(downTime + 100, eventTime + 100,
                MotionEvent.ACTION_UP, x,y, metaState);
        view.dispatchTouchEvent(upEvent);
    }
}
