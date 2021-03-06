package com.wright.paul.allergytravelcardapp.userInterface;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

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
    private SharedPreferences premium;
    private String premiumPref = "APP_PREF";
    private String premiumPrefBool = "palladium";
    public static boolean mIsPremium = false;
    private Intent newCardIntent;

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
        premium = context.getSharedPreferences(premiumPref, 0);

        //if this is the startup of the app then add all cards from the DB to the ArrayList so
        //they can be displayed in the list view
        if (appStartUp) {
            cardList.clear();
            cardList.addAll(cardDBOpenHelper.getAllCards());
            Collections.sort(cardList);
            appStartUp = false;
        }

        //define item touchhelper to handle drags and flicks in recycler view
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            int dragFrom = -1;
            int dragTo = -1;

            Card fromCard;
            Card toCard;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(cardList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                cardAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if(dragFrom == -1) {
                    fromCard = cardList.get(fromPosition);
                    dragFrom =  fromPosition;
                }
                dragTo = toPosition;
                toCard = cardList.get(toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                deleteCard(viewHolder.getLayoutPosition());
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    cardDBOpenHelper.saveCollectionToDB(cardList);

                    Log.d("clearViewOut", cardDBOpenHelper.getAllCards().toString() );
                    cardAdapter.notifyDataSetChanged();
                }
                dragFrom = dragTo = -1;
            }
        };

        //recycler view attached to view here
        cardListView = view.findViewById(R.id.listView);

        //touch helper attached to recyclerview here
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(cardListView);

        cardAdapter = new CardAdapter(view.getContext(), R.layout.custom_list_view, cardList, new CardAdapter.CardAdapterListener() {
            @Override
            public void viewButtonListener(View v, int position) {
                viewCard(position);
            }

            @Override
            public void notifButtonListener(View v, int position) {
                try {
                    Card notifCard = cardList.get(position);
                    createNotification(notifCard);
                    String msg = notifCard.getAllergy() + " " + notifCard.getLanguage() + " Allergy Card added to Notification Bar.";
                    Toast.makeText(getActivity(), msg , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Opps, tried that and it didn't seem to work, sorry.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void deleteButtonListener(View v, int position) {
                deleteCardDialog(position);
            }

            @Override
            public void shareButtonListener(View v, int position) {
                Card cardView = cardList.get(position);
                Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
                newCardIntent.putExtra(CardManager.ls, cardView.getLanguage());
                newCardIntent.putExtra(CardManager.as, cardView.getAllergy());
                newCardIntent.putExtra(CardManager.cn, cardList.indexOf(cardView));
                startActivity(newCardIntent);

                newCardIntent.putExtra(CardManager.dl, true);
                if (isStoragePermissionGranted()) {
                    startActivity(newCardIntent);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }
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
        mIsPremium = getPref();
        return view;
    }

    boolean getPref() {
        premium = context.getSharedPreferences(premiumPref, 0);
        Log.d(TAG, mIsPremium + " get pref");
        return premium.getBoolean(premiumPrefBool, false);
    }

    void deleteCardDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCard(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setTitle("Delete " + cardList.get(position).getLanguage() + " " + cardList.get(position).getAllergy() + " Card?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isStoragePermissionGranted() {

        if (checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(getContext(), "permision is already granted", Toast.LENGTH_SHORT).show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                //Toast.makeText(getContext(), "requesting permision.", Toast.LENGTH_SHORT).show();

                // The callback method gets the result of the request.
            }
        } else {
            // Permission has already been granted, do the sharing here.
            //Toast.makeText(getContext(), "permission already granted, not requesting.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(getContext(), "Permision granted", Toast.LENGTH_SHORT).show();
                    startActivity(newCardIntent);
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Download cancelled, storage permission required to download cards", Toast.LENGTH_SHORT).show();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void checkEmptyView() {
        if (cardList.isEmpty()) {
            cardListView.setVisibility(View.GONE);
            emptyTV.setVisibility(View.VISIBLE);
        } else {
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

      private void createNotification(Card notifCard) {

        //Creates an explicit intent for the Card Activity for the card
        Intent notificationIntent = new Intent(getActivity().getApplicationContext(), CardActivity.class);
        //The notification intent needs to pass the language and allergy fields to the card activity in case the card
        //is deleted prior to viewing.
        notificationIntent.putExtra(CardManager.ls, notifCard.getLanguage());
        notificationIntent.putExtra(CardManager.as, notifCard.getAllergy());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity().getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(CardActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(notifCard.getDbID(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Allergy Travel Card");
        bigText.setBigContentTitle(notifCard.getAllergy() + " Allergy in " + notifCard.getLanguage());
        bigText.setSummaryText("Tap to open Allergy Card");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.notif_icon);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), CardManager.getResourceID(notifCard.getLanguage())));
        mBuilder.setContentTitle(notifCard.getAllergy() + " " + notifCard.getLanguage());
        //mBuilder.setContentText("");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // check to see if Oreo or higher (8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(notifCard.getDbID(), mBuilder.build());
    }

    /**
     * deleteCard method to remove a card from the list and db and refresh the view.
     */
    public void deleteCard(int position) {
        Card card = cardList.get(position);
        Log.d(TAG, "Card == " + cardList.indexOf(cardList.get(position)) + "  size == " + cardList.size());
        cardDBOpenHelper.deleteCardID(cardList.get(position).getDbID());
        cardList.remove(card);
        cardAdapter.notifyDataSetChanged();

        if (cardList.size() == 0) {
            checkEmptyView();
        }

        Toast.makeText(getActivity(), card.getLanguage() + " " + card.getAllergy() + " " + "Allergy Card deleted.", Toast.LENGTH_SHORT).show();

        mNotificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //define the interface for the fragment to allow the activity to pass data to this fragment
    public interface CardListListener {
        void onCardListTouched();
    }



}




