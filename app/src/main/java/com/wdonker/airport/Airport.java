package com.wdonker.airport;

public class Airport {
    private int id;
    private String identifier;
    private AirportType type;
    private String country;
    private String name;
    private double latitude;
    private double longitude;

    public Airport(int id, String identifier, AirportType type, String country, String name, double latitude, double longitude){
        this.id = id >= 0 ? id : 0;
        this.identifier = identifier != null ? identifier : "";
        this.type = type != null ? type : AirportType.OTHER;
        this.country = country != null ? country : "";
        this.name = name != null ? name : "";
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId(){
        return this.id;
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public AirportType getType(){
        return this.type;
    }

    public String getCountry(){
        return this.country;
    }

    public String getName(){
        return this.name;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
}
