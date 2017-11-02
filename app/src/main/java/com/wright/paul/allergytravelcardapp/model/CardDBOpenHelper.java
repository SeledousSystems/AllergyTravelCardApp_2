package com.wright.paul.allergytravelcardapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * This class provides a facade to the inbuilt SQLite DB. It provides methods to add, delete and get the cards from the
 * Database. It also provides a method for updating versions of the DB.
 */
public class CardDBOpenHelper extends SQLiteOpenHelper {
    protected static final int version = 1;
    protected static final String databaseName = "cards.db";
    protected String CREATE_SQL = "create table Card (_id INTEGER PRIMARY KEY, language TEXT, allergy TEXT, date INTEGER)";

    /**
     * Constructor for helper class.
     *
     * @param context
     */
    public CardDBOpenHelper(Context context) {
        super(context, databaseName, null, version);
    }

    /**
     * Override onCreate method of super class
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    /**
     * method for upgrading the database through new versions of the application
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * method utilised to get all cards stored in the DB. Used to populate the app with data
     * the user has created previously.
     *
     * @return an ArrayList of all the Cards stored in the Database
     */
    public ArrayList<Card> getAllCards() {
        String sql = "select * from Card";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{});
        int dbIndex = cursor.getColumnIndex("_id");
        int languageIndex = cursor.getColumnIndex("language");
        int allergyIndex = cursor.getColumnIndex("allergy");
        int dateIndex = cursor.getColumnIndex("date");

        ArrayList<Card> tempArray = new ArrayList<Card>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            Card newCard = new Card(cursor.getString(languageIndex), cursor.getString(allergyIndex), cursor.getInt(dateIndex));
            newCard.setDbID(cursor.getInt(dbIndex));
            tempArray.add(newCard);
        }
        return tempArray;
    }

    /**
     * Method to add a card to the DB.
     *
     * @param db
     * @param card
     * @return long of the db ID to use as a unique identifier
     */
    public long addCard(SQLiteDatabase db, Card card) {
        ContentValues values = new ContentValues();
        //delete the card if it exists already
        deleteCardLA(card);
        values.put("language", card.getLanguage());
        values.put("allergy", card.getAllergy());
        values.put("date", card.getLastViewed());

        //returns long ID of the not in the database. Allocate this to the card to track cards between the DB
        // and the application/list.
        return db.insert("Card", "null", values);
    }

    /**
     * method to remove a card from the database based on its given dbID attribute
     *
     * @param id
     * @return
     */
    public int deleteCardID(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Card",
                "_id = ? ",
                new String[]{Integer.toString(id)});
    }

    /**
     * Method to delete a card based on its language and allergy attributes.
     * @param card
     * @return
     */
    public int deleteCardLA(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Card",
                "language = ? AND allergy = ?",
                new String[]{card.getLanguage(), card.getAllergy()});
    }

}