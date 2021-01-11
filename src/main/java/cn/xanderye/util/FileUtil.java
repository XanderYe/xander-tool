package cn.xanderye.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created on 2020/9/1.
 *
 * @author XanderYe
 */
public class FileUtil {

    /**
     * 默认缓存大小
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * 默认中等缓存大小
     */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 4096;

    /**
     * 默认大缓存大小
     */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 8192;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 复制单文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream(targetPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            int len;
            while ((len = bis.read(bytes)) != EOF) {
                bos.write(bytes, 0, len);
            }
        }
    }

    /**
     * 复制文件夹
     * @param sourcePath 源文件夹路径
     * @param targetPath 目标文件夹路径
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    public static void copyDictionary(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException();
        }
        File targetFile = new File(targetPath);
        targetFile.mkdirs();
        File[] fileList = sourceFile.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    copyDictionary(f.getAbsolutePath(), targetPath + File.separator + f.getName());
                } else {
                    copyFile(f.getAbsolutePath(), targetPath + File.separator + f.getName());
                }
            }
        }
    }

    /**
     * 删除文件夹
     * @param filePath
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    public static void deleteDictionary(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(FileUtil::deleteFile);
        }
    }

    /**
     * 删除文件
     * @param filePath
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    private static void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件编码
     * @param filePath
     * @return java.nio.charset.Charset
     * @author XanderYe
     * @date 2021/1/11
     */
    public static Charset identifyCharset(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        Charset charset;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] head = new byte[3];
            fis.read(head);
            //判断TXT文件编码格式
            if (head[0] == -1 && head[1] == -2) {
                //Unicode              -1,-2,84
                charset = Charset.forName("Unicode");
            } else if (head[0] == -2 && head[1] == -1) {
                //Unicode big endian   -2,-1,0,84
                charset = StandardCharsets.UTF_16;
            } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
                //UTF-8                -17,-69,-65,84
                charset = StandardCharsets.UTF_8;
            } else {
                //ANSI                  84 = T
                charset = Charset.forName("gb2312");
            }
        }
        return charset;
    }
}
