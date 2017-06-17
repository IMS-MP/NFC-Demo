package com.master_ss17.test.nfc_demo.core;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by dfritsch on 05.06.2017.
 */

public class Contact implements Comparable<Contact>{
    public static final String MIME_TYPE_VCARD = "text/x-vCard";
    public String contactName = "";
    public long contactID = 0;
    public Uri uriPath;

    @Override
    public int compareTo(@NonNull Contact otherContact) {
        return this.contactName.compareTo(otherContact.contactName);
    }
}
