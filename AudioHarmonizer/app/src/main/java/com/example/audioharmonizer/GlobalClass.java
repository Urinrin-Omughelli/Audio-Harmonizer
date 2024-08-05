package com.example.audioharmonizer;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;

public class GlobalClass extends Application{

    private static GlobalClass instance;

    private String NameOfSong = null;
    private String BeatsPerMeasure = null;
    private String BeatsPerMinute = null;
    private BluetoothDevice device;
    private String[] InitialInputsArray = {"Filler", "Filler", "Filler", "Filler"};
    private String[] AutomaticArray = {"Filler", "Filler", "Filler", "Filler", "Filler"};
    ArrayList<String> ManualArrayList = new ArrayList<String>();
    private String[] ListOfRecordings;
    private BluetoothConnectionService mBluetoothConnection;
    private String BatteryLevel = "100";

    public BluetoothDevice getDevice() {
        return device;
    }


    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }


    public static GlobalClass getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }


    public void setDevice(BluetoothDevice aDevice) {
        device = aDevice;
    }


    public String getNameOfSong() {
        return NameOfSong;
    }

    public void setNameOfSong(String aName) {
        NameOfSong = aName;
    }

    public String getBeatsPerMeasure() {
        return BeatsPerMeasure;
    }

    public void setBeatsPerMeasure(String aBeatsPerMeasure) {
        BeatsPerMeasure = aBeatsPerMeasure;
    }

    public String getBeatsPerMinute() {
        return BeatsPerMinute;
    }

    public void setBeatsPerMinute(String aBeatsPerMinute) {
        BeatsPerMinute = aBeatsPerMinute;
    }

    public String[] getInitialInputsArray() {
        return InitialInputsArray;
    }

    public void setInitialInputsArray(String[] InitialInputsArray) {
        this.InitialInputsArray = InitialInputsArray;
    }

    public String[] getAutomaticArray() {
        return AutomaticArray;
    }

    public void setAutomaticArray(String[] AutomaticArray) {
        this.AutomaticArray = AutomaticArray;
    }

    public ArrayList<String> getManualArrayList() {
        return ManualArrayList;
    }

    public void setManualArrayList(ArrayList<String>  ManualArrayList) {
        this.ManualArrayList = ManualArrayList;
    }

    public String[] getListOfRecordings() {
        return ListOfRecordings;
    }

    public void setListOfRecordings(String[] ListOfRecordings) {
        this.ListOfRecordings = ListOfRecordings;
    }

    public BluetoothConnectionService getmBluetoothConnection() {
        return mBluetoothConnection;
    }

    public void setmBluetoothConnection(BluetoothConnectionService mBluetoothConnection) {
        this.mBluetoothConnection = mBluetoothConnection;
    }

    public String getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(String mBatteryLevel) {
        this.BatteryLevel = mBatteryLevel;
    }
}