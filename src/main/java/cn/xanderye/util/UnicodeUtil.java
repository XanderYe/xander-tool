package cn.xanderye.util;

public class UnicodeUtil {
    /**
     * Unicode转中文
     * @param unicode
     * @return java.lang.String
     * @author XanderYe
     * @date 2020-03-29
     */
    public static String unicodeToCn(String unicode) {
        String[] uns = unicode.split("\\\\u");
        StringBuilder returnStr = new StringBuilder();
        for (int i = 1; i < uns.length; i++) {
            returnStr.append((char) Integer.valueOf(uns[i], 16).intValue());
        }
        return returnStr.toString();
    }

    /**
     * 中文转Unicode
     * @param cn
     * @return java.lang.String
     * @author XanderYe
     * @date 2020-03-29
     */
    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        StringBuilder returnStr = new StringBuilder();
        for (char aChar : chars) {
            returnStr.append("\\u").append(Integer.toString(aChar, 16));
        }
        return returnStr.toString();
    }
}
