package com.example.myapp.base.net.constant;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.constant.Env;

public class NetConstant {

    public static final class NetCode {
        public static final int CODE_200 = 200;
        public static final int CODE_299 = 299;
    }

    public static final class H5 {
        public static final String SCHEME_PALMHOUSE = "1";
        public static final String HOST_PALMHOUSE = "com.example.myapp";
        public static final String SCHEME_HTTP = "http";
        public static final String SCHEME_HTTPS = "https";
        public static final String HOST_myapp_PALMHOUSE = "com.example.myapp";
        public static final String QUERY_ACTION_TYPE = "actiontype";
        public static final String QUERY_ID = "id";
        public static final String QUERY_SHARE = "share";
        public static final String QUERY_PREVIEW_URL = "url=";
        public static final String QUERY_URL = "url";
        public static final String QUERY_NAME = "name";
    }

    public static final class H5Router {
        //////////////////////////////////////////////// h5系统页面start ///////////////////////////////////////////////////////////
        // h5-汇川官网
        public static final String OFFICIAL_WEBSITE = "22";
        // h5-服务协议
        public static final String SERVICE = "h5/system/docs/agreement";
        // h5-隐私政策
        public static final String PRIVACY = "h5/system/docs/policy";
        // h5-文件预览
        public static final String ONLINE_PREVIEW = "onlinePreview?";
        public static final String ONLINE_PREVIEW2 = "h5/system/read/";//https://22-test.myapp.com/h5/system/read/:fileUrl
        public static final String ONLINE_PREVIEW3 = "h5/system/readByBase58/";//https://22-test.myapp.com/h5/system/readByBase58/:fileUrl
        // h5-版本介绍
        public static final String VERSION_INTRO = "h5/system/version/list";
        //////////////////////////////////////////////// h5系统页面end ///////////////////////////////////////////////////////////
//////////////////////////////////////////////// h5业务页面start ///////////////////////////////////////////////////////////
        // h5-系列详情
        public static final String SERIES_DETAIL = "h5/share/seriesDetail/";
        // h5-产品详情（机型详情）
        public static final String PRODUCT_DETAIL = "h5/share/modelsDetail/";
        // h5-参数对比
        public static final String PARAMS_CONTRAST = "h5/share/paramsContrast/";
        // h5-选型清单页
        public static final String SELECTION_LIST = "h5/share/selectionList/";
        // h5-装备库
        public static final String EQUIPMENT = "h5/share/equipment/";
        //////////////////////////////////////////////// h5业务页面end ///////////////////////////////////////////////////////////
//////////////////////////////////////////////// h5其他页面start ///////////////////////////////////////////////////////////
        // h5-下载页面
        public static final String DOWNLOAD = "download";
//////////////////////////////////////////////// h5其他页面end ///////////////////////////////////////////////////////////
    }

    public static final class AppRouter {
        // app-主页
        public static final String HOME = "0";
        // app-分享id-系列详情
        public static final String SHARE_SERIES_DETAIL = "3";
        // app-分享id-产品详情
        public static final String SHARE_PRODUCT_DETAIL = "4";
        // app-分享id-参数对比
        public static final String SHARE_PARAMS_CONTRAST = "6";
        // app-分享id-选型清单页
        public static final String SHARE_SELECTION_LIST = "5";
        // app-分享id-装备库
        public static final String SHARE_EQUIPMENT = "7";
        // app-网页模块
        public static final String WEB_VIEW = "8";
        // app-视频模块
        public static final String PREVIEW_VIDEO = "9";
        // app-系列详情
        public static final String SERIES_DETAIL = "10";
        // app-产品详情
        public static final String PRODUCT_DETAIL = "11";
    }

    public static final class Config {
        /**
         * http://10.44.219.175/api/
         * 22-test.myapp.com    【待解析】  120.77.154.230      内/外网解析，部署在阿里云
         * 22.myapp.com          【待解析】  IP 待分配    部署在阿里云       内/外网解析
         * 22m.myapp.com         【待解析  IP 待分配     部署在阿里云     内/外网解析
         */
//        public static String URL_PALMHOUSE = "";//http://10.44.219.98:3000/mock/39/  http://10.44.219.175/api/
        public static String URL_JA_PALMHOUSE = "http://22-dev.myapp.com/"; //"http://10.45.161.13:8888/";
        public static String URL_PREVIEW_MEANS = "https://gray-idesign.myapp.com/kkfileview8012/";
        public static String URL_H5 = "http://22-dev.myapp.com/";

        public static void initPalmHouseBaseUrl() {
            if (BaseConstant.Environment.environ == Env.SIT) {
                URL_JA_PALMHOUSE = "http://22-dev.myapp.com/";
                URL_PREVIEW_MEANS = "https://gray-idesign.myapp.com/kkfileview8012/";
                URL_H5 = "http://22-dev.myapp.com/";
                return;
            }
            if (BaseConstant.Environment.environ == Env.UAT) {
                URL_JA_PALMHOUSE = "https://22-test.myapp.com/";
                URL_PREVIEW_MEANS = "https://22-test.myapp.com/kkfile/";
                URL_H5 = "https://22-test.myapp.com/";
                return;
            }
            if (BaseConstant.Environment.environ == Env.PRO) {
                URL_JA_PALMHOUSE = "https://22.myapp.com/";
                URL_PREVIEW_MEANS = "https://22.myapp.com/kkfile/";
                URL_H5 = "https://22.myapp.com/";
                return;
            }
        }
    }
}
