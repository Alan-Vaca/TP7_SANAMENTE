package com.example.tp7_sanamente;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.Locale;


public class Filtros_Pedidos extends AppCompatActivity {

    EditText fechaDesde, horaDesde, fechaHasta, horaHasta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_pedidos);

        fechaDesde = (EditText)findViewById(R.id.fp_et_fechaDesde);
        horaDesde = (EditText)findViewById(R.id.fp_et_horaDesde);
        fechaHasta = (EditText)findViewById(R.id.fp_et_fechaHasta);
        horaHasta = (EditText)findViewById(R.id.fp_et_horaHasta);
    }

    public void AplicarFiltroHistorial(View view) {
        //AplicarFiltroPedido(view);
        boolean isValid = true;

        // Validar que fechaDesde pertenezca a un formato dd/mm/aaaa. Si no, mostrar mensaje de error
        String fechaDesdeStr = fechaDesde.getText().toString();
        if (!fechaDesdeStr.isEmpty() && !isValidDateFormat(fechaDesdeStr, "dd/MM/yyyy")) {
            fechaDesde.setError("Formato de fecha inválido");
            isValid = false;
        }
        else{
            fechaDesde.setError(null);
        }

        // Validar que horaDesde pertenezca a un formato 00:00. Si no, mostrar mensaje de error
        String horaDesdeStr = horaDesde.getText().toString();
        if (!horaDesdeStr.isEmpty() && !isValidDateFormat(horaDesdeStr, "HH:mm")) {
            horaDesde.setError("Formato de hora inválido");
            isValid = false;
        }
        else{
            horaDesde.setError(null);
        }

        // Validar que fechaHasta pertenezca a un formato dd/mm/aaaa. Si no, mostrar mensaje de error
        String fechaHastaStr = fechaHasta.getText().toString();
        if (!fechaHastaStr.isEmpty() && !isValidDateFormat(fechaHastaStr, "dd/MM/yyyy")) {
            fechaHasta.setError("Formato de fecha inválido");
            isValid = false;
        }
        else{
            fechaHasta.setError(null);
        }

        // Validar que horaHasta pertenezca a un formato 00:00. Si no, mostrar mensaje de error
        String horaHastaStr = horaHasta.getText().toString();
        if (!horaHastaStr.isEmpty() && !isValidDateFormat(horaHastaStr, "HH:mm")) {
            horaHasta.setError("Formato de hora inválido");
            isValid = false;
        }
        else{
            horaHasta.setError(null);
        }

        if(isValid) {
        Intent AplicarFiltroHistorial = new Intent(this, MiHistorial.class);
        startActivity(AplicarFiltroHistorial);
        }
    }

    public void AplicarFiltroPedido(View view) {

        Intent AplicarFiltroPedido = new Intent(this, Mis_Pedidos.class);
        startActivity(AplicarFiltroPedido);

    }

    // Función para validar el formato de fecha y hora
    private boolean isValidDateFormat(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setLenient(false); //es estricto al momento de analizar la fecha
        try {
            sdf.parse(value);// Analiza la cadena "value" en un objeto Date utilizando el formato de fecha definido en "sdf"
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

    /*SE APLICARA DEPENDIENDO SI ES UN PEDIDO O UN HISTORIAL*/
    /*

 */