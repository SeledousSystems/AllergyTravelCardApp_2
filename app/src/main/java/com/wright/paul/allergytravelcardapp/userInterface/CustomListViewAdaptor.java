package com.wright.paul.allergytravelcardapp.userInterface;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardManager;

import java.util.List;


/**
 *
 */
public class CustomListViewAdaptor extends ArrayAdapter<Card> {

    protected ImageButton notifButton, deleteButton, viewButton, shareButton;
    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;
    protected List<Card> cards;
    protected LinearLayout linearLayout;
    protected RelativeLayout relativeLayout;
    protected Card card;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public CustomListViewAdaptor(Context context, List<Card> cards, OnItemClickListener onItemClickListener) {
        super(context, R.layout.custom_list_view, cards);
        this.cards = cards;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        CustomCardView view = new CustomCardView(getContext());
        view.setCard(getItem(position));
        this.card = cards.get(position);

//        relativeLayout = view.findViewById(R.id.image_wrapper);
//        relativeLayout.bringToFront();
//        ViewCompat.setZ(relativeLayout, 5);
        return view;
    }
}

