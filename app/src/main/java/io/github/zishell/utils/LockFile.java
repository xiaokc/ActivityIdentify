package io.github.zishell.utils;

/**
 * Created by zishell on 2015/5/14.
 * reference from http://blog.csdn.net/sunnybuer/article/details/8263056
 */

import java.io.File;
import java.net.URI;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockFile extends File {

    private static Hashtable<String, ReentrantReadWriteLock> locks = new Hashtable<String, ReentrantReadWriteLock>();
    private ReentrantReadWriteLock lock = null;

    public LockFile(File arg0, String arg1) {
        super(arg0, arg1);
        lock = initLock(this.getAbsolutePath());
    }

    public LockFile(String arg0, String arg1) {
        super(arg0, arg1);
        lock = initLock(this.getAbsolutePath());
    }

    public LockFile(String arg0) {
        super(arg0);
        lock = initLock(this.getAbsolutePath());
    }

    public LockFile(URI arg0) {
        super(arg0);
        lock = initLock(this.getAbsolutePath());
    }

    /*
     * 这里要注意使用 static synchronized，不然可能同时有多个进行初始化这个文件
     */
    private static synchronized ReentrantReadWriteLock initLock(String path) {
        ReentrantReadWriteLock lock = locks.get(path);
        if (lock == null) {
            lock = new ReentrantReadWriteLock();
            locks.put(path, lock);
        }
        return lock;
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }
}