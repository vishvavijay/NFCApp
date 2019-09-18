package com.dw.nfcapp;


import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class NFCReadFragment extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    private TextView mTvMessage;
    private RelativeLayout bg_nfcdata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_read_fragment);
        mTvMessage = findViewById(R.id.tv_message);
        bg_nfcdata=findViewById(R.id.bg_nfcdata);
        bg_nfcdata.setBackgroundResource(R.color.white);
        initNFC();

    }


    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
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
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d("fjsdjk", "onNewIntent: " + intent.getAction());

        if (tag != null) {
//            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

//            mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
//            mNfcReadFragment.onNfcDetected(ndef);
            onNfcDetected(ndef);
        }
    }


    public void onNfcDetected(Ndef ndef){
        if(ndef!=null) {
            readFromNFC(ndef);
        }
    }


    private void readFromNFC(Ndef ndef) {
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if(ndefMessage!=null) {
                String message = new String(ndefMessage.getRecords()[0].getPayload());
                Log.d("fgfdg", "readFromNFC: " + message);
                mTvMessage.setText(message);
                bg_nfcdata.setBackgroundResource(R.drawable.bg_button_blank);
                ndef.close();
            }

        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
    }



}
