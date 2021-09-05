package com.example.danhbadienthoai;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
private final int REQUEST=1;
private ListView lv;
private ContactAdapter contactAdapter;
private ArrayList<Contact> contacts;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    private static final String LOG_TAG = "AndroidExample";
private Button btn_del,btn_import,btn_export;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.lv_contact);
        btn_del=findViewById(R.id.btn_delete_All_SameContact);
        btn_import=findViewById(R.id.btn_import);
        btn_export=findViewById(R.id.btn_export);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST );
                System.out.println("DA GUI REQUEST");
            }
            else{
                doSomethings();
            }
        }
        else{
            doSomethings();
        }

    }
    public void doSomethings(){
        contacts=getContactList();
        Collections.sort(contacts,new ContactComparetor());
        contactAdapter = new ContactAdapter(contacts,getApplicationContext(),MainActivity.this);
        lv.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("DELETE");
                alertDialog.setMessage("Do you want delete all same contact");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new ContactController().deleteAllSameContact(getApplicationContext(),contacts);
                                contacts=getContactList();
                                contactAdapter.notifyDataSetChanged();
                                contactAdapter = new ContactAdapter(contacts,getApplicationContext(),MainActivity.this);
                                lv.setAdapter(contactAdapter);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_GRANTED
                ){
                    doBrowseFile();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            123);
                }

            }
        });
        btn_export.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                ) {
                    System.out.println("export click");
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                    File file = new File(dir, "myData.csv");
                    try {
                        String path = file.getAbsolutePath();
                        FileOutputStream f = new FileOutputStream(file);
                        PrintWriter pw = new PrintWriter(f);
                        for (Contact contect:contacts
                             ) {
                            pw.println(contect.getName()+","+contect.getPhoneNumber());
                        }
                        pw.flush();
                        pw.close();
                        f.close();
                        Toast.makeText(MainActivity.this, "export to "+path, Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_REQUEST_CODE_PERMISSION);
                    System.out.println("PERMISSION DENIED");
                }
            }
        });
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("CAODEV");
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doSomethings();
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to read your contact", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("request code : "+requestCode);
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null && data.getData().getPath().indexOf(".csv")>0)  {
                        System.out.println("this is data : "+data.getData().getPath());
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(new File(data.getData().getPath().replace("/document/raw:","")));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        DataInputStream in = new DataInputStream(fis);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine = "";
                        while (true) {
                            try {
                                if (!((strLine = br.readLine()) != null)) break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String[] parts = strLine.split(",");
                            Contact contact123 = new Contact(parts[0],parts[1]);
                            ContactController controller = new ContactController();
                            controller.Insert2Contacts(getApplicationContext(),contact123);
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        contacts=getContactList();
                        Collections.sort(contacts,new ContactComparetor());
                        contactAdapter = new ContactAdapter(contacts,getApplicationContext(),MainActivity.this);
                        lv.setAdapter(contactAdapter);
                        contactAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,"Added from file",Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(MainActivity.this,"Vui long chon file csv",Toast.LENGTH_LONG).show();
                }
                break;
            case 1001:
                if(resultCode==Activity.RESULT_OK){
                    System.out.println("OKE");
                    doSomethings();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @SuppressLint("Range")
    private ArrayList<Contact> getContactList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contact = new Contact(name,phoneNo);
                        contacts.add(contact);
                    }
                    pCur.close();
                }
            }
        }
        else {
            contacts.add(new Contact("khong co lien he nao","123456789"));
            return contacts;
        }
        if(cur!=null){
            cur.close();
        }
        return contacts;
    }

}