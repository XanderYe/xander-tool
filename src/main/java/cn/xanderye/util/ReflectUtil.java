package cn.xanderye.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author XanderYe
 * @description:
 * @date 2024/7/5 9:33
 */
public class ReflectUtil {

    /**
     * 获取静态变量值
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Object getStaticField(Class<?> clazz, String fieldName) {
        return getField(clazz, null, fieldName);
    }

    /**
     * 获取对象属性值
     * @param clazz
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getField(Class<?> clazz, Object obj, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置静态变量值
     * @param clazz
     * @param fieldName
     * @param value
     */
    public static void setStaticField(Class<?> clazz, String fieldName, Object value) {
        setField(clazz, null, fieldName, value);
    }

    /**
     * 设置对象属性值
     * @param clazz
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setField(Class<?> clazz, Object obj, String fieldName, Object value) {
       try {
           Field field = clazz.getDeclaredField(fieldName);
           field.setAccessible(true);
           field.set(obj, value);
       } catch (NoSuchFieldException | IllegalAccessException e) {
           e.printStackTrace();
       }
    }

    /**
     * 创建一个无参数对象
     * @param className
     * @return
     */
    public static Object createObject(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return createObject(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个无参数对象
     * @param clazz
     * @return
     */
    public static Object createObject(Class<?> clazz) {
        Class<?>[] paramTypes = new Class[]{};
        Object[] args = new Object[]{};
        return createObject(clazz, paramTypes, args);
    }

    /**
     * 创建一个单参数对象
     * @param className
     * @param paramType
     * @param arg
     * @return
     */
    public static Object createObject(String className, Class<?> paramType, Object arg) {
        try {
            Class<?> clazz = Class.forName(className);
            return createObject(clazz, paramType, arg);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个单参数对象
     * @param clazz
     * @param paramType
     * @param arg
     * @return
     */
    public static Object createObject(Class<?> clazz, Class<?> paramType, Object arg) {
        Class<?>[] paramTypes = new Class[]{paramType};
        Object[] args = new Object[]{arg};
        return createObject(clazz, paramTypes, args);
    }

    /**
     * 创建一个多参数对象
     * @param clazz
     * @param paramTypes
     * @param args
     * @return
     */
    public static Object createObject(Class<?> clazz, Class<?>[] paramTypes, Object[] args) {
        try {
            if (paramTypes == null) {
                paramTypes = new Class[]{};
            }
            if (args == null) {
                args = new Object[]{};
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射执行静态无参数方法
     * @param clazz
     * @param methodName
     * @return
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName) {
        Class<?>[] paramTypes = new Class[]{};
        Object[] args = new Object[]{};
        return invokeMethod(clazz, null, methodName, paramTypes, args);
    }

    /**
     * 反射执行静态单参数方法
     * @param clazz
     * @param methodName
     * @return
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?> paramType, Object arg) {
        if (null == paramType) {
            return invokeStaticMethod(clazz, methodName);
        }
        Class<?>[] paramTypes = new Class[]{paramType};
        Object[] args = new Object[]{arg};
        return invokeMethod(clazz, null, methodName, paramTypes, args);
    }

    /**
     * 反射执行静态方法
     * @param clazz
     * @param methodName
     * @param paramTypes
     * @param args
     * @return
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes, Object[] args) {
        return invokeMethod(clazz, null, methodName, paramTypes, args);
    }

    /**
     * 反射执行对象无参数方法
     * @param clazz
     * @param obj
     * @param methodName
     * @return
     */
    public static Object invokeMethod(Class<?> clazz, Object obj, String methodName) {
        Class<?>[] paramTypes = new Class[]{};
        Object[] args = new Object[]{};
        return invokeMethod(clazz, obj, methodName, paramTypes, args);
    }

    /**
     * 反射执行对象单参数方法
     * @param clazz
     * @param methodName
     * @return
     */
    public static Object invokeMethod(Class<?> clazz, Object obj, String methodName, Class<?> paramType, Object arg) {
        if (null == paramType) {
            return invokeMethod(clazz, obj, methodName);
        }
        Class<?>[] paramTypes = new Class[]{paramType};
        Object[] args = new Object[]{arg};
        return invokeMethod(clazz, obj, methodName, paramTypes, args);
    }

    /**
     * 反射执行对象方法
     * @param clazz
     * @param obj
     * @param methodName
     * @param paramTypes
     * @param args
     * @return
     */
    public static Object invokeMethod(Class<?> clazz, Object obj, String methodName, Class<?>[] paramTypes, Object[] args) {
        try {
            if (paramTypes == null) {
                paramTypes = new Class[]{};
            }
            if (args == null) {
                args = new Object[]{};
            }
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
