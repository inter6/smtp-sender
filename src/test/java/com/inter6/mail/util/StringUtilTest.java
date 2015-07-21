package com.inter6.mail.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by inter6 on 2015. 7. 21..
 */
public class StringUtilTest {

	@Test
	public void testSplitToSet_case1() throws Exception {
		String input = "";
		Set<String> result = StringUtil.splitToSet(input, ",");
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testSplitToSet_case2() throws Exception {
		String input = ",";
		Set<String> result = StringUtil.splitToSet(input, ",");
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testSplitToSet_case3() throws Exception {
		String input = "a,b,c";
		Set<String> result = StringUtil.splitToSet(input, ",");
		Assert.assertTrue(result.contains("a"));
		Assert.assertTrue(result.contains("b"));
		Assert.assertTrue(result.contains("c"));
	}
}