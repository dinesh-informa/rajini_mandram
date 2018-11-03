package com.dinesh.androidapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

public class AuthenticatorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Add a call to initialize AWSMobileClient
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(
                        AuthenticatorActivity.this,
                        SignInUI.class);

                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)  // true? show the Email and Password UI
                                // Show Google button
                                .logoResId(R.mipmap.rajini_logo) // Change the logo
                                .backgroundColor(Color.LTGRAY) // Change the backgroundColor
                                .isBackgroundColorFullScreen(false) // Full screen backgroundColor the backgroundColor full screenff
                                .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                                .canCancel(true)
                                .build();
                signin.login(
                        AuthenticatorActivity.this,
                        UserListActivity.class).authUIConfiguration(config).execute();
            }
        }).execute();
    }


}