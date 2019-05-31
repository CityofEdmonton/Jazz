package ca.edmonton.jazz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import android.net.Uri;

import java.io.IOException;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
public class MainActivity extends AppCompatActivity{

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(BuildConfig.server_client_id)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AppCenter.start(getApplication(), "b8cefd2a-6777-4033-ac9d-72a102c38559",
                Analytics.class, Crashes.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LunchChat();
    }

    @Override
    public void onResume() {
        super.onResume();
        LunchChat();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
        else {
            Log.w("Chat", "Received unknown code " + requestCode);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {
                LunchChat();
            }
            else {
                signIn();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Whoopsie", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void LunchChat(){
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Init a new thread to lunch the chat window
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    if (account != null) {
                        String scope = "oauth2:"+Scopes.EMAIL+" "+ Scopes.PROFILE;
                        String accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope, new Bundle());
                        Log.d("accessToken2", "accessToken:"+accessToken+", account.getDisplayName() "+account.getDisplayName() );
                        String params = "groups=" + BuildConfig.group+
                                "&name=" + account.getDisplayName() +
                                "&email=" + account.getEmail()+
                                "&token=" + accessToken;

                        Uri uri = Uri.parse("https://secure.livechatinc.com/licence/"+BuildConfig.license+"/open_chat.cgi?"+params); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);

                    }
                    else {
                        signIn();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
            }
        };
        AsyncTask.execute(runnable);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

