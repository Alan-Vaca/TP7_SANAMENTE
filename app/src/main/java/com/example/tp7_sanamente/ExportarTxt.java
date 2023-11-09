package com.example.tp7_sanamente;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExportarTxt {
    public static boolean generarTxt(Context context, ArrayList<String> data, String nombreArchivo) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fechaHoraActual = formatoFecha.format(new Date());
            File file = new File(context.getExternalFilesDir(null), nombreArchivo + "_" + fechaHoraActual + ".txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter(fos);

            for (String item : data) {
                writer.println(item);
            }

            writer.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
