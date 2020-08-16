package com.chinatelecom.template.utils;

import cn.hutool.http.HttpUtil;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

@Component
public class WxUtils {

    @Value("${oauth.wx.appid}")
    private static String appid;

    @Value("${oauth.wx.appsecret}")
    private static String secret;

//    @Value("${oauth.wx.url}")
    private static String authApi = "https://api.weixin.qq.com/sns/jscode2session";

//    @Value("${oauth.wx.AES}")
    private static String AES = "AES";

//    @Value("${oauth.wx.AES_CBC_PADDING}")
    private static String AES_CBC_PADDING = "AES/CBC/PKCS7Padding";

    public static String wxDecrypt(String encrypted, String session_key, String iv) {
        String result = null;
        byte[] encrypted64 = Base64.decodeBase64(encrypted);
        byte[] key64 = Base64.decodeBase64(session_key);
        byte[] iv64 = Base64.decodeBase64(iv);
        try {
            init();
            result = new String(decrypt(encrypted64, key64, generateIV(iv64)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *    * 初始化密钥
     *
     */

    public static void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(AES);
        KeyGenerator.getInstance(AES).init(128);
    }

    /**
     *    * 生成iv
     *
     */
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        // Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance(AES);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     *    * 生成解密
     *
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv)
            throws Exception {
        Key key = new SecretKeySpec(keyBytes, AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        // 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }

    /**
     * 根据code获取
     * @param code
     * @return
     */
    public static String getLoginInfo(String code){
        String url = authApi+"?" +
                "appid="+appid +
                "&secret="+secret+
                "&js_code="+code+
                "&grant_type=authorization_code";
        String result = HttpUtil.createGet(url)
                .execute()
                .charset("utf-8")
                .body();
        return result;
    }

//    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
}