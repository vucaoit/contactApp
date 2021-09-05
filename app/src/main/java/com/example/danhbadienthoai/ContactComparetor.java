package com.example.danhbadienthoai;

import java.util.Comparator;

public class ContactComparetor implements Comparator<Contact> {

    @Override
    public int compare(Contact o, Contact t1) {
        return o.getName().compareTo(t1.getName());
    }
}
