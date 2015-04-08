package com.example.wechatsample;


import java.net.URLEncoder;

/**
 * Created by wg on 2015/4/8.
 */
public class WanEyeUtil {
    private final String server_address = "112.126.81.167";
    private final String server_port = "10086";
    private final String register_uri = "/services/api/user.json";
    private final String login_uri = "/j_security_check";
    private final String postLocation_uri = "/services/api/pos.json";
    private final String postStarEyeInstance_uri = "/services/api/stareye.json";
    private final String getInstance_uri = "/services/api/stareye.json";
    private final String getChart_prefix = "/services/api/stareye/";
    private final String getChart_postfix = "/chart.json";

    // /services/api/stareye/${id}/pic.json or /services/api/stareye/${id}/${picId}.json
    private final String postStareyePic_uri_prefix="/services/api/stareye/";
    private final String postStareyePic_uri_pic = "pic";
    private final String postStareyePic_uri_midfix="/";
    private final String postStareyePic_uri_postfix=".json";

    private final String register_url = "http://" + this.server_address + ":" + this.server_port + this.register_uri;
    private final String login_url = "http://" + this.server_address + ":" + this.server_port + this.login_uri;
    private final String postLocation_url = "http://" + this.server_address + ":" + this.server_port + this.postLocation_uri;
    private final String postStarEyeInstance_url = "http://" + this.server_address + ":" + this.server_port + this.postStarEyeInstance_uri;
    private final String getInstance_url = "http://" + this.server_address + ":" + this.server_port + this.getInstance_uri;

    public String encUrlNoParameter(String url)
    {
        String result = "";
        try{
            result =  URLEncoder.encode(url,"UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    public String registerUrl()
    {
        return encUrlNoParameter("http://" + this.server_address + ":" + this.server_port + this.register_uri);
    }
    public String loginUrl()
    {
        return encUrlNoParameter("http://" + this.server_address + ":" + this.server_port + this.register_uri);
    }
    public String postLocationUrl()
    {
        return encUrlNoParameter("http://" + this.server_address + ":" + this.server_port + this.postLocation_uri);
    }
    public String postStarEyeInstanceUrl()
    {
        return encUrlNoParameter("http://" + this.server_address + ":" + this.server_port + this.postStarEyeInstance_uri);
    }
    public String getInstanceUrl()
    {
        return encUrlNoParameter("http://" + this.server_address + ":" + this.server_port + this.getInstance_uri);
    }
}
