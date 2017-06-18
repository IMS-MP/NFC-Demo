package com.master_ss17.test.nfc_demo.version_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.master_ss17.test.nfc_demo.R;
import com.master_ss17.test.nfc_demo.core.Util;

public class VCardActivity extends Activity {
    /**
     * The Context
     */
    private final Context context = this;

    /**
     * The tag text
     */
    private String contactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sender);

        Intent intent = getIntent();
        contactUri = intent.getStringExtra(MainActivity.CONTACT_URI_KEY);

        TextView name = (TextView) findViewById(R.id.contact_name);
        name.setText(intent.getStringExtra(MainActivity.CONTACT_NAME_KEY));

        String contactNumber = Util.retrieveContactNumber(contactUri, this);
        TextView id = (TextView) findViewById(R.id.contact_phone);
        id.setText(contactNumber);

        sendFile();
    }

    private void sendFile() {

        // The NFC Adapter
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        // Check whether NFC is enabled on device
        if (!nfcAdapter.isEnabled()) {
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if (!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        } else {
            nfcAdapter.setBeamPushUris(new Uri[]{Uri.parse(contactUri)}, this);
        }
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }
}
