package cn.xanderye.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author XanderYe
 * @description:
 * @date 2021/4/22 13:40
 */
public class EmailUtil {
    private static final Properties MAIL_PROPERTIES = new Properties();

    private static String username = null;

    private static String password = null;

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream("email.properties");
            MAIL_PROPERTIES.load(is);
            username = MAIL_PROPERTIES.getProperty("mail.username");
            password = MAIL_PROPERTIES.getProperty("mail.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件
     * @param title
     * @param content
     * @param toEmail
     * @return void
     * @author XanderYe
     * @date 2021/4/22
     */
    public static void sendEmail(String title, String content, String toEmail) throws MessagingException {
        Session session = Session.getInstance(MAIL_PROPERTIES);
        session.setDebug(true);
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
}
