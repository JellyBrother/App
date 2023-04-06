package com.example.myapp.base.net.utils;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.net.constant.NetConstant;
import com.example.myapp.base.utils.AppUtils;
import com.example.myapp.base.utils.DeviceUtils;
import com.example.myapp.base.utils.RomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetUtil {

    public static boolean isSuccess(long code) {
        return (code >= NetConstant.NetCode.CODE_200 && code <= NetConstant.NetCode.CODE_299);
    }

    public static boolean isInvalid(Response response) {
        int code = response.code();
        ResponseBody body = response.body();
        return false;
    }

    public static boolean isSuccess(ApiResponse response) {
        return response != null && response.isSuccess();
    }

    public static boolean isSuccessData(ApiResponse response) {
        return isSuccess(response) && response.getData() != null;
    }

    public static <T> boolean isListSuccess(ApiResponse<List<T>> response) {
        return isSuccessData(response);
//        if (response == null || !response.isSuccess() || ListUtil.isEmpty(response.getData()) || response.getData().get(0) == null) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public static Request builderCommonHeads(Request request) {
        if (request == null) {
            return null;
        }
        Request.Builder builder = request.newBuilder();
        if (builder == null) {
            return request;
        }
        Map<String, String> map = builderCommonParams();
        for (String key : map.keySet()) {
            builder.addHeader(key, map.get(key));
        }
        return builder.build();
    }

    public static Map<String, String> builderCommonParams() {
        return builderCommonParams(null);
    }

    public static Map<String, String> builderCommonParams(Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(BaseConstant.HttpConfig.HEAD_VERSION, AppUtils.getAppVersionName());
        //获取唯一设备 ID
        String uniqueDeviceId = DeviceUtils.getUniqueDeviceId();
        //获取设备厂商
        String manufacturer = DeviceUtils.getManufacturer();
        //获取设备型号
        String model = DeviceUtils.getModel();
        String sdkVersionName = DeviceUtils.getSDKVersionName();
        map.put(BaseConstant.HttpConfig.HEAD_DEV_ID, uniqueDeviceId);
        map.put(BaseConstant.HttpConfig.HEAD_DEV_NAME, manufacturer + "-" + model);
        map.put(BaseConstant.HttpConfig.HEAD_SYS_VER, sdkVersionName);
        map.put(BaseConstant.HttpConfig.HEAD_PLATFORM, BaseConstant.HttpConfig.HEAD_PLATFORM_ANDROID);
        map.put(BaseConstant.HttpConfig.HEAD_TIME, System.currentTimeMillis() + "");
        map.put(BaseConstant.HttpConfig.HEAD_BUILD_TIME, BaseConstant.Base.sBuildTime);
        map.put(BaseConstant.HttpConfig.HEAD_ID_CARD, DeviceUtils.getRandomId());
        map.put(BaseConstant.HttpConfig.HEAD_CHANNEL, RomUtils.getChannel());
        return map;
    }
}
