package cn.xanderye.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/1/23.
 * 服务器消息推送工具类，依赖HttpUtil
 *
 * @author XanderYe
 */
public class MsgPushUtil {

    private static final String SERVER_CHAN_URL = "https://sc.ftqq.com/${scKey}.send";

    private static final String DB_BOT_URL = "https://oapi.dingtalk.com/robot/send?access_token=${token}";

    private static final String BARK_URL = "https://api.day.app/${deviceId}/${content}";

    /**
     * Server酱推送
     * @see <a href="http://sc.ftqq.com/3.version">Server酱推送</a>
     *
     * @param scKey
     * @param title
     * @param content
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/23
     */
    public static String serverChanPush(String scKey, String title, String content) throws IOException {
        String url = SERVER_CHAN_URL.replace("${scKey}", scKey);
        Map<String, Object> params = new HashMap<>(16);
        params.put("text", title);
        params.put("desp", content);
        return HttpUtil.doPost(url, params);
    }


    /**
     * 钉钉机器人推送
     *
     * @param token
     * @param secret
     * @param content
     * @param isAtAll      是否@全体
     * @param atMobileList @的手机号列表
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/23
     */
    public static String dingTalkBotPush(String token, String secret, String content, boolean isAtAll, List<String> atMobileList) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String webhook = DB_BOT_URL.replace("${token}", token);
        long timestamp = System.currentTimeMillis();
        String sign = getSign(timestamp, secret);
        webhook = webhook + "&timestamp=" + timestamp + "&sign=" + sign;
        JSONObject atJson = new JSONObject();
        // 是否通知所有人
        atJson.put("isAtAll", isAtAll);
        // 通知人列表
        if (!isAtAll) {
            atJson.put("atMobiles", atMobileList);
        }
        // 消息内容
        JSONObject contentJson = new JSONObject();
        contentJson.put("content", content);
        // 请求体
        JSONObject params = new JSONObject();
        params.put("msgtype", "text");
        params.put("text", contentJson);
        params.put("at", atJson);
        return HttpUtil.doPostJSON(webhook, params.toJSONString());
    }

    /**
     * ios bark推送
     *
     * @see <a href="https://apps.apple.com/cn/app/bark-customed-notifications/id1403753865">Bark</a>"
     * @param deviceId
     * @param content
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/23
     */
    public static String barkPush(String deviceId, String content, String sound) throws IOException {
        try {
            content = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = BARK_URL.replace("${deviceId}", deviceId).replace("${content}", content);
        Map<String, Object> params = null;
        if (null != sound && !"".equals(sound)) {
            params = new HashMap<>(16);
            params.put("sound", sound);
        }
        System.out.println(url);
        return HttpUtil.doGet(url, params);
    }

    /**
     * 钉钉机器人签名
     *
     * @param timestamp
     * @param secret
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/23
     */
    private static String getSign(long timestamp, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }
}
