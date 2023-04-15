package com.mordechay.yemotapp.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.mordechay.yemotapp.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class filter {

    private final Activity act;
    private final SharedPreferences sp;
    private final String TYPE_IMAGE = "type_image_";
    private final String DEFAULT_IMAGE_TYPE = String.valueOf(R.drawable.ic_baseline_question_mark_24);
    private final String TYPE_MIME = "type_mime_";
    private final String DEFAULT_MIME_TYPE = "*/*";
    private final String RESELLER_IMAGE = "reseller_image_";
    private final String DEFAULT_RESELLER_IMAGE = String.valueOf(R.drawable.yemot_header);


    public filter(Activity act) {
        this.act = act;
        this.sp = act.getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_FILTER, 0);
    }

    public Drawable getTypeImage(@NonNull String type){
        String url = sp.getString(TYPE_IMAGE + type, DEFAULT_IMAGE_TYPE);
        if(url.equals(DEFAULT_IMAGE_TYPE)) {
            return  ResourcesCompat.getDrawable(act.getResources(), Integer.parseInt(DEFAULT_IMAGE_TYPE), act.getTheme());
        }else {
            return loadImageFromPrivateStorage(url);
        }
    }

    public String getTypeMIME(String type){
        return sp.getString(TYPE_MIME + type, DEFAULT_MIME_TYPE);
    }
    public Drawable getResellerImage(String reseller){
        String url = sp.getString(RESELLER_IMAGE + reseller, DEFAULT_RESELLER_IMAGE);
        if(url.equals(DEFAULT_RESELLER_IMAGE)) {
            return  ResourcesCompat.getDrawable(act.getResources(), Integer.parseInt(DEFAULT_RESELLER_IMAGE), act.getTheme());
        }else {
            return loadImageFromPrivateStorage(url);
        }
    }

        public Drawable loadImageFromPrivateStorage(String fileName) {
            // get the private storage directory
            File privateStorageDir = act.getApplicationContext().getFilesDir();

            // get the full path to the image file
            String imagePath = privateStorageDir.getAbsolutePath() + "/" + Constants.RESOURCES_PATH + "/"  + fileName;

            Drawable drawable = null;
            File file = new File(imagePath);
            try {
                InputStream is = new FileInputStream(file);
                XmlPullParser parser = Xml.newPullParser ();
                parser.setInput (is, null);
                drawable = VectorDrawableCompat.createFromXml(act.getResources(), parser);

            } catch (FileNotFoundException e) {
                Log.e("Error", "File not found: " + e.toString());
            } catch (XmlPullParserException | IOException e) {
                throw new RuntimeException(e);
            }


            // create a drawable from the bitmap and return it
            return drawable;
    }

}
