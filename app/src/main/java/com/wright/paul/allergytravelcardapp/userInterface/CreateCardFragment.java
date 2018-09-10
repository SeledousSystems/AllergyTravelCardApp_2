package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardDBOpenHelper;
import com.wright.paul.allergytravelcardapp.model.CardManager;

import java.util.Collections;

/**
 * Class that defines the Create Card Fragment for use in the Main Activity and Create Card Activity.
 */
public class CreateCardFragment extends Fragment {

    public boolean isPremium = false;
    Toast toast;
    String[] allergyArray;
    String[] languageArray;
    Button addCardButton;
    Button viewCardButton;
    Spinner allergySpinner;
    Spinner languageSpinner;
    TextView languageTV;
    TextView allergyTV;
    ImageView allergyIV;
    SQLiteDatabase db;
    CardDBOpenHelper cardDBOpenHelper;
    private CreateCardListener listener = null;
    private String TAG = "createCardFragment";
    private String buyPro = "buy_pro";

    /**
     * onCreateView method called when the fragment is created. It initialises the view and defines the
     * onClickListener for the Create Card Button
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the view
        View view = inflater.inflate(R.layout.fragment_create_card_view, container, false);

        //check if free or premium version and set boolean
        isPremium = MainActivity.mIsPremium;

        //tell activity frag has menu
        setHasOptionsMenu(true);

        if (!isPremium) {
            setHasOptionsMenu(true);
        }

        //assign the views
        addCardButton = (Button) view.findViewById(R.id.addCardButton);
        viewCardButton = (Button) view.findViewById(R.id.viewCardButton);
        languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
        languageTV = (TextView) view.findViewById(R.id.languageTV);
        languageTV.setMovementMethod(new ScrollingMovementMethod());
        if (isPremium) {
            //getAllCountriesText("afrikaans");
            CardManager.getAllCountriesText(this.getContext(), "afrikaans");
        } else {
            setLanguageTextView("afrikaans");
        }

        allergySpinner = (Spinner) view.findViewById(R.id.allergySpinner);
        allergyIV = (ImageView) view.findViewById(R.id.allergyIV);
        allergyTV = (TextView) view.findViewById(R.id.allergyTV);

        cardDBOpenHelper = new CardDBOpenHelper(view.getContext());
        if (isPremium) {
            languageArray = getResources().getStringArray(R.array.language_array_premium);
        } else {
            languageArray = getResources().getStringArray(R.array.language_array_free);
        }
        languageSpinner.setAdapter(new CustomSpinnerViewAdapter(getActivity(), R.layout.spinner_view, languageArray));
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (languageSpinner.getSelectedItem().toString().contains("Upgrade")) {
                    languageTV.setText("Upgrade to the PRO edition to add the following languages: " + CardManager.getProCountries(getContext()));
                } else {
                    setLanguageTextView(languageSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nil function
            }
        });

        allergyArray = getResources().getStringArray(R.array.allergy_array);
        allergySpinner.setAdapter(new CustomSpinnerViewAdapter(getActivity(), R.layout.spinner_view, allergyArray));
        allergySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setAllergyimageView(allergySpinner.getSelectedItem().toString());
                setAllergyTextView(allergySpinner.getSelectedItem().toString().toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //nil function
            }
        });

        viewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageSpinner.getSelectedItem().toString().contains("Upgrade")) {
                    alert("Upgrade to the PRO edition to add the following languages: " + CardManager.getProCountries(getContext()));
                } else {
                    createNewCard(languageSpinner.getSelectedItem().toString(), allergySpinner.getSelectedItem().toString(), true);
                }
            }
        });

        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageSpinner.getSelectedItem().toString().contains("Upgrade")) {
                    alert("Upgrade to the PRO edition to add the following languages: " + CardManager.getProCountries(getContext()));
                } else {
                    createNewCard(languageSpinner.getSelectedItem().toString(), allergySpinner.getSelectedItem().toString(), false);
                }
            }
        });

        //set the initial image & text views
        setAllergyimageView("celery");
        setAllergyTextView("celery");

        /**
         * call test method do not leave on in production
         */
        //createTestCards();

        return view;
    }

    //test method = treenuts is the longest text for most languages.
    private void createTestCards() {
        for (int i = 0; i < languageArray.length; i++) {
            if (!languageArray[i].contains("Upgrade"))
                createNewCard(languageArray[i], "Soya", false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.premium_Icon:
                Intent mainActivityIntent = new Intent(getContext(), MainActivity.class);
                mainActivityIntent.putExtra(buyPro, true);
                startActivity(mainActivityIntent);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof CreateCardListener)) {
            throw new IllegalStateException(
                    "Container activity must implement " +
                            "the FragmentListener interface.");
        }
        listener = (CreateCardListener) context;
    }

    public void createNewCard(String language, String allergy, boolean viewCard) {
        Card newCard = new Card(language, allergy, CardManager.getCurrentDateInt());
        db = cardDBOpenHelper.getReadableDatabase();

        //check card doesn't already exist, if it does, delete from DB so the new card's date sorts it to the top
        //if the card already exists the CardDBOpenHelper.addCard method call delete card and removes any instances of the same card
        // and ensures two cards with the same language and allergy are not held in the list at the same time.
        cardDBOpenHelper.addCard(db, newCard);

        //As the SQLite DB has been modified, update the array to match the DB. Sort the collection.
        CardListFragment.getCardList().clear();
        CardListFragment.getCardList().addAll(cardDBOpenHelper.getAllCards());
        Collections.sort(CardListFragment.getCardList());
        //Fire an intent to the Card Activity to display the newly created card
        if (viewCard) {
            Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
            newCardIntent.putExtra(CardManager.ls, languageSpinner.getSelectedItem().toString());
            newCardIntent.putExtra(CardManager.as, allergySpinner.getSelectedItem().toString());
            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            startActivity(newCardIntent);
            Toast.makeText(getActivity(), languageSpinner.getSelectedItem().toString() + " " + allergySpinner.getSelectedItem().toString() + " allergy card added", Toast.LENGTH_SHORT).show();

            //for adding a card to the list launch the main activity
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            startActivity(intent);
            toast = new Toast(getActivity());
            toast.makeText(getActivity(), languageSpinner.getSelectedItem().toString() + " " + allergySpinner.getSelectedItem().toString() + " allergy card added", Toast.LENGTH_SHORT).show();
        }
    }

    void setLanguageTextView(String language) {
        String[] countryArray = CardManager.getCountries(language, this.getContext());
        final String message = "Your " + language + " allergy travel card can be used in " + CardManager.buildCountryMessage(countryArray);
        languageTV.setText(message);
    }

    void setAllergyTextView(String allergy) {
        int allergyTextId = getResources().getIdentifier(allergy, "string", getContext().getPackageName());
        Log.d("Allergy Text ID", allergyTextId + "");
        allergyTV.setText(this.getContext().getString(allergyTextId));
    }

    void setAllergyimageView(String allergy) {
        allergyIV.setImageResource(CardManager.getResourceID(allergy + "_image"));
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setIcon(R.mipmap.ic_logo);
        bld.setTitle("Allergy Travel Cards");
        bld.setMessage(message);
        bld.setNeutralButton("CLOSE", null);
        Log.d(TAG, "Showing showAlert dialog: " + message);
        bld.create();
        AlertDialog dialog = bld.show();

        //center the text in the showAlert
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    //define the interface for the fragment to allow the activity to pass data to this fragment
    public interface CreateCardListener {
        void onCreateCardTouched();
    }

}






