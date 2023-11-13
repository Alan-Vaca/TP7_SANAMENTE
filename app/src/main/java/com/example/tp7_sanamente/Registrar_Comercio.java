package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import BaseDeDatos.Conexion;
import Entidad.Comercio;
import Entidad.Usuario;

public class Registrar_Comercio extends AppCompatActivity {

    TextView cuit,nombre,cierre,apertura, direccion, usuario_nombre, usuario_dni, usuario_apellido;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comercio);

        Usuario usuarioRecibido = getIntent().getParcelableExtra("usuarioInsert");
        user = usuarioRecibido;

        cuit = (EditText)findViewById(R.id.mod_c_usuario);
        nombre = (EditText)findViewById(R.id.r_com_nombre);
        cierre = (EditText)findViewById(R.id.r_com_cierre);
        apertura = (EditText)findViewById(R.id.r_com_apertura);
        direccion = (EditText)findViewById(R.id.r_com_direccion);

        usuario_nombre = (EditText)findViewById(R.id.r_com_nombre_user);
        usuario_dni = (EditText)findViewById(R.id.r_com_dni);
        usuario_apellido = (EditText)findViewById(R.id.r_com_apellido);

    }

    public void IngresarComercio(View view) {
        String dniStr = usuario_dni.getText().toString().trim();
        String cuitStr = cuit.getText().toString().trim();


        Comercio comercio = new Comercio();
        //Tengo que validar antes sino la app rompe
        if (!cuitStr.isEmpty()) {
            int test = Integer.parseInt(cuitStr);
            comercio.setCuit(test);
        }
        comercio.setNombreComercio(nombre.getText().toString());
        String horarios = (apertura.getText().toString()) + "-:-" + (cierre.getText().toString());
        comercio.setHorarios(horarios);
        //Tengo que validar antes sino la app rompe
        if (!dniStr.isEmpty()) {   user.setDNI(Integer.parseInt((usuario_dni.getText().toString())));  }
        user.setNombre(usuario_nombre.getText().toString());
        user.setApellido(usuario_apellido.getText().toString());
        user.setDireccion(direccion.getText().toString());

        comercio.setUsuarioAsociado(user);
        if(validarComercio(comercio)) {
            Toast toast = Toast.makeText(this, "CONSEGUIDO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            //new registrarComercio().execute(comercio);
        }
    }

    public boolean validarComercio(Comercio comercio) {
        Boolean isValid = true;
        //StringBuilder errorMessage = new StringBuilder("Complete o corrija los siguientes campos:\n");

        // Alert para las Validaciones
        AlertDialog.Builder builder = new AlertDialog.Builder(Registrar_Comercio.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_validacion, null);
        builder.setView(dialogView);

        final EditText mensajeValidacion = dialogView.findViewById(R.id.editTextValidacion);
        Button btnOK= dialogView.findViewById(R.id.btnOK);

        mensajeValidacion.setText("Complete los siguientes campos con valores válidos: ");
        final AlertDialog dialog = builder.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });




        if (comercio.getUsuarioAsociado().getDNI() <= 0) {
            mensajeValidacion.append("\nDNI");
           // errorMessage.append("- DNI\n");
            isValid = false;
        }
        // Se puede descomentar una vez se finalicen las pruebas de la app, para evitar retrasos
        else if(comercio.getUsuarioAsociado().getDNI() < 5000000 ||
                comercio.getUsuarioAsociado().getDNI() > 70000000)
        {
            mensajeValidacion.append("\nDNI inválido");
            isValid = false;
        }

        if (comercio.getUsuarioAsociado().getNombre().isEmpty()) {
            mensajeValidacion.append("\nNombre");
            isValid = false;
        }

        if (comercio.getUsuarioAsociado().getApellido().isEmpty()) {
            mensajeValidacion.append("\nApellido");
            isValid = false;
        }

        if (comercio.getCuit() <= 0) {
            mensajeValidacion.append("\nCUIT");
            isValid = false;
        }
       //  Se puede descomentar una vez se finalicen las pruebas de la app, para evitar retrasos
        else if(comercio.getCuit() < 2050000000)
        {
            mensajeValidacion.append("\nCUIT inválido");
            isValid = false;
        }


        if (comercio.getNombreComercio().isEmpty()) {
            mensajeValidacion.append("\nNombre comercio");
            isValid = false;
        }

        // Verificar que el horario sea válido (apertura menor que cierre)
        String[] horarios = comercio.getHorarios().split("-:-");


        if(horarios.length > 1) {
            if (horarios[0].isEmpty() || horarios[1].isEmpty()) {
                mensajeValidacion.append("\nHorarios");
                isValid = false;
            }
            if (!horarios[0].isEmpty() && !validarFormatoHorario(horarios[0])) {
                mensajeValidacion.append("\nApertura inválida");
                isValid = false;
            }

            if (!horarios[1].isEmpty() && !validarFormatoHorario(horarios[1])) {
                mensajeValidacion.append("\nCierre inválido");
                isValid = false;
            }

            String[] aperturaParts = horarios[0].split(":");
            String[] cierreParts = horarios[1].split(":");

            if (aperturaParts.length > 1 && cierreParts.length > 1) {
                int aperturaHoras = Integer.parseInt(aperturaParts[0]);
                int aperturaMinutos = Integer.parseInt(aperturaParts[1]);

                int cierreHoras = Integer.parseInt(cierreParts[0]);
                int cierreMinutos = Integer.parseInt(cierreParts[1]);


                if (aperturaHoras > cierreHoras || (aperturaHoras == cierreHoras && aperturaMinutos > cierreMinutos)) {
                    mensajeValidacion.append("\nHorarios incoherentes");
                    isValid = false;
                }
            }
        }
        else {
            mensajeValidacion.append("\nHorarios");
            isValid = false;
        }



        if (comercio.getUsuarioAsociado().getDireccion().isEmpty()) {
            mensajeValidacion.append("- Direccion\n");
            isValid = false;
        }

        if (!isValid) {

            dialog.show();

            //Toast toast = Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.TOP, 0, 200);
            //toast.show();
        }
        return isValid;
    }

    private boolean validarFormatoHorario(String horario) {
        // El horario debe tener el formato "00:00"
        String[] partes = horario.split(":");
        if (partes.length == 2) {
            try {
                int horas = Integer.parseInt(partes[0]);
                int minutos = Integer.parseInt(partes[1]);
                return horas >= 0 && horas <= 23 && minutos >= 0 && minutos <= 59;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public void VolverRegistro(View view) {
        Intent VolverRegistro = new Intent(this, Registrar_Usuario.class);
        startActivity(VolverRegistro);
    }

    private class registrarComercio extends AsyncTask<Comercio, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Comercio... com) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.RegistrarUsuarioComercio(com[0]);

            } catch (Exception e) {
                e.printStackTrace();

            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {

            if(exito) {

                Toast toast = Toast.makeText(Registrar_Comercio.this, "COMERCIO AGREGADO CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();

                Intent IngresarComercio = new Intent(Registrar_Comercio.this, MainActivity.class);
                startActivity(IngresarComercio);

            }else {

                Toast toast = Toast.makeText(Registrar_Comercio.this, "HUBO UN ERROR", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }
}