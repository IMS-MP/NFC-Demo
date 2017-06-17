package com.master_ss17.test.nfc_demo.core;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.master_ss17.test.nfc_demo.R;
import com.master_ss17.test.nfc_demo.core.Contact;

import java.util.List;

/**
 * Created by dfritsch on 05.06.2017.
 */

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private List<Contact> data;

    public ContactAdapter(@NonNull Context context, @NonNull List<Contact> data) {
        super(context, R.layout.layout_contact, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        TextViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.layout_contact, parent, false);

            holder = new TextViewHolder((TextView) row.findViewById(R.id.contact_view));

            row.setTag(holder);
        } else {
            holder = (TextViewHolder) row.getTag();
        }

        String rowText = data.get(position).contactName;
        holder.textView.setText(rowText);

        return row;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class TextViewHolder {
        private final TextView textView;

        TextViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
