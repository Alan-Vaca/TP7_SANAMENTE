package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Reporte;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class consultasPedidos {
    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------



    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------
    public Boolean registrarPedidoXcomercio(Connection conn, ArrayList<pedidoXproducto> misProductosPedidoxComercio, Pedido pedido, Usuario usuario) throws SQLException {
        Boolean exito = false;

        String selectMaxIdQuery = "SELECT idCliente FROM clientes where idUsuario = " + usuario.getIdUsuario();
        PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
        ResultSet resultSet = selectMaxIdStmt.executeQuery();
        int idCliente = 0;
        if (resultSet.next()) {
            idCliente = resultSet.getInt(1);
        }

        PreparedStatement pstmt = null; // Declarar el PreparedStatement fuera del bloque try para que pueda cerrarse en el bloque finally.

        try {
            if (conn != null) {

                int idComercio = 0;
                for (pedidoXproducto item : misProductosPedidoxComercio) {
                    idComercio = item.getProducto().getIdComercio();
                }

                String insertQuery = "INSERT INTO pedidos(" +
                        "idCliente,idComercio,monto,fecha,estado,medioPago" +
                        ") VALUES (?,?,?,?,?,?)";

                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, idCliente);
                pstmt.setInt(2, idComercio);
                pstmt.setFloat(3, pedido.getMonto());
                pstmt.setDate(4, (Date) pedido.getFecha());
                pstmt.setInt(5, pedido.getEstado());
                pstmt.setInt(6, pedido.getMedioPago());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    try{
        if (conn != null) {
                String selectMaxIdPQ = "SELECT max(idPedido) FROM pedidos";
                PreparedStatement selectMaxIdP = conn.prepareStatement(selectMaxIdPQ);
                ResultSet resultS = selectMaxIdP.executeQuery();
                int idPedido = 0;
                if (resultS.next()) {
                    idPedido = resultS.getInt(1);
                }


                for (pedidoXproducto item : misProductosPedidoxComercio) {

                    String insertQueryPedidoXproducto = "INSERT INTO pedidoXproducto(" +
                            "idProducto,idPedido,cantidad" +
                            ") VALUES (?,?,?)";

                    pstmt = conn.prepareStatement(insertQueryPedidoXproducto);
                    pstmt.setInt(1, item.getProducto().getIdProducto());
                    pstmt.setInt(2, idPedido);
                    pstmt.setInt(3, item.getCantidad());
                    pstmt.executeUpdate();

                    String queryRestarStock = "UPDATE productos SET stock = ? WHERE idProducto = " + item.getProducto().getIdProducto();
                    pstmt = conn.prepareStatement(queryRestarStock);
                    int nuevoStock = 0;
                    nuevoStock = item.getProducto().getStock() - item.getCantidad();
                    pstmt.setInt(1, nuevoStock);
                    pstmt.executeUpdate();
                }

                String insertQueryH = "INSERT INTO historial(" +
                        "idCliente,idPedido,fecha,estado" +
                        ") VALUES (?,?,?,?)";

                pstmt = conn.prepareStatement(insertQueryH);
                pstmt.setInt(1, idCliente);
                pstmt.setInt(2, idPedido);
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());
                pstmt.setDate(3, (Date) fechaActual);
                pstmt.setInt(4, pedido.getEstado());
                pstmt.executeUpdate();

                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }

        if (pstmt != null) {
            pstmt.close(); // Cierra el PreparedStatement en el bloque finally.
        }
        if (conn != null) {
            conn.close(); // Cierra la conexión en el bloque finally después de todas las operaciones.
        }
        return exito;
    }

    public ArrayList<pedidoXproducto> obtenerListadoPedidosXid(Connection conn, int idPedido) {
            ArrayList<pedidoXproducto> listadoPedido = new ArrayList<pedidoXproducto>();

            try {
                String query = "SELECT pp.idPedidoXproducto idPedidoXproductoPP,pp.idProducto idProductoPP,pp.idPedido idPedidoPP,pp.cantidad cantidadPP, " +
                        "p.idComercio idComercioP,p.nombreProducto nombreProductoP,p.ingredientes ingredientesP,p.precio precioP,p.stock stockP,p.estado estadoP " +
                        "FROM pedidoXproducto pp " +
                        "inner join productos p on p.idProducto = pp.idProducto where pp.idPedido = " + idPedido;



                if (conn != null) {
                    try {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            pedidoXproducto pedidoproducto = new pedidoXproducto();
                            Producto producto = new Producto();
                            Pedido pedido = new Pedido();

                            pedidoproducto.setIdPedidoXproducto(rs.getInt("idPedidoXproductoPP"));
                            pedidoproducto.setCantidad(rs.getInt("cantidadPP"));
                            pedido.setIdPedido(rs.getInt("idPedidoPP"));
                            producto.setIdProducto(rs.getInt("idProductoPP"));
                            producto.setIdComercio(rs.getInt("idComercioP"));
                            producto.setNombre(rs.getString("nombreProductoP"));
                            producto.setIngredientes(rs.getString("ingredientesP"));
                            producto.setPrecio(rs.getFloat("precioP"));
                            producto.setStock(rs.getInt("stockP"));
                            producto.setEstado(rs.getBoolean("estadoP"));
                            pedidoproducto.setProducto(producto);

                            listadoPedido.add(pedidoproducto);
                        }
                        rs.close();
                        stmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.d("ERROR-DB", e.toString());
            }

            return listadoPedido;

    }


    public Reporte obtenerVentasTotales(Connection conn, int idUsuario, String fechaDesde, String fechaHasta ) throws ParseException {
        Reporte reporte = null;
        String query;

        try {

            if ((fechaDesde == null || fechaDesde.isEmpty())&& (fechaHasta== null || fechaHasta.isEmpty())){
                Log.d("Reporte.casos", "1");
                 query = "SELECT COUNT(p.idPedido) Pedidos " +
                        "FROM pedidos p " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") ";
            }else if (fechaDesde == null || fechaDesde.isEmpty()){
                Log.d("Reporte.casos", "2");
                 query = "SELECT COUNT(p.idPedido) Pedidos " +
                        "FROM pedidos p " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha <= '" + fechaHasta +"'";
            }else if (fechaHasta == null || fechaHasta.isEmpty()){
                Log.d("Reporte.casos", "3");
                 query = "SELECT COUNT(p.idPedido) Pedidos " +
                        "FROM pedidos p " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha >= '" + fechaDesde + "' " ;
            }else{
                Log.d("Reporte.casos", "4");
                 query = "SELECT COUNT(p.idPedido) Pedidos " +
                        "FROM pedidos p " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha >= '" + fechaDesde + "' " +
                        "AND p.fecha <= '" + fechaHasta + "'";
            }

            Log.d("reporte.query", query);

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        reporte = new Reporte();
                        reporte.setCantidadPedidos(rs.getInt("Pedidos"));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return reporte;

    }


    public Reporte obtenerProductoMasVendido(Connection conn, int idUsuario, String fechaDesde, String fechaHasta ) {
        Reporte reporte = null;
        String query;

        try {
            if ((fechaDesde == null || fechaDesde.isEmpty())&& (fechaHasta== null || fechaHasta.isEmpty())){
                Log.d("Reporte.casos", "1");
                query = "SELECT pr.nombreProducto Nombre, SUM(pp.cantidad) cantidadPP " +
                        "FROM pedidoXproducto pp " +
                        "INNER JOIN pedidos p ON p.idPedido = pp.idPedido " +
                        "INNER JOIN productos pr ON pr.idProducto = pp.idProducto " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "GROUP BY Nombre " +
                        "ORDER BY cantidadPP DESC " +
                        "LIMIT 1";
            }else if (fechaDesde == null || fechaDesde.isEmpty()){
                Log.d("Reporte.casos", "2");
                query = "SELECT pr.nombreProducto Nombre, SUM(pp.cantidad) cantidadPP " +
                        "FROM pedidoXproducto pp " +
                        "INNER JOIN pedidos p ON p.idPedido = pp.idPedido " +
                        "INNER JOIN productos pr ON pr.idProducto = pp.idProducto " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha >= '" + fechaHasta + "' "+
                        "GROUP BY Nombre " +
                        "ORDER BY cantidadPP DESC " +
                        "LIMIT 1";
            }else if (fechaHasta == null || fechaHasta.isEmpty()){
                Log.d("Reporte.casos", "3");
                query = "SELECT pr.nombreProducto Nombre, SUM(pp.cantidad) cantidadPP " +
                        "FROM pedidoXproducto pp " +
                        "INNER JOIN pedidos p ON p.idPedido = pp.idPedido " +
                        "INNER JOIN productos pr ON pr.idProducto = pp.idProducto " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha >= '" + fechaDesde + "' " +
                        "GROUP BY Nombre " +
                        "ORDER BY cantidadPP DESC " +
                        "LIMIT 1";
            }else{
                Log.d("Reporte.casos", "4");
                query = "SELECT pr.nombreProducto Nombre, SUM(pp.cantidad) cantidadPP " +
                        "FROM pedidoXproducto pp " +
                        "INNER JOIN pedidos p ON p.idPedido = pp.idPedido " +
                        "INNER JOIN productos pr ON pr.idProducto = pp.idProducto " +
                        "WHERE p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") AND p.fecha >= '" + fechaDesde + "' " +
                        "AND p.fecha <= '" + fechaHasta + "' "+
                        "GROUP BY Nombre " +
                        "ORDER BY cantidadPP DESC " +
                        "LIMIT 1";
            }

            Log.d("reporte.query", query);


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        reporte = new Reporte();
                        reporte.setnombreProducto(rs.getString("Nombre"));
                        reporte.setCantidadProducto(rs.getInt("cantidadPP"));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return reporte;

    }

    public Reporte obtenerProductoFacturacion(Connection conn, int idUsuario, String fechaDesde, String fechaHasta ) {
        Reporte reporte = null;
        String query;

        try {
            if ((fechaDesde == null || fechaDesde.isEmpty())&& (fechaHasta== null || fechaHasta.isEmpty())){
                Log.d("Reporte.casos", "1");
                query = "SELECT SUM(p.monto) MontoTotal from pedidos p " +
                        "where idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "group by idComercio";
            }else if (fechaDesde == null || fechaDesde.isEmpty()){
                Log.d("Reporte.casos", "2");
                query = "SELECT SUM(p.monto) MontoTotal from pedidos p " +
                        "where idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaHasta + "' "+
                        "group by idComercio";

            }else if (fechaHasta == null || fechaHasta.isEmpty()){
                Log.d("Reporte.casos", "3");
                query = "SELECT SUM(p.monto) MontoTotal from pedidos p " +
                        "where idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' "+
                        "group by idComercio";
            }else{
                Log.d("Reporte.casos", "4");
                query = "SELECT SUM(p.monto) MontoTotal from pedidos p " +
                        "where idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' " +
                        "AND p.fecha <= '" + fechaHasta + "' "+
                        "group by idComercio";
            }

            Log.d("reporte.query", query);


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        reporte = new Reporte();
                        reporte.setMontoTotal(rs.getInt("MontoTotal"));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return reporte;

    }


    public Reporte obtenerClienteUsual(Connection conn, int idUsuario, String fechaDesde, String fechaHasta ) {
        Reporte reporte = null;
        Usuario usuario = null;
        String query;
        try {
            if ((fechaDesde == null || fechaDesde.isEmpty())&& (fechaHasta== null || fechaHasta.isEmpty())){
                Log.d("Reporte.casos", "1");
                query = "SELECT count(p.idCliente) ClienteCantidad, u.nombre, u.apellido, u.nombreUsuario, u.direccion " +
                        "from pedidos p " +
                        "inner join clientes c on p.idCliente = c.idCliente " +
                        "inner join usuarios u on u.idUsuario = c.idUsuario " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "group by p.idCliente " +
                        "order by ClienteCantidad desc " +
                        "LIMIT 1";
            }else if (fechaDesde == null || fechaDesde.isEmpty()){
                Log.d("Reporte.casos", "2");
                query = "SELECT count(p.idCliente) ClienteCantidad, u.nombre, u.apellido, u.nombreUsuario, u.direccion " +
                        "from pedidos p " +
                        "inner join clientes c on p.idCliente = c.idCliente " +
                        "inner join usuarios u on u.idUsuario = c.idUsuario " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaHasta + "' "+
                        "group by p.idCliente " +
                        "order by ClienteCantidad desc " +
                        "LIMIT 1";
            }else if (fechaHasta == null || fechaHasta.isEmpty()){
                Log.d("Reporte.casos", "3");
                query = "SELECT count(p.idCliente) ClienteCantidad, u.nombre, u.apellido, u.nombreUsuario, u.direccion " +
                        "from pedidos p " +
                        "inner join clientes c on p.idCliente = c.idCliente " +
                        "inner join usuarios u on u.idUsuario = c.idUsuario " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' "+
                        "group by p.idCliente " +
                        "order by ClienteCantidad desc " +
                        "LIMIT 1";
            }else{
                Log.d("Reporte.casos", "4");
                query = "SELECT count(p.idCliente) ClienteCantidad, u.nombre, u.apellido, u.nombreUsuario, u.direccion " +
                        "from pedidos p " +
                        "inner join clientes c on p.idCliente = c.idCliente " +
                        "inner join usuarios u on u.idUsuario = c.idUsuario " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' " +
                        "AND p.fecha <= '" + fechaHasta + "' "+
                        "group by p.idCliente " +
                        "order by ClienteCantidad desc " +
                        "LIMIT 1";
            }

            Log.d("reporte.query", query);


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        usuario = new Usuario();
                        reporte = new Reporte();

                        usuario.setNombre(rs.getString("u.nombre"));
                        usuario.setApellido(rs.getString("u.apellido"));
                        usuario.setNombreUsuario(rs.getString("u.nombreUsuario"));
                        usuario.setDireccion(rs.getString("u.direccion"));
                        reporte.setUsuario(usuario);
                        reporte.setCantidadUsuario(rs.getInt("ClienteCantidad"));


                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return reporte;

    }


    public Reporte obtenerMedioPagoMasUsado(Connection conn, int idUsuario, String fechaDesde, String fechaHasta ) {
        Reporte reporte = null;
        String query;

        try {
            if ((fechaDesde == null || fechaDesde.isEmpty())&& (fechaHasta== null || fechaHasta.isEmpty())){
                Log.d("Reporte.casos", "1");
                query = "select p.medioPago MedioPago, COUNT(p.medioPago) cantidadMedioPago from pedidos p " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "group by p.medioPago " +
                        "order by cantidadMedioPago desc " +
                        "LIMIT 1";
            }else if (fechaDesde == null || fechaDesde.isEmpty()){
                Log.d("Reporte.casos", "2");
                query = "select p.medioPago MedioPago, COUNT(p.medioPago) cantidadMedioPago from pedidos p " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaHasta + "' "+
                        "group by p.medioPago " +
                        "order by cantidadMedioPago desc " +
                        "LIMIT 1";
            }else if (fechaHasta == null || fechaHasta.isEmpty()){
                Log.d("Reporte.casos", "3");
                query = "select p.medioPago MedioPago, COUNT(p.medioPago) cantidadMedioPago from pedidos p " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' "+
                        "group by p.medioPago " +
                        "order by cantidadMedioPago desc " +
                        "LIMIT 1";
            }else{
                Log.d("Reporte.casos", "4");
                query = "select p.medioPago MedioPago, COUNT(p.medioPago) cantidadMedioPago from pedidos p " +
                        "where p.idComercio = (SELECT idComercio from usuarios u1 " +
                        "INNER JOIN comercios c1 ON u1.idUsuario = c1.idUsuario " +
                        "WHERE u1.idUsuario = " + idUsuario +
                        ") " +
                        "AND p.fecha >= '" + fechaDesde + "' " +
                        "AND p.fecha <= '" + fechaHasta + "' "+
                        "group by p.medioPago " +
                        "order by cantidadMedioPago desc " +
                        "LIMIT 1";
            }

            Log.d("reporte.query", query);


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        reporte = new Reporte();
                        reporte.setCantidadMedioPago(rs.getInt("cantidadMedioPago"));
                        reporte.setMedioPago(rs.getInt("MedioPago"));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return reporte;

    }


    public Boolean cambiarEstadoPedido(Connection conn, Pedido pedido, Integer estado) throws SQLException {
        Boolean exito = false;
        PreparedStatement pstmt = null;

        try {
            if (conn != null) {

                String updateQuery = "UPDATE pedidos SET " +
                        "estado = ? " +
                        "WHERE idPedido = ?";

                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, estado);
                pstmt.setInt(2, pedido.getIdPedido());

                int filas_modificadas= pstmt.executeUpdate();

                String insertQueryH = "INSERT INTO historial(" +
                        "idCliente,idPedido,fecha,estado" +
                        ") VALUES (?,?,?,?)";

                pstmt = conn.prepareStatement(insertQueryH);
                pstmt.setInt(1, pedido.getCliente().getIdCliente());
                pstmt.setInt(2, pedido.getIdPedido());
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());
                pstmt.setDate(3, (Date) fechaActual);
                pstmt.setInt(4, estado);
                pstmt.executeUpdate();

                String insertQueryM = "INSERT INTO motivos(" +
                        "fecha,idPedido,motivo" +
                        ") VALUES (?,?,?)";

                pstmt = conn.prepareStatement(insertQueryM);
                Date fechaCancelacion = new Date(Calendar.getInstance().getTime().getTime());
                pstmt.setDate(1, (Date) fechaCancelacion);
                pstmt.setInt(2, pedido.getIdPedido());
                pstmt.setString(3, pedido.getMotivoCancelacion());
                pstmt.executeUpdate();

                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }

        if (pstmt != null) {
            pstmt.close();
        }
        if (conn != null) {
            conn.close();
        }
        return exito;
    }

    public Pedido obtenerPedidoXid(Connection conn, int idPedido) {
        Pedido pedido = new Pedido();
        try {
            String query = "select p.idPedido,p.idCliente,p.idComercio,p.monto,p.fecha,p.estado,p.medioPago,m.motivo from pedidos p " +
                    " left join motivos m on m.idPedido = p.idPedido where p.idPedido = " + idPedido;

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        pedido.setIdPedido(rs.getInt("idPedido"));
                        Cliente cliente = new Cliente();
                        cliente.setIdCliente(rs.getInt("idCliente"));
                        pedido.setCliente(cliente);
                        Comercio comercio = new Comercio();
                        comercio.setIdComercio(rs.getInt("idComercio"));
                        pedido.setComercio(comercio);
                        pedido.setMonto(rs.getFloat("monto"));
                        pedido.setFecha(rs.getDate("fecha"));
                        pedido.setEstado(rs.getInt("estado"));
                        pedido.setMedioPago(rs.getInt("medioPago"));

                        if(rs.getString("motivo") != null){
                            pedido.setMotivoCancelacion(rs.getString("motivo"));
                        }
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
        }

        return pedido;
    }



    ///////////////////////////////////////


    public ArrayList<Pedido> obtenerListadoPedidosFiltrado (
            ArrayList<Pedido> listadoPedido, String fechaDesde, String fechaHasta,
            boolean entregado, boolean cancelado, boolean pendiente, String orden){

        ArrayList<Pedido> listadoFiltrado = listadoPedido;

        listadoFiltrado = filtrarRangoFechas (listadoFiltrado, fechaDesde, fechaHasta);
        listadoFiltrado = filtrarXEstado (listadoFiltrado, entregado, cancelado, pendiente);
        ordenarListado(listadoFiltrado, orden);


        return listadoFiltrado;
    }

    private ArrayList<Pedido> filtrarRangoFechas(ArrayList<Pedido> lista, String fechaDesde, String fechaHasta) {
        ArrayList<Pedido> listaFiltrada = new ArrayList<>();

        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatoBBDD = new SimpleDateFormat("yyyy-MM-dd");

            if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {       // con los dos limites
                java.util.Date fechaDesdeDate = formatoEntrada.parse(fechaDesde);
                java.util.Date fechaHastaDate = formatoEntrada.parse(fechaHasta);

                for (Pedido pedido : lista) {
                    java.util.Date fechaHistorial = pedido.getFecha();
                    String fechaHistorialDate = formatoBBDD.format(fechaHistorial);

                    if ((fechaHistorialDate.equals(formatoBBDD.format(fechaDesdeDate)) || fechaHistorial.after(fechaDesdeDate)) &&
                            (fechaHistorialDate.equals(formatoBBDD.format(fechaHastaDate)) || fechaHistorial.before(fechaHastaDate))) {
                        listaFiltrada.add(pedido);
                    }
                }
            } else if (!fechaDesde.isEmpty()) {         //caso solo fechaDesde
                java.util.Date fechaDesdeDate = formatoEntrada.parse(fechaDesde);

                for (Pedido pedido : lista) {
                    java.util.Date fechaHistorial = pedido.getFecha();
                    if (fechaHistorial.equals(fechaDesdeDate) || fechaHistorial.after(fechaDesdeDate)) {
                        listaFiltrada.add(pedido);
                    }
                }
            } else if (!fechaHasta.isEmpty()) {             //caso solo fechaHasta
                java.util.Date fechaHastaDate = formatoEntrada.parse(fechaHasta);

                for (Pedido pedido : lista) {
                    java.util.Date fechaHistorial = pedido.getFecha();
                    if (fechaHistorial.equals(fechaHastaDate) || fechaHistorial.before(fechaHastaDate)) {
                        listaFiltrada.add(pedido);
                    }
                }
            } else {                        // caso los dos vacios, no hace nada
                listaFiltrada = lista;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return listaFiltrada;
    }

    private ArrayList<Pedido> filtrarXEstado(ArrayList<Pedido> lista, boolean entregado, boolean cancelado, boolean pendiente) {
        ArrayList<Pedido> listaFiltrada = new ArrayList<>();

        if (!entregado && !pendiente && !cancelado) {
            return lista;
        }else {

            for (Pedido pedido : lista) {
                if (
                        (entregado && pedido.getEstado() == 2) ||              //entregado
                                (pendiente && pedido.getEstado() == 1) ||               //pendiente
                                (cancelado && pedido.getEstado() == 4)                  //cancelado
                ) {
                    listaFiltrada.add(pedido);
                }
            }
        }
        return listaFiltrada;
    }

    private void ordenarListado(ArrayList<Pedido> lista, String orden) {

        if ("en espera".equals(orden)) {
            Collections.sort(lista, Comparator.comparingInt(Pedido::getEstado));
        } else if ("recientes".equals(orden)) {
            Collections.sort(lista, Comparator.comparingInt(Pedido::getIdPedido).reversed());
        } else {      }


    }


}
