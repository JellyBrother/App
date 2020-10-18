package com.jelly.app;

import com.jelly.app.main.basics.Reflect;
import com.jelly.app.main.designpatterns.ITest;
import com.jelly.app.main.head.HeadConstant;
import com.jelly.app.main.head.utils.SaveUtil;
import com.jelly.app.main.security.EncryptionUtils;
import com.jelly.baselibrary.utils.LogUtil;

import org.json.JSONObject;
import org.junit.Test;

public class BasicsTest {
    @Test
    public void testReflect() throws Exception {
        ITest test = new Reflect();
        test.test();
    }

    @Test
    public void testEncryptionUtils() throws Exception {
        String text = "好好学习，天天向上";
        String s = EncryptionUtils.encryptS2S(text);
//            SaveUtil.saveCookie(mActivity, s);
//            String s1 = SaveUtil.getCookie(mActivity);
        String s2 = EncryptionUtils.decryptS2S(s);
        System.out.println(s);
        System.out.println(s2);
    }

    @Test
    public void setResponseHead() throws Exception {
        String result1 = "{\"startTime\":\"2018-08-16T07:00:00\",\"endTime\":\"2018-08-16T09:00:00\"}";
        // json解析
        try {
            JSONObject jsonObject = new JSONObject(result1);
            String startTime = jsonObject.optString("startTime");
            String endTime = jsonObject.optString("endTime");
            System.out.println(startTime);
            System.out.println(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}