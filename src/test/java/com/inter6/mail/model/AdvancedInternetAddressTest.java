package com.inter6.mail.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.internet.MimeUtility;

/**
 * Created by inter6 on 2015. 7. 21..
 */
@Slf4j
public class AdvancedInternetAddressTest {

    @Test
    public void testSetPersonal_case1() throws Exception {
        String addr = "inter6@inter6.com";
        AdvancedInternetAddress address = new AdvancedInternetAddress(addr, null, "UTF-8", "B");
        String result = address.toString();
        log.info(result);

        Assert.assertEquals(addr, result);
    }

    @Test
    public void testSetPersonal_case2() throws Exception {
        String addr = "inter6@inter6.com";
        String personal = "가나다";
        AdvancedInternetAddress address = new AdvancedInternetAddress(addr, personal, "UTF-8", "B");
        String result = address.toString();
        log.info(result);

        String encodedPersonal = MimeUtility.encodeWord(personal, "UTF-8", "B");
        String expectedAddressStr = encodedPersonal + " <" + addr + ">";
        Assert.assertEquals(expectedAddressStr, result);
    }
}