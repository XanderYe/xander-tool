package cn.xanderye.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

/**
 * Created on 2020/5/19.
 *
 * @author XanderYe
 */
public class RSAUtil {

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /**
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 加解密算法/工作模式/填充方式
     */
    public static final String KEY_ALGORITHM_STR = "RSA/ECB/PKCS1Padding";

    /**
     * 随机生成密钥对
     *
     * @param filePath
     * @return void
     * @author XanderYe
     * @date 2020/5/19
     */
    public static void genKeyPair(String filePath) throws IOException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (keyPairGen != null) {
            // 初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(1024, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 得到私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 得到公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 得到公钥字符串
            String publicKeyString = CodecUtil.base64Encode(publicKey.getEncoded());
            // 得到私钥字符串
            String privateKeyString = CodecUtil.base64Encode(privateKey.getEncoded());
            // 将密钥对写入到文件
            FileWriter pubFw = new FileWriter(filePath + "/publicKey.keystore");
            FileWriter priFw = new FileWriter(filePath + "/privateKey.keystore");
            BufferedWriter pubBw = new BufferedWriter(pubFw);
            BufferedWriter priBw = new BufferedWriter(priFw);
            pubBw.write(publicKeyString);
            priBw.write(privateKeyString);
            pubBw.flush();
            pubBw.close();
            pubFw.close();
            priBw.flush();
            priBw.close();
            priFw.close();
        }
    }

    /**
     * 从文件中加载公钥
     *
     * @param path
     * @return java.security.interfaces.RSAPrivateKey
     * @author XanderYe
     * @date 2020/5/19
     */
    public static RSAPublicKey loadPublicKeyByFile(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String readLine;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) != '-') {
                sb.append(readLine);
            }
        }
        br.close();
        return loadPublicKeyByStr(sb.toString());
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     * @return java.security.interfaces.RSAPublicKey
     * @author XanderYe
     * @date 2020/5/19
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (publicKeyStr != null) {
            byte[] buffer = CodecUtil.base64DecodeToByteArray(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥
     *
     * @param hexModulus 十六进制系数
     * @param hexPublicExponent 十六进制专用指数
     * @return java.security.interfaces.RSAPublicKey
     * @author XanderYe
     * @date 2022/3/29
     */
    public static RSAPublicKey generatePublicKey(String hexModulus, String hexPublicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPublicExponent)) {
            return null;
        }
        byte[] modulus = CodecUtil.hexStringToByteArray(hexModulus);
        byte[] publicExponent = CodecUtil.hexStringToByteArray(hexPublicExponent);
        return generatePublicKey(modulus, publicExponent);
    }

    /**
     * 根据给定的系数和专用指数字符串构造一个RSA专用的公钥
     *
     * @param modulus 系数
     * @param publicExponent 专用指数
     * @return java.security.interfaces.RSAPublicKey
     * @author XanderYe
     * @date 2022/3/29
     */
    public static RSAPublicKey generatePublicKey(byte[] modulus, byte[] publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus),
                new BigInteger(publicExponent));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * 从文件中加载私钥
     *
     * @param path
     * @return java.security.interfaces.RSAPrivateKey
     * @author XanderYe
     * @date 2020/5/19
     */
    public static RSAPrivateKey loadPrivateKeyByFile(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String readLine;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) != '-') {
                sb.append(readLine);
            }
        }
        br.close();
        return loadPrivateKeyByStr(sb.toString());
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr
     * @return java.security.interfaces.RSAPrivateKey
     * @author XanderYe
     * @date 2020/5/19
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (privateKeyStr != null) {
            byte[] buffer = CodecUtil.base64DecodeToByteArray(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥
     *
     * @param hexModulus 十六进制系数
     * @param hexPrivateExponent 十六进制专用指数
     * @return java.security.interfaces.RSAPrivateKey
     * @author XanderYe
     * @date 2022/3/29
     */
    public static RSAPrivateKey generatePrivateKey(String hexModulus, String hexPrivateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPrivateExponent)) {
            return null;
        }
        byte[] modulus = CodecUtil.hexStringToByteArray(hexModulus);
        byte[] publicExponent = CodecUtil.hexStringToByteArray(hexPrivateExponent);
        return generatePrivateKey(modulus, publicExponent);
    }

    /**
     * 根据给定的系数和专用指数字符串构造一个RSA专用的私钥
     *
     * @param modulus 系数
     * @param privateExponent 专用指数
     * @return java.security.interfaces.RSAPrivateKey
     * @author XanderYe
     * @date 2022/3/29
     */
    public static RSAPrivateKey generatePrivateKey(byte[] modulus, byte[] privateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus),
                new BigInteger(privateExponent));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * 使用公钥加密
     * @param plainTextData
     * @param publicKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] encrypt(String plainTextData, RSAPublicKey publicKey) throws Exception {
        return encrypt(plainTextData.getBytes(StandardCharsets.UTF_8), publicKey);
    }

    /**
     * 使用公钥加密
     * @param plainTextData
     * @param publicKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] encrypt(byte[] plainTextData, RSAPublicKey publicKey) throws Exception {
        if (publicKey == null) {
            return null;
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_STR);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainTextData);
    }

    /**
     * 使用私钥加密
     * @param plainTextData
     * @param privateKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] encrypt(byte[] plainTextData, RSAPrivateKey privateKey) throws Exception {
        if (privateKey == null) {
            return null;
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_STR);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(plainTextData);
    }

    /**
     * 使用私钥加密
     * @param plainTextData
     * @param privateKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] encrypt(String plainTextData, RSAPrivateKey privateKey) throws Exception {
       return encrypt(plainTextData.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    /**
     * 使用私钥解密
     * @param cipherData
     * @param privateKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] decrypt(byte[] cipherData, RSAPrivateKey privateKey) throws Exception {
        if (privateKey == null) {
            return null;
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherData);
    }

    /**
     * 使用公钥解密
     * @param cipherData
     * @param publicKey
     * @return byte[]
     * @author XanderYe
     * @date 2020/5/19
     */
    public static byte[] decrypt(byte[] cipherData, RSAPublicKey publicKey) throws Exception {
        if (publicKey == null) {
            return null;
        }
        // 使用默认RSA
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(cipherData);
    }

    /**
     * 私钥签名
     * @param data
     * @param privateKey
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/12/21
     */
    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return CodecUtil.base64Encode(signature.sign());
    }

    /**
     * 公钥校验
     * @param data
     * @param publicKey
     * @param sign
     * @return boolean
     * @author XanderYe
     * @date 2021/12/21
     */
    public static boolean verify(byte[] data, PublicKey publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(CodecUtil.base64DecodeToByteArray(sign));
    }
}
