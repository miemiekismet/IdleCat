package katrina.idle.cat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*;

import java.util.Timer;

public class DisplayCatActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private static final String TAG = "DisplayCatActivity";
    private static final Integer UPDATE_FREQUENCY = 10 * 1000;

    // Firebase instance variables
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mFirebaseDatabaseReference;
    private RewardedVideoAd mRewardedAd;
    private AdView mBannerAdView;

    // Activity items
    private TextView mAgeTextView;
    private TextView mCatHungryTextView;
    private TextView mCatIntimacyTextView;
    private TextView mCatFoodTextView;
    private TextView mCatChatTextView;
    private Timer mCatTimer;
    private CatStatusHolder mCatStatus;
    private CatSchedule mCatSchedule;
    private CatChat mCatChat;

    // User info
    private String mUsername = "";
    private String mUID = "";

    // System info
    private boolean inited = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cat);

        // Init user info.
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(MainActivity.USERNAME);
        mUID = intent.getStringExtra(MainActivity.USERID);
        TextView textView = (TextView) findViewById(R.id.username_text_view);
        textView.setText("Hello, " + mUsername + "!");

        // Init cat status.
        mCatStatus = null;
        mCatHungryTextView = (TextView) findViewById(R.id.cat_hungry_text_view);
        mCatIntimacyTextView = (TextView) findViewById(R.id.cat_intimacy_text_view);
        mCatFoodTextView = (TextView) findViewById(R.id.cat_food_text_view);
        mAgeTextView = (TextView) findViewById(R.id.age_text_view);

        // Init cat chat.
        mCatChat = new CatChat();
        mCatChatTextView = (TextView) findViewById(R.id.cat_chat_text_view);

        // Init Firebase Analytics.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, mUsername);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,
                payload);

        // Firebase database codes.
        mCatTimer = new Timer();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(mUID);
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get init value. null indicates it's a new user.
                CatStatus s = dataSnapshot.getValue(CatStatus.class);
                if (s == null) {
                    // New user initate.
                    mCatStatus= new CatStatusHolder(100, 100);
                    mFirebaseDatabaseReference.setValue(mCatStatus.getStatus());
                } else {
                    mCatStatus = new CatStatusHolder();
                    mCatStatus.initStatus(s);
                }
                // Update UI
                updateCatStatusTextViews();
                // Start timer.
                mCatSchedule = new CatSchedule(mFirebaseDatabaseReference, mCatStatus);
                mCatTimer.schedule(mCatSchedule, 1000, UPDATE_FREQUENCY);
                inited = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read init value.", error.toException());
            }
        });
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CatStatus s = dataSnapshot.getValue(CatStatus.class);
                if (s == null) {
                    Log.e(TAG, "If " + mUsername + "is not a new user, database is broken...");
                    return;
                }
                mCatStatus.setStatus(s);
                updateCatStatusTextViews();
                Log.d(TAG, "Value is: " + mCatStatus.getHungry());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Init Ads.
        mRewardedAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        mBannerAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest
                .Builder()
                .build();
        mBannerAdView.loadAd(adRequest);
    }

    public void feedMyCat(View view) {
        if (!inited) return;
        boolean success = mCatStatus.feed();
        mFirebaseDatabaseReference.setValue(mCatStatus.getStatus());
        if (success) {
            if (mCatStatus.isFull()) {
                mCatChatTextView.setText(getString(R.string.full_chat));
            } else {
                mCatChatTextView.setText(getString(R.string.eating_chat));
            }
            ImageView cat = (ImageView)findViewById(R.id.cat_image_view);
            cat.setImageResource(R.drawable.pusheen_eat);
        } else {
            mCatChatTextView.setText(getString(R.string.no_food_chat));
        }
    }

    public void chatWithCat(View view) {
        if (!inited) { return; }
        Resources res = getResources();
        String[] chat_array;
        Integer pat;
        Integer age = mCatStatus.getAge();

        // Bypass age for now.
        age = 20;

        ImageView cat = (ImageView)findViewById(R.id.cat_image_view);
        if (mCatStatus.getHungry() > 70) {
            if (age <= 3) {
                chat_array = res.getStringArray(R.array.happy_chat_array_1);
            } else if (age <= 10) {
                chat_array = res.getStringArray(R.array.happy_chat_array_2);
            } else {
                chat_array = res.getStringArray(R.array.happy_chat_array_3);
            }
            cat.setImageResource(R.drawable.pusheen_happy_1);
            pat = 3;
        } else if (mCatStatus.getHungry() > 30) {
            if (age <= 3) {
                chat_array = res.getStringArray(R.array.normal_chat_array_1);
            } else if (age <= 10) {
                chat_array = res.getStringArray(R.array.normal_chat_array_2);
            } else {
                chat_array = res.getStringArray(R.array.normal_chat_array_3);
            }
            double v = Math.random();
            if (v > 2/3) {
                cat.setImageResource(R.drawable.pusheen_normal_1);
            } else if (v > 1/3) {
                cat.setImageResource(R.drawable.pusheen_normal_2);
            } else {
                cat.setImageResource(R.drawable.pusheen_normal_3);
            }
            pat = 1;
        } else {
            if (age <= 3) {
                chat_array = res.getStringArray(R.array.hungry_chat_array_1);
            } else if (age <= 10) {
                chat_array = res.getStringArray(R.array.hungry_chat_array_2);
            } else {
                chat_array = res.getStringArray(R.array.hungry_chat_array_3);
            }
            cat.setImageResource(R.drawable.pusheen_hungry);
            pat = 0;
        }
        mCatChatTextView.setText(chat_array[mCatChat.chat(chat_array.length)]);
        mCatStatus.pat(pat);

        updateCatStatusTextViews();
        mFirebaseDatabaseReference.setValue(mCatStatus.getStatus());

        /* Glide.with(this)
                .load(R.drawable.kat_walk)
                .into(cat); */
    }

    public void watchAds(View view) {
        if (mRewardedAd.isLoaded()) {
            mRewardedAd.show();
        } else {
            mCatChatTextView.setText(getString(R.string.no_ads));
        }
    }


    // Rewarded video functions
    private void loadRewardedVideoAd() {
        mRewardedAd.loadAd(
                "ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder()
                        .build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
        mCatChatTextView.setText(getString(R.string.on_rewarded));
        mCatStatus.pat(reward.getAmount());
        mCatStatus.increaseFood();
        Toast.makeText(this, "Intimacy increased.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Got 1 food.", Toast.LENGTH_SHORT).show();
        updateCatStatusTextViews();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d(TAG, "Cannot load rewarded ads.");
        Toast.makeText(this, "Our grocery store cannot be stock up.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "More food in grocery store!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        // Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        // Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    private void updateCatStatusTextViews() {
        mCatHungryTextView.setText(mCatStatus.getNoHungry().toString());
        mCatIntimacyTextView.setText(mCatStatus.getIntimacy().toString());
        mCatFoodTextView.setText(mCatStatus.getFood().toString());
        mAgeTextView.setText(mCatStatus.getAgeString());
    }
}
