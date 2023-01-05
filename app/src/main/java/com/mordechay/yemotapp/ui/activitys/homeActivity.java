package com.mordechay.yemotapp.ui.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.onBackPressedFilesExplorer;
import com.mordechay.yemotapp.network.sendApiRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class homeActivity extends AppCompatActivity implements MenuProvider, View.OnClickListener {


    NavController nvc;
    NavigationView nvgv;
    Fragment fr;
    DrawerLayout drw;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private TextView txtUserName;
    private CircleImageView imgUserImage;
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sped = sp.edit();

        mAuth = FirebaseAuth.getInstance();

        addMenuProvider(this);

        nvgv = findViewById(R.id.nvgv);
        nvgv.setItemIconTintList(null);

        nvc = Navigation.findNavController(this, R.id.nvgv_fragment);
        NavigationUI.setupWithNavController(nvgv, nvc);
        drw = findViewById(R.id.drw);

        txtUserName = nvgv.getHeaderView(0).findViewById(R.id.header_user_name);
        txtUserName.setText(mAuth.getCurrentUser().getEmail());

        imgUserImage = nvgv.getHeaderView(0).findViewById(R.id.header_user_image);
        downloadAndSetImage(mAuth.getCurrentUser().getPhotoUrl());

        btnLogout = nvgv.getHeaderView(0).findViewById(R.id.header_user_logout_button);
        btnLogout.setOnClickListener(this);

        MaterialToolbar mtb = findViewById(R.id.topAppBar);
        setSupportActionBar(mtb);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drw, R.string.open_navigation,R.string.close_navigation);
        drw.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String lang = sp.getString("language", "default");
        Configuration config;
        config = getBaseContext().getResources().getConfiguration();
        Locale locale;
        if (!lang.equals("default")) {
            locale = new Locale(lang);

        } else {
            locale = new Locale(Locale.getDefault().getLanguage());

        }
        Locale.setDefault(locale);
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
 }

    @Override
    public void onBackPressed() {
        if(drw.isDrawerOpen(nvgv)){
            drw.closeDrawer(nvgv);
        } else if (!(findFragmentInstance() instanceof onBackPressedFilesExplorer) || !((onBackPressedFilesExplorer) findFragmentInstance()).onBackPressedFilesExplorer()) {
            super.onBackPressed();
        }

    }



    private Fragment findFragmentInstance() {
        Fragment fragmentHost = getSupportFragmentManager().findFragmentById(R.id.nvgv_fragment);
        List<Fragment> fragmentList = fragmentHost.getChildFragmentManager().getFragments();
        fr = fragmentList.get(fragmentList.size() - 1);
        return fr;
    }



    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(homeActivity.this, loginToServerActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else{
            DataTransfer.setUsername(currentUser.getEmail());
            DataTransfer.setUid(currentUser.getUid());
        }
    }

    private void logout(boolean isAccountsLogout) {
        if(isAccountsLogout) {
            Intent inet = new Intent(this, loginToServerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, 0).edit().clear().apply();
            getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM, 0).edit().clear().apply();

            mAuth.signOut();
            startActivity(inet);
        } else {
            SharedPreferences spPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (spPref.getBoolean("logout", false)) {
                // Create the AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("פעולה");
                builder.setMessage("האם אתה בטוח?");

                // Add the buttons
                builder.setPositiveButton("אישור", (dialog, id) -> {
                    getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM, 0).edit().clear().apply();
                    Intent inet = new Intent(this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(inet);
                });
                builder.setNegativeButton("ביטול", null);
                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM, 0).edit().clear().apply();
                Intent inet = new Intent(this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inet);
            }
        }
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.logout:
                logout(false);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }


    }

    @Override
    public void onClick(View view) {
        if (view == btnLogout) {
            logout(true);
        }
    }


//create download bitmap image function use volly library
    private void downloadAndSetImage(Uri imageUrl) {
        if (imageUrl != null && !imageUrl.toString().isEmpty()) {
            ImageRequest imageRequest = new ImageRequest(imageUrl.toString(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imgUserImage.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    imgUserImage.setImageResource(R.drawable.ic_baseline_account_circle_70);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(imageRequest);
        }else{
            imgUserImage.setImageResource(R.drawable.ic_baseline_account_circle_70);
        }
    }

}






