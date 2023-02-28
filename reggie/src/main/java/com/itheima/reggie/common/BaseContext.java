package com.itheima.reggie.common;

public class BaseContext {

    /**
     * 基于threadlocal封装工具类，用户保存和获取当前登录用户id
     */
    private static ThreadLocal<Long> local=new ThreadLocal<Long>();

    public static void setCurrentId(Long id){
        local.set(id);
    }

    public static Long getCurrentId(){
        return local.get();
    }
}
