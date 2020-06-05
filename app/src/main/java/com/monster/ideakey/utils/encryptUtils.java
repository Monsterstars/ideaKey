package com.monster.ideakey.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @auther: Monster
 * @date: 2020/5/11
 * @description: DES加密工具类 java实现
 */
public class encryptUtils {
    public static byte[] initKey() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }
//    public static void main(String[] args) throws Exception {
//        byte[] desKey = base64Utils.decode("123456".toCharArray());
//        System.out.println("DES KEY : " + desKey);
//        System.out.println(Arrays.toString(desKey));
//        byte[] desResult = {'a','b','c'};
//        System.out.println(desResult + ">>>DES 加密结果>>>" + encryptUtils.encrypt(desResult, desKey));
//        byte[] desPlain = encryptUtils.decrypt(encryptUtils.encrypt(desResult, desKey), desKey);
//        System.out.println(encryptUtils.encrypt(desResult, desKey) + ">>>DES 解密结果>>>" + new String(desPlain));
//    }
}
