package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Usuario;

public class consultasHistoriales {
    public ArrayList<Historial> obtenerListadoHistorial(Connection conn, Usuario user) {
        ArrayList<Historial> listadoHistorial = new ArrayList<Historial>();

        try {
            String query = "select h.idHistorial as HidHistorial, h.idPedido as HidPedido, h.idCliente as HidCliente, h.fecha as Hfecha, h.estado as Hestado "
                    + "from historial h "
                    + "inner join pedidos p on p.idPedido = h.idPedido "
                    + "inner join clientes c on c.idCliente = h.idCliente ";

                    if(user.isCliente()) {
                        query += "where c.idUsuario = " + user.getIdUsuario() + " order by h.estado";
                    }else{
                        query += "inner join comercios cc on cc.idComercio = p.idComercio where cc.idUsuario = " + user.getIdUsuario() + " order by h.estado";
                    }



            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Historial historial = new Historial();
                        Pedido pedido = new Pedido();
                        Cliente cliente = new Cliente();

                        historial.setIdHistorial(rs.getInt("HidHistorial"));
                        pedido.setIdPedido(rs.getInt("HidPedido"));
                        cliente.setIdCliente(rs.getInt("HidCliente"));
                        cliente.setUsuarioAsociado(user);

                        historial.setClientePedido(cliente);
                        historial.setPedidoRealizado(pedido);

                        historial.setFecha(rs.getDate("Hfecha"));
                        historial.setEstado(rs.getInt("Hestado"));

                        listadoHistorial.add(historial);
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

        return listadoHistorial;
    }




    public ArrayList<Pedido> obtenerListadoPedidos(Connection conn, Usuario user) {
        ArrayList<Pedido> listadoPedido = new ArrayList<Pedido>();

        try {
            String query = "select distinct p.idPedido PidPedido, p.idCliente PidCliente, p.idComercio PidComercio, p.monto Pmonto, p.fecha Pfecha, p.estado Pestado, p.medioPago PmedioPago "
                    + "from pedidos p "
                    + "inner join pedidoXproducto pp on pp.idPedido = p.idPedido "
                    + "inner join clientes c on c.idCliente = p.idCliente "
                    + "inner join comercios m on m.idComercio = p.idComercio "
                    + "where m.idUsuario = " + user.getIdUsuario() + " order by p.estado, p.fecha";



            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Pedido pedido = new Pedido();
                        Cliente cliente = new Cliente();
                        Comercio comercio = new Comercio();

                        pedido.setIdPedido(rs.getInt("PidPedido"));
                        pedido.setMonto(rs.getFloat("Pmonto"));
                        pedido.setFecha(rs.getDate("Pfecha"));
                        pedido.setMedioPago(rs.getInt("PmedioPago"));
                        pedido.setEstado(rs.getInt("Pestado"));

                        cliente.setIdCliente(rs.getInt("PidCliente"));
                        pedido.setCliente(cliente);

                        comercio.setUsuarioAsociado(user);
                        comercio.setIdComercio(rs.getInt("PidComercio"));
                        pedido.setComercio(comercio);

                        listadoPedido.add(pedido);
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

}
