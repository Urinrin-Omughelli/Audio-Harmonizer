package com.example.audioharmonizer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutomaticActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button automatic_finish_button;//send button

    private static final String TAG = "AutomaticActivity";

    BluetoothDevice mBTDevice;
    BluetoothAdapter mBlueAdapter;
    //public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public DrawerLayout drawerLayout_automatic;
    public ActionBarDrawerToggle actionBarDrawerToggle_automatic;
    private NavigationView navigationView;
    public String delim = ";";

    private Handler handler = new Handler();
    int batteryLevel = 0;
    ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        //**********************************bluetooth***************************************


        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBlueAdapter.isEnabled()){
            showToast("You must turn bluetooth back on");
            Intent intent = new Intent(AutomaticActivity.this, Bluetooth2Activity.class);
            startActivity(intent);
        }


        //**********************************bluetooth***************************************



        //**********************NavBar Functionality START**********************************
        drawerLayout_automatic = findViewById(R.id.my_drawer_layout_automatic);
        actionBarDrawerToggle_automatic = new ActionBarDrawerToggle(this, drawerLayout_automatic, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout_automatic.addDrawerListener(actionBarDrawerToggle_automatic);
        actionBarDrawerToggle_automatic.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationview_id_automatic);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().getItem(3).setChecked(true);
        onNavigationItemSelected(menuItem);
        //**********************NavBar Functionality END**********************************


        Spinner cp_spinner = (Spinner) findViewById(R.id.cp_spinner);
        Spinner cp_spinner2 = (Spinner) findViewById(R.id.cp_spinner2);
        Spinner cp_spinner3 = (Spinner) findViewById(R.id.cp_spinner3);
        Spinner cp_spinner4 = (Spinner) findViewById(R.id.cp_spinner4);

        Spinner noh_spinner = (Spinner) findViewById(R.id.noh_spinner);


        //***************************Chord Progression Spinners***************************************

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(AutomaticActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.chord_progressions));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cp_spinner.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(AutomaticActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.chord_progressions));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cp_spinner2.setAdapter(myAdapter2);

        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(AutomaticActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.chord_progressions));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cp_spinner3.setAdapter(myAdapter3);

        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(AutomaticActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.chord_progressions));
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cp_spinner4.setAdapter(myAdapter4);
        //*********************************************************************************************************

        ArrayAdapter<String> myHarmonyAdapter = new ArrayAdapter<String>(AutomaticActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.number_of_harmonies));
        myHarmonyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noh_spinner.setAdapter(myHarmonyAdapter);

        //**************************Battery Level Updates**********************************************************

        progress = (ProgressBar) findViewById(R.id.simpleProgressBar);
        if(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel())  < 21){
            progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else{
            progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        progress.setProgress(Integer.parseInt(GlobalClass.getInstance().getBatteryLevel()));
        updateBattery();



        automatic_finish_button = (Button)findViewById(R.id.automatic_finish_button);
        automatic_finish_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                //save spinner inputs
                String chord_spinner = cp_spinner.getSelectedItem().toString();
                String chord_spinner2 = cp_spinner2.getSelectedItem().toString();
                String chord_spinner3 = cp_spinner3.getSelectedItem().toString();
                String chord_spinner4 = cp_spinner4.getSelectedItem().toString();
                String harmony_spinner = noh_spinner.getSelectedItem().toString();


                globalVariable.getAutomaticArray()[0] = harmony_spinner;
                globalVariable.getAutomaticArray()[1] = chord_spinner;
                globalVariable.getAutomaticArray()[2] = chord_spinner2;
                globalVariable.getAutomaticArray()[3] = chord_spinner3;
                globalVariable.getAutomaticArray()[4] = chord_spinner4;



                //Writing data to the other device
                for(int i=0; i<4; ++i){
                    System.out.println("Automatic Output: " + globalVariable.getInitialInputsArray()[i]);
                    globalVariable.getmBluetoothConnection().write(globalVariable.getInitialInputsArray()[i].getBytes(Charset.defaultCharset()));
                    globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));

                }
                for(int i=0; i<5; ++i){
                    System.out.println("Automatic Output: " + globalVariable.getAutomaticArray()[i]);
                    globalVariable.getmBluetoothConnection().write(globalVariable.getAutomaticArray()[i].getBytes(Charset.defaultCharset()));
                    globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));
                }

                Intent intent = new Intent(AutomaticActivity.this, StartSingingActivity.class);
                startActivity(intent);
            }
        });





    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle_automatic.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(AutomaticActivity.this, HomePageActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_initial_inputs) {
            Intent intent = new Intent(AutomaticActivity.this, InitialInputActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_modes) {
            Intent intent = new Intent(AutomaticActivity.this, ModeOfOperationActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.nav_automatic) {
            return true;
        } else if (item.getItemId() == R.id.nav_manual) {
            Intent intent = new Intent(AutomaticActivity.this, ManualActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_manual) {
            Intent intent = new Intent(AutomaticActivity.this, StartSingingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_faq) {
            Intent intent = new Intent(AutomaticActivity.this, FAQActivity.class);
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

