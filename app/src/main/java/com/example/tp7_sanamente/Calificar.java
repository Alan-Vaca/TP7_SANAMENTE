package com.example.tp7_sanamente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

import BaseDeDatos.Conexion;
import Entidad.CalificacionXcliente;
import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class Calificar extends AppCompatActivity {
    private static final int ESTRELLA_VACIA = 0;
    private static final int ESTRELLA_MEDIA = 1;
    private static final int ESTRELLA_LLENA = 2;

    private int[] starStates;
    private ImageView[] estrellas;
    Pedido pedidoSeleccionado;
    Usuario user;
    TextView comercio,puntaje,comentario;
    private float puntajeTotal = 0;

    CalificacionXcliente calificacion;
    ArrayList<pedidoXproducto> listadoDeProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);
        comercio = (TextView)findViewById(R.id.txtComercioC);
        puntaje = (TextView)findViewById(R.id.txtPuntaje);
        comentario = (TextView)findViewById(R.id.txtComentarios);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String usuarioJson = sharedPreferences.getString("usuarioLogueado", "");
        user = new Usuario();
        if (!usuarioJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(usuarioJson, Usuario.class);
        }else{
            Toast.makeText(Calificar.this, "NO ESTAS LOGUEADO", Toast.LENGTH_LONG).show();
        }

        pedidoSeleccionado = new Pedido();
        SharedPreferences sharedPreferencesProducto = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String pedidoJson = sharedPreferencesProducto.getString("pedidoSeleccionado", "");
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy") // Establece el formato de fecha esperado
                    .create();
            pedidoSeleccionado = gson.fromJson(pedidoJson, Pedido.class);

            new Calificar.obtenerComercio().execute(pedidoSeleccionado.getComercio().getIdComercio());

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e("JSON_ERROR", e.getMessage());
        }

        estrellas = new ImageView[]{
                findViewById(R.id.estrella1),
                findViewById(R.id.estrella2),
                findViewById(R.id.estrella3),
                findViewById(R.id.estrella4),
                findViewById(R.id.estrella5)
        };
        starStates = new int[estrellas.length];
        Arrays.fill(starStates, ESTRELLA_VACIA); // Inicializa todas las estrellas como vacías

        // Agrega clic listeners a las estrellas
        for (int i = 0; i < estrellas.length; i++) {
            final int index = i;
            estrellas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float puntajeEstrella = 0.0f;
                    starStates[index] = (starStates[index] + 1) % 3; // Ciclo entre los tres estados
                    actualizarVisibilidadEstrella(estrellas[index], starStates[index]);

                    // Si se hace clic en una estrella "MEDIA" o "LLENA", las anteriores se establecen como "LLENA"
                    if (starStates[index] == ESTRELLA_MEDIA || starStates[index] == ESTRELLA_LLENA) {
                        for (int j = 0; j < index; j++) {
                            starStates[j] = ESTRELLA_LLENA;
                            actualizarVisibilidadEstrella(estrellas[j], starStates[j]);
                        }
                    }

                    // Si se hace clic en una estrella "VACÍA" o "MEDIA", las posteriores se establecen como "VACÍAS"
                    if (starStates[index] == ESTRELLA_VACIA || starStates[index] == ESTRELLA_MEDIA) {
                        for (int j = index + 1; j < estrellas.length; j++) {
                            starStates[j] = ESTRELLA_VACIA;
                            actualizarVisibilidadEstrella(estrellas[j], starStates[j]);
                        }
                    }

                    calcularPuntaje();

                }
            });
        }
    }

    private void actualizarVisibilidadEstrella(ImageView estrella, int estado) {
        switch (estado) {
            case ESTRELLA_VACIA:
                estrella.setImageResource(R.drawable.baseline_star_outline_24);
                break;
            case ESTRELLA_MEDIA:
                estrella.setImageResource(R.drawable.baseline_star_half_24);
                break;
            case ESTRELLA_LLENA:
                estrella.setImageResource(R.drawable.baseline_star_24);
                break;
        }
    }

    private void calcularPuntaje() {
        puntajeTotal = 0;
        int cantidad_medias = 0;
        int cantidad_llenas = 0;

        for (int estado : starStates) {
            if (estado == ESTRELLA_MEDIA) {
                cantidad_medias++;
            } else if (estado == ESTRELLA_LLENA) {
                cantidad_llenas++;
            }
        }

        puntajeTotal = (float) ((cantidad_medias * 0.5) + (cantidad_llenas * 1));
        puntaje.setText("PUNTAJE: " + puntajeTotal);
    }


    public void NuevaCalificacion(View view) {


        listadoDeProductos = new ArrayList<pedidoXproducto>();

        calificacion = new CalificacionXcliente();
        calificacion.setCalificacion(puntajeTotal);
        calificacion.setComentario(comentario.getText().toString());

        Cliente cliente = new Cliente();
        cliente.setIdCliente(pedidoSeleccionado.getCliente().getIdCliente());
        calificacion.setCliente(cliente);


        new Calificar.CalificarListado().execute(pedidoSeleccionado.getIdPedido());


    }

    public void CancelarCalificacion(View view) {
        Intent NuevaCalificacion = new Intent(this, Detalle_Pedido.class);
        startActivity(NuevaCalificacion);
    }

    public boolean ValidarCalificacion(CalificacionXcliente calificacion){
        //VALIDAR COMENTARIO
        return true;
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
                comercio.setText(com.getCuit() + " / " + com.getNombreComercio().toUpperCase());
            } else {
                Toast.makeText(Calificar.this, "ERROR AL OBTENER COMERCIO", Toast.LENGTH_LONG).show();
            }
        }
    }




    private class CalificarListado extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... idPedido) {
            Conexion con = new Conexion();
            boolean exito = false;
            try {
                exito = con.calificarPuntaje(idPedido[0],calificacion);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean exito) {

            if(exito) {
                Toast.makeText(Calificar.this, "EL PEDIDO YA FUE CALIFICADO CON EXITO", Toast.LENGTH_LONG).show();
                Intent IngresarCliente = new Intent(Calificar.this, MainActivity.class);
                startActivity(IngresarCliente);
            }else{
                Toast.makeText(Calificar.this, "HUBO UN ERROR", Toast.LENGTH_LONG).show();
            }
        }
    }
}