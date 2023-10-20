package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Entidad.CalificacionXcliente;

public class consultasCalificaciones {
    public Boolean calificarPuntaje(Connection conn, CalificacionXcliente calificacion) throws SQLException {
        Boolean exito = false;
        PreparedStatement pstmt = null; // Declarar el PreparedStatement fuera del bloque try para que pueda cerrarse en el bloque finally.

        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO calificaciones(" +
                        "idCliente,idProducto,calificacion,comentario" +
                        ") VALUES (?,?,?,?)";

                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, calificacion.getCliente().getIdCliente());
                pstmt.setInt(2, calificacion.getProducto().getIdProducto());
                pstmt.setFloat(3, calificacion.getCalificacion());
                pstmt.setString(4, calificacion.getComentario());
                pstmt.executeUpdate();

                exito = true;
            }
        } catch (SQLException e) {
            exito = false;
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                pstmt.close(); // Cierra el PreparedStatement en el bloque finally.
            }
            if (conn != null) {
                conn.close(); // Cierra la conexión en el bloque finally después de todas las operaciones.
            }
        }
        return exito;
    }


}
