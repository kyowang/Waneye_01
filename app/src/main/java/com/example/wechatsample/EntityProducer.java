package com.example.wechatsample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wg on 2015/4/15.
 */
public class EntityProducer {
    RequestEntity mRequestEntity;
    Context mContext;
    public EntityProducer(Context con, RequestEntity re)
    {
        this.mContext = con;
        this.mRequestEntity = re;
    }
    public View generateViewByInstances()
    {
        RequestEntity re = mRequestEntity;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, dm);
        params.setMargins(margin, margin, margin, margin);
        RequestEntityLayout ll = new RequestEntityLayout(mContext);
        ll.setEntity(re);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(params);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestEntityLayout xx = (RequestEntityLayout)v;
                RequestEntity re = xx.getmRequestEntity();
                Bundle bd = new Bundle();
                bd.putCharSequence("description", re.getmDescription());
                bd.putCharSequence("username",re.getmOwnerName());
                bd.putInt("instanceId",re.getmInstanceId());
                bd.putDouble("latitude", re.getmLatitude());
                bd.putDouble("longitude",re.getmLongitude());
                Intent intent = new Intent(mContext,StarEyeDetailActivity.class);
                intent.putExtras(bd);
                mContext.startActivity(intent);
            }
        });
        //Image
        ImageView iv = new ImageView(mContext);

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, dm);
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, dm);
        iv.setLayoutParams(new FrameLayout.LayoutParams(width,height));
        iv.setImageResource(R.drawable.place_holder);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ll.addView(iv);

        //inner Linear Layout
        LinearLayout innerLL = new LinearLayout(mContext);
        innerLL.setOrientation(LinearLayout.VERTICAL);
        innerLL.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        //address name
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //tv.setText(re.getmAddrName());
        tv.setText("Lat:" + re.getmLatitude() +", Long:" + re.getmLongitude());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        //description
        TextView tvDesc = new TextView(mContext);
        tvDesc.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tvDesc.setText(re.getmDescription());
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        tvDesc.setTextColor(Color.parseColor("#ff757575"));

        //username
        TextView tvName = new TextView(mContext);
        tvName.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tvName.setText(re.getmOwnerName());
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        tvName.setTextColor(Color.parseColor("#ff757575"));
        tvName.setGravity(Gravity.RIGHT);

        innerLL.addView(tv);
        innerLL.addView(tvDesc);
        innerLL.addView(tvName);
        ll.addView(innerLL);
        return ll;
    }
}
