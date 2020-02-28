package com.example.testlocation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient client;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<String> permissionsToRequests;
    private ArrayList<String> permisssions = new ArrayList<>();
    private ArrayList<String> permissionRejected=new ArrayList<>();
    private TextView locationTextView;
    private LocationRequest locationrequest;
    public static final long UPDATE_INTERVAL=5000;
    public static final long FASTEST_INTERVAL=5000;
    private static final int ALL_PERMISSON_RESULT=1111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationTextView=findViewById(R.id.location_text_view);
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(MainActivity.this);
        permisssions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permisssions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequests= permissonToRequest(permisssions);

        if(SDK_INT>=Build.VERSION_CODES.M)
        {
            if(permissionsToRequests.size()>0)
            {
                requestPermissions(permissionsToRequests.toArray(new String[permissionsToRequests.size()]),ALL_PERMISSON_RESULT);

            }
        }
        client= new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                      .addApi(LocationServices.API).build();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private ArrayList<String> permissonToRequest(ArrayList<String> wantedPermisssions) {
        ArrayList<String> result=new ArrayList<>();
        for(String perm : wantedPermisssions)
        {
         if(!hasPermissions(perm))
         {
             result.add(perm);
         }
        }
        return result;
    }

    private boolean hasPermissions(String perm) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return checkSelfPermission(perm)== PackageManager.PERMISSION_GRANTED;

        }
        return true;
    }

    private  void checkPlayservices()
    {
        int errorcode= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(errorcode!= ConnectionResult.SUCCESS)
        {
            Dialog errordialog=GoogleApiAvailability.getInstance().getErrorDialog(this, errorcode, errorcode, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this,"No Services",Toast.LENGTH_SHORT).show();


                }
            });
            errordialog.show();
        }
        else{
            Toast.makeText(MainActivity.this,"All Is good",Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(client!=null)
        {

            client.connect();
            Toast.makeText(MainActivity.this,"LOCATION TESTED",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        client.disconnect();
        Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkPlayservices();
        Toast.makeText(MainActivity.this,"Still connected ",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(client!=null && client.isConnected())
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(new LocationCallback(){});
            client.disconnect();
            Toast.makeText(MainActivity.this,"disconnected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
        {
            locationTextView.setText(MessageFormat.format("Lat: {0} Lon: {1}", location.getLatitude(), location.getLongitude()));
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
        &&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
            locationTextView.setText(MessageFormat.format("Lat: {0} Lon: {1}", location.getLatitude(), location.getLongitude()));
            }
        });
        
        startLocationUpdate();

    }

    private void startLocationUpdate() {
        locationrequest = new LocationRequest();
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationrequest.setInterval(UPDATE_INTERVAL);
        locationrequest.setFastestInterval(FASTEST_INTERVAL);
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(MainActivity.this,"You Have To Grant Permisson To Display Location",Toast.LENGTH_SHORT)
                    .show();

        }

        LocationServices.getFusedLocationProviderClient(MainActivity.this).
                requestLocationUpdates(locationrequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if(locationResult!=null)
                        {
                            Location location=locationResult.getLastLocation();
                            locationTextView.setText(MessageFormat.format("Lat: {0} Lon: {1}", location.getLatitude(), location.getLongitude()));

                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                },null);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case ALL_PERMISSON_RESULT:
                for(String perm : permissionsToRequests)
                {
                    if(!hasPermissions(perm))
                    {
                        permissionRejected.add(perm);
                    }
                }
                if(permissionRejected.size()>0)
                {
                    if(SDK_INT>=Build.VERSION_CODES.M)
                    {
                        if(shouldShowRequestPermissionRationale(permissionRejected.get(0)))
                        {
                            new AlertDialog.Builder(MainActivity.this).setMessage("THESE PERMISSION ARE MANDATORY" +
                                    "TO GET LOACATION").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(SDK_INT>=Build.VERSION_CODES.M)
                                    {
                                        requestPermissions(permissionRejected.toArray(new String[permissionRejected.size()])
                                        ,ALL_PERMISSON_RESULT);
                                    }
                                }
                            }).setNegativeButton("CANCEL",null).create().show();
                        }
                    }

                }
                else
                {
                    if(client!=null)
                    {
                        client.connect();
                    }
                }
                break;

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
