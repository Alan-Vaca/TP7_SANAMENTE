package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Cliente;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Usuario;

public class MiHistorial extends AppCompatActivity {

    ListView lv_historial;
    ArrayList<Historial> listadoHistorial;
    Historial itemHistorialSeleccionado;
    Pedido pedidoSeleccionado;
    Usuario user;
    Button confirmarEntrega;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_historial);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        lv_historial = (ListView)findViewById(R.id.grd_Historial);
        confirmarEntrega = (Button)findViewById(R.id.btnConfirmarEntrega);
        listadoHistorial = new ArrayList<Historial>();

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
            new MiHistorial.obtenerListadoHistorial().execute(user);

            if(user.isCliente()){
                confirmarEntrega.setEnabled(false);
                confirmarEntrega.setText("CONFIRMAR ENTREGA");
            }else {
                confirmarEntrega.setEnabled(false);
                confirmarEntrega.setText("");
            }
        }else{
            Toast.makeText(MiHistorial.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }


        lv_historial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemHistorialSeleccionado = new Historial();
                itemHistorialSeleccionado = listadoHistorial.get(position);

                if(user.isCliente()) {
                    if (itemHistorialSeleccionado.getEstado() == 3) {
                        confirmarEntrega.setEnabled(true);
                    } else {
                        confirmarEntrega.setEnabled(false);
                    }
                }
            }
        });

    }

    private class obtenerListadoHistorial extends AsyncTask<Usuario, Void, ArrayList<Historial>> {
        @Override
        protected ArrayList<Historial> doInBackground(Usuario... usuario) {

            Conexion con = new Conexion();
            try {
                listadoHistorial = con.obtenerListadoHistorial(usuario[0]);

                return listadoHistorial;
            } catch (Exception e) {
                e.printStackTrace();
                return listadoHistorial;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Historial> listaHistorial) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listaHistorial.size() > 0) {

                listadoHistorial = listaHistorial;
                ArrayAdapter<Historial> adapter = new ArrayAdapter<>(MiHistorial.this, android.R.layout.simple_spinner_dropdown_item, listadoHistorial);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                lv_historial.setAdapter(adapter);

            } else {
                Toast.makeText(MiHistorial.this, "HUBO UN ERROR AL CONSULTAR LOS HISTORIALES", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void FiltrosHistorial(View view) {
        //Se usa el mismo que el de pedidos
        Intent FiltrosHistorial = new Intent(this, Filtros_Pedidos.class);
        startActivity(FiltrosHistorial);
    }
    public void DetalleHistorial(View view) {
        new MiHistorial.obtenerPedidoXid().execute(itemHistorialSeleccionado.getPedidoRealizado().getIdPedido());

        Intent DetalleHistorial = new Intent(this, Detalle_Pedido.class);
        startActivity(DetalleHistorial);
    }

    public void VolverAlMenu(View view) {
        if(user.isCliente()){
            MenuCliente(view);
        }else{
            MenuComercio(view);
        }
    }

    public void MenuCliente(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave


        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent MenuCliente = new Intent(this, MenuAdmin.class);
            startActivity(MenuCliente);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            Intent MenuCliente = new Intent(this, Menu_Cliente.class);
            startActivity(MenuCliente);
        }
    }

    public void MenuComercio(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        if(isAdmin) {
            // El usuario es un administrador, realiza las acciones correspondientes
            Intent MenuComercio = new Intent(this, MenuAdmin.class);
            startActivity(MenuComercio);
        } else {
            // El usuario no es un administrador, realiza las acciones correspondientes
            Intent MenuComercio = new Intent(this, MenuComercio.class);
            startActivity(MenuComercio);
        }
    }


    private class obtenerPedidoXid extends AsyncTask<Integer, Void, Pedido> {
        @Override
        protected Pedido doInBackground(Integer... idPedido) {
            Conexion con = new Conexion();
            Pedido pedido = new Pedido();
            try {
                pedido = con.obtenerPedidoXid(idPedido[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pedido;
        }
        @Override
        protected void onPostExecute(Pedido pedido) {

            if (pedido.getIdPedido() > 0) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String pedidoJson = gson.toJson(pedido);
                editor.putString("pedidoSeleccionado", pedidoJson);
                editor.apply();

            } else {
                Toast.makeText(MiHistorial.this, "HUBO UN ERROR AL CONSULTAR EL PEDIDO", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void EntregarPedido(View view) {
        pedidoSeleccionado = new Pedido();
        pedidoSeleccionado.setEstado(2);
        pedidoSeleccionado.setIdPedido(itemHistorialSeleccionado.getPedidoRealizado().getIdPedido());
        Cliente cliente = new Cliente();
        cliente.setIdCliente(itemHistorialSeleccionado.getClientePedido().getIdCliente());
        pedidoSeleccionado.setCliente(cliente);
        new MiHistorial.cambiarEstadoPedido().execute(pedidoSeleccionado.getEstado());

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private class cambiarEstadoPedido extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... estado) {
            Conexion con = new Conexion();
            boolean bol = false;
            try {
                bol = con.cambiarEstadoPedido(pedidoSeleccionado,estado[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bol;
        }
        @Override
        protected void onPostExecute(Boolean bol) {
            if (bol) {
                Toast.makeText(MiHistorial.this, "EL PEDIDO CAMBIO DE ESTADO EXITOSAMENTE", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MiHistorial.this, "ERROR AL CAMBIAR EL ESTADO", Toast.LENGTH_LONG).show();
            }
        }
    }
}