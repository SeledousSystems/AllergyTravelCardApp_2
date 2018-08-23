package com.wright.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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


    public CardHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        this.cardAllergy = (TextView) itemView.findViewById(R.id.allergyTextView);
        this.cardLanguage = (TextView) itemView.findViewById(R.id.languageTextView);
        this.flagImage = (ImageView) itemView.findViewById(R.id.flagImageView);
        this.allergyImage = (ImageView) itemView.findViewById(R.id.allergyImageView);

        notifButton = itemView.findViewById(R.id.button_notif);
        shareButton = itemView.findViewById(R.id.button_share);
        viewButton = itemView.findViewById(R.id.button_view);
        deleteButton = itemView.findViewById(R.id.button_delete);
    }

    @Override
    public void onClick(View view) {

    }

    public void bindCard(Card card) {

        this.card = card;

        //set the content for Text and Image view of the card
        cardAllergy.setText(card.getAllergy());
        cardLanguage.setText(card.getLanguage());
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));
        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));

        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "notify 1888888", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "notify 2", Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "notify 3", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "notify 4", Toast.LENGTH_SHORT).show();

            }
        });


    }
}

