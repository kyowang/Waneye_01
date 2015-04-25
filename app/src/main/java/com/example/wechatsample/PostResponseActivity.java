package com.example.wechatsample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostResponseActivity extends Activity {
    private ImageButton mImageButton;
    private LinearLayout mLLContainer;
    private EditText mETPostWords;
    private Button mButtonSubmit;
    ArrayList<Uri> mImageUris;
    private int mInstanceId = -1;
    private String data = "";
    private Uri uriCaptureImage = null;

    private final static int CHOSE_ALBUM = 0;
    private final static int CHOSE_CAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_response);
        Intent intent = getIntent();
        Bundle bd =  intent.getExtras();
        if(null == bd)
        {
            Toast.makeText(getApplicationContext(), "没有找到关联问题，本页无效", Toast.LENGTH_LONG).show();
        }
        mInstanceId = bd.getInt("instanceId");
        mETPostWords = (EditText)findViewById(R.id.et_post_words);
        mButtonSubmit = (Button) findViewById(R.id.bt_request_submit);
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCommonData comData = new AppCommonData(PostResponseActivity.this);
                String words = mETPostWords.getText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append("{\"");
                sb.append("latitude\":");
                sb.append(comData.getString("my_latitude",""));
                sb.append(",");
                sb.append("\"longitude\":");
                sb.append(comData.getString("my_longitude",""));
                sb.append(",");
                sb.append("\"description\":\"");
                sb.append(words);
                sb.append("\"}");
                data = sb.toString();
                if(mImageUris.size() <= 0)
                {
                    Toast.makeText(getApplicationContext(), "没有发现图片，请先添加", Toast.LENGTH_LONG).show();
                    return;
                }
                doSendImages();
            }
        });

        mImageButton = (ImageButton) findViewById(R.id.ib_add_image);
        mLLContainer = (LinearLayout) findViewById(R.id.ll_images);
        mImageUris = new ArrayList<Uri>();
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onCreateDialog();
                MyDialogFragment dialog = MyDialogFragment.newInstance();
                dialog.show(getFragmentManager(),"dialog");
            }
        });
    }
    public void getImageFromAlbum() {
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, CHOSE_ALBUM);
    }
    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            uriCaptureImage = getOutputMediaFileUri();
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,uriCaptureImage); // set the image file name
            startActivityForResult(getImageByCamera, CHOSE_CAMERA);
        }
        else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(CHOSE_ALBUM == requestCode && resultCode == RESULT_OK) //from local image
        {
            Toast.makeText(getBaseContext(),"result : " + data.getData(),Toast.LENGTH_LONG).show();
            Uri uri = data.getData();
            if (null == uri)
            {
                return;
            }
            addImageToList(data.getData());
        }
        else if(CHOSE_CAMERA == requestCode)
        {
            if(resultCode == RESULT_OK)
            {
                Uri uri = uriCaptureImage;
                if(uri == null){
                    Log.d("onActivityResult","uri null");
                    return;
                }
                else
                {
                    Log.d("onActivityResult","uri OK");
                    addImageToList(uri);
                }
            }
        }
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(){
        return Uri.fromFile(generateFileName());
    }

    public File generateFileName()
    {
        Integer i = 1;
        String sysExtPath = Environment.getExternalStorageDirectory().getPath();
        //Log.d("generateFileName:",sysExtPath);
        String sysPicPath = Environment.DIRECTORY_DCIM + File.separator + "waneye";
        //Log.d("generateFileName:",sysPicPath);
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String dir = sysExtPath + File.separator + sysPicPath;
        String picName =  dir + "/Picture_" + str + ".JPG";
        File directory = new File(dir);
        if(! directory.exists())
        {
            directory.mkdir();
        }
        while(new File(picName).exists())
        {
            picName =   "/Picture_" + str + i.toString()  + ".JPG";
            i++;
        }
        Log.d("generateFileName",picName);
        return new File(picName);
    }
    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }
    public void addImageToList(Uri uri)
    {
        if(null == uri)
        {
            return;
        }
        DisplayMetrics dm = PostResponseActivity.this.getResources().getDisplayMetrics();
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
        Bitmap d;
        Bitmap saveVersion;
        try
        {
            File file = generateFileName();
            Uri small = Uri.fromFile(file);
            saveVersion = ImageUtil.decodeSampledBitmapFromUri(PostResponseActivity.this,uri,960,960);
            FileOutputStream osFile = new FileOutputStream(file);
            saveVersion.compress(Bitmap.CompressFormat.JPEG,100,osFile);
            osFile.flush();
            osFile.close();
            mImageUris.add(small);
            d = ImageUtil.decodeSampledBitmapFromUri(PostResponseActivity.this,uri,width,height);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        ImageView iv = new ImageView(PostResponseActivity.this);

        LinearLayout.LayoutParams paraIv = new LinearLayout.LayoutParams(width, height);
        paraIv.setMargins(margin, 0, margin, 0);
        iv.setLayoutParams(paraIv);
        iv.setImageBitmap(d);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mLLContainer.addView(iv,0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyDialogFragment extends DialogFragment {
        public MyDialogFragment() {
            //super();
        }
        public static MyDialogFragment newInstance() {
            MyDialogFragment frag = new MyDialogFragment();
            //Bundle args = new Bundle();
            //args.putInt("title", title);
            //frag.setArguments(args);
            return frag;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] items = {"相册选择","新拍照片"};
            builder.setTitle("请选择：")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == 0) {
                                ((PostResponseActivity)getActivity()).getImageFromAlbum();
                            } else//拍照
                            {
                                ((PostResponseActivity)getActivity()).getImageFromCamera();
                                //Toast.makeText(getActivity(), "相册", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    private class doPostPicOneTask extends AsyncTask<InputStream, Void, String> {
        protected String doInBackground(InputStream... iss)
        {
            String result = "";
            String picUrl = "";
            String picId = "";
            if(iss.length <= 0)
            {
                return "";
            }
            if(null == iss[0])
            {
                return "";
            }
            try
            {
                result = WanEyeUtil.doPostPicOne(mInstanceId, data, "application/json");
                Log.d("doPostPicOneTask", result);
                picUrl = getPicUrlFromJson(result);
                picId = getPicIdFromJson(result);
                Log.d("doPostPicOneTask", picUrl);
                Integer status = WanEyeUtil.doPostPicTwo(picUrl,iss[0]);
                if(status == HttpURLConnection.HTTP_OK)
                {
                    status = 0;
                    status = WanEyeUtil.doPostPicThree(mInstanceId,picId);
                    if(status == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("doPostPicOneTask","success");
                        return "OK";
                    }
                    else
                    {
                        return "3";
                    }
                }
                else
                {
                    return "2";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            //mJson = result;
            String picUrl = "";
            if("" == result)
            {
                return;
            }
            else if("OK" == result)
            {
                Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void doSendImages()
    {
        Integer result = -1;
        if(mImageUris.size() <= 0)
        {
            Log.d("doSendImages","mImageUris.size() <= 0");
            return;
        }
        Uri uri = mImageUris.get(0);
        InputStream is = null;
        Log.d("doSendImages",uri.getPath());
        try
        {
            is = PostResponseActivity.this.getContentResolver().openInputStream(uri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        new doPostPicOneTask().execute(is);
    }
    public String getPicUrlFromJson(String json) throws JSONException
    {
        String url = "";
        JSONObject jo = new JSONObject(json);
        url = jo.getString("pictureUrl");
        return url;
    }
    public String getPicIdFromJson(String json) throws JSONException
    {
        String url = "";
        JSONObject jo = new JSONObject(json);
        url = jo.getString("id");
        return url;
    }
}
