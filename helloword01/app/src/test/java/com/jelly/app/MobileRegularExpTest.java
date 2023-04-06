package com.jelly.app;

import com.jelly.app.main.basics.Reflect;
import com.jelly.app.main.designpatterns.ITest;
import com.jelly.app.main.security.EncryptionUtils;

import org.json.JSONObject;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileRegularExpTest {
    @Test
    public void testMobileRegularExp() throws Exception {
        String regex = "^(\\+?84|0)?((1(2([0-9])|6([2-9])|88|99))|(9((?!5)[0-9])))([0-9]{7})$";
        String mobileNumber = "+84384259837";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobileNumber);
        boolean matches = m.matches();
        System.out.println("testMobileRegularExp:" + matches);
    }
}