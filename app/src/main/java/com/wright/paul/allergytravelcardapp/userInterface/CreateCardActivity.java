package com.wright.paul.allergytravelcardapp.userInterface;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.wright.paul.allergytravelcardapp.R;

/**
 * Class that defines the CreateCardActivity
 */

public class CreateCardActivity extends AppCompatActivity implements CreateCardFragment.CreateCardListener {

    /**
     * Class attributes
     */
    Configuration configuration;

    /**
     * onCreate method for the activity. If the orientation is switched to Landscape an intent is
     * fired to the main activity, which in landscape mode displays the fragment utilised in the
     * create card activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = getResources().getConfiguration();

        // the orientation is switched to Landscape an intent is fired to the main activity, which
        // in landscape mode displays the fragment utilised in the createcard activity
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent newCardIntent = new Intent(CreateCardActivity.this, MainActivity.class);
            startActivity(newCardIntent);
        } else {
            setContentView(R.layout.activity_create_card);
        }

        //Add the app icon to the action bar
        Toolbar myToolbar = findViewById(R.id.app_bar);
        myToolbar.setTitle("Create Allergy Card");
        setSupportActionBar(myToolbar);
    }

    //method utilised to pass data to the create card fragment
    public void onCreateCardTouched() {
        CreateCardFragment createCardFragment = (CreateCardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_create_card);
    }


}

