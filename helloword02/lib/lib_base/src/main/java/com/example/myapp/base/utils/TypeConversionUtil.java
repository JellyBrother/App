package com.example.myapp.base.utils;

import android.text.TextUtils;

public class TypeConversionUtil {
    private static final String TAG = "TypeConversionUtil";
    public static final String S_TRUE = "true";
    public static final String S_FALSE = "false";
    public static final String S_YES = "yes";
    public static final String S_NO = "no";
    public static final String S_Y = "y";
    public static final String S_N = "n";
    public static final String S_1 = "1";
    public static final String S_0 = "0";
    public static final int I_TRUE = 1;
    public static final int I_FALSE = 0;

    public static String getString(Object o) {
        return getString(o, "");
    }

    public static String getString(Object o, String s) {
        if (o == null) {
            return s;
        }
        if (o instanceof Boolean) {
            return (Boolean) o ? S_TRUE : S_FALSE;
        }
        return o.toString();
    }

    public static int getInt(Object o) {
        return getInt(o, 0);
    }

    public static int getInt(Object o, int i) {
        if (o == null) {
            return i;
        }
        if (o instanceof Boolean) {
            return (Boolean) o ? I_TRUE : I_FALSE;

        }
        if (o instanceof Integer || o instanceof Float || o instanceof Short) {
            return (int) o;
        }
        if (o instanceof Long) {
            long aLong = getLong(o, i);
            if (aLong > Integer.MAX_VALUE) {
                return i;
            }
            return (int) aLong;
        }
        if (o instanceof Double) {
            double aDouble = getDouble(o, i);
            if (aDouble > Integer.MAX_VALUE) {
                return i;
            }
            return (int) aDouble;
        }
        try {
            return Integer.parseInt(o.toString());
        } catch (Throwable t) {
//            LogUtils.e(TAG, "getInt Throwable:", t);
        }
        return i;
    }

    public static float getFloat(Object o) {
        return getFloat(o, 0);
    }

    public static float getFloat(Object o, float f) {
        if (o == null) {
            return f;
        }
        if (o instanceof Boolean) {
            return (Boolean) o ? I_TRUE : I_FALSE;
        }
        if (o instanceof Integer || o instanceof Float || o instanceof Short) {
            return (float) o;
        }
        if (o instanceof Long) {
            Long aLong = getLong(o, getLong(f));
            if (aLong > Integer.MAX_VALUE) {
                return f;
            }
            return aLong.floatValue();
        }
        if (o instanceof Double) {
            Double aDouble = getDouble(o, f);
            if (aDouble > Integer.MAX_VALUE) {
                return f;
            }
            return aDouble.floatValue();
        }
        try {
            return Float.parseFloat(o.toString());
        } catch (Throwable t) {
//            LogUtils.w(TAG, "getFloat Throwable:", t);
        }
        return f;
    }

    public static long getLong(Object o) {
        return getLong(o, 0);
    }

    public static long getLong(Object o, long l) {
        if (o == null) {
            return l;
        }
        if (o instanceof Boolean) {
            return (Boolean) o ? I_TRUE : I_FALSE;
        }
        try {
            return Long.parseLong(o.toString());
        } catch (Throwable t) {
//            LogUtils.e(TAG, "getLong Throwable:", t);
        }
        return l;
    }

    public static double getDouble(Object o) {
        return getDouble(o, 0);
    }

    public static double getDouble(Object o, double d) {
        if (o == null) {
            return d;
        }
        if (o instanceof Boolean) {
            return (Boolean) o ? I_TRUE : I_FALSE;
        }
        try {
            return Double.parseDouble(o.toString());
        } catch (Throwable t) {
//            LogUtils.e(TAG, "getDouble Throwable:", t);
        }
        return d;
    }

    public static boolean getBoolean(Object o) {
        return getBoolean(o, false);
    }

    public static boolean getBoolean(Object o, boolean b) {
        if (o == null) {
            return b;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        String s = o.toString().toLowerCase();
        if (TextUtils.equals(S_TRUE, s) || TextUtils.equals(S_YES, s) || TextUtils.equals(S_Y, s) || TextUtils.equals(S_1, s)) {
            return true;
        }
        if (TextUtils.equals(S_FALSE, s) || TextUtils.equals(S_NO, s) || TextUtils.equals(S_N, s) || TextUtils.equals(S_0, s)) {
            return false;
        }
        int anInt = getInt(o);
        if (anInt == I_TRUE) {
            return true;
        }
        return b;
    }
}
