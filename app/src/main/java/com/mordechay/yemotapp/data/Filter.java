package com.mordechay.yemotapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Filter {

    private static Filter instance;
    private Context ctx;
    private SharedPreferences sp;
    private final String TYPE_IMAGE = "type_image_";
    private final String DEFAULT_IMAGE_TYPE = String.valueOf(R.drawable.ic_baseline_question_mark_24);
    private final String TYPE_MIME = "type_mime_";
    private final String DEFAULT_MIME_TYPE = "*/*";
    private final String RESELLER_IMAGE = "reseller_image_";
    private final String DEFAULT_RESELLER_IMAGE = String.valueOf(R.drawable.yemot_header);

    private Filter(@NonNull Context ctx) {
        this.ctx = ctx;
        this.sp = ctx.getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_FILTER, 0);
    }

    public static synchronized Filter getInstance(Context ctx) {
        if (instance == null) {
            instance = new Filter(ctx);
        }
        return instance;
    }

    public Drawable getTypeImage(@NonNull String type) {
        String url = sp.getString(TYPE_IMAGE + type, DEFAULT_IMAGE_TYPE);
        if (url.equals(DEFAULT_IMAGE_TYPE)) {
            return ResourcesCompat.getDrawable(ctx.getResources(), Integer.parseInt(DEFAULT_IMAGE_TYPE), ctx.getTheme());
        } else {
            return loadImageFromPrivateStorage(url, ResourcesCompat.getDrawable(ctx.getResources(), Integer.parseInt(DEFAULT_IMAGE_TYPE), ctx.getTheme()));
        }
    }

    public String getTypeMIME(String type) {
        return sp.getString(TYPE_MIME + type, DEFAULT_MIME_TYPE);
    }

    public Drawable getResellerImage(String reseller) {
        String url = sp.getString(RESELLER_IMAGE + reseller, DEFAULT_RESELLER_IMAGE);
        if (url.equals(DEFAULT_RESELLER_IMAGE)) {
            return ResourcesCompat.getDrawable(ctx.getResources(), Integer.parseInt(DEFAULT_RESELLER_IMAGE), ctx.getTheme());
        } else {
            return loadImageFromPrivateStorage(url, ResourcesCompat.getDrawable(ctx.getResources(), Integer.parseInt(DEFAULT_RESELLER_IMAGE), ctx.getTheme()));
        }
    }


    /*public Drawable loadImageFromPrivateStorage(String fileName) {
            // get the private storage directory
            File privateStorageDir = ctx.getApplicationContext().getFilesDir();

            // get the full path to the image file
            String imagePath = privateStorageDir.getAbsolutePath() + "/" + Constants.RESOURCES_PATH + "/"  + fileName;

            Drawable drawable = null;
            File file = new File(imagePath);
            try {
                drawable = new BitmapDrawable(ctx.getResources(), BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(Uri.fromFile(file))));
            } catch (FileNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    Log.e("Error", "File not found: " + e.toString());
                }
            }


            // create a drawable from the bitmap and return it
            return drawable;
    }
     */

    private Drawable loadImageFromPrivateStorage(String fileName, Drawable defaultImage) {
        return getImageByValue(fileName, defaultImage);
    }


    private Drawable getImageByValue(@NonNull String fileName, @NonNull Drawable defaultImage) {

        String uri = "drawable/" + fileName.substring(0, fileName.length() -4);

        int imageResource = ctx.getResources().getIdentifier(uri, "drawable", ctx.getPackageName());

        return imageResource != 0x0 ? ctx.getResources().getDrawable(imageResource, ctx.getTheme()) : defaultImage;
    }
}
