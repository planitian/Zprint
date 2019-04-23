package com.admin.plani.zprintlibrary;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建时间 2018/8/22
 *
 * @author plani
 */
public class Zprint {
    /*是否输出日志  默认输出*/
    public static boolean OUT = true;
    /*默认debug*/
    public static Level level = Level.DEBUG;
    /*是否将日志输入到文件，默认不输出*/
    private static boolean isWrite;

    private static String pre;
    private static String end;

    static {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            stringBuilder.append("-");
        }
        pre = end = stringBuilder.toString();
        end = "代码操作结束" + end;
    }

    /**
     * @param path 文件路径
     * @return
     */
    public static boolean startWrite(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("文件存在");
                if (!file.canWrite()) {
                    Log.e(Thread.currentThread().getName(), "开启写入log异常");
                    throw new IllegalStateException("文件存在，但是不可写入");
                }
            }
            System.out.println("配置开始 ");
            isWrite = true;
            PlaniLog.path = path;
            PlaniLog.pre();
        } catch (Exception e) {
            System.out.println("异常抛出   ");
            Log.e(Thread.currentThread().getName(), "开启写入log异常", e);
        }
        System.out.println("最后返回 ");
        return false;
    }

    /**
     * @param key 要输出数据的标识
     * @param out 动态参数，这里是要输出的数据
     */
    public static void log(@Nullable String key, Object... out) {
        if (!OUT) {
            return;
        }
        //得到信息
        String[] info = obtainInfo();
        String className = info[0];
        String methodName = info[1];
        String lines = info[2];

        //生成指向java的字符串 加入到TAG标签里面
        String TAG = "类class" + "(" + className + ".java:" + lines + ")";

        //生成用户想要输出的数据
        StringBuilder temp = new StringBuilder();
        for (Object anOut : out) {
            temp.append(" ").append(anOut).append(",");
        }
        //删除输出数据 最后的 ,  号
        if (out.length != 0) {
            temp.deleteCharAt(temp.length() - 1);
        }
        String parameter;
        if (key == null || key.trim().isEmpty()) {
            parameter = "方法method ：" + methodName + "  输出： " + temp;
        } else {
            if (out.length == 0) {
                parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"";
            } else {
                parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" + " ::" + temp;
            }
        }
        print(TAG, parameter);
        level = Level.DEBUG;
    }


    /**
     * note  这个方法弃用，这个第一个参数 总是会变为key
     * 和上面方法功能相同，少了 key
     *
     * @param out 动态参数，这里是要输出的数据
     */

    private static void log(Object... out) {
        log(null, out);
    }

    /**
     * 有key   一个参数  操作
     *
     * @param key       要输出数据的标识
     * @param objects   值
     * @param operation 传回值  可以操作   注意 值类型
     */
    public static <T> void log(@Nullable String key, T objects, Operation<T> operation) {
        if (!OUT) {
            return;
        }
        //得到信息
        String[] info = obtainInfo();
        String className = info[0];
        String methodName = info[1];
        String lines = info[2];

        //生成指向java的字符串 加入到TAG标签里面
        String TAG = "类class" + "(" + className + ".java:" + lines + ")";
        //上面标识
        String parameter;
        if (key == null || key.trim().isEmpty()) {
            parameter = "方法method ：" + methodName + "  输出代码操作" + pre;
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" + " ::  " + "输出代码操作" + pre;
        }
        print(TAG, parameter);
        //内容
        operation.operation(objects);

        print(TAG, end);

        level = Level.DEBUG;//变回debug
    }

    /**
     * 没有key的 一个参数 操作
     *
     * @param objects   值
     * @param operation 传回值  可以操作   注意 值类型
     * @param <T>       第一个参数泛型
     */
    public static <T> void log(T objects, Operation<T> operation) {
        log(null, objects, operation);
    }

    /**
     * 有key    两个参数 操作
     *
     * @param key          要输出数据的标识
     * @param objects      值
     * @param operation    传回值  可以操作   注意 值类型
     * @param objectsTwo
     * @param operationTwo
     */
    public static <T, J> void log(@Nullable String key, T objects, Operation<T> operation, J objectsTwo, Operation<J> operationTwo) {
        if (!OUT) {
            return;
        }
        //得到信息
        String[] info = obtainInfo();
        String className = info[0];
        String methodName = info[1];
        String lines = info[2];
        //生成指向java的字符串 加入到TAG标签里面
        String TAG = "类class" + "(" + className + ".java:" + lines + ")";
        //上面标识
        String parameter;
        if (key == null || key.trim().isEmpty()) {
            parameter = "方法method ：" + methodName + "  输出代码操作" + pre;
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" + " ::  " + "输出代码操作" + pre;
        }
        print(TAG, parameter);
        //内容
        operation.operation(objects);
        operationTwo.operation(objectsTwo);

        print(TAG, end);
        level = Level.DEBUG;
    }

    /**
     * 无key    两个参数 操作
     *
     * @param objects      值
     * @param operation    传回值  可以操作   注意 值类型
     * @param objectsTwo
     * @param operationTwo
     */
    public static <T, J> void log(T objects, Operation<T> operation, J objectsTwo, Operation<J> operationTwo) {
        log(null, objects, operation, objectsTwo, operationTwo);
    }


    /**
     * 有key  对参数数组 进行操作
     *
     * @param key        要输出数据的标识
     * @param objects    值
     * @param operations 操作
     */
    public static <T> void log(@Nullable String key, T[] objects, Operation<T>... operations) {
        if (!OUT) {
            return;
        }
        //得到信息
        String[] info = obtainInfo();
        String className = info[0];
        String methodName = info[1];
        String lines = info[2];

        //生成指向java的字符串 加入到TAG标签里面
        String TAG = "类class" + "(" + className + ".java:" + lines + ")";
        //上面标识
        String parameter;
        if (key == null || key.trim().isEmpty()) {
            parameter = "方法method ：" + methodName + "  输出代码操作" + pre;
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" + " ::  " + "输出代码操作" + pre;
        }
        print(TAG, parameter);
        //内容
        if (objects != null && operations != null) {
            for (int i = 0, j = 0; i < objects.length && j < operations.length; i++) {
                operations[i].operation(objects[i]);
            }
        }

        print(TAG, end);
        level = Level.DEBUG;
    }

    /**
     * 无key  对参数数组 进行操作
     *
     * @param objects    值
     * @param operations
     */
    public static <T> void log(T[] objects, Operation<T>... operations) {
        log(null, objects, operations);
    }

    //内部接口  之所以不用Consumer  是因为 这个对android api有限制
    @FunctionalInterface
    public interface Operation<T> {
        void operation(T t);
    }

    /**
     * 废弃
     *
     * @param content 内容
     * @return 返回字符串中的中文个数 没有返回0
     */
    private static int chineseCounts(String content) {
        int count = 0;
        if (content == null) {
            return count;
        }
        String reg = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(content);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     * @return 返回类名 方法 行数信息
     */
    private static String[] obtainInfo() {
        String[] result = new String[3];
        //方法
        String methodName;
        //调用类名
        String className;
        //行数
        int lines = -1;
        int last = 0;
        //得到堆栈 里面存储着 类名，方法 行号 最上面的是最近的方法
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        for (int i = 0; i < s.length; i++) {
            if (s[i].getClassName().contains("Zprint")) {
                last = i;
            }
        }
//        System.out.println("last "+last);
//        for (StackTraceElement value : s) {
//            Log.e("class ", value.getClassName() + "   " + value.getMethodName() + "  " + value.getLineNumber());
//        }
        //加一 因为寻找调用最近调用 Zprint的一个方法 在它下面
        last++;
        //会有lambda表达式 会让方法属性 在下一行
        if (s[last].getMethodName().contains("lambda")) {
            methodName = s[last + 1].getMethodName();
        } else {
            methodName = s[last].getMethodName();
        }
        //类名
        className = s[last].getClassName();
        lines = s[last].getLineNumber();
        //解析类名 因为内部类 会含有$
        if (className == null || className.isEmpty()) {
            return null;
        } else if (className.contains("$")) { //用于内部类的名字解析
            className = className.substring(className.lastIndexOf(".") + 1, className.indexOf("$"));
        } else {
            className = className.substring(className.lastIndexOf(".") + 1, className.length());
        }
        result[0] = className;
        result[1] = methodName;
        result[2] = String.valueOf(lines);
        return result;
    }

    public enum Level {
        WARN,//默认蓝色
        DEBUG,//默认 黑色
        ERROR,//默认黑色
    }

    private static void print(String TAG, String content) {
        switch (level) {
            case DEBUG:
                Log.d(TAG, content.toString());
                break;
            case WARN:
                Log.w(TAG, content.toString());
                break;
            case ERROR:
                Log.e(TAG, content.toString());
                break;
        }
        if (isWrite)
            PlaniLog.writeLog(content);
    }
}
