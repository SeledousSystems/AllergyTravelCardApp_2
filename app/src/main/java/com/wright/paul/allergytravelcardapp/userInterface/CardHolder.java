package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;


/**
 *
 */
public class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected ImageButton notifButton, deleteButton, viewButton, shareButton;
    private Context context;
    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;
    private Card card;
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

        notifButton = itemView.findViewById(R.id.button_notif);
        //shareButton = itemView.findViewById(R.id.button_share);
        viewButton = itemView.findViewById(R.id.button_view);
        deleteButton = itemView.findViewById(R.id.button_delete);
    }

    @Override
    public void onClick(View view) {

    }

    public int getDominantColor(int drawable) {
        //TODO
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                drawable);
        Bitmap newBitmap = Bitmap.createScaledBitmap(icon, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public void bindCard(Card card) {

        this.card = card;
        final Card cardFinal = card;

        //set the content for Text and Image view of the card
        cardAllergy.setText(card.getAllergy() + " Allergy");
        cardLanguage.setText(card.getLanguage());
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));

        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));
        linearLayout.setBackgroundColor(getDominantColor(CardManager.getResourceID(card.getAllergy())));
    }
}

