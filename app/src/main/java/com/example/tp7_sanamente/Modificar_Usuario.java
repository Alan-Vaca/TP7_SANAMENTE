package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import BaseDeDatos.Conexion;
import Entidad.Restriccion;
import Entidad.Usuario;

public class Modificar_Usuario extends AppCompatActivity {

    TextView nombreApellido,dni,usuario,alergia,direccion,pass0,pass1,pass2;
    CheckBox hipertenso,celiaco,diabetico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario);

        nombreApellido = (TextView)findViewById(R.id.mod_nombre_apellido);
        dni = (TextView)findViewById(R.id.mod_dni);
        usuario = (TextView)findViewById(R.id.mod_c_direccion);
        alergia = (TextView)findViewById(R.id.r_cli_alergias);
        direccion = (TextView)findViewById(R.id.r_cli_direc);
        pass0 = (TextView)findViewById(R.id.r_cli_pass_actual);
        pass1 = (TextView)findViewById(R.id.r_cli_pass_nuevo);
        pass2 = (TextView)findViewById(R.id.r_cli_pass_repetir);

        hipertenso = (CheckBox)findViewById(R.id.r_cb_hipertenso);
        celiaco = (CheckBox)findViewById(R.id.r_cb_celiaco);
        diabetico = (CheckBox)findViewById(R.id.r_cb_diabetico);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        Usuario user = new Usuario();

        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                usuario.setText(user.getNombreUsuario());
                nombreApellido.setText(user.getApellido() + ", " + user.getNombre());
                String dniTxt = String.valueOf(user.getDNI());
                dni.setText(dniTxt);
                direccion.setText(user.getDireccion());

                new Modificar_Usuario.cargarRestricciones().execute(user);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(Modificar_Usuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(Modificar_Usuario.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    public void MenuMiUsuario(View view) {
        Intent menuMiUsuario = new Intent(this, MiUsuario.class);
        startActivity(menuMiUsuario);
    }



    private class cargarRestricciones extends AsyncTask<Usuario, Void, Restriccion> {
        @Override
        protected Restriccion doInBackground(Usuario... user) {
            Conexion con = new Conexion();
            Restriccion res = new Restriccion();
            try {
                res = con.obtenerRestriccion(user[0].getIdUsuario());
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return res;
            }
        }

        @Override
        protected void onPostExecute(Restriccion res) {
            if (res.getIdRestriccion() > 0) {
                    hipertenso.setChecked(res.isHipertenso());
                    celiaco.setChecked(res.isCeliaco());
                    diabetico.setChecked(res.isDiabetico());
                    alergia.setText(res.getAlergico());
            } else {
                Toast.makeText(Modificar_Usuario.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG).show();
            }
        }
    }


}