package com.example.myapp.base.widget.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class DimenUtils {
    private DimenUtils() {
    }

    /**
     * 钱数格式化
     *
     * @param data
     * @return
     */
    public static String moneyFormate(String data) {
        if (data != null) {
            NumberFormat format = new DecimalFormat("#,###");
            return format.format(Double.valueOf(data));
            //return format.format(new BigDecimal(data));
        } else {
            return "";
        }
    }

    /**
     * 钱数格式化
     *
     * @param data
     * @return
     */
    public static String moneyFormate(String data, String form) {
        if (data != null) {
            NumberFormat format = new DecimalFormat(form);
            return format.format(new BigDecimal(data));
        } else {
            return "";
        }
    }
}
