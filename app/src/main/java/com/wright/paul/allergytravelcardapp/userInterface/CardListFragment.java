package com.wright.paul.allergytravelcardapp.userInterface;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import jp.wasabeef.recyclerview.animators.LandingAnimator;

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
    private TextView emptyTV;
    protected RecyclerView cardListView;
    private CardAdapter cardAdapter;

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
                createNotification_2(position);
                Toast.makeText(getActivity(), "Allergy Card added to Notification Bar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteButtonListener(View v, int position) {
                deleteCard(position);
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
        cardListView.setItemAnimator(new LandingAnimator());
        ((SimpleItemAnimator) cardListView.getItemAnimator()).setSupportsChangeAnimations(false);
        swipeToDismissTouchHelper.attachToRecyclerView(cardListView);

        //handle empty list view
        emptyTV = view.findViewById(R.id.emptyTV);
        checkEmptyView();

        //TextView emptyText = (TextView) view.findViewById(R.id.emptyTV);
        emptyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateCardActivity.class);
                startActivity(intent);
            }
        });
        //cardListView.setEmptyView(emptyText);

        //create an instance of the handler for non-UI process
        handler = new Handler();

        return view;
    }


    private void checkEmptyView() {

        if (cardList.isEmpty()) {
            cardListView.setVisibility(View.GONE);
            emptyTV.setVisibility(View.VISIBLE);
        }
        else {
            cardListView.setVisibility(View.VISIBLE);
            emptyTV.setVisibility(View.GONE);
        }
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

    private void viewCard(int position) {
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
                        .setSmallIcon(R.drawable.appl_icon)
                        .setContentTitle(notificationCard.getLanguage() + " " + notificationCard.getAllergy() + " Allergy Card")
                        .setContentText("Tap to view Allergy Card");
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

    private void createNotification_2(int position) {
        Card notificationCard = cardList.get(position);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_notif)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), CardManager.getResourceID(notificationCard.getLanguage())))
                .setContentTitle(notificationCard.getLanguage() + " " + notificationCard.getAllergy() + " Allergy Card")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Tap to show card"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Creates an explicit intent for the Card Activity and passes the language and allergy to the
        Intent notificationIntent = new Intent(getActivity().getApplicationContext(), CardActivity.class);
        //The notification intent needs to pass the language and allergy fields to the card activity incase the card
        //is deleted prior to viewing.
        notificationIntent.putExtra(CardManager.ls, notificationCard.getLanguage());
        notificationIntent.putExtra(CardManager.as, notificationCard.getAllergy());
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

    //Swipe to Delete
    ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteCard(viewHolder.getAdapterPosition());
        }
    });

    /**
     * deleteCard method to remove a card from the list and db and refresh the view.
     */
    public void deleteCard(int position) {
        Card card = cardList.get(position);
        Log.d(TAG, "Card == " + cardList.indexOf(cardList.get(position)) + "  size == " + cardList.size());
        cardDBOpenHelper.deleteCardID(cardList.get(position).getDbID());
        cardList.remove(card);
        cardAdapter.notifyDataSetChanged();

        if(cardList.size()==0){
            checkEmptyView();
        }

        //Toast.makeText(getActivity(), "Allergy Card Deleted", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Card == " + cardList.indexOf(card) + "  size == " + cardList.size());

        mNotificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //define the interface for the fragment to allow the activity to pass data to this fragment
    public interface CardListListener {
        void onCardListTouched();
    }

    /**
     * Define the gesture detector class to respond to user inputs.
     */

}




