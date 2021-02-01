package cn.xanderye.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 数据库工具类
 */
public class DbUtil {

    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;

    private static final char UNDERLINE = '_';

    // 类加载时先初始化连接
    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream("db.properties");
            Properties p = new Properties();
            p.load(is);
            url = p.getProperty("db.url");
            username = p.getProperty("db.username");
            password = p.getProperty("db.password");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化资源池
     *
     * @param
     * @return void
     */
    public static Connection getConn() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            throw new RuntimeException("Database connect failed.");
        }
        return connection;
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

    // 根据id查询
    public static <T> T queryById(Class<T> clazz, String sql, int id) throws SQLException {
        Connection conn = null;
        T obj = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> map = getMapFromResultSet(rs);
                obj = mapToObject(clazz, map);
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, conn);
        }
        return obj;
    }

    /**
     * 查询一条
     *
     * @param clazz
     * @param sql
     * @param args
     * @return T
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> T queryOne(Class<T> clazz, String sql, Object... args) throws SQLException {
        Connection conn = null;
        T obj = null;
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
                Map<String, Object> map = getMapFromResultSet(rs);
                obj = mapToObject(clazz, map);
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, conn);
        }
        return obj;
    }

    /**
     * 查询全部
     *
     * @param clazz
     * @param sql
     * @param objs
     * @return java.util.List<T>
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> List<T> queryAll(Class<T> clazz, String sql, Object... objs) throws SQLException {
        List<T> list = new ArrayList<>();
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
                T obj = mapToObject(clazz, map);
                if (obj != null) {
                    list.add(obj);
                }
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, conn);
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
        Map<String, Object> map = new HashMap<>(16);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String columnName = resultSetMetaData.getColumnName(i + 1);
            map.put(columnName, resultSet.getObject(columnName));
        }
        return map;
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
                    Field f = clazz.getDeclaredField(camel);
                    f.setAccessible(true);
                    f.set(object, value);
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
