package com.example.julia.chat.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.core.QBEntityCallbackImpl;
import com.example.julia.chat.ApplicationSingleton;
import com.example.julia.chat.core.ChatService;
import com.quickblox.users.model.QBUser;
import com.example.julia.chat.R;

import java.util.List;

public class SplashActivityChat extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
                Intent intent = new Intent(SplashActivityChat.this, DialogsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivityChat.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }
}