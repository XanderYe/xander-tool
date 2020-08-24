package cn.xanderye.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2020/7/30.
 *
 * @author XanderYe
 */
public class StrokeUtil {
    private static JSONObject strokeJson;
    static {
        try (InputStream inputStream = StrokeUtil.class.getClassLoader().getResourceAsStream("stroke.json")){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            strokeJson = JSON.parseObject(new String(bos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取汉字在笔划库中的排序编号
     * @param keyPoint
     * @return java.lang.Integer
     * @author XanderYe
     * @date 2020/8/24
     */
    public static Integer stroke(Integer keyPoint) {
        if (keyPoint == null) {
            return -1;
        }
        Integer order = (Integer) strokeJson.get(keyPoint);
        return order == null ? -1 : order;
    }

    /**
     * 按笔划升序
     * @param a
     * @param b
     * @return java.lang.Integer
     * @author yezhendong
     * @date 2020/8/24
     */
    public static Integer compareAsc(Integer a, Integer b) {
        Integer order1 = stroke(a);
        Integer order2 = stroke(b);
        return order1 - order2;
    }

    /**
     * 按笔划降序
     * @param a
     * @param b
     * @return java.lang.Integer
     * @author yezhendong
     * @date 2020/8/24
     */
    public static Integer compareDesc(Integer a, Integer b) {
        Integer order1 = stroke(a);
        Integer order2 = stroke(b);
        return order2 - order1;
    }
}
