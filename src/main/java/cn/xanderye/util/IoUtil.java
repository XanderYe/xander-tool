package cn.xanderye.util;

import java.io.*;

/**
 * Created on 2020/3/2.
 *
 * @author XanderYe
 */
public class IoUtil {

    /**
     * 复制流
     * @param in
     * @param out
     * @return long
     * @author XanderYe
     * @date 2021/4/20
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, 8192);
    }

    /**
     * 复制流
     * @param in
     * @param out
     * @param bufferSize
     * @return long
     * @author XanderYe
     * @date 2021/4/20
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        if (bufferSize < 0) {
            bufferSize = 8192;
        }
        byte[] bytes = new byte[bufferSize];
        int len;
        long size = 0L;
        while ((len = in.read(bytes)) > 0) {
            out.write(bytes, 0, len);
            size += len;
            out.flush();
        }
        return size;
    }

    /**
     * 读取byte数组
     * @param in
     * @return byte[]
     * @author XanderYe
     * @date 2021/4/20
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        return readBytes(in, true);
    }

    /**
     * 读取byte数组
     * @param in
     * @param isClose
     * @return byte[]
     * @author XanderYe
     * @date 2021/4/20
     */
    public static byte[] readBytes(InputStream in, boolean isClose) throws IOException {
        return read(in, isClose).toByteArray();
    }

    /**
     * 读取outputStream
     * @param in
     * @param isClose
     * @return byte[]
     * @author XanderYe
     * @date 2021/4/20
     */
    public static ByteArrayOutputStream read(InputStream in, boolean isClose) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            copy(in, bos);
        } finally {
            if (isClose) {
                close(in);
            }
        }
        return bos;
    }

    /**
     * 读取socket数据方法
     *
     * @param inputStream
     * @return byte[]
     * @author XanderYe
     * @date 2019/8/6
     */
    public static byte[] readSocket(InputStream inputStream) {
        return readSocket(inputStream, null);
    }

    /**
     * 读取socket定长数据方法
     *
     * @param inputStream
     * @return byte[]
     * @author XanderYe
     * @date 2019/8/6
     */
    public static byte[] readSocket(InputStream inputStream, Integer length) {
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

    /**
     * 关闭流
     * @param closeable
     * @return void
     * @author XanderYe
     * @date 2021/4/20
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
