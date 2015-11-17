package com.example.julia.chat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.julia.chat.ApplicationSingleton;
import com.example.julia.chat.core.ChatService;
import com.example.julia.chat.ui.activities.DialogsActivity;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.example.julia.chat.R;
import com.example.julia.chat.helper.DataHolder;
import com.example.julia.chat.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.julia.chat.definitions.Consts.APP_ID;
import static com.example.julia.chat.definitions.Consts.AUTH_KEY;
import static com.example.julia.chat.definitions.Consts.AUTH_SECRET;

public class SplashActivity extends Activity{

    private Context context;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        initUI();

        // Initialize QuickBlox application with credentials.
        //
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);

        // Create QuickBlox session
        //
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                getAllUser();
            }

            @Override
            public void onError(List<String> errors) {
                // print errors that came from server
                DialogUtils.showLong(context, errors.get(0));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        // Login to REST API
        //
        final QBUser user = new QBUser();
        user.setLogin(ApplicationSingleton.USER_LOGIN);
        user.setPassword(ApplicationSingleton.USER_PASSWORD);

        ChatService.initIfNeed(this);

        ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

            @Override
            public void onSuccess() {
                // Go to Dialogs screen
                //
                //Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                //startActivity(intent);
               // finish();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });






    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getAllUser() {

        QBUsers.getUsers(null, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                DataHolder.getDataHolder().setQbUsersList(qbUsers);
                startGetAllUsersActivity();
            }

            @Override
            public void onError(List<String> errors) {
                DialogUtils.showLong(context, errors.get(0));
            }
        });
    }

    private void startGetAllUsersActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}