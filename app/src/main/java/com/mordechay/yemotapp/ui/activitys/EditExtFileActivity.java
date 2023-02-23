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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.ui.programmatically.file.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class EditExtFileActivity extends AppCompatActivity {




    public final static String TAG = "EditExtFileActivity";

    public final static String PATH = "path";
    public final static String EDIT = "edit";
    public final static String MATCH = "match";
    public final static String CHANGED = "changed";
    public final static String CONTENT = "content";
    public final static String MODIFIED = "modified";
    public final static String PREF_HIGHLIGHT = "pref_highlight";
    public final static String PREF_PATHS = "pref_paths";
    public final static String PREF_SAVE = "pref_save";
    public final static String PREF_VIEW = "pref_view";
    public final static String PREF_SIZE = "pref_size";
    public final static String PREF_SUGGEST = "pref_suggest";
    public final static String PREF_THEME = "pref_theme";
    public final static String PREF_TYPE = "pref_type";
    public final static String PREF_WRAP = "pref_wrap";

    public final static String TEXT_PLAIN = "text/plain";
    public final static String UTF_8 = "UTF-8";


    public final static Pattern QUOTED = Pattern.compile
            // "'([^\\\\']+|\\\\([btnfr\"'\\\\]|" +
            // "[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|" +
                    ("\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|" +
                                    "[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"",
                            Pattern.MULTILINE);

    public final static Pattern HTML_TAGS = Pattern.compile
            ("\\b(html|base|head|link|meta|style|title|body|address|article|" +
                            "aside|footer|header|h\\d|hgroup|main|nav|section|blockquote|dd|" +
                            "dir|div|dl|dt|figcaption|figure|hr|li|main|ol|p|pre|ul|a|abbr|" +
                            "b|bdi|bdo|br|cite|code|data|dfn|em|i|kbd|mark|q|rb|rp|rt|rtc|" +
                            "ruby|s|samp|small|span|strong|sub|sup|time|tt|u|var|wbr|area|" +
                            "audio|img|map|track|video|applet|embed|iframe|noembed|object|" +
                            "param|picture|source|canvas|noscript|script|del|ins|caption|" +
                            "col|colgroup|table|tbody|td|tfoot|th|thead|tr|button|datalist|" +
                            "fieldset|form|input|label|legend|meter|optgroup|option|output|" +
                            "progress|select|textarea|details|dialog|menu|menuitem|summary|" +
                            "content|element|shadow|slot|template|acronym|applet|basefont|" +
                            "bgsound|big|blink|center|command|content|dir|element|font|" +
                            "frame|frameset|image|isindex|keygen|listing|marquee|menuitem|" +
                            "multicol|nextid|nobr|noembed|noframes|plaintext|shadow|spacer|" +
                            "strike|tt|xmp|doctype)\\b",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public final static Pattern HTML_ATTRS = Pattern.compile
            ("\\b(accept|accesskey|action|align|allow|alt|async|" +
                            "auto(capitalize|complete|focus|play)|background|" +
                            "bgcolor|border|buffered|challenge|charset|checked|cite|" +
                            "class|code(base)?|color|cols|colspan|content(" +
                            "editable)?|contextmenu|controls|coords|crossorigin|" +
                            "csp|data|datetime|decoding|def(ault|er)|dir|dirname|" +
                            "disabled|download|draggable|dropzone|enctype|enterkeyhint|" +
                            "equiv|for|form(action|novalidate)?|headers|height|" +
                            "hidden|high|href(lang)?|http|icon|id|importance|" +
                            "inputmode|integrity|intrinsicsize|ismap|itemprop|keytype|" +
                            "kind|label|lang|language|list|loading|loop|low|manifest|" +
                            "max|maxlength|media|method|min|minlength|multiple|muted|" +
                            "name|novalidate|open|optimum|pattern|ping|placeholder|" +
                            "poster|preload|property|radiogroup|readonly|referrerpolicy|" +
                            "rel|required|reversed|rows|rowspan|sandbox|scope|scoped|" +
                            "selected|shape|size|sizes|slot|span|spellcheck|src|srcdoc|" +
                            "srclang|srcset|start|step|style|summary|tabindex|target|" +
                            "title|translate|type|usemap|value|width|wrap)\\b",
                    Pattern.MULTILINE);

    public final static Pattern HTML_COMMENT =
            Pattern.compile("<!--.*?-->", Pattern.MULTILINE);
    public final static Pattern MODE_PATTERN = Pattern.compile
            ("^\\S+\\s+ed:(.+)$", Pattern.MULTILINE);
    public final static Pattern OPTION_PATTERN = Pattern.compile
            ("(\\s+(no)?(vw|ww|sg|cs|hs|th|ts|tf)(:\\w)?)", Pattern.MULTILINE);
    public final static Pattern WORD_PATTERN = Pattern.compile
            ("\\w+", Pattern.MULTILINE);

    private final static double KEYBOARD_RATIO = 0.25;

    private final static int LAST_SIZE = 256;
    private final static int FIRST_SIZE = 256;
    private final static int POSITION_DELAY = 128;
    private final static int UPDATE_DELAY = 128;
    private final static int FIND_DELAY = 128;
    private final static int MAX_PATHS = 10;

    private final static int REQUEST_READ = 1;
    private final static int REQUEST_SAVE = 2;
    private final static int REQUEST_OPEN = 3;

    private final static int OPEN_DOCUMENT   = 1;
    private final static int CREATE_DOCUMENT = 2;

    private final static int LIGHT = 1;
    private final static int DARK  = 2;
    private final static int BLACK = 3;
    private final static int RETRO = 4;

    private final static int TINY   = 8;
    private final static int SMALL  = 12;
    private final static int MEDIUM = 18;
    private final static int LARGE  = 24;
    private final static int HUGE  =  32;

    private final static int NORMAL = 1;
    private final static int MONO   = 2;

    private final static int NO_SYNTAX   = 0;
    private final static int CC_SYNTAX   = 1;
    private final static int HTML_SYNTAX = 2;
    private final static int CSS_SYNTAX  = 3;
    private final static int ORG_SYNTAX  = 4;
    private final static int MD_SYNTAX   = 5;
    private final static int SH_SYNTAX   = 6;
    private final static int DEF_SYNTAX  = 7;

    private Uri uri;
    private File file;
    private String path;
    private Uri content;
    private String match;
    private EditText textView;
    private TextView customView;
    private MenuItem searchItem;
    private SearchView searchView;
    private ScrollView scrollView;
    private Runnable updateHighlight;
    private Runnable updateWordCount;

    private ScaleGestureDetector scaleDetector;

    private Map<String, Integer> pathMap;
    private List<String> removeList;

    private boolean highlight = false;

    private boolean save = false;
    private boolean edit = false;
    private boolean view = false;

    private boolean wrap = false;
    private boolean suggest = true;

    private boolean changed = false;

    private long modified;

    private int theme = LIGHT;
    private int size = MEDIUM;
    private int type = MONO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);


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


        updateWordCount = () -> wordCountText();

        edit = true;


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


    @Override
    public void onBackPressed() {
        finish();
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

                if (updateHighlight != null)
                {
                    textView.removeCallbacks(updateHighlight);
                    textView.postDelayed(updateHighlight, UPDATE_DELAY);
                }
            });

            // onLongClick
            textView.setOnLongClickListener(v ->
            {
                // Do nothing if already editable
                if (edit)
                    return false;

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
                    textView
                            .setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                else
                    textView
                            .setInputType(InputType.TYPE_CLASS_TEXT |
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
                // Update boolean
                edit = true;

                // Restart
                recreate(this);

                return false;
            });

            textView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener()
                    {
                        private boolean keyboard;

                        // onGlobalLayout
                        @Override
                        public void onGlobalLayout()
                        {
                            if (updateHighlight != null)
                            {
                                int rootHeight = scrollView.getRootView().getHeight();
                                int height = scrollView.getHeight();

                                boolean shown = (((rootHeight - height) /
                                        (double) rootHeight) >
                                        KEYBOARD_RATIO);

                                if (shown != keyboard)
                                {
                                    if (!shown)
                                    {
                                        textView.removeCallbacks(updateHighlight);
                                        textView.postDelayed(updateHighlight,
                                                UPDATE_DELAY);
                                    }

                                    keyboard = shown;
                                }
                            }
                        }
                    });
        }

        if (scrollView != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                // onScrollChange
                scrollView.setOnScrollChangeListener((v, x, y, oldX, oldY) ->
                {
                    if (updateHighlight != null)
                    {
                        textView.removeCallbacks(updateHighlight);
                        textView.postDelayed(updateHighlight, UPDATE_DELAY);
                    }
                });

            else
                // onScrollChange
                scrollView.getViewTreeObserver()
                        .addOnScrollChangedListener(() ->
                        {
                            if (updateHighlight != null)
                            {
                                textView.removeCallbacks(updateHighlight);
                                textView.postDelayed(updateHighlight, UPDATE_DELAY);
                            }
                        });
        }
    }










    // onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        path = savedInstanceState.getString(PATH);
        edit = savedInstanceState.getBoolean(EDIT);
        changed = savedInstanceState.getBoolean(CHANGED);
        match = savedInstanceState.getString(MATCH);
        modified = savedInstanceState.getLong(MODIFIED);
        content = savedInstanceState.getParcelable(CONTENT);
        invalidateOptionsMenu();

        file = new File(path);
        uri = Uri.fromFile(file);

        if (content != null)
            setTitle(FileUtils.getDisplayName(this, content, null, null));

        else
            setTitle(uri.getLastPathSegment());

        if (match == null)
            match = UTF_8;
        getSupportActionBar().setSubtitle(match);

        checkHighlight();

    }







    // onPause
    @Override
    public void onPause()
    {
        super.onPause();

        // Save current path
        savePath(path);

        // Stop highlighting
        textView.removeCallbacks(updateHighlight);
        textView.removeCallbacks(updateWordCount);
    }

    // onSaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable(CONTENT, content);
        outState.putLong(MODIFIED, modified);
        outState.putBoolean(CHANGED, changed);
        outState.putString(MATCH, match);
        outState.putBoolean(EDIT, edit);
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
        // Update boolean
        edit = true;

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
                removeList.add(name);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_OPEN);
                return;
            }
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
        if (uri == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
                this.uri = uri;
                return;
            }
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

        // Stop highlighting
        textView.removeCallbacks(updateHighlight);
        textView.removeCallbacks(updateWordCount);

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

        changed = false;
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

    // saveCheck
    private void saveCheck()
    {
        saveFile();
    }

    // saveFile
    private void saveFile()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SAVE);
                return;
            }
        }

        // Stop highlighting
        textView.removeCallbacks(updateHighlight);
        textView.removeCallbacks(updateWordCount);

        if (content == null)
            saveFile(file);
        else
                saveFile(content);
    }

    // saveFile
    private void saveFile(File file)
    {
        CharSequence text = textView.getText();
        write(text, file);
    }

    // saveFile
    private void saveFile(Uri uri)
    {
        CharSequence text = textView.getText();
        try (OutputStream outputStream =
                     getContentResolver().openOutputStream(uri, "rwt"))
        {
            write(text, outputStream);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }

    // write
    private void write(CharSequence text, File file)
    {
        file.getParentFile().mkdirs();

        String charset = UTF_8;

        if (match != null && !match.equals(getString(R.string.detect)))
            charset = match;

        try (BufferedWriter writer = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(file), charset)))
        {
            writer.append(text);
            writer.flush();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        changed = false;
        invalidateOptionsMenu();
        modified = file.lastModified();
        savePath(file.getPath());
    }

    // write
    private void write(CharSequence text, OutputStream os)
    {
        String charset = UTF_8;
        if (match != null && !match.equals(getString(R.string.detect)))
            charset = match;

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

        changed = false;
        invalidateOptionsMenu();
    }

    // checkHighlight
    private void checkHighlight()
    {
        // Check extension
        if (highlight && file != null)
        {
            String ext = FileUtils.getExtension(file.getName());
            if (ext != null)
            {
                // Add callback
                if (textView != null)
                {
                    if (updateHighlight == null)
                        updateHighlight = () -> highlightText();

                    textView.removeCallbacks(updateHighlight);
                    textView.postDelayed(updateHighlight, UPDATE_DELAY);

                    return;
                }
            }
        }

        // Remove highlighting
        if (updateHighlight != null)
        {
            textView.removeCallbacks(updateHighlight);
            textView.postDelayed(updateHighlight, UPDATE_DELAY);

            updateHighlight = null;
        }
    }

    // highlightText
    private void highlightText()
    {
        // Get visible extent
        int top = scrollView.getScrollY();
        int height = scrollView.getHeight();

        int line = textView.getLayout().getLineForVertical(top);
        int start = textView.getLayout().getLineStart(line);
        int first = textView.getLayout().getLineStart(line + 1);

        line = textView.getLayout().getLineForVertical(top + height);
        int end = textView.getLayout().getLineEnd(line);
        int last = (line == 0)? end:
                textView.getLayout().getLineStart(line - 1);

        // Move selection if outside range
        if (textView.getSelectionStart() < start)
            textView.setSelection(first);

        if (textView.getSelectionStart() > end)
            textView.setSelection(last);

        // Get editable
        Editable editable = textView.getEditableText();

        // Get current spans
        ForegroundColorSpan spans[] =
                editable.getSpans(start, end, ForegroundColorSpan.class);
        // Remove spans
        for (ForegroundColorSpan span: spans)
            editable.removeSpan(span);

        Matcher matcher;

                matcher = HTML_TAGS.matcher(editable);
                matcher.region(start, end);
                while (matcher.find())
                {
                    ForegroundColorSpan span = new
                            ForegroundColorSpan(Color.CYAN);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                matcher.region(start, end).usePattern(HTML_ATTRS);
                while (matcher.find())
                {
                    ForegroundColorSpan span = new
                            ForegroundColorSpan(Color.MAGENTA);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                matcher.region(start, end).usePattern(QUOTED);
                while (matcher.find())
                {
                    ForegroundColorSpan span = new
                            ForegroundColorSpan(Color.RED);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                matcher.region(start, end).usePattern(HTML_COMMENT);
                while (matcher.find())
                {
                    ForegroundColorSpan span = new
                            ForegroundColorSpan(Color.RED);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
    }

    // wordCountText
    private void wordCountText()
    {
        int words = 0;
        Matcher matcher = WORD_PATTERN.matcher(textView.getText());
        while (matcher.find())
        {
            words++;
        }

        if (customView != null)
        {
            String string = String.format(Locale.getDefault(), "%d\n%d",
                    words, textView.length());
            customView.setText(string);
        }
    }






    // checkMode
    private void checkMode(CharSequence text)
    {
        boolean change = false;

        CharSequence first = text.subSequence
                (0, Math.min(text.length(), FIRST_SIZE));
        CharSequence last = text.subSequence
                (Math.max(0, text.length() - LAST_SIZE), text.length());
        for (CharSequence sequence: new CharSequence[]{first, last})
        {
            Matcher matcher = MODE_PATTERN.matcher(sequence);
            if (matcher.find())
            {
                matcher.region(matcher.start(1), matcher.end(1));
                matcher.usePattern(OPTION_PATTERN);
                while (matcher.find())
                {
                    boolean no = "no".equals(matcher.group(2));

                    if ("vw".equals(matcher.group(3)))
                    {
                        if (view == no)
                        {
                            view = !no;
                            change = true;
                        }
                    }

                    else if ("ww".equals(matcher.group(3)))
                    {
                        if (wrap == no)
                        {
                            wrap = !no;
                            change = true;
                        }
                    }

                    else if ("sg".equals(matcher.group(3)))
                    {
                        if (suggest == no)
                        {
                            suggest = !no;
                            change = true;
                        }
                    }

                    else if ("hs".equals(matcher.group(3)))
                    {
                        if (highlight == no)
                        {
                            highlight = !no;
                            checkHighlight();
                        }
                    }

                    else if ("th".equals(matcher.group(3)))
                    {
                        if (":l".equals(matcher.group(4)))
                        {
                            if (theme != LIGHT)
                            {
                                theme = LIGHT;
                                change = true;
                            }
                        }

                        else if (":d".equals(matcher.group(4)))
                        {
                            if (theme != DARK)
                            {
                                theme = DARK;
                                change = true;
                            }
                        }

                        else if (":b".equals(matcher.group(4)))
                        {
                            if (theme != BLACK)
                            {
                                theme = BLACK;
                                change = true;
                            }
                        }

                        else if (":r".equals(matcher.group(4)))
                        {
                            if (theme != RETRO)
                            {
                                theme = RETRO;
                                change = true;
                            }
                        }
                    }

                    else if ("ts".equals(matcher.group(3)))
                    {
                        if (":l".equals(matcher.group(4)))
                        {
                            if (size != LARGE)
                            {
                                size = LARGE;
                                textView.setTextSize(size);
                            }
                        }

                        else if (":m".equals(matcher.group(4)))
                        {
                            if (size != MEDIUM)
                            {
                                size = MEDIUM;
                                textView.setTextSize(size);
                            }
                        }

                        else if (":s".equals(matcher.group(4)))
                        {
                            if (size != SMALL)
                            {
                                size = SMALL;
                                textView.setTextSize(size);
                            }
                        }
                    }

                    else if ("tf".equals(matcher.group(3)))
                    {
                        if (":m".equals(matcher.group(4)))
                        {
                            if (type != MONO)
                            {
                                type = MONO;
                                textView.setTypeface
                                        (Typeface.MONOSPACE, Typeface.NORMAL);
                            }
                        }

                        else if (":p".equals(matcher.group(4)))
                        {
                            if (type != NORMAL)
                            {
                                type = NORMAL;
                                textView.setTypeface
                                        (Typeface.DEFAULT, Typeface.NORMAL);
                            }
                        }
                    }

                    else if ("cs".equals(matcher.group(3)))
                    {
                        if (":u".equals(matcher.group(4)))
                        {
                            match = UTF_8;
                        }
                    }
                }
            }
        }

        if (change)
            recreate(this);
    }

    // loadText
    private void loadText(CharSequence text)
    {
        if (textView != null)
            textView.setText(text);

        changed = false;

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
        // Check mode
        checkMode(text);

        // Check highlighting
        checkHighlight();

        // Set read only
        if (view)
        {
            textView.setRawInputType(InputType.TYPE_NULL);

            // Update boolean
            edit = false;
        }

        else
        {
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
            // Update boolean
            edit = true;
        }

        // Dismiss keyboard
        textView.clearFocus();
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

    // FindTask
    private static class FindTask
            extends AsyncTask<String, Void, List<File>>
    {
        private WeakReference<EditExtFileActivity> editorWeakReference;
        private Pattern pattern;
        private String search;

        // FindTask
        public FindTask(EditExtFileActivity editor)
        {
            editorWeakReference = new WeakReference<>(editor);
        }

        // doInBackground
        @Override
        protected List<File> doInBackground(String... params)
        {
            // Create a list of matches
            List<File> matchList = new ArrayList<>();
            final EditExtFileActivity editor = editorWeakReference.get();
            if (editor == null)
                return matchList;

            search = params[0];
            // Check pattern
            try
            {
                pattern = Pattern.compile(search, Pattern.MULTILINE);
            }

            catch (Exception e)
            {
                return matchList;
            }

            // Get entry list
            List<File> entries = new ArrayList<>();
            for (String path : editor.pathMap.keySet())
            {
                File entry = new File(path);
                entries.add(entry);
            }

            // Check the entries
            for (File file : entries)
            {
                CharSequence content = editor.readFile(file);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find())
                    matchList.add(file);
            }

            return matchList;
        }

        // onPostExecute
        @Override
        protected void onPostExecute(List<File> matchList)
        {
            final EditExtFileActivity editor = editorWeakReference.get();
            if (editor == null)
                return;

            // Build dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(editor);
            builder.setTitle("חפש");

            // If found populate dialog
            if (!matchList.isEmpty())
            {
                List<String> choiceList = new ArrayList<>();
                for (File file : matchList)
                {
                    // Remove path prefix
                    String path = file.getPath();
                    String name =
                            path.replaceFirst(Environment
                                    .getExternalStorageDirectory()
                                    .getPath() + File.separator, "");

                    choiceList.add(name);
                }

                String[] choices = choiceList.toArray(new String[0]);
                builder.setItems(choices, (dialog, which) ->
                {
                    File file = matchList.get(which);
                    Uri uri = Uri.fromFile(file);
                    // Open the entry chosen
                    editor.readFile(uri);

                    // Put the search text back - why it
                    // disappears I have no idea or why I have to
                    // do it after a delay
                    editor.searchView.postDelayed(() ->
                            editor.searchView.setQuery(search, false), FIND_DELAY);
                });
            }

            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
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

            // Default UTF-8
            if (editor.match == null)
            {
                editor.match = UTF_8;
                if(editor.getActionBar()!= null) {
                    editor.runOnUiThread(() ->
                            editor.getActionBar().setSubtitle(editor.match));
                }
            }

            try (BufferedInputStream in = new BufferedInputStream
                    (editor.getContentResolver().openInputStream(uris[0])))
            {
                // Create reader
                BufferedReader reader = null;
                if (editor.match.equals(editor.getString(R.string.detect)))
                {
                    // Detect charset, using UTF-8 hint
                    CharsetMatch match = new
                            CharsetDetector().setDeclaredEncoding(UTF_8)
                            .setText(in).detect();

                    if (match != null)
                    {
                        editor.match = match.getName();
                        editor.runOnUiThread(() ->
                                editor.getActionBar().setSubtitle(editor.match));
                        reader = new BufferedReader(match.getReader());
                    }

                    else
                        reader = new BufferedReader
                                (new InputStreamReader(in));

                    if (BuildConfig.DEBUG && match != null)
                        Log.d(TAG, "Charset " + editor.match);
                }

                else
                    reader = new BufferedReader
                            (new InputStreamReader(in, editor.match));

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
}