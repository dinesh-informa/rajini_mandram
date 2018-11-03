package com.dinesh.androidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.dinesh.androidapp.model.Users;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataActivity extends BaseActivity implements IPickResult {

    @BindView(R.id.imgUserPic)
    ImageView imgUserPic;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtPhoneNo)
    EditText txtPhoneNo;
    @BindView(R.id.txtAddress)
    EditText txtAddress;

    DynamoDBMapper dynamoDBMapper;
    String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        locality = b.getString("LOCALITY");
        txtAddress.setText(b.getString("ADDRESS"));
        init(this);
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {

            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {

                // Add code to instantiate a AmazonDynamoDBClient
                AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
                dynamoDBMapper = DynamoDBMapper.builder()
                        .dynamoDBClient(dynamoDBClient)
                        .awsConfiguration(
                                AWSMobileClient.getInstance().getConfiguration())
                        .build();

            }
        }).execute();

    }

    @OnClick(R.id.imgUserPic)
    public void onImgUserPicClicked() {
        PickImageDialog.build(new PickSetup()).show(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Save User");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (imgUserPic.getTag() == null && (!txtName.getText().toString().trim().isEmpty())
                && (!txtAddress.getText().toString().trim().isEmpty())
                && (!txtPhoneNo.getText().toString().trim().isEmpty())) {
            Toast toat = Toast.makeText(UserDataActivity.this, "All Fields are mandatory", Toast.LENGTH_SHORT);
            toat.setGravity(Gravity.TOP, 0, 0);
            toat.show();
        } else {
            showProgress("Updating", "Uploading and saving data ");
            uploadWithTransferUtility(imgUserPic.getTag() + "");
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveUser() {
        final Users users = new Users();
        users.setAddress(txtAddress.getText().toString());
        users.setName(txtName.getText().toString());
        users.setPhoneNumber(txtPhoneNo.getText().toString());
        users.setLocality(locality);
        users.setImageUrl("https://s3.amazonaws.com/app-user-pic/" + users.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        reset();
        onBackPressed();
    }

    private void reset() {
        txtAddress.setText("");
        txtName.setText("");
        txtPhoneNo.setText("");
        imgUserPic.setImageResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            imgUserPic.setImageBitmap(r.getBitmap());
            imgUserPic.setTag(r.getPath());
        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadWithTransferUtility(String uri) {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))

                        .build();

        final TransferObserver uploadObserver =
                transferUtility.upload(
                        txtName.getText().toString(),
                        new File(uri));

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED == state) {
                    dismiss();


                    showProgress("Saving", "Saving data ");
                    saveUser();

                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                setProgressTitle("Uploading " + percentDone+ "%");
                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            System.out.println(uploadObserver.getAbsoluteFilePath());

        }

        Log.d("YourActivity", "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }
}
