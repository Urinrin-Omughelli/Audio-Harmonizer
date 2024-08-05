package com.example.audioharmonizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import java.nio.charset.Charset;

public class StartSingingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    public int counter = 10;
    public int bpMinute = 0;
    public int bpMeasure = 0;
    public long fullLengthTime = 0;
    Button start_singing_button, stop_singing_button;
    TextView countDown_textview;
    private ProgressBar progressBar;
    BluetoothAdapter mBlueAdapter;
    private static final String TAG = "StartSingingActivity";

    public DrawerLayout drawerLayout_StartSinging;
    public ActionBarDrawerToggle actionBarDrawerToggle_StartSinging;
    private NavigationView navigationView;

    private Handler handler = new Handler();
    int batteryLevel = 0;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_singing);

        initializeNavBar();

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        bpMinute = Integer.parseInt(globalVariable.getBeatsPerMinute());
        bpMeasure = Integer.parseInt(globalVariable.getBeatsPerMeasure());
        fullLengthTime = (bpMeasure* 60L)/bpMinute;


        //********************************bluetooth***********************************
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBlueAdapter.isEnabled()){
            showToast("You must turn bluetooth back on");
            Intent intent = new Intent(StartSingingActivity.this, Bluetooth2Activity.class);
            startActivity(intent);
        }

        progress = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel())  < 21){
            progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else{
            progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        progress.setProgress(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()));
        updateBattery();

        start_singing_button = (Button)findViewById(R.id.start_singing_button);
        stop_singing_button = (Button)findViewById(R.id.stop_singing_button);
        countDown_textview= (TextView) findViewById(R.id.countDown_textview);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(bpMeasure-1);
        progressBar.setProgress(bpMeasure-1);

        start_singing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_singing_button.setEnabled(false);
                counter = bpMeasure;
                new CountDownTimer( fullLengthTime*1000, (fullLengthTime*1000/bpMeasure)){
                    public void onTick(long millisUntilFinished){
                        System.out.println("Counter Value: " + String.valueOf(counter));
                        countDown_textview.setText(String.valueOf(counter));
                        counter--;
                        progressBar.setProgress(counter);

                    }
                    public void onFinish(){
                        try {
                            countDown_textview.setText("GO!!");
                            Log.d(TAG, "CountDown Timer is working and has finished");

                            String startSinging = "start";
                            globalVariable.getmBluetoothConnection().write(startSinging.getBytes(Charset.defaultCharset()));
                            String delim = ";";
                            globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));
                        }
                        catch (Exception e) {
                            Log.d(TAG, "CountDown Timer is not Working");
                        }
                    }
                }.start();


            }
        });

        stop_singing_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_singing_button.setEnabled(true);
                String stopSinging = "stop";
                globalVariable.getmBluetoothConnection().write(stopSinging.getBytes(Charset.defaultCharset()));
            }
        });


    }

    public void initializeNavBar(){
        drawerLayout_StartSinging = findViewById(R.id.my_drawer_layout_start_singing);
        actionBarDrawerToggle_StartSinging = new ActionBarDrawerToggle(this, drawerLayout_StartSinging, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout_StartSinging.addDrawerListener(actionBarDrawerToggle_StartSinging);
        actionBarDrawerToggle_StartSinging.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationview_id_start_singing);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().getItem(5).setChecked(true);
        onNavigationItemSelected(menuItem);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle_StartSinging.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(StartSingingActivity.this, HomePageActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_initial_inputs) {
            Intent intent = new Intent(StartSingingActivity.this, InitialInputActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_modes) {
            Intent intent = new Intent(StartSingingActivity.this, ModeOfOperationActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.nav_automatic) {
            Intent intent = new Intent(StartSingingActivity.this, AutomaticActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_manual) {
            Intent intent = new Intent(StartSingingActivity.this, ManualActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_start_singing) {
            return true;
        } else if (item.getItemId() == R.id.nav_faq) {
            Intent intent = new Intent(StartSingingActivity.this, FAQActivity.class);
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