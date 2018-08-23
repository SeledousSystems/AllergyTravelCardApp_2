package com.wright.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;

/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardView_bup extends LinearLayout {

    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardView_bup(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view_bup, this, true);
        cardLanguage = (TextView) findViewById(R.id.languageTextView);
        cardAllergy = (TextView) findViewById(R.id.allergyTextView);
        flagImage = (ImageView) findViewById(R.id.flagImageView);
        allergyImage = (ImageView) findViewById(R.id.allergyImageView);
    }

    /**
     * setCard method to assign the elements of the card.
     *
     * @param card
     */
    public void setCard(Card card) {
        // check if the string is too long and reduce text size to fit screen if in portrait mode.
        if (card.getAllergy().length() > 9 && !MainActivity.wideLayout) {
            cardAllergy.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        //set the content for Text and Image view of the card
        cardAllergy.setText(card.getAllergy());
        cardLanguage.setText(card.getLanguage());
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));
        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));
    }
}

