package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.onBackPressedFilesExplorer;

import java.util.List;
import java.util.Locale;


public class homeActivity extends AppCompatActivity implements MenuProvider {


    NavController nvc;
    NavigationView nvgv;
    Fragment fr;
    DrawerLayout drw;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;


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

    private void logout() {
        Intent inet = new Intent(this, loginToServerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, 0).edit().clear().commit();
        mAuth.signOut();
        startActivity(inet);
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
                logout();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }


    }
}





