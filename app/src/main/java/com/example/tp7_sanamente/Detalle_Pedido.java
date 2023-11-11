package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import BaseDeDatos.Conexion;
import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Detalle_Pedido extends AppCompatActivity {

    TextView numeroPedido, cliente, estado, comercio, fecha, cantidadProductos, montoTotal;
    Usuario user;
    Pedido pedidoSeleccionado;
    ListView lv_listado;
    ArrayList<pedidoXproducto> detallesDelPedido;
    Button calificar;
    String strCliente, srtComercio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        numeroPedido = (TextView)findViewById(R.id.dp_tv_pedido);
        cliente = (TextView)findViewById(R.id.dp_tv_datosCliente);
        comercio = (TextView)findViewById(R.id.dp_tv_nombreComercio);
        fecha = (TextView)findViewById(R.id.dp_tv_fecha);
        cantidadProductos = (TextView)findViewById(R.id.dp_tv_cantitdadProductos);
        montoTotal = (TextView)findViewById(R.id.dp_tv_importeTotal);
        estado = (TextView)findViewById(R.id.txtEstado);
        lv_listado = (ListView)findViewById(R.id.lv_detallePedido);
        calificar = (Button)findViewById(R.id.dp_bn_calificar);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");

        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast.makeText(Detalle_Pedido.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

        detallesDelPedido = new ArrayList<pedidoXproducto>();

        //if(!user.isCliente()) {

            SharedPreferences sharedPreferencesProducto = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String pedidoJson = sharedPreferencesProducto.getString("pedidoSeleccionado", "");
            try {
                Gson gson = new GsonBuilder()
                        .setDateFormat("MMM dd, yyyy") // Establece el formato de fecha esperado
                        .create();
                pedidoSeleccionado = gson.fromJson(pedidoJson, Pedido.class);

               // SimpleDateFormat fechaFormateada = new SimpleDateFormat("dd/MM/yyyy");
                //fecha.setText("FECHA DEL PEDIDO: " + fechaFormateada.format(pedidoSeleccionado.getFecha()));


                numeroPedido.setText("PEDIDO N°" + pedidoSeleccionado.getIdPedido());
                estado.setText("ESTADO: " + pedidoSeleccionado.getEstadoString());
                fecha.setText(pedidoSeleccionado.getFechaFormateada());
                montoTotal.setText("MONTO TOTAL: $" + pedidoSeleccionado.getMonto().toString());

                if(user.isCliente()){
                    calificar.setText("CALIFICAR");
                    if(pedidoSeleccionado.getEstado() == 2){
                        calificar.setEnabled(true);
                    }else{
                        calificar.setEnabled(false);
                    }
                }else {
                    calificar.setText("");
                    calificar.setEnabled(false);
                }

                new Detalle_Pedido.obtenerCliente().execute(pedidoSeleccionado.getCliente().getIdCliente());
                new Detalle_Pedido.obtenerComercio().execute(pedidoSeleccionado.getComercio().getIdComercio());
                new Detalle_Pedido.obtenerListado().execute(pedidoSeleccionado.getIdPedido());

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Log.e("JSON_ERROR", e.getMessage());
            }

        //}
    }


    /*ACA DEPENDE QUE USUARIO SE LOGUEO, POR EL MOMENTO SOLO VUELVE AL HISTORIAL*/
    public void VolverHistorial(View view) {
        if(user.isCliente()){
            Intent VolverHistorial = new Intent(this, MiHistorial.class);
            startActivity(VolverHistorial);
        }else {
            onBackPressed();
        }
    }





    public void VolverPedidos(View view) {
        Intent VolverPedidos = new Intent(this, Mis_Pedidos.class);
        startActivity(VolverPedidos);
    }
    /*ACA DEPENDE QUE USUARIO SE LOGUEO, POR EL MOMENTO SOLO VUELVE AL HISTORIAL*/
    public void CalificarPedido(View view) {
        Intent CalificarPedido = new Intent(this, Calificar.class);
        startActivity(CalificarPedido);
    }

    public void ImprimirPedido(View view) {


        ArrayList<String> datosPedido = new ArrayList<>();
        Toast.makeText(Detalle_Pedido.this, "Generando Reporte...", Toast.LENGTH_LONG).show();

        datosPedido.add("REPORTE SANAMENTE");
        datosPedido.add("-----------------------");
        datosPedido.add("Detalle del Pedido Nro " + pedidoSeleccionado.getIdPedido());
        datosPedido.add("ESTADO: " + pedidoSeleccionado.getEstadoString());
        datosPedido.add("CLIENTE: " + strCliente);
        datosPedido.add("COMERCIO: " + srtComercio);
        datosPedido.add("FECHA DEL PEDIDO: " + pedidoSeleccionado.getFechaFormateada());
        datosPedido.add("");
        datosPedido.add("");
        datosPedido.add("DETALLE DEL PEDIDO");
        datosPedido.add("");

        for (pedidoXproducto detalle : detallesDelPedido) {
            datosPedido.add(detalle.getProducto().getNombre() + " - $" + detalle.getProducto().getPrecio() + " - Cantidad: " + detalle.getCantidad());
        }
        datosPedido.add("");
        datosPedido.add("");
        datosPedido.add("CANTIDAD DE PRODUCTOS: " + detallesDelPedido.size());
        datosPedido.add("MONTO TOTAL: $" + pedidoSeleccionado.getMonto());

        boolean exito = ExportarPdf.exportarAPdf(Detalle_Pedido.this, datosPedido, "DetallePedido");

        if (exito) {
            Toast.makeText(Detalle_Pedido.this, "PDF generado con éxito", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Detalle_Pedido.this, "Error al generar el PDF", Toast.LENGTH_LONG).show();
        }


    }




    private class obtenerCliente extends AsyncTask<Integer, Void, Cliente> {
        @Override
        protected Cliente doInBackground(Integer... idCliente) {
            Conexion con = new Conexion();
            Cliente cli = new Cliente();
            try {
                cli = con.obtenerClienteXid(idCliente[0]);
                return cli;
            } catch (Exception e) {
                e.printStackTrace();
                return cli;
            }
        }
        @Override
        protected void onPostExecute(Cliente cli) {
            if (cli.getIdCliente() > 0) {
                cliente.setText("CLIENTE: " + cli.getUsuarioAsociado().getDNI() + " - " + cli.getUsuarioAsociado().getNombre() + ", " + cli.getUsuarioAsociado().getApellido());
                strCliente = (cli.getUsuarioAsociado().getDNI() + " - " + cli.getUsuarioAsociado().getNombre() + ", " + cli.getUsuarioAsociado().getApellido());
            } else {
                Toast.makeText(Detalle_Pedido.this, "ERROR AL OBTENER CLIENTE", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class obtenerComercio extends AsyncTask<Integer, Void, Comercio> {
        @Override
        protected Comercio doInBackground(Integer... idComercio) {
            Conexion con = new Conexion();
            Comercio com = new Comercio();
            try {
                com = con.obtenerComercioXid(idComercio[0]);
                return com;
            } catch (Exception e) {
                e.printStackTrace();
                return com;
            }
        }
        @Override
        protected void onPostExecute(Comercio com) {
            if (com.getIdComercio() > 0) {
                comercio.setText(com.getCuit() + " / " + com.getNombreComercio());
                srtComercio = com.getCuit() + " / " + com.getNombreComercio();
            } else {
                Toast.makeText(Detalle_Pedido.this, "ERROR AL OBTENER COMERCIO", Toast.LENGTH_LONG).show();
            }
        }
    }



    private class obtenerListado extends AsyncTask<Integer, Void, ArrayList<pedidoXproducto>> {
        @Override
        protected ArrayList<pedidoXproducto> doInBackground(Integer... idPedido) {
            Conexion con = new Conexion();
            ArrayList<pedidoXproducto> detallesPedido = new ArrayList<pedidoXproducto>();
            try {
                detallesPedido = con.obtenerListadoPedidosXid(idPedido[0]);
                return detallesPedido;
            } catch (Exception e) {
                e.printStackTrace();
                return detallesPedido;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<pedidoXproducto> detallesPedido) {
            if (detallesPedido.size() > 0) {
                detallesDelPedido = detallesPedido;
                ArrayAdapter<pedidoXproducto> adapter = new ArrayAdapter<>(Detalle_Pedido.this, android.R.layout.simple_spinner_dropdown_item, detallesDelPedido);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                lv_listado.setAdapter(adapter);

                cantidadProductos.setText("CANTIDAD DE PRODUCTOS " + detallesPedido.size());

            } else {
                Toast.makeText(Detalle_Pedido.this, "ERROR AL OBTENER LISTADO DE PEDIDOS", Toast.LENGTH_LONG).show();
            }
        }
    }
}