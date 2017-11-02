//package com.example.xxxx.allergytravelcardapp;
//
//import android.test.AndroidTestCase;
//import android.test.suitebuilder.annotation.SmallTest;
//
//import com.wright.paul.allergytravelcardapp.R;
//import com.wright.paul.allergytravelcardapp.model.CardManager;
//
//import org.junit.Test;
//
//import java.util.Date;
//
//
//@SmallTest
//public class CardManagerTest extends AndroidTestCase {
//
//    @Test
//    public void testGetResourceIDisCorrect() throws Exception {
//        int id = R.drawable.celery;
//        assertEquals(id, CardManager.getResourceID("celery"));
//    }
//
//    @Test
//    public void testGetResourceIDNull() throws Exception {
//        String res = null;
//        int id = R.drawable.appl_icon;
//        assertEquals(id, CardManager.getResourceID(res));
//    }
//
//    @Test
//    public void testGetCurrentDateInt() throws Exception {
//        int time = (int) new Date().getTime() / 1000;
//        assertEquals(time, CardManager.getCurrentDateInt());
//    }
//
//    @Test
//    public void testBuildCountryMessage() throws Exception {
//        String[] countries = new String[]{"CountryA", "CountryB", "CountryC"};
//        assertEquals("CountryA,  CountryB and CountryC.", CardManager.buildCountryMessage(countries));
//    }
//
//
//
//    @Test
//    public void testGetCardPosition() throws Exception {
//        String language = "language";
//        String allergy = "allergy";
//        assertEquals(-1, CardManager.getCardPosition(language, allergy));
//    }
//
//}
