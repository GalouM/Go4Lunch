package com.galou.go4lunch.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.galou.go4lunch.util.TextUtil.isEmailCorrect;
import static com.galou.go4lunch.util.TextUtil.isTextLongEnough;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by galou on 2019-05-01
 */
@RunWith(JUnit4.class)
public class TextUtilUnitTest {

    @Test
    public void correctEmail_returnTrue() throws Exception{
        String email = "email@email.com";
        assertTrue(isEmailCorrect(email));
    }

    @Test
    public void missingAtEmail_returnFalse() throws Exception{
        String email = "emailemail.com";
        assertFalse(isEmailCorrect(email));
    }

    @Test
    public void missingDomainExt_returnFalse() throws Exception{
        String email = "email@email";
        assertFalse(isEmailCorrect(email));
    }

    @Test
    public void textLongEnough_returnTrue() throws Exception{
        String text = "text";
        assertTrue(isTextLongEnough(text, 3));
    }

    @Test
    public void textTooShort_returnFalse() throws Exception{
        String text = "te";
        assertFalse(isTextLongEnough(text, 3));
    }
}
