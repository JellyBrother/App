//package com.jelly.provider.search.provider;
//
//import android.content.ContentProvider;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.sqlite.SQLiteConstraintException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDoneException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteStatement;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.os.Message;
//import android.os.storage.StorageManager;
//import android.provider.BaseColumns;
//import android.provider.Settings;
//import android.provider.Telephony;
//import android.provider.Telephony.Mms;
//import android.provider.Telephony.Mms.Addr;
//import android.provider.Telephony.Mms.Part;
//import android.provider.Telephony.Mms.Rate;
//import android.provider.Telephony.MmsSms;
//import android.provider.Telephony.MmsSms.PendingMessages;
//import android.provider.Telephony.Sms;
//import android.provider.Telephony.Threads;
//import android.support.annotation.VisibleForTesting;
//import android.text.TextUtils;
//
//import com.jelly.baselibrary.utils.LogUtil;
//import com.jelly.providerlibrary.utils.ProviderUtil;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//public class SearchDatabaseHelper extends SQLiteOpenHelper {
//	private static final String TAG="DbHelper";
//    private static final String UPDATE_THREAD_READ_COUNT =
//                        "  UPDATE threads SET readcount = " +
//                        "  (SELECT count(_id)FROM "+
//                        "  (SELECT DISTINCT date * 1 AS normalized_date, _id, read FROM sms "+
//                        "    WHERE ((read=1) AND thread_id = new.thread_id AND (type != 3))  "+
//                        "  UNION SELECT DISTINCT date * 1000 AS normalized_date, pdu._id, read "+
//                        "  FROM pdu LEFT JOIN pending_msgs ON pdu._id = pending_msgs.msg_id "+
//                        "  WHERE ((read=1) AND thread_id = new.thread_id AND msg_box != 3 AND (msg_box != 3 "+
//                        "        AND (m_type = 128 OR m_type = 132 OR m_type = 130)))" +
//                        "   ORDER BY normalized_date ASC))  "+
//                        "  WHERE threads._id = new.thread_id; ";
//
//    private static final String SMS_UPDATE_THREAD_READ_BODY =
//                        "  UPDATE threads SET read = " +
//                        "    CASE (SELECT COUNT(*)" +
//                        "          FROM sms" +
//                        "          WHERE " + Sms.READ + " = 0" +
//                        "            AND " + Sms.THREAD_ID + " = threads._id)" +
//                        "      WHEN 0 THEN 1" +
//                        "      ELSE 0" +
//                        "    END" +
//                        "  WHERE threads._id = new." + Sms.THREAD_ID + "; ";
//
//    private static final String UPDATE_THREAD_COUNT_ON_NEW =
//                        "  UPDATE threads SET message_count = " +
//                        "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//                        "      ON threads._id = " + Sms.THREAD_ID +
//                        "      WHERE " + Sms.THREAD_ID + " = new.thread_id" +
//                        "        AND sms." + Sms.TYPE + " != 3) + " +
//                        "     (SELECT COUNT(pdu._id) FROM pdu LEFT JOIN threads " +
//                        "      ON threads._id = " + Mms.THREAD_ID +
//                        "      WHERE " + Mms.THREAD_ID + " = new.thread_id" +
//                        "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//                        "        AND " + Mms.MESSAGE_BOX + " != 3) " +
//                        "  WHERE threads._id = new.thread_id; ";
//    //add by lk 2011-07-22
//
//    private static final String UPDATE_THREAD_UNREADCOUNT_ON_NEW =
//        "  UPDATE threads SET unreadcount = " +
//        "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//        "      ON threads._id = " + Sms.THREAD_ID +
//        "      WHERE " + Sms.THREAD_ID + " = new.thread_id" +
//        "        AND sms." + Sms.TYPE + " != 3" +
//        "        AND sms." + Sms.READ + " = 0) + " +
//        "     (SELECT COUNT(pdu._id) FROM pdu LEFT JOIN threads " +
//        "      ON threads._id = " + Mms.THREAD_ID +
//        "      WHERE " + Mms.THREAD_ID + " = new.thread_id" +
//        "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//        "        AND " + Mms.MESSAGE_BOX + " != 3" +
//        "        AND pdu." + Mms.READ + " = 0) " +
//        "  WHERE threads._id = new.thread_id ; ";
//    //add by lk
//    private static final String UPDATE_THREAD_SIMID_ON_NEW =
//        "UPDATE threads SET sub_id = " +
//        "(SELECT sub_id FROM "+
//        "(SELECT thread_id, date * 1000 AS date,sub_id,message_mode FROM pdu"+
//        " UNION SELECT thread_id,date,sub_id,message_mode FROM sms )"+
//        " WHERE thread_id = " + " new.thread_id"+
//        " ORDER BY date DESC LIMIT 1)" +
//        "  WHERE threads._id = new.thread_id; ";
//    //end
//
//    //add by lk
//    private static final String UPDATE_THREAD_SIMID_ON_DELETE =
//        "UPDATE threads SET sub_id = " +
//        "(SELECT sub_id FROM "+
//        "(SELECT thread_id, date * 1000 AS date,sub_id,message_mode FROM pdu"+
//        " UNION SELECT thread_id,date,sub_id,message_mode FROM sms)"+
//        " WHERE thread_id = " + " old.thread_id" +
//        " ORDER BY date DESC LIMIT 1)" +
//        "  WHERE threads._id = old.thread_id; ";
//    //end
//    //end
//
//    private static final String UPDATE_THREAD_COUNT_ON_OLD =
//                        "  UPDATE threads SET message_count = " +
//                        "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//                        "      ON threads._id = " + Sms.THREAD_ID +
//                        "      WHERE " + Sms.THREAD_ID + " = old.thread_id" +
//                        "        AND sms." + Sms.TYPE + " != 3) + " +
//                        "     (SELECT COUNT(pdu._id) FROM pdu LEFT JOIN threads " +
//                        "      ON threads._id = " + Mms.THREAD_ID +
//                        "      WHERE " + Mms.THREAD_ID + " = old.thread_id" +
//                        "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//                        "        AND " + Mms.MESSAGE_BOX + " != 3) " +
//                        "  WHERE threads._id = old.thread_id; ";
//
//    //add by lk 2011-07-22
//
//    private static final String UPDATE_THREAD_UNREADCOUNT_ON_OLD =
//        "  UPDATE threads SET unreadcount = " +
//        "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//        "      ON threads._id = " + Sms.THREAD_ID +
//        "      WHERE " + Sms.THREAD_ID + " = old.thread_id" +
//        "        AND sms." + Sms.TYPE + " != 3 " +
//        "        AND sms." + Sms.READ + " = 0) + " +
//        "     (SELECT COUNT(pdu._id) FROM pdu LEFT JOIN threads " +
//        "      ON threads._id = " + Mms.THREAD_ID +
//        "      WHERE " + Mms.THREAD_ID + " = old.thread_id" +
//        "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//        "        AND " + Mms.MESSAGE_BOX + " != 3 " +
//        "        AND pdu." + Mms.READ + " = 0) " +
//        "  WHERE threads._id = old.thread_id; ";
//
//    //end
//
//    private static final String SMS_UPDATE_THREAD_DATE_SNIPPET_COUNT_ON_UPDATE =
//                        "BEGIN" +
//                        "  UPDATE threads SET" +
//                        "    date = new." + Sms.DATE + ", " +
//                        "    sub_id = new.sub_id, " +  //added by lengxibo for 批量数据优化. 2014.5.14
//                        " time = new.time, "+  //add by liukai
//                        "    snippet = new." + Sms.BODY + ", " +
//                        "    snippet_cs = 0" +
//                        "  WHERE threads._id = new." + Sms.THREAD_ID + " ; " +
//                        UPDATE_THREAD_COUNT_ON_NEW +
//                        UPDATE_THREAD_UNREADCOUNT_ON_NEW +   //add by lk 2011-07-22
//                        SMS_UPDATE_THREAD_READ_BODY +
//                        UPDATE_THREAD_READ_COUNT+
//                        "END;";//
//    private static final String PDU_UPDATE_THREAD_DATE =
//            "UPDATE threads" +
//            "  SET" +
//            "  date =" +
//            "    (SELECT date FROM" +
//            "        (SELECT date * 1000 AS date, thread_id,message_mode FROM pdu " +
//            "         WHERE (new.m_type=132 OR new.m_type=130 OR new.m_type=128) " +
//            "               AND (thread_id = " + "new." + Mms.THREAD_ID + " AND new.message_mode = message_mode ) "+
//            "         UNION SELECT date, thread_id,message_mode FROM sms " +
//            "         WHERE thread_id = " + "new." + Mms.THREAD_ID + " AND new.message_mode = message_mode ) " +
//            "     WHERE thread_id = " + "new." + Mms.THREAD_ID  + " ORDER BY date DESC LIMIT 1) " +
//            "  WHERE threads._id = " + "new." + Mms.THREAD_ID + ";";
//
//    private static final String PDU_UPDATE_THREAD_TIME =
//            "UPDATE threads" +
//            "  SET" +
//            "  time =" +
//            "    (SELECT time FROM" +
//            "        (SELECT  time,date * 1000 AS date, thread_id,message_mode FROM pdu " +
//            "         WHERE (new.m_type=132 OR new.m_type=130 OR new.m_type=128) " +
//            "               AND (thread_id = " + "new." + Mms.THREAD_ID + " AND new.message_mode = message_mode ) "+
//            "         UNION SELECT time,date, thread_id,message_mode FROM sms " +
//            "         WHERE thread_id = " + "new." + Mms.THREAD_ID + " AND new.message_mode = message_mode ) " +
//            "     WHERE thread_id = " + "new." + Mms.THREAD_ID  + " ORDER BY date DESC LIMIT 1) " +
//            "  WHERE threads._id = " + "new." + Mms.THREAD_ID + ";";
//
//    private static final String PDU_UPDATE_THREAD_DATE_ON_OLD =
//            "UPDATE threads" +
//            "  SET" +
//            "  date =" +
//            "    (SELECT date FROM" +
//            "        (SELECT date * 1000 AS date, thread_id,message_mode FROM pdu " +
//            "         WHERE (old.m_type=132 OR old.m_type=130 OR old.m_type=128) " +
//            "               AND (thread_id = " + "old." + Mms.THREAD_ID + " AND old.message_mode = message_mode ) "+
//            "         UNION SELECT date, thread_id,message_mode FROM sms " +
//            "         WHERE thread_id = " + "old." + Mms.THREAD_ID + " AND old.message_mode = message_mode ) " +
//            "     WHERE thread_id = " + "old." + Mms.THREAD_ID  + " ORDER BY date DESC LIMIT 1) " +
//            "  WHERE threads._id = " + "old." + Mms.THREAD_ID + ";";
//
//    private static final String PDU_UPDATE_THREAD_TIME_ON_OLD =
//            "UPDATE threads" +
//            "  SET" +
//            "  time =" +
//            "    (SELECT time FROM" +
//            "        (SELECT  time,date * 1000 AS date, thread_id,message_mode FROM pdu " +
//            "         WHERE (old.m_type=132 OR old.m_type=130 OR old.m_type=128) " +
//            "               AND (thread_id = " + "old." + Mms.THREAD_ID + " AND old.message_mode = message_mode ) "+
//            "         UNION SELECT time,date, thread_id,message_mode FROM sms " +
//            "         WHERE thread_id = " + "old." + Mms.THREAD_ID + " AND old.message_mode = message_mode ) " +
//            "     WHERE thread_id = " + "old." + Mms.THREAD_ID  + " ORDER BY date DESC LIMIT 1) " +
//            "  WHERE threads._id = " + "old." + Mms.THREAD_ID + ";";
//    //end add
//    private static final String UPDATE_THREAD_SNIPPET_SNIPPET_CS_ON_DELETE =
//			            "  UPDATE threads SET message_mode =" +
//			        			"	CASE " +
//			                    "   WHEN " +
//		                        "   (0 IN (SELECT message_mode FROM sms WHERE OLD.thread_id = thread_id) OR" +
//		                        "  0 IN ( SELECT message_mode FROM pdu WHERE OLD.thread_id = thread_id) )" +
//		                        "   THEN 0 " +
//								"	ELSE 2 " +
//			        			"	END " +
//			            "  WHERE threads._id = OLD.thread_id; " +
//                        "  UPDATE threads SET snippet = " +
//                        "   (SELECT snippet FROM" +
//                        "     (SELECT date * 1000 AS date, sub AS snippet, thread_id ,message_mode FROM pdu WHERE m_type=132 OR m_type=130 OR m_type=128" +
//                        "      UNION SELECT date, body AS snippet, thread_id,message_mode FROM sms)" +
//                        "    WHERE thread_id = OLD.thread_id ORDER BY date DESC LIMIT 1 ) " +
//                        "  WHERE threads._id = OLD.thread_id; " +
//                        "  UPDATE threads SET snippet_cs = " +
//                        "   (SELECT snippet_cs FROM" +
//                        "     (SELECT date * 1000 AS date, sub_cs AS snippet_cs, thread_id,message_mode FROM pdu WHERE m_type=132 OR m_type=130 OR m_type=128" +
//                        "      UNION SELECT date, 0 AS snippet_cs, thread_id,message_mode FROM sms)" +
//                        "    WHERE thread_id = OLD.thread_id ORDER BY date DESC LIMIT 1) " +
//                        "  WHERE threads._id = OLD.thread_id; ";
//
//    private static SearchDatabaseHelper sDeInstance = null;
//    private static SearchDatabaseHelper sCeInstance = null;
//
//    public static final String COL_THREADS_V_ADDRESS_TYPE = "v_address_type";
//    public static final String COL_THREADS_V_ADDRESS_NAME = "v_address_name";
//    public static final String COL_THREADS_V_ADDRESS_FROM = "v_address_from";
//
////    private static final String PREFERENCE_XML_SETTING = "setting_key_value";//统一使用MMSSettingProvider中的定义
//    private static final String VERIFY_CODE_PROTECTED = "pref_key_verify_code";//验证码保护
////    private static final String RESTORE_DB_SUCCESSED = "restore_db_successed";
////    private static final String BACKUP_DB_FAILED = "backup_db_failed";//该值为true，代表备份和恢复db的方案失败，需要采用插入的方案
//    private static final String KEY_SMS_CARD_CONFIG = "sms_card_cfg";
//    private static final String SMS_RECOGNITION = "pref_key_sms_recognition";//信息智能识别
//    private static final String NOTICE_MERGE = "pref_key_notice_merge";//通知类信息聚合
//    private static final String SMS_CARD = "pref_key_sms_card";//信息卡片化显示
//
//    public static final String TABLE_NAME_NOTICE = "notice";
//    private static final String COL_NOTICE_ID = "_id";
//    public static final String COL_NOTICE_ADDRESS = "address";
//    public static final String COL_NOTICE_NAME = "name";
//    public static final String COL_NOTICE_INLINE = "inline";
//    public static final String COL_NOTICE_DATE = "date";
//    public static final String COL_NOTICE_LOGO_ID ="logo_id";
//    public static final String COL_NOTICE_FROM ="notice_from";
//
//
//    public static final String TABLE_LOGO_RES = "logo_res";
//    public static final String COL_LOGO_ID = "_id";
//    public static final String COL_LOGO_HASH = "hash";
//    public static final String COL_LOGO_URL = "url";
//    public static final String COL_LOGO_LAST_TIME ="time";
//    public static final String COL_LOGO_EXISTS = "exs";
//
//    public static final String TABLE_NAME_NOTICE_MENU = "notice_menu";
//    public static final String COL_NOTICE_MENU_ID = "_id";
//    public static final String COL_NOTICE_MENU_CONTENT = "menu";
//    public static final String COL_NOTICE_MENU_LAST_TIME = "time";
//    public static final String COL_NOTICE_MENU_SHOP_ID ="shop_id";
//    public static final String COL_NOTICE_MENU_NAME = "name";
//    public static final String COL_NOTICE_MENU_AGENCY = "agency";
//
//    //sp_to_type table
//    public static final String TABLE_NAME_SP_TYPE = "sp_type";
//    public static final String COL_SP_PUSH_TYPE = "push_type";
//    public static final String COL_SP_SPID = "sp_id";
//    //push_mms table
//    public static final String TABLE_NAME_PUSH_MMS = "push_mms";
//    public static final String COL_PUSH_MMS_ID = "_id";
//    public static final String COL_PUSH_MMS_THREAD_ID = "thread_id";
//    public static final String COL_PUSH_MMS_SHOP_ID = "shop_id";
//    public static final String COL_PUSH_MMS_MSG_ID = "msg_id";
//    public static final String COL_PUSH_MMS_NUMBER = "number";
//    public static final String COL_PUSH_MMS_NAME = "name";
//    public static final String COL_PUSH_MMS_CONTENT = "content";
//    public static final String COL_PUSH_MMS_MSG_JSON = "msg_json";
//    public static final String COL_PUSH_MMS_MEDIA_TYPE = "media_type";
//    public static final String COL_PUSH_MMS_READ_TYPE = "read_type";
//    public static final String COL_PUSH_MMS_SEND_STATE = "send_state";
//    public static final String COL_PUSH_MMS_RECEIVE_TYPE = "receive_type";
//    public static final String COL_PUSH_MMS_NOTIFY_TYPE = "notify_type";
//    public static final String COL_PUSH_MMS_SCENE_TYPE = "scene_type";
//    public static final String COL_PUSH_MMS_DATE = "date";
//    public static final String COL_PUSH_MMS_SOURCE = "source";
//    public static final String COL_PUSH_MMS_SEEN = "seen";
//    public static final String COL_PUSH_MMS_TYPE = "push_type"; // vivo <lipeng> add for differentiate push mms type, ect from ted or monternet portal.
//    public static final String COL_PUSH_MMS_SUBID = "sub_id";   // vivo <lipeng> add for double card phone.
//    public static final String COL_PUSH_MMS_BUBBLE = "bubble";  // vivo <lipeng> add for parsing the card of push mms.
//    public static final String COL_PUSH_MMS_BUBBLE_TYPE = "bubble_type"; // vivo <lipeng> add for show card or original.
//    public static final String COL_PUSH_MMS_SP_ID = "sp_id"; // vivo <lipeng> add for which tripartite access .
//    public static final String COL_PUSH_MMS_EXTRA = "extra";//vivo <yanglei> add for reply mw
//    public static final String COL_PUSH_MMS_RISK_WEBSITE = "risk_website";//vivo <liwanbing> add for reply mw
//    public static final String COL_PUSH_MMS_BUBBLE_PARSE_TIME = "bubble_parse_time";
//    public static final String COL_PUSH_MMS_VERIFY_CODE = "verify_code";
//    public static final String COL_PUSH_MMS_READ_TYPE_ALIAS = "read";
//    public static final String COL_PUSH_MMS_SEND_STATE_ALIAS = "status";
//    public static final String COL_PUSH_MMS_RECEIVE_TYPE_ALIAS = "type";
//    public static final String COL_PUSH_MMS_ID_EX = "_id_ex"; // 扩展的主键，用于多表查询时的主键
//    public static final String COL_PUSH_MMS_DYNAMIC_BUBBLE = "push_dynamic_bubble";
//    public static final String COL_PUSH_MMS_DYNAMIC_BUBBLE_DATE = "push_dynamic_update_date";
//    public static final String COL_PUSH_MMS_EXTEND_TYPE = "push_mms_extend_type";
//
//    //push_shop table
//    public static final String TABLE_NAME_PUSH_SHOP = "push_shop";
//    public static final String COL_PUSH_SHOP_ID = "_id";
//    public static final String COL_PUSH_SHOP_THREAD_ID = "thread_id";
//    public static final String COL_PUSH_SHOP_SHOP_ID = "shop_id";
//    public static final String COL_PUSH_SHOP_NUMBER = "number";
//    public static final String COL_PUSH_SHOP_NAME = "name";
//    public static final String COL_PUSH_SHOP_LOGO = "logo";
//    public static final String COL_PUSH_SHOP_MENU = "shop_menu";
//    public static final String COL_PUSH_SHOP_DESC = "shop_desc";
//    public static final String COL_PUSH_SHOP_IS_IN_BLACK = "is_in_black";
//    public static final String COL_PUSH_SHOP_IS_ENCRYPTED = "is_encrypted";
//    public static final String COL_PUSH_SHOP_BLACK_UPLOADED = "black_uploaded";
//    public static final String COL_PUSH_SHOP_NOTIFICATION = "notification";
//    public static final String COL_PUSH_SHOP_NOTIFICATION_UPLOADED = "notification_uploaded";
//    public static final String COL_PUSH_SHOP_MENU_RECEIVED_TIME = "menu_received_time";
//    public static final String COL_PUSH_SHOP_TYPE = "push_type";    // vivo <lipeng> add for differentiate push mms type, ect from ted or monternet portal.
//
//    //push_sync table
//    public static final String TABLE_NAME_PUSH_SYNC = "push_sync";
//    public static final String COL_PUSH_SYNC_ID = "_id";
//    public static final String COL_PUSH_SYNC_SHOP_ID = "shop_id";
//    public static final String COL_PUSH_SYNC_TYPE = "sync_type";
//    public static final String COL_PUSH_SYNC_DATE = "insert_date";
//
//    //iroaming_support table
//    public static final String TABLE_NAME_IROAMING_SUPPORT = "iroaming_support";
//    public static final String COL_IROAMING_SUPPORT_ID = "_id";
//    public static final String COL_IROAMING_SUPPORT_DEEPLINK = "deeplink";
//    public static final String COL_IROAMING_SUPPORT_COUNTRY_CODE = "country_code";
//    public static final String COL_IROAMING_SUPPORT_VERSION_TYPE = "type";
//
//    //wifi_push table
//    public static final String TABLE_NAME_WIFI_PUSH = "wifi_push";
//    public static final String COL_WIFI_PUSH_BSSID = "bssid";
//    public static final String COL_WIFI_PUSH_TYPE = "type"; //1：个人WiFi，黑名单   2：商业WiFi，不在白名单  3：商业WiFi，在白名单
//    public static final String COL_WIFI_PUSH_EXPIRY_DATE = "expiry_date";
//    public static final String COL_WIFI_PUSH_LAST_UPDATE_TIME = "ltupdate_time";
//
//    //frequency table
//    public static final String TABLE_NAME_FREQUENCY = "frequency";
//    public static final String COL_FREQUENCY_NAME = "name";
//    public static final String COL_FREQUENCY_TYPE = "type";
//    public static final String COL_FREQUENCY_REQUEST_TIME = "request_time";
//
//    //pdu table
//    public static final String SUBJECT_TEXT = "subject_txt";
//
//    public static final String TABLE_NAME_RCS_FILE_PATH = "temp_rcs_file_path";
//    //block sms keyword table
//    public static final String TABLE_BLOCK_SMS_TABLE_KEYWORD = "block_sms_keyword";
//    public static final String COL_KEYWORD_ID = "_id";
//    public static final String COL_KEYWORD_KEYWORD = "keyword";
//
//    //block notice keyword table
//    public static final String TABLE_BLOCK_NOTICE_KEYWORD = "block_notice_keyword";
//    public static final String COL_BLOCK_NOTICE_ID = "_id";
//    public static final String COL_SERVICE_CATEGORY = "service_category";
//    public static final String COL_BLOCK_NOTICE_TYPE = "block_notice_type";
//    public static final String COL_BLOCK_NOTICE_KEYWORDS = "block_notice_keywords";
//
//    private static final int ADDRESS_TYPE_SERVICE = 1;
//	private static final int PUSH_SEND_MSG_TYPE = 12;
//
//    private static final String NO_SUCH_COLUMN_EXCEPTION_MESSAGE = "no such column";
//    private static final String NO_SUCH_TABLE_EXCEPTION_MESSAGE = "no such table";
//
//    public static final int TABLE_TYPE_THREADS = 1;
//    public static final int TABLE_TYPE_SMS = 2;
//    public static final int TABLE_TYPE_MMS = 3;
//    public static final int TABLE_TYPE_PUSH_MESSAGE = 4;
//
//    static final String DATABASE_NAME = "mmssms.db";
//    private static final int DATABASE_VERSION = 8650;
//    private final Context mContext;
//    private ArrayList<ContentProvider> mProviders = new ArrayList<>();
//
//    private static boolean sIsRestored = false;
//    private static boolean sIsCeMode = false;
//    private RestoreHandler mRestoreHandler;
//
//    private SearchDatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        mContext = context;
//        LogUtil.getInstance().i(TAG, "SearchDatabaseHelper constructor sIsRestored: " + sIsRestored + ", sIsCeMode: " + sIsCeMode);
//        if (sIsCeMode && !sIsRestored) {
//            HandlerThread thread = new HandlerThread("RestoreDataThread");
//            thread.start();
//            mRestoreHandler = new RestoreHandler(thread.getLooper());
//            checkAndRestoreDb(false);
//        }
//        SQLiteOpenHelperManager.getInstance(context);
//    }
//
//    private static String getStorePath(Context context, int mode) {
//        String file;
//        try {
//            if (mode == 0) {
//                file = StorageManagerWrapper.getInstance(context).getInternalStorageDirectory()
//                        + File.separator + "Backup" + File.separator + "Message";
//            } else {
//                file = StorageManagerWrapper.getInstance(context).getExternalSdDirectory()
//                        + File.separator + "Backup" + File.separator + "Message";
//            }
//        } catch (Exception e) {
//            return null;
//        }
//        return createDir(file);
//    }
//
//    private static String createDir(String destDirName) {
//        String ret;
//        File dir = new File(destDirName);
//        if (dir.exists()) {
//            ret = dir.getPath();
//            return ret;
//        }
//        // 创建单个目录
////        if (dir.mkdirs()) {
////            ret = dir.getPath();
////        } else {
////            ret = dir.getPath();
////        }
//        dir.mkdirs();
//        ret = dir.getPath();
//        return ret;
//    }
//
//    private static void copyDir(String srcPath, String desPath) throws Exception {
//        LogUtil.getInstance().d(TAG, "copyDir srcPath: " + srcPath + ", ndesPath: " + desPath);
//        File file = new File(srcPath);
//        String[] filePath = file.list();
//
//        if (!(new File(desPath)).exists()) {
//            (new File(desPath)).mkdir();
//        }
//
//        if (filePath != null) {
//            for (int i = 0; i < filePath.length; i++) {
//                File tempFile = new File(srcPath + file.separator + filePath[i]);
//                LogUtil.getInstance().d(TAG, "copyDir tempFile: " + tempFile);
//                if (tempFile.isDirectory()) {
//                    copyDir(srcPath + file.separator + filePath[i], desPath + file.separator + filePath[i]);
//                } else if (new File(srcPath + file.separator + filePath[i]).isFile()) {
//                    com.vivo.mms.smart.provider.AESUtils.decryptionFile(srcPath + file.separator + filePath[i], desPath + file.separator + filePath[i]);
//                }
//            }
//        }
//        LogUtil.getInstance().d(TAG, "copyDir end srcPath: " + srcPath);
//    }
//
//    private static void copyFile(String oldPath, String newPath) throws Exception {
//        File oldFile = new File(oldPath);
//        File file = new File(newPath);
//        FileInputStream in = null;
//        FileOutputStream out = null;
//
//        try {
//            in = new FileInputStream(oldFile);
//            out = new FileOutputStream(file);
//            byte[] buffer=new byte[1024];
//            int byteread = 0;
//            while((byteread = in.read(buffer)) != -1){
//                out.write(buffer, 0, byteread);
//            }
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                    in = null;
//                }
//            } catch (IOException ioe1) {
//                LogUtil.getInstance().e(TAG, "copyFile close FileInputStream IOException: " + ioe1);
//            }
//            try {
//                if(out != null){
//                    out.close();
//                    out = null;
//                }
//            } catch (IOException ioe2) {
//                LogUtil.getInstance().e(TAG, "copyFile close FileOutputStream IOException: " + ioe2);
//            }
//        }
//    }
//
//    private static void deleteDir(final String pPath) {
//        File dir = new File(pPath);
//        deleteDirWithFile(dir);
//    }
//
//    private static void deleteDirWithFile(File dir) {
//        if (dir == null || !dir.exists() || !dir.isDirectory()) {
//            return;
//        }
//        File[] listFiles = dir.listFiles();
//        if (listFiles != null) {
//            for (File file : listFiles) {
//                if (file != null) {
//                    if (file.isFile()) {
//                        file.delete(); // 删除所有文件
//                    } else if (file.isDirectory()) {
//                        deleteDirWithFile(file); // 递规的方式删除文件夹
//                    }
//                }
//            }
//        }
//        dir.delete();// 删除目录本身
//    }
//
//    private void restoreMmssmsDb() {
//        String path = getStorePath(mContext, 0);
//        File srcFile = new File(path + File.separator + ".temp");
//        File desFile  = mContext.getDatabasePath(DATABASE_NAME);
//
//        boolean isRestoreSuccess = false;
//        try {
//            LogUtil.getInstance().i(TAG, "restoreMmssmsDb old file exist: " + srcFile.exists()
//                    + ", new file exist: " + desFile.exists());
//            if (srcFile.exists()) {
//                synchronized (this) {
//                    if (desFile.exists()) {
//                        boolean hasNewMessage = false;
//                        Cursor c = null;
//                        try {
//                            c = getReadableDatabase().rawQuery("SELECT _id FROM threads", null);
//                            if (c != null && c.getCount() > 0) {
//                                hasNewMessage = true;
//                                LogUtil.getInstance().i(TAG, "restoreMmssmsDb old db has message count: " + c.getCount());
//                            } else {
//                                hasNewMessage = false;
//                                LogUtil.getInstance().i(TAG, "restoreMmssmsDb old db has no message.");
//                            }
//                        } catch (Exception e) {
//                            LogUtil.getInstance().e(TAG, "restoreMmssmsDb read old db exception: " + e);
//                        } finally {
//                            if (c != null) {
//                                c.close();
//                            }
//                        }
//
//                        //数据库中有收到新消息，停止恢复数据库的方案
//                        if (hasNewMessage) {
//                            SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//                            editor.putBoolean(MmsSettingProvider.BACKUP_DB_FAILED, true);
//                            editor.putLong(MmsSettingProvider.RESTORE_DB_FAILED_TIME, System.currentTimeMillis());
//                            editor.apply();
//                            isRestoreSuccess = true;
//                            return;
//                        }
//                    }
//                    //telephonyprovider数据库备份失败，停止恢复数据库的方案
//                    SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                    boolean isBackupFailed = preferences.getBoolean(MmsSettingProvider.BACKUP_DB_FAILED, false);
//                    LogUtil.getInstance().i(TAG, "restoreMmssmsDb local failed result isBackupFailed: " + isBackupFailed);
//                    if (!isBackupFailed) {
//                        Bundle extras = new Bundle();
//                        extras.putString("type", "getBoolean");
//                        try {
//                            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://vivo-mms-setting"),
//                                    "method_get_value", MmsSettingProvider.BACKUP_DB_FAILED, extras);
//                            if (bundle != null) {
//                                isBackupFailed = bundle.getBoolean(MmsSettingProvider.BACKUP_DB_FAILED, false);
//                            }
//                        }catch (Exception e) {
//                            LogUtil.getInstance().e(TAG, "restoreMmssmsDb get backupFailed exception: " + e);
//                        }
//                    }
//                    LogUtil.getInstance().i(TAG, "restoreMmssmsDb last failed result isBackupFailed: " + isBackupFailed);
//                    if (isBackupFailed) {
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//                        editor.putBoolean(MmsSettingProvider.BACKUP_DB_FAILED, true);
//                        editor.putLong(MmsSettingProvider.RESTORE_DB_FAILED_TIME, System.currentTimeMillis());
//                        editor.apply();
//                        isRestoreSuccess = true;
//                        return;
//                    }
//                    if (desFile.exists()) {
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb old file delete begin.");
//                        desFile.delete();
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb old file delete end.");
//                    }
//                    try {
//                        createDir(desFile.getParent());
//                        desFile.createNewFile();
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb new db file create begin.");
//                        AESUtils.decryptionFile(srcFile.getPath(), desFile.getPath());
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb new db file create end.");
//                    } finally {
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb old helper close begin.");
//                        if (sDeInstance != null) {
//                            sDeInstance.close();
//                        }
//                        if (sCeInstance != null) {
//                            sCeInstance.close();
//                        }
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb old helper close end.");
//                    }
//                }
//                LogUtil.getInstance().i(TAG, "restoreMmssmsDb success middle mProviders size: " + mProviders.size());
////                synchronized (mProviders) {
////                    sDeInstance = null;
////                    sCeInstance = null;
////                    if (!mProviders.isEmpty()) {
////                        for(ContentProvider p : mProviders) {
////                            if (p instanceof MmsProvider) {
////                                ((MmsProvider)p).updateOpenHelper();
////                            } else if (p instanceof MmsSmsProvider) {
////                                ((MmsSmsProvider)p).updateOpenHelper();
////                            } else if (p instanceof SmsProvider) {
////                                ((SmsProvider)p).updateOpenHelper();
////                            } else if (p instanceof SmsRecogProvider) {
////                                ((SmsRecogProvider)p).updateOpenHelper();
////                            }
////                        }
////                        mProviders.clear();
////                    }
////                }
//                isRestoreSuccess = true;
//            } else {
//                SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                boolean isBackupFailed = preferences.getBoolean(MmsSettingProvider.BACKUP_DB_FAILED, false);
//                LogUtil.getInstance().i(TAG, "restoreMmssmsDb local backupFailed flag: " + isBackupFailed);
//                if (!isBackupFailed) {
//                    Bundle extras = new Bundle();
//                    extras.putString("type", "getBoolean");
//                    try {
//                        Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://vivo-mms-setting"),
//                                "method_get_value", MmsSettingProvider.BACKUP_DB_FAILED, extras);
//                        if (bundle != null) {
//                            isBackupFailed = bundle.getBoolean(MmsSettingProvider.BACKUP_DB_FAILED, false);
//                        }
//                    }catch (Exception e) {
//                        LogUtil.getInstance().e(TAG, "restoreMmssmsDb get backupFailed exception: " + e);
//                    }
//                    LogUtil.getInstance().i(TAG, "restoreMmssmsDb remote backupFailed flag: " + isBackupFailed);
//                    if (!isBackupFailed) {
//                        isRestoreSuccess = true;
//                        SharedPreferences sp = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//                        editor.apply();
//                        LogUtil.getInstance().i(TAG, "restoreMmssmsDb no temp file, no any failed flag, so set restore finish.");
//                        return;
//                    }
//                }
//            }
//            LogUtil.getInstance().i(TAG, "restoreMmssmsDb copy databases success end.");
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "restoreMmssmsDb copy dataBase to SD error" + e);
//        }finally {
//            try {
//                if (srcFile.exists()) {
//                    LogUtil.getInstance().i(TAG, "restoreMmssmsDb delete temp file : " + srcFile);
//                    srcFile.delete();
//                }
//            } catch (Exception ex) {
//                LogUtil.getInstance().e(TAG, "restoreMmssmsDb finally delete old database exception: " + ex);
//            }
//            restoreUserOldBehavior();
//            LogUtil.getInstance().i(TAG, "restoreMmssmsDb copy databases finally : " + isRestoreSuccess);
//            if (!isRestoreSuccess) {
//                SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//                editor.putBoolean(MmsSettingProvider.BACKUP_DB_FAILED, true);
//                editor.putLong(MmsSettingProvider.RESTORE_DB_FAILED_TIME, System.currentTimeMillis());
//                editor.apply();
//                return;
//            }
//        }
//
//        //restore mms parts
//        String srcPath = path + File.separator + "app_parts";
//        String desPath = desFile.getParentFile().getParent() + File.separator + "app_parts";
//        File appParts = new File(srcPath);
//        try {
//
//            LogUtil.getInstance().i(TAG, "restore mms parts file exist: " + appParts.exists());
//            if (appParts.exists()) {
//                copyDir(srcPath, desPath);
//            }
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "restore mms parts file exception: " + e);
//        } finally {
//            try {
//                deleteDir(srcPath);
//            } catch (Exception ex) {
//                LogUtil.getInstance().e(TAG, "restore mms delete parts file exception: " + ex);
//            }
//        }
//
//        //update _data column, such as from "/data/user_de/0/com.android.providers.telephony/app_parts/PART_1526894705000" to
//        // "/data/user_de/0/com.android.mms/app_parts/PART_1526894705000"
//        Cursor c = null;
//        try {
//            c = getInstanceForCe(mContext).getReadableDatabase().rawQuery("SELECT _id, _data FROM part", null);
//            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
//                LogUtil.getInstance().i(TAG, "query part _data count: " + c.getCount());
//                do {
//                    int _id = c.getInt(c.getColumnIndex("_id"));
//                    String _data = c.getString(c.getColumnIndex("_data"));
//                    LogUtil.getInstance().d(TAG, "query part _data : " + _data + " , id: " + _id);
//                    if (!TextUtils.isEmpty(_data)) {
//                        _data = _data.replace("com.android.providers.telephony", "com.android.mms");
//                        ContentValues values = new ContentValues();
//                        values.put("_data", _data);
//                        String where = "_id = " + _id;
//                        getInstanceForCe(mContext).getWritableDatabase().update(MmsProvider.TABLE_PART, values, where, null);
//                    }
//                } while (c.moveToNext());
//            } else {
//                LogUtil.getInstance().i(TAG, "query part _data column cursor is null or count is 0. cursor: " + c);
//            }
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "update parts _data exception: " + e);
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
//
//        SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//        editor.apply();
//        LogUtil.getInstance().i(TAG, "restoreMmssmsDb success end.");
//
//        //following is testing code
//        Cursor cc = null;
//        try {
//            cc = getInstanceForCe(mContext).getReadableDatabase().rawQuery("SELECT _id FROM threads", null);
//            if (cc != null && cc.getCount() > 0) {
//                LogUtil.getInstance().i(TAG, "restoreMmssmsDb new db has message count: " + cc.getCount());
//            } else {
//                LogUtil.getInstance().i(TAG, "restoreMmssmsDb new db has no message.");
//            }
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "restoreMmssmsDb read new db exception: " + e);
//        } finally {
//            if (cc != null) {
//                cc.close();
//            }
//        }
//    }
//
//    private void restoreUserOldBehavior() {
//        //restore user default behaviors
//        try {
//            SharedPreferences sp = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_XML_SETTING, Context.MODE_PRIVATE);
//            if (sp.contains(SMS_RECOGNITION) || sp.contains(SMS_CARD) || sp.contains(VERIFY_CODE_PROTECTED)) {
//                LogUtil.getInstance().i(TAG, "new app has contained setting_key_value key-value, so not need restore old behavior!");
//                return;
//            }
//            Bundle extras = new Bundle();
//            extras.putString("type", "getAll");
//            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://vivo-mms-setting"),
//                    "method_get_value", "getAll", extras);
//            LogUtil.getInstance().v(TAG, "bundle: " + bundle);
//            if (bundle != null) {
//                String config = bundle.getString(KEY_SMS_CARD_CONFIG, "");
//                boolean smsRecognition = bundle.getBoolean(SMS_RECOGNITION,
//                        SFeatureContorl.isForCmccTest());
//                boolean noticeMerge = bundle.getBoolean(NOTICE_MERGE, false);
//                boolean smsCard = bundle.getBoolean(SMS_CARD, true);
//                boolean verifyCode = bundle.getBoolean(VERIFY_CODE_PROTECTED, true);
//                boolean websiteCheck = bundle.getBoolean(ISettingsKey.WEBSITE_CHECKED_CONTROL, true);
//                LogUtil.getInstance().i(TAG, String.format("config sSmsRecognition:%b ,sNoticeMerge:%b , sSmsCard: %b , mVerifyCode: %b, websiteCheck: %b",
//                        smsRecognition, noticeMerge, smsCard, verifyCode, websiteCheck));
//
//                SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_XML_SETTING, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                if (!TextUtils.isEmpty(config)) {
//                    editor.putString(KEY_SMS_CARD_CONFIG, config);
//                }
//                editor.putBoolean(NOTICE_MERGE, noticeMerge);
//                editor.putBoolean(SMS_CARD, smsCard);
//                editor.putBoolean(VERIFY_CODE_PROTECTED, verifyCode);
//                editor.putBoolean(SMS_RECOGNITION, smsRecognition);
//                if (LocaleUtils.isInternationalVersion()) {
//                    editor.putBoolean(ISettingsKey.WEBSITE_CHECKED_CONTROL, websiteCheck);
//                }
//                editor.apply();
//            }
//        }catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "restoreMmssmsDb restore user behavior ex:" + e);
//        }
//    }
//
//    public void checkAndRestoreDb(boolean isBackupOk) {
//        boolean restoreSuccessed = isRestoredSuccessed();
//		LogUtil.getInstance().i(TAG, "checkAndRestoreDb restoreSuccessed: " + restoreSuccessed + ", sIsRestored: " + sIsRestored + ", isBackupOk: " + isBackupOk);
//        if (restoreSuccessed || sIsRestored) {
//            LogUtil.getInstance().i(TAG, "checkAndRestoreDb restore ok or restoring, so ignore restore.");
//            return;
//        }
//        LogUtil.getInstance().i(TAG, "checkAndRestoreDb isBackupOK: " + isBackupOk);
//        if (!isBackupOk) {
//            String path = getStorePath(mContext, 0);
//            File srcFile = new File(path + File.separator + ".temp");
//            if (!srcFile.exists()) {
//                if (mRestoreHandler != null) {
//                    mRestoreHandler.sendEmptyMessage(RestoreHandler.RESTORE_USER_BEHAVIOR);
//                    mRestoreHandler.sendEmptyMessageDelayed(RestoreHandler.UPDATE_RESTORE_DATA_FINISHED,
//                            RestoreHandler.RESTORE_DATA_FINISHED_DELAY_TIME);
//                } else {
//                    LogUtil.getInstance().i(TAG, "checkAndRestoreDb source file does not exist, handler is null, so ignore restore.");
//                }
//                LogUtil.getInstance().i(TAG, "checkAndRestoreDb source file does not exist, so restore user old behavior firstly.");
//                return;
//            }
//        }
//        if (mRestoreHandler != null) {
//            mRestoreHandler.removeMessages(RestoreHandler.UPDATE_RESTORE_DATA_FINISHED);
//            LogUtil.getInstance().i(TAG, "checkAndRestoreDb remove UPDATE_RESTORE_DATA_FINISHED.");
//        }
//        new Thread(new Runnable() {
//            public void run() {
//                sIsRestored = true;
//                restoreMmssmsDb();
//            }
//        }).start();
//    }
//
//    private boolean isRestoredSuccessed() {
//        SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//        boolean ret = preferences.getBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, false);
//        if (!ret) {
//            preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_XML_SETTING, Context.MODE_PRIVATE);
//            ret = preferences.getBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, false);
//        }
//        return ret;
//    }
//
//    public void addProvider(ContentProvider p) {
//        if (isRestoredSuccessed()) {
//            LogUtil.getInstance().d(TAG, "addProvider ignore add provider: " + p);
//            return;
//        }
//        synchronized (mProviders) {
//            if (!mProviders.contains(p)) {
//                mProviders.add(p);
//            }
//        }
//    }
//    /**
//     * Returns a singleton helper for the combined MMS and SMS database in device encrypted storage.
//     */
//    /* package */ static synchronized MmsSmsDatabaseHelper getInstanceForDe(Context context) {
//        if (sDeInstance == null) {
//            sIsCeMode = false;
//            sDeInstance = new MmsSmsDatabaseHelper(ProviderUtil.getDeviceEncryptedContext(context));
//        }
//        return sDeInstance;
//    }
//
//    /**
//     * Returns a singleton helper for the combined MMS and SMS database in device encrypted storage.
//     */
//    /* package */ static synchronized MmsSmsDatabaseHelper getInstanceForDe(Context context, boolean isCeMode) {
//        if (sDeInstance == null) {
//            sIsCeMode = isCeMode;
//            sDeInstance = new MmsSmsDatabaseHelper(ProviderUtil.getDeviceEncryptedContext(context));
//        }
//        return sDeInstance;
//    }
//
//    private static boolean isFileEncryptedNativeOrEmulated() {
//        boolean ret = false;
//        try {
//            Class cls = StorageManager.class;
//            Method method = cls.getDeclaredMethod("isFileEncryptedNativeOrEmulated");
//            ret = (boolean) method.invoke(null);
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "isFileEncryptedNativeOrEmulated exception : " + e.getMessage());
//        } catch (Error error) {
//            LogUtil.getInstance().e(TAG, "isFileEncryptedNativeOrEmulated error : " + error.getMessage());
//        } catch (Throwable throwable) {
//            LogUtil.getInstance().e(TAG, "isFileEncryptedNativeOrEmulated throwable : " + throwable.getMessage());
//        }
//        return ret;
//    }
//
//    public static synchronized MmsSmsDatabaseHelper getDatabaseHelper(Context context) {
//        return getInstanceForCe(context);
//    }
//
//    /**
//     * Returns a singleton helper for the combined MMS and SMS database in credential encrypted
//     * storage. If FBE is not available, use the device encrypted storage instead.
//     */
//    /* package */ static synchronized MmsSmsDatabaseHelper getInstanceForCe(Context context) {
//        if (sCeInstance == null) {
//            if (isFileEncryptedNativeOrEmulated()) {
//                sIsCeMode = true;
//                Context contextCredential = ProviderUtil.getCredentialEncryptedContext(context);
//                if (contextCredential != null) {
//                    sCeInstance = new MmsSmsDatabaseHelper(contextCredential);
//                }
//            } else {
//                sCeInstance = getInstanceForDe(context, true);
//            }
//        }
//        return sCeInstance;
//    }
//
//    /**
//     * added by lengxibo for 批量数据优化. 2014.5.14
//     * @param db
//     * @param thread_id
//     * @param isBatch 是否是批量操作
//     */
//    public static void updateThread(SQLiteDatabase db, long thread_id, boolean isBatch) {
//        if (thread_id < 0) {
//            updateAllThreads(db, null, null, isBatch, false);
//            return;
//        }
//        LogUtil.getInstance().d(TAG, "1--------->updateThread,thread: " +thread_id);
//        //if it's a wappush thread, it doesn't need to be updated here;
//
//        // Delete the row for this thread in the threads table if
//        // there are no more messages attached to it in either
//        // the sms or pdu tables.
//		int rows = 0;
//		String threadId = String.valueOf(thread_id);
//		rows = db
//				.delete("threads",
//						"_id = ? AND _id NOT IN"
//								+ " (SELECT thread_id FROM sms where thread_id = ? "
//								+ " UNION SELECT thread_id FROM pdu where thread_id = ? "
//                                + " UNION SELECT thread_id FROM push_mms where thread_id = ? "
//                                + " UNION SELECT thread_id FROM im_message where thread_id = ? "
//                                + ")",
//						new String[] { threadId, threadId, threadId, threadId, threadId});
//		if (rows > 0) {
//            // If this deleted a row, let's remove orphaned canonical_addresses and get outta here
//            if (!isBatch) {
//            	removeOrphanedAddresses(db);
//            }
//            return;
//        }
//		updateThreadDb(db,thread_id);
//
//    }
//
//    public static void updateThread(SQLiteDatabase db, long thread_id) {
//        if (thread_id < 0) {
//            updateAllThreads(db, null, null, false, false);  //modified by lengxibo for 批量数据优化. 2014.5.14
//            return;
//        }
//        LogUtil.getInstance().d(TAG, "1--------->updateThread,thread: " +thread_id);
//        //if it's a wappush thread, it doesn't need to be updated here;
//
//        // Delete the row for this thread in the threads table if
//        // there are no more messages attached to it in either
//        // the sms or pdu tables.
//		int rows = 0;
//        rows = db.delete("threads",
//                "_id = ? AND _id NOT IN" +
//                        "          (SELECT thread_id FROM sms " +
//                        "           UNION SELECT thread_id FROM pdu " +
//                        "           UNION SELECT thread_id FROM push_mms" +
//                        "           UNION SELECT thread_id FROM im_message" +
//                ")",
//                new String[] {
//                    String.valueOf(thread_id)
//                });
//		if (rows > 0) {
//            // If this deleted a row, let's remove orphaned canonical_addresses and get outta here
//            removeOrphanedAddresses(db);
//            return;
//        }
//		updateThreadDb(db,thread_id);
//    }
//
//    public static void updateThread(final Context context,SQLiteDatabase db, long thread_id) {
//    	if (thread_id < 0) {
//    		return;
//    	}
//		int rows = 0;
//        rows = db.delete("threads",
//                "_id = ? AND _id NOT IN" +
//                        "          (SELECT thread_id FROM sms " +
//                        "           UNION SELECT thread_id FROM pdu" +
//                        "           UNION SELECT thread_id FROM push_mms" +
//                        "           UNION SELECT thread_id FROM im_message" +
//                        ")",
//                new String[] {
//                    String.valueOf(thread_id)
//                });
//		if (rows > 0) {
//            // If this deleted a row, let's remove orphaned canonical_addresses and get outta here
//            removeOrphanedAddresses(db);
//            ContentResolver cr = context.getContentResolver();
//            cr.notifyChange(MmsSms.CONTENT_URI, null);
//            return;
//        }
//		updateThreadDb(db,thread_id);
//    }
//
//    private static void updateThreadDb(SQLiteDatabase db, long thread_id){
//		LogUtil.getInstance().d(TAG, "2--------->updateThreadDb,thread: " +thread_id);
//		//纠正threads表中的message_mode
//		db.execSQL(
//				"UPDATE threads SET message_mode = " +
//				"	CASE " +
//				"	WHEN " +
//						"	(0 IN (SELECT message_mode FROM sms WHERE "+ thread_id + " = thread_id) OR" +
//						"  0 IN ( SELECT message_mode FROM pdu WHERE "+ thread_id + " = thread_id) )" +
//					"	THEN 0 " +
//				"	ELSE 2 " +
//				"	END " +
//				"WHERE threads._id = " + thread_id +";");
//		//LogUtil.getInstance().d(TAG, "3--------->updateThreadDb,thread: " +thread_id);
//
//        // Update the message count in the threads table as the sum
//        // of all messages in both the sms and pdu tables.
//        db.execSQL(
//            "  UPDATE threads SET message_count = " +
//            "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//            "      ON threads._id = " + Sms.THREAD_ID +
//            "      WHERE " + Sms.THREAD_ID + " = " + thread_id +
//            "        AND sms." + Sms.TYPE + " != 3) + " +
//            "      (SELECT COUNT(im_message._id) FROM im_message LEFT JOIN threads " +
//            "        ON threads._id = im_message.thread_id WHERE im_message.thread_id = " + thread_id +") + " +
//            "     (SELECT COUNT(pdu._id) FROM pdu LEFT JOIN threads " +
//            "      ON threads._id = " + Mms.THREAD_ID +
//            "      WHERE " + Mms.THREAD_ID + " = " + thread_id +
//            "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//            "        AND " + Mms.MESSAGE_BOX + " != 3) " +
//            "  WHERE threads._id = " + thread_id + ";");
//        //add by lk
//        //LogUtil.getInstance().d(TAG, "4--------->updateThreadDb,thread: " +thread_id);
//        db.execSQL("UPDATE threads SET unreadcount ="+
//                "     (SELECT COUNT(sms._id) FROM sms LEFT JOIN threads " +
//                "      ON threads._id = " + Sms.THREAD_ID +
//                "      WHERE " + Sms.THREAD_ID + " = " + thread_id +
//                "        AND sms." + Sms.TYPE + " != 3" +
//                "        AND sms." + Sms.READ + " = 0) + " +
//                "      (SELECT COUNT(im_message._id) FROM im_message LEFT JOIN threads " +
//                "       ON threads._id = im_message.thread_id " +
//                "       WHERE im_message.thread_id = "+ thread_id +
//                "       AND im_message.read = 0) + " +
//                "     (SELECT COUNT(pdu._id) FROM pdu  LEFT JOIN threads " +
//                "      ON threads._id = " + Mms.THREAD_ID +
//                "      WHERE " + Mms.THREAD_ID + " = "+ thread_id +
//                "        AND (m_type=132 OR m_type=130 OR m_type=128)" +
//                "        AND " + Mms.MESSAGE_BOX + " != 3" +
//                "        AND pdu." + Mms.READ + " = 0) " +
//                "  WHERE threads._id = " + thread_id + ";");
//           //end
//        // Update the date and the snippet (and its character set) in
//        // the threads table to be that of the most recent message in
//        // the thread.
//        //LogUtil.getInstance().d(TAG, "5--------->updateThreadDb,thread: " +thread_id);
//        //将date,snippet,snippet_cs的更新合并，节省时间 by liuxinglin
//        String sql = " SELECT d,s,s_cs ,s_id,t FROM threads," +
//                                "        (SELECT date * 1000 AS d, sub AS s, sub_cs AS s_cs, sub_id AS s_id,time AS t,thread_id,message_mode AS m_mode FROM pdu WHERE (m_type =128 OR m_type = 130 OR m_type = 132)" +
//                                "         UNION ALL SELECT date AS d,  body AS s,0 AS s_cs,  sub_id AS s_id,time AS t,thread_id,message_mode AS m_mode FROM sms " +
//                                "         UNION ALL SELECT date AS d,  body AS s,0 AS s_cs,  sub_id AS s_id,time AS t,thread_id,0 AS m_mode FROM im_message)" +
//                                "     WHERE threads._id = "+thread_id+" AND thread_id = " + thread_id +" ORDER BY d DESC LIMIT 1";
//
//
//        Cursor cc = db.rawQuery(sql, null);
//        if (cc != null) {  //added by lengxibo for 空指针异常. 2014.5.16
//            try {
//                if(cc.getCount() > 0){
//                cc.moveToFirst();
//                String date = cc.getString(0);
//                String snippet =cc.getString(1);
//                if(snippet!= null)
//                {
//                	//modified by fuleilei for 恢复的短信的时间和备份短信前的时间不一致 2013.10.8
//                	snippet = (DatabaseUtils.sqlEscapeString(snippet));//(trimIllegalChar(snippet)));//@ExportTeam modify for PD1401F_EX [B141105-546]
//                	//snippet= "'"+snippet+"'";
//                	//modified end fuleilei
//                	LogUtil.getInstance().v(TAG,"when the snippet is not null,come here set to string");
//                }
//                LogUtil.getInstance().v(TAG,"update thread snippet ="+snippet);
//                String snippet_cs =cc.getString(2);
//                int simId = cc.getInt(3);
//                String time = cc.getString(4);
//                db.execSQL(" UPDATE threads "+
//                                        " SET " +
//                                        "       date = " + date +",  snippet = " +snippet + ",snippet_cs = "+snippet_cs +
//                                        ", sub_id  = "+ simId + ","+
//                                        "  time = " +time + /*","+
//                                        "  has_attachment = " +
//                                        "   CASE " +
//                                        "    (SELECT COUNT(*) FROM part JOIN pdu " +
//                                        "       WHERE pdu.thread_id = threads._id " +
//                                        "       AND part.ct != 'text/plain' AND part.ct != 'application/smil' " +
//                                        "       AND part.mid = pdu._id  AND ("+THREADS_MESSAGE_MODE+" = "+MMS_MESSAGE_MODE+" ))" +
//                                        "   WHEN 0 THEN 0 " +
//                                        "   ELSE 1 " +
//                                        "   END " +*/
//                                        "  WHERE threads._id = " + thread_id + ";"     );
//                }
//            } catch (Exception e) {
//                LogUtil.getInstance().e(TAG, "Update threads id= "+thread_id+" occur error: "+VLog.getStackTraceString(e));
//            }finally{
//                cc.close();
//                cc = null;
//            }
//        }
//        //LogUtil.getInstance().d(TAG, "6--------->updateThreadDb,thread:  " +thread_id);
//        // Update the error column of the thread to indicate if there
//        // are any messages in it that have failed to send.
//        // First check to see if there are any messages with errors in this thread.
//        // Modify begin for RCS
//        String query;
//        if (SFeatureContorl.isRcsVersion()) {
//            query = "SELECT thread_id FROM sms, threads  WHERE (sms.type=" +
//                    Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED +
//                    " OR (sms.rcs_msg_state is not null AND sms.rcs_msg_state=" + Constants.MessageConstants.CONST_STATUS_SEND_FAIL + "))" +
//                    " AND thread_id = " + thread_id + "  AND threads._id = thread_id" +
//                    " LIMIT 1";
//        } else {
//            query = "SELECT thread_id FROM sms, threads  WHERE sms.type=" +
//                    Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED +
//                    " AND thread_id = " + thread_id + "  AND threads._id = thread_id" +
//                    " LIMIT 1";
//        }
//        // Modify end for RCS
//        int setError = 0;
//        Cursor c = db.rawQuery(query, null);
//        if (c != null) {
//            try {
//                setError = c.getCount();    // Because of the LIMIT 1, count will be 1 or 0.
//                if(setError == 0){
//                    // select all _id from pdu of the thread
//                    String mms_query = "SELECT pdu._id FROM pdu,threads WHERE thread_id = " + thread_id +
//                        " AND m_type = " + PduHeaders.MESSAGE_TYPE_SEND_REQ+"  AND threads._id = thread_id";
//                    Cursor c_mms = db.rawQuery(mms_query, null);
//                    if ( c_mms != null) {
//                        try {
//                            if (c_mms.moveToFirst()) {
//                                int count = c_mms.getCount();
//                                Cursor c_pending = null;
//                                // for all the _id, check the err_type in pending_msgs
//                                for (int i = 0; i < count; ++i) {
//                                    int msg_id = c_mms.getInt(0);
//                                    String pending_query = "SELECT err_type FROM pending_msgs WHERE err_type >= " + MmsSms.ERR_TYPE_GENERIC_PERMANENT + " AND msg_id = " + msg_id ;
//                                    c_pending = db.rawQuery(pending_query, null);
//                                    if (c_pending != null) {
//                                        try {
//                                            if (c_pending.getCount() != 0) {
//                                                setError = 1;
//                                                break;
//                                             }
//                                        } finally {
//                                           c_pending.close();
//                                        }
//                                    }
//                                    c_mms.moveToNext();
//                                }
//                            }
//
//                        }finally {
//                            c_mms.close();
//                        }
//                    }
//                }
//            } finally {
//                c.close();
//                c= null;
//            }
//        }
//        // What's the current state of the error flag in the threads table?
//        String errorQuery = "SELECT error FROM threads WHERE _id = " + thread_id;
//        c = db.rawQuery(errorQuery, null);
//        if (c != null) {  //modified by lengxibo for 空指针异常. 2014.5.16
//            try {
//                if (c.getCount() > 0 && c.moveToNext()) {
//                    int curError = c.getInt(0);
//                    if (curError != setError) {
//                        // The current thread error column differs, update it.
//                        db.execSQL("UPDATE threads SET error=" + setError +
//                                " WHERE _id = " + thread_id);
//                    }
//                }
//            } finally {
//                c.close();
//            }
//        }
//        //LogUtil.getInstance().d(TAG, "7--------->updateThreadDb,thread: " +thread_id);
//        // VIVO yantiefang add for RCS begin
//        if (SFeatureContorl.isRcsVersion()) {
//            String groupIdQuery = "SELECT _id, thread_id FROM rcs_groupchat WHERE thread_id = " + thread_id;
//            c = db.rawQuery(groupIdQuery, null);
//            if (c != null) {
//                try {
//                    if (c.getCount() > 0 && c.moveToNext()) {
//                        int groupId = c.getInt(0);
//                        if (groupId > 0) {
//                            db.execSQL("UPDATE threads SET rcs_group_id=" + groupId +
//                                    " WHERE _id = " + thread_id);
//                        }
//                    }
//                } finally {
//                    c.close();
//                }
//            }
//        }
//        // VIVO yantiefang add for RCS end
//
//        String msgCategoryQuery = " SELECT d, msg_category, rcs_msg_type FROM threads," +
//                " (SELECT date * 1000 AS d, " + IImData.SMS_MMS + " AS msg_category, -1 AS rcs_msg_type, thread_id FROM pdu WHERE (m_type =128 OR m_type = 130 OR m_type = 132)" +
//                " UNION ALL SELECT date AS d, " + IImData.SMS_MMS + " AS msg_category, rcs_msg_type, thread_id FROM sms " +
//                " UNION ALL SELECT date AS d, " + IImData.IM_MSG + " AS msg_category, -1 AS rcs_msg_type, thread_id FROM im_message)" +
//                " WHERE threads._id = " + thread_id + " AND thread_id = " + thread_id + " ORDER BY d DESC LIMIT 1";
//
//        Cursor cursor = db.rawQuery(msgCategoryQuery, null);
//        if (cursor != null) {
//            try {
//                if (cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    String date = cursor.getString(0);
//                    int msgCategory = cursor.getInt(1);
//                    if (msgCategory == IImData.SMS_MMS) {
//                        int rcsMsgType = cursor.getInt(2);
//                        if (rcsMsgType > CONST_MESSAGE_SMS) {
//                            msgCategory = IImData.RCS_MSG;
//                        }
//                    }
//                    LogUtil.getInstance().d(TAG, "update thread msgCategory =" + msgCategory);
//                    db.execSQL(" UPDATE threads " +
//                            " SET " +
//                            ImProviderConstants.ThreadColumns.COL_LAST_MSG_CATEGORY + " = " + msgCategory +
//                            "  WHERE threads._id = " + thread_id + ";");
//                }
//            } catch (Exception e) {
//                LogUtil.getInstance().e(TAG, "Update threads id= " + thread_id + " occur error: " + VLog.getStackTraceString(e));
//            } finally {
//                cursor.close();
//                cursor = null;
//            }
//        }
//    }
//
//    public static void updateAllThreads(SQLiteDatabase db, String where, String[] whereArgs, boolean isBatch, boolean fromSms) {
//        String smsWhere = "";
//        if (where == null) {
//            where = "";
//        } else {
//            if (fromSms) {
//                smsWhere = where;
//                where = "";
//            } else {
//                where = "WHERE (" + where + ")";
//            }
//        }
//        String query = "SELECT DISTINCT _id FROM threads WHERE _id IN " +
//                       "(SELECT DISTINCT thread_id FROM sms " + smsWhere +
//                       "UNION SELECT DISTINCT thread_id FROM im_message "+ where +
//                       "UNION SELECT DISTINCT thread_id FROM pdu " + where + ")";
//        Cursor c = null;
//        try {
//            c = db.rawQuery(query, whereArgs);
//            if (c != null) {
//                while (c.moveToNext()) {
//                    updateThread(db, c.getInt(0), isBatch);  //modified by lengxibo for 批量数据优化. 2014.5.14
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.getInstance().e(TAG, "updateAllThreads exception" + e.toString());
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
//
//        // TODO: there are several db operations in this function. Lets wrap them in a
//        // transaction to make it faster.
//        // remove orphaned threads
//
//        db.delete("threads",
//                "_id NOT IN (SELECT DISTINCT thread_id FROM sms " +
//                        "UNION SELECT DISTINCT thread_id FROM im_message " +
//                        "UNION SELECT DISTINCT thread_id FROM push_mms " +
//                        "UNION SELECT DISTINCT thread_id FROM pdu)", null);
//
//        // remove orphaned canonical_addresses
//        removeOrphanedAddresses(db);
//    }
//
//  //生成threadId的时候是先写canonical_addresses表，然后再写thread表
//    //而removeOrphanedAddresses 是先查询thread表，在删除canonical_addresses表
//    //所以可能会删除正在入的thread表中canonical_addresses记录
//    //采用线程同步的方法容易导致线程阻塞，而canonical_addresses也不需要及时删除，故用一个标志位来做简单的控制。
//    public static boolean sInSertingThread = false;
//
//    public static void removeOrphanedAddresses(SQLiteDatabase db) {
//    	LogUtil.getInstance().v(TAG,"-------removeOrphanedAddresses---");
//    	if(sInSertingThread){
//    		return;
//    	}
//        final Cursor c = db.rawQuery("SELECT DISTINCT recipient_ids FROM threads WHERE v_address_type!=2", null);
//        final StringBuilder recipientIds = new StringBuilder();
//        final String separator = ",";
//        try {
//            if (c != null && c.moveToFirst()) {
//                do {
//                	if(sInSertingThread){
//                		return;
//                	}
//                    String id = c.getString(0);
//                    if (!TextUtils.isEmpty(id)) {
//                        id = id.trim();
//                        if (!TextUtils.isEmpty(id)) {
//                            recipientIds.append(id.replaceAll(" ", separator));
//                            recipientIds.append(separator);
//                        }
//                    }
//                } while (c.moveToNext());
//            }
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
//        String ids = recipientIds.toString();
//        if (!TextUtils.isEmpty(ids) && ids.endsWith(separator)) {
//            ids = ids.substring(0, ids.lastIndexOf(separator));
//        }
//        if(!TextUtils.isEmpty(ids) && ids.startsWith(separator)){
//        	ids = ids.substring(1, ids.length());
//        }
//        LogUtil.getInstance().v(TAG, "recipient ids = " + ids);
//    	if(sInSertingThread){
//    		return;
//    	}
//        db.delete("canonical_addresses",
//                //"_id NOT IN (SELECT DISTINCT recipient_ids FROM threads)", null);
//                "_id NOT IN (" + ids + ")", null);
//    }
//
//    private static void updateThreadDate(SQLiteDatabase db, long thread_id) {
//        if (thread_id <= 0) {
//            return;
//        }
//
//        try {
//            db.execSQL(
//                    "  UPDATE threads" +
//                            "  SET" +
//                            "  date =" +
//                            "    (SELECT date FROM" +
//                            "        (SELECT date * 1000 AS date, thread_id FROM pdu" +
//                            "         UNION SELECT date, thread_id FROM sms)" +
//                            "     WHERE thread_id = " + thread_id + " ORDER BY date DESC LIMIT 1)" +
//                            "  WHERE threads._id = " + thread_id + ";");
//        } catch (Throwable ex) {
//            VLog.e(TAG, ex.getMessage(), ex);
//        }
//    }
//
//    public static void updateThreadsDate(SQLiteDatabase db, String where, String[] whereArgs) {
//        db.beginTransaction();
//        try {
//            if (where == null) {
//                where = "";
//            } else {
//                where = "WHERE (" + where + ")";
//            }
//            String query = "SELECT _id FROM threads " + where;
//            Cursor c = db.rawQuery(query, whereArgs);
//            if (c != null) {
//                try {
//                    VLog.d(TAG, "updateThread count : " + c.getCount());
//                    while (c.moveToNext()) {
//                        updateThreadDate(db, c.getInt(0));
//                    }
//                } finally {
//                    c.close();
//                }
//            }
//            db.setTransactionSuccessful();
//        } catch (Throwable ex) {
//            VLog.e(TAG, ex.getMessage(), ex);
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    /**
//     * Author：chenbin 11096671
//     * Date：2019.06.12 10:35
//     * Description: 通过message_id、groupId删除sms表中的信息
//     * @param db  数据库db
//     * @param message_id  信息的id
//     * @param groupId  群发信息的id
//     * @return 返回删除成功的rows
//     */
//    public static int deleteOneSms(SQLiteDatabase db, int message_id, String groupId) {
//    	LogUtil.getInstance().v(TAG, "---------message_id=" + message_id);
//        int thread_id = -1;
//        // Find the thread ID that the specified SMS belongs to.
//        Cursor c = db.query("sms", new String[] { "thread_id" },
//                            "_id=" + message_id, null, null, null, null);
//        try {
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    thread_id = c.getInt(0);
//                }
//            }
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
//
//        // Delete the specified message.
//        int rows;
//        if (TextUtils.isEmpty(groupId)) {
//            rows = db.delete("sms", "_id=" + message_id, null);
//        } else {
//            rows = db.delete("sms", "group_id=" + "'" + groupId + "'", null);
//        }
//        if (thread_id > 0) {
//            // Update its thread.
//            updateThread(db, thread_id);
//        }
//        return rows;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        createMmsTables(db);
//        createSmsTables(db);
//        createCommonTables(db);
//        createNoticeTables(db);   //added by lengxibo for notification mms 2017.07.29
//        createNoticeMenuTables(db);
//        createPushMmsTables(db);
//        createPushShopTables(db);
//        createSpIdToPushType(db);
//        createPushSyncTables(db);
//        createCommonTriggers(db);
//        createMmsTriggers(db);
//        createPushMmsTriggers(db);
//        //createWordsTables(db);
//        createIndices(db);
//        createQuickText(db);
//        createIRoamingTables(db);
//        createWifiPushListTables(db);
//        createFrequencyTables(db);
//        createPushSpNumTables(db);
//        createPushFindPhoneNumTables(db);
//        createPushResultPhoneNumTables(db);
//        createPushSmsTempTables(db);
//        createSmartSmsEngineConfigTables(db);
//        createSmartSmsEngineBlackListTables(db);
//        createBlockSmsTables(db);
//        createBlockSmsMessageTrigger(db);
//        createPushBindSimNumTables(db);
//        db.beginTransaction();
//        try{
//            intSecurity(db);
//            db.setTransactionSuccessful();
//        }catch(Throwable ex){
//            LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//        }finally{
//            db.endTransaction();
//        }
//      //end
//
//        // Add begin for RCS
//        RcsMessageProviderUtils.addRcsColumnForSmsTable(db);
//        RcsMessageProviderUtils.addRcsColumnForThreadTable(db);
//        createRcsNewTable(db);
//        createRcsNewTrigger(db);
//        createTempGroupChatMemeberTable(db);
//        // Add end for RCS
//        createPushMmsAliasTriggers(db);
//        createBackupSmsTables(db);
//        createBackupSmsTriggers(db);
//        createErrorCodeTables(db);
//        createBlockSmsKeywordTable(db);
//        createRcsFilePathTable(db);
//        createRcsFilePathTrigger(db);
//        createBlockNoticeKeywordTable(db);
//
//        SQLiteOpenHelperManager.getInstance(mContext).onCreate(db);
//        LogUtil.getInstance().i(TAG,"onCreate");
//
//    }
//
//    private void intSecurity(SQLiteDatabase db){
//        ContentValues values = new ContentValues();
//        values.put("_id", 1);
//        values.put("pd","");
//        values.put("enable", 0);
//        db.insert("security", "pd", values);
//    }
//
//    // When upgrading the database we need to populate the words
//    // table with the rows out of sms and part.
//    private void populateWordsTable(SQLiteDatabase db) {
//        final String TABLE_WORDS = "words";
//        {
//            Cursor smsRows = db.query(
//                    "sms",
//                    new String[] { Sms._ID, Sms.BODY },
//                    null,
//                    null,
//                    null,
//                    null,
//                    null);
//            try {
//                if (smsRows != null) {
//                    smsRows.moveToPosition(-1);
//                    ContentValues cv = new ContentValues();
//                    while (smsRows.moveToNext()) {
//                        cv.clear();
//
//                        long id = smsRows.getLong(0);        // 0 for Sms._ID
//                        String body = smsRows.getString(1);  // 1 for Sms.BODY
//
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.ID, id);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.INDEXED_TEXT, body);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.SOURCE_ROW_ID, id);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.TABLE_ID, 1);
//                        db.insert(TABLE_WORDS, com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.INDEXED_TEXT, cv);
//                    }
//                }
//            } finally {
//                if (smsRows != null) {
//                    smsRows.close();
//                }
//            }
//        }
//
//        {
//            Cursor mmsRows = db.query(
//                    "part",
//                    new String[] { Part._ID, Part.TEXT },
//                    "ct = 'text/plain'",
//                    null,
//                    null,
//                    null,
//                    null);
//            try {
//                if (mmsRows != null) {
//                    mmsRows.moveToPosition(-1);
//                    ContentValues cv = new ContentValues();
//                    while (mmsRows.moveToNext()) {
//                        cv.clear();
//
//                        long id = mmsRows.getLong(0);         // 0 for Part._ID
//                        String body = mmsRows.getString(1);   // 1 for Part.TEXT
//
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.ID, id);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.INDEXED_TEXT, body);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.SOURCE_ROW_ID, id);
//                        cv.put(com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.TABLE_ID, 1);
//                        db.insert(TABLE_WORDS, com.vivo.mms.common.utils.VivoTelephony.MmsSms.WordsTable.INDEXED_TEXT, cv);
//                    }
//                }
//            } finally {
//                if (mmsRows != null) {
//                    mmsRows.close();
//                }
//            }
//        }
//    }
//
//    private void createIndices(SQLiteDatabase db) {
//        createThreadIdIndex(db);
//        createThreadIdDateIndex(db);
//        createPartMidIndex(db);
//        createAddrMsgIdIndex(db);
//        createPduPartIndex(db);
//    }
//
//    private void createPduPartIndex(SQLiteDatabase db) {
//        try {
//            db.execSQL("CREATE INDEX IF NOT EXISTS index_part ON " + MmsProvider.TABLE_PART +
//                        " (mid);");
//        } catch (Exception ex) {
//            VLog.e(TAG, "got exception creating indices: " + ex.toString());
//        }
//     }
//
//    private void createThreadIdDateIndex(SQLiteDatabase db) {
//        try {
//            db.execSQL("CREATE INDEX IF NOT EXISTS threadIdDateIndex ON sms" +
//            " (thread_id, date);");
//        } catch (Exception ex) {
//            VLog.e(TAG, "got exception creating indices: " + ex.toString());
//        }
//    }
//
//    private void createPartMidIndex(SQLiteDatabase db) {
//        try {
//            db.execSQL("CREATE INDEX IF NOT EXISTS partMidIndex ON part (mid)");
//        } catch (Exception ex) {
//            VLog.e(TAG, "got exception creating indices: " + ex.toString());
//        }
//    }
//
//    private void createAddrMsgIdIndex(SQLiteDatabase db) {
//        try {
//            db.execSQL("CREATE INDEX IF NOT EXISTS addrMsgIdIndex ON addr (msg_id)");
//        } catch (Exception ex) {
//            VLog.e(TAG, "got exception creating indices: " + ex.toString());
//        }
//    }
//
//    private void createThreadIdIndex(SQLiteDatabase db) {
//        try {
//            db.execSQL("CREATE INDEX IF NOT EXISTS typeThreadIdIndex ON sms" +
//            " (type, thread_id);");
//        } catch (Exception ex) {
//            LogUtil.getInstance().e(TAG, "got exception creating indices: " + ex.toString());
//        }
//    }
//
//    private void createMmsTables(SQLiteDatabase db) {
//        // N.B.: Whenever the columns here are changed, the columns in
//        // {@ref MmsSmsProvider} must be changed to match.
//        db.execSQL("CREATE TABLE " + MmsProvider.TABLE_PDU + " (" +
//                   Mms._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                   Mms.THREAD_ID + " INTEGER," +
//                   Mms.DATE + " INTEGER," +
//                   Mms.DATE_SENT + " INTEGER DEFAULT 0," +
//                   Mms.MESSAGE_BOX + " INTEGER," +
//                   Mms.READ + " INTEGER DEFAULT 0," +
//                   Mms.MESSAGE_ID + " TEXT," +
//                   Mms.SUBJECT + " TEXT," +
//                   Mms.SUBJECT_CHARSET + " INTEGER," +
//                   Mms.CONTENT_TYPE + " TEXT," +
//                   Mms.CONTENT_LOCATION + " TEXT," +
//                   Mms.EXPIRY + " INTEGER," +
//                   Mms.MESSAGE_CLASS + " TEXT," +
//                   Mms.MESSAGE_TYPE + " INTEGER," +
//                   Mms.MMS_VERSION + " INTEGER," +
//                   Mms.MESSAGE_SIZE + " INTEGER," +
//                   Mms.PRIORITY + " INTEGER," +
//                   Mms.READ_REPORT + " INTEGER," +
//                   Mms.REPORT_ALLOWED + " INTEGER," +
//                   Mms.RESPONSE_STATUS + " INTEGER," +
//                   Mms.STATUS + " INTEGER," +
//                   Mms.TRANSACTION_ID + " TEXT," +
//                   Mms.RETRIEVE_STATUS + " INTEGER," +
//                   Mms.RETRIEVE_TEXT + " TEXT," +
//                   Mms.RETRIEVE_TEXT_CHARSET + " INTEGER," +
//                   Mms.READ_STATUS + " INTEGER," +
//                   Mms.CONTENT_CLASS + " INTEGER," +
//                   Mms.RESPONSE_TEXT + " TEXT," +
//                   Mms.DELIVERY_TIME + " INTEGER," +
//                   Mms.DELIVERY_REPORT + " INTEGER," +
//                   Mms.LOCKED + " INTEGER DEFAULT 0," +
//                   /*Mms.SUB_ID */Mms.SUBSCRIPTION_ID+ " INTEGER DEFAULT -1," +  //SIM_ID change SUB_ID.modified by lengxibo for Android 5.0 2014.12.24
//                   Mms.CREATOR + " TEXT," +     //added by lengxibo for Android 5.0 2014.12.24
//                   Mms.SEEN + " INTEGER DEFAULT 0," +
//                   "st_ext INTEGER DEFAULT 0," +   //added by lengxibo for mtk Android 5.0 2015.01.09
//                   "is_encrypted INTEGER DEFAULT 0," +
//                   "time INTEGER DEFAULT 0,"+
//                   "service_center TEXT," +
//                   "dirty INTEGER DEFAULT 1,"+
//                   "text_only " + " INTEGER DEFAULT 0, " +
//                    "prepared_type INTEGER DEFAULT -1," +
//                    "prepared_body TEXT,"+
//                    "prepared_width INTEGER DEFAULT 0," +
//                    "prepared_height INTEGER DEFAULT 0," +
//                    "risk_website INTEGER DEFAULT 0,"+   //added by lengxibo for Rom3.1 2016.07.29
//                    PrivacyFilterParams.MESSAGE_MODE +" INTEGER DEFAULT 0"+ //add by zouyongjun/BBK
//                   ",phone_id INTEGER DEFAULT -1" +  //added by maoyuanze for Android 5.1 2015.07.17
//                   ","+SUBJECT_TEXT+" TEXT," +
//                    "block_mms_type INTEGER DEFAULT 0"+
//                    ");");
//
//        db.execSQL("CREATE TABLE " + MmsProvider.TABLE_ADDR + " (" +
//                   Addr._ID + " INTEGER PRIMARY KEY," +
//                   Addr.MSG_ID + " INTEGER," +
//                   Addr.CONTACT_ID + " INTEGER," +
//                   Addr.ADDRESS + " TEXT," +
//                   Addr.TYPE + " INTEGER," +
//                   Addr.CHARSET + " INTEGER);");
//
//        db.execSQL("CREATE TABLE " + MmsProvider.TABLE_PART + " (" +
//                   Part._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                   Part.MSG_ID + " INTEGER," +
//                   Part.SEQ + " INTEGER DEFAULT 0," +
//                   Part.CONTENT_TYPE + " TEXT," +
//                   Part.NAME + " TEXT," +
//                   Part.CHARSET + " INTEGER," +
//                   Part.CONTENT_DISPOSITION + " TEXT," +
//                   Part.FILENAME + " TEXT," +
//                   Part.CONTENT_ID + " TEXT," +
//                   Part.CONTENT_LOCATION + " TEXT," +
//                   Part.CT_START + " INTEGER," +
//                   Part.CT_TYPE + " TEXT," +
//                   Part._DATA + " TEXT," +
//                   Part.TEXT + " TEXT," +
//                   " data BLOB);");
//
//        db.execSQL("CREATE TABLE " + MmsProvider.TABLE_RATE + " (" +
//                   Rate.SENT_TIME + " INTEGER);");
//
//        db.execSQL("CREATE TABLE " + MmsProvider.TABLE_DRM + " (" +
//                   BaseColumns._ID + " INTEGER PRIMARY KEY," +
//                   "_data TEXT);");
//    }
//
//    private void createMmsTriggers(SQLiteDatabase db) {
//        // Cleans up parts when a MM is deleted.
//        db.execSQL("DROP TRIGGER IF EXISTS part_cleanup");
//        db.execSQL("CREATE TRIGGER part_cleanup DELETE ON " + MmsProvider.TABLE_PDU + " " +
//                   "BEGIN " +
//                   "  DELETE FROM " + MmsProvider.TABLE_PART +
//                   "  WHERE " + Part.MSG_ID + "=old._id;" +
//                   "END;");
//
//        // Cleans up address info when a MM is deleted.
//        db.execSQL("DROP TRIGGER IF EXISTS addr_cleanup");
//        db.execSQL("CREATE TRIGGER addr_cleanup DELETE ON " + MmsProvider.TABLE_PDU + " " +
//                   "BEGIN " +
//                   "  DELETE FROM " + MmsProvider.TABLE_ADDR +
//                   "  WHERE " + Addr.MSG_ID + "=old._id;" +
//                   "END;");
//
//        // Delete obsolete delivery-report, read-report while deleting their
//        // associated Send.req.
//        db.execSQL("DROP TRIGGER IF EXISTS cleanup_delivery_and_read_report");
//        db.execSQL("CREATE TRIGGER cleanup_delivery_and_read_report " +
//                   "AFTER DELETE ON " + MmsProvider.TABLE_PDU + " " +
//                   "WHEN old." + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_SEND_REQ + " " +
//                   "BEGIN " +
//                   "  DELETE FROM " + MmsProvider.TABLE_PDU +
//                   "  WHERE (" + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_DELIVERY_IND +
//                   "    OR " + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_READ_ORIG_IND +
//                   ")" +
//                   "    AND " + Mms.MESSAGE_ID + "=old." + Mms.MESSAGE_ID + "; " +
//                   "END;");
//
////        db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_insert_part");
////        db.execSQL(PART_UPDATE_THREADS_ON_INSERT_TRIGGER);
////
////        db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_update_part");
////        db.execSQL(PART_UPDATE_THREADS_ON_UPDATE_TRIGGER);
//
//        //removed by fuleilei
////        db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_delete_part");
////        db.execSQL(PART_UPDATE_THREADS_ON_DELETE_TRIGGER);
//
////        db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_update_pdu");
////        db.execSQL(PDU_UPDATE_THREADS_ON_UPDATE_TRIGGER);
//    }
//
//    private void createPushMmsTriggers(SQLiteDatabase db) {
//        //modify by yanglei at 2019-07-12
//        db.execSQL("DROP TRIGGER IF EXISTS INSERT_THREADS_AND_SHOP_ON_PUSH_MMS_INSERT;");
//        db.execSQL("DROP TRIGGER IF EXISTS INSERT_THREADS_ON_PUSH_MMS_INSERT;");
//        db.execSQL(" CREATE TRIGGER INSERT_THREADS_ON_PUSH_MMS_INSERT AFTER " +
//                "  INSERT ON " + TABLE_NAME_PUSH_MMS +
//                "  WHEN NOT EXISTS ( SELECT _id FROM threads where " + ICommonData.COL_THREAD_KEY + " = new."+ICommonData.COL_THREAD_KEY +" ) " +
//                "  BEGIN " +
//                "  INSERT INTO threads "  +
//                "  (" + Threads.SNIPPET + ", " + Threads.RECIPIENT_IDS + ", " + Threads.DATE + ", "+
//                "  " + Threads.MESSAGE_COUNT + ", "+ "unreadcount" + ", " + Threads.READ + ", topindex, " +
//                " "  + COL_THREADS_V_ADDRESS_NAME + ", " + COL_THREADS_V_ADDRESS_TYPE + ", " + ICommonData.COL_THREAD_KEY  + ", " + ICommonData.MsgBussinessType.KEY + ", sub_id)" +
//                "  VALUES (new." + COL_PUSH_MMS_CONTENT + ",new."+ COL_PUSH_MMS_NUMBER+
//                ", new." + COL_PUSH_MMS_DATE + ", 1, CASE new." + COL_PUSH_MMS_READ_TYPE +
//                " WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE + " THEN 1 WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_READ +
//                " THEN 0 ELSE 0 END, CASE new." + COL_PUSH_MMS_READ_TYPE +
//                " WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE + " THEN 0 WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_READ +
//                " THEN 1 ELSE 1 END, -1, new." + COL_PUSH_MMS_NAME + ", new."+COL_PUSH_MMS_TYPE + ", new." +  ICommonData.COL_THREAD_KEY  + ", new." + ICommonData.MsgBussinessType.KEY +", new.sub_id);" +
//                "  UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_THREAD_ID + " =  " +
//                "       (SELECT " + Threads._ID + " FROM threads WHERE "+ICommonData.COL_THREAD_KEY +" = new."+ICommonData.COL_THREAD_KEY + " )" +
//                "  WHERE  " + COL_PUSH_MMS_ID + " = new." + COL_PUSH_MMS_ID +";" +
//                "       " +
//                "  END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS INSERT_SHOP_ON_PUSH_MMS_INSERT;");
//        db.execSQL(" CREATE TRIGGER INSERT_SHOP_ON_PUSH_MMS_INSERT AFTER " +
//                "  INSERT ON " + TABLE_NAME_PUSH_MMS + " " +
//                "  FOR EACH ROW WHEN NOT EXISTS " +
//                "  (SELECT _id FROM " + TABLE_NAME_PUSH_SHOP +
//                "  WHERE " + COL_PUSH_MMS_SHOP_ID + " = new." + COL_PUSH_MMS_SHOP_ID +" AND "+ ICommonData.MsgBussinessType.KEY +" = new." + ICommonData.MsgBussinessType.KEY + ") " +
//                "  BEGIN " +
//                "  INSERT INTO " + TABLE_NAME_PUSH_SHOP +
//                "  (" + COL_PUSH_SHOP_SHOP_ID + ", " + COL_PUSH_SHOP_NAME + ", " + COL_PUSH_SHOP_TYPE + ", " + ICommonData.MsgBussinessType.KEY + ") VALUES (" +
//                "  new." + COL_PUSH_MMS_SHOP_ID + ", new." + COL_PUSH_MMS_NAME +  ", new." + COL_PUSH_SHOP_TYPE + ", new."+ ICommonData.MsgBussinessType.KEY +");" +
//                "  END;");
//        // vivo <yanglei> modify end.
//
//
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_THREADS_ON_MMS_INSERT;");
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_THREADS_ON_PUSH_MMS_INSERT;");
//        String updateSimId = "";
//        if (ProviderUtil.isMultiSimEnabled(mContext)) {
//            updateSimId = "    sub_id = new.sub_id, ";
//        }
//        db.execSQL("CREATE TRIGGER UPDATE_THREADS_ON_PUSH_MMS_INSERT AFTER " +
//                "  INSERT ON " + TABLE_NAME_PUSH_MMS +
//                "  WHEN EXISTS ( SELECT _id FROM threads where " + ICommonData.COL_THREAD_KEY + " = new."+ICommonData.COL_THREAD_KEY +" ) " +
//                "  BEGIN " +
//                    "   UPDATE threads " +  " SET " +
//                    "   " + Threads.SNIPPET + " = " +
//                    "   " + "   (CASE WHEN (SELECT COUNT( " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " ) " +
//                    "   " + "                           FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "                           WHERE " + ICommonData.COL_THREAD_KEY + " =  new." + ICommonData.COL_THREAD_KEY +
//                    "   " + "                           AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " ) = 0 " +
//                    "   " + "    THEN new." + COL_PUSH_MMS_CONTENT +
//                    "   " + "    ELSE (SELECT " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "              WHERE " + ICommonData.COL_THREAD_KEY + " = new." + ICommonData.COL_THREAD_KEY +
//                    "   " + "              AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " LIMIT 0,1)" +
//                    "   " + "    END), " +
//                    "   " + Threads.DATE + "= new." + COL_PUSH_MMS_DATE + ", " +
//                    "   " + Threads.MESSAGE_COUNT + " = " + "(" + Threads.MESSAGE_COUNT + " + 1)," +
//                    "   " + updateSimId +
//                    "   " + "unreadcount = " +
//                    "   " + "   (CASE WHEN new." + COL_PUSH_MMS_MEDIA_TYPE + " <> " + PUSH_SEND_MSG_TYPE + " THEN " +
//                    "   " + "       (CASE new."+ COL_PUSH_MMS_READ_TYPE +
//                    "   " + "        WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE +
//                    "   " + "        THEN (unreadcount + 1) " +
//                    "   " + "        WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_READ +
//                    "   " + "        THEN (unreadcount + 0) " +
//                    "   " + "        ELSE (unreadcount + 0) " +
//                    "   " + "        END) " +
//                    "   " + "   ELSE (unreadcount + 0) " +
//                    "   " + "   END)," +
//                    "   " + Threads.READ + " = " +
//                    "   " + "   (CASE WHEN new." + COL_PUSH_MMS_MEDIA_TYPE + " <> " + PUSH_SEND_MSG_TYPE + " THEN " +
//                    "   " + "       (CASE new." + COL_PUSH_MMS_READ_TYPE +
//                    "   " + "       WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE +
//                    "   " + "       THEN (" + Threads.READ + " + 0) " +
//                    "   " + "       WHEN " + IPushData.MsgStatusValue.PMMS_TYPE_READ +
//                    "   " + "       THEN (" + Threads.READ + " + 1) " +
//                    "   " + "       ELSE (" + Threads.READ + " + 0) " +
//                    "   " + "       END) " +
//                    "   " + "   ELSE ( " + Threads.READ + " + 0) " +
//                    "   " + "   END) " +
//                    "  WHERE threads"  + "." + ICommonData.COL_THREAD_KEY + " = new." + ICommonData.COL_THREAD_KEY + ";" +
//                    "  UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_THREAD_ID + " =  " +
//                    "       (SELECT " + Threads._ID + " FROM threads WHERE "+ICommonData.COL_THREAD_KEY +" = new."+ICommonData.COL_THREAD_KEY + " )" +
//                    "  WHERE  " + COL_PUSH_MMS_ID + " = new." + COL_PUSH_MMS_ID +";" +
//                    "       " +
//                "  END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_THREADS_ON_MMS_UPDATE;");
//        db.execSQL("CREATE TRIGGER UPDATE_THREADS_ON_MMS_UPDATE AFTER " +
//                "  UPDATE OF " + COL_PUSH_MMS_READ_TYPE + ", " + COL_PUSH_MMS_CONTENT +
//                "  ON " + TABLE_NAME_PUSH_MMS + " " +
//                "  BEGIN " +
//                    "  UPDATE threads " + " SET " +
//                    "   " + Threads.READ + " = " +
//                    "   " + "   (CASE (SELECT COUNT( " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_ID + " )" +
//                    "   " + "               FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "               WHERE " + COL_PUSH_MMS_READ_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE +
//                    "   " + "               AND " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_THREAD_ID + " = threads." + Threads._ID + " ) " +
//                    "   " + "    WHEN 0 " +
//                    "   " + "    THEN  1 " +
//                    "   " + "    ELSE   0 " +
//                    "   " + "    END) " +
//                    "  WHERE threads." + Threads._ID + " = new." + COL_PUSH_MMS_THREAD_ID + ";" +
//
//                    "  UPDATE threads " + " SET " +
//                    "   " + "unreadcount = " +
//                    "   " + "   (SELECT COUNT( " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_ID + " )" +
//                    "   " + "    FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "    LEFT JOIN threads ON threads." + Threads._ID + " = " + COL_PUSH_MMS_THREAD_ID +
//                    "   " + "    WHERE " + COL_PUSH_MMS_THREAD_ID + " = new. " + COL_PUSH_MMS_THREAD_ID +
//                    "   " + "    AND threads." + Threads.READ + " = 0 " +
//                    "   " + "    AND " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_READ_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_UNREADE + "), " +
//                    "   " +  Threads.SNIPPET + "=" +
//                    "   " + "   (CASE WHEN (SELECT COUNT( " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " ) " +
//                    "   " + "                           FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "                           WHERE " + COL_PUSH_MMS_THREAD_ID + " =  new." + COL_PUSH_MMS_THREAD_ID +
//                    "   " + "                           AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " ) = 0 " +
//                    "   " + "    THEN (SELECT " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT +
//                    "   " + "               FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "               WHERE " + COL_PUSH_MMS_THREAD_ID + " = new." + COL_PUSH_MMS_THREAD_ID +
//                    "   " + "               ORDER BY " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_DATE + " DESC LIMIT 0,1) " +
//                    "   " + "    ELSE (SELECT " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " FROM " + TABLE_NAME_PUSH_MMS +
//                    "   " + "              WHERE " + COL_PUSH_MMS_THREAD_ID + " = new." + COL_PUSH_MMS_THREAD_ID +
//                    "   " + "              AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " LIMIT 0,1)" +
//                    "   " + "    END) " +
//                    "  WHERE threads." + Threads._ID + " = new." + COL_PUSH_MMS_THREAD_ID + ";" +
//
//                "  END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_THREADS_ON_MMS_DELETE;");
//        db.execSQL("CREATE TRIGGER UPDATE_THREADS_ON_MMS_DELETE " +
//                "  AFTER DELETE ON " + TABLE_NAME_PUSH_MMS  +
//                "  BEGIN " +
//                "    UPDATE threads SET " + Threads.SNIPPET + " = " +
//                "       (CASE WHEN (SELECT COUNT( " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " ) " +
//                "                               FROM " + TABLE_NAME_PUSH_MMS +
//                "                               WHERE " + COL_PUSH_MMS_THREAD_ID + " =  old." + COL_PUSH_MMS_THREAD_ID +
//                "                               AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " ) = 0 " +
//                "        THEN (SELECT DISTINCT " + COL_PUSH_MMS_CONTENT + " FROM " + TABLE_NAME_PUSH_MMS +
//                "                   WHERE " + COL_PUSH_MMS_THREAD_ID+ " = old." + COL_PUSH_MMS_THREAD_ID +
//                "                   ORDER BY " + COL_PUSH_MMS_DATE + " DESC) " +
//                "        ELSE (SELECT " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_CONTENT + " FROM " + TABLE_NAME_PUSH_MMS +
//                "                  WHERE " + COL_PUSH_MMS_THREAD_ID + " = old." + COL_PUSH_MMS_THREAD_ID +
//                "                  AND " + COL_PUSH_MMS_RECEIVE_TYPE + " = " + IPushData.MsgStatusValue.PMMS_TYPE_DRAFT + " LIMIT 0,1)" +
//                "        END), " +
//                "   " + Threads.DATE + " = (SELECT DISTINCT " + COL_PUSH_MMS_DATE + " FROM " + TABLE_NAME_PUSH_MMS +
//                "       WHERE " + COL_PUSH_MMS_THREAD_ID + " = old." + COL_PUSH_MMS_THREAD_ID +
//                "           ORDER BY " + COL_PUSH_MMS_DATE + " DESC), " +
//                "   " + Threads.MESSAGE_COUNT + " = ( " + Threads.MESSAGE_COUNT + " - 1), " +
//                "   unreadcount = (CASE WHEN old.media_type <> 12 THEN (CASE old.[read_type] WHEN 0 THEN (unreadcount - 1) WHEN 1 THEN (unreadcount + 0) ELSE (unreadcount + 0) END) ELSE (unreadcount + 0) END)"+
//                "    WHERE " + Threads._ID + " = old." + COL_PUSH_MMS_THREAD_ID + ";" +
//
//                "    DELETE FROM threads " +
//                "    WHERE " + Threads._ID + " = old." + COL_PUSH_MMS_THREAD_ID +
//                "    AND ((SELECT COUNT( " + COL_PUSH_MMS_THREAD_ID + ") FROM " + TABLE_NAME_PUSH_MMS +
//                "    WHERE (thread_id = old." + COL_PUSH_MMS_THREAD_ID + ")) = 0);" +
//
//                "    DELETE FROM  " + TABLE_NAME_PUSH_SHOP +
//                "    WHERE " + COL_PUSH_SHOP_THREAD_ID + " = old." + COL_PUSH_MMS_THREAD_ID +
//                "    AND ((SELECT COUNT( " + COL_PUSH_MMS_THREAD_ID + ") FROM " + TABLE_NAME_PUSH_MMS +
//                "    WHERE (thread_id = old." + COL_PUSH_MMS_THREAD_ID + ")) = 0);" +
//                "  END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS INSERT_PUSH_SYNC_ON_SHOP_DELETE;");
//        db.execSQL("CREATE TRIGGER INSERT_PUSH_SYNC_ON_SHOP_DELETE " +
//                "  AFTER DELETE ON " + TABLE_NAME_PUSH_SHOP  +
//                "  WHEN (old." + COL_PUSH_SHOP_IS_IN_BLACK + " = " + IPushData.BlackValue.IN_BLACK_LIST_VALUE +
//                "       OR old." + COL_PUSH_SHOP_NOTIFICATION + " = " + IPushData.NotificationTypeValue.DONOT_DISTURB_MODE_VALUE + " ) " +
//                "  BEGIN " +
//                "       INSERT INTO " + TABLE_NAME_PUSH_SYNC +
//                "           (" + COL_PUSH_SYNC_SHOP_ID + ", " + COL_PUSH_SYNC_TYPE + ", " + COL_PUSH_SYNC_DATE + ") " +
//                "       VALUES (old." + COL_PUSH_SHOP_SHOP_ID + ", " +
//                "           CASE WHEN (old." + COL_PUSH_SHOP_IS_IN_BLACK + " = " + IPushData.BlackValue.IN_BLACK_LIST_VALUE +
//                "               AND old." + COL_PUSH_SHOP_NOTIFICATION + " = " + IPushData.NotificationTypeValue.DONOT_DISTURB_MODE_VALUE + ")" +
//                "           THEN " + IPushData.CommonPushSyncEntry.TYPE_NOTIFICATION_AND_BLACK +
//                "           ELSE " +
//                "               CASE WHEN old." + COL_PUSH_SHOP_IS_IN_BLACK + " = " + IPushData.BlackValue.IN_BLACK_LIST_VALUE +
//                "               THEN " + IPushData.CommonPushSyncEntry.TYPE_BLACK_LIST +
//                "               ELSE " +
//                "                   CASE WHEN old." + COL_PUSH_SHOP_NOTIFICATION + " = " + IPushData.NotificationTypeValue.DONOT_DISTURB_MODE_VALUE +
//                "                   THEN " + IPushData.CommonPushSyncEntry.TYPE_NOTIFICATION +
//                "                   ELSE " + IPushData.CommonPushSyncEntry.TYPE_BLACK_LIST +
//                "                   END " +
//                "               END " +
//                "           END, STRFTIME('%s','now'));" +
//                "  END;");
//    }
//
//    private void createPushMmsAliasTriggers(SQLiteDatabase db) {
//
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_PMMS_ALIAS_ON_PMMS_INSERT;");
//        db.execSQL(" CREATE TRIGGER UPDATE_PMMS_ALIAS_ON_PMMS_INSERT AFTER " +
//                "  INSERT ON " + TABLE_NAME_PUSH_MMS + " " +
//                "  BEGIN " +
//                    "  UPDATE " + TABLE_NAME_PUSH_MMS + " SET " +
//                    "   " + COL_PUSH_MMS_READ_TYPE_ALIAS + " = " + COL_PUSH_MMS_READ_TYPE + "," +
//                    "   " + COL_PUSH_MMS_RECEIVE_TYPE_ALIAS + " = " + COL_PUSH_MMS_RECEIVE_TYPE + "," +
//                    "   " + COL_PUSH_MMS_SEND_STATE_ALIAS + " = " +
//                    "   " + "(CASE " + COL_PUSH_MMS_SEND_STATE + " WHEN 0 THEN 2 WHEN 1 THEN 0 WHEN 2 THEN 1 ELSE -1 END), " +
//                    "   " + COL_PUSH_MMS_ID_EX + " = ('" + ExtendSmsContract.DB_TABLE_PREFIX_PUSH_MMS + "'||" + COL_PUSH_MMS_ID + ")" +
//                    "  WHERE " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_ID + " = new." + COL_PUSH_MMS_ID + ";" +
//                "  END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS UPDATE_PMMS_ALIAS_ON_PMMS_UPDATE;");
//        db.execSQL("CREATE TRIGGER UPDATE_PMMS_ALIAS_ON_PMMS_UPDATE AFTER " +
//                "  UPDATE OF " + COL_PUSH_MMS_READ_TYPE + ", " + COL_PUSH_MMS_SEND_STATE + "," + COL_PUSH_MMS_RECEIVE_TYPE +
//                "  ON " + TABLE_NAME_PUSH_MMS + " " +
//                "  BEGIN " +
//                    "  UPDATE " + TABLE_NAME_PUSH_MMS + " SET " +
//                    "   " + COL_PUSH_MMS_READ_TYPE_ALIAS + " = " +  COL_PUSH_MMS_READ_TYPE + "," +
//                    "   " + COL_PUSH_MMS_RECEIVE_TYPE_ALIAS + " = " + COL_PUSH_MMS_RECEIVE_TYPE + "," +
//                    "   " + COL_PUSH_MMS_SEND_STATE_ALIAS + " = " +
//                    "   " + "(CASE  " + COL_PUSH_MMS_SEND_STATE + " WHEN 0 THEN 2 WHEN 1 THEN 0 WHEN 2 THEN 1 ELSE -1 END) " +
//                    "  WHERE " + TABLE_NAME_PUSH_MMS + "." + COL_PUSH_MMS_ID + " = new." + COL_PUSH_MMS_ID + ";" +
//                "  END;");
//    }
//
//    private void createSmsTables(SQLiteDatabase db) {
//        // N.B.: Whenever the columns here are changed, the columns in
//        // {@ref MmsSmsProvider} must be changed to match.
//        db.execSQL("CREATE TABLE sms (" +
//                "_id INTEGER PRIMARY KEY," +
//                "thread_id INTEGER," +
//                "address TEXT," +
//                "m_size INTEGER," +
//                "person INTEGER," +
//                "date INTEGER," +
//                "date_sent INTEGER DEFAULT 0," +
//                "protocol INTEGER," +
//                "read INTEGER DEFAULT 0," +
//                "status INTEGER DEFAULT -1," + // a TP-Status value
//                // or -1 if it
//                // status hasn't
//                // been received
//                "type INTEGER," +
//                "reply_path_present INTEGER," +
//                "subject TEXT," +
//                "body TEXT," +
//                "service_center TEXT," +
//                "locked INTEGER DEFAULT 0," +
//                "sub_id INTEGER DEFAULT -1," +   //sim_id change sub_id.modified by lengxibo for Android 5.0 2014.12.24
//                "error_code INTEGER DEFAULT 0," +
//                "creator TEXT," +   //added by lengxibo for Android 5.0 2014.12.24
//                "seen INTEGER DEFAULT 0," +
//                "is_encrypted INTEGER DEFAULT 0,"+  //Add by lk  2011-03-08
//                "time INTEGER DEFAULT 0,"+ //添加一个加密字段
//                "dirty INTEGER DEFAULT 1,"+
//                //added by lengxibo for mtk Android 5.0 2015.01.09
//                   /* todo open if need lengxibo 2015.01.10
//                    * "ipmsg_id INTEGER DEFAULT 0," +
//                   "ref_id INTEGER," +
//                   "total_len INTEGER," +
//                   "rec_len INTEGER" +*/
//                //added end lengxibo
//                PrivacyFilterParams.MESSAGE_MODE + " INTEGER DEFAULT 0"+ //add by zouyongjun/BBK
//                ",priority INTEGER DEFAULT -1" +                 //pri change priority.modified by lengxibo for Android 5.0 2014.12.24
//                ",phone_id INTEGER DEFAULT -1" +  //added by maoyuanze for Android 5.1 2015.07.17
//                ",is_exec_trigger INTEGER DEFAULT 1"+
//                ",verify_code INTEGER DEFAULT 0"+   //added by lengxibo for Rom3.1 2016.07.29
//                ",risk_website INTEGER DEFAULT 0"+   //added by lengxibo for Rom3.1 2016.07.29
//                ",bubble TEXT DEFAULT -1" +
//                ",bubble_type INTEGER DEFAULT 1" +
//                ",black_type INTEGER DEFAULT -1"+
//                ",bubble_parse_time INTEGER DEFAULT 0" +
//                // Add for RCS begin
//                "," + RcsData.RCS_SMS_COLUMN_SHOW_TIME + " INTEGER DEFAULT " + RcsData.SHOW_TIME_DEFAULT +
//                // Add for RCS end
//                ",group_id TEXT"+
//                ",block_sms_type INTEGER DEFAULT 0"+
//                ",sms_extend_type INTEGER DEFAULT 0"+
//                ",dynamic_bubble TEXT DEFAULT -1"+
//                ",dynamic_update_date INTEGER DEFAULT 0"+
//                ");");
//
//        /**
//         * This table is used by the SMS dispatcher to hold
//         * incomplete partial messages until all the parts arrive.
//         */
//        db.execSQL("CREATE TABLE raw (" +
//                "_id INTEGER PRIMARY KEY," +
//                "date INTEGER," +
//                "reference_number INTEGER," + // one per full message
//                "count INTEGER," + // the number of parts
//                "sequence INTEGER," + // the part number of this message
//                "destination_port INTEGER," +
//                "address TEXT," +
//                "sub_id INTEGER DEFAULT -1," +  //sim_id change sub_id.modified by lengxibo for Android 5.0 2014.12.24
//                "pdu TEXT," +
//                /// M: for ct new feature of concatenated sms @{
//                "recv_time INTEGER," +   //added by leiyaotao for 信息：手机接收不到长短信。2013.12.20
//                "upload_flag INTEGER," +  //added by leiyaotao for 信息：手机接收不到长短信。2013.12.20
//                /// M: @}
//                "deleted INTEGER DEFAULT 0," + // bool to indicate if row is deleted
//                "message_body TEXT," + // message body
//                "display_originating_addr TEXT" +// email address if from an email gateway, otherwise same as address
//                ");");
//
//        db.execSQL("CREATE TABLE attachments (" +
//                "sms_id INTEGER," +
//                "content_url TEXT," +
//                "offset INTEGER);");
//
//        /**
//         * This table is used by the SMS dispatcher to hold pending
//         * delivery status report intents.
//         */
//        db.execSQL("CREATE TABLE sr_pending (" +
//                "reference_number INTEGER," +
//                "action TEXT," +
//                "data TEXT);");
//        //add by sangzhonghai 10-10-19
//        db.execSQL("CREATE TABLE security_box (" +
//                "_id INTEGER PRIMARY KEY," +
//                "type INTEGER,"+
//                "date INTEGER,"+
//                "save_date INTEGER,"+
//                "number TEXT," +
//                "content TEXT," +
//                "sub_id INTEGER DEFAULT -1," +
//                "dirty INTEGER DEFAULT 1,"+
//                PrivacyFilterParams.MESSAGE_MODE + " INTEGER DEFAULT 0"+
//                ");");
//        //add end ...
//
//        db.execSQL("CREATE TABLE recents (" +
//                "_id INTEGER PRIMARY KEY," +
//                "address text,"+   //最近记录号码
//                "date INTEGER,"+   //保存日期
//                "type INTEGER DEFAULT 0,"+   //0 发送 1 接收  未使用
//                "sub_id INTEGER DEFAULT -1,"+  //SIM卡ID  未使用
//                "is_secret INTEGER DEFAULT 0"+ //访客相关标志 added by liuhaipeng 2013.10.21
//                ");");
//
//        //added by fuleilei for save received sms of app 2014.10.24
//        db.execSQL("CREATE TABLE sms_raw (" +
//                "_id INTEGER PRIMARY KEY," +
//                //"thread_id INTEGER," +
//                "address TEXT," +
//                //"m_size INTEGER," +
//                //"person INTEGER," +
//                "date INTEGER," +
//                //"date_sent INTEGER DEFAULT 0," +
//                "protocol INTEGER," +
//                "read INTEGER DEFAULT 0," +
//                "status INTEGER DEFAULT -1," + // a TP-Status value
//                                               // or -1 if it
//                                               // status hasn't
//                                               // been received
//                "type INTEGER," +
//                "reply_path_present INTEGER," +
//                "subject TEXT," +
//                "body TEXT," +
//                "service_center TEXT," +
//                "locked INTEGER DEFAULT 0," +
//                "sub_id INTEGER DEFAULT -1," +  //sim_id change sub_id.modified by lengxibo for Android 5.0 2014.12.24
//                "error_code INTEGER DEFAULT 0," +
//                "seen INTEGER DEFAULT 0," +
//				//"is_encrypted INTEGER DEFAULT 0,"+  //Add by lk  2011-03-08
//                "time INTEGER DEFAULT 0,"+ //添加一个加密字段
//                "dirty INTEGER DEFAULT 1"+
//                //MESSAGE_MODE + " INTEGER DEFAULT 0"+ //add by zouyongjun/BBK
//     			");");
//        //add end 20140919
//    }
//
//    private void createCommonTables(SQLiteDatabase db) {
//        // TODO Ensure that each entry is removed when the last use of
//        // any address equivalent to its address is removed.
//
//        /**
//         * This table maps the first instance seen of any particular
//         * MMS/SMS address to an ID, which is then used as its
//         * canonical representation.  If the same address or an
//         * equivalent address (as determined by our Sqlite
//         * PHONE_NUMBERS_EQUAL extension) is seen later, this same ID
//         * will be used. The _id is created with AUTOINCREMENT so it
//         * will never be reused again if a recipient is deleted.
//         */
//        db.execSQL("CREATE TABLE canonical_addresses (" +
//                   "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "address TEXT,"+
//                "inverted_address TEXT,"+
//                "date INTEGER);");
//
//        /**
//         * This table maps the subject and an ordered set of recipient
//         * IDs, separated by spaces, to a unique thread ID.  The IDs
//         * come from the canonical_addresses table.  This works
//         * because messages are considered to be part of the same
//         * thread if they have the same subject (or a null subject)
//         * and the same set of recipients.
//         */
//        db.execSQL("CREATE TABLE threads (" +
//                Threads._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                Threads.DATE + " INTEGER DEFAULT 0," +
//                Threads.MESSAGE_COUNT + " INTEGER DEFAULT 0," +
//                " readcount INTEGER DEFAULT 0," +
//                Threads.RECIPIENT_IDS + " TEXT," +
//                Threads.SNIPPET + " TEXT," +
//                Threads.SNIPPET_CHARSET + " INTEGER DEFAULT 0," +
//                Threads.READ + " INTEGER DEFAULT 1," +
//                Threads.ARCHIVED + " INTEGER DEFAULT 0," +  //added by lengxibo for Android 5.0 2014.12.24
//                Threads.TYPE + " INTEGER DEFAULT 0," +
//                Threads.ERROR + " INTEGER DEFAULT 0," +
//                Threads.HAS_ATTACHMENT + " INTEGER DEFAULT 0," +
//                "is_encrypted INTEGER DEFAULT 0," +
//                "unreadcount INTEGER DEFAULT 0," +
//                "sub_id INTEGER DEFAULT -1," +  //sim_id change sub_id.modified by lengxibo for Android 5.0 2015.01.01
//                PrivacyFilterParams.MESSAGE_MODE + " INTEGER DEFAULT 0," +  //add by zouyongjun/BBK
//                " status INTEGER DEFAULT 0," +
//                //added by lengxibo for mtk Android 5.0 2015.01.09
//                   /* todo open if need lengxibo 2015.01.10
//                    * "date_sent INTEGER DEFAULT 0," +
//                   "li_date INTEGER DEFAULT 0," +
//                   "li_snippet TEXT," +
//                   "li_snippet_cs INTEGER DEFAULT 0," +*/
//                //added end lengxibo
//                "time INTEGER DEFAULT 0," +
//                "snippet_verify_code TEXT," +   //added by lengxibo for Rom3.1 2016.07.29
//                "verify_code INTEGER DEFAULT 0," +   //added by lengxibo for Rom3.1 2016.07.29
//                "color INTEGER DEFAULT 0," +    //added by haipeng for 信息列表背景色 2013.9.15
//                "topindex INTEGER DEFAULT 0," +     //added by haipeng for 信息列表背景色 2013.9.15
//                COL_THREADS_V_ADDRESS_NAME + " TEXT," +   //added by lengxibo for notification mms 2017.07.29
//                COL_THREADS_V_ADDRESS_TYPE + " INTEGER DEFAULT 0," + //added by lengxibo for notification mms 2017.07.29
//                COL_THREADS_V_ADDRESS_FROM + " INTEGER DEFAULT -1," +
//                ICommonData.COL_THREAD_KEY + " TEXT," +
//                ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.DEFAULT + ","+
//                THREAD_CLOUMN_EXTEND_TYPE + " INTEGER DEFAULT " + THREAD_EXTEND_TYPE_DEFAULT +
//                ");");
//
//        /**
//         * This table stores the queue of messages to be sent/downloaded.
//         */
//        db.execSQL("CREATE TABLE " + MmsSmsProvider.TABLE_PENDING_MSG +" (" +
//                   PendingMessages._ID + " INTEGER PRIMARY KEY," +
//                   PendingMessages.PROTO_TYPE + " INTEGER," +
//                   PendingMessages.MSG_ID + " INTEGER," +
//                   PendingMessages.MSG_TYPE + " INTEGER," +
//                   PendingMessages.ERROR_TYPE + " INTEGER," +
//                   PendingMessages.ERROR_CODE + " INTEGER," +
//                   PendingMessages.RETRY_INDEX + " INTEGER NOT NULL DEFAULT 0," +
//                   PendingMessages.DUE_TIME + " INTEGER," +
//                   " pending_sub_id INTEGER DEFAULT 0, " +
//                   PendingMessages.LAST_TRY + " INTEGER);");
//        //add by lk
//        db.execSQL("CREATE TABLE security (" +
//                "_id INTEGER PRIMARY KEY," +
//                "pd TEXT,"+
//                "enable INTEGER DEFAULT 0);");
//        //end
//    }
//	private void createQuickText(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE quicktext (" +
//                "_id INTEGER PRIMARY KEY," +
//                "text TEXT);");
//	}
//    // TODO Check the query plans for these triggers.
//    private void createCommonTriggers(SQLiteDatabase db) {
//        // Updates threads table whenever a message is added to pdu.
//        db.execSQL("CREATE TRIGGER pdu_update_thread_on_insert AFTER INSERT ON " +
//                   MmsProvider.TABLE_PDU + " " +
//                   PDU_UPDATE_THREAD_CONSTRAINTS +
//                   PDU_UPDATE_THREAD_DATE_SNIPPET_COUNT_ON_INSERT);
//
//        //modified by lengxibo for 批量数据优化. 2014.5.14
//        String updateSimId = "";
//        if (ProviderUtil.isMultiSimEnabled(mContext)) {
//        	updateSimId = "    sub_id = new.sub_id, ";
//        }
//        String updateThreadTable = "BEGIN" +
//                "  UPDATE threads SET" +
//                "    date = new." + Sms.DATE + ", " +
//                updateSimId +
//                " time = new.time, "+  //add by liukai
//                "    snippet = new." + Sms.BODY + ", " +
//                "    snippet_cs = 0" +
//                "  WHERE threads._id = new." + Sms.THREAD_ID + ";"+
//                UPDATE_THREAD_COUNT_ON_NEW +
//                UPDATE_THREAD_UNREADCOUNT_ON_NEW +   //add by lk 2011-07-22
//                SMS_UPDATE_THREAD_READ_BODY +
//                UPDATE_THREAD_READ_COUNT+
//                "END;";
//        // Updates threads table whenever a message is added to sms.
//        db.execSQL("CREATE TRIGGER sms_update_thread_on_insert AFTER INSERT ON sms when new.is_exec_trigger =1 " +
//        		updateThreadTable);
//        //modified end lengxibo
//
//        // Updates threads table whenever a message in pdu is updated.
//        db.execSQL("CREATE TRIGGER pdu_update_thread_date_subject_on_update AFTER" +
//                   "  UPDATE OF " + Mms.DATE + ", " + Mms.SUBJECT + ", " + Mms.MESSAGE_BOX +
//                   "  ON " + MmsProvider.TABLE_PDU + " " +
//                   PDU_UPDATE_THREAD_CONSTRAINTS +
//                   PDU_UPDATE_THREAD_DATE_SNIPPET_COUNT_ON_UPDATE);
//
//        // Updates threads table whenever a message in sms is updated.
//        db.execSQL("CREATE TRIGGER sms_update_thread_date_subject_on_update AFTER" +
//                   "  UPDATE OF " + Sms.DATE + ", " + Sms.BODY + ", " + Sms.TYPE +", time "+
//                   "  ON sms " +
//                   updateThreadTable);
//
//        db.execSQL("CREATE TRIGGER mms_update_thread_unread_on_update AFTER" +
//                "  UPDATE OF " + Mms.READ +
//                "  ON pdu " +
//                " BEGIN " +
//                UPDATE_THREAD_UNREADCOUNT_ON_NEW +
//                "END;");
//
//        //add by lk
//        if(ProviderUtil.isMultiSimEnabled(mContext)){
//            db.execSQL("CREATE TRIGGER sms_update_thread_sim_id_on_insert AFTER" +
//                    "  INSERT " +
//                    "  ON sms when new.is_exec_trigger =1 " +
//                    "BEGIN " +
//                    UPDATE_THREAD_SIMID_ON_NEW +
//                    "END;");
//
//            db.execSQL("CREATE TRIGGER mms_update_thread_sim_id_on_insert AFTER" +
//                    "  INSERT " +
//                    "  ON pdu " +
//                    "BEGIN " +
//                    UPDATE_THREAD_SIMID_ON_NEW +
//                    "END;");
////          db.execSQL("CREATE TRIGGER sms_update_thread_sim_id_on_delete AFTER" +
////          "  DELETE " +
////          "  ON sms " +
////          "BEGIN " +
////          UPDATE_THREAD_SIMID_ON_DELETE +
////          "END;");
//
//			 db.execSQL("CREATE TRIGGER mms_update_thread_sim_id_on_delete AFTER" +
//			          "  DELETE " +
//			          "  ON pdu " +
//			          "BEGIN " +
//			          UPDATE_THREAD_SIMID_ON_DELETE +
//			          "END;");
//
//			 db.execSQL("CREATE TRIGGER mms_update_thread_sim_id_on_update AFTER" +
//			          "  UPDATE OF " + "sub_id" +
//			          "  ON pdu " +
//			          "BEGIN " +
//			          UPDATE_THREAD_SIMID_ON_NEW +
//			          "END;");
//        }
//
//        //end
//
//        // Updates threads table whenever a message in pdu is updated.
//        db.execSQL("CREATE TRIGGER pdu_update_thread_read_on_update AFTER" +
//                   "  UPDATE OF " + Mms.READ +
//                   "  ON " + MmsProvider.TABLE_PDU + " " +
//                   PDU_UPDATE_THREAD_CONSTRAINTS +
//                   "BEGIN " +
//                   PDU_UPDATE_THREAD_READ_BODY +
//                   UPDATE_THREAD_READ_COUNT+
//                   "END;");
//
//        // Updates threads table whenever a message in sms is updated.
//        db.execSQL("CREATE TRIGGER sms_update_thread_read_on_update AFTER" +
//                   "  UPDATE OF " + Sms.READ +
//                   "  ON sms " +
//                   "BEGIN " +
//                   SMS_UPDATE_THREAD_READ_BODY +
//                   UPDATE_THREAD_READ_COUNT+
//                   UPDATE_THREAD_UNREADCOUNT_ON_NEW+  //add by  lk
//                   "END;");
//
//        // Update threads table whenever a message in pdu is deleted
//        db.execSQL("CREATE TRIGGER pdu_update_thread_on_delete " +
//                   "AFTER DELETE ON pdu " +
//                   "BEGIN " +
//                   PDU_UPDATE_THREAD_DATE_ON_OLD +
//                   PDU_UPDATE_THREAD_TIME_ON_OLD +
//                   UPDATE_THREAD_COUNT_ON_OLD +
//                   UPDATE_THREAD_UNREADCOUNT_ON_OLD + //add by lk 2011-07-22
//                   UPDATE_THREAD_SNIPPET_SNIPPET_CS_ON_DELETE +
//                   // Add begin for RCS
//                   (SFeatureContorl.isRcsVersion() ? RcsMessageProviderConstants.UPDATE_THREAD_MSG_TYPE_ON_PDU_DELETE : "") +
//                   // Add end for RCS
//                   "END;");
//        // When the last message in a thread is deleted, these
//        // triggers ensure that the entry for its thread ID is removed
//        // from the threads table.
//        db.execSQL("CREATE TRIGGER delete_obsolete_threads_pdu " +
//                   "AFTER DELETE ON pdu " +
//                   "BEGIN " +
//                   "  DELETE FROM threads " +
//                   "  WHERE " +
//                   "    _id = old.thread_id " +
//                   "    AND _id NOT IN " +
//                   "    (SELECT thread_id FROM sms " +
//                   "     UNION SELECT thread_id from pdu " +
//                   "     ); " +
//                   "END;");
//
//        db.execSQL("CREATE TRIGGER delete_obsolete_threads_when_update_pdu " +
//                   "AFTER UPDATE OF " + Mms.THREAD_ID + " ON pdu " +
//                   "WHEN old." + Mms.THREAD_ID + " != new." + Mms.THREAD_ID + " " +
//                   "BEGIN " +
//                   "  DELETE FROM threads " +
//                   "  WHERE " +
//                   "    _id = old.thread_id " +
//                   "    AND _id NOT IN " +
//                   "    (SELECT thread_id FROM sms " +
//                   "     UNION SELECT thread_id from pdu " +
//                   "   ); " +
//                   "END;");
//
//        // Insert pending status for M-Notification.ind or M-ReadRec.ind
//        // when they are inserted into Inbox/Outbox.
//        db.execSQL("CREATE TRIGGER insert_mms_pending_on_insert " +
//                   "AFTER INSERT ON pdu " +
//                   "WHEN new." + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND +
//                   "  OR new." + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_READ_REC_IND +
//                   " " +
//                   "BEGIN " +
//                   "  INSERT INTO " + MmsSmsProvider.TABLE_PENDING_MSG +
//                   "    (" + PendingMessages.PROTO_TYPE + "," +
//                   "     " + PendingMessages.MSG_ID + "," +
//                   "     " + PendingMessages.MSG_TYPE + "," +
//                   "     " + PendingMessages.ERROR_TYPE + "," +
//                   "     " + PendingMessages.ERROR_CODE + "," +
//                   "     " + PendingMessages.RETRY_INDEX + "," +
//                   "     " + PendingMessages.DUE_TIME + ") " +
//                   "  VALUES " +
//                   "    (" + MmsSms.MMS_PROTO + "," +
//                   "      new." + BaseColumns._ID + "," +
//                   "      new." + Mms.MESSAGE_TYPE + ",0,0,0,0);" +
//                   "END;");
//
//        // Insert pending status for M-Send.req when it is moved into Outbox.
//        db.execSQL("CREATE TRIGGER insert_mms_pending_on_update " +
//                   "AFTER UPDATE ON pdu " +
//                   "WHEN new." + Mms.MESSAGE_TYPE + "=" + PduHeaders.MESSAGE_TYPE_SEND_REQ +
//                   "  AND new." + Mms.MESSAGE_BOX + "=" + Mms.MESSAGE_BOX_OUTBOX +
//                   "  AND old." + Mms.MESSAGE_BOX + "!=" + Mms.MESSAGE_BOX_OUTBOX + " " +
//                   "BEGIN " +
//                   "  INSERT INTO " + MmsSmsProvider.TABLE_PENDING_MSG +
//                   "    (" + PendingMessages.PROTO_TYPE + "," +
//                   "     " + PendingMessages.MSG_ID + "," +
//                   "     " + PendingMessages.MSG_TYPE + "," +
//                   "     " + PendingMessages.ERROR_TYPE + "," +
//                   "     " + PendingMessages.ERROR_CODE + "," +
//                   "     " + PendingMessages.RETRY_INDEX + "," +
//                   "     " + PendingMessages.DUE_TIME + ") " +
//                   "  VALUES " +
//                   "    (" + MmsSms.MMS_PROTO + "," +
//                   "      new." + BaseColumns._ID + "," +
//                   "      new." + Mms.MESSAGE_TYPE + ",0,0,0,0);" +
//                   "END;");
//
//        // When a message is moved out of Outbox, delete its pending status.
//        db.execSQL("CREATE TRIGGER delete_mms_pending_on_update " +
//                "AFTER UPDATE OF msg_box ON " + MmsProvider.TABLE_PDU + " " +
//                   "WHEN old." + Mms.MESSAGE_BOX + "=" + Mms.MESSAGE_BOX_OUTBOX +
//                   "  AND new." + Mms.MESSAGE_BOX + "!=" + Mms.MESSAGE_BOX_OUTBOX + " " +
//                   "BEGIN " +
//                   "  DELETE FROM " + MmsSmsProvider.TABLE_PENDING_MSG +
//                   "  WHERE " + PendingMessages.MSG_ID + "=new._id; " +
//                   "END;");
//
//        // Delete pending status for a message when it is deleted.
//        db.execSQL("CREATE TRIGGER delete_mms_pending_on_delete " +
//                   "AFTER DELETE ON " + MmsProvider.TABLE_PDU + " " +
//                   "BEGIN " +
//                   "  DELETE FROM " + MmsSmsProvider.TABLE_PENDING_MSG +
//                   "  WHERE " + PendingMessages.MSG_ID + "=old._id; " +
//                   "END;");
//
//        // TODO Add triggers for SMS retry-status management.
//
//        // Update the error flag of threads when the error type of
//        // a pending MM is updated.
//        db.execSQL("CREATE TRIGGER update_threads_error_on_update_mms " +
//                   "  AFTER UPDATE OF err_type ON pending_msgs " +
//                   "  WHEN (OLD.err_type < 10 AND NEW.err_type >= 10 AND NEW.proto_type = " + MmsSms.MMS_PROTO + " AND NEW.msg_type = " + PduHeaders.MESSAGE_TYPE_SEND_REQ + ")" +
//                   "    OR (OLD.err_type >= 10 AND NEW.err_type < 10) " +
//                   "BEGIN" +
//                   "  UPDATE threads SET error = " +
//                   "    CASE" +
//                   "      WHEN NEW.err_type >= 10 THEN error + 1" +
//                   "      ELSE error - 1" +
//                   "    END " +
//                   "  WHERE _id =" +
//                   "   (SELECT DISTINCT thread_id" +
//                   "    FROM pdu" +
//                   "    WHERE _id = NEW.msg_id ); " +
//                   "END;");
//
//        // Update the error flag of threads when delete pending message.
//        db.execSQL("CREATE TRIGGER update_threads_error_on_delete_mms " +
//                   "  BEFORE DELETE ON pdu" +
//                   "  WHEN OLD._id IN (SELECT DISTINCT msg_id" +
//                   "                   FROM pending_msgs" +
//                   "                   WHERE err_type >= 10 AND msg_type = 128) " +
//                   "BEGIN " +
//                   "  UPDATE threads SET error = error - 1" +
//                   "  WHERE _id = OLD.thread_id; " +
//                   "END;");
//
//        // Update the error flag of threads while moving an MM out of Outbox,
//        // which was failed to be sent permanently.
//        db.execSQL("CREATE TRIGGER update_threads_error_on_move_mms " +
//                   "  BEFORE UPDATE OF msg_box ON pdu " +
//                   "  WHEN (OLD.msg_box = 4 AND NEW.msg_box != 4) " +
//                   "  AND (OLD._id IN (SELECT DISTINCT msg_id" +
//                   "                   FROM pending_msgs" +
//                   "                   WHERE err_type >= 10)) " +
//                   "BEGIN " +
//                   "  UPDATE threads SET error = error - 1" +
//                   "  WHERE _id = OLD.thread_id; " +
//                   "END;");
//
//        // Update the error flag of threads after a text message was
//        // failed to send/receive.
//        db.execSQL("CREATE TRIGGER update_threads_error_on_update_sms " +
//                   "  AFTER UPDATE OF type ON sms" +
//                   "  WHEN (OLD.type != 5 AND NEW.type = 5)" +
//                   "    OR (OLD.type = 5 AND NEW.type != 5) " +
//                   "BEGIN " +
//                   "  UPDATE threads SET error = " +
//                   "    CASE" +
//                   "      WHEN NEW.type = 5 THEN error + 1" +
//                   "      ELSE error - 1" +
//                   "    END " +
//                   "  WHERE _id = NEW.thread_id; " +
//                   "END;");
//        //added by lengxibo for Rom3.1 2016.07.29
//        db.execSQL("CREATE TRIGGER snippet_verify_code_update AFTER " +
//                "UPDATE OF snippet "  +
//                "ON threads " +
//                "WHEN OLD.verify_code = 0 " +
//                "BEGIN " +
//                "UPDATE threads SET snippet_verify_code = NEW.snippet " +
//                "WHERE _id = NEW._id; " +
//                "END;");
//        //added by lengxibo for Rom3.1 end 2016.07.29
//    }
//
//    /**
//     * added by lengxibo for notification mms 2017.07.29
//     * @param db
//     */
//    private void createNoticeTables(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTICE
//                + " ("
//                + COL_NOTICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_NOTICE_ADDRESS + " TEXT UNIQUE,"
//                + COL_NOTICE_NAME + " TEXT,"
//                + COL_NOTICE_INLINE + " INTEGER,"//0表示查到的号码，1表示是内置数据，2表示是陌生号码
//                + COL_NOTICE_DATE + " INTEGER DEFAULT 0,"
//                + COL_NOTICE_LOGO_ID +" INTEGER DEFAULT 0,"
//                + COL_NOTICE_FROM +" INTEGER DEFAULT 0"
//                + ");");
//
//        db.execSQL("CREATE TABLE " + TABLE_LOGO_RES + "("
//                + COL_LOGO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_LOGO_URL + " TEXT,"
//                + COL_LOGO_HASH + " TEXT UNIQUE,"
//                + COL_LOGO_LAST_TIME +" INTEGER,"
//                + COL_LOGO_EXISTS + " INTEGER DEFAULT 0"
//                + ");");
//    }
//    private void createNoticeMenuTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NOTICE_MENU
//                + " ("
//                + COL_NOTICE_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_NOTICE_MENU_SHOP_ID + " TEXT,"
//                + COL_NOTICE_MENU_CONTENT + " TEXT,"
//                + COL_NOTICE_MENU_NAME + " TEXT,"
//                + COL_NOTICE_MENU_LAST_TIME + " INTEGER,"
//                + COL_NOTICE_MENU_AGENCY +" TEXT"
//                + ");");
//    }
//
//    /**
//     * added by yanweihao for push mms 2018.04.13
//     * @param db
//     */
//    private void createPushMmsTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PUSH_MMS
//                + " ("
//                + COL_PUSH_MMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_PUSH_MMS_THREAD_ID + " INTEGER,"
//                + COL_PUSH_MMS_SHOP_ID + " TEXT,"
//                + COL_PUSH_MMS_MSG_ID + " TEXT,"
//                + COL_PUSH_MMS_NUMBER + " TEXT,"
//                + COL_PUSH_MMS_NAME + " TEXT,"
//                + COL_PUSH_MMS_CONTENT + " TEXT,"
//                + COL_PUSH_MMS_MSG_JSON + " TEXT,"
//                + COL_PUSH_MMS_MEDIA_TYPE + " INTEGER,"
//                + COL_PUSH_MMS_READ_TYPE + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_SEND_STATE + " INTEGER,"
//                + COL_PUSH_MMS_RECEIVE_TYPE + " INTEGER DEFAULT 1,"
//                + COL_PUSH_MMS_NOTIFY_TYPE + " INTEGER DEFAULT 1,"
//                + COL_PUSH_MMS_SCENE_TYPE + " INTEGER DEFAULT 1,"
//                + COL_PUSH_MMS_DATE + " INT8,"
//                + COL_PUSH_MMS_SOURCE + " TEXT,"
//                + COL_PUSH_MMS_SEEN + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_TYPE + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_SUBID+" INTEGER DEFAULT -1,"
//                + COL_PUSH_MMS_BUBBLE+" TEXT DEFAULT -1,"
//                + COL_PUSH_MMS_BUBBLE_TYPE+" INTEGER DEFAULT 1,"
//                + COL_PUSH_MMS_SP_ID +" INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_EXTRA + " TEXT,"
//                + COL_PUSH_MMS_RISK_WEBSITE + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_BUBBLE_PARSE_TIME + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_VERIFY_CODE + " TEXT,"
//                + COL_PUSH_MMS_READ_TYPE_ALIAS + " INTEGER DEFAULT 0,"
//                + COL_PUSH_MMS_SEND_STATE_ALIAS + " INTEGER DEFAULT -1,"
//                + COL_PUSH_MMS_RECEIVE_TYPE_ALIAS + " INTEGER DEFAULT 1,"
//                + COL_PUSH_MMS_ID_EX + " TEXT,"
//                + COL_PUSH_MMS_DYNAMIC_BUBBLE + " TEXT DEFAULT -1,"
//                + COL_PUSH_MMS_DYNAMIC_BUBBLE_DATE + " INTEGER DEFAULT 0,"
//                + ICommonData.COL_THREAD_KEY + " TEXT,"
//                + ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.NOTICE + " ,"
//                + COL_PUSH_MMS_EXTEND_TYPE + " INTEGER DEFAULT 0"
//                + ");");
//    }
//
//    private void createPushShopTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PUSH_SHOP
//                + " ("
//                + COL_PUSH_SHOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_PUSH_SHOP_THREAD_ID + " INTEGER,"
//                + COL_PUSH_SHOP_SHOP_ID + " TEXT,"
//                + COL_PUSH_SHOP_NUMBER + " TEXT,"
//                + COL_PUSH_SHOP_NAME + " TEXT,"
//                + COL_PUSH_SHOP_LOGO + " TEXT,"
//                + COL_PUSH_SHOP_MENU + " TEXT,"
//                + COL_PUSH_SHOP_DESC + " TEXT,"
//                + COL_PUSH_SHOP_IS_IN_BLACK + " INTEGER DEFAULT 0,"
//                + COL_PUSH_SHOP_IS_ENCRYPTED + " INTEGER DEFAULT 0,"
//                + COL_PUSH_SHOP_BLACK_UPLOADED + " INTEGER DEFAULT 1,"
//                + COL_PUSH_SHOP_NOTIFICATION + " INTEGER DEFAULT 1,"
//                + COL_PUSH_SHOP_NOTIFICATION_UPLOADED + " INTEGER DEFAULT 1,"
//                + COL_PUSH_SHOP_MENU_RECEIVED_TIME + " INT8,"
//                + COL_PUSH_MMS_SOURCE + " TEXT,"
//                + COL_PUSH_SHOP_TYPE + " INTEGER DEFAULT 0,"
//                + ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.DEFAULT + " "
//                + ");");
//    }
//
//    private void createPushSyncTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PUSH_SYNC
//                + " ("
//                + COL_PUSH_SYNC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_PUSH_SYNC_SHOP_ID + " TEXT,"
//                + COL_PUSH_SYNC_TYPE + " INTEGER DEFAULT " + IPushData.BlackValue.IN_BLACK_LIST_VALUE + ","
//                + COL_PUSH_SYNC_DATE + " INT8 "
//                + ");");
//    }
//
//    /**
//     * added by yanweihao for iroaming 2018.08.22
//     * @param db
//     */
//    private void createIRoamingTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_IROAMING_SUPPORT
//                + " ("
//                + COL_IROAMING_SUPPORT_ID + " INTEGER,"
//                + COL_IROAMING_SUPPORT_DEEPLINK + " TEXT,"
//                + COL_IROAMING_SUPPORT_COUNTRY_CODE + " TEXT,"
//                + COL_IROAMING_SUPPORT_VERSION_TYPE + " INTERGER DEFAULT 0"
//                + ");");
//    }
//
//    /**
//     * added by yanweihao for wifi-push list 2018.10.16
//     * @param db
//     */
//    private void createWifiPushListTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_WIFI_PUSH
//                + " ("
//                + COL_WIFI_PUSH_BSSID + " TEXT,"
//                + COL_WIFI_PUSH_TYPE + " INTEGER,"
//                + COL_WIFI_PUSH_EXPIRY_DATE + " INTERGER DEFAULT 0,"
//                + COL_WIFI_PUSH_LAST_UPDATE_TIME + " INTERGER DEFAULT 0"
//                + ");");
//    }
//
//    /**
//     * added by yanglei for push-frequency list 2018.10.25
//     * @param db
//     */
//    private void createFrequencyTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(TABLE_NAME_FREQUENCY);
//        sb.append(" (");
//        sb.append(COL_IROAMING_SUPPORT_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(COL_FREQUENCY_NAME);
//        sb.append(" TEXT, ");
//        sb.append(COL_FREQUENCY_TYPE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(COL_FREQUENCY_COUNT);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(COL_FREQUENCY_REQUEST_TIME);
//        sb.append(" INTEGER  DEFAULT 0 ");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for push-sp-num list 2018.11.09
//     * @param db
//     */
//    private void createPushSpNumTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(TABLE_NAME_PUSH_SP_NUM);
//        sb.append(" (");
//        sb.append(COL_IROAMING_SUPPORT_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(COL_SP_NUM);
//        sb.append(" TEXT, ");
//        sb.append(COL_SP_TYPE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(COL_VERSION);
//        sb.append(" INTEGER  DEFAULT 0");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for  2018.11.10:号码寻址表
//     * @param db
//     */
//    private void createPushFindPhoneNumTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_CONTENT);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_NUMBER);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_CONTENT_LENGTH);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_ICCID);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_SUB_ID);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_SP_TYPE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_STATUS);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_FIND_TIME);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushFindPhoneNumColumns.COL_RECEIVE_TIME);
//        sb.append(" INTEGER  DEFAULT 0");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for  2018.11.10:号码寻址结果表
//     * @param db
//     */
//    private void createPushResultPhoneNumTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_ICCID);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_SUB_ID);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_SP_TYPE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_STATUS);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushResultPhoneNumColumns.COL_UPDATE_TIME);
//        sb.append(" INTEGER  DEFAULT 0");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for  2019.01.23:push sms临时表
//     * @param db
//     */
//    private void createPushSmsTempTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.PushMmsTempColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.PushMmsTempColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.PushMmsTempColumns.COL_MSG_ID);
//        sb.append(" TEXT UNIQUE, ");
//        sb.append(IDataBaseColunms.PushMmsTempColumns.COL_JSON);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushMmsTempColumns.COL_RECEIVE_TIME);
//        sb.append(" INTEGER  DEFAULT 0");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for  2019.03.23:smart_sms_engine运营商引擎表配置表
//     * @param db
//     */
//    private void createSmartSmsEngineConfigTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.SmartSmsEngineConfigColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.SmartSmsEngineConfigColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineConfigColumns.COL_MENU_ENGINE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineConfigColumns.COL_CARD_ENGINE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineConfigColumns.COL_ENGINE_NAME);
//        sb.append(" TEXT UNIQUE ");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanglei for  2019.03.28:smart_sms_engine_blacklist运营商引擎黑名单表
//     * @param db
//     */
//    private void createSmartSmsEngineBlackListTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.SmartSmsEngineBlackListColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.SmartSmsEngineBlackListColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineBlackListColumns.COL_VERSION);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineBlackListColumns.COL_ENGINE_NAME);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.SmartSmsEngineBlackListColumns.COL_BLACK);
//        sb.append(" TEXT ");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//
//    /**
//     * added by yanweihao 2019.03.23  for 记录运营商识别来源
//     * @param db
//     */
//    private void addAddressFromToThreads(SQLiteDatabase db){
//        if (!isColumnExist(db, "threads", COL_THREADS_V_ADDRESS_FROM)) {
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + COL_THREADS_V_ADDRESS_FROM + " INTEGER DEFAULT -1");
//        }
//    }
//
//    /**
//     * added by zhouyinghui 2019.04.03.for 记录卡片解析时间
//     * @param db
//     */
//    private void addBubbleParseTimeTosmsTab(SQLiteDatabase db){
//        if (!isColumnExist(db, "sms", COL_PUSH_MMS_BUBBLE_PARSE_TIME)) {
//            db.execSQL("ALTER TABLE sms ADD COLUMN bubble_parse_time INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_BUBBLE_PARSE_TIME)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_BUBBLE_PARSE_TIME+" INTEGER DEFAULT 0;");
//        }
//    }
//
//    /**
//     * added by zhouyinghui 2019.06.03.for 记录商户名称和logo识别来源
//     * @param db
//     */
//    private void addNoticeFromToNoticeTab(SQLiteDatabase db){
//        if (!isColumnExist(db, "notice", COL_NOTICE_FROM)) {
//            db.execSQL("ALTER TABLE notice ADD COLUMN notice_from INTEGER DEFAULT 0");
//        }
//    }
//
//    /**
//     * added by yanglei for  2019.04.08:号码寻址结果表
//     * @param db
//     */
//    private void createPushBindSimNumTables(SQLiteDatabase db){
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.TABLE_NAME);
//        sb.append(" (");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_ID);
//        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_ICCID);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_CONTENT);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_FROM);
//        sb.append(" TEXT, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_SUB_ID);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_TYPE);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_STATUS);
//        sb.append(" INTEGER  DEFAULT 0, ");
//        sb.append(IDataBaseColunms.PushBindSimNumColumns.COL_TIME);
//        sb.append(" INTEGER  DEFAULT 0");
//        sb.append("); ");
//        db.execSQL(sb.toString());
//    }
//    /**
//     * newVersion >= version: 这个的意思是升级到的新版本必须要大于指定的版本,
//     * 比如：代码中有升级到7400，8000，8400的升级代码。如果你手机中当前的版本为7000，
//     * 想升级到8000，这时候就会判断(8000 >= 7400),(8000 >= 7400)执行7400，8000的升级
//     * (8000 >= 8400)条件为false,不执行8400的升级。
//     *
//     * @param oldVersion
//     * @param newVersion
//     * @param version
//     * @return
//     */
//    private static boolean isUpgradeRequired(int oldVersion, int newVersion, int version) {
//        return oldVersion < version && newVersion >= version;
//    }
//
//    private void upgradeDatabaseToVersion1002(SQLiteDatabase db){
//    	LogUtil.getInstance().d(TAG, "upgrade database to version 1002.");
//    	db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_delete_part");
//    }
//
//    private void upgradeDatabaseToVersion1003(SQLiteDatabase db){
//        LogUtil.getInstance().d(TAG, "upgrade database to version 1003.");
//        db.execSQL("DROP TRIGGER IF EXISTS pdu_update_thread_on_insert ");
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_on_insert");
//        db.execSQL("DROP TRIGGER IF EXISTS pdu_update_thread_date_subject_on_update ");
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_date_subject_on_update ");
//        db.execSQL("DROP TRIGGER IF EXISTS mms_update_thread_unread_on_update ");
//        if(ProviderUtil.isMultiSimEnabled(mContext)){
//            db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_sim_id_on_insert");
//            db.execSQL("DROP TRIGGER IF EXISTS mms_update_thread_sim_id_on_insert ");
//             db.execSQL("DROP TRIGGER IF EXISTS mms_update_thread_sim_id_on_delete ");
//             db.execSQL("DROP TRIGGER IF EXISTS mms_update_thread_sim_id_on_update ");
//        }
//        db.execSQL("DROP TRIGGER IF EXISTS pdu_update_thread_read_on_update ");
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_read_on_update ");
//        db.execSQL("DROP TRIGGER IF EXISTS pdu_update_thread_on_delete ");
//        db.execSQL("DROP TRIGGER IF EXISTS delete_obsolete_threads_pdu ");
//        db.execSQL("DROP TRIGGER IF EXISTS delete_obsolete_threads_when_update_pdu ");
//        db.execSQL("DROP TRIGGER IF EXISTS insert_mms_pending_on_insert " );
//        db.execSQL("DROP TRIGGER IF EXISTS insert_mms_pending_on_update " );
//        db.execSQL("DROP TRIGGER IF EXISTS delete_mms_pending_on_update " );
//        db.execSQL("DROP TRIGGER IF EXISTS delete_mms_pending_on_delete " );
//        db.execSQL("DROP TRIGGER IF EXISTS update_threads_error_on_update_mms " );
//        db.execSQL("DROP TRIGGER IF EXISTS update_threads_error_on_delete_mms " );
//        db.execSQL("DROP TRIGGER IF EXISTS update_threads_error_on_move_mms " );
//        db.execSQL("DROP TRIGGER IF EXISTS update_threads_error_on_update_sms " );
//        createCommonTriggers(db);
//    }
//
//    //added by haipeng for 信息列表背景色 2013.9.15
//	private void upgradeDatabaseToVersion1004(SQLiteDatabase db){
//        LogUtil.getInstance().d(TAG,"upgradeDatabaseToVersion1004");
//        db.execSQL("ALTER TABLE threads ADD COLUMN color TEXT");
//        db.execSQL("ALTER TABLE threads ADD COLUMN topindex INTEGER");
//    }
//
//	//added by huangjm for Rom数据库升级，加密信息强制解密 2013.10.16
//	private void upgradeDatabaseToVersion1005(SQLiteDatabase db){
//        //this upgrade is for normal version to rom version,
//        //force convert encrypted message to unencrypted message
//        //so that keep all message didn't disappear in Mms app
//		//modified by liuxinglin for Force decrypte encrypted msg;set default value of topindex 2013.10.16
//        LogUtil.getInstance().d(TAG,"upgradeDatabaseToVersion1005");
//        updateAllEncryptedDatabases(db, false);//desencrypt all encryted message
//        db.execSQL("UPDATE threads SET topindex = -1");
//        db.execSQL("UPDATE threads SET color = 0"); //added by liuhaipeng for default value 2013.10.16
//        LogUtil.getInstance().d(TAG, "upgrade to V1005 finished");
//        //modified end liuxinglin
//	}
//	private void upgradeDatabaseToVersion1006(SQLiteDatabase db){
//        //this upgrade is for after visitor change ,the recent can't see for new outgoing
//        LogUtil.getInstance().d(TAG,"upgradeDatabaseToVersion1006");
//        db.execSQL("ALTER TABLE recents ADD COLUMN is_secret INTEGER");
//        db.execSQL("UPDATE recents SET is_secret = 0");
//        LogUtil.getInstance().d(TAG, "upgrade to V1006 finished");
//	}
//
//	//added by leiyaotao for 信息：手机接收不到长短信。2013.12.20
//	private void upgradeDatabaseToVersion1007(SQLiteDatabase db){
//        //this upgrade is for after visitor change ,the recent can't see for new outgoing
//        LogUtil.getInstance().d(TAG,"upgradeDatabaseToVersion1007");
//        db.execSQL("ALTER TABLE raw ADD COLUMN recv_time INTEGER");
//        db.execSQL("ALTER TABLE raw ADD COLUMN upload_flag INTEGER");
//        db.execSQL("UPDATE raw SET recv_time = 0");
//        db.execSQL("UPDATE raw SET upload_flag = 0");
//        LogUtil.getInstance().d(TAG, "upgrade to V1007 finished");
//	}
//
//	//added by huangjm for Rom1.5隐私信息数据库修改 2014.3.10
//	private void upgradeDatabaseToVersion1008(SQLiteDatabase db){
//		//this upgrade is for alter table(sms) encrypted as is_encrypted , and descrypted all encrypted message
//        LogUtil.getInstance().d(TAG,"upgradeDatabaseToVersion1008");
//        db.execSQL("ALTER TABLE sms RENAME TO oldsms");
//        db.execSQL("CREATE TABLE sms (" +
//                "_id INTEGER PRIMARY KEY," +
//                "thread_id INTEGER," +
//                "address TEXT," +
//                "m_size INTEGER," +
//                "person INTEGER," +
//                "date INTEGER," +
//                "date_sent INTEGER DEFAULT 0," +
//                "protocol INTEGER," +
//                "read INTEGER DEFAULT 0," +
//                "status INTEGER DEFAULT -1," + // a TP-Status value
//                                               // or -1 if it
//                                               // status hasn't
//                                               // been received
//                "type INTEGER," +
//                "reply_path_present INTEGER," +
//                "subject TEXT," +
//                "body TEXT," +
//                "service_center TEXT," +
//                "locked INTEGER DEFAULT 0," +
//                "sub_id INTEGER DEFAULT -1," +  //sim_id change sub_id.modified by lengxibo for Android 5.0 2015.01.01
//                "error_code INTEGER DEFAULT 0," +
//                "seen INTEGER DEFAULT 0," +
//				"is_encrypted INTEGER DEFAULT 0,"+  //Add by lk  2011-03-08
//                "time INTEGER DEFAULT 0,"+ //添加一个加密字段
//                "dirty INTEGER DEFAULT 1,"+
//                PrivacyFilterParams.MESSAGE_MODE + " INTEGER DEFAULT 0"+ //add by zouyongjun/BBK
//     			");");
//        db.execSQL("INSERT INTO sms SELECT * FROM oldsms");
//        db.execSQL("DROP TABLE oldsms");
//        reCreateSmsTableRelativeTrigger(db);
//        updateAllEncryptedDatabases(db, true);//desencrypt all encryted message
//        LogUtil.getInstance().d(TAG, "upgrade to V1008 finished");
//	}
//
//	//add by fuleilei for save sms in temp sms_raw table and make sure it display to users 2014.10.24
//	private void upgradeDatabaseToVersion1009 (SQLiteDatabase db) {
//		LogUtil.getInstance().d(TAG, "upgrade to V1009 finished");
//		if (isColumnExist(db, "sms_raw", "_id")) {
//			VLog.d(TAG, "sms_raw is already exits");
//			return;
//		}
//		db.execSQL("CREATE TABLE sms_raw (" +
//                "_id INTEGER PRIMARY KEY," +
//                "address TEXT," +
//                "date INTEGER," +
//                "protocol INTEGER," +
//                "read INTEGER DEFAULT 0," +
//                "status INTEGER DEFAULT -1," + // a TP-Status value
//                                               // or -1 if it
//                                               // status hasn't
//                                               // been received
//                "type INTEGER," +
//                "reply_path_present INTEGER," +
//                "subject TEXT," +
//                "body TEXT," +
//                "service_centesub_id," +
//                "locked INTEGER DEFAULT 0," +
//                "sub_id INTEGER DEFAULT -1," +  //sim_id change sub_id.modified by lengxibo for Android 5.0 2015.01.01
//                "error_code INTEGER DEFAULT 0," +
//                "seen INTEGER DEFAULT 0," +
//                "time INTEGER DEFAULT 0,"+ //添加一个加密字段
//                "dirty INTEGER DEFAULT 1"+
//     			");");
//	}
//	//add end fuleilei
//
//	//add by fuleilei @ 20141110 @ add column from qcom to Compatible with any third party
//	private void upgradeDatabaseToVersion1010 (SQLiteDatabase db) {
//		if (!isColumnExist(db, "sms", "priority")) {
//			db.execSQL("ALTER TABLE sms ADD COLUMN priority INTEGER DEFAULT -1");
//		}
//		if (!isColumnExist(db, "sms", "sub_id")) {
//			db.execSQL("ALTER TABLE sms ADD COLUMN sub_id INTEGER DEFAULT -1");
//		}
//		if (!isColumnExist(db, "pdu", "sub_id")) {
//			db.execSQL("ALTER TABLE pdu ADD COLUMN sub_id INTEGER DEFAULT -1");
//		}
//		VLog.d(TAG, "upgrade to V1010 finished");
//	}
//	//add end fuleilei@ 20141110 @ add column from qcom to Compatible with any third party
//
//	/**
//	 * added by lengxibo for Andorid5.0 2015.01.10
//	 * @param db
//	 */
//	private void upgradeDatabaseToVersion2011(SQLiteDatabase db) {
//		if (!isColumnExist(db, "sms", "creator")) {
//			db.execSQL("ALTER TABLE sms ADD COLUMN creator TEXT");
//		}
//
//		if (!isColumnExist(db, "pdu", "st_ext")) {
//			db.execSQL("ALTER TABLE pdu ADD COLUMN st_ext INTEGER DEFAULT 0");
//		}
//
//		if (!isColumnExist(db, "pdu", "creator")) {
//			db.execSQL("ALTER TABLE pdu ADD COLUMN creator TEXT");
//		}
//
//		if (!isColumnExist(db, "threads", Threads.ARCHIVED)) {
//			db.execSQL("ALTER TABLE threads ADD COLUMN " + Threads.ARCHIVED + " INTEGER DEFAULT 0");
//		}
//
//		if (isColumnExist(db, "threads", "sim_id")) {
//			db.execSQL("ALTER TABLE threads ADD COLUMN sub_id INTEGER DEFAULT -1");
//		}
//
//		/* todo open if need lengxibo 2015.01.10
//		 * if (!isColumnExist(db, "sms", "ipmsg_id")) {
//			db.execSQL("ALTER TABLE sms ADD COLUMN ipmsg_id INTEGER DEFAULT 0");
//			db.execSQL("ALTER TABLE sms ADD COLUMN ref_id INTEGER");
//			db.execSQL("ALTER TABLE sms ADD COLUMN total_len INTEGER");
//			db.execSQL("ALTER TABLE sms ADD COLUMN rec_len INTEGER");
//		}*/
//
//		db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_insert_part");
//		db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_update_part");
//		db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_delete_part");
//		db.execSQL("DROP TRIGGER IF EXISTS update_threads_on_update_pdu");
//		VLog.d(TAG, "upgrade to V2011 finished");
//	}
//
//	//add by maoyuanze for upgrade the db for android 5.1
//	private void upgradeDatabaseToVersion2031(SQLiteDatabase db) {
//        if (!isColumnExist(db, "sms", "phone_id")) {
//            db.execSQL("ALTER TABLE sms ADD COLUMN phone_id INTEGER DEFAULT -1");
//        }
//
//        if (!isColumnExist(db, "pdu", "phone_id")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN phone_id INTEGER DEFAULT -1");
//        }
//        VLog.d(TAG, "upgrade to V2031 finished");
//	}
//
//    private void upgradeDatabaseToVersion2035(SQLiteDatabase db){
//        if(!isColumnExist(db,"sms","is_exec_trigger")){
//            db.execSQL("ALTER TABLE sms ADD COLUMN is_exec_trigger INTEGER DEFAULT 1");
//        }
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_on_insert");
//        String updateSimId = "";
//        if (ProviderUtil.isMultiSimEnabled(mContext)) {
//            updateSimId = "    sub_id = new.sub_id, ";
//        }
//        String updateThreadTable = "BEGIN" +
//                "  UPDATE threads SET" +
//                "    date = new." + Sms.DATE + ", " +
//                updateSimId +
//                " time = new.time, "+  //add by liukai
//                "    snippet = new." + Sms.BODY + ", " +
//                "    snippet_cs = 0" +
//                "  WHERE threads._id = new." + Sms.THREAD_ID + " ; " +
//                UPDATE_THREAD_COUNT_ON_NEW +
//                UPDATE_THREAD_UNREADCOUNT_ON_NEW +   //add by lk 2011-07-22
//                SMS_UPDATE_THREAD_READ_BODY +
//                UPDATE_THREAD_READ_COUNT+
//                "END;";
//        db.execSQL("CREATE TRIGGER sms_update_thread_on_insert AFTER INSERT ON sms when new.is_exec_trigger =1 " +
//                updateThreadTable);
//    }
//
//    private void upgradeDatabaseToVersion2036(SQLiteDatabase db) {
//        if (!isColumnExist(db, "pdu", "prepared_type")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN prepared_type INTEGER DEFAULT -1");
//        }
//        if (!isColumnExist(db, "pdu", "prepared_body")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN prepared_body TEXT");
//        }
//        if (!isColumnExist(db, "pdu", "prepared_width")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN prepared_width INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, "pdu", "prepared_height")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN prepared_height INTEGER DEFAULT 0");
//        }
//        VLog.d(TAG, "upgrade to V2036 finished");
//    }
//
//  //add by liuhaipeng @ 2016-0630 @ add column from the third part to add the smsEntityJSON String
//    private void upgradeDatabaseToVersion2037 (SQLiteDatabase db) {
//        if (!isColumnExist(db, "sms", "bubble")) {
//           db.execSQL("ALTER TABLE sms ADD COLUMN bubble TEXT DEFAULT -1");
//        }
//        VLog.d(TAG, "upgrade to V2037 finished");
//    }
//    //add end fuleilei@ 20141110 @ add column from qcom to Compatible with any third party
//
//    /**
//     * added by lengxibo for Rom3.1 2016.07.29
//     * @param db
//     */
//    private void upgradeDatabaseToVersion2047 (SQLiteDatabase db) {
//        if (!isColumnExist(db, "sms", "verify_code")) {
//           db.execSQL("ALTER TABLE sms ADD COLUMN verify_code INTEGER DEFAULT 0");
//           db.execSQL("ALTER TABLE sms ADD COLUMN risk_website INTEGER DEFAULT 0");
//        }
//
//        if (!isColumnExist(db, "pdu", "risk_website")) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN risk_website INTEGER DEFAULT 0");
//        }
//
//        if (!isColumnExist(db, "threads", "verify_code")) {
//            db.execSQL("ALTER TABLE threads ADD COLUMN verify_code INTEGER DEFAULT 0");
//            db.execSQL("ALTER TABLE threads ADD COLUMN snippet_verify_code TEXT");
//        }
//
//        db.execSQL("DROP TRIGGER IF EXISTS snippet_verify_code_update " );
//        db.execSQL("CREATE TRIGGER snippet_verify_code_update AFTER " +
//                "UPDATE OF snippet "  +
//                "ON threads " +
//                "WHEN OLD.verify_code = 0 " +
//                "BEGIN " +
//                "UPDATE threads SET snippet_verify_code = new.snippet " +
//                "WHERE _id = NEW._id; " +
//                "END;");
//
//        VLog.d(TAG, "upgrade to V2038 finished");
//    }
//
//    private void upgradeDatabaseToVersion2070(SQLiteDatabase db) {
//        //从ROM2.5或FUNTOUCHOS3.0 LITE升级上来后，需要将原来被隐藏的联系人对应的信息给释放出来，以可显示的隐私信息展现
//        db.execSQL("UPDATE sms set is_encrypted =1 WHERE is_encrypted=2");
//        db.execSQL("UPDATE pdu set is_encrypted =1 WHERE is_encrypted=2");
//        db.execSQL("UPDATE threads set is_encrypted =1 WHERE is_encrypted=2");
//        //end
//        VLog.d(TAG, "upgrade to V2070 finished");
//    }
//
//    private static final long BEGINTIME = 1483200000;//2017.01.01 00:00:00
//
//    private void upgradeDatabaseToVersion2049(SQLiteDatabase db) {
//        String sql = "SELECT _id,sub FROM pdu WHERE date >= " + BEGINTIME;
//        Cursor cc = null;
//        try {
//            cc = db.rawQuery(sql, null);
//            if (cc != null) {
//                cc.moveToPosition(-1);
//                while (cc.moveToNext()) {
//                    int id = cc.getInt(0);
//                    String subject = cc.getString(1);
//                    if (!TextUtils.isEmpty(subject)) {
//                        String localSubject = reCheckEncodeForSubject(subject);
//                        if (!subject.equals(localSubject)) {
//                            ContentValues contentValues = new ContentValues(1);
//                            contentValues.put("sub", localSubject);
//                            db.update("pdu", contentValues, "_id = " + id, null);
//                        }
//                    }
//                }
//            }
//        } finally {
//            if (cc != null) {
//                cc.close();
//            }
//        }
//        VLog.d(TAG, "upgrade to V2049 finished");
//    }
//
//    /**
//     * added by leiyaotao for android7.0 2016.12.12
//     * @param db upgrade
//     */
//    private void upgradeDatabaseToVersion2147 (SQLiteDatabase db) {
//        // When a non-FBE device is upgraded to N, all MMS attachment files are moved from
//        // /data/data to /data/user_de. We need to update the paths stored in the parts table to
//        // reflect this change.
//        try {
//            String newPartsDirPath = mContext.getDir("parts", 0).getCanonicalPath();
//            // The old path of the part files will be something like this:
//            //   /data/data/0/com.android.providers.telephony/app_parts
//            // The new path of the part files will be something like this:
//            //   /data/user_de/0/com.android.providers.telephony/app_parts
//            int partsDirIndex = newPartsDirPath.lastIndexOf(
//                    File.separator, newPartsDirPath.lastIndexOf("parts"));
//            String partsDirName = newPartsDirPath.substring(partsDirIndex) + File.separator;
//            // The query to update the part path will be:
//            //   UPDATE part SET _data = '/data/user_de/0/com.android.providers.telephony' ||
//            //                           SUBSTR(_data, INSTR(_data, '/app_parts/'))
//            //   WHERE INSTR(_data, '/app_parts/') > 0
//            db.execSQL("UPDATE " + MmsProvider.TABLE_PART +
//                    " SET " + Part._DATA + " = '" + newPartsDirPath.substring(0, partsDirIndex) + "' ||" +
//                    " SUBSTR(" + Part._DATA + ", INSTR(" + Part._DATA + ", '" + partsDirName + "'))" +
//                    " WHERE INSTR(" + Part._DATA + ", '" + partsDirName + "') > 0");
//        } catch (IOException e){
//            VLog.e(TAG, "openFile: check file path failed " + e, e);
//        }
//
//        if (!isColumnExist(db, SmsProvider.TABLE_RAW, "deleted")) {
//            db.execSQL("ALTER TABLE " + SmsProvider.TABLE_RAW +" ADD COLUMN deleted INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, SmsProvider.TABLE_RAW, "message_body")) {
//            db.execSQL("ALTER TABLE " + SmsProvider.TABLE_RAW +" ADD COLUMN message_body TEXT");
//        }
//
//        VLog.d(TAG, "upgrade to V2048 finished");
//    }
///**
//     * add by maoyuanze 20170112 add column inverted_address
//     * @param db
//     */
//    private void upgradeDatabaseToVersion2148 (SQLiteDatabase db) {
//        if (!isColumnExist(db, "canonical_addresses", "inverted_address")) {
//           db.execSQL("ALTER TABLE canonical_addresses ADD COLUMN inverted_address TEXT");
//           updateCanonicalAddresses(db);
//        }
//        VLog.d(TAG, " upgrade to V2148 finished");
//    }
//
//    /**
//     * add by liuhaipeng 20170605 add column display_originating_addr
//     * @param db
//     */
//    private void upgradeDatabaseToVersion2152(SQLiteDatabase db) {
//        if (!isColumnExist(db, SmsProvider.TABLE_RAW, "display_originating_addr")) {
//            db.execSQL("ALTER TABLE " + SmsProvider.TABLE_RAW + " ADD COLUMN display_originating_addr TEXT");
//        }
//        VLog.d(TAG, " upgrade to V2152 finished");
//    }
//
//    /**
//     * added by lengxibo for notification mms 2017.07.29
//     * @param db
//     */
//    private void upgradeDatabaseToVersion7000(SQLiteDatabase db) {
//        if (!isColumnExist(db, "threads", COL_THREADS_V_ADDRESS_NAME)) {
//            createNoticeTables(db);
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + COL_THREADS_V_ADDRESS_NAME + " TEXT");
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + COL_THREADS_V_ADDRESS_TYPE + " INTEGER DEFAULT 0");
//        }
//
//        if (!LocaleUtils.isInternationalVersion()) {
//            String query = "SELECT threads._id, canonical_addresses.address FROM threads LEFT JOIN canonical_addresses ON threads.recipient_ids = canonical_addresses._id";
//            Cursor cursor = null;
//            HashSet<Integer> serviceSet = new HashSet<>();
//            try {
//                cursor = db.rawQuery(query, null);
//                if (cursor != null) {
//                    cursor.moveToPosition(-1);
//                    while (cursor.moveToNext()) {
//                        if (AddressUtils.isServiceNumber(cursor.getString(1))) {
//                            serviceSet.add(cursor.getInt(0));
//                        }
//                    }
//
//                    db.execSQL("UPDATE threads SET " + COL_THREADS_V_ADDRESS_TYPE + " = " + ADDRESS_TYPE_SERVICE + ", " + COL_THREADS_V_ADDRESS_NAME + " = '-' || _id " + " where _id IN " + ProviderUtil
//                            .SetToWhereString(serviceSet));
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//
//        VLog.d(TAG, " upgrade to V7000 finished");
//    }
//
//    /**
//     * added by lengxibo for smart card 2017.10.17
//     * @param db
//     */
//    private void upgradeDatabaseToVersion7400(SQLiteDatabase db) {
//        if (!LocaleUtils.isInternationalVersion()) {
//            db.execSQL("UPDATE sms SET bubble = -1");
//
//            boolean result = true;
//            try {
//                result = Settings.System.getInt(mContext.getContentResolver(), "verify_code_protected_enable", 1) == 1;
//            } catch (Exception e) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(e));
//            }
//            SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_XML_SETTING, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean(VERIFY_CODE_PROTECTED, result);
//            editor.apply();
//        }
//
//        VLog.d(TAG, " upgrade to V7400 finished");
//    }
//
//    /**
//     * added by gengshengen for B180116-472 2018.01.16
//     * @param db
//     */
//    private void upgradeDatabaseToVersion8405(SQLiteDatabase db) {
//        try {
//            db.execSQL("UPDATE threads SET v_address_name = '" + MmsSmsProvider.PERSONAL_NAME + "' where v_address_type = 0");
//        } catch (Exception e) {
//            VLog.e(TAG, " upgrade to V8405 exception: " + e);
//        }
//
//        VLog.d(TAG, " upgrade to V8405 finished");
//    }
//
//    /**
//     * add by lipeng for monternet portal.
//     * @param db
//     * {@link http://km.vivo.xyz/pages/viewpage.action?pageId=33402259}
//     */
//    private void upgradeDatabaseToVersion8530(SQLiteDatabase db){
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_TYPE)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_TYPE+" INTEGER DEFAULT 2;");
//            //db.execSQL("UPDATE push_mms set push_type = (select threads.v_address_type from threads where threads._id = push_mms.thread_id);");
//        }
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_SP_ID)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_SP_ID+" INTEGER DEFAULT 0;");
//            //db.execSQL("UPDATE push_mms set push_type = (select threads.v_address_type from threads where threads._id = push_mms.thread_id);");
//        }
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_SUBID)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_SUBID+" INTEGER DEFAULT -1;");
//            //db.execSQL("UPDATE push_mms set sub_id = (select threads.sub_id from threads where threads._id = push_mms.thread_id);");
//        }
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_BUBBLE)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_BUBBLE+" TEXT DEFAULT -1;");
//            //db.execSQL("UPDATE push_mms set sub_id = (select threads.sub_id from threads where threads._id = push_mms.thread_id);");
//        }
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_BUBBLE_TYPE)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_BUBBLE_TYPE+" INTEGER DEFAULT 1;");
//            //db.execSQL("UPDATE push_mms set sub_id = (select threads.sub_id from threads where threads._id = push_mms.thread_id);");
//        }
//
//        if (!isColumnExist(db,TABLE_NAME_PUSH_MMS,COL_PUSH_MMS_EXTRA)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_MMS+" ADD COLUMN "+COL_PUSH_MMS_EXTRA+" TEXT;");
//            //db.execSQL("UPDATE push_mms set sub_id = (select threads.sub_id from threads where threads._id = push_mms.thread_id);");
//        }
//
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_RISK_WEBSITE)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_RISK_WEBSITE + " INTEGER DEFAULT 0;");
//        }
//
//        if (!isColumnExist(db,TABLE_NAME_PUSH_SHOP,COL_PUSH_SHOP_TYPE)) {
//            db.execSQL("ALTER TABLE "+TABLE_NAME_PUSH_SHOP+" ADD COLUMN "+COL_PUSH_SHOP_TYPE+" INTEGER DEFAULT 2;");
//            //db.execSQL("UPDATE push_shop set push_type = (select threads.v_address_type from threads where threads._id = push_shop.thread_id);");
//        }
//
//        createSpIdToPushType(db);
//    }
//
//    /**
//     * Author：liwanbing 11002396
//     * Date：2019.06.25 16:14
//     * Description: 数据库升版，增加push_mms表的字段，供其他app读取。
//     * @param db
//     */
//    private void upgradeDatabaseToVersion8571(SQLiteDatabase db) {
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_VERIFY_CODE)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_VERIFY_CODE + " TEXT");
//        }
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_READ_TYPE_ALIAS)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_READ_TYPE_ALIAS + " INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_SEND_STATE_ALIAS)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_SEND_STATE_ALIAS + " INTEGER DEFAULT -1");
//        }
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_RECEIVE_TYPE_ALIAS)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_RECEIVE_TYPE_ALIAS + " INTEGER DEFAULT 1");
//        }
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_ID_EX)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + COL_PUSH_MMS_ID_EX + " TEXT");
//        }
//
//        // 创建触发器
//        createPushMmsAliasTriggers(db);
//
//        // 历史数据处理
//        try {
//            // 同步read
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_READ_TYPE_ALIAS + " = " + COL_PUSH_MMS_READ_TYPE);
//
//            // 同步status
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_SEND_STATE_ALIAS
//                    + " = (CASE " + COL_PUSH_MMS_SEND_STATE + " WHEN 0 THEN 2 WHEN 1 THEN 0 WHEN 2 THEN 1 ELSE -1 END)");
//
//            // 同步type
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_RECEIVE_TYPE_ALIAS + " = " + COL_PUSH_MMS_RECEIVE_TYPE);
//
//            // 生成扩展主键
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + COL_PUSH_MMS_ID_EX + " = ('" + ExtendSmsContract.DB_TABLE_PREFIX_PUSH_MMS
//                    + "'||" + COL_PUSH_MMS_ID + ")");
//
//        } catch (Throwable e) {
//            LogUtil.getInstance().e(TAG, "[upgradeDatabaseToVersion8571] process old data error:" + e);
//        }
//    }
//
//    private void upgradeDatabaseToVersion8576(SQLiteDatabase db) {
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, ICommonData.COL_THREAD_KEY)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + ICommonData.COL_THREAD_KEY + " TEXT");
//        }
//        if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, ICommonData.MsgBussinessType.KEY)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_MMS + " ADD COLUMN " + ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.DEFAULT);
//        }
//        if (!isColumnExist(db, "threads", ICommonData.COL_THREAD_KEY)) {
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + ICommonData.COL_THREAD_KEY + " TEXT");
//        }
//        if (!isColumnExist(db, "threads", ICommonData.MsgBussinessType.KEY)) {
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.DEFAULT);
//        }
//
//        if (!isColumnExist(db, TABLE_NAME_PUSH_SHOP, ICommonData.MsgBussinessType.KEY)) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME_PUSH_SHOP + " ADD COLUMN " + ICommonData.MsgBussinessType.KEY + " INTEGER DEFAULT " + ICommonData.MsgBussinessType.DEFAULT);
//        }
//        createPushMmsTriggers(db);
//        // 历史数据处理
//        Cursor cursor = null;
//        try {
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + ICommonData.MsgBussinessType.KEY + " = (CASE " + COL_PUSH_MMS_SP_ID +" WHEN " + IPushData.SP_ID_TED_SERVICE +" THEN " +
//                    ICommonData.MsgBussinessType.SERVICE + " ELSE "+ ICommonData.MsgBussinessType.NOTICE  +" END)");
//            db.execSQL("UPDATE " + TABLE_NAME_PUSH_SHOP + " SET " + ICommonData.MsgBussinessType.KEY + " = (CASE " + COL_PUSH_SHOP_TYPE +" WHEN " + IPushData.AddressTypeValue.ADDRESS_TYPE_PUSH_SERVICE_TED +" THEN " +
//                    ICommonData.MsgBussinessType.SERVICE + " ELSE "+ ICommonData.MsgBussinessType.NOTICE  +" END)");
//
//            cursor = db.query(true, TABLE_NAME_PUSH_MMS, new String[]{COL_PUSH_MMS_THREAD_ID, COL_PUSH_MMS_SP_ID, COL_PUSH_MMS_SHOP_ID, COL_PUSH_MMS_NUMBER, ICommonData.MsgBussinessType.KEY}, null, null, null, null, null, null);
//            if (cursor != null && cursor.moveToFirst()){
//                do{
//                    long thread_id = cursor.getLong(0);
//                    int sp_id = cursor.getInt(1);
//                    String shop_id = cursor.getString(2);
//                    String number = cursor.getString(3);
//                    int bussine_type = cursor.getInt(4);
//
//                    String thread_key = PushSQLHelper.createThreadKey(sp_id, 0, shop_id, number, bussine_type);
//                    if (TextUtils.isEmpty(thread_key)){
//                       continue;
//                    }
//                    try{
//                        db.execSQL("UPDATE " + TABLE_NAME_PUSH_MMS + " SET " + ICommonData.COL_THREAD_KEY + "='" + thread_key + "' WHERE " + COL_PUSH_MMS_THREAD_ID + "=" + thread_id);
//                        db.execSQL("UPDATE threads SET " + ICommonData.COL_THREAD_KEY + "='" + thread_key + "'," + ICommonData.MsgBussinessType.KEY +"=" + bussine_type +" WHERE " + "_id=" + thread_id);
//                    }catch(Throwable e){
//                        LogUtil.getInstance().e(TAG, "[upgradeDatabaseToVersion8576] update error:" + e);
//                    }
//                }while (cursor.moveToNext());
//            }
//        } catch (Throwable e) {
//            LogUtil.getInstance().e(TAG, "[upgradeDatabaseToVersion8576] process old data error:" + e);
//        }finally {
//            if (cursor != null){
//                cursor.close();
//            }
//        }
//
//    }
//
//    /**
//     * added by lengxibo for android 8.0(no contain 7400) upgrade to 8400 2017.11.16
//     * @param db
//     */
//    private void upgradeDatabaseToVersion8400(SQLiteDatabase db) {
//        SharedPreferences preferences = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_XML_SETTING, Context.MODE_PRIVATE);
//        if (!preferences.contains(VERIFY_CODE_PROTECTED)) {
//            upgradeDatabaseToVersion7400(db);
//        }
//        VLog.d(TAG, " upgrade to V8400 finished");
//    }
//
//    private void updateCanonicalAddresses(SQLiteDatabase db) {
//        String query = "SELECT _id,address FROM canonical_addresses WHERE inverted_address is null";
//        Cursor c = null;
//        try {
//            c = db.rawQuery(query, null);
//            if (c != null) {
//                c.moveToPosition(-1);
//                while (c.moveToNext()) {
//                    int id = c.getInt(0);
//                    String number = c.getString(1);
//                    String invertedNumber = MmsPhoneNumberUtils.toCallerIDMinMatch(number);
//                    if (invertedNumber != null) {
//                        db.execSQL("UPDATE canonical_addresses SET inverted_address= '" + invertedNumber +
//                                "' WHERE _id = " + id);
//                    }
//                }
//            }
//       } finally {
//           if(c != null) {
//               c.close();
//           }
//       }
//    }
//
//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
//        if(newVersion >= oldVersion){
//            return;
//        }
//        switch (oldVersion) {//TODO 升级数据库时如果降级需要处理，请处理降级保证功能正常
//        }
//        if(FfpmAnalyticsDataUtil.IS_FFPM_ENABLE && !LocaleUtils.isInternationalVersion()){
//            try {
//                HashMap<String, String> map1 = new HashMap<>();
//                map1.put("sub_type", FfpmAnalyticsDataUtil.FFPM_ENVENTID_DOWNGRADE);
//                map1.put("reason", FfpmAnalyticsDataUtil.FFPM_ENVENTID_DOWNGRADE_REASON);
//                FfpmAnalyticsDataUtil.writeFFPMEvent(FfpmAnalyticsDataUtil.FFPM_EXCEPITON_LEVEL_ONE,FfpmAnalyticsDataUtil.FFPM_TROUBLE_ONE,
//                        map1, null, String.valueOf(oldVersion),String.valueOf(newVersion));
//            } catch (Exception e) {
//                LogUtil.getInstance().w(TAG, "write ffpm failed e" + e.getMessage());
//            }
//        }
//
//        SQLiteOpenHelperManager.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
//        LogUtil.getInstance().w(TAG, "Upgrading database from version " + oldVersion
//                + " to " + currentVersion + ".");
//        if(oldVersion >= currentVersion){
//            return;
//        }
//
//        switch (oldVersion){
//        case 1001:
//
//        	db.beginTransaction();
//            try {
//                upgradeDatabaseToVersion1002(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//        case 1002:
//
//            db.beginTransaction();
//            try {
//                upgradeDatabaseToVersion1003(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//        //added by haipeng for 信息列表背景色 2013.9.15
//		case 1003:
//
//            db.beginTransaction();
//            try {
//                upgradeDatabaseToVersion1004(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//        //added by huangjm for Rom数据库升级，加密信息强制解密 2013.10.16
//		case 1004:
//
//            db.beginTransaction();
//            try {
//            		upgradeDatabaseToVersion1005(db);
//            		db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//		case 1005:
//			db.beginTransaction();
//			try{
//				upgradeDatabaseToVersion1006(db);
//				db.setTransactionSuccessful();
//			}catch(Throwable ex){
//				 LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//	             break;
//			} finally {
//                db.endTransaction();
//            }
//		//added by leiyaotao for 信息：手机接收不到长短信。2013.12.20
//		case 1006:
//			db.beginTransaction();
//			try{
//				upgradeDatabaseToVersion1007(db);
//				db.setTransactionSuccessful();
//			}catch(Throwable ex){
//				 LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//	             break;
//			} finally {
//                db.endTransaction();
//            }
//		//added by huangjm for Rom1.5隐私信息数据库修改 2014.3.10
//		case 1007:
//			db.beginTransaction();
//			try{
//				upgradeDatabaseToVersion1008(db);
//				db.setTransactionSuccessful();
//			}catch (Throwable ex) {
//				 LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//	             break;
//			} finally {
//                db.endTransaction();
//			}
//		//added by fuleilei for save received sms of app 2014.10.24
//		case 1008:
//			db.beginTransaction();
//			try{
//				upgradeDatabaseToVersion1009(db);
//				db.setTransactionSuccessful();
//			}catch (Throwable ex) {
//				 LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//	             break;
//			} finally {
//                db.endTransaction();
//			}
//		//added by fuleilei copy from qcom 2014.11.11 添加字段以兼容第三方
//		case 1009:
//			db.beginTransaction();
//			try{
//				upgradeDatabaseToVersion1010(db);
//				db.setTransactionSuccessful();
//			}catch (Throwable ex) {
//				 LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//	             break;
//			} finally {
//                db.endTransaction();
//			}
//
//			//added by lengxibo for Android 5.0 2015.01.10
//		case 1010:
//		case 1011:
//		case 1012:
//		case 1013:
//			db.beginTransaction();
//			try {
//				upgradeDatabaseToVersion2011(db);
//				db.setTransactionSuccessful();
//			} catch (Throwable ex) {
//				LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//				break;
//			} finally {
//				db.endTransaction();
//			}
//		case 2011:
//            db.beginTransaction();
//            try {
//                upgradeDatabaseToVersion2031(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//
//        case 2031:
//            db.beginTransaction();
//            try{
//                upgradeDatabaseToVersion2035(db);
//                db.setTransactionSuccessful();
//            }catch (Throwable ex){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//
//        case 2035:
//            db.beginTransaction();
//            try{
//                upgradeDatabaseToVersion2036(db);
//                db.setTransactionSuccessful();
//            }catch (Throwable ex){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//        case 2036:
//            db.beginTransaction();
//            try{
//                upgradeDatabaseToVersion2037(db);
//                db.setTransactionSuccessful();
//            }catch (Throwable ex){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//        case 2037:
//        case 2038:
//        case 2039:
//        case 2040:
//            db.beginTransaction();
//            try{
//                upgradeDatabaseToVersion2047(db);
//                db.setTransactionSuccessful();
//            }catch (Throwable ex){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                break;
//            } finally {
//                db.endTransaction();
//            }
//
//            case 2047:
//            case 2048:
//                db.beginTransaction();
//                try {
//                    upgradeDatabaseToVersion2049(db);
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                    break;
//                } finally {
//                    db.endTransaction();
//                }
//            case 2049:
//            case 2050:
//            case 2051:
//            case 2052:
//            case 2053:
//            case 2054:
//            case 2055:
//            case 2056:
//            case 2057:
//            case 2058:
//                //vivo added update from ROM2.5 (ROM3.0 LITE)to ROM3.1,the version (2050 ~ 2068)maybe used
//            case 2069:
//                db.beginTransaction();
//                try {
//                    upgradeDatabaseToVersion2070(db);
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                    break;
//                } finally {
//                    db.endTransaction();
//                }
//            case 2070:
//            case 2071:
//            case 2072:
//            case 2073:
//            case 2074:
//            case 2075:
//                db.beginTransaction();
//                try {
//                    upgradeDatabaseToVersion2147(db);
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                    break;
//                } finally {
//                    db.endTransaction();
//                }
//
//            case 2147:
//                db.beginTransaction();
//                try {
//                    upgradeDatabaseToVersion2148(db);
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                    break;
//                } finally {
//                    db.endTransaction();
//                }
//            case 2148:
//            case 2149:
//            case 2150:
//            case 2151:
//                db.beginTransaction();
//                try {
//                    upgradeDatabaseToVersion2152(db);
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                    break;
//                } finally {
//                    db.endTransaction();
//                }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 7000)) {
//            upgradeDatabaseToVersion7000(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 7400)) {
//            upgradeDatabaseToVersion7400(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8400)) {
//            upgradeDatabaseToVersion8400(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8405)) {
//            upgradeDatabaseToVersion8405(db);
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8410)) {
//            db.beginTransaction();
//            try {
//                createNoticeMenuTables(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8430)) {
//            db.beginTransaction();
//            try {
//                createPushMmsTables(db);
//                createPushShopTables(db);
//                createPushSyncTables(db);
//                createPushMmsTriggers(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8500)) {
//            db.beginTransaction();
//            try {
//                createThreadIdDateIndex(db);
//                createPartMidIndex(db);
//                createAddrMsgIdIndex(db);
//                createPduPartIndex(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8510)) {
//            if (!isColumnExist(db, "sms", "bubble_type")) {
//                db.beginTransaction();
//                try {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN bubble_type INTEGER DEFAULT 1");
//                    db.setTransactionSuccessful();
//                } catch (Throwable ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                } finally {
//                    db.endTransaction();
//                }
//            }
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8511)) {
//            createIRoamingTables(db);
//        }
//
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8512)) {
//            createWifiPushListTables(db);
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8513)) {
//            createFrequencyTables(db);
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8530)) {
//            upgradeDatabaseToVersion8530(db);
//            createPushMmsTriggers(db);
//            createPushSpNumTables(db);
//            createPushFindPhoneNumTables(db);
//            createPushResultPhoneNumTables(db);
//
//            createBlockSmsTables(db);
//            createBlockSmsMessageTrigger(db);
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8531)) {
//            createPushSmsTempTables(db);
//        }
//        if (isUpgradeRequired(oldVersion, currentVersion, 8532)){
//            if (!isColumnExist(db, "sms", "black_type")) {
//                db.beginTransaction();
//                try {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN black_type INTEGER DEFAULT -1");
//                    db.setTransactionSuccessful();
//                } catch (Exception ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                } finally {
//                    db.endTransaction();
//                }
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8540)) {
//            createPushBindSimNumTables(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8550)) {
//            createSmartSmsEngineConfigTables(db);
//            createSmartSmsEngineBlackListTables(db);
//            addAddressFromToThreads(db);
//            addNoticeFromToNoticeTab(db);
//            addBubbleParseTimeTosmsTab(db);
//        }
//
//        // Add begin for RCS
//        if (isUpgradeRequired(oldVersion, currentVersion, 8561)) {
//            db.beginTransaction();
//            try {
//                RcsMessageProviderUtils.checkAndUpdateRcsSmsTable(db);
//                RcsMessageProviderUtils.checkAndUpdateRcsThreadTable(db);
//                createRcsNewTable(db);
//                createRcsNewTrigger(db);
//                addRcsSmsShowTimeColumn(db);
//                createTempGroupChatMemeberTable(db);
//                addPduSubjectTextColumn(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//        // Add end for RCS
//		if (isUpgradeRequired(oldVersion, currentVersion, 8570)){
//            if (!isColumnExist(db, "sms", "group_id")) {
//                try {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN group_id TEXT");
//                } catch (Exception ex) {
//                    LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//                }
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8571)) {
//            upgradeDatabaseToVersion8571(db);
//        }
//
//         if (isUpgradeRequired(oldVersion, currentVersion, 8575)) {
//            db.beginTransaction();
//            try {
//                createBackupSmsTables(db);
//                createBackupSmsTriggers(db);
//                createErrorCodeTables(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8576)) {
//            upgradeDatabaseToVersion8576(db);
//        }
//
//        if (isUpgradeRequired(oldVersion,currentVersion,8580)){
//            db.beginTransaction();
//            try {
//                if (!isColumnExist(db, "sms", "block_sms_type")) {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN block_sms_type INTEGER DEFAULT 0");
//                }
//                if (!isColumnExist(db, "pdu", "block_mms_type")) {
//                    db.execSQL("ALTER TABLE pdu ADD COLUMN block_mms_type INTEGER DEFAULT 0");
//                }
//                createBlockSmsKeywordTable(db);
//                db.setTransactionSuccessful();
//            } catch (Exception e){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(e));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion,currentVersion,8581)){
//            upgradeDatabaseToVersion8581(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8582)) {
//            addGroupIdToBackupSmsTable(db);
//            addRcsColumnToBackupSmsTable(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8583)) {
//            db.beginTransaction();
//            try {
//                createRcsFilePathTable(db);
//                createRcsFilePathTrigger(db);
//                db.setTransactionSuccessful();
//            } catch (Throwable ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8590)){
//            try {
//                if (!isColumnExist(db, TABLE_NAME_NOTICE_MENU, COL_NOTICE_MENU_AGENCY)) {
//                    db.execSQL("ALTER TABLE " + TABLE_NAME_NOTICE_MENU + " ADD COLUMN " +  COL_NOTICE_MENU_AGENCY + " TEXT");
//                }
//                if (!isColumnExist(db, SmsProvider.TABLE_SMS, "sms_extend_type")) {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN " + " sms_extend_type INTEGER DEFAULT 0");
//                }
//                if (!isColumnExist(db, SmsProvider.TABLE_SMS, "dynamic_bubble")) {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN " + " dynamic_bubble INTEGER DEFAULT -1");
//                }
//                if (!isColumnExist(db, SmsProvider.TABLE_SMS, "dynamic_update_date")) {
//                    db.execSQL("ALTER TABLE sms ADD COLUMN " + " dynamic_update_date INTEGER DEFAULT -1");
//                }
//                if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_DYNAMIC_BUBBLE)) {
//                    db.execSQL("ALTER TABLE push_mms ADD COLUMN " + COL_PUSH_MMS_DYNAMIC_BUBBLE + " INTEGER DEFAULT -1");
//                }
//                if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_DYNAMIC_BUBBLE_DATE)) {
//                    db.execSQL("ALTER TABLE push_mms ADD COLUMN " + COL_PUSH_MMS_DYNAMIC_BUBBLE_DATE + " INTEGER DEFAULT -1");
//                }
//            } catch (Exception ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            }
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8591)) {
//            upgradeDatabaseToVersion8591(db);
//        }
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8596)) {
//            db.beginTransaction();
//            try {
//                createBlockNoticeKeywordTable(db);
//                db.setTransactionSuccessful();
//            } catch (Exception e){
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(e));
//            } finally {
//                db.endTransaction();
//            }
//        }
//
//        //8600 update for C2C:C2C database upgrade
//        SQLiteOpenHelperManager.getInstance(mContext).onUpgrade(db, oldVersion, currentVersion);
//
//        if (isUpgradeRequired(oldVersion, currentVersion, 8620)){
//            try {
//                if (!isColumnExist(db, TABLE_NAME_PUSH_MMS, COL_PUSH_MMS_EXTEND_TYPE)) {
//                    db.execSQL("ALTER TABLE push_mms ADD COLUMN " + COL_PUSH_MMS_EXTEND_TYPE + " INTEGER DEFAULT -1");
//                }
//            } catch (Exception ex) {
//                LogUtil.getInstance().e(TAG, VLog.getStackTraceString(ex));
//            }
//        }
//
//    }
//
//    /** added by huangjm for Rom数据库升级，加密信息强制解密 2013.10.16
//     * @param db
//     * @param onlyDescrypt  only descrypt encrypted body,not update the is_encrypted  column
//     */
//    private void updateAllEncryptedDatabases(SQLiteDatabase db, boolean onlyDescrypt){
//        int row = 0;
//        LogUtil.getInstance().v(TAG, "----------updateAllEncryptedDatabases--------------");
//
//        String thread_where = " is_encrypted != 0";
//        LogUtil.getInstance().d(TAG,"update thread selection ="+thread_where);
//        if(!onlyDescrypt){
//        	ContentValues values = new ContentValues(1);
//        	values.put("is_encrypted", 0);
//        	row = db.update("threads", values, thread_where, null);
//        	LogUtil.getInstance().d(TAG,"update thread row------>" +row);
//        	if(row == 0){
//        		return ;
//        	}
//        }
//        String queryStr = "select _id, body from sms where is_encrypted != 0" ;
//
//        Cursor mCursor = db.rawQuery(queryStr, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//            String where = "";
//            int columnCount = onlyDescrypt ? 1 : 2;
//            ContentValues smsValues = new ContentValues(columnCount);
//            while (!mCursor.isAfterLast()) {
//                where = "_id = " + mCursor.getLong(0);
//                smsValues.put("body", MmsSmsProvider.getDesString(mCursor.getString(1)));
//                if(!onlyDescrypt){
//                	smsValues.put("is_encrypted", 0);
//                }
//                db.update("sms", smsValues, where, null);
//                mCursor.moveToNext();
//            }
//            mCursor.close();
//        }
//        LogUtil.getInstance().d(TAG, "Force updated all encrypted sms as decrypted!");
//        if(!onlyDescrypt){
//        	ContentValues mmsValues = new ContentValues(1);
//        	mmsValues.put("is_encrypted", 0);
//        	db.update("pdu", mmsValues, "is_encrypted != 0", null);
//        }
//
//        if(!onlyDescrypt) {
//            ContentValues values = new ContentValues(1);
//            values.put("is_encrypted", 0);
//            ImSQLHelper.updateImMessageEncrypted(db, values, null,true);
//        }
//        LogUtil.getInstance().d(TAG, "Force updated all encrypted mms as decrypted!");
//    }
//
//    //added by huangjm for Rom1.5隐私信息数据库修改 2014.3.10
//    private void reCreateSmsTableRelativeTrigger(SQLiteDatabase db){
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_on_insert");
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_date_subject_on_update ");
//        if(ProviderUtil.isMultiSimEnabled(mContext)){
//            db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_sim_id_on_insert");
//        }
//        db.execSQL("DROP TRIGGER IF EXISTS sms_update_thread_read_on_update ");
//        db.execSQL("DROP TRIGGER IF EXISTS update_threads_error_on_update_sms " );
//
//        // Updates threads table whenever a message is added to sms.
//        db.execSQL("CREATE TRIGGER sms_update_thread_on_insert AFTER INSERT ON sms " +
//                   SMS_UPDATE_THREAD_DATE_SNIPPET_COUNT_ON_UPDATE);
//
//        // Updates threads table whenever a message in sms is updated.
//        db.execSQL("CREATE TRIGGER sms_update_thread_date_subject_on_update AFTER" +
//                   "  UPDATE OF " + Sms.DATE + ", " + Sms.BODY + ", " + Sms.TYPE +", time "+
//                   "  ON sms " +
//                   SMS_UPDATE_THREAD_DATE_SNIPPET_COUNT_ON_UPDATE);
//
//        if(ProviderUtil.isMultiSimEnabled(mContext)){
//            db.execSQL("CREATE TRIGGER sms_update_thread_sim_id_on_insert AFTER" +
//                    "  INSERT " +
//                    "  ON sms " +
//                    "BEGIN " +
//                    UPDATE_THREAD_SIMID_ON_NEW +
//                    "END;");
//        }
//
//         // Updates threads table whenever a message in sms is updated.
//            db.execSQL("CREATE TRIGGER sms_update_thread_read_on_update AFTER" +
//                       "  UPDATE OF " + Sms.READ +
//                       "  ON sms " +
//                       "BEGIN " +
//                       SMS_UPDATE_THREAD_READ_BODY +
//                       UPDATE_THREAD_READ_COUNT+
//                       UPDATE_THREAD_UNREADCOUNT_ON_NEW+  //add by  lk
//                       "END;");
//
//         // Update the error flag of threads after a text message was
//            // failed to send/receive.
//            db.execSQL("CREATE TRIGGER update_threads_error_on_update_sms " +
//                       "  AFTER UPDATE OF type ON sms" +
//                       "  WHEN (OLD.type != 5 AND NEW.type = 5)" +
//                       "    OR (OLD.type = 5 AND NEW.type != 5) " +
//                       "BEGIN " +
//                       "  UPDATE threads SET error = " +
//                       "    CASE" +
//                       "      WHEN NEW.type = 5 THEN error + 1" +
//                       "      ELSE error - 1" +
//                       "    END " +
//                       "  WHERE _id = NEW.thread_id ; " +
//                       "END;");
//
//    }
//
//    /**
//     * @fuleilei
//     * @reason for upgrade database, judge the column exist or not 2014.11.28
//     */
//    private boolean isColumnExist(SQLiteDatabase db, String table, String column) {
//        if (db == null || TextUtils.isEmpty(table) || TextUtils.isEmpty(column)) {
//            return false;
//        }
//        boolean exist = false;
//        Cursor cursor = null;
//        try {
//            cursor = db.query(table, new String[]{column}, null, null, null, null, null);
//        } catch (Throwable e) {
//            cursor = null;
//        } finally {
//            if (cursor != null) {
//                exist = true;
//                cursor.close();
//            }
//        }
//
//        LogUtil.getInstance().i(TAG, "isColumnExist: exist = " + exist + ", cursor = " + cursor + ", table = " + table + ", column = " + column);
//        return exist;
//    }
//
//    /**
//     * 判断SUBJECT的编码是UTF-8还是ISO-8859-1,然后将UTF-8编码的SUBJECT转成ISO-8859-1
//     *
//     * @param subject
//     * @return
//     */
//    private String reCheckEncodeForSubject(String subject) {
//        try {
//            byte[] iso = subject.getBytes("ISO-8859-1");
//            byte[] utf = subject.getBytes("UTF-8");
//            String disIso = new String(iso);
//            String disUtf = new String(utf);
//            int isoLen = disIso.toCharArray().length;
//            int utfLen = disUtf.toCharArray().length;
//            if (isoLen == utfLen) {
//                subject = new String(utf, "ISO-8859-1");
//            } else {
//                subject = new String(iso, "ISO-8859-1");
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return subject;
//    }
//
//
//    final class RestoreHandler extends Handler {
//        static final int UPDATE_RESTORE_DATA_FINISHED = 1;
//        static final int RESTORE_USER_BEHAVIOR = 2;
//
//        //if backup db not finish in telephony provider, waiting for 5s
//        //if still not backup finish, ignore it.
//        static final int RESTORE_DATA_FINISHED_DELAY_TIME = 5 * 1000;
//
//        public RestoreHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case UPDATE_RESTORE_DATA_FINISHED:{
//                    SharedPreferences sp = mDeContext.getSharedPreferences(MmsSettingProvider.PREFERENCE_MIGRATING_DATABASE_SETTINGS, Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean(MmsSettingProvider.RESTORE_DB_SUCCESSED, true);
//                    editor.apply();
//                }
//                break;
//                case RESTORE_USER_BEHAVIOR:{
//                    restoreUserOldBehavior();
//                }
//                break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//
//
//    private void createSpIdToPushType(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SP_TYPE
//                + " ("
//                + COL_SP_PUSH_TYPE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_SP_SPID + " INTEGER"
//                + ");");
//        db.execSQL("insert or ignore into sp_type (push_type,sp_id) values(2,'1000');");
//        db.execSQL("insert or ignore into sp_type (push_type,sp_id) values(3,'1001');");
//    }
//
//    @VisibleForTesting
//    static long insertWithOneArgAndReturnId(SQLiteDatabase db, String sql, int sqlArgument) {
//        final SQLiteStatement insert = db.compileStatement(sql);
//        try {
//            bindString(insert, 1, sqlArgument);
//            try {
//                return insert.executeInsert();
//            } catch (SQLiteConstraintException conflict) {
//                return -1;
//            }
//        } finally {
//            insert.close();
//        }
//    }
//
//    private long insertSpType(SQLiteDatabase db, int spId) {
//        final String insert = "INSERT INTO " + TABLE_NAME_SP_TYPE + "("
//                + COL_SP_SPID +
//                ") VALUES (?)";
//        long id = insertWithOneArgAndReturnId(db, insert, spId);
//        if (id >= 0) {
//            ContentResolver cr = mContext.getContentResolver();
//            cr.notifyChange(IPushData.UrlValue.PUSH_SP_TYPE_URI, null);
//            return id;
//        }
//        return lookupPushType(db, spId);
//    }
//
//    @VisibleForTesting
//    static long queryIdWithOneArg(SQLiteDatabase db, String sql, int sqlArgument) {
//        final SQLiteStatement query = db.compileStatement(sql);
//        try {
//            bindString(query, 1, sqlArgument);
//            try {
//                return query.simpleQueryForLong();
//            } catch (SQLiteDoneException notFound) {
//                return -1;
//            }
//        } finally {
//            query.close();
//        }
//    }
//    private long lookupPushType(SQLiteDatabase db, int spId) {
//        Long id;
//        final String query = "SELECT " +
//                COL_SP_PUSH_TYPE + " FROM " + TABLE_NAME_SP_TYPE + " WHERE "
//                + COL_SP_SPID +
//                "=?";
//        id = queryIdWithOneArg(db, query, spId);
//        if (id < 0) {
//            VLog.e(TAG, "spId " + spId + " not found in the sp_type table");
//        }
//        return id;
//    }
//
//    private static void bindString(SQLiteStatement stmt, int index, int value) {
//        if (value == 0) {
//            stmt.bindNull(index);
//        } else {
//        stmt.bindLong(index, value);}
//    }
//
//    public long getPushType(int spId) {
//        SQLiteDatabase db = getWritableDatabase();
//        long id = lookupPushType(db, spId);
//        if (id < 0) {
//            return insertSpType(db, spId);
//        }
//        return id;
//    }
//
//    /**
//     * Add by yantiefang for block sms 2018.11.29
//     * @param db
//     */
//    private void createBlockSmsTables(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + BlockMessage.TABLE
//                + " ("
//                + BlockMessage.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + BlockMessage.COLUMN_ADDRESS + " TEXT,"
//                + BlockMessage.COLUMN_TIME + " INTEGER DEFAULT 0,"
//                + BlockMessage.COLUMN_DATE + " INTEGER DEFAULT 0,"
//                + BlockMessage.COLUMN_TYPE + " INTEGER DEFAULT -1,"
//                + BlockMessage.COLUMN_DURATION + " INTEGER DEFAULT 0,"
//                + BlockMessage.COLUMN_SOURCE + " INTEGER DEFAULT -1,"
//                + BlockMessage.COLUMN_MD5_MODEL + " TEXT,"
//                + BlockMessage.COLUMN_BODY_DESEN_VIVO + " TEXT,"
//                + BlockMessage.COLUMN_ADDRESS_DESEN_THIRD + " TEXT,"
//                + BlockMessage.COLUMN_BODY_DESEN_THIRD + " TEXT"
//                + ");");
//
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + BlockDistribution.TABLE
//                + " ("
//                + BlockDistribution.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + BlockDistribution.COLUMN_TYPE + " INTEGER DEFAULT -1,"
//                + BlockDistribution.COLUMN_MD5_MODEL + " TEXT"
//                + ");");
//    }
//
//    private void createBlockSmsMessageTrigger(SQLiteDatabase db) {
//        db.execSQL("DROP TRIGGER IF EXISTS delete_block_message_exceed_max_count");
//        db.execSQL("CREATE TRIGGER delete_block_message_exceed_max_count AFTER " +
//                "INSERT ON " + BlockMessage.TABLE +
//                " WHEN (SELECT COUNT(*) FROM " + BlockMessage.TABLE +
//                " )>" + BlockConstance.BLOCK_SMS_MESSAGE_DB_MAX_COUNT +
//                " BEGIN " +
//                "DELETE FROM " + BlockMessage.TABLE +
//                " WHERE " + BlockMessage.TABLE + "." + BlockMessage.COLUMN_ID + " IN " +
//                "(SELECT " + BlockMessage.TABLE + "." + BlockMessage.COLUMN_ID +
//                " FROM " + BlockMessage.TABLE +
//                " ORDER BY " + BlockMessage.TABLE + "." + BlockMessage.COLUMN_ID  + " ASC LIMIT " +
//                "(SELECT COUNT(*) -" + BlockConstance.BLOCK_SMS_MESSAGE_DB_MAX_COUNT +
//                " FROM " + BlockMessage.TABLE + ")); " +
//                "END;");
//    }
//
//    // Add begin for RCS
//    private void createRcsNewTable(SQLiteDatabase db) {
//        RcsMessageProviderUtils.checkAndUpgradeOneToManyMesageStatusTable(db);
//        RcsGroupChatInviteProvider.checkAndUpdateNoticeTable(db);
//        //groupchat table
//        createGroupChatTable(db);
//        createGroupChatMemeberTable(db);
//
//        createFavouriteTable(db);
//    }
//
//    private void createRcsNewTrigger(SQLiteDatabase db) {
//        RcsMessageProviderUtils.createRcsThreadUpdateTriggers(db);
//        RcsMessageProviderUtils.createSmsDeleteDuplicateRecordBeforeInsertTriggers(db);
//        RcsMessageProviderUtils.createRcsThreadUpdateOnDeleteTriggers(db);
//        RcsMessageProviderUtils.createRcsThreadUpdateMmsTriggers(db);
//        RcsMessageProviderUtils.createGroupStatusUpdateTriggers(db);
//        RcsMessageProviderUtils.createFavouriteMsgIdTriggers(db);
//        RcsMessageProviderUtils.createThreadErrorAfterSmsUpdate(db);
//        RcsMessageProviderUtils.createUpdateGroupStatusAfterSmsUpdate(db);
//        createSmsDeleteDuplicateRecordBeforeInsertTriggers(db);
//        createGroupChatTrigger(db);
//    }
//
//    // VIVO yantiefang add for RCS begin
//    private static void createSmsDeleteDuplicateRecordBeforeInsertTriggers(SQLiteDatabase db) {
//        db.execSQL("DROP TRIGGER IF EXISTS sms_delete_duplicate_record_before_insert");
//        db.execSQL("CREATE TRIGGER sms_delete_duplicate_record_before_insert BEFORE INSERT " +
//                "ON sms when new.type= 1 and new.rcs_message_id!= -1 and new.rcs_message_id IS NOT NULL  " +
//                "BEGIN  select raise(IGNORE)  where (select _id from sms " +
//                "where rcs_message_id = new.rcs_message_id and type= 1 and sub_id = new.sub_id) is not null; END;");
//    }
//    // VIVO yantiefang add for RCS end
//
//
//    /**
//     * Author：yantiefang 11055467
//     * Date：2019.06.13 10:39
//     * Description: 创建rcs_groupchat（群聊）表
//     *
//     * @param db
//     */
//    private void createGroupChatTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RcsGroupChatProvider.TABLE_NAME + " ("
//                + Constants.GroupChatProvider.GroupChat._ID
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + Constants.GroupChatProvider.GroupChat.THREAD_ID + " INTEGER,"
//                + Constants.GroupChatProvider.GroupChat.SUBJECT + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.CHAT_URI + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.STATUS + " INTEGER,"
//                + Constants.GroupChatProvider.GroupChat.CHAIRMAN + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.DIRECTION + " INTEGER,"
//                + Constants.GroupChatProvider.GroupChat.MAX_COUNT + " INTEGER,"
//                + Constants.GroupChatProvider.GroupChat.REMARK + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.POLICY + " INTEGER default 0,"
//                + Constants.GroupChatProvider.GroupChat.CONVERSATION_ID + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.CONTRIBUTION_ID + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.OWNER + " TEXT,"
//                + Constants.GroupChatProvider.GroupChat.DATE + " LONG" + ");");
//    }
//
//    /**
//     * Author：yantiefang 11055467
//     * Date：2019.06.13 10:40
//     * Description: 创建rcs_groupchat_member（群聊成员）表
//     *
//     * @param db
//     */
//    private void createGroupChatMemeberTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RcsGroupChatMemberProvider.TABLE_NAME + " ("
//                + Constants.GroupChatMemberProvider.GroupChatMember._ID
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.GROUP_ID + " INTEGER,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.NUMBER + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ALIAS + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ROLE + " INTEGER,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ETYPE + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ETAG + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.IMG_TYPE + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.HEAD_IMG + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.DATE + " LONG" + ");");
//
//        db.execSQL("CREATE INDEX IF NOT EXISTS idx_group_id on " + RcsGroupChatMemberProvider.TABLE_NAME + "("
//                + Constants.GroupChatMemberProvider.GroupChatMember.GROUP_ID + ")");
//    }
//
//    private void createGroupChatTrigger(SQLiteDatabase db) {
//        db.execSQL("DROP TRIGGER IF EXISTS member_delete_group_chat_on_delete");
//        StringBuilder buf = new StringBuilder();
//        buf.append("CREATE TRIGGER member_delete_group_chat_on_delete AFTER DELETE ON ")
//                .append(RcsGroupChatProvider.TABLE_NAME)
//                .append(" BEGIN DELETE from ")
//                .append(RcsGroupChatMemberProvider.TABLE_NAME)
//                .append(" where ")
//                .append(Constants.GroupChatMemberProvider.GroupChatMember.GROUP_ID)
//                .append(" = old." + Constants.GroupChatProvider.GroupChat._ID)
//                .append("; END;");
//        db.execSQL(buf.toString());
//    }
//
//    /**
//     * Author：yantiefang 11055467
//     * Date：2019.06.13 10:42
//     * Description: 创建rcs_fav_message（我的收藏）表
//     *
//     * @param db
//     */
//    private void createFavouriteTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RcsFavoriteMessageProvider.TABLE_NAME + " ("
//                + Constants.FavoriteMessageProvider.FavoriteMessage._ID
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.MSG_ID + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.THREAD_ID + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.NUMBER + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.CONTENT + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.BODY + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.DIRECTION + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.CHAT_TYPE + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.MSG_TYPE + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.STATUS + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.BURN + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.CONVERSATION_ID + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.CONTRIBUTION_ID + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.MESSAGE_ID + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.FILE_NAME + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.THUMBNAIL + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.MIME_TYPE + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.FILE_SIZE + " LONG,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.FILE_SELECTOR + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.TRANSFER_ID + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.FILE_ICON + " TEXT,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.TRANSFERED + " LONG,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.FILE_RECORD + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.BLACK + " INTEGER,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.BLOCK_TYPE
//                + " INTEGER default 2,"
//                + Constants.FavoriteMessageProvider.FavoriteMessage.DATE + " LONG" + ");");
//
//        db.execSQL("CREATE INDEX IF NOT EXISTS idx_fav_transfer_id on " +
//                RcsFavoriteMessageProvider.TABLE_NAME + "("
//                + Constants.FavoriteMessageProvider.FavoriteMessage.TRANSFER_ID + ")");
//    }
//
//    // VIVO yantiefang add for RCS begin
//    private void createTempGroupChatMemeberTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + RcsGroupChatMemberProvider.TEMP_TABLE_NAME + " ("
//                + Constants.GroupChatMemberProvider.GroupChatMember._ID
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + Constants.GroupChatProvider.GroupChat.CONVERSATION_ID + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.NUMBER + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ALIAS + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ROLE + " INTEGER,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ETYPE + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.ETAG + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.IMG_TYPE + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.HEAD_IMG + " TEXT,"
//                + Constants.GroupChatMemberProvider.GroupChatMember.DATE + " LONG" + ");");
//    }
//    // VIVO yantiefang add for RCS end
//
//    private void createBlockSmsKeywordTable(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BLOCK_SMS_TABLE_KEYWORD + "(" + COL_KEYWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                COL_KEYWORD_KEYWORD + " TEXT)");
//    }
//
//    private void createBlockNoticeKeywordTable(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BLOCK_NOTICE_KEYWORD + "(" + COL_BLOCK_NOTICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_SERVICE_CATEGORY + " INTEGER,"
//                + COL_BLOCK_NOTICE_TYPE + " INTEGER,"
//                + COL_BLOCK_NOTICE_KEYWORDS + " TEXT"
//                + ");");
//    }
//
//    private void addRcsSmsShowTimeColumn(SQLiteDatabase db) {
//        if (!isColumnExist(db, "sms", RcsData.RCS_SMS_COLUMN_SHOW_TIME)) {
//            db.execSQL("ALTER TABLE sms ADD COLUMN " + RcsData.RCS_SMS_COLUMN_SHOW_TIME +
//                    " INTEGER DEFAULT " + RcsData.SHOW_TIME_DEFAULT);
//        }
//    }
//    // Add end for RCS
//
//    private void addPduSubjectTextColumn(SQLiteDatabase db) {
//        if (!isColumnExist(db, "pdu", SUBJECT_TEXT)) {
//            db.execSQL("ALTER TABLE pdu ADD COLUMN " + SUBJECT_TEXT + " TEXT");
//        }
//    }
//
//    /**
//     * Author：zhouyinghui 11071508
//     * Date：2019.06.14 20:17
//     * Description:创建临时表，和sms表结构相同，存储最近接收的500条短信
//     *             用于恶意短信删除后查询时间确定是否恶意删除短信和展示给用户删除的短信
//     *
//     * @param db
//     */
//    private void createBackupSmsTables(SQLiteDatabase db) {
//        // N.B.: Whenever the columns here are changed, the columns in
//        // {@ref MmsSmsProvider} must be changed to match.
//        db.execSQL("CREATE TABLE IF NOT EXISTS backup_sms (" + "_id INTEGER PRIMARY KEY," + "delete_packagename TEXT," + "sms_id INTEGER," + "thread_id INTEGER," + "address TEXT," + "m_size INTEGER," + "person INTEGER," +
//                "date INTEGER," + "date_sent INTEGER DEFAULT 0," + "protocol INTEGER," + "read INTEGER DEFAULT 0," + "status INTEGER DEFAULT -1," + // a TP-Status value
//                // or -1 if it
//                // status hasn't
//                // been received
//                "type INTEGER," + "reply_path_present INTEGER," + "subject TEXT," + "body TEXT," + "service_center TEXT," +
//                "locked INTEGER DEFAULT 0," + "sub_id INTEGER DEFAULT -1," +
//                //sim_id change sub_id.modified by lengxibo for Android 5.0 2014.12.24
//                "error_code INTEGER DEFAULT 0," + "creator TEXT," +   //added by lengxibo for Android 5.0 2014.12.24
//                "seen INTEGER DEFAULT 0," + "is_encrypted INTEGER DEFAULT 0," +  //Add by lk  2011-03-08
//                "time INTEGER DEFAULT 0," + //添加一个加密字段
//                "dirty INTEGER DEFAULT 1," +
//                //added by lengxibo for mtk Android 5.0 2015.01.09
//                   /* todo open if need lengxibo 2015.01.10
//                    * "ipmsg_id INTEGER DEFAULT 0," +
//                   "ref_id INTEGER," +
//                   "total_len INTEGER," +
//                   "rec_len INTEGER" +*/
//                //added end lengxibo
//                PrivacyFilterParams.MESSAGE_MODE + " INTEGER DEFAULT 0" + //add by zouyongjun/BBK
//                ",priority INTEGER DEFAULT -1" +                 //pri change priority.modified by lengxibo for Android 5.0 2014.12.24
//                ",phone_id INTEGER DEFAULT -1" +  //added by maoyuanze for Android 5.1 2015.07.17
//                ",is_exec_trigger INTEGER DEFAULT 1" + ",verify_code INTEGER DEFAULT 0" +   //added by lengxibo for Rom3.1 2016.07.29
//                ",risk_website INTEGER DEFAULT 0" +   //added by lengxibo for Rom3.1 2016.07.29
//                ",bubble TEXT DEFAULT -1" + ",bubble_type INTEGER DEFAULT 1" + ",black_type INTEGER DEFAULT -1" +
//                ",bubble_parse_time INTEGER DEFAULT 0" +
//                // Add for RCS begin
//                "," + RcsData.RCS_SMS_COLUMN_SHOW_TIME + " INTEGER DEFAULT " + RcsData.SHOW_TIME_DEFAULT +
//                // Add for RCS end
//                ",group_id TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FAVOURITE + " INTEGER DEFAULT 0" +
//                "," + RcsColumns.SmsRcsColumns.RCS_MESSAGE_ID + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILENAME + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_MIME_TYPE + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_MSG_TYPE + " INTEGER DEFAULT -1" +
//                "," + RcsColumns.SmsRcsColumns.RCS_MSG_STATE + " INTEGER" +
//                "," + RcsColumns.SmsRcsColumns.RCS_CHAT_TYPE + " INTEGER DEFAULT -1" +
//                "," + RcsColumns.SmsRcsColumns.RCS_CONVERSATION_ID + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_CONTRIBUTION_ID + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_SELECTOR + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFERED + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFER_ID + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_ICON + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_BURN + " INTEGER  DEFAULT -1" +
//                "," + RcsColumns.SmsRcsColumns.RCS_HEADER + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_PATH + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_IS_DOWNLOAD + " INTEGER DEFAULT 0" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_SIZE + " INTEGER DEFAULT 0" +
//                "," + RcsColumns.SmsRcsColumns.RCS_THUMB_PATH + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_EXTEND_BODY + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_MEDIA_PLAYED + " INTEGER DEFAULT 0" +
//                "," + RcsColumns.SmsRcsColumns.RCS_EXT_CONTACT + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_FILE_RECORD + " INTEGER" +
//                "," + RcsColumns.SmsRcsColumns.RCS_TRANSFER_DATE + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_GROUP_AT_REMINDS + " TEXT" +
//                "," + RcsColumns.SmsRcsColumns.RCS_AUDIO_READ + " INTEGER DEFAULT 0" +
//                ",block_sms_type INTEGER DEFAULT 0"+
//                ",sms_extend_type INTEGER DEFAULT 0"+
//                ",dynamic_bubble TEXT DEFAULT -1"+
//                ",dynamic_update_date INTEGER DEFAULT 0"+
//                ");");
//    }
//    private static void createBackupSmsTriggers(SQLiteDatabase db) {
//        db.execSQL("DROP TRIGGER IF EXISTS delete_backup_sms_exceed");
//        db.execSQL("CREATE TRIGGER delete_backup_sms_exceed AFTER INSERT ON backup_sms " +
//                "BEGIN delete from backup_sms where (select count(_id) from backup_sms) > 500 " +
//                "and _id in (select _id from backup_sms order by date desc limit (select count(_id) from backup_sms) offset 500);END;");
//    }
//
//    /**
//     * Author：zhouyinghui 11071508
//     * Date：2019.06.14 20:27
//     * Description: 创建表用于存储框架接收短信上报的接收异常error code和pdu。
//     * @param db
//     */
//    private void createErrorCodeTables(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS errorcode_sms (" + "_id INTEGER PRIMARY KEY," + "error_code INTEGER," + "pdu TEXT," + "receive_time INTEGER DEFAULT 0," +"extra TEXT"+ ");");
//    }
//
//    private void upgradeDatabaseToVersion8581(SQLiteDatabase db) {
//        if (!isColumnExist(db, "threads", THREAD_CLOUMN_EXTEND_TYPE)) {
//            db.execSQL("ALTER TABLE threads ADD COLUMN " + THREAD_CLOUMN_EXTEND_TYPE + " INTEGER DEFAULT " + THREAD_EXTEND_TYPE_DEFAULT);
//        }
//    }
//
//
//    /**
//     * Author：shaozhujian 11097716
//     * Date：2019.07.20 16:10
//     * Description:创建temp_rcs_file_path表，用来临时保存删除信息的涉及文件的路径信息，方便后面删除无用资源文件
//     * @param db
//     */
//    private void createRcsFilePathTable(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_RCS_FILE_PATH + " ("
//                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + RcsColumns.SmsRcsColumns.RCS_FILENAME + " TEXT,"
//                + RcsColumns.SmsRcsColumns.RCS_THUMB_PATH + " TEXT"
//                + ");");
//    }
//
//    /**
//     * Author：shaozhujian 11097716
//     * Date：2019.07.20 16:14
//     * Description:创建触发器，当sms和rcs_fav_message表中有数据删除时，如果sms表的信息没有添加收藏以及rcs_fav_message
//     *             表的信息对应的信息已经被删除，则启动触发器，保存文件路径到temp_rcs_file_path表中
//     * @param db
//     */
//    private void createRcsFilePathTrigger(SQLiteDatabase db) {
//        db.execSQL("DROP TRIGGER IF EXISTS delete_rcs_sms_on_rcs_file_path_insert");
//        db.execSQL("CREATE TRIGGER delete_rcs_sms_on_rcs_file_path_insert BEFORE DELETE ON sms " +
//                "FOR EACH ROW WHEN old.favourite == 0 AND (old.rcs_file_name is not null OR old.rcs_thumb_path is not null) " +
//                "BEGIN INSERT INTO temp_rcs_file_path (rcs_file_name,rcs_thumb_path) VALUES (old.rcs_file_name,old.rcs_thumb_path);END;");
//
//        db.execSQL("DROP TRIGGER IF EXISTS delete_rcs_fav_message_on_rcs_file_path_insert");
//        db.execSQL("CREATE TRIGGER delete_rcs_fav_message_on_rcs_file_path_insert BEFORE DELETE ON rcs_fav_message " +
//                "FOR EACH ROW WHEN old.msg_id == 0 AND (old.filename is not null OR old.thumbnail is not null) " +
//                "BEGIN INSERT INTO temp_rcs_file_path (rcs_file_name,rcs_thumb_path) VALUES (old.filename,old.thumbnail);END;");
//    }
//
//    private void addGroupIdToBackupSmsTable(SQLiteDatabase db) {
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, "group_id")) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + "group_id TEXT");
//        }
//    }
//
//    /**
//     * Author：yantiefang 11055467
//     * Date：2019.07.29 19:33
//     * Description: 为临时backup_sms增加rcs字段
//     *
//     * @param db
//     */
//    private void addRcsColumnToBackupSmsTable(SQLiteDatabase db) {
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FAVOURITE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FAVOURITE + " INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_MESSAGE_ID)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_MESSAGE_ID + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILENAME)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILENAME + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_MIME_TYPE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_MIME_TYPE + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_MSG_TYPE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_MSG_TYPE + " INTEGER DEFAULT -1");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_MSG_STATE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_MSG_STATE + " INTEGER");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_CHAT_TYPE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_CHAT_TYPE + " INTEGER DEFAULT -1");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_CONVERSATION_ID)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_CONVERSATION_ID + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_CONTRIBUTION_ID)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_CONTRIBUTION_ID + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_SELECTOR)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_SELECTOR + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFERED)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFERED + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFER_ID)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_TRANSFER_ID + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_ICON)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_ICON + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_BURN)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_BURN + " INTEGER  DEFAULT -1");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_HEADER)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_HEADER + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_PATH)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_PATH + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_IS_DOWNLOAD)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_IS_DOWNLOAD + " INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_SIZE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_SIZE + " INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_THUMB_PATH)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_THUMB_PATH + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_EXTEND_BODY)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_EXTEND_BODY + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_MEDIA_PLAYED)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_MEDIA_PLAYED + " INTEGER DEFAULT 0");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_EXT_CONTACT)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_EXT_CONTACT + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_FILE_RECORD)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_FILE_RECORD + " INTEGER");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_TRANSFER_DATE)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_TRANSFER_DATE + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_GROUP_AT_REMINDS)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_GROUP_AT_REMINDS + " TEXT");
//        }
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, RcsColumns.SmsRcsColumns.RCS_AUDIO_READ)) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + RcsColumns.SmsRcsColumns.RCS_AUDIO_READ + " INTEGER DEFAULT 0");
//        }
//    }
//
//    /**
//     * Author：yantiefang 11055467
//     * Date：2019.09.02 17:23
//     * Description:为backup_sms表增加block_sms_type等字段
//     *
//     * @param db
//     */
//    private void upgradeDatabaseToVersion8591(SQLiteDatabase db) {
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, "block_sms_type")) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + "block_sms_type INTEGER DEFAULT 0");
//        }
//
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, "sms_extend_type")) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + "sms_extend_type INTEGER DEFAULT 0");
//        }
//
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, "dynamic_bubble")) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + "dynamic_bubble TEXT DEFAULT -1");
//        }
//
//        if (!isColumnExist(db, BackupSmsProvider.TABLE_NAME, "dynamic_update_date")) {
//            db.execSQL("ALTER TABLE "+ BackupSmsProvider.TABLE_NAME +" ADD COLUMN "
//                    + "dynamic_update_date INTEGER DEFAULT 0");
//        }
//    }
//}
