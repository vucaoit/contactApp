package com.example.danhbadienthoai;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
private ArrayList<Contact> contacts;
private Context context;
private LayoutInflater layoutInflater;
private Activity activity;
    public ContactAdapter(ArrayList<Contact> contacts, Context context,Activity activity) {
        this.contacts = contacts;
        this.activity=activity;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.items_contact, null);
        }
        TextView tv_alarm = (TextView) convertView.findViewById(R.id.txt_name_contact);
        TextView tv_phone = (TextView) convertView.findViewById(R.id.txt_phone_contact);
        Button btn_add = (Button) convertView.findViewById(R.id.btn_delete);

        Contact contact = this.contacts.get(position);
        tv_alarm.setText(contact.getName());
        tv_phone.setText(contact.getPhoneNumber());
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("DELETE");
                alertDialog.setMessage("Do you want delete this contact");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new ContactController().deleteContact(context,contacts.get(position));
                                contacts.remove(contacts.get(position));
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });
        return convertView;
    }
    public void setListContact(ArrayList<Contact> contact){
        this.contacts=contact;
        notifyDataSetChanged();
    }
}
