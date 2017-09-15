package com.bytelicious.barlocator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bytelicious.barlocator.base.BarFragment;
import com.bytelicious.barlocator.dagger.DI;
import com.bytelicious.barlocator.list.BarListFragment;
import com.bytelicious.barlocator.managers.BarLocationManager;
import com.bytelicious.barlocator.managers.NetworkManager;
import com.bytelicious.barlocator.map.BarMapFragment;
import com.bytelicious.barlocator.model.Bar;
import com.google.android.gms.common.ConnectionResult;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.bytelicious.barlocator.BarPagerAdapter.MAP;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED;

public class MainActivity extends AppCompatActivity implements BarLocationManager.ConnectionListener,
        BarListFragment.OnBarSelectedListener,
        NetworkManager.OnBarsReadyListener, SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener {

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
    private static final String ARG_LOCATION = "MainActivity.location";
    private static final String ARG_RADIUS = "MainActivity.radius";
    private static final String ARG_BARS = "MainActivity.bars";
    public static final int LOCATION_PERMISSION_REQUEST = 99;
    private static final int DEFAULT_RADIUS = 1000;

    private ViewPager viewPager;

    @Inject
    NetworkManager networkManager;
    @Inject
    BarLocationManager locationManager;
    private ArrayList<Bar> bars;
    private Location location;
    private int radius = DEFAULT_RADIUS;
    private TextView distanceTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocation();
                } else {
                    showResolutionSnackbar();
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
            Snackbar.make(viewPager,
                    String.format(getString(R.string.error_google_client_connection),
                            connectionResult.getErrorCode()),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case CAUSE_NETWORK_LOST:
                Snackbar.make(viewPager, R.string.error_network_lost, Snackbar.LENGTH_SHORT).show();
                break;

            case CAUSE_SERVICE_DISCONNECTED:
                Snackbar.make(viewPager, R.string.error_service_disconnected,
                        Snackbar.LENGTH_SHORT).show();
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
    public void onBarsSuccess(ArrayList<Bar> bars, Location location) {
        this.bars = bars;
        this.location = location;
        deliverResultsToFragment(viewPager.getCurrentItem());
    }

    @Override
    public void onBarsFailure() {
        Snackbar.make(viewPager, R.string.error_could_not_load_bars, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(final Location location) {
        networkManager.getBars(location, radius, getString(R.string.web_api_key));
    }

    @Override
    public void onBarSelectedListener(String barId) {
        viewPager.setCurrentItem(MAP);
        Fragment f = getSupportFragmentManager().getFragments().get(MAP);
        if (f instanceof BarMapFragment) {
            ((BarMapFragment) f).onBarSelected(barId);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {
        deliverResultsToFragment(position);
    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        radius = i;
        distanceTextView.setText(String.format(getString(R.string.distance_meters), radius));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        networkManager.getBars(location, radius, getString(R.string.web_api_key));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DI.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViewPager();
        restoreData(savedInstanceState);
        setupDistanceSeekBar();

        if (!isLocationAllowed()) {
            checkLocationPermission();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_BARS, bars);
        outState.putParcelable(ARG_LOCATION, location);
        outState.putInt(ARG_RADIUS, radius);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkManager.setOnBarsReadyListener(this);
        locationManager.setConnectionListener(this);
        if (isLocationAllowed()) {
            locationManager.requestLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkManager.setOnBarsReadyListener(null);
        locationManager.setConnectionListener(null);
    }

    private void checkLocationPermission() {
        if (!isLocationAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_needed_title)
                .setMessage(R.string.permission_needed_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST);
                    }
                })
                .create()
                .show();
    }

    private boolean isLocationAllowed() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.view_pager);
        BarPagerAdapter barPagerAdapter = new BarPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(barPagerAdapter);

        viewPager.addOnPageChangeListener(this);
    }

    private void deliverResultsToFragment(int position) {
        // After screen rotation new instance of FragmentPagerAdapter is created.
        // But the ViewPager restores its state and state of all fragments it contains.
        // ViewPager doesn't call adapters getView() method.
        Fragment f = getSupportFragmentManager().getFragments().get(position);
        if (f instanceof BarFragment) {
            ((BarFragment) f).setBars(bars, location);
        }
    }

    private void setupDistanceSeekBar() {
        distanceTextView = findViewById(R.id.distance_text_view);
        SeekBar seekBar = findViewById(R.id.distance_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(radius);
        distanceTextView.setText(String.format(getString(R.string.distance_meters), radius));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void goToPermissions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showResolutionSnackbar() {
        Snackbar.make(viewPager, R.string.message_enable_location,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToPermissions();
                    }
                }).show();
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            bars = savedInstanceState.getParcelableArrayList(ARG_BARS);
            location = savedInstanceState.getParcelable(ARG_LOCATION);
            radius = savedInstanceState.getInt(ARG_RADIUS);
            deliverResultsToFragment(viewPager.getCurrentItem());
        }
    }

}