package com.example.fittracker;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fittracker.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private static final String TAG = "FitActivity";
    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFrameWithFragment(new HomeFragment()); // default is home screen

        connectFitness();

        clickBottomMenuListener();

       /* binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.idHome:
                    replaceFrameWithFragment(new HomeFragment());
                    break;
                case R.id.idProfile:
                    replaceFrameWithFragment(new ProfileFragment());
                    break;
                case R.id.idSettings:
                    replaceFrameWithFragment(new SetingsFragment());
                    break;
            }

            return true;
        }); */

    }


    @Override
    protected void onResume() {
        super.onResume();
        connectFitness();
    }

    private void clickBottomMenuListener() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.idHome:
                    replaceFrameWithFragment(new HomeFragment());
                    break;
                case R.id.idProfile:
                    replaceFrameWithFragment(new ProfileFragment());
                    break;
                case R.id.idSettings:
                    replaceFrameWithFragment(new SetingsFragment());
                    break;
            }

            return true;
        });
    }

    private void connectFitness() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 100);
        }

        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    // .addScope(new Scope(Scopes.)) // GET STEP VALUES
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                                @Override
                                                public void onConnected(Bundle bundle) {
                                                    Log.e(TAG, "Connected!!!");
                                                    // Now you can make calls to the Fitness APIs.
                                                    findFitnessDataSources();

                                                }

                                                @Override
                                                public void onConnectionSuspended(int i) {
                                                    // If your connection to the sensor gets lost at some point,
                                                    // you'll be able to determine the reason and react to it here.
                                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                                    } else if (i
                                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                                        Log.i(TAG,
                                                                "Connection lost.  Reason: Service Disconnected");
                                                    }
                                                }
                                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.e(TAG, "!_@@ERROR :: Google Play services connection failed. Cause: " + result.toString());
                        }
                    })
                    .build();
        }

    }

    private void findFitnessDataSources() {
        Fitness.SensorsApi.findDataSources(
                        mClient,
                        new DataSourcesRequest.Builder()
                                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                                .build())
                .setResultCallback(new ResultCallbacks<DataSourcesResult>() {
                    @Override
                    public void onFailure(@NonNull Status status) {

                    }

                    @Override
                    public void onSuccess(@NonNull DataSourcesResult dataSourcesResult) {
                        Log.e(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.e(TAG, "Data source found: " + dataSource.toString());
                            Log.e(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mListener == null) {
                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");

                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                            }
                        }
                    }
                });
    }

    private void registerFitnessDataListener(final DataSource dataSource, DataType dataType) {


        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.e(TAG, "Detected DataPoint field: " + field.getName());
                    Log.e(TAG, "Detected DataPoint value: " + val);

                }
            }
        };


        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mListener).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onFailure(@NonNull Status status) {

            }

            @Override
            public void onSuccess(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Listener registered!");
                } else {
                    Log.i(TAG, "Listener not registered.");
                }
            }
        });

    }

    private void replaceFrameWithFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }
}