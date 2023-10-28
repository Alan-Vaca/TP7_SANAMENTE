package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import BaseDeDatos.Conexion;
import Entidad.Comercio;
import Entidad.Usuario;

public class Mi_Comercio extends AppCompatActivity {

    Comercio comercio;
    Usuario user;
    TextView nombreComercio, direccionComercio, horarioApertura, horarioCierre, nombreUsuario, contraseñaActual, contraseñaNueva1, contraseñaNueva2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_comercio);

        nombreComercio = (TextView)findViewById(R.id.mod_c_comercio);
        direccionComercio = (TextView)findViewById(R.id.mod_c_direccion);
        horarioApertura = (TextView)findViewById(R.id.mod_c_apertura);
        horarioCierre = (TextView)findViewById(R.id.mod_c_cierre);
        nombreUsuario = (TextView)findViewById(R.id.mod_c_usuario);
        contraseñaActual = (TextView)findViewById(R.id.mod_c_actual);
        contraseñaNueva1 = (TextView)findViewById(R.id.mod_c_pass);
        contraseñaNueva2 = (TextView)findViewById(R.id.mod_c_repetir);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        comercio = new Comercio();
        user = new Usuario();

        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                direccionComercio.setText(user.getDireccion());
                nombreUsuario.setText(user.getNombreUsuario());

                comercio.setUsuarioAsociado(user);
                new Mi_Comercio.cargarComercio().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(Mi_Comercio.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(Mi_Comercio.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }


    public void MenuMiUsuarioComercio(View view) {
        Intent menuMiUsuarioComercio = new Intent(this, MiUsuarioComercio.class);
        startActivity(menuMiUsuarioComercio);
    }

    public void MenuComercio(View view) {
        Intent menuComercio = new Intent(this, MenuComercio.class);
        startActivity(menuComercio);
    }

    public void Modificar(View view) {
        String NombreComercio = nombreComercio.getText().toString();
        String DireccionComercio = direccionComercio.getText().toString();
        String HorarioApertura = horarioApertura.getText().toString();
        String HorarioCierre = horarioCierre.getText().toString();
        String NombreUsuario = nombreUsuario.getText().toString();

        String ContraseñaActual = contraseñaActual.getText().toString();
        String ContraseñaNueva1 = contraseñaNueva1.getText().toString();
        String ContraseñaNueva2 = contraseñaNueva2.getText().toString();

        //IF -- LAS CONTRASEÑA ACTUAL DEBE SER IGUAL A LA DE comercio.user.getContraseña
        if(ContraseñaActual.equals(comercio.getUsuarioAsociado().getContraseña()) ){

            //SI ES VALIDO EL CONDICIONAL ANTERIOR LA NUEVA 1 Y NUEVA 2 DEBEN SER IGUALES
            if(ContraseñaNueva1.equals(ContraseñaNueva2)){
                comercio.setNombreComercio(NombreComercio);
                comercio.setHorarios(HorarioApertura + "-:-" + HorarioCierre);

                comercio.getUsuarioAsociado().setNombreUsuario(NombreUsuario);
                comercio.getUsuarioAsociado().setDireccion(DireccionComercio);
                comercio.getUsuarioAsociado().setContraseña(ContraseñaNueva1);

                //SI CUMPLE CON TODAS LAS CONDICIONES PROSIGUE
                if(validarComercio(comercio)) {
                    new Mi_Comercio.modificarComercio().execute(comercio);
                    MenuMiUsuarioComercio(view);
                    Toast.makeText(Mi_Comercio.this, "Exito", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(Mi_Comercio.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            }
        }
        else{
            contraseñaActual.setError("La contraseña actual es incorrecta");
        }






    }

    public boolean validarComercio(Comercio comercio) {
        // Verificar que ningún campo inicie con espacio en blanco
        if (comercio.getNombreComercio().startsWith(" ") ||
                comercio.getUsuarioAsociado().getNombreUsuario().startsWith(" ") ||
                comercio.getUsuarioAsociado().getDireccion().startsWith(" ")) {
            Toast.makeText(Mi_Comercio.this, "Ningún campo debe empezar con espacio en blanco", Toast.LENGTH_LONG).show();
            return false;
        }

        // Verificar que los campos de contraseña no tengan espacios en blanco y no estén vacíos
        String nuevaContraseña = comercio.getUsuarioAsociado().getContraseña();
        if (nuevaContraseña.trim().isEmpty() || nuevaContraseña.contains(" ")) {
            Toast.makeText(Mi_Comercio.this, "La contraseña no puede contener espacios en blanco o estar vacía", Toast.LENGTH_LONG).show();
            return false;
        }

        // Verificar que la contraseña no contenga caracteres especiales (solo letras y números permitidos)
        if (!nuevaContraseña.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(Mi_Comercio.this, "La contraseña solo puede contener letras y números", Toast.LENGTH_LONG).show();
            return false;
        }


        // Verificar que el horario sea válido (apertura menor que cierre)
        String[] horarios = comercio.getHorarios().split("-:-");


        if (horarios[0].isEmpty() || horarios[1].isEmpty()) {
            Toast.makeText(Mi_Comercio.this, "Los campos de horario no pueden estar vacíos", Toast.LENGTH_LONG).show();
            return false;
        }

        String[] aperturaParts = horarios[0].split(":");
        String[] cierreParts = horarios[1].split(":");

        int aperturaHoras = Integer.parseInt(aperturaParts[0]);
        int aperturaMinutos = Integer.parseInt(aperturaParts[1]);

        int cierreHoras = Integer.parseInt(cierreParts[0]);
        int cierreMinutos = Integer.parseInt(cierreParts[1]);

        if (aperturaHoras > cierreHoras || (aperturaHoras == cierreHoras && aperturaMinutos > cierreMinutos)) {
            Toast.makeText(Mi_Comercio.this, "El horario de apertura debe ser antes que el horario de cierre", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private class cargarComercio extends AsyncTask<Usuario, Void, Comercio> {
        @Override
        protected Comercio doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Comercio comercio = new Comercio();
            try {
                comercio = con.obtenerComercio(user[0].getIdUsuario());
                return comercio;
            } catch (Exception e) {
                e.printStackTrace();
                return comercio;
            }
        }

        @Override
        protected void onPostExecute(Comercio comercio) {
            if (comercio.getIdComercio() > 0) {
                nombreComercio.setText(comercio.getNombreComercio().toString());
                String horariosS = comercio.getHorarios().toString();
                String[] horarios = horariosS.split("-:-");

                horarioApertura.setText(horarios[0]);
                horarioCierre.setText(horarios[1]);

                comercio.setUsuarioAsociado(user);
            } else {
                Toast.makeText(Mi_Comercio.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class modificarComercio extends AsyncTask<Comercio, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Comercio... comercio) {
            Conexion con = new Conexion();
            try {
                con.ModificarUsuarioComercio(comercio[0]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String usuarioJson = gson.toJson(comercio.getUsuarioAsociado());
                editor.putString("usuarioLogueado", usuarioJson);
                editor.apply();

                Toast.makeText(Mi_Comercio.this, "EL COMERCIO HA SIDO MODIFICADO CON EXITO", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Mi_Comercio.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }

}