package com.example.wechatsample;

/**
 * Created by kyo on 2015/4/12.
 */
public class StarEyeInstance {
    private Double latitude;
    private Double longitude;
    private Integer status = 1;
    private String description;
    public static final String contentType = "application/json";

    public StarEyeInstance(Double latitude, Double longitude,String desc)
    {
        StarEyeInstance.this.latitude = latitude;
        StarEyeInstance.this.longitude = longitude;
        StarEyeInstance.this.description = desc;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"latitude\":");
        sb.append(this.latitude.toString());
        sb.append(",\"longitude\":");
        sb.append(this.longitude.toString());
        sb.append(",\"status\":");
        sb.append(this.status.toString());
        sb.append(",\"description\":\"");
        sb.append(this.description);
        sb.append("\"}");
        return sb.toString();
    }
}
