//package com.example.myapp.base.widget.utils;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.example.myapp.base.bridge.constant.ARouterConstant;
//import com.example.myapp.base.bridge.utils.BridgeUtil;
//import com.example.myapp.base.bridge.utils.CommonJumpUtil;
//import com.example.myapp.base.bridge.utils.DetailJumpUtil;
//import com.example.myapp.base.bridge.utils.MainJumpUtil;
//import com.example.myapp.base.constant.BaseConstant;
//import com.example.myapp.base.utils.ActivityUtils;
//import com.example.myapp.base.utils.ClickUtils;
//import com.example.myapp.base.utils.IntentUtils;
//import com.example.myapp.base.utils.LogUtils;
//import com.example.myapp.base.utils.Utils;
//import com.example.myapp.base.widget.R;
//import com.example.myapp.base.widget.dialog.SimpleTitleDialog;
//import com.example.myapp.base.widget.helper.DialogHelper;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//
///**
// * 外部跳转进来工具类
// * 用于分享到微信的链接，由h5拉起app首页，再首页进其他页面
// * 用于首页动态配置的h5路径跳转app其他页面
// */
//public class H5JumpUtil {
//    private static final String TAG = "ShareJumpUtil";
//    public static final String KEY_SHARE_ID = "shareId";
//
//    public static Uri getUri(String uriStr) {
//        LogUtils.d(TAG, "getUri uriStr:" + uriStr);
//        Uri uri = null;
//        try {
//            uri = Uri.parse(uriStr);
//        } catch (Throwable throwable) {
//            LogUtils.e("getUri Throwable" + throwable);
//        }
//        return uri;
//    }
//
//    public static boolean isJump(String uriStr) {
//        Uri uri = getUri(uriStr);
//        return isJump(uri);
//    }
//
//    public static boolean isJump(Uri uri) {
//        if (!isJumpWithHome(uri)) {
//            return false;
//        }
//        String actionType = uri.getQueryParameter(BaseConstant.H5.QUERY_ACTION_TYPE);
//        if (TextUtils.equals(actionType, BaseConstant.AppRouter.HOME)) {
//            LogUtils.e(TAG, "isJump TextUtils.equals(actionType, BaseConstant.AppRouter.HOME)");
//            return false;
//        }
//        LogUtils.e(TAG, "isJump true");
//        return true;
//    }
//
//    public static boolean isJumpWithHome(Uri uri) {
//        if (uri == null) {
//            LogUtils.e(TAG, "isJumpWithHome uri == null");
//            return false;
//        }
//        try {
//            String scheme = uri.getScheme();
//            if (!TextUtils.equals(scheme, BaseConstant.H5.SCHEME_PALMHOUSE)) {
//                LogUtils.e(TAG, "isJumpWithHome !TextUtils.equals(scheme, BaseConstant.H5.SCHEME_PALMHOUSE)");
//                return false;
//            }
//            String host = uri.getHost();
//            if (!TextUtils.equals(host, BaseConstant.H5.HOST_PALMHOUSE)) {
//                LogUtils.e(TAG, "isJumpWithHome !TextUtils.equals(host, BaseConstant.H5.HOST_PALMHOUSE)");
//                return false;
//            }
//            String actionType = uri.getQueryParameter(BaseConstant.H5.QUERY_ACTION_TYPE);
//            if (TextUtils.isEmpty(actionType)) {
//                LogUtils.e(TAG, "isJumpWithHome TextUtils.isEmpty(actionType)");
//                return false;
//            }
//            LogUtils.e(TAG, "isJumpWithHome true");
//            return true;
//        } catch (Throwable t) {
//            LogUtils.e(t);
//        }
//        LogUtils.e(TAG, "isJumpWithHome false");
//        return false;
//    }
//
//    public static void jumpBySplashUri() {
//        if (isJump(BaseConstant.Base.splashUri)) {
//            jumpByUri(BaseConstant.Base.splashUri);
//        }
//        BaseConstant.Base.splashUri = null;
//    }
//
//    public static void jumpByUri(String uriStr) {
//        Uri uri = getUri(uriStr);
//        if (isJump(uri)) {
//            jumpByUri(uri);
//        }
//    }
//
//    public static void jumpByUriNoJudge(String uriStr) {
//        Uri uri = getUri(uriStr);
//        jumpByUriNoJudge(uri);
//    }
//
//    public static void jumpByUriNoJudge(Uri uri) {
//        jumpByUri(uri);
//    }
//
//    /**
//     * 外部链接跳转进来，不能用ARouter，因为ARouter是异步线程初始化的
//     * 后面ARouter放主线程了，可以用ARouter了
//     */
//    private static void jumpByUri(Uri uri) {
//        LogUtils.d(TAG, "jumpByUri uri:" + uri);
//        if (uri == null) {
//            return;
//        }
//        if (ClickUtils.isDupClick("base_H5JumpUtil_jumpByUri")) {
//            return;
//        }
//        try {
//            String actionType = uri.getQueryParameter(BaseConstant.H5.QUERY_ACTION_TYPE);
//            String encodedPath = uri.getEncodedPath();
//            String id = uri.getQueryParameter(BaseConstant.H5.QUERY_ID);
//            String url = uri.getQueryParameter(BaseConstant.H5.QUERY_URL);
//            String name = uri.getQueryParameter(BaseConstant.H5.QUERY_NAME);
//            switch (actionType) {
//                case BaseConstant.AppRouter.SHARE_SERIES_DETAIL:
//                case BaseConstant.AppRouter.SHARE_PRODUCT_DETAIL:
//                    // 分享-产品详情、系列详情
//                    jumpDetailActivityByShare(id);
//                    break;
//                case BaseConstant.AppRouter.SHARE_PARAMS_CONTRAST:
//                    // 分享-参数对比
//                    jumpParamsPkActivityByShare(id);
//                    break;
//                case BaseConstant.AppRouter.SHARE_SELECTION_LIST:
//                    // 分享-选型清单详情
//                    jumpOrderDetailActivityByShare(id);
//                    break;
//                case BaseConstant.AppRouter.SHARE_EQUIPMENT:
//                    // 分享-装备库详情 （示例：zshc://com.example.myapp/?actiontype=7&share=1&id=9b0bbdd74e9511eda1690242ac110003）
//                    jumpWarehouseDetailActivityByShare(id);
//                    break;
//                case BaseConstant.AppRouter.WEB_VIEW:
//                    // 网页模块
//                    jumpWebViewActivity(name, url);
//                    break;
//                case BaseConstant.AppRouter.PREVIEW_VIDEO:
//                    // 视频模块
//                    jumpPreviewVideoActivity(name, url);
//                    break;
//                case BaseConstant.AppRouter.SERIES_DETAIL:
//                case BaseConstant.AppRouter.PRODUCT_DETAIL:
//                    // 产品详情、系列详情
//                    DetailJumpUtil.jumpDetailActivity(id);
//                    break;
//                case BaseConstant.AppRouter.HOME:
//                    MainJumpUtil.jumpMainActivity();
//                    break;
//                default:
//                    // actionType协议不存在，弹框提示用户下载最新版本
//                    defaultTips();
//                    break;
//
//            }
//        } catch (Throwable t) {
//            LogUtils.e(t);
//        }
//    }
//
//    public static void jumpDetailActivityByShare(String shareId) {
//        LogUtils.e(TAG, "jumpDetailActivityByShare shareId:" + shareId);
//        if (ClickUtils.isDupClick(ARouterConstant.Detail.DETAIL) || TextUtils.isEmpty(shareId)) {
//            return;
//        }
//        Intent intent = new Intent();
//        intent.setClassName("com.example.myapp", "com.example.myapp.detail.ui.activity.DetailActivity");
//        jumpShareByIntent(intent, shareId);
//    }
//
//    public static void jumpParamsPkActivityByShare(String shareId) {
//        LogUtils.e(TAG, "jumpParamsPkActivityByShare shareId:" + shareId);
//        if (ClickUtils.isDupClick(ARouterConstant.PK.PARAMS_PK) || TextUtils.isEmpty(shareId)) {
//            return;
//        }
//        Intent intent = new Intent();
//        intent.setClassName("com.example.myapp", "com.example.myapp.pk.ui.activity.PkParamsActivity");
//        jumpShareByIntent(intent, shareId);
//    }
//
//    public static void jumpOrderDetailActivityByShare(String shareId) {
//        LogUtils.e(TAG, "jumpOrderDetailActivityByShare shareId:" + shareId);
//        if (ClickUtils.isDupClick(ARouterConstant.User.HISTORY_ORDER_DETAIL) || TextUtils.isEmpty(shareId)) {
//            return;
//        }
//        Intent intent = new Intent();
//        intent.setClassName("com.example.myapp", "com.example.myapp.user.ui.activity.cart.OrderSharedDetailActivity");
//        jumpShareByIntent(intent, shareId);
//    }
//
//    public static void jumpWarehouseDetailActivityByShare(String shareId) {
//        LogUtils.e(TAG, "jumpWarehouseDetailActivityByShare shareId:" + shareId);
//        if (ClickUtils.isDupClick(ARouterConstant.User.WAREHOUSE_SHARED_DETAIL) || TextUtils.isEmpty(shareId)) {
//            return;
//        }
//        Intent intent = new Intent();
//        intent.setClassName("com.example.myapp", "com.example.myapp.user.ui.activity.warehouse.WarehouseSharedDetailActivity");
//        jumpShareByIntent(intent, shareId);
//    }
//
//    private static void jumpShareByIntent(Intent intent, String shareId) {
//        intent.putExtra(KEY_SHARE_ID, shareId);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Utils.getApp().startActivity(intent);
//    }
//
//    public static void jumpWebViewActivity(String name, String url) {
//        String decodeUrl = BridgeUtil.decodeUrl(url);
//        if (TextUtils.isEmpty(decodeUrl)) {
//            LogUtils.e(TAG, "jumpWebViewActivity TextUtils.isEmpty(decodeUrl)");
//            return;
//        }
//        CommonJumpUtil.jumpWebViewActivity(name, decodeUrl, TextUtils.isEmpty(name));
//    }
//
//    public static void jumpPreviewVideoActivity(String name, String url) {
//        String decodeUrl = BridgeUtil.decodeUrl(url);
//        if (TextUtils.isEmpty(decodeUrl)) {
//            LogUtils.e(TAG, "jumpPreviewVideoActivity TextUtils.isEmpty(decodeUrl)");
//            return;
//        }
//        CommonJumpUtil.jumpPreviewVideoActivity(name, decodeUrl);
//    }
//
//    public static void defaultTips() {
//        // actionType协议不存在，弹框提示用户下载最新版本
//        Activity topActivity = ActivityUtils.getTopActivity();
//        if (Utils.isActivityDestroy(topActivity)) {
//            return;
//        }
//        SimpleTitleDialog tipsDialog = (SimpleTitleDialog) DialogHelper.INSTANCE.create2ButtonTitleDialog(topActivity,
//                topActivity.getString(R.string.base_tips), topActivity.getString(R.string.base_update_tips),
//                topActivity.getString(R.string.base_cancel), topActivity.getString(R.string.base_update),
//                new Function1<View, Unit>() {
//                    @Override
//                    public Unit invoke(View view) {
//                        Intent browseIntent = IntentUtils.getBrowseIntent(BaseConstant.Config.URL_H5 + BaseConstant.H5Router.DOWNLOAD);
//                        if (browseIntent != null) {
//                            topActivity.startActivity(browseIntent);
//                        }
//                        return null;
//                    }
//                });
//        tipsDialog.show();
//    }
//}
