package com.dinesh.androidapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {
    @BindView(R.id.txtCompleteAddress)
    EditText txtCompleteAddress;
    @BindView(R.id.txtLocality)
    EditText txtLocality;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("User Location");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


        Dexter.withActivity(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            mFusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                txtCompleteAddress.setText(getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                                                System.out.println("Hekki " + getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                                                // Logic to handle location object

                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
// Define a listener that responds to location updates
//            startActivity(new Intent(this,UserDataActivity.class));

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                txtLocality.setText(returnedAddress.getSubLocality());
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My addr", strReturnedAddress.toString());
            } else {
                Log.w("My address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle=new Bundle();
        bundle.putString("LOCALITY",txtLocality.getText().toString());
        bundle.putString("ADDRESS",txtCompleteAddress.getText().toString());
        Intent intent=new Intent(HomeActivity.this,UserDataActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }


}
