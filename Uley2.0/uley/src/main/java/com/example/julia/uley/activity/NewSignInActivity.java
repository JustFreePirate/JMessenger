package com.example.julia.uley.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.julia.uley.R;
import com.example.julia.uley.client.Client;
import com.example.julia.uley.common.Login;
import com.example.julia.uley.common.Package;
import com.example.julia.uley.common.PackageType;
import com.example.julia.uley.common.Pass;
import com.example.julia.uley.manager.ListenerManager;

/**
 * Created by Михаил on 05.12.2015.
 */
public class NewSignInActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private static Context context;
    private View mProgressView;
    private Client client;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        try {
            setClient(NewSignInActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_sign_in);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Trying send Package to server
                try {
                    String email = mEmailView.getText().toString();
                    email = email.trim();
                    String password = mPasswordView.getText().toString();
                    Package signInPackage = new Package(PackageType.REQ_SIGN_IN, new Login(email), new Pass(password));
                    //Client client = new Client(NewSignInActivity.this);
                    ListenerManager listenerManager = new ListenerManager(client);
                    //client.send(signInPackage);
                    Package tempPackage = listenerManager.listenerManager();
                    if (tempPackage.getType() == PackageType.RESP_SIGN_IN_FAILED) {
                        Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_LONG).show();
                    }
                    if (tempPackage.getType() == PackageType.RESP_SIGN_IN_OK) {
                        Intent intent = new Intent(NewSignInActivity.this, DialogsActivity.class);
                        intent.putExtra("client", client);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        TextView registerScreen = (TextView) findViewById(R.id.link_to_sign_up);

        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void setClient(Context context) throws Exception {
        if (client == null) {
            client = new Client(context);
        }
    }


//    private void populateAutoComplete() {
//        getLoaderManager().initLoader(0, null, this);
//    }


}