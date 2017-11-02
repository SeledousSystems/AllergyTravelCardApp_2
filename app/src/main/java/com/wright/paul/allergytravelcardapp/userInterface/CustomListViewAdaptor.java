package com.wright.paul.allergytravelcardapp.userInterface;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;

import java.util.List;


/**
 *
 */
public class CustomListViewAdaptor extends ArrayAdapter<Card> {

    public CustomListViewAdaptor(Context context, List<Card> objects) {
        super(context, R.layout.custom_list_view, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomCardView view = new CustomCardView(getContext());
        view.setCard(getItem(position));
        return view;
    }
}

