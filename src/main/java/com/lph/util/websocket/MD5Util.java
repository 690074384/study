package com.lph.util.websocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MD5Util {

    public static String buildMysignV1(Map<String, String> sArray,
                                       String secretKey) {
        String mysign = "";
        try {
            String prestr = createLinkString(sArray);
            prestr = prestr + "&secret_key=" + secretKey;
            System.out.println("prestr     " + prestr);
            mysign = getMD5String(prestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mysign;
    }

    static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }
        return prestr.toString();
    }

    /**
     * 生成32位大写MD5值
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    static String getMD5String(String str) {
        try {
            if (str == null || str.trim().length() == 0) {
                return "";
            }
            byte[] bytes = str.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(HEX_DIGITS[(aByte & 0xf0) >> 4]).append(HEX_DIGITS[aByte & 0xf]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getParams(Map<String, String> map) {
        StringBuilder params = new StringBuilder("{");
        for (Entry<String, String> param : map.entrySet()) {
            params.append("'").append(param.getKey()).append("':'").append(param.getValue()).append("',");
        }
        params.replace(params.length() - 1, params.length(), "}");
        return params.toString();
    }
}
