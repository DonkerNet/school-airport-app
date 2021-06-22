package com.wdonker.airport;

public enum AirportType {
    OTHER(0),
    CLOSED(1),
    SMALL_AIRPORT(2),
    MEDIUM_AIRPORT(3),
    LARGE_AIRPORT(4),
    HELIPORT(5);

    private int value;

    AirportType(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static AirportType fromInt(int value){
        switch (value){
            case 1:
                return CLOSED;
            case 2:
                return SMALL_AIRPORT;
            case 3:
                return MEDIUM_AIRPORT;
            case 4:
                return LARGE_AIRPORT;
            case 5:
                return HELIPORT;
            default:
                return OTHER;
        }
    }

    public static AirportType parse(String text){
        switch (text.toLowerCase()){
            case "closed":
                return CLOSED;
            case "small_airport":
                return SMALL_AIRPORT;
            case "medium_airport":
                return MEDIUM_AIRPORT;
            case "large_airport":
                return LARGE_AIRPORT;
            case "heliport":
                return HELIPORT;
            default:
                return OTHER;
        }
    }

    @Override
    public String toString(){
        switch (this.value){
            case 1:
                return "Closed";
            case 2:
                return "Small airport";
            case 3:
                return "Medium airport";
            case 4:
                return "Large airport";
            case 5:
                return "Heliport";
            default:
                return "Other";
        }
    }
}
