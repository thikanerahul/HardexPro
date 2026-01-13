package com.example.hardexpro.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constants {
    public static String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public static String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() / 1000;
    }
}
