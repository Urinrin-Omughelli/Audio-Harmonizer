package com.example.audioharmonizer;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class InitialInputActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout_InitialInputs;
    public ActionBarDrawerToggle actionBarDrawerToggle_InitialInputs;
    private NavigationView navigationView;
    BluetoothAdapter mBlueAdapter;

    private Handler handler = new Handler();
    //private ReadInput mReadThread = null;
    int batteryLevel = 0;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_input);

        //accessing global variables
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        Button start_button = (Button) findViewById(R.id.start_button);
        EditText name_of_song = (EditText) findViewById(R.id.name_of_song);
        EditText beats_per_measure = (EditText) findViewById(R.id.beats_per_measure);
        EditText beats_per_minute = (EditText) findViewById(R.id.beats_per_minute);


        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBlueAdapter.isEnabled()){
            showToast("You must turn bluetooth back on");
            Intent intent = new Intent(InitialInputActivity.this, Bluetooth2Activity.class);
            startActivity(intent);
        }


        //**********************NavBar Functionality**********************************
        drawerLayout_InitialInputs = findViewById(R.id.my_drawer_layout_initial_inputs);
        actionBarDrawerToggle_InitialInputs = new ActionBarDrawerToggle(this, drawerLayout_InitialInputs, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout_InitialInputs.addDrawerListener(actionBarDrawerToggle_InitialInputs);
        actionBarDrawerToggle_InitialInputs.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationview_id_initial_inputs);
        navigationView.setNavigationItemSelectedListener(this);

        //the integer determines which page on the navbar is highlighted
        MenuItem menuItem = navigationView.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(menuItem);

        //**********************************************************************


        //mReadThread = new ReadInput(globalVariable.getmBluetoothConnection().getSocket());
        progress = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel())  < 21){
            progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else{
            progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        progress.setProgress(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()));
        updateBattery();

        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //Save inputs in global automatic array to access in other files

                globalVariable.setNameOfSong(name_of_song.getText().toString());
                globalVariable.setBeatsPerMeasure(beats_per_measure.getText().toString());
                globalVariable.setBeatsPerMinute(beats_per_minute.getText().toString());

                globalVariable.getInitialInputsArray()[0] =  globalVariable.getNameOfSong();
                globalVariable.getInitialInputsArray()[1] = globalVariable.getBeatsPerMeasure();
                globalVariable.getInitialInputsArray()[2] = globalVariable.getBeatsPerMinute();


                //here should be an error checker -> if fields are empty then it should not proceed to the next page
                //this does not work
                if (TextUtils.isEmpty(beats_per_measure.getText().toString()) ||
                        TextUtils.isEmpty(beats_per_minute.getText().toString()) ||
                            TextUtils.isEmpty(name_of_song.getText().toString()) ){
                    showToast("You must enter in all the fields to continue");
                } else if(Integer.parseInt(beats_per_minute.getText().toString()) > 150){
                    showToast("Beats per Minute must be 130 or less");
                }else if(Integer.parseInt(beats_per_measure.getText().toString()) > 20){
                    showToast("Beats per Measure must be 20 or less");
                }else{
                    //mReadThread.stop();
                    Intent intent = new Intent(InitialInputActivity.this, ModeOfOperationActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle_InitialInputs.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        if (item.getItemId() == R.id.nav_home) {
            //mReadThread.stop();
            Intent intent = new Intent(InitialInputActivity.this, HomePageActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_initial_inputs) {
            return true;
        } else if (item.getItemId() == R.id.nav_modes) {
            //showToast(globalVariable.getNameOfSong());
            if (TextUtils.isEmpty(globalVariable.getBeatsPerMeasure()) ||
                    TextUtils.isEmpty(globalVariable.getBeatsPerMinute()) ||
                        TextUtils.isEmpty(globalVariable.getNameOfSong())  ){
                showToast("You must enter in all the fields to continue");
            } else{
                //mReadThread.stop();
                Intent intent = new Intent(InitialInputActivity.this, ModeOfOperationActivity.class);
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.nav_automatic) {
            //mReadThread.stop();
            Intent intent = new Intent(InitialInputActivity.this, AutomaticActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.nav_manual) {
            //mReadThread.stop();
            Intent intent = new Intent(InitialInputActivity.this, ManualActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_start_singing) {
            //mReadThread.stop();
            Intent intent = new Intent(InitialInputActivity.this, StartSingingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_faq) {
            //mReadThread.stop();
            Intent intent = new Intent(InitialInputActivity.this, FAQActivity.class);
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