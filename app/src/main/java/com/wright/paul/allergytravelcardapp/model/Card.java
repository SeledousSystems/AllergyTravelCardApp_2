package com.wright.paul.allergytravelcardapp.model;

import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Card class for creating card objects which have an allergy and language. DBID and lastViewed are
 * utilised to sort and track Card objects. Implements comparable so that cards can be sorted by
 * the lastViewed value. This allows more recently created/viewed cards to appear at the top of the
 * list view.
 */
public class Card implements Comparable<Card> {

    private String language;
    private String allergy;
    private int lastViewed;
    private int dbID = -1;
    public Intent notificationIntent;

    /**
     * Constructor requires an allergy, language and a lastviewed value. lastviewed is a java
     * calendar value cast to an int as it utilised as a means to sorting a collection of Card
     * objects.
     *
     * @param language
     * @param allergy
     * @param lastViewed
     */
    public Card(String language, String allergy, int lastViewed) {
        this.language = language;
        this.allergy = allergy;
        this.lastViewed = lastViewed;
    }

    /**
     * getter/setter methods for class fields.
     */
    public int getDbID() {
        return dbID;
    }

    public final void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String langauge) {
        this.language = langauge;
    }

    public int getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(int lastViewed) {
        this.lastViewed = lastViewed;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    /**
     * Override to String method to output object details.
     *
     * @return
     */
    @Override
    public String toString() {
        return "Card{" +
                "allergy='" + allergy + '\'' +
                ", language='" + language + '\'' +
                ", lastViewed=" + lastViewed +
                ", dbID=" + dbID +
                '}';
    }

    /**
     * Overriden compare to method for sorting collections of Card objects based on their
     * lastViewed value.
     *
     * @param anotherCard
     * @return
     */
    @Override
    @NonNull
    public int compareTo(Card anotherCard) {
        int compareLastView = anotherCard.getLastViewed();
        return compareLastView - this.getLastViewed();
    }
}
