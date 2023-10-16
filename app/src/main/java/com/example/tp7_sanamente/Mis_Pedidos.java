package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Pedido;
import Entidad.Usuario;

public class Mis_Pedidos extends AppCompatActivity {

    ListView lv_pedidos;
    ArrayList<Pedido> listadoPedidos;
    Pedido pedidoSeleccionado;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        lv_pedidos = (ListView)findViewById(R.id.grd_Pedidos);

        listadoPedidos = new ArrayList<Pedido>();

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);

            new Mis_Pedidos.obtenerListadoPedidos().execute(user);
        }else{
            Toast.makeText(Mis_Pedidos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

        lv_pedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pedidoSeleccionado = new Pedido();
                pedidoSeleccionado = listadoPedidos.get(position);

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String pedidoJson = gson.toJson(pedidoSeleccionado);
                editor.putString("pedidoSeleccionado", pedidoJson);
                editor.apply();
            }
        });


    }

    private class obtenerListadoPedidos extends AsyncTask<Usuario, Void, ArrayList<Pedido>> {
        @Override
        protected ArrayList<Pedido> doInBackground(Usuario... usuario) {

            Conexion con = new Conexion();
            try {
                listadoPedidos = con.obtenerListadoPedidos(usuario[0]);

                return listadoPedidos;
            } catch (Exception e) {
                e.printStackTrace();
                return listadoPedidos;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Pedido> listaPedido) {
            //Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_LONG).show();

            if (listaPedido.size() > 0) {

                listadoPedidos = listaPedido;
                ArrayAdapter<Pedido> adapter = new ArrayAdapter<>(Mis_Pedidos.this, android.R.layout.simple_spinner_dropdown_item, listadoPedidos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                lv_pedidos.setAdapter(adapter);

            } else {
                Toast.makeText(Mis_Pedidos.this, "HUBO UN ERROR AL CONSULTAR LOS HISTORIALES", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void FiltrosPedidos(View view) {
        //Se usa el mismo que el de pedidos
        Intent FiltrosHistorial = new Intent(this, Filtros_Pedidos.class);
        startActivity(FiltrosHistorial);
    }
    public void DetallePedidos(View view) {
        Intent DetalleHistorial = new Intent(this, Detalle_Pedido.class);
        startActivity(DetalleHistorial);
    }

    public void CancelarPedido(View view) {
        pedidoSeleccionado.setEstado(4);
        new Mis_Pedidos.cambiarEstadoPedido().execute(pedidoSeleccionado.getEstado());

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void ConfirmarPedido(View view) {
        pedidoSeleccionado.setEstado(3);
        new Mis_Pedidos.cambiarEstadoPedido().execute(pedidoSeleccionado.getEstado());

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void MenuComercio(View view) {
        Intent MenuComercio = new Intent(this, MenuComercio.class);
        startActivity(MenuComercio);
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
                Toast.makeText(Mis_Pedidos.this, "EL PEDIDO CAMBIO DE ESTADO EXITOSAMENTE", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Mis_Pedidos.this, "ERROR AL CAMBIAR EL ESTADO", Toast.LENGTH_LONG).show();
            }
        }
    }
}