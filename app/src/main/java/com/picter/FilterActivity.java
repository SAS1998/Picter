package com.picter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.picter.Utility.Helper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.ImageProcessor;
import com.zomato.photofilters.imageprocessors.SubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubfilter;


public class FilterActivity extends AppCompatActivity {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    Toolbar mToolbar;
    ImageView mTickImage;
    ImageView mCenterImage;
    final static int PICK_IMAGE =2;
    final static int MY_PERMISSION_REQUEST=3;

    private static final String TAG=FilterActivity.class.getSimpleName();

    ImageView m1stFilterImage;
    ImageView m2ndFilterImage;
    ImageView m3rdFilterImage;
    ImageView m4thFilterImage;

    Target mSmallTarget=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
//Brightness
            Filter myFilterBrightness = new Filter();
            myFilterBrightness.addSubFilter(new BrightnessSubfilter(90));
            Bitmap outputImageBrightness = myFilterBrightness.processFilter(mutableBitmap);
            Helper.writeToExternalStorage(FilterActivity.this,"Brightness.png",outputImageBrightness);
            Picasso.with(FilterActivity.this).load(Helper.getFileFromExternalStorage(FilterActivity.this,"Brightness.png")).fit().centerInside().into(m1stFilterImage);
//contrast
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterContrast = new Filter();
            myFilterContrast.addSubFilter(new ContrastSubfilter(1.2f));
            Bitmap outputImageContrast = myFilterContrast.processFilter(mutableBitmap);
            Helper.writeToExternalStorage(FilterActivity.this,"Contrast.png",outputImageContrast);
            Picasso.with(FilterActivity.this).load(Helper.getFileFromExternalStorage(FilterActivity.this,"Contrast.png")).fit().centerInside().into(m2ndFilterImage);
//saturation
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterSaturation = new Filter();
            myFilterSaturation.addSubFilter(new SaturationSubfilter(1.3f));
            Bitmap outputImageSaturation = myFilterSaturation.processFilter(mutableBitmap);
            Helper.writeToExternalStorage(FilterActivity.this,"Saturation.png",outputImageSaturation);
            Picasso.with(FilterActivity.this).load(Helper.getFileFromExternalStorage(FilterActivity.this,"Saturation.png")).fit().centerInside().into(m3rdFilterImage);
//vignette
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterVignette = new Filter();
            myFilterVignette.addSubFilter(new VignetteSubfilter(FilterActivity.this, 100));
            Bitmap outputImageVignette = myFilterVignette.processFilter(mutableBitmap);
            Helper.writeToExternalStorage(FilterActivity.this,"Vignette.png",outputImageVignette);
            Picasso.with(FilterActivity.this).load(Helper.getFileFromExternalStorage(FilterActivity.this,"Vignette.png")).fit().centerInside().into(m4thFilterImage);




        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mToolbar= findViewById(R.id.toolbar);
        mCenterImage=(ImageView)findViewById(R.id.CenterImage);

        m1stFilterImage=(ImageView)findViewById(R.id.imageView4);
        m2ndFilterImage=(ImageView)findViewById(R.id.imageView5);
        m3rdFilterImage=(ImageView)findViewById(R.id.imageView6);
        m4thFilterImage=(ImageView)findViewById(R.id.imageView7);

        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setLogo(R.drawable.icon);

        mTickImage=(ImageView)findViewById(R.id.imageView2);
        mTickImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(FilterActivity.this, ImagePriviewActivity.class);
                startActivity(intent);
            }
        });

        mCenterImage.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                requestStoragePermission();
                if (ContextCompat.checkSelfPermission(FilterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

            }

        });

    }


// on create closes


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults ){
        switch(requestCode){
            case MY_PERMISSION_REQUEST:
                    if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        new MaterialDialog.Builder(FilterActivity.this).title(R.string.permission_granted_title).content(R.string.permission_granted)
                                .positiveText("OK")
                                .canceledOnTouchOutside(true).show();
                    }else{
                Log.d(TAG,"Permission denied!");
                    }
        }
    }
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(requestCode==PICK_IMAGE&&resultCode==Activity.RESULT_OK){
            Uri selectedImageUri= data.getData();

            Picasso.with(FilterActivity.this).load(selectedImageUri).fit().centerInside().into(mCenterImage);
            Picasso.with(FilterActivity.this).load(selectedImageUri).into(mSmallTarget);

            Picasso.with(FilterActivity.this).load(selectedImageUri).fit().centerInside().into(m1stFilterImage);
            Picasso.with(FilterActivity.this).load(selectedImageUri).fit().centerInside().into(m2ndFilterImage);
            Picasso.with(FilterActivity.this).load(selectedImageUri).fit().centerInside().into(m3rdFilterImage);
            Picasso.with(FilterActivity.this).load(selectedImageUri).fit().centerInside().into(m4thFilterImage);
        }
    }
    public  void  requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(FilterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FilterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(FilterActivity.this).title(R.string.permission_request_title)
                        .content(R.string.permission_request)
                        .positiveText("Yes").negativeText("No")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(FilterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            return;
        }
    }




}
