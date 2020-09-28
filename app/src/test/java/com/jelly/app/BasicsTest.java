package com.jelly.app;

import com.jelly.app.main.basics.Reflect;
import com.jelly.app.main.designpatterns.ITest;

import org.junit.Test;

public class BasicsTest {
    @Test
    public void testReflect() throws Exception {
        ITest test = new Reflect();
        test.test();
    }
}