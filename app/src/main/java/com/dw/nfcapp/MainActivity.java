package com.dw.nfcapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    public static EditText et_text;
    private Button btn_write,btn_read;
    private NfcAdapter mNfcAdapterMain;
    private boolean isWrite = false;
    private FrameLayout frame_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        et_text=findViewById(R.id.et_text);
        btn_write=findViewById(R.id.btn_write);
        btn_read=findViewById(R.id.btn_read);
        frame_main=findViewById(R.id.frame_main);
        mNfcAdapterMain = NfcAdapter.getDefaultAdapter(this);

        initNFC();
        clickEvent();
    }

    private void clickEvent(){
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_write.setBackgroundResource(R.drawable.bg_button);
                btn_write.setTextColor(getResources().getColor(R.color.white));
                btn_read.setBackgroundResource(R.drawable.bg_button_blank);
                btn_read.setTextColor(getResources().getColor(R.color.colorAccent));

                if(checkNFC()==true) {
                    Intent intent = new Intent(MainActivity.this, NFCWriteFragment.class);
                    startActivity(intent);
                }
            }
        });

        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_write.setBackgroundResource(R.drawable.bg_button_blank);
                btn_write.setTextColor(getResources().getColor(R.color.colorAccent));
                btn_read.setBackgroundResource(R.drawable.bg_button);
                btn_read.setTextColor(getResources().getColor(R.color.white));

                if(checkNFC()==true) {
                    Intent intent = new Intent(MainActivity.this, NFCReadFragment.class);
                    startActivity(intent);
                }

            }
        });


    }


    private boolean checkNFC(){
        boolean f=false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {

        } else {
            if (getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_NFC)) {
                //do processing
                f=true;
            }else {
                f=false;
                Toast.makeText(this, "NFC not available on this Device !", Toast.LENGTH_SHORT).show();
            }
        }
        return f;
    }

    private void initNFC(){

        mNfcAdapterMain = NfcAdapter.getDefaultAdapter(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapterMain!= null)
            mNfcAdapterMain.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    /*@Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapterMain!= null)
            mNfcAdapterMain.disableForegroundDispatch(this);
    }*/
}
