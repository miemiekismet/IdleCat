package katrina.idle.cat;

import android.util.Log;

import java.util.Date;

import static java.lang.Math.toIntExact;

/**
 * Created by katrina on 08/06/2017.
 */

public class CatStatusHolder {
    // Static values
    private final String TAG = "CatStatus";
    private final Integer MAX_HUNGRY = 100;
    private final Integer MAX_INTIMACY = 100;
    private final Integer ZERO = 0;
    private final Integer MILLIS = 1000;

    // Status values
    private CatStatus mStatus;

    // Constructor
    public CatStatusHolder() {
        mStatus = new CatStatus();
        mStatus.last_login_sec = System.currentTimeMillis() / MILLIS;
        // Default constructor required for calls to DataSnapshot.getValue(CatStatus.class)
    }

    public CatStatusHolder(Integer hungry, Integer intimacy) {
        if (hungry > MAX_HUNGRY || hungry < ZERO || intimacy > MAX_INTIMACY || intimacy < MAX_HUNGRY) {
            Log.d(TAG, "Bad init CatStatus");
            return;
        }
        else {
            mStatus = new CatStatus();
            mStatus.hungry = hungry;
            mStatus.intimacy = intimacy;
            mStatus.food = 10;
            mStatus.last_login_sec = System.currentTimeMillis() / MILLIS;
            mStatus.creation_date = new Date();
        }
    }

    public CatStatus getStatus() {return mStatus;}

    // Init status from database.
    public void initStatus(CatStatus status) {
        Long time_sec = System.currentTimeMillis() / MILLIS;
        Long time_diff = (time_sec - status.last_login_sec) / 10;
        if (time_diff < 0) {
            Log.e(TAG, "Bad init. last login sec bigger than current time");
            // Do nothing now, figure out solution later.
        }
        if (status.hungry > time_diff) {
            status.hungry -= toIntExact(time_diff);
        } else {
            status.hungry = 0;
        }
        if (status.intimacy > time_diff) {
            status.intimacy -= toIntExact(time_diff);
        } else {
            status.intimacy = 0;
        }
        mStatus = status;

    }

    public void setStatus(CatStatus status) {mStatus = status;}

    public void increaseFood() {
        mStatus.food++;
    }

    public Integer getFood() {
        return mStatus.food;
    }

    // Hungry related functions
    public boolean feed() {
        if (mStatus.food > 0) {
            mStatus.hungry = mStatus.hungry + 30 > MAX_HUNGRY ? MAX_HUNGRY : mStatus.hungry + 30;
            mStatus.food -= 1;
            return true;
        } else {
            return false;
        }
    }

    public void hungry() {
        mStatus.hungry = mStatus.hungry - 1 < 0 ? 0 : mStatus.hungry - 1;
    }

    public boolean isFull() {
        return mStatus.hungry == MAX_HUNGRY;
    }

    public void setHungry(Integer value) {
        if (value > MAX_HUNGRY || value < ZERO) return;
        mStatus.hungry = value;
    }

    public Integer getHungry() {
        return mStatus.hungry;
    }

    // Intimacy related functions
    public void pat(Integer value) {
        if (value < ZERO) return;
        mStatus.intimacy = mStatus.intimacy + value > MAX_INTIMACY ? MAX_INTIMACY : mStatus.intimacy + value;
    }

    public void unhappy() {
        mStatus.intimacy = mStatus.intimacy - 1 < 0 ? 0 : mStatus.intimacy - 1;
    }

    public boolean isHappy() {
        return mStatus.intimacy == MAX_INTIMACY;
    }

    public Integer getIntimacy() { return mStatus.intimacy; }

    public void updateTime() { mStatus.last_login_sec = System.currentTimeMillis() / MILLIS; }

    public Integer getAge() {
        if (mStatus.creation_date == null) {
            return 0;
        }
        Date today = new Date();
        Integer age = (int) ((today.getTime() - mStatus.creation_date.getTime()) / (1000 * 60 * 60 * 24));
        if (age <= 0) {
            Log.e(TAG, "Database might be corrupt.");
            age = 1;
        }
        return age;
    }
    public String getAgeString() {
        Integer age = getAge();
        return age.toString() + (age <= 1 ? " Day Old" : " Days Old");
    }
}
