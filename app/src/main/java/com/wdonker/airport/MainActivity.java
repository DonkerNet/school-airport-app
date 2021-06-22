package com.wdonker.airport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Filterable;
import android.widget.SearchView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchView airportSearchView;
    private ExpandableListView airportExpandableListView;

    private AirportDataSource airportDataSource;
    private List<Airport> airports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.airportSearchView = (SearchView)findViewById(R.id.airportSearchView);
        this.airportExpandableListView = (ExpandableListView)findViewById(R.id.airportExpandableListView);
        this.airportExpandableListView.setFastScrollEnabled(true);
        this.airportExpandableListView.setFastScrollAlwaysVisible(true);

        this.airportExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("airportId", (int) id);
                startActivity(detailsIntent);
                return true;
            }
        });

        this.airportSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filterable filterable = (Filterable) MainActivity.this.airportExpandableListView.getAdapter();
                filterable.getFilter().filter(newText);
                return false;
            }
        });

        this.airportDataSource = new AirportDataSource(this);
        loadAirports();
    }

    private void loadAirports(){
        this.airportDataSource.open();
        this.airports = this.airportDataSource.getAllAirports();
        this.airportDataSource.close();

        AirportExpandableListViewAdapter airportAdapter = new AirportExpandableListViewAdapter(this, this.airports);
        this.airportExpandableListView.setAdapter(airportAdapter);
    }
}
