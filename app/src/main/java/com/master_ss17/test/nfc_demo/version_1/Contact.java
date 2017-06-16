package com.master_ss17.test.nfc_demo.version_1;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by dfritsch on 05.06.2017.
 */

class Contact implements Comparable<Contact>{
    static final String MIME_TYPE_VCARD = "text/x-vCard";
    String contactName = "";
    long contactID = 0;
    Uri uriPath;

    @Override
    public int compareTo(@NonNull Contact otherContact) {
        return this.contactName.compareTo(otherContact.contactName);
    }
}
