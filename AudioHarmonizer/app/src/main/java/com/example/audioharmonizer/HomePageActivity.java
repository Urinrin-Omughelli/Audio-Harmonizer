package com.example.audioharmonizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // define the global variable
    TextView batteryLevel_tv;
    Button next_button;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Handler handler = new Handler();
    BluetoothAdapter mBlueAdapter;

    int batteryLevel = 100;
    ProgressBar progress;

    //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    //BluetoothSocket mBTSocket = preferences.getBluetoothSocket("mBTSocket", null);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();


        //********************************bluetooth***********************************
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBlueAdapter.isEnabled()){
            showToast("You must turn bluetooth back on");
            Intent intent = new Intent(HomePageActivity.this, Bluetooth2Activity.class);
            startActivity(intent);
        }

        //**********************NavBar Functionality**********************************
        drawerLayout = findViewById(R.id.my_drawer_layout_home_page);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationview_id_home);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(menuItem);
        //**********************************************************************

        // by ID we can use each component which id is assign in xml file
        // use findViewById() to get the both Button and textview

        progress = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel())  < 21){
            progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else{
            progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        progress.setProgress(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()));
        updateBattery();


        next_button = (Button)findViewById(R.id.get_started);
        next_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                //mReadThread.stop();
                Intent intent = new Intent(HomePageActivity.this, InitialInputActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            return true;
        } else if (item.getItemId() == R.id.nav_initial_inputs) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, InitialInputActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_modes) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, ModeOfOperationActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.nav_automatic) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, AutomaticActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_manual) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, ManualActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_start_singing) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, StartSingingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_faq) {
            //mReadThread.stop();
            Intent intent = new Intent(HomePageActivity.this, FAQActivity.class);
            startActivity(intent);
        } else {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private final int FIVE_SECONDS = 5000;
    public void updateBattery() {
        handler.postDelayed(new Runnable() {
            public void run() {
                //System.out.println("updating battery in runnable");
                if (Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()) <= 100) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel())  < 21){
                                progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            } else{
                                progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            }

                            progress.setProgress(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()) );
                        }
                    });
                }
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }

}

