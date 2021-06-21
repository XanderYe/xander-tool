package cn.xanderye.util;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author yezhendong
 * @description:
 * @date 2021/6/21 10:29
 */
public class AreaUtil {

    private static List<District> areaData;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("area.json")){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            areaData = JSON.parseArray(new String(bos.toByteArray()), District.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据地名搜索
     * @param name
     * @return cn.xanderye.util.AreaUtil.District
     * @author XanderYe
     * @date 2021/6/21
     */
    public static District searchByName(String name) {
        return searchByName(areaData, name);
    }

    /**
     * 根据行政区划代码搜索
     * @param code
     * @return cn.xanderye.util.AreaUtil.District
     * @author XanderYe
     * @date 2021/6/21
     */
    public static District searchByCode(String code) {
        return searchByCode(areaData, code);
    }

    /**
     * 根据地名搜索
     * @param name
     * @return cn.xanderye.util.AreaUtil.District
     * @author XanderYe
     * @date 2021/6/21
     */
    private static District searchByName(List<District> children, String name) {
        if (children.size() > 0) {
            for (District district : children) {
                if (district.getKey().equals(name)) {
                    return district;
                }
                if (district.getChildren() != null && !district.getChildren().isEmpty()) {
                    District d = searchByName(district.getChildren(), name);
                    if (d != null) {
                        return d;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据行政区划代码搜索
     * @param code
     * @return cn.xanderye.util.AreaUtil.District
     * @author XanderYe
     * @date 2021/6/21
     */
    private static District searchByCode(List<District> children, String code) {
        if (children.size() > 0) {
            for (District district : children) {
                if (district.getValue().equals(code)) {
                    return district;
                }
                if (district.getChildren() != null && !district.getChildren().isEmpty()) {
                    District d = searchByCode(district.getChildren(), code);
                    if (d != null) {
                        return d;
                    }
                }
            }
        }
        return null;
    }

    public static List<District> getAreaData() {
        return areaData;
    }

    public static class District {

        private String key;

        private String value;

        private List<District> children;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<District> getChildren() {
            return children;
        }

        public void setChildren(List<District> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return "District{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    ", children=" + children +
                    '}';
        }
    }
}
