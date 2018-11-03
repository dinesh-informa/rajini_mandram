package com.dinesh.androidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(Context context) {
        progressDialog=new ProgressDialog(context);

    }

    public void showProgress(String title,String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismiss(){
        progressDialog.dismiss();
    }

    public void setProgressTitle(String title){
        progressDialog.setTitle(title);
    }
}
