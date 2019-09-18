package com.dw.nfcapp;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.nio.charset.Charset;

public class NFCWriteFragment extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private TextView mTvMessage;
    private ProgressBar mProgress;
    private EditText et_text;
    private Button btn_save;

    public static final String TAG = NFCWriteFragment.class.getSimpleName();

    public static NFCWriteFragment newInstance() {
        return new NFCWriteFragment();
    }
    private ImageView iv_logo;

    private boolean save=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_write_fragment);

        initViews();
        initNFC();
        clickEvent();
    }

    private void initViews() {
        et_text=findViewById(R.id.et_nfcdata);
        mTvMessage =  findViewById(R.id.tv_message);
        mTvMessage.setVisibility(View.INVISIBLE);
        mProgress =  findViewById(R.id.progress);
        mProgress.setVisibility(View.GONE);
        iv_logo=findViewById(R.id.iv_logo);
        btn_save=findViewById(R.id.btn_save);

    }

    private void clickEvent(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save=true;
               showClickedView();
            }
        });

        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               renewView();
            }
        });

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
        if(save==true){
            writeToNfc(ndef,et_text.getText().toString());
            save=false;
        }


    }


    private void writeToNfc(Ndef ndef, String message){
        mProgress.setVisibility(View.VISIBLE);
        mTvMessage.setText(getString(R.string.message_write_progress));
        if (ndef != null) {

            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", message.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                showSuccessView();

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                mTvMessage.setText(getString(R.string.message_write_error));

            } finally {
                mProgress.setVisibility(View.GONE);
            }

        }
    }

    private void renewView(){
        mTvMessage.setVisibility(View.INVISIBLE);

        btn_save.setBackgroundResource(R.drawable.bg_button_blank);
        btn_save.setTextColor(getResources().getColor(R.color.colorAccent));
        iv_logo.setImageResource(R.drawable.nfc_icon);
    }

    private void showClickedView(){
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setTextColor(getResources().getColor(R.color.gray));
        mTvMessage.setText(getString(R.string.message_tap_tag));

        btn_save.setBackgroundResource(R.drawable.bg_button);
        btn_save.setTextColor(getResources().getColor(R.color.white));
        iv_logo.setImageResource(R.drawable.nfc_icon);
    }

    private void showSuccessView(){
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setTextColor(getResources().getColor(R.color.colorAccent));
        mTvMessage.setText(getString(R.string.message_write_success));

        btn_save.setBackgroundResource(R.drawable.bg_button);
        btn_save.setTextColor(getResources().getColor(R.color.white));
        iv_logo.setImageResource(R.drawable.succes_icon);
    }


}
