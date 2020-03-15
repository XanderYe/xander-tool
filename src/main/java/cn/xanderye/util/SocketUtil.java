package cn.xanderye.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2020/3/2.
 *
 * @author XanderYe
 */
public class SocketUtil {

    /**
     * 读取数据方法
     *
     * @param inputStream
     * @return byte[]
     * @author XanderYe
     * @date 2019/8/6
     */
    public static byte[] read(InputStream inputStream) {
        return read(inputStream, null);
    }

    /**
     * 读取定长数据方法
     *
     * @param inputStream
     * @return byte[]
     * @author XanderYe
     * @date 2019/8/6
     */
    public static byte[] read(InputStream inputStream, Integer length) {
        try {
            // 避免获取0的情况
            int firstByte = inputStream.read();
            length = length == null ? inputStream.available() + 1 : length;
            byte[] bytes = new byte[length];
            bytes[0] = (byte) firstByte;
            inputStream.read(bytes, 1, length - 1);
            return bytes;
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
