package katrina.idle.cat;

import android.util.Log;

/**
 * Created by katrina on 08/06/2017.
 */

public class CatStatusHolder {
    // Static values
    private final String TAG = "CatStatus";
    private final Integer MAX_HUNGRY = 100;
    private final Integer MAX_INTIMACY = 100;
    private final Integer ZERO = 0;

    // Status values
    private CatStatus mStatus;

    // Constructor
    public CatStatusHolder() {
        mStatus = new CatStatus();
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
        }
    }

    public CatStatus getStatus() {return mStatus;}
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
}
