package com.wright.paul.allergytravelcardapp.userInterface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.CardManager;

/**
 * CardActivity that defines the Allergy Card.
 */
public class CardActivity extends AppCompatActivity {

    private static final int SWIPE_MIN_DISTANCE = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    protected TextView cardTitleTextView;
    //protected AutofitTextView cardBodyTextView;
    protected TextView cardBodyTextView;
    protected ImageView allergyImageView, iconImageView, flagImageView;
    private String language = null;
    private String allergy = null;
    private int cardPos = 0;
    private LinearLayout cardLL;
    private Context context;
    private String TAG = "CARD_ACTIVITY";
    private Button englishTranslation, allergyDescription, allergyPicture, lock;
    private boolean locked = false;
    private int redColour = Color.parseColor("#c2185b");
    private int lightBlueColour = Color.parseColor("#375273");
    private Toast toast;

    //method for counting space taken up by unicode
    static int countUniCodeChar(String s) {
        int count = 0;

        for (int i = 0; i < s.length(); i++) {

            if (Character.getType(i) == Character.NON_SPACING_MARK ||
                    Character.getType(i) == Character.ENCLOSING_MARK ||
                    Character.getType(i) == Character.COMBINING_SPACING_MARK)
                count++;

        }

        return count;
    }

    /**
     * onCreate method for the card activity. The card details are passed over utilising the intent extras.
     * if the intent comes from a card notification the card must be found so that wis a user flings to the
     * next card its position in the array is known.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up the view so it is ful screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
//        getSupportActionBar().hide();
        //set the context for future reference
        context = this;
        toast = new Toast(this);

        //hold the card number so on swiping to the next card an intent can be passed using the next card in the arrayList.
        //if the card number is not passed in, the default is -1.
        cardPos = getIntent().getIntExtra(CardManager.cn, -1);
        Log.d("cardPOs = -1", cardPos + "");
        //if a card is viewed from a notification, find the card to allow correct flinging between all cards
        if (cardPos == -1) {
            language = getIntent().getStringExtra(CardManager.ls);
            allergy = getIntent().getStringExtra(CardManager.as);
            // if viewing a card from a notification it will be placed as the first entry in the array, based on its recentlyView attribute
            cardPos = CardManager.getCardPosition(language, allergy);
        }
        // if the card does pass its position the language and allergy for the card are set.
        else {
            language = CardListFragment.getCardList().get(cardPos).getLanguage();
            allergy = CardListFragment.getCardList().get(cardPos).getAllergy();
        }

        Resources r = getResources();
        //views for the card are assigned
        iconImageView = findViewById(R.id.iconImageView);
        iconImageView.setImageResource(R.drawable.appl_icon);

        allergyImageView = findViewById(R.id.allergyImageView);
        allergyImageView.setImageResource(CardManager.getResourceID(allergy));

        //set a layered drawable to overlay the info icon on the flag
//        Drawable[] layers1 = new Drawable[2];
//        //get the height and width to place the info icon correctly
//        BitmapFactory.Options o1 = new BitmapFactory.Options();
//        o1.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
//                CardManager.getResourceID(language), o1);

//        layers1[0] = r.getDrawable(CardManager.getResourceID(allergy));
//        layers1[1] = r.getDrawable(R.drawable.ic_info_outline_black_small);
//        if (language.equals("English")) {
//            layers1[1].setAlpha(0);
//        } else {
//            layers1[1].setAlpha(100);
//        }

        allergyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked) {
                    showLockedToast();
                } else {
                    showAllergyText(allergy);
                }
            }
        });

        //set the layered flag image
        flagImageView = findViewById(R.id.flagImageView);
        flagImageView.setImageResource(CardManager.getResourceID(language));

//        //set a layered drawable to overlay the info icon on the flag
//        Drawable[] layers2 = new Drawable[2];
//        //get the height and width to place the info icon correctly
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
//        Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(),
//                CardManager.getResourceID(language), o2);
////        int bmpWidth2 = bmp.getWidth();
////        int bmpHeight2 = bmp.getHeight();
////        Log.d("TAGGGGGG", "width = " + bmpWidth + " bmpHeight " + bmpHeight);
//
//
//        layers2[0] = r.getDrawable(CardManager.getResourceID(language));
//        layers2[1] = r.getDrawable(R.drawable.ic_info_outline_black_small);
//        if (language.equals("English")) {
//            layers2[1].setAlpha(0);
//        } else {
//            layers2[1].setAlpha(100);
//        }
//        //layers[1].setBounds(60, 60, bmpWidth, bmpHeight);
//        LayerDrawable layerDrawable2 = new LayerDrawable(layers2);
//        // left top right bottom
//        layerDrawable2.setLayerInset(1, 450, 0, 0, 450);

        //LayerDrawable layerDrawable = new LayerDrawable(layers);
        //flagImageView.setImageDrawable(layerDrawable2);

        flagImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked) {
                    showLockedToast();
                } else {
                    if (allergy != null && !language.equals("English")) {
                        showCardInEnglish(allergy);
                    }
                }
            }
        });

        cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        cardBodyTextView = (TextView) findViewById(R.id.cardBodyTextView);

        // Set the text for the card title
        cardTitleTextView.setText(allergy + " Allergy - " + language);
        //get the text for the body of the card based on the allergy and language.
        String bodyText = CardManager.getCardBodyText(allergy, language, this);

//        //get display size
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//        Log.d(TAG, "x= " + width + "    y = " + height + " size = " + size);
//        Log.d(TAG, getResources().getConfiguration().screenLayout + "    " + Configuration.SCREENLAYOUT_SIZE_LARGE);
//
//        if ((getResources().getConfiguration().screenLayout &
//                Configuration.SCREENLAYOUT_SIZE_MASK) ==
//                Configuration.SCREENLAYOUT_SIZE_LARGE) {
//            // on a large screen device ...
//            Log.d(TAG, Configuration.SCREENLAYOUT_SIZE_MASK + "  = true   ");
//        }

        // if language text size gets too large or small or nulls, set it to 21.
        //if (languageTextSize < 20 || languageTextSize > 30) languageTextSize = 21;

        // cardBodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, languageTextSize);

        //set the text for the card body
        //cardBodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, languageTextSize);
        cardBodyTextView.setText(bodyText);

        //make the icon clickable and return to main activity, not working at present
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locked) {
                    showLockedToast();
                } else {
                    Intent intent = new Intent(CardActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();
                    startActivity(intent);
                }
            }
        });

        englishTranslation = findViewById((R.id.englishTranslationButton));
        englishTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked) {
                    showLockedToast();
                } else {
                    showCardInEnglish(allergy);
                }
            }
        });

        allergyDescription = findViewById((R.id.allergyDescription));
        allergyDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked) {
                    showLockedToast();
                } else {
                    showAllergyText(allergy);
                }
            }
        });

        allergyPicture = (Button)

                findViewById((R.id.allergyPicture));
        allergyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locked) {
                    showLockedToast();
                } else {
                    showAllergyPicture(allergy);
                }
            }
        });

        lock = findViewById((R.id.lock));
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockScreen();
            }
        });

        //get the view and set a gesture detector for the card to respond ot flings.
        cardLL = (LinearLayout) findViewById(R.id.cardLL);

        final GestureDetector gestureDetectorC = new GestureDetector(this.getApplicationContext(), new GestureListener());
        cardLL.setOnTouchListener(new View.OnTouchListener()

        {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                return gestureDetectorC.onTouchEvent(event);
            }
        });
    }

    /**
     * lock the card to stop accidental touches
     */
    private void lockScreen() {
        if (!locked) {
            showLockedToast();
            lock.setText("Unlock\nCard");
            lock.setBackgroundColor(redColour);
        } else {
            showUnlockedToast();
            lock.setText("Lock\nCard");
            lock.setBackgroundColor(lightBlueColour);
        }
        locked = !locked;
    }

    /**
     * show locked toast
     */
    private void showLockedToast() {
        toast.cancel();
        toast = Toast.makeText(context, "Card is locked", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * show unlocked toast
     */
    private void showUnlockedToast() {
        toast.cancel();
        toast = Toast.makeText(context, "Card is unlocked", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * show the allergy picture
     */
    private void showAllergyPicture(String allergy) {
        int allergyTextId = getResources().getIdentifier(allergy.toLowerCase(), "string", this.getPackageName());
        Log.d("Allergy Text ID", allergyTextId + "");
        //showAlert(allergy, this.getString(allergyTextId));
        showImageAlert(allergy + "_image");
    }

    /**
     * showImageAlert show a dialog with an Image.
     */
    public void showImageAlert(String allergyImageName) {

        AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
        alertadd.setIcon(R.mipmap.ic_logo);
        alertadd.setTitle(allergy);

        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alert_image_view, null);
        ImageView iv = (ImageView) view.findViewById(R.id.dialog_imageview);
        iv.setImageResource(CardManager.getResourceID(allergyImageName));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        iv.setLayoutParams(layoutParams);

        alertadd.setView(view);
        alertadd.setNeutralButton("Close", null);
        alertadd.show();
    }

    /**
     * Show the text in english so the user can interpret it.
     * Idea from Steve Hunt.
     */
    private void showCardInEnglish(String allergy) {
        showAlert("English Translation for " + allergy + " Allergy", CardManager.getCardBodyText(allergy, "English", this));
    }

    /**
     * Show the allergy text in english
     */
    private void showAllergyText(String allergy) {
        int allergyTextId = getResources().getIdentifier(allergy.toLowerCase(), "string", this.getPackageName());
        Log.d("Allergy Text ID", allergyTextId + "");
        showAlert(allergy, this.getString(allergyTextId));
    }

    /**
     * showAlert dialog method. Maybe pull this out to static class at a later date.
     *
     * @param message
     */
    void showAlert(String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setIcon(R.mipmap.ic_logo);
        bld.setTitle(title);
        bld.setMessage(message);
        bld.setNeutralButton("CLOSE", null);
        Log.d(TAG, "Showing showAlert dialog: " + message);
        bld.create();
        AlertDialog dialog = bld.show();

        //center the text in the showAlert
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    /**
     * Override the onBackPressed method so that when the back button is pressed the user is
     * returned to the Main Activity
     */
    @Override
    public void onBackPressed() {
        if (locked) {
            showLockedToast();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(CardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
            startActivity(intent);
        }
    }

    /**
     * Define the gesture detector class to respond to user inputs.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        /**
         * onFling method for the Card Activity.
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (locked) {
                showLockedToast();
            } else {
                //Detect a left to right fling that exceeds the distance and velocity minimums defined in the class attributes.
                // Animate the move to the a new Card activity utilising the attributes of the next card in the Array.
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.
                        abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (cardPos > 0) {
                        Intent newCardIntent = new Intent(CardActivity.this, CardActivity.class);
                        newCardIntent.putExtra(CardManager.cn, cardPos - 1);
                        startActivity(newCardIntent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }
                    //If the card being flung is the first in the list the card flashes, showing that it is first in the list.
                    if (cardPos == 0) {
                        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                        anim.setDuration(500);
                        cardLL.startAnimation(anim);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {

                            }

                        }, anim.getDuration());

                    }
                }
                // Detect a right to left fling that exceeds the distance and velocity minimums defined in the class attributes.
                // Animate the move to the a new Card activity utilising the attributes of the next card in the Array.
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int newCardPos = cardPos + 1;
                    if (newCardPos < CardListFragment.cardList.size()) {
                        Intent newCardIntent = new Intent(CardActivity.this, CardActivity.class);
                        newCardIntent.putExtra(CardManager.cn, newCardPos);
                        startActivity(newCardIntent);
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                    }
                    //If the card being flung is the last in the list the card flashes, showing that it is last in the list.
                    if (newCardPos == CardListFragment.cardList.size()) {
                        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                        anim.setDuration(300);
                        cardLL.startAnimation(anim);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {

                            }

                        }, anim.getDuration());

                    }
                }
            }

            return false;
        }

        // onDown method must be defined to ensure Gesture Dectore behaves correctly as per the api.
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
