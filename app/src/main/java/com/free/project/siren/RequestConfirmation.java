package com.free.project.siren;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestConfirmation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String latitude;
    private String lngtude;
    private GoogleMap mMap;
    private DatabaseReference customerDatabaseRef;
    private FirebaseAuth mAuth;
    private String requestID;
    private Double lat,lng;
    private LatLng customerPickUpLocation;
   // private String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation);

/*
        //spinner color to white :(
        Spinner coloredSpinner = findViewById(R.id.incedent_type);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Incident_Type_Items,
                R.layout.color_spinner_layout
        );
       // adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        coloredSpinner.setAdapter(adapter);
        coloredSpinner.setOnItemSelectedListener(this);*/



        customerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        requestID = database.getReference("Customers Requests").push().getKey();
        final AppCompatTextView streetNameTextView = findViewById(R.id.street_name);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_confirmation);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

                try {
            /*
            Double lat = Double.parseDouble(latitude);
            Double lng = Double.parseDouble(lngtude);
            Double lng = getIntent().getDoubleExtra("PickUpLngtude", Double.parseDouble(null));
*/
                    latitude = getIntent().getStringExtra("PickUpLatitude");
                    lngtude = getIntent().getStringExtra("PickUpLngtude");

                     lat = Double.parseDouble(latitude);
                     lng = Double.parseDouble(lngtude);

                    streetNameTextView.setText("Lat: "+latitude +"\n" + "Lng: "+lngtude);

                    moveCamera(new LatLng(Double.parseDouble(latitude), Double.parseDouble(lngtude)), 15f, "Pick Up Point");



                }catch (NullPointerException ignored){
                    return;
                }



            }
        });


        AppCompatTextView cancelBtn = findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        AppCompatImageButton confirmBtn = findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //info = "An incident of type ("+findViewById(R.id.incedent_type)+"), the severity of the incident ("+findViewById(R.id.severity_of_the_incedent)+"), and the expected number of injuries are("+findViewById(R.id.expectednumberofinjuries)+").";
                if (latitude!=null) {

                    GeoFire geoFire = new GeoFire(customerDatabaseRef);
                    geoFire.setLocation(requestID, new GeoLocation(lat,lng));

                    customerPickUpLocation = new LatLng(lat,lng);
                    //customerDatabaseRef.setValue(info);
                    //customerDatabaseRef.setValue(lat);
                    //customerDatabaseRef.setValue(lng);




                    Toast.makeText(RequestConfirmation.this, "Request Confirmed!", Toast.LENGTH_SHORT).show();
                  //  moveCamera(new LatLng(Double.parseDouble(latitude), Double.parseDouble(lngtude)), 15f, "Pick Up Point");
                }

            }
        });


    }


    private void moveCamera(LatLng latLng, float zoom, String title) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
