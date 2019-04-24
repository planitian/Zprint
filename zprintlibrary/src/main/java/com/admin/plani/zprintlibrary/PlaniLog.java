package com.admin.plani.zprintlibrary;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class PlaniLog {
    private static ExecutorService executorService;//线程池
    private static ConcurrentLinkedQueue<String> concurrentLinkedQueue;//并发 无界 队列
    private static ReentrantLock reentrantLock = new ReentrantLock();//可重入锁
    private static Condition empty = reentrantLock.newCondition();
    static String path;//文件位置
    static int SIZE = 1;//以兆为单位
    private static StringBuilder stringBuilder = new StringBuilder();
    private static volatile Boolean isRun = true;//标志 当前 线程池里面的线程 是否结束
    static int BUFFE_SIZE = 5;//缓存 默认5

    static {
        executorService = Executors.newSingleThreadExecutor();
        concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    static void pre() {
        if (path == null) {
            throw new IllegalStateException("请先设置好 文件路径");
        }
        executorService.execute(() -> {
            int counts = 0;
            try {
                while (isRun) {
                    String temp = concurrentLinkedQueue.poll();
                    if (temp == null) {
                        if (reentrantLock.tryLock()) {
                            empty.await();
                            reentrantLock.unlock();
                            continue;
                        } else {
//                            System.out.println("没有获取到锁");
                        }
                    } else if (temp.length() != 0) {
                        stringBuilder.append(temp).append(System.lineSeparator());
                        counts++;
                    }

                    if (counts > BUFFE_SIZE) {
//                        System.out.println(counts + "   " + stringBuilder.toString());
                        write(path, stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        stringBuilder.trimToSize();
                        counts = 0;
                    }
                }
//                System.out.println("销毁前  写入缓存 》》》》》》》》");
                if (stringBuilder.length() > 0) {
                    write(path, stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.trimToSize();
                    stringBuilder = null;//help GC
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 写入内容
     * @param content
     */
    static void writeLog(String content) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
        String time = simpleDateFormat.format(new Date()) + "    " + content;
        concurrentLinkedQueue.add(time);
        boolean lock;
        if (lock = reentrantLock.tryLock()) {
            empty.signal();
            reentrantLock.unlock();
        }
    }

    /**
     * 创建文件，整个文件夹及文件都创建
     * @param path   文件路径
     * @param isFile  true 是文件 ，false 是目录
     * @return  文件
     * @throws IOException
     */
    private static File createFile(String path, boolean isFile) throws IOException {
        File file = new File(path);
        if (!file.exists()) {//不存在
            String parent = file.getParent();//得到父母，如果返回空 ，那么就到顶了
            if (parent != null) {
                createFile(parent, false);//因为是父母，所以肯定不是file；
            }
            if (isFile) {
                file.createNewFile();
            } else {
                file.mkdir();
            }
        } else if (file.isFile() != isFile) {//如果 有相同的文件夹或文件 和path重名，但类型不一样
            //先删除
            file.delete();
            createFile(path, isFile);//再重新进入
        }
        return file;
    }

    private static void write(String path, String content) throws IOException {
        check(path);
        File file = createFile(path, true);
        FileWriter fileWriter = new FileWriter(file, true);//追加模式写入
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * 检查文件是否大于约定的大小
     *
     * @param path
     */
    private static void check(String path) {
        File file = new File(path);
        if (file.exists() && (file.length() / (1024 * 1024)) > SIZE) {
            file.delete();
        }
    }

    /**
     * 销毁资源
     */
    static void destroy() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

}
