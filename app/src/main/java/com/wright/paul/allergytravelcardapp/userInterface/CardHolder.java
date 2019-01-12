package com.wright.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;


/**
 *
 */
public class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected Button notifButton, deleteButton, viewButton, shareButton;
    private Context context;
    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;
    protected Card card;
    protected LinearLayout linearLayout, cardWrapper;


    public CardHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        linearLayout = (LinearLayout) itemView.findViewById(R.id.button_wrapper);
        cardAllergy = (TextView) itemView.findViewById(R.id.allergyTextView);
        cardLanguage = (TextView) itemView.findViewById(R.id.languageTextView);
        flagImage = (ImageView) itemView.findViewById(R.id.flagImageView);
        allergyImage = (ImageView) itemView.findViewById(R.id.allergyImageView);
        cardWrapper = itemView.findViewById(R.id.card_wrapper);

        notifButton = itemView.findViewById(R.id.notif_button);
        //shareButton = itemView.findViewById(R.id.button_share);
        viewButton = itemView.findViewById(R.id.share_button);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }

    @Override
    public void onClick(View view) {

    }

    public void bindCard(Card card) {

        this.card = card;
        final Card cardFinal = card;

        //set the content for Text and Image view of the card
        cardAllergy.setText(card.getAllergy() + " Allergy");
        cardLanguage.setText(card.getLanguage());
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));

        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));

//        try {
//            Bitmap bitmap = ((BitmapDrawable) flagImage.getDrawable()).getBitmap();
//            int x = bitmap.getWidth() / 5;
//            int y = bitmap.getHeight() / 5;
//
//            int pixel = bitmap.getPixel(x, y);
//
//            linearLayout.setBackgroundColor(pixel);
//            linearLayout.getBackground().setAlpha(200);
//            Log.d("color", "int = " + pixel);
//        } catch (Exception e) {
//            Log.d("exception", e.getMessage());
//        }
    }
}

