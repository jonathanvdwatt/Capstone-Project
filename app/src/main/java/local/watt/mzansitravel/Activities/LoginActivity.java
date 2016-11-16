package local.watt.mzansitravel.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.watt.mzansitravel.R;
import local.watt.mzansitravel.Utils.GradientBackgroundPainter;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private CallbackManager mCallbackManager; // For Facebook Logins
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability mGoogleApiAvailability;
    private int mRequestCode;
    private static final int RC_SIGN_IN = 007;

    /*
     * Property Animation API stuff
     */
    private GradientBackgroundPainter mGradientBackgroundPainter;
    @BindView(R.id.activity_login) CoordinatorLayout mActivityLogin;

    private SharedPreferences mPref;

    @BindView(R.id.google_login_button) SignInButton _googleLoginButton;
    @BindView(R.id.fb_login_button) LoginButton _fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        /*
         * Color animation
         */
        final int[] drawables = new int[9];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;
        drawables[3] = R.drawable.gradient_4;
        drawables[4] = R.drawable.gradient_5;
        drawables[5] = R.drawable.gradient_6;
        drawables[6] = R.drawable.gradient_7;
        drawables[7] = R.drawable.gradient_8;
        drawables[8] = R.drawable.gradient_9;

        mGradientBackgroundPainter = new GradientBackgroundPainter(mActivityLogin, drawables);
        mGradientBackgroundPainter.start();

        /*
         * Google Sign-in
         */

        // Configure sign-in to request the User's ID, email address and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize the G+ button
        _googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        _googleLoginButton.setScopes(gso.getScopeArray());

        // Set G+ button's onClick
        _googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin();
            }
        });


        /*
         * Facebook Sign-in
         */

        _fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGradientBackgroundPainter.stop();
    }

    /*
         * Log in via Google
         */
    public void googleLogin() {
        Log.d(TAG, "Attempting Google logon");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    /*
     * Log in via Facebook
    */
    public void facebookLogin() {
        Log.d(TAG, "Attempting Facebook logon");

        mCallbackManager = CallbackManager.Factory.create();

        _fbLoginButton.setReadPermissions(getString(R.string.facebook_login_set_read_permissions_text));
        _fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), R.string.facebook_login_successful_logon_text, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.facebook_logon_cancelled_logon_text, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Failed Facebook logon " + error);
            }
        });

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFacebookSignInResult();
            }
        }, 3000);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult(" + result.isSuccess() + ")");

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Log.d(TAG, "Successful Google login");
            Log.d(TAG, "Display name: " + account.getDisplayName());
            Log.d(TAG, "Email: " + account.getEmail());

            /*
             * Added logged in status to SharedPreference
             */
            mPref = getSharedPreferences(getString(R.string.authentication_shared_preference_key_text), 0);
            SharedPreferences.Editor editor = mPref.edit();
            editor.putBoolean(getString(R.string.authentication_shared_pref_editor_boolean_text), true);
            editor.commit();

            finish();
        }
    }

    private void handleFacebookSignInResult() {
        Log.d(TAG, "Successful Facebook login");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

        if(requestCode == RESULT_OK) {
            this.finish();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), mRequestCode).show();
            return;
        }
    }
}
