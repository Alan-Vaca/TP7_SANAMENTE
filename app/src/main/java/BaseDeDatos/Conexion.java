package BaseDeDatos;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Entidad.Alergia;
import Entidad.CalificacionXcliente;
import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Etiquetado;
import Entidad.Historial;
import Entidad.Notificacion;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Reporte;
import Entidad.Restriccion;
import Entidad.Usuario;
import Entidad.pedidoXproducto;
import kotlin.contracts.Returns;


public class Conexion extends AsyncTask<String,Void, String> {

    //CREDENCIALES DE CONEXION PARA BASE DE DATOS REMOTA
    public static String ConexionHOST = "3306";
    public static String ConexionUSER = "sql10652400";
    public static String ConexionPASS = "H1E7UP5V5e";
    public static String ConexionURL = "jdbc:mysql://sql10.freesqldatabase.com:" + ConexionHOST + "/" + ConexionUSER;

    //CLASES PARA LAS CONSULTAS
    public static consultasUsuario consultasUsuario = new consultasUsuario();
    public static consultasNotificaciones consultasNotificaciones = new consultasNotificaciones();
    public static consultasRestricciones consultasRestricciones = new consultasRestricciones();
    public static consultasEtiquetados consultasEtiquetados = new consultasEtiquetados();
    public static consultasProductos consultasProductos = new consultasProductos();
    public static consultasComercios consultasComercios = new consultasComercios();
    public static consultasHistoriales consultasHistoriales = new consultasHistoriales();
    public static consultasPedidos consultasPedidos = new consultasPedidos();
    public static consultasCalificaciones consultasCalificaciones = new consultasCalificaciones();

    //CONEXION
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(ConexionURL, ConexionUSER, ConexionPASS);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
            return null;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    public void cerrarConexion() {
        try {
            Connection con = getConnection();
            con.close();
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    //CONSULTAS - A LA BASE DE DATOS

    //--------------------------------------------------------------------------------------
    //USUARIOS
    //--------------------------------------------------------------------------------------

    public Usuario obtenerUsuarioXlogin(Usuario user) {
        try {
            Connection con = getConnection();
            user = consultasUsuario.obtenerUsuarioXlogin(con,user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return user;
    }

    public String obtenerMSJNotificaciones(Usuario usuario) {
        try {
            Connection con = getConnection();
            return consultasUsuario.obtenerMSJNotificaciones(con,usuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
            return "";
        }
    }

    public String obtenerMSJNotificacionesCOMERCIO(Usuario usuario) {
        try {
            Connection con = getConnection();
            return consultasUsuario.obtenerMSJNotificacionesCOMERCIO(con,usuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
            return "";
        }
    }

    public Boolean ExisteUsuario(String nombreUsuario) {
        try {
            Connection con = getConnection();
            return consultasUsuario.ExisteUsuario(con, nombreUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return false;
    }

    public boolean RegistrarUsuarioCliente(Restriccion res,int idAlergia1,int idAlergia2,int idAlergia3) {
        Boolean exito = false;
        try {
            res.getClienteAsociado().getUsuarioAsociado().setCliente(true);
            exito = consultasUsuario.registrarUsuario(getConnection(),res.getClienteAsociado().getUsuarioAsociado());
            if(exito) {
                altaNotificacion(res.getClienteAsociado().getUsuarioAsociado().getIdUsuario());
                altaCliente(res.getClienteAsociado().getUsuarioAsociado());
                altaRestricciones(res);
                altaAlergias(idAlergia1,idAlergia2,idAlergia3);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }



    public boolean ModificarUsuarioCliente(Restriccion res, Notificacion not,int idAlergia1,int idAlergia2,int idAlergia3) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.modificarUsuario(getConnection(),res.getClienteAsociado().getUsuarioAsociado());
            if(exito) {
                ModificarRestriccion(res);
                ModificarNotificacion(not);
                ModificarAlergias(res.getClienteAsociado().getUsuarioAsociado(),idAlergia1,idAlergia2,idAlergia3);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }




    public boolean RegistrarUsuarioComercio(Comercio com) {
        Boolean exito = false;
        try {
            com.getUsuarioAsociado().setCliente(false);
            exito = consultasUsuario.registrarUsuario(getConnection(),com.getUsuarioAsociado());
            if(exito) {
                altaComercio(com);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean ModificarUsuarioComercio(Comercio com) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.modificarUsuario(getConnection(),com.getUsuarioAsociado());
            if(exito) {
                ModificarComercio(com);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public boolean BajaUsuario(Usuario usuario) {
        Boolean exito = false;
        try {
            exito = consultasUsuario.BajaUsuario(getConnection(), usuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //NOTIFICACIONES
    //--------------------------------------------------------------------------------------

    public void altaNotificacion(int idUsuario) {
        try {
            consultasNotificaciones.altaNotificacion(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    public Notificacion obtenerNotificaciones(Usuario usuario) {
        Notificacion not = new Notificacion();
        try {
            Connection con = getConnection();
            not = consultasNotificaciones.obtenerNotificaciones(getConnection(),usuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return not;
    }
    public boolean ModificarNotificacion(Notificacion not) {
        Boolean exito = false;
        try {
            exito = consultasNotificaciones.modificarNotificacion(getConnection(),not);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //CLIENTE
    //--------------------------------------------------------------------------------------

    public void altaCliente(Usuario user) {
        try {
            consultasUsuario.altaCliente(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    public Cliente obtenerClienteXid(int idCliente) {
        Cliente cliente = new Cliente();
        try {
            Connection con = getConnection();
            cliente = consultasUsuario.obtenerClienteXid(getConnection(),idCliente);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return cliente;
    }

    //--------------------------------------------------------------------------------------
    //RESTRICCIONES
    //--------------------------------------------------------------------------------------

    private void altaAlergias(int idAlergia1, int idAlergia2, int idAlergia3) {
        try {
            consultasRestricciones.altaAlergiaXcliente(getConnection(),idAlergia1,idAlergia2,idAlergia3);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    public void altaRestricciones(Restriccion rest) {
        try {
            consultasRestricciones.altaRestriccion(getConnection(),rest);
            consultasRestricciones.altaRestriccionXcliente(getConnection());
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }
    public Restriccion obtenerRestriccion(int idUsuario) {
        Restriccion res = new Restriccion();
        try {
            Connection con = getConnection();
            res = consultasRestricciones.obtenerRestriccion(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return res;
    }
    public void ModificarRestriccion(Restriccion rest) {
        try {
            consultasRestricciones.modificarRestriccion(getConnection(),rest);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    private void ModificarAlergias(Usuario user,int idAlergia1,int idAlergia2,int idAlergia3) {
        try {
            consultasRestricciones.modificarAlergias(getConnection(),user,idAlergia1,idAlergia2,idAlergia3);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    //--------------------------------------------------------------------------------------
    //COMERCIOS
    //--------------------------------------------------------------------------------------

    public void altaComercio(Comercio comercio) {
        try {
            consultasUsuario.altaComercio(getConnection(),comercio);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }

    public Comercio obtenerComercioXid(int idComercio) {
        Comercio comercio = new Comercio();
        try {
            Connection con = getConnection();
            comercio = consultasUsuario.obtenerComercioXid(getConnection(),idComercio);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return comercio;
    }

    public Comercio obtenerComercio(int idUsuario) {
        Comercio comercio = new Comercio();
        try {
            Connection con = getConnection();
            comercio = consultasUsuario.obtenerComercio(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return comercio;
    }
    public void ModificarComercio(Comercio comercio) {
        try {
            BaseDeDatos.consultasUsuario.modificarComercio(getConnection(),comercio);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
    }



    //--------------------------------------------------------------------------------------
    //PRODUCTOS
    //--------------------------------------------------------------------------------------

    public boolean altaProducto(Producto producto, Comercio comercio, int idEtiquetado1, int idEtiquetado2, int idEtiquetado3) {
        Boolean exito = false;
        try {
            Connection con = getConnection();
            exito = consultasProductos.agregarProducto(getConnection(),producto,comercio);
            if(exito) {
                if(idEtiquetado1 > 0){
                    consultasEtiquetados.agregarProductoXetiquetado(getConnection(),idEtiquetado1);
                }
                if(idEtiquetado2 > 0){
                    consultasEtiquetados.agregarProductoXetiquetado(getConnection(),idEtiquetado2);
                }
                if(idEtiquetado3 > 0){
                    consultasEtiquetados.agregarProductoXetiquetado(getConnection(),idEtiquetado3);
                }
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public Boolean ExisteProducto(String nombreProducto, Comercio comercio) {
        try {
            Connection con = getConnection();
            return consultasProductos.ExisteProducto(con, nombreProducto, comercio);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return false;
    }

    public boolean modificarProducto(Producto producto, int idEtiquetado1, int idEtiquetado2, int idEtiquetado3) {
        Boolean exito = false;
        try {
            Connection con = getConnection();
            exito = consultasProductos.modificarProducto(getConnection(),producto);
            if(exito) {
                consultasEtiquetados.eliminarProductoXetiquetado(getConnection(),producto.getIdProducto());

                if(idEtiquetado1 > 0){
                    consultasEtiquetados.agregarProductoXetiquetadoXID(getConnection(),idEtiquetado1, producto.getIdProducto());
                }
                if(idEtiquetado2 > 0){
                    consultasEtiquetados.agregarProductoXetiquetadoXID(getConnection(),idEtiquetado2, producto.getIdProducto());
                }
                if(idEtiquetado3 > 0){
                    consultasEtiquetados.agregarProductoXetiquetadoXID(getConnection(),idEtiquetado3, producto.getIdProducto());
                }
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    public ArrayList<Producto> obtenerListadoProductos(Usuario user) {
        ArrayList<Producto> listaProducto = new ArrayList<Producto>();
        try {
            Connection con = getConnection();
            listaProducto = consultasProductos.obtenerListadoProductos(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaProducto;
    }

    public ArrayList<Producto> obtenerListadoProductosOfertas(Usuario user) {
        ArrayList<Producto> listaProducto = new ArrayList<Producto>();
        try {
            Connection con = getConnection();
            listaProducto = consultasProductos.obtenerListadoProductosOfertas(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaProducto;
    }

    public ArrayList<Producto> obtenerListadoProductosConRestricciones(Usuario user, boolean hipertenso, boolean diabetico, boolean celiaco) {
        ArrayList<Producto> listaProducto = new ArrayList<Producto>();
        try {
            Connection con = getConnection();
            listaProducto = consultasProductos.obtenerListadoProductosConRestricciones(getConnection(), user, hipertenso, diabetico, celiaco);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaProducto;
    }






    public Integer PermisosBajaProducto(Producto producto) {
        try {
            Connection con = getConnection();
            return consultasProductos.PermisosBajaProducto(con, producto);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return 0;
    }

    //--------------------------------------------------------------------------------------
    //ETIQUETADO
    //--------------------------------------------------------------------------------------

    //-ALTA ETIQUETADO (ID PRODUCTO)

    public ArrayList<Etiquetado> obtenerListadoEtiquetado() {
        ArrayList<Etiquetado> listaEtiquetado = new ArrayList<Etiquetado>();
        try {
            Connection con = getConnection();
            listaEtiquetado = consultasEtiquetados.obtenerListadoEtiquetado(getConnection());
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaEtiquetado;
    }

    public ArrayList<Alergia> obtenerListadoAlergias() {
        ArrayList<Alergia> listaAlergicos = new ArrayList<Alergia>();
        try {
            Connection con = getConnection();
            listaAlergicos = consultasEtiquetados.obtenerListadoAlergias(getConnection());
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaAlergicos;
    }

    public ArrayList<Alergia> obtenerListadoAlergiasXusuario(int idUsuario) {
        ArrayList<Alergia> listaAlergicos = new ArrayList<Alergia>();
        try {
            Connection con = getConnection();
            listaAlergicos = consultasEtiquetados.obtenerListadoAlergiasXusuario(getConnection(),idUsuario);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaAlergicos;
    }

    public ArrayList<Etiquetado> obtenerListadoEtiquetadoXproducto(Producto producto) {
        ArrayList<Etiquetado> listaEtiquetado = new ArrayList<Etiquetado>();
        try {
            Connection con = getConnection();
            listaEtiquetado = consultasEtiquetados.obtenerListadoEtiquetadoXproducto(getConnection(),producto);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaEtiquetado;
    }




    //-MODIFICAR ETIQUETADO (ID PRODUCTO, ETIQUETADO)

    //--------------------------------------------------------------------------------------
    //PEDIDOS
    //--------------------------------------------------------------------------------------

    public boolean altaPedido(Usuario usuario,Pedido pedido, ArrayList<pedidoXproducto> misProductosPedido) {
        Boolean exito = false;
        try {
            Map<Integer, ArrayList<pedidoXproducto>> productosAgrupados = new HashMap<>();
            // Iteramos sobre misProductosPedido para agruparlos por idComercio
            for (pedidoXproducto producto : misProductosPedido) {
                int idComercio = producto.getProducto().getIdComercio();
                if (!productosAgrupados.containsKey(idComercio)) {
                    productosAgrupados.put(idComercio, new ArrayList<>());
                }
                productosAgrupados.get(idComercio).add(producto);
            }
            // Iteramos sobre los grupos de productos y registramos uno a uno
            for (ArrayList<pedidoXproducto> grupoProductos : productosAgrupados.values()) {
                ArrayList<pedidoXproducto> misProductosPedidoxComercio = grupoProductos;

                //Sumo el total por comercio recorriendo el arraylist Cantidad * Precio
                float monto = 0;
                for(pedidoXproducto  p : misProductosPedidoxComercio){
                    monto += (p.getProducto().getPrecio() * p.getCantidad());
                }
                pedido.setMonto(monto);
                exito = consultasPedidos.registrarPedidoXcomercio(getConnection(), misProductosPedidoxComercio, pedido, usuario);
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }
    public ArrayList<pedidoXproducto> obtenerListadoPedidosXid(int idPedido) {
        ArrayList<pedidoXproducto> listaPedido = new ArrayList<pedidoXproducto>();
        try {
            Connection con = getConnection();
            listaPedido = consultasPedidos.obtenerListadoPedidosXid(getConnection(),idPedido);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaPedido;
    }

    public Pedido obtenerPedidoXid(int idPedido) {
        Pedido pedido = new Pedido();
        try {
            Connection con = getConnection();
            pedido = consultasPedidos.obtenerPedidoXid(getConnection(),idPedido);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return pedido;
    }

    public boolean cambiarEstadoPedido(Pedido pedido, Integer estado) {
        Boolean exito = false;
        try {
            exito = consultasPedidos.cambiarEstadoPedido(getConnection(), pedido, estado);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //CALIFICACIONES
    //--------------------------------------------------------------------------------------

    public boolean calificarPuntaje(int idPedido,CalificacionXcliente calificacionXcliente) {
        Boolean exito = false;
        try {
            ArrayList<pedidoXproducto> productos = new ArrayList<pedidoXproducto>();
            productos = obtenerListadoPedidosXid(idPedido);
            if(productos.size() > 0){
                for(pedidoXproducto producto : productos){
                    calificacionXcliente.setProducto(producto.getProducto());
                    exito = consultasCalificaciones.calificarPuntaje(getConnection(),calificacionXcliente);
                }
            }else{
                exito = false;
            }
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return exito;
    }

    //--------------------------------------------------------------------------------------
    //HISTORIAL
    //--------------------------------------------------------------------------------------

    public ArrayList<Historial> obtenerListadoHistorial(Usuario user) {
        ArrayList<Historial> listaHistorial = new ArrayList<Historial>();
        try {
            Connection con = getConnection();
            listaHistorial = consultasHistoriales.obtenerListadoHistorial(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaHistorial;
    }

    public ArrayList<Pedido> obtenerListadoPedidos(Usuario user) {
        ArrayList<Pedido> listaPedido = new ArrayList<Pedido>();
        try {
            Connection con = getConnection();
            listaPedido = consultasHistoriales.obtenerListadoPedidos(getConnection(),user);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return listaPedido;
    }


    ////REPORTE////

    public Reporte obtenerVentasTotales(int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            Connection con = getConnection();
            reporte = consultasPedidos.obtenerVentasTotales(getConnection(),id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return reporte;
    }


    public Reporte obtenerProductoMasVendido(int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            Connection con = getConnection();
            reporte = consultasPedidos.obtenerProductoMasVendido(getConnection(),id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return reporte;
    }

    public Reporte obtenerProductoFacturacion(int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            Connection con = getConnection();
            reporte = consultasPedidos.obtenerProductoFacturacion(getConnection(),id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return reporte;
    }

    public Reporte obtenerClienteUsual(int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            Connection con = getConnection();
            reporte = consultasPedidos.obtenerClienteUsual(getConnection(),id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return reporte;
    }

    public Reporte obtenerMedioPagoMasUsado(int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            Connection con = getConnection();
            reporte = consultasPedidos.obtenerMedioPagoMasUsado(getConnection(),id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            Log.d("BD-ERROR", e.toString());
        }
        return reporte;
    }




    //--------------------------------------------------------------------------------------
    //REPORTE
    //--------------------------------------------------------------------------------------

    //-REALIZAR REPORTE (ID COMERCIO) SE REALIZARA UN REPORTE CON TODOS LOS POSIBLES DATOS CALCULABLES Y CONVENIENTES PARA EL COMERCIO
    //---ejemplos: TOTAL SEMANAL, PROMEDIO DIARIO, MAYOR VENTA (FECHA Y MONTO), MENOR VENTA (FECHA Y MONTO), PRODUCTOS MAS VENDIDOS (5), PRODUCTOS MENOS VENDIDOS (5)
}
