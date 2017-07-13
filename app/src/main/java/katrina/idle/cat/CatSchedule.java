package katrina.idle.cat;

import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;

import java.util.TimerTask;

/**
 * Created by katrina on 07/06/2017.
 */
public class CatSchedule extends TimerTask {
    private static final Integer MAX_TARDINESS  = 1000;
    private static final String TAG = "CatSchedule";
    private DatabaseReference mDatabaseRef;
    private CatStatusHolder mCatStatus;
    CatSchedule(DatabaseReference schedule_ref, CatStatusHolder status) {
        mDatabaseRef = schedule_ref;
        mCatStatus = status;
    }
    public void run() {
        if (System.currentTimeMillis() - scheduledExecutionTime() >=
                MAX_TARDINESS)
            return;  // Too late; skip this execution.
        // Perform the task
        timeLapse();
    }
    private void timeLapse() {
        mCatStatus.hungry();
        if (Math.random() > 0.5) {
            mCatStatus.unhappy();
        }
        if (Math.random() > 0.99) {
          mCatStatus.increaseFood();
        }
        mCatStatus.updateTime();
        mDatabaseRef.setValue(mCatStatus.getStatus());
    }
}
