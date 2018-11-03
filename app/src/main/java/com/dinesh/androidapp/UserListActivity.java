package com.dinesh.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.dinesh.androidapp.adaptor.UserListAdapter;
import com.dinesh.androidapp.model.Users;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BaseActivity {
    DynamoDBMapper dynamoDBMapper;
    @BindView(R.id.userList)
    ListView userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        init(this);

    }

    private void init() {
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
                getData();

            }
        }).execute();
    }

    void getData() {
        showProgress("Connecting", "Fetching User Data");
        new Thread(new Runnable() {
            @Override
            public void run() {

                PaginatedList<Users> users = dynamoDBMapper.scan(Users.class, new DynamoDBScanExpression());

                final UserListAdapter adapter = new UserListAdapter(UserListActivity.this, users);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList.setAdapter(adapter);
                        dismiss();
                    }
                });

            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, HomeActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("User List");
        init();
    }
}
