package com.master_ss17.test.nfc_demo.version_1;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.master_ss17.test.nfc_demo.R;

import java.io.IOException;

public class VCardActivity extends Activity {
    private static final String TAG = VCardActivity.class.getSimpleName();

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

        retrieveContactNumber();

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

    private void retrieveContactNumber() {

        String contactNumber = "Keine Nummer vorhanden";

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(Uri.parse(contactUri),
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        String contactID = "";
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",
                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        TextView id = (TextView) findViewById(R.id.contact_phone);
        id.setText(contactNumber);
    }
}
