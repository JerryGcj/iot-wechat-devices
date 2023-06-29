package com.wechat.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AESUtil {

    /**
     * 加密解密算法 工作模式 填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    private static byte[] initKey(String keyPassword) {
        return Base64.getDecoder().decode(keyPassword);
    }

    /**
     * 转换密钥，二进制密钥转换为密钥对象
     *
     * @param key 二级制密钥
     * @return Key 密钥
     * @throws Exception
     */
    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param str 需要加密的内容
     * @return
     */
    public static String encrypt(String str, String password) {
        try {
            byte[] data = str.getBytes();
            byte[] key = initKey(password);
            // 实例化，创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 初始化 设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, toKey(key));
            // 加密
            return parseByte2HexStr(cipher.doFinal(data));
        } catch (IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | BadPaddingException e) {
            log.error("", e);
        }
        return null;

    }

    /**
     * 将二进制转换为十六进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            String content = "{\"appId\":\"SZHY\",\"accessTime\":\""+System.currentTimeMillis()+"\",\"business\":\"20210128142535475\"}";

            // 用AES加密内容
            //String miContent = AES.encrypt(content, mingKey);
            String miContent = encrypt(content,"7nmmYfxGNhxQtrMTES/08A==");
            System.out.println("AES加密后的内容:" + miContent);
            // 用AES解密内容
            //String mingContent = AES_Decrypt("ABUUTrv.L1wS", miContent);
            //System.out.println("AES解密后的内容:" + mingContent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
