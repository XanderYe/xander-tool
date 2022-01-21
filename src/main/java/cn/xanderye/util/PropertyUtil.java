package cn.xanderye.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;

/**
 * 配置工具类
 *
 * @author XanderYe
 * @date 2020/3/15
 */
@Slf4j
public class PropertyUtil {

    private static volatile Properties properties = null;

    private static String filePath = null;

    /**
     * 初始化
     *
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    public static void init() {
        init(null);
    }

    /**
     * 初始化
     *
     * @param path
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    public static void init(String path) {
        if (properties == null) {
            synchronized (PropertyUtil.class) {
                if (properties == null) {
                    if (path == null) {
                        path = System.getProperty("user.dir");
                    }
                    filePath = path.endsWith("/") || path.endsWith("\\") ? path + "config.properties" : path;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                log.error("Creating {} failed.", filePath);
                                return;
                            }
                        } catch (IOException e) {
                            log.error("Error when creating {}: {}", filePath, e.getMessage());
                            return;
                        }
                    }
                    try (FileInputStream fis = new FileInputStream(file);
                         InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                        properties = new Properties();
                        properties.load(inputStreamReader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取值
     *
     * @param key
     * @return java.lang.String
     * @author XanderYe
     * @date 2020-03-15
     */
    public static String get(String key) {
        if (properties == null) {
            return null;
        }
        return properties.getProperty(key);
    }

    /**
     * 获取值，为空返回默认值
     *
     * @param key
     * @param defaultValue
     * @return java.lang.String
     * @author XanderYe
     * @date 2022/1/21
     */
    public static String get(String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        return Optional.ofNullable(properties.getProperty(key)).orElse(defaultValue);
    }

    /**
     * 重写保存方法，不转义
     *
     * @param key
     * @param value
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    public static void save(String key, String value) {
        save(key, value, null);
    }

    /**
     * 重写保存方法，不转义
     *
     * @param key
     * @param value
     * @param comment
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    public static synchronized void save(String key, String value, String comment) {
        if (properties != null) {
            if (properties.getProperty(key) == null) {
                append(key, value, comment);
            } else {
                rewrite(key, value, comment);
            }
            properties.setProperty(key, value);
        }
    }

    /**
     * 追加文件
     *
     * @param key
     * @param value
     * @param comment
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void append(String key, String value, String comment) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            if (comment != null) {
                bw.write("#" + comment);
                bw.newLine();
            }
            bw.write(key + "=" + value);
            bw.newLine();
        } catch (IOException e) {
            log.error("Error when appending key {}: {}", key, e.getMessage());
        }
    }

    /**
     * 重写文件
     *
     * @param key
     * @param value
     * @param comment
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void rewrite(String key, String value, String comment) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            Enumeration<?> enumeration = properties.keys();
            while (enumeration.hasMoreElements()) {
                String k = (String) enumeration.nextElement();
                String val = properties.getProperty(k);
                if (k.equals(key)) {
                    if (comment != null) {
                        bw.write("#" + comment);
                        bw.newLine();
                    }
                    bw.write(key + "=" + value);
                } else {
                    bw.write(k + "=" + val);
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            log.error("Error when rewriting key {}: {}", key, e.getMessage());
        }
    }
}
