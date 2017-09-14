package com.bytelicious.barlocator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bytelicious.barlocator.base.BarFragment;
import com.bytelicious.barlocator.dagger.DI;
import com.bytelicious.barlocator.list.BarListFragment;
import com.bytelicious.barlocator.managers.BarLocationManager;
import com.bytelicious.barlocator.map.BarMapFragment;
import com.bytelicious.barlocator.model.Bar;
import com.bytelicious.barlocator.model.BarsResponse;
import com.bytelicious.barlocator.networking.API;
import com.google.android.gms.common.ConnectionResult;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bytelicious.barlocator.BarPagerAdapter.LIST;
import static com.bytelicious.barlocator.BarPagerAdapter.MAP;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED;

public class MainActivity extends AppCompatActivity implements BarLocationManager.ConnectionListener,
        BarListFragment.OnBarSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int LOCATION_PERMISSION_REQUEST = 99;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
    private static final int DEFAULT_RADIUS = 500;

    private ViewPager viewPager;
    private BarPagerAdapter barPagerAdapter;

    @Inject
    API api;
    @Inject
    BarLocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DI.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        setSupportActionBar(toolbar);
        barPagerAdapter = new BarPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(barPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.setConnectionListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationAllowed()) {
                locationManager.requestLocation();
            } else {
                checkLocationPermission();
            }
        } else {
            locationManager.requestLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.setConnectionListener(null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationAllowed()) {
                        locationManager.requestLocation();
                    }
                } else {
                    //TODO disable functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO
        switch (i) {
            case CAUSE_NETWORK_LOST:

                break;

            case CAUSE_SERVICE_DISCONNECTED:

                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (isLocationAllowed()) {
            locationManager.requestLocation();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        api.getBars(location.getLatitude() + "," + location.getLongitude(), DEFAULT_RADIUS,
                getString(R.string.web_api_key))
                .enqueue(new Callback<BarsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BarsResponse> call,
                                           @NonNull Response<BarsResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getBars() != null) {
                                setNewBars(response.body().getBars(), location);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.error_could_not_load_bars,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<BarsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(MainActivity.this, R.string.error_could_not_load_bars,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("We cannot locate bars nearby without your current location.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    private boolean isLocationAllowed() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setNewBars(ArrayList<Bar> bars, Location location) {
        Fragment f = getSupportFragmentManager().getFragments().get(LIST);
        // After screen rotation new instance of FragmentPagerAdapter is created.
        // But the ViewPager restores its state and state of all fragments it contains.
        // ViewPager doesn't call adapters getView() method.
        if (f instanceof BarFragment) {
            ((BarFragment) f).setBars(bars, location);
        }
    }

    @Override
    public void onBarSelectedListener(Bar bar) {
        viewPager.setCurrentItem(MAP);
        Fragment f = getSupportFragmentManager().getFragments().get(MAP);
        if(f instanceof BarMapFragment) {
            ((BarMapFragment)f).onBarSelected(bar);
        }
    }
}