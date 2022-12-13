package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.interfaces.IOnBackPressed;

import java.util.List;
import java.util.Locale;


public class homeActivity extends AppCompatActivity {


    NavController nvc;
    NavigationView nvgv;
    static Menu menu;
    Fragment fragment;
    NavDestination fil;
    Fragment fr;
    DrawerLayout drw;
    ActionBar actb;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sped = sp.edit();

        mAuth = FirebaseAuth.getInstance();

        nvgv = findViewById(R.id.nvgv);
        nvgv.setItemIconTintList(null);
        nvc = Navigation.findNavController(this, R.id.nvgv_fragment);
        NavigationUI.setupWithNavController(nvgv, nvc);
        drw = findViewById(R.id.drw);

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
        } else if (!(findFragmentInstance() instanceof IOnBackPressed) || !((IOnBackPressed) findFragmentInstance()).onBackPressed()) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);

        return super.onCreateOptionsMenu(menu);

    }


    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(homeActivity.this, loginToServerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void logout() {
        Intent inet = new Intent(this, loginToServerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getSharedPreferences("User", 0).edit().clear().commit();
        mAuth.signOut();
        startActivity(inet);
    }


}





