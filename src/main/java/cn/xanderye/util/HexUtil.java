package cn.xanderye.util;

/**
 * Created on 2020/3/2.
 *
 * @author 叶振东
 */
public class HexUtil {

    private static final String HEX_STRING = "0123456789ABCDEF";

    /**
     * byte数组转十六进制字符串
     *
     * @param bytes
     * @return java.lang.String
     * @author yezhendong
     * @date 2019/8/6
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex.toUpperCase()).append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 字符串转成字节流
     */
    public static byte[] hexStringToByteArray(String hex) {
        if (hex.length() % 2 == 0) {
            hex = hex.replace(" ", "").toUpperCase();
            int len = (hex.length() / 2);
            byte[] bytes = new byte[len];
            char[] chars = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int pos = i * 2;
                bytes[i] = (byte) (HEX_STRING.indexOf(chars[pos]) << 4 | HEX_STRING.indexOf(chars[pos + 1]));
            }
            return bytes;
        }
        return null;
    }
}
