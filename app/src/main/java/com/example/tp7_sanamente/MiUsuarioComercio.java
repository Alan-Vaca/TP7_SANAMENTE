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

public class MiUsuarioComercio extends AppCompatActivity {

    Usuario user;
    Comercio comercio;

    TextView nombreComercio, cuit, direccion, dni, nombreApellido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario_comercio);

        nombreApellido = (TextView)findViewById(R.id.mi_nombreApellido);
        dni = (TextView)findViewById(R.id.mi_dni);
        direccion = (TextView)findViewById(R.id.mi_direccion);

        cuit = (TextView)findViewById(R.id.mi_cuit);
        nombreComercio = (TextView)findViewById(R.id.mi_nombreComercio);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        comercio = new Comercio();
        user = new Usuario();


        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                nombreApellido.setText(user.getApellido() + ", " + user.getNombre());
                String dniTxt = String.valueOf(user.getDNI());
                dni.setText(dniTxt);
                direccion.setText(user.getDireccion());

                new MiUsuarioComercio.cargarComercio().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(MiUsuarioComercio.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(MiUsuarioComercio.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    public void MenuComercio(View view) {
        Intent menuComercio = new Intent(this, MenuComercio.class);
        startActivity(menuComercio);
    }

    public void MenuMi_Comercio(View view) {
        Intent MenuMi_Comercio = new Intent(this, Mi_Comercio.class);
        startActivity(MenuMi_Comercio);
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
                String cuits = String.valueOf(comercio.getCuit());
                cuit.setText(cuits);
                nombreComercio.setText(comercio.getNombreComercio().toString());

            } else {
                Toast.makeText(MiUsuarioComercio.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }

}