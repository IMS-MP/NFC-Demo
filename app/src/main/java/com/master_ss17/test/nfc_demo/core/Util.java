package com.master_ss17.test.nfc_demo.core;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.master_ss17.test.nfc_demo.R;

/**
 * Created by dfritsch on 18.06.2017.
 */

public class Util {

    public static String retrieveContactNumber(String contactUri, Activity activity) {
        String contactNumber = "Keine Nummer vorhanden";

        // getting contacts ID
        Cursor cursorID = activity.getContentResolver().query(Uri.parse(contactUri),
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID == null) {
            return contactNumber;
        }

        String contactID = "";
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",
                new String[]{contactID},
                null);

        if (cursorPhone == null) {
            return contactNumber;
        }

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        return contactNumber;
    }
}
