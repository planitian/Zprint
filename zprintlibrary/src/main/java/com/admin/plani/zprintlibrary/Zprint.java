package com.admin.plani.zprintlibrary;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建时间 2018/8/22
 *
 * @author plani
 */
public class Zprint {
    public static boolean OUT = true;

    public static Level level = Level.DEBUG;

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
            Log.d(TAG, parameter);
        } else {
            if (out.length == 0) {
                parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"";
            } else {
                parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" + " ::" + temp;
            }
            Log.d(TAG, parameter);
        }
    }


    /**
     * 和上面方法功能相同，少了 key
     *
     * @param out 动态参数，这里是要输出的数据
     */
    public static void log(Object... out) {
      log(null,out);
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
            parameter = "方法method ：" + methodName + "  输出代码操作-------------------【";
            Log.d(TAG, parameter);
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" +" ::  "+ "输出代码操作-------------------【";
            Log.d(TAG, parameter);
        }
        //内容
        operation.operation(objects);
        //生成结束标志
        String blank = "-";
        StringBuilder end = new StringBuilder("输出代码操作-------------------】");
        int gap = parameter.length() - end.length();//获取 上面的开始标志 和下面结束 相差几个长度
        int paraChinese = chineseCounts(parameter);//得到上面的 汉字个数
        int endChinese = chineseCounts(end.toString());//得到下面的汉字个数
        //因为 汉字 相当于两个英文字母
        gap += (paraChinese - endChinese);
        //有时候上面标志 英文字数 是奇数 会导致上面 比下面长一块
        if ((parameter.length()-paraChinese)%2!=0){
            gap++;//补齐下面长度
        }
        for (int i = 0; i < gap; i++)
            end.insert(0, blank);

        Log.d(TAG, end.toString());
    }

    /**
     * 没有key的 一个参数 操作
     *
     * @param objects   值
     * @param operation 传回值  可以操作   注意 值类型
     * @param <T> 第一个参数泛型
     */
    public static <T> void log(T objects, Operation<T> operation) {
      log(null,objects,operation);
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
            parameter = "方法method ：" + methodName + "  输出代码操作-------------------【";
            Log.d(TAG, parameter);
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" +" ::  "+ "输出代码操作-------------------【";
            Log.d(TAG, parameter);
        }
        //内容
        operation.operation(objects);
        operationTwo.operation(objectsTwo);

        //生成结束标志
        String blank = "-";
        StringBuilder end = new StringBuilder("输出代码操作-------------------】");
        int gap = parameter.length() - end.length();//获取 上面的开始标志 和下面结束 相差几个长度
        int paraChinese = chineseCounts(parameter);//得到上面的 汉字个数
        int endChinese = chineseCounts(end.toString());//得到下面的汉字个数
        //因为 汉字 相当于两个英文字母
        gap += (paraChinese - endChinese);

        //有时候上面标志 英文字数 是奇数 会导致上面 比下面长一块
        if ((parameter.length()-paraChinese)%2!=0){
            gap++;//补齐下面长度
        }
        for (int i = 0; i < gap; i++)
            end.insert(0, blank);

        Log.d(TAG, end.toString());
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
      log(null,objects,operation,objectsTwo,operationTwo);
    }


    /**
     * 有key  对参数数组 进行操作
     *
     * @param key        要输出数据的标识
     * @param objects    值
     * @param operations  操作
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
            parameter = "方法method ：" + methodName + "  输出代码操作-------------------【";
            Log.d(TAG, parameter);
        } else {
            parameter = "方法method ：" + methodName + "  输出： " + "\"" + key + "\"" +" ::  "+ "输出代码操作-------------------【";
            Log.d(TAG, parameter);
        }
        //内容
        if (objects != null && operations != null) {
            for (int i = 0, j = 0; i < objects.length && j < operations.length; i++) {
                operations[i].operation(objects[i]);
            }
        }
        //生成结束标志
        String blank = "-";
        StringBuilder end = new StringBuilder("输出代码操作-------------------】");
        int gap = parameter.length() - end.length();//获取 上面的开始标志 和下面结束 相差几个长度
        int paraChinese = chineseCounts(parameter);//得到上面的 汉字个数
        int endChinese = chineseCounts(end.toString());//得到下面的汉字个数
        //因为 汉字 相当于两个英文字母
        gap += (paraChinese - endChinese);

        //有时候上面标志 英文字数 是奇数 会导致上面 比下面长一块
        if ((parameter.length()-paraChinese)%2!=0){
            gap++;//补齐下面长度
        }
        for (int i = 0; i < gap; i++)
            end.insert(0, blank);

        Log.d(TAG, end.toString());
    }

    /**
     * 无key  对参数数组 进行操作
     *
     * @param objects    值
     * @param operations
     */
    public static <T> void log(T[] objects, Operation<T>... operations) {
      log(null,objects,operations);
    }

    //内部接口  之所以不用Consumer  是因为 这个对android api有限制
    @FunctionalInterface
    public interface Operation<T> {
        void operation(T t);
    }

    /**
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
     *
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
        for (int i = 0; i <s.length ; i++) {
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
            methodName = s[last+1].getMethodName();
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

    public  enum Level{
        WARN,//默认蓝色
        DEBUG,//默认 黑色
        ERROR,//默认黑色
    }
    private static void log(String TAG,String content){
        switch (level){
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

    }
}
