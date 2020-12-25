package cn.xanderye.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created on 2020/5/18.
 *
 * @author XanderYe
 */
public class ZipUtil {

    private static final int BUFFER_LENGTH = 4 * 1024;

    /**
     * 压缩方法
     * @param targetPath 目标路径
     * @param zipFileName 压缩文件名
     * @param filePaths 源文件（夹）路径
     * @return void
     * @author XanderYe
     * @date 2020/5/18
     */
    public static void zip(String targetPath, String zipFileName, String...filePaths) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetPath + File.separator + zipFileName));
        for (String filePath : filePaths) {
            File file = new File(filePath);
            compress(out, file, file.getName());
        }
        out.close();
    }

    /**
     * 解压方法
     * @param zipFilePath 压缩文件路径
     * @param targetPath 解压路径
     * @return void
     * @author XanderYe
     * @date 2020/5/18
     */
    public static void unzip(String zipFilePath, String targetPath) throws IOException {
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream(zipFilePath);
            zis = new ZipInputStream(fis);
            File dir = new File(targetPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ZipEntry entry;
            while (((entry = zis.getNextEntry()) != null)) {
                File file = new File(targetPath + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    OutputStream os = new FileOutputStream(file);
                    byte[] buffer = new byte[BUFFER_LENGTH];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.close();
                    zis.closeEntry();
                }
            }
        } finally {
            if (zis != null) {
                zis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * 压缩递归方法
     * @param zos
     * @param f 文件对象
     * @param path 文件相对路径
     * @return void
     * @author XanderYe
     * @date 2020/5/18
     */
    private static void compress(ZipOutputStream zos, File f, String path) throws IOException {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (fl == null || fl.length == 0) {
                zos.putNextEntry(new ZipEntry(path + "/"));
                zos.closeEntry();
            } else {
                for (File file : fl) {
                    compress(zos, file, path + "/" + file.getName());
                }
            }
        } else {
            zos.putNextEntry(new ZipEntry(path));
            FileInputStream fin = new FileInputStream(f);
            byte[] buffer = new byte[BUFFER_LENGTH];
            int len;
            while ((len = fin.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
            fin.close();
            zos.closeEntry();
        }
    }
}
