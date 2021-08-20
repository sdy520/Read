package com.example.read.util;

/**
 * Created by zhao on 2016/10/20.
 */

public class URLCONST {
    //小说网站名称
    public static String tianlai = "天籁小说";
    public static String bxwx = "笔下文学";
    public static String duoben = "多本小说";
    // 命名空间
    //public static String nameSpace_tianlai = "https://m.23sk.com";
    public static String nameSpace_tianlai = "https://www.tianlaixsw.com";
    public static String nameSpace_bxwx = "https://www.bxwxorg.com";
    public static String nameSpace_duoben = "https://www.duoben.net";


    public static String nameSpace_system = "https://10.10.123.31:8080/jeecg";

    public static boolean isRSA = false;

    // 搜索小说
    //public static String method_tl_search = "https://m.23sk.com/search.php?q=";
    public static String method_tl_search = "https://www.tianlaixsw.com/search.html,searchkey={key}&searchtype=all";
    public static String method_db_search = "https://www.duoben.net/s123.php?ie=gbk&q=";

    // 获取最新版本号
    public static String method_getCurAppVersion = nameSpace_system + "/mReaderController.do?getCurAppVersion";

    // app下载
    public static String method_downloadApp = nameSpace_system + "/upload/app/MissZzzReader.apk";

}

