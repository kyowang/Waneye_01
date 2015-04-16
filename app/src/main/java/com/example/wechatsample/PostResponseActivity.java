package com.example.wechatsample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;


public class PostResponseActivity extends Activity {
    private ImageButton mImageButton;
    private LinearLayout mLLContainer;
    ArrayList<Uri> mImageUris;
    ArrayList<Bitmap> mBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_response);
        mImageButton = (ImageButton) findViewById(R.id.ib_add_image);
        mLLContainer = (LinearLayout) findViewById(R.id.ll_images);
        mImageUris = new ArrayList<Uri>();
        mBitmaps = new ArrayList<Bitmap>();
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
        startActivityForResult(intent, 0);
    }
    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, 1);
        }
        else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(0 == requestCode && resultCode == RESULT_OK) //from local image
        {
            Toast.makeText(getBaseContext(),"result : " + data.getData(),Toast.LENGTH_LONG).show();
            Uri uri = data.getData();
            if (null == uri)
            {
                //use bundle to get data
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap  photo = (Bitmap) bundle.get("data"); //get bitmap
                    mBitmaps.add(photo);
                    addBitmapToList(photo);
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "请重新选择图片", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            mImageUris.add(uri);
            addImageToList(data.getData());
        }
        else if(1 == requestCode)
        {
            if(resultCode == RESULT_OK)
            {
                Uri uri = data.getData();
                if(uri == null){
                    //use bundle to get data
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap  photo = (Bitmap) bundle.get("data"); //get bitmap
                        mBitmaps.add(photo);
                    } else {
                        Toast.makeText(getApplicationContext(), "请重新选择图片****", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else
                {
                    mImageUris.add(uri);
                    addImageToList(data.getData());
                }
            }
        }
    }
    public void addBitmapToList(Bitmap photo)
    {
        if(null != photo)
        {
            DisplayMetrics dm = PostResponseActivity.this.getResources().getDisplayMetrics();
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);

            ImageView iv = new ImageView(PostResponseActivity.this);
            final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
            LinearLayout.LayoutParams paraIv = new LinearLayout.LayoutParams(width, height);
            paraIv.setMargins(margin, 0, margin, 0);
            iv.setLayoutParams(paraIv);
            //iv.setImageDrawable(d);
            iv.setImageBitmap(photo);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mLLContainer.addView(iv,0);
        }
        else
        {
            return;
        }
    }
    public void addImageToList(Uri uri)
    {
        if(null == uri)
        {
            return;
        }
        DisplayMetrics dm = PostResponseActivity.this.getResources().getDisplayMetrics();
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
        Drawable d;
        try
        {
            d = Drawable.createFromStream(PostResponseActivity.this.getContentResolver().openInputStream(uri), null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        ImageView iv = new ImageView(PostResponseActivity.this);
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, dm);
        LinearLayout.LayoutParams paraIv = new LinearLayout.LayoutParams(width, height);
        paraIv.setMargins(margin, 0, margin, 0);
        iv.setLayoutParams(paraIv);
        iv.setImageDrawable(d);
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
}
