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
     * @param
     * @return void
     */
    public static Connection getConn() throws SQLException{
        try {
            if(connection == null || connection.isClosed()){
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新操作，包括（insert,update,delete）
     * @param sql
     * @param args
     * @return boolean
     * @author XanderYe
     * @date 2020/9/1
     */
    public static int update(String sql, Object... args) throws SQLException {
        int affect = 0;
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
            DbUtil.close(null, ps, null);
        }
        return affect;
    }

    // 根据id查询
    public static <T> T queryById(Class<T> t, String sql, int id) throws SQLException {
        Connection conn = null;
        T obj = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                Map<String,Object> map = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    map.put(columnName, value);
                }

                if(!map.isEmpty()){
                    Set<String> columnNames = map.keySet();
                    obj = t.newInstance();
                    for(String column : columnNames){
                        Object value = map.get(column);
                        Field f  = t.getDeclaredField(CamelUnderlineUtil.underlineToCamel(column));
                        f.setAccessible(true);
                        f.set(obj, value);
                    }
                }
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally{
            close(rs, ps, conn);
        }
        return obj;
    }

    /**
     * 查询一条
     * @param t
     * @param sql
     * @param args
     * @return T
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> T queryOne(Class<T> t, String sql, Object... args) throws SQLException {
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
            ResultSetMetaData rsmd = rs.getMetaData();
            Map<String,Object> map = new HashMap<>();

            while(rs.next()){
                map.clear();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    map.put(columnName, rs.getObject(columnName));
                }
                if(!map.isEmpty()){
                    Set<String> columnNames = map.keySet();
                    obj = t.newInstance();
                    for(String column : columnNames){
                        Object value = map.get(column);
                        Field f  = t.getDeclaredField(CamelUnderlineUtil.underlineToCamel(column));
                        f.setAccessible(true);
                        f.set(obj, value);
                    }
                }
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally{
            close(rs, ps, conn);
        }
        return obj;
    }

    /**
     * 查询全部
     * @param t
     * @param sql
     * @param objs
     * @return java.util.List<T>
     * @author XanderYe
     * @date 2020/9/1
     */
    public static <T> List<T> queryAll(Class<T> t, String sql, Object... objs) throws SQLException {
        List<T> list = new ArrayList<>();
        Connection conn = null;
        T obj = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ps.setObject(i + 1, objs[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Map<String,Object> map = new HashMap<>();

            while(rs.next()){
                map.clear();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    map.put(columnName, rs.getObject(columnName));
                }
                if(!map.isEmpty()){
                    Set<String> columnNames = map.keySet();
                    obj = t.newInstance();
                    for(String column : columnNames){
                        Object value = map.get(column);
                        Field f  = t.getDeclaredField(CamelUnderlineUtil.underlineToCamel(column));
                        f.setAccessible(true);
                        f.set(obj, value);
                    }
                    list.add(obj);
                }
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        } finally{
            close(rs, ps, conn);
        }
        return list;
    }

    /**
     * 关闭资源
     * @param rs
     * @param ps
     * @param conn
     * @return void
     * @author XanderYe
     * @date 2020/9/1
     */
    public static void close(ResultSet rs, PreparedStatement ps, Connection conn){
        try {
            if(rs != null){
                rs.close();
            }
            if(ps != null){
                ps.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
