package cn.xanderye.util;

/**
 * 驼峰工具类
 */
public class CamelUnderlineUtil {

    private static final char UNDERLINE = '_';

    /**
     * 驼峰转下划线
     * @param param
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/9/1
     */
    public static String camelToUnderline(String param) {
        if (param == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = param.length();
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     * @param param
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/9/1
     */
    public static String underlineToCamel(String param) {
        if (param == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = param.length();
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
