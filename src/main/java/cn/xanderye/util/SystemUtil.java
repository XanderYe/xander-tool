package cn.xanderye.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created on 2020/11/5.
 *
 * @author XanderYe
 */
public class SystemUtil {

    /**
     * 调用cmd方法，默认GBK编码
     * @param cmdStr
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/11/5
     */
    public static String executeCmd(String cmdStr) {
        return executeCmd(cmdStr, "GBK");
    }

    /**
     * 调用cmd方法
     * @param cmdStr
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/11/5
     */
    public static String executeCmd(String cmdStr, String charset) {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("cmd /c " + cmdStr);
            InputStream is = process.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is, charset));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = buffer.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            is.close();
            process.waitFor();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(executeCmd("D:\\SOFTWARE\\淘宝猫猫脚本\\adb.exe version"));
    }
}
