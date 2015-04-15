package com.example.wechatsample;

import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by wg on 2015/4/15.
 */
public class RequestEntity extends Object {
    @Override
    public String toString() {
        return "{" +
            "mAddrName:\"" + mAddrName + "\"," +
            "mDescription:\"" + mDescription + "\"," +
            "mOwnerName:\"" + mOwnerName + "\"," +
            "mOwnerId:" +  mOwnerId + "," +
            "mInstanceId:" + mInstanceId + "," +
            "mLatitude:" + mLatitude + "," +
            "mLongitude:" + mLongitude + "}";
    }

    @Override
    public int hashCode() {
        HashSet ll = new HashSet();
        ll.add(mAddrName);
        ll.add(mDescription);
        ll.add(mOwnerName);
        ll.add(mOwnerId);
        ll.add(mInstanceId);
        ll.add(mLatitude);
        ll.add(mLongitude);
        return ll.hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return equals((RequestEntity)o);
    }
    public boolean equals(RequestEntity o)
    {
        if(o.mAddrName == this.mAddrName &&
                o.mDescription == this.mDescription &&
                o.mInstanceId == this.mInstanceId &&
                o.mLatitude == this.mLatitude &&
                o.mLongitude == this.mLongitude)
        {
            return true;
        }
        return false;
    }

    public RequestEntity() {
        super();
    }

    private String mAddrName;

    public String getmAddrName() {
        return mAddrName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmOwnerName() {
        return mOwnerName;
    }

    public Integer getmOwnerId() {
        return mOwnerId;
    }

    public Integer getmInstanceId() {
        return mInstanceId;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public Drawable getmPic() {
        return mPic;
    }

    private String mDescription;
    private String mOwnerName;
    private Integer mOwnerId;
    private Integer mInstanceId;
    private double mLatitude;
    private double mLongitude;
    private Drawable mPic;
    public RequestEntity(String addrName,String description,String ownerName,Integer ownerId,Integer instanceId,double latitude,double longitude,Drawable pic)
    {
        super();
        mAddrName = addrName;
        mDescription = description;
        mOwnerName = ownerName;
        mOwnerId = ownerId;
        mInstanceId = instanceId;
        mLatitude = latitude;
        mLongitude = longitude;
        mPic = pic;
    }
}
