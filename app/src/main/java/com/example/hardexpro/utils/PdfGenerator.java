package com.example.hardexpro.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.example.hardexpro.data.model.CartItem;
import com.example.hardexpro.data.model.Transaction;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfGenerator {

    public static File generateInvoice(Context context, Transaction transaction, List<CartItem> cartItems) {
        String fileName = "Invoice_" + transaction.getInvoiceNumber() + ".pdf";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            // Title
            Paragraph title = new Paragraph("HARDEX PRO - INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Spacer

            // Shop Details (Mocked for now)
            document.add(new Paragraph("Hardex Pro Hardware Shop", labelFont));
            document.add(new Paragraph("GST: 27AAAAA0000A1Z5", normalFont));
            document.add(new Paragraph("Contact: +91 9876543210", normalFont));
            document.add(new Paragraph(" ")); // Spacer

            // Bill Details
            document.add(new Paragraph("Invoice No: " + transaction.getInvoiceNumber(), normalFont));
            document.add(new Paragraph("Date: " + transaction.getDate(), normalFont));
            document.add(new Paragraph("Customer: " + transaction.getCustomerName(), normalFont));
            document.add(new Paragraph(" ")); // Spacer

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Item", labelFont)));
            table.addCell(new PdfPCell(new Phrase("Qty", labelFont)));
            table.addCell(new PdfPCell(new Phrase("Price", labelFont)));
            table.addCell(new PdfPCell(new Phrase("Total", labelFont)));

            for (CartItem item : cartItems) {
                table.addCell(new PdfPCell(new Phrase(item.getItem().getName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getItem().getSellingPrice()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getTotal()), normalFont)));
            }
            document.add(table);

            // Totals
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Subtotal: ₹ " + transaction.getSubtotal(), labelFont));
            document.add(new Paragraph("GST: ₹ " + transaction.getTax(), labelFont));
            document.add(
                    new Paragraph("Grand Total: ₹ " + String.format("%.2f", transaction.getTotalAmount()), titleFont));
            document.add(
                    new Paragraph("Amount Paid: ₹ " + String.format("%.2f", transaction.getAmountPaid()), labelFont));
            document.add(new Paragraph(
                    "Balance Due: ₹ "
                            + String.format("%.2f", (transaction.getTotalAmount() - transaction.getAmountPaid())),
                    labelFont));

            document.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sharePdf(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Share Invoice"));
    }
}
