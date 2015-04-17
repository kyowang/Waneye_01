package com.example.wechatsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by kyo on 2015/4/17.
 */
public class DetailResponseProducer {
    Context mContext;
    String mUserName;
    String mDescription;
    Bitmap mImage;
    public DetailResponseProducer(Context con, String un,String desc, Bitmap image)
    {
        this.mContext = con;
        this.mUserName = un;
        this.mDescription = desc;
        this.mImage = image;
    }
    public LinearLayout generateViewByInstances() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
        params.setMargins(margin, margin, margin, margin);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(params);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvName = new TextView(mContext);
        tvName.setLayoutParams(params2);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tvName.setText("From:" + mUserName);
        ll.addView(tvName);

        //Image
        ImageView iv = new ImageView(mContext);
        //final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, dm);
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, dm);
        LinearLayout.LayoutParams paraIv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        paraIv.setMargins(0,margin,0,margin);
        iv.setLayoutParams(paraIv);
        iv.setImageBitmap(mImage);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ll.addView(iv);

        TextView tvDesc = new TextView(mContext);
        tvDesc.setLayoutParams(params2);
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tvDesc.setText("留言:" + mDescription);
        ll.addView(tvDesc);

        return ll;
    }
}
