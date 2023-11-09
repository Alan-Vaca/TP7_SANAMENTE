package com.example.tp7_sanamente;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExportarPdf {


    public static boolean exportarAPdf(Context context, ArrayList<String> data, String nombreArchivo) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fechaHoraActual = formatoFecha.format(new Date());
        String nombrePDF = nombreArchivo + "_" + fechaHoraActual + ".pdf";

        try {
            File file = new File(context.getExternalFilesDir(null), nombrePDF);
           // File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), nombreArchivo + ".pdf");
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);


            for (String item : data) {
                document.add(new Paragraph(item));
            }

            document.close();


            String path = String.valueOf(file);

            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Necesario para conceder permisos de lectura a otras aplicaciones

            context.startActivity(intent);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PdfExporter", "Error al exportar a PDF: " + e.getMessage());
            return false;
        }





    }
}
