package cn.xanderye.util;

public class QQUtil {

    /**
     * qq登录需要用到的hash33算法
     * @param qrsig
     * @return java.lang.String
     * @author XanderYe
     * @date 2020-03-15
     */
    public static String hash33(String qrsig) {
        int e = 0;
        for (int i = 0; i < qrsig.length(); i++) {
            e += (e << 5) + qrsig.charAt(i);
        }
        return String.valueOf(2147483647 & e);
    }

    /**
     * 调用qq接口用到的gtk算法
     * @param skey
     * @return java.lang.String
     * @author XanderYe
     * @date 2020-03-15
     */
    public static String getGTK(String skey) {
        int hash = 5381;
        for (int i = 0; i < skey.length(); ++i) {
            hash += (hash << 5) + skey.charAt(i);
        }
        return String.valueOf(hash & 0x7fffffff);
    }
}
