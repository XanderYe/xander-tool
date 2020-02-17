package cn.xanderye.util;

/**
 * 身份证校验工具
 * @author XanderYe
 * @date 2020/2/4
 */
public class IdCardUtil {
    private static final int ID_LENGTH = 18;

    /**
     * 生成身份证
     * @param idNum
     * @return java.lang.String
     * @author yezhendong
     * @date 2020/2/12
     */
    public static String generateIdNum(String idNum) {
        return idNum + calcCheckCode(idNum);
    }
    /**
     * 计算校验码
     * @param idNum 十七位
     * @return java.lang.String
     * @author yezhendong
     * @date 2020/2/4
     */
    private static String calcCheckCode(String idNum) {
        //权数数组
        int[] weightArray = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String vCode = "10X98765432";
        int s = 0;
        for (int i = 0; i < 17; i++) {
            s += Integer.parseInt(String.valueOf(idNum.charAt(i))) * weightArray[i];
        }
        return String.valueOf(vCode.charAt(s % 11));
    }

    /**
     * 验证身份证
     * @param idNum
     * @return boolean
     * @author yezhendong
     * @date 2020/2/12
     */
    public static boolean validate(String idNum) {
        if (idNum == null || idNum.length() != ID_LENGTH) {
            return false;
        }
        String num = idNum.substring(0, ID_LENGTH - 1);
        return idNum.equals(generateIdNum(num));
    }
}
