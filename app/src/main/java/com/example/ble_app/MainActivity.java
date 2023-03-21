package com.example.ble_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    int REQUEST_ENABLE_BLUETOOTH = 11;
    private BluetoothAdapter BA;



    CheckBox btCheckbox;
    Button scanButton;

    private le_device_list_adapter custom_test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btCheckbox = findViewById(R.id.btCheckbox);
        scanButton = findViewById(R.id.bleScanbtn);
        // get default bluetooth adapter
        BA = BluetoothAdapter.getDefaultAdapter();
        checkBluetoothState();
        setListeners();
        checkCoarseLocationPermission();





    }



    @Override
    protected void onPause(){
        Log.e("MAIN", "ONPAUSE ");
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }

    @Override
    protected void onResume(){
        Log.e("MAIN", "ONRESUME ");
        super.onResume();

        // register dedicated receiver for some bluetooth actions
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }












    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        Log.e("CALLBACK", "onActivityResult ");
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            checkBluetoothState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.e("CALLBACK", "onRequestPermissionsResult ");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch(requestCode){
            case REQUEST_ACCESS_COARSE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"ACCESS CORASE LOCATION ALLOWED, YOU CAN SCAN DEVICES",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(MainActivity.this,"ACCESS CORASE LOCATION IS NOT ALLOWED, YOU CANOT SCAN DEVICES",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private boolean checkCoarseLocationPermission(){
        Log.e("CHECK", "CHECKING ACCESS_COARSE_LOCATION ");
/*
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.e("CHECK", "COARSE LOCATION PERMISSION DENIED ");
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return false;
            }
        }
        else{
            Log.e("CHECK", "COARSE LOCATION PERMISSION ALLOWED ");
            return true;

        }

        return false;

 */


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        }
        else{
            return true;
        }


    }

    private void checkBluetoothState(){
        Log.e("CHECK", "CALLING CHECK BLUETOOTH STATE");

        if(BA == null){
            Toast.makeText(MainActivity.this,"BLUETOOTH IS NOT SUPPORTED ON YOUR DEVICE",Toast.LENGTH_SHORT).show();
        }
        else{
            if(BA.isEnabled()){
                Log.e("CHECK", "BLUETOOTH IS ALREADY ENABLED");
                if(BA.isDiscovering()){
                    Toast.makeText(MainActivity.this,"Device is discovering.......",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"Bluetooth is already enabled",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Log.e("CHECK", "BLUETOOTH IS NOT ENABLED");
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= 31) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
                        return;
                    }
                }

                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                Toast.makeText(MainActivity.this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void setListeners(){
        Log.e("MAIN", "SETTING LISTENERS");
        /*
        btCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IF CHECKBOX CHECKED
                if (((CompoundButton) view).isChecked()) {
                    //System.out.println("Checked");
                    Log.e("BLE", "Checked ");
                    if (BA == null) {
                        Toast.makeText(MainActivity.this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                            if (Build.VERSION.SDK_INT >= 31) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
                                return;
                            }
                        }
                        if(!BA.isEnabled()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                            Toast.makeText(MainActivity.this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Bluetooth already enabled", Toast.LENGTH_SHORT).show();
                            //BA = BluetoothAdapter.getDefaultAdapter();
                        }
                    }
                }
                //IF CHECKBOX UNCHECKED
                else{


                    //System.out.println("UnChecked");
                    Log.e("BLE", "Unchecked ");
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= 31) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, 100);
                            return;
                        }
                    }
                    BluetoothAdapter BA2 = BluetoothAdapter.getDefaultAdapter();
                    if(BA2.isEnabled()){
                        //System.out.println("Disabling the Bluetooth \n");
                        Log.e("BLE", "Disabling the Bluetooth: ");
                        BA2.disable();
                        Toast.makeText(MainActivity.this,"Bluetooth disabled",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

*/

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("SCAN BUTTON", "Clicked ");

                if(BA != null && BA.isEnabled()) {
                    Log.e("SCAN BUTTON", "STARTING BLE DISCOVERY ");
                    // we check if course location must be asked
                    if(checkCoarseLocationPermission()){
                        BA.startDiscovery();
                    }

                }
                else{
                    checkBluetoothState();
                }


            }

        });




    }












    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e("CALLBACK", "onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //System.out.print("DEVICE FOUND=")
                Log.e("BroadcastReceiver", "DEVICE FOUND: ");
                System.out.println(deviceName);
            }
            else if(BA.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.e("BroadcastReceiver", "SCANNING BLUETOOTH DEVICES ");
            }
            else if(BA.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.e("BroadcastReceiver", "SCANNING IN PROGRESS");
            }



        }
    };





}




