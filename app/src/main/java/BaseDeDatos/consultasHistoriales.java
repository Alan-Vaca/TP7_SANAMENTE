
package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Entidad.Cliente;
import Entidad.Comercio;
import Entidad.Historial;
import Entidad.Pedido;
import Entidad.Producto;
import Entidad.Usuario;

public class consultasHistoriales {
    public ArrayList<Historial> obtenerListadoHistorial(Connection conn, Usuario user) {
        ArrayList<Historial> listadoHistorial = new ArrayList<Historial>();

        try {
            String query = "select h.idHistorial as HidHistorial, h.idPedido as HidPedido, h.idCliente as HidCliente, h.fecha as Hfecha, h.estado as Hestado "
                    + "from historial h ";

            if(user.isCliente()) {
                query += "INNER JOIN ( SELECT idPedido, MAX(idHistorial) AS MaxIdHistorial FROM historial ";
                query += "GROUP BY idPedido) latest_h ON h.idPedido = latest_h.idPedido AND h.idHistorial = latest_h.MaxIdHistorial ";
                query += "INNER JOIN clientes c ON c.idCliente = h.idCliente ";
                query += "where c.idUsuario = " + user.getIdUsuario();
                query += " ORDER BY Hfecha DESC";
            }else{
                query += "inner join pedidos p on p.idPedido = h.idPedido ";
                query += "inner join clientes c on c.idCliente = h.idCliente ";
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


    public ArrayList<Historial> obtenerListadoHistorialesFiltrado (
            ArrayList<Historial> listadoHistorial, String fechaDesde, String fechaHasta,
            boolean entregado, boolean cancelado, boolean pendiente, String orden){

        ArrayList<Historial> listadoFiltrado = listadoHistorial;

        listadoFiltrado = filtrarRangoFechas (listadoFiltrado, fechaDesde, fechaHasta);
        listadoFiltrado = filtrarXEstado (listadoFiltrado, entregado, cancelado, pendiente);
        ordenarListado(listadoFiltrado, orden);


        return listadoFiltrado;
    }

    private ArrayList<Historial> filtrarRangoFechas(ArrayList<Historial> lista, String fechaDesde, String fechaHasta) {
        ArrayList<Historial> listaFiltrada = new ArrayList<>();

        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatoBBDD = new SimpleDateFormat("yyyy-MM-dd");

            if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {       // con los dos limites
                Date fechaDesdeDate = formatoEntrada.parse(fechaDesde);
                Date fechaHastaDate = formatoEntrada.parse(fechaHasta);

                for (Historial historial : lista) {
                    Date fechaHistorial = historial.getFecha();
                    String fechaHistorialDate = formatoBBDD.format(fechaHistorial);

                    if ((fechaHistorialDate.equals(formatoBBDD.format(fechaDesdeDate)) || fechaHistorial.after(fechaDesdeDate)) &&
                            (fechaHistorialDate.equals(formatoBBDD.format(fechaHastaDate)) || fechaHistorial.before(fechaHastaDate))) {
                        listaFiltrada.add(historial);
                    }
                }
            } else if (!fechaDesde.isEmpty()) {         //caso solo fechaDesde
                Date fechaDesdeDate = formatoEntrada.parse(fechaDesde);

                for (Historial historial : lista) {
                    Date fechaHistorial = historial.getFecha();
                    if (fechaHistorial.equals(fechaDesdeDate) || fechaHistorial.after(fechaDesdeDate)) {
                        listaFiltrada.add(historial);
                    }
                }
            } else if (!fechaHasta.isEmpty()) {             //caso solo fechaHasta
                Date fechaHastaDate = formatoEntrada.parse(fechaHasta);

                for (Historial historial : lista) {
                    Date fechaHistorial = historial.getFecha();
                    if (fechaHistorial.equals(fechaHastaDate) || fechaHistorial.before(fechaHastaDate)) {
                        listaFiltrada.add(historial);
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

    private ArrayList<Historial> filtrarXEstado(ArrayList<Historial> lista, boolean entregado, boolean cancelado, boolean pendiente) {
        ArrayList<Historial> listaFiltrada = new ArrayList<>();

        if (!entregado && !pendiente && !cancelado) {
            return lista;
        }else {

            for (Historial historial : lista) {
                if (
                        (entregado && historial.getEstado() == 2) ||              //entregado
                                (pendiente && historial.getEstado() == 1) ||               //pendiente
                                (cancelado && historial.getEstado() == 4)                  //cancelado
                ) {
                    Log.d("Filtro", historial.getEstadoString());
                    listaFiltrada.add(historial);
                }
            }
        }
        return listaFiltrada;
    }

    private void ordenarListado(ArrayList<Historial> lista, String orden) {
        Log.d("listadoHistorial.orden", String.valueOf(orden));
        switch (orden) {
            case "en espera":
                Collections.sort(lista, Comparator.comparingInt(Historial::getEstado));
                break;
            case "recientes":
                Collections.sort(lista, Comparator.comparingInt(Historial::getIdHistorial).reversed());
                break;
            default:
                break;
        }
    }



}