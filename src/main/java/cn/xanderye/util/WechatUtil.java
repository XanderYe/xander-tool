package cn.xanderye.util;

import java.io.*;

/**
 * @author XanderYe
 * @description: 微信工具类
 * @date 2021/5/18 15:08
 */
public class WechatUtil {
    /**
     * JPG开头二进制
     */
    private static final byte[] JPG_BYTES = new byte[]{-1, -40};
    /**
     * PNG开头二进制
     */
    private static final byte[] PNG_BYTES = new byte[]{-119, 80};

    /**
     * 解密微信图片
     * @param inputPath
     * @param outputPath
     * @return void
     * @author XanderYe
     * @date 2021/5/18
     */
    public static void decodeImage(String inputPath, String outputPath) throws IOException {
        File datFile = new File(inputPath);
        if (!datFile.exists()) {
            throw new FileNotFoundException("dat文件不存在");
        }
        File dir = new File(outputPath);
        if (!dir.isDirectory()) {
            throw new IOException("outputPath请传入文件夹");
        }
        String fileName = datFile.getName().substring(0, datFile.getName().lastIndexOf("."));
        OutputStream os = null;
        try (InputStream is = new FileInputStream(datFile)) {
            byte[] data = IoUtil.readBytes(is);
            byte xorData = 0;
            String suffix = null;
            if ((byte) (data[0] ^ JPG_BYTES[0]) == (byte) (data[1] ^ JPG_BYTES[1])) {
                xorData = (byte) (data[0] ^ JPG_BYTES[0]);
                suffix = "jpg";
            } else if ((byte) (data[0] ^ PNG_BYTES[0]) == (byte) (data[1] ^ PNG_BYTES[1])) {
                xorData = (byte) (data[0] ^ PNG_BYTES[0]);
                suffix = "png";
            }
            if (suffix == null) {
                throw new IOException("解密失败");
            }
            File outputFile = new File(outputPath + File.separator + fileName + "." + suffix);
            os = new FileOutputStream(outputFile);
            decodeByte(data, xorData);
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(os);
        }
    }

    /**
     * 异或处理图片数据
     * @param data
     * @param xorData
     * @return void
     * @author XanderYe
     * @date 2021/5/18
     */
    private static void decodeByte(byte[] data, byte xorData) {
        for (int i = 0; i < data.length; i++) {
            data[i] ^= xorData;
        }
    }
}
