package com.wright.paul.allergytravelcardapp.userInterface;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.CardManager;


/**
 * Class that defines the Spinner View utilised for the Country and Allergy spinners.
 */
public class CustomSpinnerViewAdapter extends ArrayAdapter<String> {

    /**
     * Class attributes
     */
    protected TextView spinnerTextView;
    protected ImageView spinnerImageView;
    protected String[] strings;
    private Context context;

    /**
     * Constructor which initialises the Adapter and assigns the attributes.
     *
     * @param context
     * @param txtViewResourceId
     * @param objects
     */
    public CustomSpinnerViewAdapter(Context context, int txtViewResourceId, String[] objects) {
        super(context, txtViewResourceId, objects);
        this.strings = objects;
        this.context = context;
    }

    /**
     * Method to get the drop down view for the adapter.
     *
     * @param position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        return getCustomView(position, view, viewGroup);
    }

    /**
     * Method to get drop down view for the adapter.
     *
     * @param position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return getCustomView(position, view, viewGroup);
    }

    /**
     * Method to the getCustomView and assign the layout elements as required to define the
     * spinner UI.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_view, parent, false);
        spinnerTextView = (TextView) view.findViewById(R.id.spinnerTextView);
        spinnerTextView.setText(strings[position]);
        spinnerImageView = (ImageView) view.findViewById(R.id.spinnerImageView);
        spinnerImageView.setImageResource(CardManager.getResourceID(strings[position]));
        return view;
    }
}