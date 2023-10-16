package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Pedido;
import Entidad.Producto;
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
                }

                String insertQueryH = "INSERT INTO historial(" +
                        "idCliente,idPedido,fecha,estado" +
                        ") VALUES (?,?,?,?)";

                pstmt = conn.prepareStatement(insertQueryH);
                pstmt.setInt(1, idCliente);
                pstmt.setInt(2, idPedido);
                pstmt.setDate(3, (Date) pedido.getFecha());
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

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------

}
