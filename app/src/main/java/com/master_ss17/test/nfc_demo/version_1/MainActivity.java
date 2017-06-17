package com.master_ss17.test.nfc_demo.version_1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.master_ss17.test.nfc_demo.R;
import com.master_ss17.test.nfc_demo.core.Contact;
import com.master_ss17.test.nfc_demo.core.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dfritsch on 05.06.2017.
 */

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final String CONTACT_URI_KEY = "CONTACT_URI";
    public static final String CONTACT_NAME_KEY = "CONTACT_NAME";
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAdapter.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("NFC ist deaktiviert! Einstellungen öffnen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Ohne NFC geht es nicht!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        readContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Intent intent = new Intent(context, MainActivity.class);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                startActivity(intent);
                finish();
                break;
        }
    }

    private void readContacts() {
        ListView listView = (ListView) findViewById(R.id.listView);
        final List<Contact> contacts = getAndroidContacts();
        Collections.sort(contacts);

        // Eigenen Kontakt noch auslesen
        getOwnVCard(contacts);

        listView.setAdapter(new ContactAdapter(context, contacts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    /* Kurzer Codesnipsel zum ansehen, was in der VCard drinsteht
                    AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(contacts.get(position).uriPath, "r");
                    FileInputStream fis = fd.createInputStream();
                    byte[] buf = new byte[(int) fd.getDeclaredLength()];
                    fis.read(buf);
                    String VCard = new String(buf);*/
                    Intent intent = new Intent(context, VCardActivity.class);
                    intent.putExtra(CONTACT_URI_KEY, contacts.get(position).uriPath.toString());
                    intent.putExtra(CONTACT_NAME_KEY, contacts.get(position).contactName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<Contact> getAndroidContacts() {
        List<Contact> list = new ArrayList<>();

        Cursor cursor;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            return list;
        }

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Contact contact = new Contact();
                    String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    contact.contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contact.contactID = Integer.parseInt(contactID);

                    String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    contact.uriPath = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);

                    list.add(contact);
                }
            }
            cursor.close();
        }

        return list;
    }

    private void getOwnVCard(List<Contact> contacts) {
        Cursor profile = this.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if (profile != null && profile.moveToFirst()) {
            Contact contact = new Contact();
            String contactID = profile.getString(profile.getColumnIndex(ContactsContract.Contacts._ID));

            contact.contactName = profile.getString(profile.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.contactID = Long.parseLong(contactID);

            String lookupKey = profile.getString(profile.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            contact.uriPath = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);

            contacts.add(0, contact);
            profile.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
