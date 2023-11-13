package com.example.tp7_sanamente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import BaseDeDatos.Conexion;
import Entidad.Cliente;
import Entidad.Restriccion;
import Entidad.Usuario;

public class Registrar_Cliente extends AppCompatActivity {


    TextView nombre,apellido,dni,alergias, direccion;
    CheckBox hipertenso,diabetico,celiaco;

    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente);

        Usuario usuarioRecibido = getIntent().getParcelableExtra("usuarioInsert");
        user = usuarioRecibido;

        nombre = (EditText)findViewById(R.id.mod_c_comercio);
        apellido = (EditText)findViewById(R.id.mod_c_direccion);
        dni = (EditText)findViewById(R.id.r_cli_dni);
        alergias = (EditText)findViewById(R.id.txtComentarios);
        direccion = (EditText)findViewById(R.id.r_cli_direccion);
        hipertenso = (CheckBox)findViewById(R.id.r_cb_hipertenso);
        diabetico = (CheckBox)findViewById(R.id.r_cb_diabetico);
        celiaco = (CheckBox)findViewById(R.id.r_cb_celiaco);
    }

    public void VolverRegistro(View view) {
        Intent VolverRegistro = new Intent(this, Registrar_Usuario.class);
        startActivity(VolverRegistro);
    }

    public void IngresarCliente(View view) {
        String dniStr = dni.getText().toString().trim();


        Restriccion restriccion = new Restriccion();
        restriccion.setAlergico(alergias.getText().toString());
        restriccion.setHipertenso(hipertenso.isChecked());
        restriccion.setDiabetico(hipertenso.isChecked());
        restriccion.setCeliaco(celiaco.isChecked());
        Cliente cliente = new Cliente();
        user.setNombre(nombre.getText().toString());
        user.setApellido(apellido.getText().toString());

        //Tengo que validar antes sino la app rompe
        if (!dniStr.isEmpty()) {   user.setDNI(Integer.parseInt(dni.getText().toString().trim()));  }
        user.setDireccion(direccion.getText().toString());
        cliente.setUsuarioAsociado(user);
        restriccion.setClienteAsociado(cliente);


        if(validarCliente(restriccion)) {
           // new registrarCliente().execute(restriccion);
            Toast toast = Toast.makeText(Registrar_Cliente.this, "CLIENTE AGREGADO CON EXITO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            Log.d("Ingresar", "Validado");
        }
    }

    public boolean validarCliente(Restriccion res) {
        boolean isValid = true;
        //StringBuilder errorMessage = new StringBuilder("Complete los siguientes campos con valores válidos:\n");

        // Alert para las Validaciones
        AlertDialog.Builder builder = new AlertDialog.Builder(Registrar_Cliente.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_validacion, null);
        builder.setView(dialogView);

        final EditText mensajeConfirm = dialogView.findViewById(R.id.editTextValidacion);
        Button btnOK= dialogView.findViewById(R.id.btnOK);

        mensajeConfirm.setText("Complete los siguientes campos con valores válidos: ");
        final AlertDialog dialog = builder.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        // Obtener el cliente asociado desde la restricción.
        Cliente clienteAsociado = res.getClienteAsociado();

        // Validar que el cliente asociado no sea nulo.
        if (clienteAsociado != null) {
            // Obtener el usuario asociado al cliente.
            Usuario usuarioAsociado = clienteAsociado.getUsuarioAsociado();

            // Validar que los campos del usuario asociado no estén vacíos o nulos.
            if (usuarioAsociado != null) {
                if (usuarioAsociado.getNombre() == null || usuarioAsociado.getNombre().isEmpty()) {
                    isValid = false;
                    mensajeConfirm.append("\nNombre");
                    //errorMessage.append("- Nombre\n");
                }

                if (usuarioAsociado.getApellido() == null || usuarioAsociado.getApellido().isEmpty()) {
                    isValid = false;
                    mensajeConfirm.append("\nApellido");
                    //errorMessage.append("- Apellido\n");
                }

                if (usuarioAsociado.getDNI() <= 0) {
                    isValid = false;
                    //errorMessage.append(" - DNI\n");
                    mensajeConfirm.append("\nDNI");
                }else if(usuarioAsociado.getDNI() < 5000000 ||
                        usuarioAsociado.getDNI() > 70000000)
                {
                    mensajeConfirm.append("\nDNI inválido");
                    isValid = false;
                }

                if (usuarioAsociado.getDireccion() == null || usuarioAsociado.getDireccion().isEmpty()) {
                    isValid = false;
                    mensajeConfirm.append("\nDirección");
                    //errorMessage.append("- Dirección\n");
                }
            } else {
                isValid = false;
                mensajeConfirm.append("\nUsuario asociado es nulo");
                //errorMessage.append("- Usuario asociado es nulo\n");
            }
        } else {
            // Si el cliente asociado es nulo, la restricción no es válida.
            isValid = false;
            mensajeConfirm.append("\nCliente asociado es nulo");
        }

        // Mostrar mensaje de error si la validación falla.
        if (!isValid) {



            dialog.show();
            /*
            Toast toast = Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();

             */
        }

        return isValid;
    }


    private class registrarCliente extends AsyncTask<Restriccion, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Restriccion... res) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.RegistrarUsuarioCliente(res[0]);

            } catch (Exception e) {
                e.printStackTrace();

            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {

            if(exito) {

                Toast toast = Toast.makeText(Registrar_Cliente.this, "CLIENTE AGREGADO CON EXITO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();

                Intent IngresarCliente = new Intent(Registrar_Cliente.this, MainActivity.class);
                startActivity(IngresarCliente);

            }else {

                Toast toast = Toast.makeText(Registrar_Cliente.this, "HUBO UN ERROR", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }
}