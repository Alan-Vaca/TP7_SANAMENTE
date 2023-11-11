package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
            new registrarComercio().execute(comercio);
        }
    }

    public boolean validarComercio(Comercio comercio) {
        Boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Complete o corrija los siguientes campos:\n");

        if (comercio.getUsuarioAsociado().getDNI() <= 0) {
            errorMessage.append("- DNI\n");
            isValid = false;
        }
        // Se puede descomentar una vez se finalicen las pruebas de la app, para evitar retrasos
        else if(comercio.getUsuarioAsociado().getDNI() < 99999 ||
                comercio.getUsuarioAsociado().getDNI() > 1000000000)
        {
            errorMessage.append("- DNI inválido\n");
            isValid = false;
        }

        if (comercio.getUsuarioAsociado().getNombre().isEmpty()) {
            errorMessage.append("- Nombre\n");
            isValid = false;
        }

        if (comercio.getUsuarioAsociado().getApellido().isEmpty()) {
            errorMessage.append("- Apellido\n");
            isValid = false;
        }

        if (comercio.getCuit() <= 0) {
            errorMessage.append("- CUIT\n");
            isValid = false;
        }
        /* Se puede descomentar una vez se finalicen las pruebas de la app, para evitar retrasos
        else if(comercio.getCuit() < 999999 || comercio.getCuit() > 1000000000)
        {
            errorMessage.append("- CUIT inválido\n");
            isValid = false;
        }*/


        if (comercio.getNombreComercio().isEmpty()) {
            errorMessage.append("- Nombre comercio\n");
            isValid = false;
        }

        // Verificar que el horario sea válido (apertura menor que cierre)
        String[] horarios = comercio.getHorarios().split("-:-");


        if(horarios.length > 1) {
            if (horarios[0].isEmpty() || horarios[1].isEmpty()) {
                errorMessage.append("- Horarios\n");
                isValid = false;
            }
            if (!horarios[0].isEmpty() && !validarFormatoHorario(horarios[0])) {
                errorMessage.append("- Apertura inválida\n");
                isValid = false;
            }

            if (!horarios[1].isEmpty() && !validarFormatoHorario(horarios[1])) {
                errorMessage.append("- Cierre inválido\n");
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
                    errorMessage.append("- Horarios incoherentes\n");
                    isValid = false;
                }
            }
        }
        else {
            errorMessage.append("- Horarios\n");
            isValid = false;
        }



        if (comercio.getUsuarioAsociado().getDireccion().isEmpty()) {
            errorMessage.append("- Direccion\n");
            isValid = false;
        }

        if (!isValid) {

            Toast toast = Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
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