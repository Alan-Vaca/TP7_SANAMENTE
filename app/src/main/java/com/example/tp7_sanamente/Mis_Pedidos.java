package com.example.tp7_sanamente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Usuario;

public class Mis_Pedidos extends AppCompatActivity {

    ListView lv_pedidos;
    ArrayList<Pedido> listadoPedidos;
    Pedido pedidoSeleccionado;
    Usuario user;
    Button cancelar, confirmar;
    boolean listaPedidoConFiltro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        lv_pedidos = (ListView)findViewById(R.id.grd_Pedidos);
        cancelar = (Button)findViewById(R.id.btnCancelar);
        confirmar = (Button)findViewById(R.id.btnConfirmar);
        listadoPedidos = new ArrayList<Pedido>();
        listaPedidoConFiltro = false;

        try {
            SharedPreferences preferencesFiltradoPedido = getSharedPreferences("mi_prefPedido", Context.MODE_PRIVATE);
            Gson gsonFiltradoPedido = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();

            String listaComoJsonFiltradosPedido = preferencesFiltradoPedido.getString("listadoPedidosFiltrado", "");
            Type typeFiltradoPedido = new TypeToken<ArrayList<Pedido>>() {}.getType();
            listadoPedidos = gsonFiltradoPedido.fromJson(listaComoJsonFiltradosPedido, typeFiltradoPedido);

        }   catch (Exception e) {
            Log.e("Error FiltroPedido", String.valueOf(e));
        }

        if (listadoPedidos != null && !listadoPedidos.isEmpty()) {
            listaPedidoConFiltro = true;
        }




        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
           // new Mis_Pedidos.obtenerListadoPedidos().execute(user);

            if (!listaPedidoConFiltro ) {
                new Mis_Pedidos.obtenerListadoPedidos().execute(user);
            }else{
                if (listadoPedidos.size() > 0) {
                    ArrayAdapter<Pedido> adapter = new ArrayAdapter<>(Mis_Pedidos.this, android.R.layout.simple_spinner_dropdown_item, listadoPedidos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    lv_pedidos.setAdapter(adapter);
                }
            }

        }else{
            Toast toast = Toast.makeText(Mis_Pedidos.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
        }

        confirmar.setEnabled(false);
        cancelar.setEnabled(false);

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


                if(pedidoSeleccionado.getEstado() == 1){
                    //PENDIENTE
                    cancelar.setEnabled(true);
                    confirmar.setEnabled(true);
                }else if (pedidoSeleccionado.getEstado() == 2) {
                    //ENTREGADO
                    cancelar.setEnabled(false);
                    confirmar.setEnabled(false);
                }else if (pedidoSeleccionado.getEstado() == 3) {
                    //CONFIRMADO
                    confirmar.setEnabled(false);
                    cancelar.setEnabled(true);
                }else {
                    //CANCELADO
                    confirmar.setEnabled(false);
                    cancelar.setEnabled(false);
                }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_motivo, null);
        builder.setView(dialogView);

        final EditText textMotivo = dialogView.findViewById(R.id.editTextMotivo);
        Button btnGuardarMotivo = dialogView.findViewById(R.id.btnGuardarMotivo);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnGuardarMotivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motivoCancelacion = textMotivo.getText().toString();

                pedidoSeleccionado.setMotivoCancelacion(motivoCancelacion);

                dialog.dismiss();

                pedidoSeleccionado.setEstado(4);
                new Mis_Pedidos.cambiarEstadoPedido().execute(pedidoSeleccionado.getEstado());

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }

    public void ConfirmarPedido(View view) {
        pedidoSeleccionado.setEstado(3);
        new Mis_Pedidos.cambiarEstadoPedido().execute(pedidoSeleccionado.getEstado());

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void MenuComercio(View view) {
        // Recuperar el booleano isAdmin de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false); // El segundo parámetro es el valor predeterminado si no se encuentra la clave

        // Borro la lista de filtros al salir
        listaPedidoConFiltro = false;
        listadoPedidos = null;

        SharedPreferences preferencesFiltradoPedido = getSharedPreferences("mi_prefPedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorFiltradoPedido = preferencesFiltradoPedido.edit();
        editorFiltradoPedido.clear();
        editorFiltradoPedido.apply();


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
                Toast toast = Toast.makeText(Mis_Pedidos.this, "EL PEDIDO CAMBIO DE ESTADO EXITOSAMENTE", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            } else {
                Toast toast = Toast.makeText(Mis_Pedidos.this, "ERROR AL CAMBIAR EL ESTADO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        }
    }
}