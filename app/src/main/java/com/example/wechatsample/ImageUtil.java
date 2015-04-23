package com.example.wechatsample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kyo on 2015/4/21.
 */
public class ImageUtil {
    public static String generateFileName()
    {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String picName = "/waneye/capturedPicture_" + str;
        Log.d("generateFileName", picName);
        return picName;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    public static Bitmap decodeFile(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        try {
            BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(
                    options, true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (filePath != null) {
            bitmap = BitmapFactory.decodeFile(filePath, options);
        }
        return bitmap;
    }
    public static Bitmap ReadBitMap(Context context, int resId){

        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        //获取资源图片

        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is,null,opt);

    }

    public static Bitmap decodeSampledBitmapFromResource(
            Resources res,
            int resId,
             int reqWidth,
             int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static Bitmap decodeSampleBitmapFromStream(InputStream in, int sampleSize) throws IOException
    {
        BufferedInputStream bin = new BufferedInputStream(in, 8 * 1024);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(bin, null,options);
        //Log.d("Debug: width = ", options.outWidth + " height= " + options.outHeight);
        // Calculate inSampleSize
        options.inSampleSize = sampleSize;//calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d("decodeStream",options.inSampleSize + "");
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //bin.close();
        return BitmapFactory.decodeStream(bin, null, options);
    }
    public static int decodeSizeOnlyFromStream(InputStream in , int reqWidth, int reqHeight)
    {
        int sampleSize = 1;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null,options);
        //Log.d("Debug: width = ", options.outWidth + " height= " + options.outHeight);
        //Calculate inSampleSize
        sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        return sampleSize;
    }
    public static Bitmap decodeSampledBitmapFromUri(
            Context context,
            Uri uri,
            int reqWidth,
            int reqHeight)throws FileNotFoundException
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri),null,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri),null,options);
    }
}
