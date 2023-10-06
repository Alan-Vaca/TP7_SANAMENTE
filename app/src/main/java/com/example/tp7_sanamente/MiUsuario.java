package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import Entidad.Usuario;

public class MiUsuario extends AppCompatActivity {

    TextView usuario,nombre,apellido,dni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_usuario);

        usuario = findViewById(R.id.miUsuario);
        nombre = findViewById(R.id.miNombre);
        apellido = findViewById(R.id.miApellido);
        dni = findViewById(R.id.miDni);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        Usuario user = new Usuario();

        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            try{
                user = gson.fromJson(usuarioJson, Usuario.class);
                usuario.setText(user.getNombreUsuario());
                nombre.setText(user.getNombre());
                apellido.setText(user.getApellido());
                String dniTxt = String.valueOf(user.getDNI());
                dni.setText(dniTxt);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(MiUsuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(MiUsuario.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }
    }

    public void MenuModificarUsuario(View view) {
        Intent menuModificarUsuario = new Intent(this, Modificar_Usuario.class);
        startActivity(menuModificarUsuario);
    }

    public void MenuCliente(View view) {
        Intent menuCliente = new Intent(this, Menu_Cliente.class);
        startActivity(menuCliente);
    }
}