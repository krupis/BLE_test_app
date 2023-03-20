package com.example.ble_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter BA;
    int REQUEST_ENABLE_BLUETOOTH = 0;
    CheckBox btCheckbox;


    //private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btCheckbox = findViewById(R.id.btCheckbox);
        BA = BluetoothAdapter.getDefaultAdapter();
        setListeners();

    }



/*
        BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (BA.isEnabled()) {
            Log.w("BLUETOOTH", "BLUETOOTH ENABLED \n");
        } else {
            Log.w("BLUETOOTH", "BLUETOOTH NOT ENABLED\n");
            Toast.makeText(this, "Enable the Bluetooth", Toast.LENGTH_SHORT).show();

        }
*/






    private void setListeners(){
        btCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IF CHECKBOX CHECKED
                if (((CompoundButton) view).isChecked()) {
                    System.out.println("Checked");
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
                        }
                    }
                }
                //IF CHECKBOX UNCHECKED
                else{
                    System.out.println("UnChecked");
                    if(BA.isEnabled()){
                        System.out.println("Disabling the Bluetooth \n");
                        BA.disable();
                        Toast.makeText(MainActivity.this,"Bluetooth disabled",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }




}