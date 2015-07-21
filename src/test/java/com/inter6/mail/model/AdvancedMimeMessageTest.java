package com.inter6.mail.model;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by inter6 on 2015. 7. 21..
 */
public class AdvancedMimeMessageTest {

	@Test
	public void testUpdateMessageID_case1() throws Exception {
		InputStream emlStream = null;
		try {
			emlStream = new FileInputStream(ResourceUtils.getFile("classpath:eml/test1_normal.eml"));
			AdvancedMimeMessage mime = new AdvancedMimeMessage(emlStream);
			String messageID = mime.getMessageID();
			Assert.assertTrue(StringUtils.isNotEmpty(messageID));

			mime.setSentDate(new Date());
			mime.saveChanges();
			Assert.assertTrue(messageID.equals(mime.getMessageID()));
		} finally {
			IOUtils.closeQuietly(emlStream);
		}
	}

	@Test
	public void testUpdateMessageID_case2() throws Exception {
		InputStream emlStream = null;
		try {
			emlStream = new FileInputStream(ResourceUtils.getFile("classpath:eml/test1_no_message_id.eml"));
			AdvancedMimeMessage mime = new AdvancedMimeMessage(emlStream);
			Assert.assertTrue(StringUtils.isEmpty(mime.getMessageID()));

			mime.setSentDate(new Date());
			mime.saveChanges();
			Assert.assertTrue(StringUtils.isNotEmpty(mime.getMessageID()));
		} finally {
			IOUtils.closeQuietly(emlStream);
		}
	}
}