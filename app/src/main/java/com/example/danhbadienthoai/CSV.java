package com.example.danhbadienthoai;

import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CSV {
    public ArrayList<Contact> readdata(Uri uri){
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            File gpxfile = new File(uri.getPath());
            FileReader writer = new FileReader(gpxfile);
            System.out.println(gpxfile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(new File("/storage/emulated/0/Download/CONTACT1218832543.csv"));
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }
    public void exportfile(ArrayList<Contact> contacts){
        String imageFileName = "CONTACT";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        try {
            File image = File.createTempFile(
                    imageFileName,  // prefix
                    ".csv",         // suffix
                    storageDir      // directory
            );
            System.out.println(image.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(new File(image.getAbsolutePath()));
            for (Contact contact:contacts
            ) {
                fos.write((contact.getName()+","+contact.getPhoneNumber()).getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Da export");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
