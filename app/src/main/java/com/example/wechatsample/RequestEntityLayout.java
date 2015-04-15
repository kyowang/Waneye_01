package com.example.wechatsample;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by wg on 2015/4/15.
 */
public class RequestEntityLayout extends LinearLayout {
    public RequestEntity getmRequestEntity() {
        return mRequestEntity;
    }

    private RequestEntity mRequestEntity;
    @Override
    public int getBaseline() {
        return super.getBaseline();
    }

    public RequestEntityLayout(Context context) {
        super(context);
    }

    public RequestEntityLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RequestEntityLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setEntity(RequestEntity re)
    {
        this.mRequestEntity = re;
    }
}
