package katrina.idle.cat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final String USERNAME = "katrina.idle.cat.USERNAME";
    public static final String USERID = "katrina.idle.cat.USERID";
    private static final String TAG = "MainActivity";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername = "";
    private String mUID = "";
    private GoogleApiClient mGoogleApiClient;
    public static final String DEFAULT_USER = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUsername = mFirebaseUser.getDisplayName();
            mUID = mFirebaseUser.getUid();
            Intent intent = new Intent(this, DisplayCatActivity.class);
            intent.putExtra(USERNAME, mUsername);
            intent.putExtra(USERID, mUID);
            startActivity(intent);
        }
    }
    /** Called when the user taps the Send button */
    public void GenerateCat(View view) {
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            mUsername = mFirebaseUser.getDisplayName();
            mUID = mFirebaseUser.getUid();
            Intent intent = new Intent(this, DisplayCatActivity.class);
            intent.putExtra(USERNAME, mUsername);
            intent.putExtra(USERID, mUID);
            startActivity(intent);
        }
    }


}
