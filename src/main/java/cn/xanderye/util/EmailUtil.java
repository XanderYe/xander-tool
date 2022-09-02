package cn.xanderye.util;

import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author XanderYe
 * @description:
 * @date 2021/4/22 13:40
 */
@Slf4j
public class EmailUtil {

    /**
     * 配置对象
     */
    private static Properties mailConfig;
    /**
     * 用户名
     */
    private static String username = null;
    /**
     * 密码
     */
    private static String password = null;

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream("email.properties");
            if (is != null) {
                mailConfig = new Properties();
                mailConfig.load(is);
                username = mailConfig.getProperty("mail.username");
                password = mailConfig.getProperty("mail.password");
            } else {
                log.warn("Could not found email.properties, please configure manually.");
            }
        } catch (Exception e) {
            log.error("Error initializing the config.");
        }
    }

    /**
     * 手动配置邮箱
     * @param properties
     * @return void
     * @author yezhendong
     * @date 2021/12/16
     */
    public static void setConfig(Properties properties) {
        mailConfig = properties;
    }

    /**
     * 发送邮件给一个地址
     * @param title
     * @param content
     * @param toEmail
     * @return void
     * @author XanderYe
     * @date 2021/4/22
     */
    public static void sendSimpleEmail(String title, String content, String toEmail) throws MessagingException {
        Session session = Session.getInstance(mailConfig);
        Message msg = new MimeMessage(session);
        // 标题
        msg.setSubject(title);
        // 内容
        msg.setText(content);
        // 发件人
        msg.setFrom(new InternetAddress(username));
        // 收件人
        msg.setRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmail));
        msg.saveChanges();

        Transport transport = session.getTransport();
        transport.connect(username,password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    /**
     *
     * @param title
     * @param content
     * @param toEmailList 收件人
     * @param carbonCopyList 抄送
     * @return void
     * @author XanderYe
     * @date 2021/6/18
     */
    public static void sendEmail(String title, String content, List<String> toEmailList, List<String> carbonCopyList) throws MessagingException {
        if (toEmailList == null || toEmailList.isEmpty()) {
            throw new RuntimeException("接收人不为空");
        }
        Session session = Session.getInstance(mailConfig);
        Message msg = new MimeMessage(session);

        InternetAddress[] addressesTo = new InternetAddress[toEmailList.size()];
        for (int i = 0; i < toEmailList.size(); i++) {
            addressesTo[i] = new InternetAddress(toEmailList.get(i));
        }

        InternetAddress[] addressesCc = null;
        if (carbonCopyList != null && !carbonCopyList.isEmpty()) {
            addressesCc = new InternetAddress[carbonCopyList.size()];
            for (int i = 0; i < carbonCopyList.size(); i++) {
                addressesCc[i] = new InternetAddress(carbonCopyList.get(i));
            }
        }

        // 标题
        msg.setSubject(title);
        // 内容
        msg.setText(content);
        // 发件人
        msg.setFrom(new InternetAddress(username));
        // 收件人
        msg.setRecipients(Message.RecipientType.TO, addressesTo);
        // 抄送
        if (addressesCc != null) {
            msg.setRecipients(Message.RecipientType.CC, addressesCc);
        }
        msg.saveChanges();

        Transport transport = session.getTransport();
        transport.connect(username, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
}
