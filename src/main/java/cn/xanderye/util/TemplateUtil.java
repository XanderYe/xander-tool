package cn.xanderye.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * 模板工具类
 * @author XanderYe
 * @date 2021/2/9 17:29
 */
public class TemplateUtil {

    /**
     * 从resources下获取ftl模板导出文件
     * @param root
     * @param dictName
     * @param ftlName
     * @param targetPath
     * @param targetName
     * @return void
     * @author XanderYe
     * @date 2021/2/9
     */
    public static void generateFromResources(Map<String, Object> root, String dictName, String ftlName, String targetPath, String targetName) throws IOException, TemplateException {
        File parentFile = new File(targetPath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (FileWriter fw = new FileWriter(new File(targetPath + "/" + targetName));
             BufferedWriter bw = new BufferedWriter(fw)) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setClassForTemplateLoading(TemplateUtil.class, "/" + dictName);
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            Template temp = cfg.getTemplate(ftlName, "UTF-8");
            temp.process(root, bw);
            bw.flush();
        }
    }

    /**
     * 从文件获取ftl模板导出文件
     * @param root
     * @param sourcePath
     * @param ftlName
     * @param targetPath
     * @param targetName
     * @return void
     * @author XanderYe
     * @date 2021/2/9
     */
    public static void generateFromFile(Map<String, Object> root, String sourcePath, String ftlName, String targetPath, String targetName) throws IOException, TemplateException {
        File parentFile = new File(targetPath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (FileWriter fw = new FileWriter(new File(targetPath + "/" + targetName));
             BufferedWriter bw = new BufferedWriter(fw)) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setDirectoryForTemplateLoading(new File(sourcePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            Template temp = cfg.getTemplate(ftlName, "UTF-8");
            temp.process(root, bw);
            bw.flush();
        }
    }
}
