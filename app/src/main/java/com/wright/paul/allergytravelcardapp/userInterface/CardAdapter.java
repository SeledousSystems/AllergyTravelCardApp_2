package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardHolder> {

    private final List<Card> cards;
    private Context context;
    private int itemResource;
    CardHolder holder;
    public CardAdapterListener onClickListener;

    public interface CardAdapterListener {
        void deleteButtonListener(View v, int position);

        void notifButtonListener(View v, int position);

        void viewButtonListener(View v, int position);

        void shareButtonListener(View v, int position);

        void cardWrapperListener(int position);

    }

    public CardAdapter(Context context, int itemResource, List<Card> cards, CardAdapterListener listener) {
        // 1. Initialize our adapter
        this.cards = cards;
        this.context = context;
        this.itemResource = itemResource;
        this.onClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return this.cards.size();
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new CardHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, final int position) {

        // 5. Use position to access the correct Card object
        final Card card = this.cards.get(position);


        this.holder = holder;
        // 6. Bind the card object to the holder
        holder.bindCard(card);
        holder.cardWrapper.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        holder.cardWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.cardWrapperListener(position);

            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.deleteButtonListener(v, position);
            }
        });

        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.viewButtonListener(v, position);
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.shareButtonListener(v, position);
            }
        });


        holder.notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.notifButtonListener(v, position);
            }
        });

        holder.flagImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String message = CardManager.getAllCountriesText(context, card.getLanguage());
                    AlertDialog.Builder bld = new AlertDialog.Builder(context);
                    bld.setIcon(R.mipmap.ic_logo);
                    bld.setTitle(card.getLanguage() + " Language");
                    bld.setMessage(message);
                    bld.setNeutralButton("CLOSE", null);
                    Log.d("FLAG_DIALOG", "Showing showAlert dialog: " + message);
                    bld.create();
                    AlertDialog dialog = bld.show();
                    //center the text in the showAlert
                    TextView messageView = dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);
                } catch (Exception e) {

                }
            }
        });

        holder.allergyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String message = CardManager.getAllergyText(context, card.getAllergy());
                    Log.d("FLAG_DIALOG", "Showing showAlert dialog: " + message);
                    AlertDialog.Builder bld = new AlertDialog.Builder(context);
                    bld.setIcon(R.mipmap.ic_logo);
                    bld.setTitle(card.getAllergy());
                    bld.setMessage(message);
                    bld.setNeutralButton("CLOSE", null);
                    Log.d("FLAG_DIALOG", "Showing showAlert dialog: " + message);
                    bld.create();
                    AlertDialog dialog = bld.show();
                    //center the text in the showAlert
                    TextView messageView = dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);
                } catch (Exception e) {
                }
            }
        });
    }
}
