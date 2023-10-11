package BaseDeDatos;

import android.util.Log;

import com.example.tp7_sanamente.R;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import Entidad.Restriccion;

public class consultasRestricciones {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public Restriccion obtenerRestriccion(Connection conn, int idUsuario) {
        Restriccion res = new Restriccion();
        try {
            String query = "select r.alergico ResAlergico, r.celiaco ResCeliaco, r.diabetico ResDiabetico, r.hipertenso ResHipertenso, r.idRestriccion ResID from restricciones r " +
                    "inner join restriccionXcliente rc on rc.idRestriccion = r.idRestriccion " +
                    "inner join clientes c on c.idCliente = rc.idCliente " +
                    "where c.idUsuario = " + idUsuario;

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        res.setAlergico(rs.getString("ResAlergico"));
                        res.setCeliaco(rs.getBoolean("ResCeliaco"));
                        res.setDiabetico(rs.getBoolean("ResDiabetico"));
                        res.setHipertenso(rs.getBoolean("ResHipertenso"));
                        res.setIdRestriccion(rs.getInt("ResId"));

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

        return res;
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO INSERT
    //--------------------------------------------------------------------------------------
    public void altaRestriccion(Connection conn, Restriccion rest) {
        try {
            if (conn != null) {
                String insertQuery = "INSERT INTO restricciones(alergico,celiaco,diabetico,hipertenso) VALUES (?,?,?,?)";
                Date fechaActual = new Date(Calendar.getInstance().getTime().getTime());

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, rest.getAlergico());
                pstmt.setBoolean(2, rest.isCeliaco());
                pstmt.setBoolean(3, rest.isDiabetico());
                pstmt.setBoolean(4, rest.isHipertenso());

                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    public void altaRestriccionXcliente(Connection conn) {
        try {
            if (conn != null) {
                String selectMaxIdQuery = "SELECT max(idRestriccion) FROM restricciones";
                PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                ResultSet resultSet = selectMaxIdStmt.executeQuery();
                int maxIdRestriccion = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxIdRestriccion = resultSet.getInt(1);
                }

                selectMaxIdQuery = "SELECT max(idCliente) FROM clientes";
                selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                resultSet = selectMaxIdStmt.executeQuery();
                int maxIdCliente = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxIdCliente = resultSet.getInt(1);
                }

                String insertQuery = "INSERT INTO restriccionXcliente(idRestriccion,idCliente) VALUES (?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, maxIdRestriccion);
                pstmt.setInt(2, maxIdCliente);
                pstmt.executeUpdate();



                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------
    //CONSULTA DE TIPO UPDATE
    //--------------------------------------------------------------------------------------
    public void modificarRestriccion(Connection conn, Restriccion rest) {

        try {
            if (conn != null) {
                String updateQuery = "UPDATE restricciones SET " +
                        "alergico = ?,celiaco = ?,diabetico = ?,hipertenso = ? " +
                        "WHERE idRestriccion = ?";

                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setString(1, rest.getAlergico());
                pstmt.setBoolean(2, rest.isCeliaco());
                pstmt.setBoolean(3, rest.isDiabetico());
                pstmt.setBoolean(4, rest.isHipertenso());

                pstmt.setInt(5, rest.getIdRestriccion());

                int filas_modificadas= pstmt.executeUpdate();
                pstmt.close();
                conn.close();
            }
            else {
                Log.d("ERROR-DB", "NO hay conexion");
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }
}
