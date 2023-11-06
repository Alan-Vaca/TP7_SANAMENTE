package com.example.tp7_sanamente;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.ArrayList;

public class ExportarPdf {

    private static ActivityResultLauncher<String> requestPermissionLauncher = null;

    public ExportarPdf(ActivityResultLauncher<String> requestPermissionLauncher) {
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    public static boolean exportarAPdf(Context context, ArrayList<String> data, String nombreArchivo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }
        try {
           // File file = new File(context.getExternalFilesDir(null), nombreArchivo + ".pdf");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), nombreArchivo + ".pdf");
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            for (String item : data) {
                document.add(new Paragraph(item));
            }

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PdfExporter", "Error al exportar a PDF: " + e.getMessage());
            return false;
        }
    }
}
