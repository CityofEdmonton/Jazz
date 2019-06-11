package ca.edmonton.jazz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.net.Uri;
import android.view.View;

import java.io.IOException;
import java.util.HashMap;

import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.livechatinc.inappchat.ChatWindowView;
import com.livechatinc.inappchat.models.NewMessageModel;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


public class MainActivity extends AppCompatActivity implements ChatWindowView.ChatWindowEventsListener {

    private GoogleSignInClient mGoogleSignInClient;
    private ChatWindowView fullScreenChatWindow;
    private int RC_SIGN_IN = 1;
    private boolean isForeground = false;
    private FloatingActionButton mLunchChatButton;



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

        mLunchChatButton = findViewById(R.id.lunchChatButton);
        mLunchChatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            refreshIdToken();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("OS", "Android " + Build.VERSION.SDK_INT + " " + Build.VERSION.RELEASE);
            params.put("Device", Build.MANUFACTURER + " " + Build.MODEL);
            params.put("token", account.getIdToken());
            Log.d("token",account.getIdToken());

            ChatWindowConfiguration config = new ChatWindowConfiguration(
                    BuildConfig.license,
                    BuildConfig.group,
                    account.getDisplayName(),
                    account.getEmail(),
                    params
            );

            launchChat(this, config);

        }
        else {
            signIn();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fullScreenChatWindow != null) fullScreenChatWindow.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onChatWindowVisibilityChanged(boolean b) {
        Log.w("Chat", "Chat window visibility changed.");
    }

    @Override
    public void onNewMessage(NewMessageModel newMessageModel, boolean b) {
        Log.w("Chat", "Chat message received.");
        if (!isForeground) {
            Log.w("Chat", "Sending notification.");
            sendNotification(newMessageModel.getAuthor().getName(), newMessageModel.getText());
        }
    }

    @Override
    public void onStartFilePickerActivity(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean handleUri(Uri uri) {
        return false;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            refreshIdToken();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("OS", "Android " + Build.VERSION.SDK_INT + " " + Build.VERSION.RELEASE);
            params.put("Device", Build.MANUFACTURER + " " + Build.MODEL);
            params.put("token", account.getIdToken());

            ChatWindowConfiguration config = new ChatWindowConfiguration(
                    BuildConfig.license,
                    BuildConfig.group,
                    account.getDisplayName(),
                    account.getEmail(),
                    params
            );

            launchChat(this, config);

        }
        else {
            signIn();
        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void refreshIdToken() {
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }

    private void launchChat(Context context, ChatWindowConfiguration config) {
        if (fullScreenChatWindow == null) {
            fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(this);
            fullScreenChatWindow.setUpWindow(config);
            fullScreenChatWindow.setUpListener(this);
            fullScreenChatWindow.initialize();
        }
        fullScreenChatWindow.showChatWindow();
    }

    private void sendNotification(String author, String value) {
        // The id of the channel.
        String CHANNEL_ID = "jazz_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle(author)
                        .setContentText(value);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(1, mBuilder.build());
    }
}