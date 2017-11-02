package com.example.xxxx.allergytravelcardapp;

import android.database.sqlite.SQLiteDatabase;

import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardDBOpenHelper;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Date;

public class CardTest extends TestCase {

    Card testCard = null;
    SQLiteDatabase db = null;
    CardDBOpenHelper cardDBOpenHelper = null;

    @Override
    protected void setUp() throws Exception {
        //create test card
        testCard = new Card("English", "Gluten", (int) new Date().getTime()/1000);
    }

    @Test
    public void testCardGetLanguage() throws Exception {
        assertEquals("English", testCard.getLanguage());
    }

    @Test
    public void testCardGetAllergy() throws Exception {
        assertEquals("Gluten", testCard.getAllergy());
    }

    @Test
    public void testGetDbID() throws Exception {
        assertEquals(-1, testCard.getDbID());
    }

    @Test
    public void testCardSetLastViewed() throws Exception {
        testCard.setLastViewed((int) new Date().getTime() / 1000);
        int date = (int) new Date().getTime()/1000;
        assertEquals(date, testCard.getLastViewed());
    }

    @Test
    public void testCardSetAllergy() throws Exception {
        testCard.setAllergy("Nuts");
        assertEquals("Nuts", testCard.getAllergy());
    }

    @Test
    public void testCardSetLanguage() throws Exception {
        testCard.setLanguage("German");
        assertEquals("German", testCard.getLanguage());
    }
}
