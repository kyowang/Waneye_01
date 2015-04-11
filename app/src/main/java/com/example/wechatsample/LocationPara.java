package com.example.wechatsample;

/**
 * Created by kyo on 2015/4/11.
 */
public class LocationPara
{
    private Double latitude;
    private Double longitude;
    private Double altitude;
    public static final String contentType = "application/json";

    public LocationPara(Double latitude, Double longitude)
    {
        LocationPara.this.latitude = latitude;
        LocationPara.this.longitude = longitude;
        LocationPara.this.altitude = 0.000000;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"latitude\":");
        sb.append(this.latitude.toString());
        sb.append(",\"longitude\":");
        sb.append(this.longitude.toString());
        sb.append(",\"altitude\":");
        sb.append(this.altitude.toString());

        sb.append("}");
        return sb.toString();
    }
}
