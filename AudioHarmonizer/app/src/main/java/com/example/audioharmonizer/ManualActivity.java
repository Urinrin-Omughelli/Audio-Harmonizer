package com.example.audioharmonizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ManualActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button manual_finish_button, generate_new_button;
    private NavigationView navigationView;
    public DrawerLayout drawerLayout_manual;
    public ActionBarDrawerToggle actionBarDrawerToggle_manual;
    BluetoothAdapter mBlueAdapter;
    public int manualArrayLength;
    public String delim = ";";
    public int numberHarmonyToSend;
    public int numberNotesToSend;

    private Handler handler = new Handler();
    int batteryLevel = 0;
    ProgressBar progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.getManualArrayList().clear();

        initializeNavBar();

        //********************************bluetooth***********************************
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBlueAdapter.isEnabled()){
            showToast("You must turn bluetooth back on");
            Intent intent = new Intent(ManualActivity.this, Bluetooth2Activity.class);
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

        EditText number_of_harmonies = (EditText) findViewById(R.id.number_of_harmonies);
        EditText notes_per_harmony = (EditText) findViewById(R.id.notes_per_harmony);

       // manual_finish_button.setEnabled(false);

        generate_new_button = (Button)findViewById(R.id.generate_new);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.linear_inside_scroll);
        //ScrollView myScrollview = findViewById(R.id.scrollView_manual);

        generate_new_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                   // manual_finish_button.setEnabled(true);

                    if(Integer.parseInt(notes_per_harmony.getText().toString())>50){
                        showToast("You cannot add more than 50 notes");
                    }else if(Integer.parseInt(number_of_harmonies.getText().toString())>3){
                        showToast("You cannot add more than 3 harmonies");
                    }else {
                        //generate_new_button.setEnabled(false);
                        numberHarmonyToSend = Integer.parseInt(number_of_harmonies.getText().toString());


                        //creating the length array{
                        final ArrayList<String> lengthArray = new ArrayList<String>();
                        for (double i = 0.5; i <= Integer.parseInt(globalVariable.getBeatsPerMeasure()); i += 0.5) {
                            lengthArray.add(String.valueOf(i));
                        }

                        numberHarmonyToSend = Integer.parseInt(number_of_harmonies.getText().toString());
                        numberNotesToSend = Integer.parseInt(notes_per_harmony.getText().toString());


                        int NumberOfHarmonies = Integer.parseInt(number_of_harmonies.getText().toString());
                        int count = 1;
                        int k = 1;

                        manualArrayLength = numberHarmonyToSend * numberNotesToSend * 3;

                        while (NumberOfHarmonies != 0) {

                            String num = Integer.toString(count);
                            TextView HarmonyNumber = new TextView(ManualActivity.this);
                            HarmonyNumber.setText("Harmony " + num);
                            HarmonyNumber.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
                            LinearLayout.LayoutParams tvHarmonyNumber = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            tvHarmonyNumber.setMargins(dpToPx(20), dpToPx(0), dpToPx(20), dpToPx(0));
                            HarmonyNumber.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0));
                            HarmonyNumber.setLayoutParams(tvHarmonyNumber);
                            myLayout.addView(HarmonyNumber);


                            TextView tvNote = new TextView(ManualActivity.this);
                            tvNote.setText("Note, Octave, and Length Blocks:");
                            tvNote.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
                            LinearLayout.LayoutParams tvNoteLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            tvNoteLayout.setMargins(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(0));
                            tvNote.setLayoutParams(tvNoteLayout);
                            //tvNote.setPadding(0, 0, 0, 0);
                            myLayout.addView(tvNote);

                            for (int j = 0; j < Integer.parseInt(notes_per_harmony.getText().toString()); j++) {
                                Spinner spinnerNote = new Spinner(ManualActivity.this);
                                ArrayAdapter<String> myNoteAdapter = new ArrayAdapter<String>(ManualActivity.this,
                                        android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.notes));
                                myNoteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(dpToPx(150), dpToPx(0), dpToPx(150), 0);
                                spinnerNote.setLayoutParams(lp);
                                spinnerNote.setGravity(Gravity.RIGHT);
                                spinnerNote.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0));
                                spinnerNote.setId(k + j + 1);
                                spinnerNote.setAdapter(myNoteAdapter);
                                myLayout.addView(spinnerNote);

                                Spinner spinnerOctave = new Spinner(ManualActivity.this);
                                ArrayAdapter<String> myOctaveAdapter = new ArrayAdapter<String>(ManualActivity.this,
                                        android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.octave));
                                myOctaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp3.setMargins(dpToPx(150), dpToPx(0), dpToPx(150), 0);
                                spinnerOctave.setLayoutParams(lp3);
                                spinnerOctave.setGravity(Gravity.RIGHT);
                                spinnerOctave.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0));
                                spinnerOctave.setId(k + j + 200);
                                spinnerOctave.setAdapter(myOctaveAdapter);
                                myLayout.addView(spinnerOctave);


                                Spinner spinnerLength = new Spinner(ManualActivity.this);
                                ArrayAdapter<String> myLengthAdapter = new ArrayAdapter<String>(ManualActivity.this,
                                        android.R.layout.simple_list_item_1, lengthArray);
                                myLengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp2.setMargins(dpToPx(150), dpToPx(0), dpToPx(150), dpToPx(10));
                                spinnerLength.setLayoutParams(lp2);
                                spinnerLength.setGravity(Gravity.RIGHT);
                                spinnerNote.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0));
                                spinnerLength.setId(k + j + 100);
                                spinnerLength.setAdapter(myLengthAdapter);
                                myLayout.addView(spinnerLength);

                            }

                            NumberOfHarmonies--;
                            count++;
                            k *= 10;
                        }

                        Button dummyButton = new Button(ManualActivity.this);
                        dummyButton.setText("dummy");
                        dummyButton.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK);
                        dummyButton.setEnabled(false);
                        LinearLayout.LayoutParams tvDummyButton= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        tvDummyButton.setMargins(dpToPx(20), dpToPx(0), dpToPx(20), dpToPx(0));
                        dummyButton.setLayoutParams(tvDummyButton);
                        dummyButton.setBackgroundColor(getResources().getColor(R.color.black));
                        dummyButton.setTextColor(getResources().getColor(R.color.black));
                        myLayout.addView(dummyButton);

                    }
                }

        });


        manual_finish_button = (Button)findViewById(R.id.finish_button);
        manual_finish_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int NumberOfHarmonies = Integer.parseInt(number_of_harmonies.getText().toString());
                int count = 1;
                int k = 1;

                while (NumberOfHarmonies != 0) {
                    for (int j = 0; j < Integer.parseInt(notes_per_harmony.getText().toString()); j++) {
                        Spinner note_spinner = (Spinner) findViewById(k + j + 1);
                        String save = note_spinner.getSelectedItem().toString();
                        globalVariable.getManualArrayList().add(save);

                        Spinner octave_spinner = (Spinner) findViewById(k+j+200);
                        String saveOctave = octave_spinner.getSelectedItem().toString();
                        globalVariable.getManualArrayList().add(saveOctave);


                        Spinner length_spinner = (Spinner) findViewById(k+j+100);
                        String saveLength = length_spinner.getSelectedItem().toString();
                        globalVariable.getManualArrayList().add(saveLength);

                    }
                    NumberOfHarmonies--;
                    count++;
                    k*=10;
                }


                //Write out to esp32
                for(int i=0; i<4; ++i){
                    System.out.println("Manual Output: " + globalVariable.getInitialInputsArray()[i]);
                    globalVariable.getmBluetoothConnection().write(globalVariable.getInitialInputsArray()[i].getBytes(Charset.defaultCharset()));
                    globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));

                }

                //Here add length of manual array
                globalVariable.getmBluetoothConnection().write(Integer.toString(manualArrayLength).getBytes(Charset.defaultCharset()));
                globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));

                globalVariable.getmBluetoothConnection().write(Integer.toString(numberHarmonyToSend).getBytes(Charset.defaultCharset()));
                globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));

                globalVariable.getmBluetoothConnection().write(Integer.toString(numberNotesToSend).getBytes(Charset.defaultCharset()));
                globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));


                for(String i : globalVariable.getManualArrayList()){
                    System.out.println("Manual Output: " + i);
                    globalVariable.getmBluetoothConnection().write(i.getBytes(Charset.defaultCharset()));
                    globalVariable.getmBluetoothConnection().write(delim.getBytes(Charset.defaultCharset()));

                }

                Intent intent = new Intent(ManualActivity.this, StartSingingActivity.class);
                startActivity(intent);
            }
        });

    }

    public void initializeNavBar(){
        drawerLayout_manual = findViewById(R.id.my_drawer_layout_manual);
        actionBarDrawerToggle_manual = new ActionBarDrawerToggle(this, drawerLayout_manual, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout_manual.addDrawerListener(actionBarDrawerToggle_manual);
        actionBarDrawerToggle_manual.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationview_id_manual);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem = navigationView.getMenu().getItem(4).setChecked(true);
        onNavigationItemSelected(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle_manual.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(ManualActivity.this, HomePageActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_initial_inputs) {
            Intent intent = new Intent(ManualActivity.this, InitialInputActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_modes) {
            Intent intent = new Intent(ManualActivity.this, ModeOfOperationActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.nav_automatic) {
            Intent intent = new Intent(ManualActivity.this, AutomaticActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_manual) {
            return true;
        } else if (item.getItemId() == R.id.nav_start_singing) {
            Intent intent = new Intent(ManualActivity.this, StartSingingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_faq) {
            Intent intent = new Intent(ManualActivity.this, FAQActivity.class);
            startActivity(intent);
        } else {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
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