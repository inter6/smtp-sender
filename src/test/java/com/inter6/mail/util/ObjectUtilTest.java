package com.inter6.mail.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by inter6 on 2015. 7. 21..
 */
public class ObjectUtilTest {

    @Test
    public void testDefaultString_case1() {
        Object input = null;
        String result = ObjectUtil.defaultString(input, "null");
        Assert.assertEquals("null", result);
    }

    @Test
    public void testDefaultString_case2() {
        Object input = "input";
        String result = ObjectUtil.defaultString(input, "hmm");
        Assert.assertEquals("input", result);
    }

    @Test
    public void testDefaultBoolean_case1() {
        Object input = null;
        boolean result = ObjectUtil.defaultBoolean(input, true);
        Assert.assertTrue(result);
    }

    @Test
    public void testDefaultBoolean_case2() {
        Object input = new Boolean(true);
        boolean result = ObjectUtil.defaultBoolean(input, false);
        Assert.assertTrue(result);
    }
}