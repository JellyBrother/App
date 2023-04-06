package com.jelly.providerlibrary.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jelly.baselibrary.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProviderUtil {
    private final static String TAG = "ProviderUtil";
    private static final long MIN_DELETE_WARING_TIME = 1000;//1s
    private static final Uri BACKUP_SMS = Uri.parse("content://backup-sms/");
    public static final int MAX_NOTIFY_ID_COUNT = 50;//通知给全局搜索删除信息的id的最大个数，超过该值减一，认为批量删除
    public static final String NOTIFY_PARAM_SEPARATOR = "@";
    public static final String NOTIFY_ID_SEPARATOR = ",";

    /**
     * Description:将本次批量删除（通过applyBatch）的信息的id列表通知全局搜索
     * 目前设计，每次最多通知25个id，当超过25个id时，通知id=-1代表
     * 通知内容的格式是id@deleteNums@totalNums，ids列表数据超过1个时，用逗号分隔，
     * 如23,24,25@6@100 代表本次删除3个会话，删除短彩信总数是6，删除前短信彩信总条数是100
     * 当删除超过25条时，内容是-1@27@100
     */
    public static void notifyDeleteChange(Context context, boolean isThreadId, ArrayList<String> ids, int deleteNums, int totalNums) {
        if (ids == null || ids.size() == 0) {
            LogUtil.getInstance().i(TAG, "notifyDeleteChange ids is null or empty");
            return;
        }
        if (deleteNums == 0) {
            LogUtil.getInstance().i(TAG, "notifyDeleteChange deleteNums is 0.");
            return;
        }
        int idNum = ids.size();

        if (idNum <= MAX_NOTIFY_ID_COUNT) {
            notifyDeleteChangePartly(context, isThreadId, ids, deleteNums, totalNums);
        } else {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 0; i < idNum; i++) {
                tempList.add(ids.get(i));
                if (tempList.size() == MAX_NOTIFY_ID_COUNT) {
                    notifyDeleteChangePartly(context, isThreadId, tempList, deleteNums, totalNums);
                    tempList.clear();
                }
            }
            if (tempList.size() > 0) {
                notifyDeleteChangePartly(context, isThreadId, tempList, deleteNums, totalNums);
                tempList.clear();
            }
        }
        ids.clear();
    }

    private static void notifyDeleteChangePartly(Context context, boolean isThreadId, ArrayList<String> ids, int deleteNums, int totalNums) {
        if (ids == null || ids.size() == 0) {
            LogUtil.getInstance().i(TAG, "notifyDeleteChangePartly ids is null or empty");
            return;
        }
        int idNum = ids.size();
        if (idNum > MAX_NOTIFY_ID_COUNT) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idNum; i++) {
            if (i == ids.size() - 1) {
                sb.append(ids.get(i));
            } else {
                sb.append(ids.get(i)).append(NOTIFY_ID_SEPARATOR);
            }
        }
        String info = sb.toString() + NOTIFY_PARAM_SEPARATOR + deleteNums + NOTIFY_PARAM_SEPARATOR + totalNums;
        Uri uri = Uri.parse("content://sms/" + (isThreadId ? "threadId/" : "messageId/") + info);
        LogUtil.getInstance().i(TAG, "notifyDeleteChangePartly uri: " + uri);
        context.getContentResolver().notifyChange(uri, null, true);
        ids.clear();
    }

    /**
     * Get space separated package names associated with a UID
     *
     * @param context The context to use
     * @param uid     The UID to look up
     * @return The space separated list of package names for UID
     */
    public static String getPackageNamesByUid(Context context, int uid) {
        final PackageManager pm = context.getPackageManager();
        final String[] packageNames = pm.getPackagesForUid(uid);
        if (packageNames != null) {
            final StringBuilder sb = new StringBuilder();
            for (String name : packageNames) {
                if (!TextUtils.isEmpty(name)) {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(name);
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Whether should set CREATOR for an insertion
     *
     * @param values The content of the message
     * @param uid    The caller UID of the insertion
     * @return true if we should set CREATOR, false otherwise
     */
    public static boolean shouldSetCreator(ContentValues values, int uid) {
        return (uid != Process.SYSTEM_UID && uid != Process.PHONE_UID) ||
                (!values.containsKey(Telephony.Sms.CREATOR) &&
                        !values.containsKey(Telephony.Mms.CREATOR));
    }

    /**
     * Whether should remove CREATOR for an update
     *
     * @param values The content of the message
     * @param uid    The caller UID of the update
     * @return true if we should remove CREATOR, false otherwise
     */
    public static boolean shouldRemoveCreator(ContentValues values, int uid) {
        return (uid != Process.SYSTEM_UID && uid != Process.PHONE_UID) &&
                (values.containsKey(Telephony.Sms.CREATOR) ||
                        values.containsKey(Telephony.Mms.CREATOR));
    }

    public static boolean isDefaltSmsApplication(Context context, String packageName) {
        String defaultSmsApplication = Telephony.Sms.getDefaultSmsPackage(context);
        if (defaultSmsApplication != null && defaultSmsApplication.equals(packageName)) {
            return true;
        }
        return false;
    }

    //added by lengxibo for Rom3.1 2016.07.29
    public static boolean isPackageSystemApp(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                return true;
            }

            String defaultSmsApplication = Telephony.Sms.getDefaultSmsPackage(context);
            if (defaultSmsApplication != null && defaultSmsApplication.equals(packageName)) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public static boolean isMultiSimEnabled(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isMultiSimEnabled = false;
        try {
            Method method = TelephonyManager.class.getDeclaredMethod("isMultiSimEnabled");
            method.setAccessible(true);
            isMultiSimEnabled = (boolean) method.invoke(tm);
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "isMultiSimEnabled exception: " + e);
        }
        return isMultiSimEnabled;
    }

    private static String invokeDestinationAddress(SmsMessage smsMessage, String methodName) {
        String recipientAddr = "";
        try {
            Class cls = smsMessage.getClass();
            Method method = cls.getMethod(methodName, new Class[0]);
            Object object = method.invoke(smsMessage, new Object[]{});
            recipientAddr = (String) object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return recipientAddr;
    }

    /**
     * 用于将set集合转换成where条件的IN语句集合<p>里面需要做的操作包括，将[]替换为()，为每一个元素添加''
     */
    public static String SetToWhereString(Set<?> sets) {
        String str = "()";
        if (sets.size() > 0) {
            str = sets.toString();
            str = str.replace("[", "('");
            str = str.replace("]", "')");
            str = str.replace(", ", "','");
        }
        return str;
    }

    @NonNull
    public static Bundle jsonToBundle(String json) {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                Iterator<String> iterator = jsonObj.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    bundle.putString(key, jsonObj.getString(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bundle;
    }

    @NonNull
    public static String bundleToJson(Bundle bundle) {
        JSONObject jsonObject = new JSONObject();
        Set<String> set = bundle.keySet();
        try {
            for (String key : set) {
                jsonObject.put(key, bundle.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @NonNull
    public static Map<String, String> bundleToMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>();
        try {
            Set<String> set = bundle.keySet();
            for (String key : set) {
                Object value = bundle.get(key);
                map.put(key, String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    @NonNull
    public static Map<String, String> jsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                map.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static int updateDeleteBackupSms(Context context, ContentValues values, Uri uri, boolean isThreadDelete) {
        String selection = "";
        int count = 0;
        String id = uri.getLastPathSegment();
        String[] selectionArgs = {id};
        if (isThreadDelete) {
            selection = "thread_id=?";
        } else {
            selection = "sms_id=?";
        }
        try {
            count = context.getContentResolver().update(BACKUP_SMS, values, selection, selectionArgs);
        } catch (Exception e) {
            LogUtil.getInstance().w(TAG, "updateDeleteBackupSms failed " + e.getMessage());
        }
        return count;
    }

    public static boolean isQuicklyDelete(Context context, String id, boolean isThreadDelete) {
        String[] projection = {"time"};
        String selection = "";
        String[] selectionArgs = {id};
        if (isThreadDelete) {
            selection = "thread_id=?";
        } else {
            selection = "sms_id=?";
        }
        Cursor c = null;
        try {
            c = context.getContentResolver().query(BACKUP_SMS, projection, selection, selectionArgs, null);
            if (c != null && c.moveToFirst()) {
                String time = c.getString(0);
                LogUtil.getInstance().v(TAG, " time = " + time);
                if (System.currentTimeMillis() - Long.parseLong(time) < MIN_DELETE_WARING_TIME) {
                    return true;
                }
            } else {
                LogUtil.getInstance().w(TAG, "cursor is null");
                return false;
            }
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "query faild error " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    public static String replaceStringWithPoint(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            int length = str.length();
            int pointLength = length / 3;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < pointLength; i++) {
                sb.append("*");
            }
            return str.substring(0, pointLength) + sb.toString() + str.substring(2 * pointLength, length);
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "replaceStringWithPoint ERROR" + e.getMessage());
        }
        return str;
    }

    public static String getChatbotNumber(String serviceId) {
        String number = "";
        String regx = "(?<=:).*?(?=@)";
        try {
            Pattern pattern = Pattern.compile(regx);
            Matcher m = pattern.matcher(serviceId);
            if (m.find()) {
                number = m.group(0);
            }
        } catch (Exception e) {
            LogUtil.getInstance().e(TAG, "getNumber error: " + e);
        }
        return number;
    }
}
