package com.wechat.utils;


import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

public class SignatureUtil {

    public static final String TOKEN = "3ad8id23i907o2kmli03";//用于生成数字签名

    /**
     * 创建SHA1签名
     * @param params
     * @return SHA1签名
     */
    public static String createSignature(SortedMap<String, String> params) {
        return sha1Encrypt(sortParams(params));
    }

    /**
     * 创建SHA1签名
     * @param timeStamp
     * @param nonce
     * @return SHA1签名
     */
    public static String createSignature(String timeStamp, String nonce) {
        SortedMap<String, String> signParams = new TreeMap<String, String>();
        signParams.put("token", TOKEN);
        signParams.put("timeStamp", timeStamp);
        signParams.put("nonce", nonce);
        return createSignature(signParams);
    }

    /**
     * 使用SHA1算法对字符串进行加密
     * @param str
     * @return
     */
    public static String sha1Encrypt(String str) {

        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {

            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(buf);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成时间戳
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 生成6位随机字符串
     * @return
     */
    public static String getRandomStr() {
        int length = 6;
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 根据参数名称对参数进行字典排序
     * @param params
     * @return
     */
    private static String sortParams(SortedMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<String, String>> es = params.entrySet();
        Iterator<Entry<String, String>> it = es.iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String k = entry.getKey();
            String v = entry.getValue();
            sb.append(k + "=" + v + "&");
        }
        return sb.substring(0, sb.lastIndexOf("&"));
    }

    public static void main(String[] args) throws Exception {
        //c4db7f5867a6290378cf24429f2803da0516a788
        String timeStamp = getTimeStamp();
        String nonce = getRandomStr();
        String sign = createSignature(timeStamp, nonce);
        System.out.println("timeStamp:"+timeStamp);
        System.out.println("nonce:"+nonce);
        System.out.println("signature:" + sign);
    }

}
