package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.CardManager;
import com.wright.paul.allergytravelcardapp.util.IabBroadcastReceiver;
import com.wright.paul.allergytravelcardapp.util.IabHelper;
import com.wright.paul.allergytravelcardapp.util.IabResult;
import com.wright.paul.allergytravelcardapp.util.Inventory;
import com.wright.paul.allergytravelcardapp.util.Purchase;


/**
 * Class that defines the Main activity of the application for both Landscape and Portrait Orientations.
 * This class also starts the sticky location service.
 */
public class MainActivity extends AppCompatActivity implements CreateCardFragment.CreateCardListener, CardListFragment.CardListListener, IabBroadcastReceiver.IabBroadcastListener, NavigationView.OnNavigationItemSelectedListener {

    /**
     * In app purchases object and params
     */
    // SKUs for the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "premium_edition";
    // request code for the purchase flow of premium
    static final int RC_REQUEST = 10001;
    public static boolean wideLayout = false;
    // Does the user have the premium upgrade, altered via preferences
    public static boolean mIsPremium = false;
    protected Button createNewCardButton;
    protected Context context;
    protected String developerPayload = "";
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    // The iap helper object
    IabHelper mHelper;
    // Reference to the menu to update it when a user purchases premium
    Menu mMenu;
    boolean doubleBackToExitPressedOnce = false;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    /**
     * Class Attributes
     */
    private Spinner languageSpinner = null;
    private Spinner allergySpinner = null;
    private String TAG = "mainActivityTAG";
    private String buyPro = "buy_pro";
    // Listener that's called when we finish querying the items we own
    //preference attributes
    private String premiumPref = "APP_PREF";
    private String premiumPrefBool = "palladium"; // store the premium setting in shared preferences for offline use, call it palladium so its not totally obvious
    private SharedPreferences premiumSP;
    private int downloadresult = 0;

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }
            //output an error for failed purchase
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                //already owned error is returned set premium to true
                if (result.toString().contains("Already Owned")) {
                    mIsPremium = true;
                    setPref(mIsPremium);
                    alert("You own the premium edition");
                    updateMenuItems();
                }
                return;
            }
            //where the current users' email does not match the email used for the purchase
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }
            //purchase is successful. Set the premium boolean to true, store the variable into prefs
            Log.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                //showAlert("Thank you for upgrading to the premium edition. You now have all 12 languages available to you.");
                mIsPremium = true;
                updateMenuItems();
                mMenu.findItem(R.id.premium_Icon).setVisible(false);

                //save the premium setting to Preferences
                setPref(mIsPremium);
                Log.d(TAG, mIsPremium + " purchased finished");
            }

        }
    };
    // listener for receiving the response from the inventory query. The inventory is queried each time the activity is created, so this listener is called for each activity creation.
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Has the activity been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure, if so output and quit
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            //success
            Log.d(TAG, "Query inventory was successful.");

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            //set the premium boolean to true if the premiumPurchase is not null and the email payload of purchase and user match.
            if (premiumPurchase != null) {
                Log.d(TAG, premiumPurchase.toString());
                Log.d(TAG, "developer payload = " + premiumPurchase.getDeveloperPayload());
            }
            mIsPremium = (premiumPurchase != null);
            //save the premium setting to Preferences
            setPref(mIsPremium);
            updateMenuItems();
            Log.d(TAG, mIsPremium + " query inventory");
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    public void testMethod(String string) {

        //test stuff
    }

    // Handle navigation view item clicks here.
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_version:
                alert("Version: " + this.getString(R.string.version) + "\nDeveloper: PeaJay");
                break;

            case R.id.nav_about:
                if (!mIsPremium) {
                    alert("This app creates allergy cards in " + this.getString(R.string.total_languages_number) + " different languages to use when purchasing food. The free version offers all " + this.getString(R.string.allergies_number) + " allergies and " + this.getString(R.string.free_countries_number) + " languages. To unlock " + this.getString(R.string.additional_languages_countries) + " click 'GET PRO'. Your purchase supports the developer and allows the addition of further languages, allergies and features.");
                } else {
                    alert("This app creates " + this.getString(R.string.total_cards_number) + " different food allergy cards, in " + this.getString(R.string.total_languages_number) + " languages, for " + this.getString(R.string.total_languages_countries) + " countries. You own the PRO edition.");
                }
                return super.onOptionsItemSelected(item);

            case R.id.nav_rate:
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.wright.paul.allergytranslationcardapp")));
                break;

            case R.id.nav_get_pro:
                Log.d(TAG, mIsPremium + " upgrade button");
                AlertDialog.Builder bld = new AlertDialog.Builder(this);
                final AlertDialog alert = bld.create();
                bld.setIcon(R.mipmap.ic_logo);
                bld.setTitle("Allergy Travel Cards");
                //bld.setMessage("Upgrade to the PRO edition to add the following languages: " + CardManager.getProCountries(this));
                bld.setMessage("Upgrade to the PRO edition to add " + this.getString(R.string.pro_languages_number) + " languages for all allergies. The PRO edition has a total of " + getString(R.string.total_cards_number) + " unique cards and access to all future PRO edition languages, allergies and features.");
                bld.setPositiveButton("Upgrade to PRO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buy();
                    }
                });
                bld.setNegativeButton("Close", null);
                bld.create();
                AlertDialog dialog = bld.show();
                //center the text in the showAlert
                TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
                break;

            case R.id.nav_features:
                alertHTML(this.getString(R.string.features_body));

                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void updateMenuItems() {
        if (mMenu != null) {
            navigationView.setItemIconTintList(null);
            mMenu.findItem(R.id.premium_Icon).setVisible(!mIsPremium);
            navigationView.getMenu().findItem(R.id.nav_get_pro).setVisible(!mIsPremium);
            //mMenu.findItem(R.id.upgrade).setVisible(!mIsPremium);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration configuration = getResources().getConfiguration();
        context = this;

        // Restore premium preferences
        mIsPremium = getPref();
        Log.d(TAG, mIsPremium + " onCreate");

        downloadresult = getIntent().getIntExtra(CardManager.ds, 0);
        String language = getIntent().getStringExtra(CardManager.ls);
        String allergy = getIntent().getStringExtra(CardManager.as);
        if (downloadresult == 1) {
            Toast.makeText(this, "Download failed.....whoops, sorry.", Toast.LENGTH_SHORT).show();
        }
        if (downloadresult == 2) {
            Toast.makeText(this, language + " " + allergy + " Card Downloaded.", Toast.LENGTH_SHORT).show();

        }
        /**
         * Logic to manage the layout if dependant on device orientation
         * Landscape - utilises landscape layout and sets boolean WideLayout to true. Spinners are resized for landscape.
         */
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
            wideLayout = true;

            languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
            languageSpinner.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spinner_landscape_height);

            allergySpinner = (Spinner) findViewById(R.id.allergySpinner);
            allergySpinner.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spinner_landscape_height);

            /**
             * Portrait - utilises portrait layout and sets boolean WideLayout to true. Create new card button is initialised and click listener set which starts the create card activity.
             */
        } else {
            setContentView(R.layout.activity_main_portrait);
            wideLayout = false;
            createNewCardButton = (Button) findViewById(R.id.createNewCardButton);
            createNewCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIsPremium = getPref();
                    updateMenuItems();
                    Intent newCardIntent = new Intent(MainActivity.this, CreateCardActivity.class);
                    // invalidateOptionsMenu();
                    startActivity(newCardIntent, new Bundle());
                }
            });
        }

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsELOm9n3HjMhOA2pf9ic6mN1HD8CqDOgLd46j66a77WKlVgM22E+i5uQnuIbWVgqp5Gam6MVIztBkpUYMVgurItzXlnoufW0POFvYLVdZXiJXfKuT5GpP/8GyMYKlPbcVvoytFRQuR/Ox/nBdNzzSbBvFeNInpqZn6BNuJ+sjZ6ixj/KZCOycUvriRA5VLiw6KxpEMrqGSaICfgBO4pjia+00+J0G8PMZAyg/ObudTAaY8LItWxeOEH2VvGxn1FgH4XEEQGPL4MsXxm0pll0oSKykfdZhcFSae9KhYGVxn/rqExRWA0X97bz20fgxERXloFGgvPaURHO4YJxzvq8tQIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);

                } catch (IabHelper.IabAsyncInProgressException iAIPE) {
                    Log.d(TAG, iAIPE.getMessage());
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mIsPremium) {
            navigationView.getMenu().findItem(R.id.nav_get_pro).setVisible(false);
        }

        /**
         * Starts the 'sticky'/'start' location service. Note this service runs even after closing the app. Turned off for now
         */
        //Intent locationServiceIntent = new Intent(this, LocationService.class);
        //startService(locationServiceIntent);


        //Add the app icon to the action bar
        Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        myToolbar.setTitle("My Allergy Cards");
        setSupportActionBar(myToolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        boolean buyIntent = getIntent().getBooleanExtra(buyPro, false);
        if (buyIntent) {
            buy();
        }
    }

    /**
     * Fragment Listener Method for passing data between the Activity and the create card fragment
     */
    @Override
    public void onCreateCardTouched() {
        CardListFragment cardListFragment = (CardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_create_card);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment Listener Method for passing data between the Activity and the card list fragment
     */
    @Override
    public void onCardListTouched() {
        CardListFragment cardListFragment = (CardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_card_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateMenuItems();
        Log.d(TAG, mIsPremium + " onCreate Options menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        updateMenuItems();
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
        updateMenuItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        updateMenuItems();
//        if (id == R.id.about) {
//            if (!mIsPremium) {
//                showAlert("This app creates allergy cards in " + this.getString(R.string.total_languages_number) + " different languages to use when purchasing food. The free version offers all " + this.getString(R.string.allergies_number) + " allergies and " + this.getString(R.string.free_countries_number) + " languages. To unlock " + this.getString(R.string.additional_languages_countries) + ", click 'GET PRO'. Your purchase supports the developer and allows the addition of further languages, allergies and features.");
//            } else {
//                showAlert("This app creates " + this.getString(R.string.total_cards_number) + " different food allergy cards, in " + this.getString(R.string.total_languages_number) + " languages, for " + this.getString(R.string.total_languages_countries) + " countries. You own the PRO edition.");
//            }
//            return super.onOptionsItemSelected(item);
//        }
//
//        if (id == R.id.version) {
//            showAlert("Version: 1.2.6\n Developer: PeaJay");
//        }
//
//        if (id == R.id.rate) {
//            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.wright.paul.allergytranslationcardapp")));
//        }
//
//        if (id == R.id.features) {
//            showAlert("Create allergy cards: Use the \"Add New Card\" button, select the language and allergy and then press the \"Create Card\" button. \n\n" +
//                    "View a card: Tap a previously created card.\n\n" +
//                    "View different cards: Swipe a card to the left or right to view the next card. \n\n" +
//                    "Delete allergy cards: Swipe a card to the right.\n\n" +
//                    "Additional options: Tap and hold a card in the home screen to view additional options.");
//        }
//
//        if (id == R.id.upgrade) {
//            Log.d(TAG, mIsPremium + " upgrade button");
//            AlertDialog.Builder bld = new AlertDialog.Builder(this);
//            final AlertDialog showAlert = bld.create();
//            bld.setIcon(R.mipmap.ic_logo);
//            bld.setTitle("Allergy Travel Cards");
//            //bld.setMessage("Upgrade to the PRO edition to add the following languages: " + CardManager.getProCountries(this));
//            bld.setMessage("Upgrade to the PRO edition to add " + this.getString(R.string.pro_languages_number) + " languages for an additional " + this.getString(R.string.pro_countries_number) + " countries. The PRO edition has a total of 294 unique cards and access to all future PRO edition languages, allergies and features.");
//            bld.setPositiveButton("Upgrade to PRO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    buy();
//                }
//            });
//            bld.setNegativeButton("Close", null);
//            bld.create();
//            AlertDialog dialog = bld.show();
//
//            //center the text in the showAlert
//            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
//            messageView.setGravity(Gravity.CENTER);
//        }

        if (id == R.id.premium_Icon) {
            Log.d(TAG, mIsPremium + " PRO button");
            buy();
        }
        updateMenuItems();
        return super.onOptionsItemSelected(item);
    }

    public void buy() {
        if (mIsPremium) {
            alert("You own the PRO edition");
        } else {
            try {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
                String payload = developerPayload;
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(MainActivity.this, SKU_PREMIUM, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
                updateMenuItems();
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress. PRO icon");
            } catch (java.lang.IllegalStateException e) {
                complain("There is a problem with your google play account. Please check your login details.");
            }
        }
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** Allergy Card App Error: " + message);
        //uncomment showAlert to turn on in app debugging
        //showAlert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
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

    void alertHTML(String HTMLmessage) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setIcon(R.mipmap.ic_logo);
        bld.setTitle("Allergy Travel Cards");
        bld.setMessage((Html.fromHtml(HTMLmessage)));
        bld.setNeutralButton("CLOSE", null);
        bld.create();
        AlertDialog dialog = bld.show();

        //center the text in the showAlert
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    /**
     * Verifies the developer payload of a purchase by comparing the initial purchase user
     * email to the current user email for the playstore.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        if (payload.equals(developerPayload)) return true;
        else return false;
    }

    // get the users email for use in confirming legitimate purchase
//    String getUserEmail() {
//        String marketAssociatedEmailId = "";
//        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
//        if (accounts.length > 0) {
//            marketAssociatedEmailId = accounts[0].name;
//        }
//        Log.d(TAG, marketAssociatedEmailId);
//        return marketAssociatedEmailId;
//    }

    //on destroy clean up the iap objects
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mHelper != null) mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    //test method for premium buy
//    void testPremium() {
//        mIsPremium = true;
//        setPref(mIsPremium);
//        Log.d(TAG, mIsPremium + " testPremium");
//    }

    void testCards() {


    }

    boolean getPref() {
        premiumSP = getSharedPreferences(premiumPref, 0);
        Log.d(TAG, mIsPremium + " get pref");
        return premiumSP.getBoolean(premiumPrefBool, false);
    }

    void setPref(boolean premiumBool) {
        premiumSP = getSharedPreferences(premiumPref, 0);
        SharedPreferences.Editor editor = premiumSP.edit();
        editor.putBoolean(premiumPrefBool, premiumBool);
        editor.commit();
        Log.d(TAG, mIsPremium + " set pref");
    }
}
