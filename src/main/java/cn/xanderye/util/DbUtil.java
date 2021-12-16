package cn.xanderye.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 数据库工具类
 */
@Slf4j
public class DbUtil {

    /**
     * 连接地址
     */
    private static String url;
    /**
     * 用户名
     */
    private static String username;
    /**
     * 密码
     */
    private static String password;
    /**
     * 驱动名称
     */
    private static String driver;
    /**
     * 下划线
     */
    private static final char UNDERLINE = '_';

    static {
        // 类加载时先初始化连接
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream("db.properties");
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                url = p.getProperty("db.url");
                username = p.getProperty("db.username");
                password = p.getProperty("db.password");
                driver = p.getProperty("db.driver");
            } else {
                log.warn("Could not found db.properties, please configure manually.");
            }
        } catch (IOException e) {
            log.error("Error initializing the config.");
        }
    }

    /**
     * 手动配置数据库配置
     * @param customUrl
     * @param customUsername
     * @param customPassword
     * @param customDriver
     * @return void
     * @author XanderYe
     * @date 2021/12/16
     */
    public static void setConfig(String customUrl, String customUsername, String customPassword, String customDriver) {
        url = customUrl;
        username = customUsername;
        password = customPassword;
        driver = customDriver;
    }

    /**
     * 初始化连接池
     * @param
     * @return java.sql.Connection
     * @author XanderYe
     * @date 2021/12/16
     */
    public static Connection getConn() throws SQLException {
        if (StringUtils.isAnyEmpty(url, username, password, driver)) {
            throw new RuntimeException("Please check the database config.");
        }
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (conn == null) {
            throw new RuntimeException("Error connecting to the database.");
        }
        return conn;
    }

    /**
     * 更新操作，包括（insert,update,delete）
     *
     * @param sql
     * @param args
     * @return boolean
     * @author XanderYe
     * @date 2020/9/1
     */
    public static int update(String sql, Object... args) throws SQLException {
        int affect;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            affect = ps.executeUpdate();
        } finally {
            DbUtil.close(null, ps, conn);
        }
        return affect;
    }

    /**
     * 根据条件查询map
     * @param sql
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author XanderYe
     * @date 2020/9/1
     */
    public static Map<String, Object> queryOne(String sql, Object... args) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return getMapFromResultSet(rs);
            }
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, conn);
        }
        return null;
    }

    /**
     * 根据条件查询对象
     * @param clazz
     * @param sql
     * @param args
     * @return T
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> T queryOne(Class<T> clazz, String sql, Object... args) throws SQLException {
        Map<String, Object> map = queryOne(sql, args);
        if (map != null) {
            try {
                return mapToObject(clazz, map);
            } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询列表
     * @param sql
     * @param objs
     * @return java.util.List<T>
     * @author XanderYe
     * @date 2020/9/1
     */
    public static List<Map<String, Object>> queryList(String sql, Object... objs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ps.setObject(i + 1, objs[i]);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = getMapFromResultSet(rs);
                if (map != null) {
                    list.add(map);
                }
            }
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, conn);
        }
        return list;
    }

    /**
     * 查询列表
     * @param clazz
     * @param sql
     * @param objs
     * @return java.util.List<T>
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> List<T> queryList(Class<T> clazz, String sql, Object... objs) throws SQLException {
        List<T> list = new ArrayList<>();
        List<Map<String, Object>> mapList = queryList(sql, objs);
        for (Map<String, Object> map : mapList) {
            try {
                T obj = mapToObject(clazz, map);
                if (obj != null) {
                    list.add(obj);
                }
            } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * resultSet转map
     *
     * @param resultSet
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author XanderYe
     * @date 2021/2/1
     */
    private static Map<String, Object> getMapFromResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        if (columnCount > 0) {
            Map<String, Object> map = new HashMap<>(16);
            for (int i = 0; i < columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);
                map.put(columnName, resultSet.getObject(columnName));
            }
            return map;
        }
        return null;
    }

    /**
     * map转对象
     * @param clazz
     * @param map
     * @return T
     * @author XanderYe
     * @date 2021/2/1
     */
    private static <T> T mapToObject(Class<T> clazz, Map<String, Object> map) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        T object = null;
        if (!map.isEmpty()) {
            Set<String> columnNames = map.keySet();
            object = clazz.newInstance();
            for (String column : columnNames) {
                Object value = map.get(column);
                String camel = underlineToCamel(column);
                if (camel != null) {
                    try {
                        Field f = clazz.getDeclaredField(camel);
                        f.setAccessible(true);
                        f.set(object, value);
                    } catch (NoSuchFieldException ignored){}
                }
            }
        }
        return object;
    }

    /**
     * 关闭资源
     *
     * @param rs
     * @param ps
     * @param conn
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 驼峰转下划线
     *
     * @param param
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/9/1
     */
    public static String camelToUnderline(String param) {
        if (param == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int len = param.length();
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param param
     * @return java.lang.String
     * @author XanderYe
     * @date 2020/9/1
     */
    public static String underlineToCamel(String param) {
        if (param == null) {
            return null;
        }
        param = param.toLowerCase();
        StringBuilder sb = new StringBuilder();
        int len = param.length();
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
