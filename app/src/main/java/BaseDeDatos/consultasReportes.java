package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Entidad.Cliente;
import Entidad.Pedido;
import Entidad.Reporte;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class consultasReportes {
    public static Reporte reporteSemanal(int id, String fechaDesdeEntrada, String fechaHastaEntrada) throws ParseException {

        String fechaDesde = null;
        String fechaHasta = null;

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoBBDD = new SimpleDateFormat("yyyy-MM-dd");


        if(!fechaDesdeEntrada.isEmpty()){
            java.util.Date fechaDesdeDate = formatoEntrada.parse(fechaDesdeEntrada);
            fechaDesde = formatoBBDD.format(fechaDesdeDate);
        }

        if(!fechaHastaEntrada.isEmpty()){
            java.util.Date fechaHastaDate = formatoEntrada.parse(fechaHastaEntrada);
            fechaHasta = formatoBBDD.format(fechaHastaDate);
        }

        //(int id, String fechaDesde, String fechaHasta,
        //                                         boolean facturacion,boolean cliente, boolean producto )
    /* Facturacion
            Ventas Totales
            Facturación total
    */

     /* Cliente
            Cliente mas frecuente
            Cantidad de pedidos
            Medio de pago mas usado
      */

        /*
        Producto
            Producto mas vendido
            Cantidad de ventas
         */



        Reporte reporteSemanal = new Reporte();
        Reporte reporteAux = new Reporte();
        Conexion con = new Conexion();


        reporteAux = VentasTotales(con, id, fechaDesde, fechaHasta);
        reporteSemanal.setCantidadPedidos(reporteAux.getCantidadPedidos());

        reporteAux = ProductoMasVendido(con, id, fechaDesde, fechaHasta);
        reporteSemanal.setCantidadProducto(reporteAux.getCantidadProducto());
        reporteSemanal.setnombreProducto(reporteAux.getnombreProducto());

        reporteAux = FacturacionTotal(con, id, fechaDesde, fechaHasta);
        reporteSemanal.setMontoTotal(reporteAux.getMontoTotal());

        reporteAux = ClienteMasUsual(con, id, fechaDesde, fechaHasta);
        reporteSemanal.setUsuario(reporteAux.getUsuario());
        reporteSemanal.setCantidadUsuario(reporteAux.getCantidadUsuario());

        reporteAux = MedioPagoMasUsado(con, id, fechaDesde, fechaHasta);
        reporteSemanal.setCantidadMedioPago(reporteAux.getCantidadMedioPago());

        if (reporteAux.getMedioPago() == 0){
            reporteSemanal.setDescripcionPago("Efectivo");
        }else if (reporteAux.getMedioPago() == 1){
            reporteSemanal.setDescripcionPago("Tarjeta de debito");
        }else {
            reporteSemanal.setDescripcionPago("Tarjeta de credito");
        }
        Log.d("ReporteFinal: ", reporteSemanal.toString());

        return reporteSemanal;


    }

    private static Reporte VentasTotales(Conexion con, int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerVentasTotales(id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte ProductoMasVendido(Conexion con, int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerProductoMasVendido(id, fechaDesde, fechaHasta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte FacturacionTotal(Conexion con, int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerProductoFacturacion(id, fechaDesde, fechaHasta);
            //Log.d("ReporteFinal.Nombre2: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }


    private static Reporte ClienteMasUsual(Conexion con, int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerClienteUsual(id, fechaDesde, fechaHasta);
            //Log.d("ReporteFinal3: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte MedioPagoMasUsado(Conexion con, int id, String fechaDesde, String fechaHasta) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerMedioPagoMasUsado(id, fechaDesde, fechaHasta);
           // Log.d("ReporteFinal4: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }
}

