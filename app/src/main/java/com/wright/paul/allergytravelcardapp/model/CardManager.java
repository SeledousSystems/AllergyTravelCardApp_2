package com.wright.paul.allergytravelcardapp.model;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.userInterface.CardListFragment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

/**
 * Class with static methods to provide business logic for Card objects.
 */
public class CardManager {

    //static Strings for naming data to b passed over intents
    public static String ls = "languageSelected";
    public static String as = "allergySelected";
    public static String cn = "cardNumber";
    private static String TAG = "CARDMANAGER";

    /**
     * Method to turn a String into required resource ID. utilised to turn a card parameter,
     * e.g. language or allergy, into a resource ID and get the associated image from the res
     * directory.
     *
     * @param string
     * @return
     */
    public static int getResourceID(String string) {
        // int to hold resource id to be returned
        int id = 0;

        if (string.contains("Upgrade")) return R.drawable.upgrade_button_blue;
        if (string != null) {
            //to lowercase
            string = string.toLowerCase();
            //remove whitespace
            string = string.replaceAll("\\s+", "");
            try {
                Class res = R.drawable.class;
                Field field = res.getField(string);
                id = field.getInt(null);
            } catch (Exception e) {
                //if resource is null the apps symbol is displayed as the default (fail gracefully)
                id = R.drawable.appl_icon;
                Log.e("MyTag", "Failure to get drawable id. For " + string, e);
            }
            return id;
        } else {
            //if resource is null the apps symbol is displayed as the default (fail gracefully)
            return R.drawable.appl_icon;
        }
    }

    /**
     * Method for getting the current data and converting to int. Utilised as the lastViewed
     * parameter in a Card object to sort objects.
     *
     * @return
     */
    public static int getCurrentDateInt() {
        int date = (int) new Date().getTime() / 1000;
        return date;
    }

    public static String getAllCountriesText(Context context, String language) {
        String[] countryArray = CardManager.getCountries(language, context);
        final String message = language + " can be used in " + CardManager.buildCountryMessage(countryArray);
        return message;
    }

    public static String getAllergyText(Context context, String allergy) {
        int allergyTextId = context.getResources().getIdentifier(allergy.toLowerCase(), "string", context.getPackageName());
        Log.d("Allergy Text ID", allergyTextId + "  " + context.getPackageName());
        String allergyText = context.getString(allergyTextId);
        return allergyText;
    }


    /**
     * Method to builds a string to output to the user the countries their card can be utilised in.
     *
     * @param countryArray
     * @return
     */
    public static String buildCountryMessage(String countryArray[]) {
        StringBuilder builder = new StringBuilder();
        if (countryArray.length == 1) builder.append(countryArray[0]);
        else {
            for (int i = 0; i < countryArray.length; i++) {
                if (i == (countryArray.length - 1)) {
                    builder.append("and ");
                }
                builder.append(countryArray[i]);
                if (i < (countryArray.length - 2)) {
                    builder.append(", ");
                }
                if (i < (countryArray.length - 1)) {
                    builder.append(" ");
                }
            }
        }
        builder.append(".");
        return builder.toString();
    }

    /**
     * Method that builds the message for the card body based on the language and allergy
     *
     * @param allergy
     * @param language
     * @param context
     * @return
     */
    public static String getCardBodyText(String allergy, String language, Context context) {
        String cardBody = "";
        try {
            int allergyId = context.getResources().getIdentifier("allergy_index", "array", context.getPackageName());
            int languageId = context.getResources().getIdentifier(language, "array", context.getPackageName());
            String[] allergyIndex = context.getResources().getStringArray(allergyId);
            String[] languageArray = context.getResources().getStringArray(languageId);
            cardBody = "Card not yet available";
            int index = -1;
            for (int i = 0; i < allergyIndex.length; i++) {
                if (allergyIndex[i].equals(allergy)) {
                    index = i;
                    break;
                }
            }
            if (index > -1 && index < languageArray.length) {
                cardBody = languageArray[index];
            }
        } catch (Exception e) {
            cardBody = "Card not yet available";
            Log.d(TAG, "Resource Exception" + e.getMessage());
        }
        return cardBody;
    }

    /**
     * Method that provides the requesting context with a string[] of countries that speak Language
     * parameter. Provides the user with a list of counties that a given card can be used based on language.
     *
     * @param language
     * @param context
     * @return
     */
    public static String[] getCountries(String language, Context context) {
        int id = 0;
        String lang = language.toLowerCase();
        id = context.getResources().getIdentifier(lang, "array", context.getPackageName());
        return context.getResources().getStringArray(id);
    }

    /**
     * Method to return the language based on the country parameter.  This is utlised to inform a
     * user when they arrive in a supported country they will be provided a notification that
     * they can make an allergy card in the local language.
     *
     * @param country
     * @param context
     * @return Language String
     */
    public static String getLanguage(String country, Context context) {
        String language = null;

        String[] languagesArray = context.getResources().getStringArray(R.array.language_array_free);
        if (languagesArray.equals(null)) return "empty";

        for (String l : languagesArray) {

            String[] countries = getCountries(l, context);
            for (String c : countries) {
                if (c.equals(country)) {
                    language = l;
                    break;
                }
            }
        }
        return language;
    }

    /**
     * Method to get the position of a card in the array given the card's language and allergy
     *
     * @param language
     * @param allergy
     * @return
     */
    public static int getCardPosition(String language, String allergy) {
        int position = -1;
        for (int i = 0; i < CardListFragment.getCardList().size(); i++) {
            Card card = CardListFragment.getCardList().get(i);
            if (card.getLanguage().equals(language) && card.getAllergy().equals(allergy)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public static String getProCountries(Context context) {
        String[] freeCountries = context.getResources().getStringArray(R.array.language_array_free);
        String[] proCountries = context.getResources().getStringArray(R.array.language_array_premium);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < proCountries.length; i++) {

            if (!Arrays.asList(freeCountries).contains(proCountries[i])) {
                sb.append(proCountries[i]);
                if (i < (proCountries.length - 2)) sb.append(", ");
                if (i == (proCountries.length - 2)) {
                    sb.append(" and ");
                }
            }
        }
        return sb.toString() + ". \n\n The PRO edition includes over 490 allergy cards for 170 countries.";
    }
}
