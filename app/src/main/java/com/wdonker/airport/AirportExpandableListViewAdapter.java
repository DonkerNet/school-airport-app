package com.wdonker.airport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AirportExpandableListViewAdapter extends BaseExpandableListAdapter implements Filterable, SectionIndexer {

    private Context context;
    private List<Airport> sortedAirports;

    private List<String> sections;
    private List<String> headers;
    private HashMap<String, List<Airport>> itemGroups;

    public AirportExpandableListViewAdapter(Context context, List<Airport> airports){
        this.context = context;

        // Add the airports to a new list that we can sort by country and name
        this.sortedAirports = new ArrayList<>(airports);
        Collections.sort(this.sortedAirports, new Comparator<Airport>() {
            @Override
            public int compare(Airport lhs, Airport rhs) {
                int value = lhs.getCountry().compareToIgnoreCase(rhs.getCountry());
                if (value == 0)
                    value = lhs.getName().compareToIgnoreCase(rhs.getName());
                return value;
            }
        });

        loadAirports(this.sortedAirports);
    }

    // Create item groups and extract the headers
    private void loadAirports(List<Airport> airports){
        ArrayList<String> newSections = new ArrayList<>();
        ArrayList<String> newHeaders = new ArrayList<>();
        HashMap<String, List<Airport>> newItemGroups = new HashMap<>();

        for (int i = 0; i < airports.size(); i++){
            Airport airport = airports.get(i);

            String country = airport.getCountry();

            List<Airport> airportGroup;

            if (newItemGroups.containsKey(country)){
                airportGroup = newItemGroups.get(country);
                airportGroup.add(airport);
            }
            else{
                String section = country.substring(0, 1).toUpperCase();
                if (!newSections.contains(section))
                    newSections.add(section);

                newHeaders.add(country);

                airportGroup = new ArrayList<>();
                airportGroup.add(airport);
                newItemGroups.put(country, airportGroup);
            }
        }

        this.sections = newSections;
        this.headers = newHeaders;
        this.itemGroups = newItemGroups;
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.itemGroups.get(this.headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.itemGroups.get(this.headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return this.itemGroups.get(this.headers.get(groupPosition)).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_group, null);
        }

        TextView groupTextView = (TextView)convertView.findViewById(R.id.groupTextView);
        groupTextView.setText(this.headers.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater =(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row, null);
        }

        TextView rowTextView = (TextView)convertView.findViewById(R.id.rowTextView);
        rowTextView.setText(this.itemGroups.get(this.headers.get(groupPosition)).get(childPosition).getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                boolean shouldFilter = constraint != null && constraint.length() > 0;

                List<Airport> airports = AirportExpandableListViewAdapter.this.sortedAirports;

                if (shouldFilter){
                    String[] nameFilters = constraint.toString().toLowerCase().split(" ");
                    ArrayList<Airport> filteredAirports = new ArrayList<>();

                    for (int i = 0; i < airports.size(); i++){
                        Airport airport = airports.get(i);

                        boolean isMatch = false;

                        for (String nameFilter : nameFilters) {
                            if (nameFilter.length() == 0)
                                continue;

                            isMatch = airport.getCountry().toLowerCase().contains(nameFilter)
                                    || airport.getName().toLowerCase().contains(nameFilter);

                            if (!isMatch)
                                break;
                        }

                        if (isMatch)
                            filteredAirports.add(airport);
                    }

                    airports = filteredAirports;
                }

                results.values = airports;
                results.count = airports.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                loadAirports((List<Airport>)results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Object[] getSections() {
        return sections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        sectionIndex = clampInt(sectionIndex, 0, this.sections.size() - 1);
        String section = this.sections.get(sectionIndex);

        int position = 0;

        for (String header : this.headers){
            String headerSection = header.substring(0, 1).toUpperCase();
            if (section.equals(headerSection))
                return position;

            ++position;
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        position = clampInt(position, 0, this.headers.size() - 1);
        String header = this.headers.get(position);
        String section = header.substring(0, 1).toUpperCase();
        return this.sections.indexOf(section);
    }

    // Used for clamping the position or section index
    // Example from official docs: "If the section's starting position is outside of the adapter bounds, the position must be clipped to fall within the size of the adapter."
    private int clampInt(int value, int min, int max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
}
