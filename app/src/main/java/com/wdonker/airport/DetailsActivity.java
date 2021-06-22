package com.wdonker.airport;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView identifierValueTextView;
    private TextView typeValueTextView;
    private TextView countryValueTextView;
    private TextView nameValueTextView;
    private TextView latitudeValueTextView;
    private TextView longitudeValueTextView;
    private GoogleMap mMap;

    private int airportId;
    private AirportDataSource airportDataSource;
    private Airport airport;
    private Airport schiphol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.identifierValueTextView = (TextView)findViewById(R.id.identifierValueTextView);
        this.typeValueTextView = (TextView)findViewById(R.id.typeValueTextView);
        this.countryValueTextView = (TextView)findViewById(R.id.countryValueTextView);
        this.nameValueTextView = (TextView)findViewById(R.id.nameValueTextView);
        this.latitudeValueTextView = (TextView)findViewById(R.id.latitudeValueTextView);
        this.longitudeValueTextView = (TextView)findViewById(R.id.longitudeValueTextView);

        Intent intent = getIntent();

        this.airportId = intent.getIntExtra("airportId", 0);

        this.airportDataSource = new AirportDataSource(this);
        loadAirports();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.airportMap);
        mapFragment.getMapAsync(this);
    }

    private void loadAirports(){
        this.airportDataSource.open();
        this.airport = this.airportDataSource.getAirportById(this.airportId);
        this.schiphol = this.airportDataSource.getAirportByIdentifier("EHAM");
        this.airportDataSource.close();

        this.identifierValueTextView.setText(this.airport.getIdentifier());
        this.typeValueTextView.setText(this.airport.getType().toString());
        this.countryValueTextView.setText(this.airport.getCountry());
        this.nameValueTextView.setText(this.airport.getName());
        this.latitudeValueTextView.setText(Double.toString(this.airport.getLatitude()));
        this.longitudeValueTextView.setText(Double.toString(this.airport.getLongitude()));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location = new LatLng(this.airport.getLatitude(), this.airport.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title(this.airport.getName()));

        // If the selected airport is Schiphol, we don't need to show a great circle
        if (this.airportId == this.schiphol.getId()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
        // Otherwise, show the distance as a great circle
        else{
            LatLng schipholLocation = new LatLng(this.schiphol.getLatitude(), this.schiphol.getLongitude());

            mMap.addPolyline(new PolylineOptions()
                    .add(schipholLocation, location)
                    .width(5)
                    .color(Color.RED)
                    .geodesic(true));

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.builder()
                    .include(schipholLocation)
                    .include(location)
                    .build(), 40));
        }
    }
}
