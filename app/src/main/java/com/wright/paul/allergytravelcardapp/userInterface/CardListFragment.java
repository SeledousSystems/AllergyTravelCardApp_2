package com.wright.paul.allergytravelcardapp.userInterface;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.Card;
import com.wright.paul.allergytravelcardapp.model.CardDBOpenHelper;
import com.wright.paul.allergytravelcardapp.model.CardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that defines the CardListFragment
 */
public class CardListFragment extends Fragment implements View.OnClickListener {

    private static final int SWIPE_MIN_DISTANCE = 300;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    /**
     * Class Attributes
     */
    //Static list to hold the user created cards during run time
    public static List<Card> cardList = new ArrayList<>();
    static Boolean appStartUp = true;
    //    protected ListView cardListView;
    protected CustomListViewAdaptor cardListViewAdaptor;
    protected int itemPosition;
    private SQLiteDatabase db;
    private CardDBOpenHelper cardDBOpenHelper;
    private Context context;
    private String TAG = "CARDLISTFRAGMENTTAG";
    private Handler handler;
    private CardListListener listener = null;
    private NotificationManager mNotificationManager;

    protected RecyclerView cardListView;
    private CardHolder cardHolder;
    private CardAdapter cardAdapter;
    private RecyclerView recyclerView;

    /**
     * Static method to get the static Card Collection
     *
     * @return
     */
    public static List<Card> getCardList() {
        return cardList;
    }

    /**
     * onCreateView method for when the fragment is created. It initialises the view and defines the
     * context menu and gesture detector.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate and initialise the view
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);
        container = new LinearLayout(view.getContext());
        LayoutTransition layoutTransition = new LayoutTransition();
        container.setLayoutTransition(layoutTransition);
        //assign the conext
        context = this.getContext();
        //get the SQLite DB
        cardDBOpenHelper = new CardDBOpenHelper(view.getContext());
        db = cardDBOpenHelper.getReadableDatabase();

        //if this is the startup of the app then add all cards from the DB to the ArrayList so
        //they can be displayed in the list view
        if (appStartUp) {
            cardList.clear();
            cardList.addAll(cardDBOpenHelper.getAllCards());
            Collections.sort(cardList);
            appStartUp = false;
        }
        //recycler here
        cardListView = (RecyclerView) view.findViewById(R.id.listView);
        cardAdapter = new CardAdapter(view.getContext(), R.layout.custom_list_view, cardList, new CardAdapter.CardAdapterListener() {
            @Override
            public void viewButtonListener(View v, int position) {
                viewCard(position);
            }

            @Override
            public void notifButtonListener(View v, int position) {
                createNotification(position);
            }

            @Override
            public void deleteButtonListener(View v, int position) {
                final Card cardDelete = cardList.get(position);
                deleteCard(cardDelete);
                Toast.makeText(getActivity(), "Allergy Card Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardWrapperListener(int position) {
                viewCard(position);
            }
        });

        cardListView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        cardListView.setLayoutManager(mLayoutManager);
        cardListView.setAdapter(cardAdapter);


        Log.d("***", "cardlist size = " + cardList.size());

        //assign and initialise the list view
//        cardListView = (ListView) view.findViewById(R.id.listView);
//        cardListViewAdaptor = new CustomListViewAdaptor(view.getContext(), cardList);
//        cardListView.setAdapter(cardListViewAdaptor);
        //initialise the context menu
        registerForContextMenu(cardListView);

        //handle empty list view
        TextView emptyText = (TextView) view.findViewById(R.id.emptyTV);
        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateCardActivity.class);
                startActivity(intent);
            }
        });
        //cardListView.setEmptyView(emptyText);

        //create an instance of the handler for non-UI process
        handler = new Handler();
        //define the gesture detector for the list view
        final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureListener());
//        cardListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(final View view, final MotionEvent event) {
//                //itemPosition = cardListView.getSelectedItemPosition();
//                itemPosition = cardListView.getC;
//                return gestureDetector.onTouchEvent(event);
//            }
//        });
        return view;
    }

    /**
     * onDetach method for when the fragment is detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * onClick method to handle click event for the buttons and the empty list view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        }
    }

    /**
     * onAttach method for when the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CardListListener)) {
            throw new IllegalStateException("Container activity must implement the FragmentListener interface.");
        }
        listener = (CardListListener) context;
    }

    private void viewCard(int position){
        Card cardView = cardList.get(position);
        Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
        newCardIntent.putExtra(CardManager.ls, cardView.getLanguage());
        newCardIntent.putExtra(CardManager.as, cardView.getAllergy());
        newCardIntent.putExtra(CardManager.cn, cardList.indexOf(cardView));
        startActivity(newCardIntent);
    }

    private void createNotification(int position) {
        Card notificationCard = cardList.get(position);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity().getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle(notificationCard.getLanguage() + " " + notificationCard.getAllergy() + " Allergy Card")
                        .setContentText("Tap to view this Allergy Card");
        // Creates an explicit intent for the Card Activity and passes the language and allergy to the
        Intent notificationIntent = new Intent(getActivity().getApplicationContext(), CardActivity.class);
        //The notification intent needs to pass the language and allergy fields to the card activity incase the card
        //is deleted prior to viewing.
        notificationIntent.putExtra(CardManager.ls, notificationCard.getLanguage());
        notificationIntent.putExtra(CardManager.as, notificationCard.getAllergy());
        //notificationIntent.putExtra(CardManager.cn, notificationCard.getDbID());
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity().getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(CardActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(notificationCard.getDbID(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationCard.getDbID(), mBuilder.build());
    }

    /**
     * method to set up the context menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        switch (item.getItemId()) {
            //Context menu selection to view the card, fires an intent for the Card activity
            case R.id.view_card:
                viewCard(position);
                return true;
            //Context menu selection to delete card card is deleted from the list view
            case R.id.delete_card:
                final Card cardDelete = cardList.get(position);
                deleteCard(cardDelete);
                Toast.makeText(getActivity(), "Allergy Card Deleted", Toast.LENGTH_SHORT).show();
                return true;
            //Context menu selection to move the card to the bottom of the list view by setting the
            // card's lastviewed attribute to less than the last card in the list and then updating the listview.
            case R.id.move_card_bottom:
                Card cardBottom = cardList.get(position);
                cardBottom.setLastViewed(cardList.get(cardList.size() - 1).getLastViewed() - 1);
                Collections.sort(cardList);
                cardListViewAdaptor.notifyDataSetChanged();
                return true;
            //Context menu selection to move the card to the top of the list view by setting the lastviewed
            //int to current date and then updating the list.
            case R.id.move_card_top:
                Card cardTop = cardList.get(position);
                cardTop.setLastViewed(CardManager.getCurrentDateInt());
                Collections.sort(cardList);
                cardListViewAdaptor.notifyDataSetChanged();
                return true;
            //Context menu selection to show the countries that the card can be used in. Gets teh list, creates a
            //message and then displays it in an AlertDialog.
            case R.id.show_countries:
                final Card cardShowCountries = cardList.get(position);
                final String language = cardShowCountries.getLanguage();
                final String allergy = cardShowCountries.getAllergy();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] countryArray = CardManager.getCountries(cardShowCountries.getLanguage(), context);
                        final String message = "Your " + language + " " + allergy + " Allergy Card can be used in " + CardManager.buildCountryMessage(countryArray);
                        //on completion of the background task run the handler to post the showAlert dialog, which is run in the UI Thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle(cardShowCountries.getLanguage() + " " + cardShowCountries.getAllergy() + " Allergy Card.");
                                alertDialog.setMessage(message);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CLOSE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                }).start();
                return true;
            //Context menu selection to create a notification for the user to display a card
            //directly from their notification bar. Clicking on the notification fires an intent
            //to display the card
            case R.id.create_notification:


                //Context menu default selection
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * deleteCard method to remove a card from the list and db and refresh the view.
     */
    public void deleteCard(Card card) {
        cardDBOpenHelper.deleteCardID(card.getDbID());
        cardList.remove(card);
        cardAdapter.notifyDataSetChanged();
        mNotificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(card.getDbID());
    }

    /**
     * onCreateContextMenu method override for creating the context menu for the list view
     *
     * @param menu
     * @param view
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.long_click_menu, menu);
    }

    //define the interface for the fragment to allow the activity to pass data to this fragment
    public interface CardListListener {
        void onCardListTouched();
    }

    /**
     * Define the gesture detector class to respond to user inputs.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * method to
         *
         * @param position
         * @param listView
         * @return
         */
        public View getViewByPosition(int position, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (position < firstListItemPosition || position > lastListItemPosition) {
                return listView.getAdapter().getView(position, null, listView);
            } else {
                final int childIndex = position - firstListItemPosition;
                Log.d(TAG, firstListItemPosition + " " + position);
                return listView.getChildAt(childIndex);
            }
        }

//        /**
//         * override method for a singleTap event. Intent is fired to the card activity to
//         * display the tapped card.
//         *
//         * @param e
//         * @return
//         */
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            final int position = cardListView.pointToPosition((int) e.getX(), (int) e.getY());
//            if (position != -1) {
//                //get the card tapped and send an intent to the Card Activity
//                Card card = cardList.get(position);
//                Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
//                newCardIntent.putExtra(CardManager.cn, position);
//                startActivity(newCardIntent);
//                //animate the transition
//                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
//            }
//            return super.onSingleTapConfirmed(e);
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            //get the card position id
//            int position = cardListView.pointToPosition((int) e1.getX(), (int) e1.getY());
//            cardListView.getSelectedView();
//            if (position != -1 && position < cardList.size()) {
//                //Detect a left to right fling that exceeds the distance and velocity minimums defined in the class attributes.
//                //On detection delete the flung card
//                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Handler handler = new Handler();
//                    //animate the deletion in the direction on the fling
//                    Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
//                    anim.setDuration(500);
//                    Log.d(TAG, position + "");
//                    final Card card = cardList.get(position);
//                    Log.d(TAG, position + "" + card.getLanguage() + " " + cardListView.getChildCount());
//                    //cardListView.getChildAt(position).startAnimation(anim);
//                    getViewByPosition(position, cardListView).startAnimation(anim);
//                    anim.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            //delete the card.
//                            deleteCard(card);
//                            //notify user
//                            Toast.makeText(getActivity(), "Allergy Card Deleted", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//                    });
//
//
//                }
//            }
//            return false;
//        }
    }
}




