package com.wright.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardHolder> {

    private final List<Card> cards;
    private Context context;
    private int itemResource;
    CardHolder holder;
    protected ImageButton notifButton, deleteButton, viewButton, shareButton;
    public CardAdapterListener onClickListener;

    private CustomListViewAdaptor.OnItemClickListener mListener;

    public interface CardAdapterListener {
        void deleteButtonListener(View v, int position);
        void notifButtonListener(View v, int position);
        void viewButtonListener(View v, int position);
        void shareButtonListener(View v, int position);
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
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        // 5. Use position to access the correct Card object
        Card card = this.cards.get(position);

        this.holder = holder;
        // 6. Bind the card object to the holder
        holder.bindCard(card);
        final int cardPos = position;

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.deleteButtonListener(v, cardPos);
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.shareButtonListener(v, cardPos);
            }
        });

        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.viewButtonListener(v, cardPos);
            }
        });

        holder.notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.notifButtonListener(v, cardPos);
            }
        });


        Log.d("testy", "card = " + cards.get(position));

    }
}
