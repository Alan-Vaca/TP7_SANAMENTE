package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import BaseDeDatos.Conexion;
import Entidad.Cliente;
import Entidad.Notificacion;
import Entidad.Producto;
import Entidad.Restriccion;
import Entidad.Usuario;

public class Modificar_Usuario extends AppCompatActivity {

    TextView nombreApellido,dni,usuario,alergia,direccion,pass0,pass1,pass2;
    CheckBox hipertenso,celiaco,diabetico;
Usuario user;
    Restriccion restriccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario);

        nombreApellido = (TextView)findViewById(R.id.mod_nombre_apellido);
        dni = (TextView)findViewById(R.id.mod_dni);
        usuario = (TextView)findViewById(R.id.mod_c_direccion);
        alergia = (TextView)findViewById(R.id.txtComentarios);
        direccion = (TextView)findViewById(R.id.r_cli_direc);
        pass0 = (TextView)findViewById(R.id.r_cli_pass_actual);
        pass1 = (TextView)findViewById(R.id.r_cli_pass_nuevo);
        pass2 = (TextView)findViewById(R.id.r_cli_pass_repetir);

        hipertenso = (CheckBox)findViewById(R.id.r_cb_hipertenso);
        celiaco = (CheckBox)findViewById(R.id.r_cb_celiaco);
        diabetico = (CheckBox)findViewById(R.id.r_cb_diabetico);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();

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

                Toast toast = Toast.makeText(Modificar_Usuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }else{

            Toast toast = Toast.makeText(Modificar_Usuario.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }
    }


    public void ModificarClienteUsuario(View view) {

        String Usuario = usuario.getText().toString();
        String Alergia = alergia.getText().toString();
        String Direccion = direccion.getText().toString();
        String Pass0 = pass0.getText().toString();
        String Pass1 = pass1.getText().toString();
        String Pass2 = pass2.getText().toString();

        boolean Hipertenso = hipertenso.isChecked();
        boolean Celiaco = celiaco.isChecked();
        boolean Diabetico = diabetico.isChecked();


        //IF -- LAS CONTRASEÑA ACTUAL DEBE SER IGUAL A LA DE user.getContraseña
        if(Pass0.equals(user.getContraseña())){

            //SI ES VALIDO EL CONDICIONAL ANTERIOR LA PASS 1 Y PASS 2 DEBEN SER IGUALES
            if(Pass1.equals(Pass2)) {
                user.setNombreUsuario(Usuario);
                user.setDireccion(Direccion);
                user.setContraseña(Pass1);

                if(Alergia.isEmpty()){
                    restriccion.setAlergico("");
                }
                else{
                    restriccion.setAlergico(Alergia);
                }
                restriccion.setHipertenso(Hipertenso);
                restriccion.setCeliaco(Celiaco);
                restriccion.setDiabetico(Diabetico);

                //SI CUMPLE CON TODAS LAS CONDICIONES PROSIGUE
                if (validarCliente(user)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_confirm, null);
                    builder.setView(dialogView);
                    final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextMensaje);
                    Button btnCancelarConfirm = dialogView.findViewById(R.id.btnCancelarMensaje);
                    Button btnConfirmarConfirm = dialogView.findViewById(R.id.btnConfirmarMensaje);

                    mensajeConfirm.setText("¿ESTAS SEGURO QUE QUIERES MODIFICAR EL USUARIO?");

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    btnCancelarConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    btnConfirmarConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Cliente cliente = new Cliente();
                    restriccion.setClienteAsociado(cliente);
                    restriccion.getClienteAsociado().setUsuarioAsociado(user);
                    new Modificar_Usuario.modificarCliente().execute(restriccion);
                    MenuirMiUsuario(view);

                        Toast toast = Toast.makeText(Modificar_Usuario.this, "Usuario validado correctamente", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 200);
                        toast.show();
                        dialog.dismiss();
                        }
                    });

                }
            }
            else{
                pass2.setError("Las contraseñas no coinciden");
            }
        }
        else{
            pass0.setError("La contraseña actual es incorrecta");
        }

    }

    public boolean validarCliente(Usuario usuario){
        boolean isValid = true;
        String Alergia = alergia.getText().toString();
        String Direccion = direccion.getText().toString();
        String Pass0 = pass0.getText().toString();
        String Pass1 = pass1.getText().toString();
        String Pass2 = pass2.getText().toString();


        //valida que Direccion contenga al menos 3 caracteres, si tiene menos o está vacío marca un error en el campo informando.
        if(Direccion.isEmpty()){

            Toast toast = Toast.makeText(Modificar_Usuario.this, "La direccion no puede estar vacía", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return false;
        }
        else if(Direccion.length() <= 3){

            Toast toast = Toast.makeText(Modificar_Usuario.this, "La dirección debe contener más caracteres", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return false;
        }

        //valida que pass1 o pass2 sea una sola palabra y no contenga caracteres especiales, si empieza con un "espacio" elimina el espacio en blanco
        // Verificar que los campos de contraseña no tengan espacios en blanco y no estén vacíos
        if (Pass1.trim().isEmpty() || Pass1.contains(" ")) {

            Toast toast = Toast.makeText(Modificar_Usuario.this, "La nueva contraseña no puede contener espacios en blanco o estar vacía", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return false;
        }


        // Verificar que la contraseña no contenga caracteres especiales (solo letras y números permitidos)
        if (!Pass1.matches("[a-zA-Z0-9]+")) {

            Toast toast = Toast.makeText(Modificar_Usuario.this, "La nueva contraseña solo puede contener letras y números", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return false;
        }

        return isValid;
    }

    public void MenuirMiUsuario(View view) {
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

                    restriccion = new Restriccion();
                    restriccion = res;
            } else {

                Toast toast = Toast.makeText(Modificar_Usuario.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }

    private class modificarCliente extends AsyncTask<Restriccion, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Restriccion... restriccion) {
            Conexion con = new Conexion();
            try {
                Notificacion notificacion = new Notificacion();
                return con.ModificarUsuarioCliente(restriccion[0],notificacion);
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
                String usuarioJson = gson.toJson(restriccion.getClienteAsociado().getUsuarioAsociado());
                editor.putString("usuarioLogueado", usuarioJson);
                editor.apply();


                Toast toast = Toast.makeText(Modificar_Usuario.this, "EL CLIENTE HA SIDO MODIFICADO CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            } else {

                Toast toast = Toast.makeText(Modificar_Usuario.this, "ERROR AL INGRESAR" + "\n" + "VERIFIQUE SUS CREDENCIALES", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }


}