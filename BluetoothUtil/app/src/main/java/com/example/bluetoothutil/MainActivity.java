package com.example.bluetoothutil;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = this.getClass().getSimpleName();
    private BluetoothAdapter mBluetoothAdapter = null;
    TextView textViewStatus;
    private ListView mDevicesListView;
    private ArrayAdapter<String> mBTArrayAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive");

            String action = intent.getAction();
            boolean isDeviceDiscovered = false;

            if (intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_FOUND)) {
                isDeviceDiscovered = true;
                BluetoothDevice remoteDevice;
                remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                //showToast(context, "Discovered: " + remoteDevice.getName() + "  RSSI: " + rssi);
                mBTArrayAdapter.add("Discovered: " + remoteDevice.getName() + "  RSSI: " + rssi);
                mBTArrayAdapter.notifyDataSetChanged();
                Log.e(getClass().getSimpleName(), "Name: " + remoteDevice.getName());
                Log.e(getClass().getSimpleName(), "Address: " + remoteDevice.getAddress());
                Log.e(getClass().getSimpleName(), "RSSI: " + rssi);
            } else if (intent.getAction().equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                isDeviceDiscovered = false;
                Log.e(getClass().getSimpleName(), "Discovery Started...");
                textViewStatus.setText("Discovery Started");
            } else if (intent.getAction().equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (!isDeviceDiscovered) {
                    textViewStatus.setText("No device found");
                }
                textViewStatus.setText("Discovery Finished");
                Log.e(getClass().getSimpleName(), "Discovery Finished");
            }
        }
    };
    java.util.HashMap<String, String> mDeviceList = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStatus = findViewById(R.id.textViewStatus);
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDevicesListView = findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        //registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        //mReceiver = new BtReceiver();
        registerDiscoveryBroadcastReceiver();
    }

    private void registerDiscoveryBroadcastReceiver() {
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void bluetoothScanOn(View view) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityIntent.launch(enableBtIntent);
        } else {
            Toast.makeText(MainActivity.this, "Bluetooth already Enabled", Toast.LENGTH_SHORT).show();
        }
        listDevices();
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // The user picked a contact.
                    // The Intent's data Uri identifies which contact was selected.
                    Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            });

    private void listDevices() {
        mDeviceList.clear();
        mBluetoothAdapter.startDiscovery();
        Log.d(LOG_TAG, "registerReceiver");

    }

    private void showToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}
