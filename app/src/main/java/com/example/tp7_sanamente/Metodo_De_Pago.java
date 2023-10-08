package com.example.tp7_sanamente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Metodo_De_Pago extends AppCompatActivity {

    EditText numeroTarjeta, nombreTarjeta, fecha, codSeguridad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);

        numeroTarjeta = (EditText)findViewById(R.id.mp_et_numeroTarjeta);
        nombreTarjeta = (EditText)findViewById(R.id.mp_et_nombre);
        fecha = (EditText)findViewById(R.id.mp_et_fecha);
        codSeguridad = (EditText)findViewById(R.id.mp_et_codigoSeguridad);
    }




    public void MetodoDePagoFinalizarCompra(View view) {
        boolean isValid = true;

        // valida que el numero de la tarjeta contenga 16 dígitos
        if(numeroTarjeta.getText().toString().length() != 16) {
            numeroTarjeta.setError("El número de tarjeta debe contener 16 dígitos");
            isValid = false;
        } else {
            numeroTarjeta.setError(null); // Limpia el error
        }

        // valida que el nombre de la tarjeta no contenga más de 24 caracteres
        if(nombreTarjeta.getText().toString().length() > 24) {
            nombreTarjeta.setError("El nombre de tarjeta no puede exceder los 24 caracteres");
            isValid = false;
        } else {
            nombreTarjeta.setError(null); // Limpia el error
        }

        // valida que el formato de la tarjeta tenga un formato correcto "mm/aa"
        String[] fechaArray = fecha.getText().toString().split("/");
        if(fechaArray.length != 2) {
            fecha.setError("El formato de fecha debe ser mm/aa");
            isValid = false;
        } else {
            int mes = Integer.parseInt(fechaArray[0]);
            int ano = Integer.parseInt(fechaArray[1]);
            if(mes < 1 || mes > 12 || ano < 21) {
                fecha.setError("Fecha de expiración inválida");
                isValid = false;
            } else {
                fecha.setError(null); // Limpia el error
            }
        }

        // valida que el código de seguridad solo sea de 3 dígitos
        if(codSeguridad.getText().toString().length() != 3) {
            codSeguridad.setError("El código de seguridad debe contener 3 dígitos");
            isValid = false;
        } else {
            codSeguridad.setError(null); // Limpia el error
        }

        if(isValid) {
            Intent metodoDePagoFinalizarCompra = new Intent(this, MiHistorial.class);
            startActivity(metodoDePagoFinalizarCompra);
        }
    }

    public void MetodoDePagoCancelarCompra(View view) {
        Intent metodoDePagoCancelarCompra = new Intent(this, MiCarritoCompras.class);
        startActivity(metodoDePagoCancelarCompra);
    }
}