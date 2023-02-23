///////////////////////////////////////////////////////////////////////////////
//
//  Editor - Text editor for Android
//
//  Copyright © 2017  Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer  william j farmer [at] yahoo [dot] co [dot] uk.
//
////////////////////////////////////////////////////////////////////////////////

package com.mordechay.yemotapp.ui.activitys;




import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SearchView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.file.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class EditExtFileActivity extends AppCompatActivity implements sendApiRequest.RespondsListener {




    public final static String TAG = "EditExtFileActivity";

    public final static String PATH = "path";
    public final static String CONTENT = "content";
    public final static String MODIFIED = "modified";

    public final static String UTF_8 = "UTF-8";

    private final static int POSITION_DELAY = 128;
    private final static int MAX_PATHS = 10;

    private final static int REQUEST_READ = 1;
    private final static int REQUEST_SAVE = 2;
    private final static int REQUEST_OPEN = 3;


    private final static int TINY   = 8;
    private final static int MEDIUM = 18;
    private final static int HUGE  =  32;

    private final static int NORMAL = 1;
    private final static int MONO   = 2;

    private Uri uri;
    private File file;
    private String path;
    private Uri content;
    private EditText textView;
    private ScrollView scrollView;

    private ScaleGestureDetector scaleDetector;

    private Map<String, Integer> pathMap;
    private boolean suggest = true;

    private long modified;

    private int size = MEDIUM;
    private int type = MONO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ext_file);


        if (DataTransfer.getFileUrl() == null || DataTransfer.getFileUrl().isEmpty() || DataTransfer.getFileType() == null || DataTransfer.getFileType().isEmpty()) {
            Toast.makeText(this, "שגיאה לא התקבל נתיב לקובץ או סוג קובץ לא מזוהה.", Toast.LENGTH_LONG).show();
            Log.e("error", DataTransfer.getFileUrl() + "        " +  DataTransfer.getFileType());
            finish();
        }


        MaterialToolbar mtb = findViewById(R.id.open_file_mtb);
        setSupportActionBar(mtb);

        pathMap = new HashMap<>();



        textView = findViewById(R.id.open_file_text);
        scrollView = findViewById(R.id.open_file_vscroll);


            textView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        textView.setTextSize(size);
        textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

        Intent intent = getIntent();
        Uri uri = intent.getData();


                if ((savedInstanceState == null) && (uri != null))
                    readFile(uri);

                if(getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("שם: " + DataTransfer.getFileName());
                    getSupportActionBar().setSubtitle("נתיב: " + DataTransfer.getFilePath());
                }

        setListeners();
    }


    // setListeners
    private void setListeners()
    {
        scaleDetector =
                new ScaleGestureDetector(this, new EditExtFileActivity.ScaleListener());

        if (textView != null)
        {

            // onFocusChange
            textView.setOnFocusChangeListener((v, hasFocus) ->
            {
                // Hide keyboard
                InputMethodManager manager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                if (!hasFocus)
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            });
        }
    }










    // onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        path = savedInstanceState.getString(PATH);
        modified = savedInstanceState.getLong(MODIFIED);
        content = savedInstanceState.getParcelable(CONTENT);
        invalidateOptionsMenu();

        file = new File(path);
        uri = Uri.fromFile(file);

        if (content != null)
            setTitle(FileUtils.getDisplayName(this, content, null, null));

        else
            setTitle(uri.getLastPathSegment());
    }







    // onPause
    @Override
    public void onPause()
    {
        super.onPause();

        // Save current path
        savePath(path);
    }

    // onSaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable(CONTENT, content);
        outState.putLong(MODIFIED, modified);
        outState.putString(PATH, path);
    }

    // dispatchTouchEvent
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        scaleDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    // editClicked
    private void editClicked(MenuItem item)
    {
        // Get scroll position
        int y = scrollView.getScrollY();
        // Get height
        int height = scrollView.getHeight();
        // Get width
        int width = scrollView.getWidth();

        // Get offset
        int line = textView.getLayout()
                .getLineForVertical(y + height / 2);
        int offset = textView.getLayout()
                .getOffsetForHorizontal(line, width / 2);
        // Set cursor
        textView.setSelection(offset);

        // Set editable with or without suggestions
        if (suggest)
            textView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        else
            textView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // Change size and typeface temporarily as workaround for yet
        // another obscure feature of some versions of android
        textView.setTextSize((size == TINY)? HUGE: TINY);
        textView.setTextSize(size);
        textView.setTypeface((type == NORMAL)?
                Typeface.MONOSPACE:
                Typeface.DEFAULT, Typeface.NORMAL);
        textView.setTypeface((type == NORMAL)?
                Typeface.DEFAULT:
                Typeface.MONOSPACE, Typeface.NORMAL);

        // Recreate
        recreate(this);
    }

    // savePath
    private void savePath(String path)
    {
        if (path == null)
            return;

        // Save the current position
        pathMap.put(path, scrollView.getScrollY());

        // Get a list of files
        List<Long> list = new ArrayList<>();
        Map<Long, String> map = new HashMap<>();
        for (String name : pathMap.keySet())
        {
            File file = new File(name);
            list.add(file.lastModified());
            map.put(file.lastModified(), name);
        }

        // Sort in reverse order
        Collections.sort(list);
        Collections.reverse(list);

        int count = 0;
        for (long date : list)
        {
            String name = map.get(date);

            // Remove old files
            if (count >= MAX_PATHS)
            {
                pathMap.remove(name);
            }

            count++;
        }
    }


    // recreate
    private void recreate(Context context)
    {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            recreate();
    }

    // getFile
    private void getFile()
    {
        // Check permissions
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_OPEN);
            return;
        }

        // Open parent folder
        File dir = file.getParentFile();
    }


    // getList
    private List<File> getList(File dir)
    {
        List<File> list = null;
        File[] files = dir.listFiles();
        // Check files
        if (files == null)
        {
            // Create a list with just the parent folder and the
            // external storage folder
            list = new ArrayList<File>();
            if (dir.getParentFile() == null)
                list.add(dir);

            else
                list.add(dir.getParentFile());

            list.add(Environment.getExternalStorageDirectory());

            return list;
        }

        // Sort the files
        Arrays.sort(files);
        // Create a list
        list = new ArrayList<File>(Arrays.asList(files));

        // Add parent folder
        if (dir.getParentFile() == null)
            list.add(0, dir);

        else
            list.add(0, dir.getParentFile());

        return list;
    }


    // onRequestPermissionsResult
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_SAVE:
                for (int i = 0; i < grantResults.length; i++)
                    if (permissions[i].equals(Manifest.permission
                            .WRITE_EXTERNAL_STORAGE) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        // Granted, save file
                        saveFile();
                break;

            case REQUEST_READ:
                for (int i = 0; i < grantResults.length; i++)
                    if (permissions[i].equals(Manifest.permission
                            .READ_EXTERNAL_STORAGE) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        // Granted, read file
                        readFile(uri);
                break;

            case REQUEST_OPEN:
                for (int i = 0; i < grantResults.length; i++)
                    if (permissions[i].equals(Manifest.permission
                            .READ_EXTERNAL_STORAGE) &&
                            grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        // Granted, open file
                        getFile();
                break;
        }
    }

    // readFile
    private void readFile(Uri uri)
    {
        if (uri == null) {
            Log.e("error", "error uri == null...");
            Toast.makeText(this, "שגיאה! לא התקבל נתיב לקובץ", Toast.LENGTH_LONG).show();
            finish();
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
            this.uri = uri;
            return;
        }

        long size = 0;
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
            size = FileUtils.getSize(this, uri, null, null);

        else
        {
            File file = new File(uri.getPath());
            size = file.length();
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Size " + size);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Uri: " + uri);

        // Attempt to resolve content uri
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
        {
            content = uri;
            uri = resolveContent(uri);
        }

        else
            content = null;

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Uri: " + uri);

        // Read into new file if unresolved
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
        {
            Uri defaultUri = Uri.fromFile(file);
            path = defaultUri.getPath();

            setTitle(FileUtils.getDisplayName(this, content, null, null));
        }

        // Read file
        else
        {
            this.uri = uri;
            path = uri.getPath();
            file = new File(path);

            setTitle(uri.getLastPathSegment());
        }

        textView.setText(R.string.loading);

        EditExtFileActivity.ReadTask read = new EditExtFileActivity.ReadTask(this);
        read.execute(uri);

        modified = file.lastModified();
        savePath(path);
        invalidateOptionsMenu();
    }

    // resolveContent
    private Uri resolveContent(Uri uri)
    {
        String path = FileUtils.getPath(this, uri);

        if (path != null)
        {
            File file = new File(path);
            if (file.canRead())
                uri = Uri.fromFile(file);
        }

        return uri;
    }

    // saveFile
    private void saveFile()
    {
        uploadFile();
    }

    private void uploadFile() {
        String token;
        token = DataTransfer.getToken();
        try {
            String url = Constants.URL_UPLOAD_TEXT_FILE + token + "&what=" + DataTransfer.getFilePath() + "&contents=" + URLEncoder.encode(textView.getText().toString(), UTF_8);
            new sendApiRequest(this, this, "uploadFile", url);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "error encode text");
            Toast.makeText(this, "שגיאה בשמירת קובץ", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // write
    private void write(CharSequence text, OutputStream os)
    {
        String charset = UTF_8;

        try (BufferedWriter writer =
                     new BufferedWriter(new OutputStreamWriter(os, charset)))
        {
            writer.append(text);
            writer.flush();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        invalidateOptionsMenu();
    }





    // loadText
    private void loadText(CharSequence text)
    {
        if (textView != null)
            textView.setText(text);

        // Check for saved position
        if (pathMap.containsKey(path))
            textView.postDelayed(() ->
                            scrollView.smoothScrollTo
                                    (0, pathMap.get(path)),
                    POSITION_DELAY);
        else
            textView.postDelayed(() ->
                            scrollView.smoothScrollTo(0, 0),
                    POSITION_DELAY);



            // Set editable with or without suggestions
            if (suggest)
                textView.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            else
                textView.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            // Change typeface temporarily as workaround for yet another
            // obscure feature of some versions of android
            textView.setTypeface((type == NORMAL)?
                    Typeface.MONOSPACE:
                    Typeface.DEFAULT, Typeface.NORMAL);
            textView.setTypeface((type == NORMAL)?
                    Typeface.DEFAULT:
                    Typeface.MONOSPACE, Typeface.NORMAL);


        // Dismiss keyboard
        textView.clearFocus();
    }

    @Override
    public void onSuccess(String result, String type) {
        onBackPressed();
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }

    // QueryTextListener
    private class QueryTextListener
            implements SearchView.OnQueryTextListener
    {
        private BackgroundColorSpan span = new
                BackgroundColorSpan(Color.YELLOW);
        private Editable editable;
        private Matcher matcher;
        private Pattern pattern;
        private int index;
        private int height;

        // onQueryTextChange
        @Override
        @SuppressWarnings("deprecation")
        public boolean onQueryTextChange(String newText)
        {
            // Use regex search and spannable for highlighting
            height = scrollView.getHeight();
            editable = textView.getEditableText();

            // Reset the index and clear highlighting
            if (newText.length() == 0)
            {
                index = 0;
                editable.removeSpan(span);
                return false;
            }

            // Check pattern
            try
            {
                pattern = Pattern.compile(newText, Pattern.MULTILINE);
                matcher = pattern.matcher(editable);
            }

            catch (Exception e)
            {
                return false;
            }

            // Find text
            if (matcher.find(index))
            {
                // Get index
                index = matcher.start();

                // Check layout
                if (textView.getLayout() == null)
                    return false;

                // Get text position
                int line = textView.getLayout().getLineForOffset(index);
                int pos = textView.getLayout().getLineBaseline(line);

                // Scroll to it
                scrollView.smoothScrollTo(0, pos - height / 2);

                // Highlight it
                editable.setSpan(span, matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            else
                index = 0;

            return true;
        }

        // onQueryTextSubmit
        @Override
        public boolean onQueryTextSubmit(String query)
        {
            // Find next text
            if (matcher.find())
            {
                // Get index
                index = matcher.start();

                // Get text position
                int line = textView.getLayout().getLineForOffset(index);
                int pos = textView.getLayout().getLineBaseline(line);

                // Scroll to it
                scrollView.smoothScrollTo(0, pos - height / 2);

                // Highlight it
                editable.setSpan(span, matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            else
            {
                matcher.reset();
                index = 0;
            }

            return true;
        }
    }

    // readFile
    private CharSequence readFile(File file)
    {
        StringBuilder text = new StringBuilder();
        // Open file
        try (BufferedReader reader = new BufferedReader
                (new InputStreamReader
                        (new BufferedInputStream(new FileInputStream(file)))))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                text.append(line);
                text.append(System.getProperty("line.separator"));
            }

            return text;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return text;
    }

    // ScaleListener
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        // onScale
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            size *= detector.getScaleFactor();
            size = Math.max(TINY, Math.min(size, HUGE));
            textView.setTextSize(size);

            return true;
        }
    }

    // ReadTask
    private static class ReadTask
            extends AsyncTask<Uri, Void, CharSequence>
    {
        private WeakReference<EditExtFileActivity> editorWeakReference;

        public ReadTask(EditExtFileActivity editor)
        {
            editorWeakReference = new WeakReference<>(editor);
        }

        // doInBackground
        @Override
        protected CharSequence doInBackground(Uri... uris)
        {
            StringBuilder stringBuilder = new StringBuilder();
            final EditExtFileActivity editor = editorWeakReference.get();
            if (editor == null)
                return stringBuilder;

                if(editor.getActionBar()!= null) {
                    editor.runOnUiThread(() ->
                            editor.getActionBar().setSubtitle(UTF_8));
                }

            try (BufferedInputStream in = new BufferedInputStream
                    (editor.getContentResolver().openInputStream(uris[0])))
            {
                // Create reader
                BufferedReader reader = null;
                    // Detect charset, using UTF-8 hint
                    CharsetMatch match = new
                            CharsetDetector().setDeclaredEncoding(UTF_8)
                            .setText(in).detect();

                    if (match != null)
                    {
                        reader = new BufferedReader(match.getReader());
                    }

                    else
                        reader = new BufferedReader
                                (new InputStreamReader(in));
                    reader = new BufferedReader
                            (new InputStreamReader(in, UTF_8));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line);
                    stringBuilder.append(System.getProperty("line.separator"));
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            return stringBuilder;
        }

        // onPostExecute
        @Override
        protected void onPostExecute(CharSequence result)
        {
            final EditExtFileActivity editor = editorWeakReference.get();
            if (editor == null)
                return;
            editor.loadText(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("שמור");
        menu.getItem(0).setIcon(R.drawable.baseline_save_24);
        menu.getItem(0).setOnMenuItemClickListener(item -> {
            saveFile();
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }
}