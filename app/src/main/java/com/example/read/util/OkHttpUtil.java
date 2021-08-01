package com.example.read.util;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
/*
网络访问框架一般都需要单例模式（singleton），首先我们也进行单利模式。
1 首先私有化构造器，让别人不能new出其它实例。
2 声明该类的一个静态成员变量实例，本篇为instance
3 声明一个公有的方法getInstance提供给调用者本类实例。
 */
public class OkHttpUtil {
    // 必须要用的Okhttpclient实例,在构造器中实例化保证单一实例
    private static OkHttpClient okHttpClient;
    //防止多个线程同时访问
    //网络访问要求singleton
    private volatile static OkHttpUtil instance;
    //把构造器定义为私有的，只有OkHttpUtil类内可以调用构造器
    private OkHttpUtil(){
        okHttpClient = new OkHttpClient();
        //请求超时设置
        okHttpClient.newBuilder()
                    //.connectTimeout(10, TimeUnit.SECONDS)
                    //.readTimeout(10, TimeUnit.SECONDS)
                    //.writeTimeout(10, TimeUnit.SECONDS)
                    .build();
    }
    /**
     * 懒汉式双重检查加锁单例模式
     */
    public static OkHttpUtil getInstance(){
        if(instance == null){
            synchronized (OkHttpUtil.class){
                if(instance == null) {
                    instance = new OkHttpUtil();
                }
            }
        }
        return instance;
    }
    /**
     * get异步请求不传参数
     * 通过response.body().string()获取返回的字符串
     * 异步返回值不能更新UI，要开启新线程
     */
    Call call;
    public void Get(String url, Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //.get()默认就是GET请求，可以不写
        call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public void Getwithparms(String url,String tag, Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        //.get()默认就是GET请求，可以不写
        call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public void Canale(Object tag){
        if (tag == null) return;
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}
