package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;

/**
 * Class that defines the Custom Card view for each card in the list view
 */
public class CustomCardView extends LinearLayout {

    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;

    /**
     * Constructor for the CustomCardView which defines the view.
     */
    public CustomCardView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view, this, true);
        cardAllergy = (TextView) findViewById(R.id.allergyTextView);
        cardLanguage = (TextView) findViewById(R.id.languageTextView);
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
        if (card.getAllergy().length() > 15 && !MainActivity.wideLayout) {
            cardLanguage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        //set the content for Text and Image view of the card
        cardAllergy.setText(card.getAllergy());
        cardLanguage.setText(card.getLanguage());
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));
        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));





    }

}

