package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Entidad.Cliente;
import Entidad.Pedido;
import Entidad.Reporte;
import Entidad.Usuario;
import Entidad.pedidoXproducto;

public class consultasReportes {
    public static Reporte reporteSemanal(int id) {
        Reporte reporteSemanal = new Reporte();
        Reporte reporteAux = new Reporte();
        Conexion con = new Conexion();


        reporteAux = VentasTotales(con, id);
        reporteSemanal.setCantidadPedidos(reporteAux.getCantidadPedidos());

        reporteAux = ProductoMasVendido(con, id);
        reporteSemanal.setCantidadProducto(reporteAux.getCantidadProducto());
        reporteSemanal.setnombreProducto(reporteAux.getnombreProducto());

        reporteAux = FacturacionTotal(con, id);
        reporteSemanal.setMontoTotal(reporteAux.getMontoTotal());

        reporteAux = ClienteMasUsual(con, id);
        reporteSemanal.setUsuario(reporteAux.getUsuario());
        reporteSemanal.setCantidadUsuario(reporteAux.getCantidadUsuario());

        reporteAux = MedioPagoMasUsado(con, id);
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

    private static Reporte VentasTotales(Conexion con, int id) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerVentasTotales(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte ProductoMasVendido(Conexion con, int id) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerProductoMasVendido(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte FacturacionTotal(Conexion con, int id) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerProductoFacturacion(id);
            //Log.d("ReporteFinal.Nombre2: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }


    private static Reporte ClienteMasUsual(Conexion con, int id) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerClienteUsual(id);
            //Log.d("ReporteFinal3: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }

    private static Reporte MedioPagoMasUsado(Conexion con, int id) {
        Reporte reporte = new Reporte();
        try {
            reporte = con.obtenerMedioPagoMasUsado(id);
           // Log.d("ReporteFinal4: ", reporte.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reporte;
    }
}

