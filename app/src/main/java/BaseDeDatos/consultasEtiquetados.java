package BaseDeDatos;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Entidad.Alergia;
import Entidad.Etiquetado;
import Entidad.Producto;

public class consultasEtiquetados {

    //--------------------------------------------------------------------------------------
    //CONSULTAS DE TIPO SELECT
    //--------------------------------------------------------------------------------------
    public ArrayList<Etiquetado> obtenerListadoEtiquetado(Connection conn) {
        ArrayList<Etiquetado> listadoEtiquetado = new ArrayList<Etiquetado>();

        try {
            String query = "select idEtiquetado, descripcion, estado from etiquetados";
            Etiquetado etiquetadoInicial = new Etiquetado();
            etiquetadoInicial.setDescripcion("Seleccione...");
            etiquetadoInicial.setIdEtiquetado(0);
            etiquetadoInicial.setEstado(false);
            listadoEtiquetado.add(etiquetadoInicial);

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Etiquetado etiquetado = new Etiquetado();

                        etiquetado.setIdEtiquetado(rs.getInt("idEtiquetado"));
                        etiquetado.setDescripcion(rs.getString("descripcion"));
                        etiquetado.setEstado(rs.getBoolean("estado"));

                        listadoEtiquetado.add(etiquetado);
                    }
                    Etiquetado etiquetado = new Etiquetado();
                    if(listadoEtiquetado.size() == 0){
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                    } else if (listadoEtiquetado.size() == 1) {
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                    }
                    else if (listadoEtiquetado.size() == 2) {
                        listadoEtiquetado.add(etiquetado);
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

        return listadoEtiquetado;
    }

        public ArrayList<Etiquetado> obtenerListadoEtiquetadoXproducto(Connection conn, Producto producto) {
        ArrayList<Etiquetado> listadoEtiquetado = new ArrayList<Etiquetado>();

        try {
            String query = "select e.idEtiquetado as PidEtiquetado, e.descripcion as Pdescripcion, e.estado as Pestado from etiquetados e "+
                    "inner join productoXetiquetado ep on e.idEtiquetado = ep.idEtiquetado where ep.idProducto = " + producto.getIdProducto();


            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Etiquetado etiquetado = new Etiquetado();

                        etiquetado.setIdEtiquetado(rs.getInt("PidEtiquetado"));
                        etiquetado.setDescripcion(rs.getString("Pdescripcion"));
                        etiquetado.setEstado(rs.getBoolean("Pestado"));

                        listadoEtiquetado.add(etiquetado);
                    }

                    Etiquetado etiquetado = new Etiquetado();
                    if(listadoEtiquetado.size() == 0){
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                    } else if (listadoEtiquetado.size() == 1) {
                        listadoEtiquetado.add(etiquetado);
                        listadoEtiquetado.add(etiquetado);
                    }
                    else if (listadoEtiquetado.size() == 2) {
                        listadoEtiquetado.add(etiquetado);
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

        return listadoEtiquetado;
    }


    public void agregarProductoXetiquetado(Connection conn, int idEtiquetado) {
        try {
            if (conn != null) {
                String selectMaxIdQuery = "SELECT max(idProducto) FROM productos";
                PreparedStatement selectMaxIdStmt = conn.prepareStatement(selectMaxIdQuery);
                ResultSet resultSet = selectMaxIdStmt.executeQuery();
                int maxIdProducto = 0; // Valor predeterminado en caso de que no se encuentre ningún resultado
                if (resultSet.next()) {
                    maxIdProducto = resultSet.getInt(1);
                }

                String insertQuery = "INSERT INTO productoXetiquetado(" +
                        "idProducto, idEtiquetado" +
                        ") VALUES (?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, maxIdProducto);
                pstmt.setInt(2, idEtiquetado);
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }

    public void agregarProductoXetiquetadoXID(Connection conn, int idEtiquetado, int idProducto) {
        try {
            if (conn != null) {


                String insertQuery = "INSERT INTO productoXetiquetado(" +
                        "idProducto, idEtiquetado" +
                        ") VALUES (?,?)";

                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, idProducto);
                pstmt.setInt(2, idEtiquetado);
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
    //CONSULTA DE TIPO DELETE
    //--------------------------------------------------------------------------------------

    public ArrayList<Alergia> obtenerListadoAlergias(Connection conn) {
        ArrayList<Alergia> listadoAlergias = new ArrayList<Alergia>();

        try {
            String query = "select idAlergia, descripcionAlergia,ingredientesAlergicos from alergias";
            Alergia alergiaInicial = new Alergia();
            alergiaInicial.setDescripcionAlergia("Seleccione...");
            alergiaInicial.setIdAlergia(0);
            alergiaInicial.setIngredientesAlergicos("");
            listadoAlergias.add(alergiaInicial);

            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Alergia alergia = new Alergia();

                        alergia.setIdAlergia(rs.getInt("idAlergia"));
                        alergia.setDescripcionAlergia(rs.getString("descripcionAlergia"));
                        alergia.setIngredientesAlergicos(rs.getString("ingredientesAlergicos"));

                        listadoAlergias.add(alergia);
                    }
                    Alergia alergia = new Alergia();
                    if(listadoAlergias.size() == 0){
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                    } else if (listadoAlergias.size() == 1) {
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                    }
                    else if (listadoAlergias.size() == 2) {
                        listadoAlergias.add(alergia);
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

        return listadoAlergias;
    }

    public void eliminarProductoXetiquetado(Connection conn,int idProducto) {
        try {
            if (conn != null) {
                String deleteQuery = "DELETE FROM productoXetiquetado WHERE idProducto = ?";

                PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
                pstmt.setInt(1, idProducto);
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();
            }
        } catch (Exception e) {
            Log.d("ERROR-DB", e.toString());
            e.printStackTrace();
        }
    }


    public ArrayList<Alergia> obtenerListadoAlergiasXusuario(Connection conn,int idUsuario) {
        ArrayList<Alergia> listadoAlergias = new ArrayList<Alergia>();

        try {
            String query = "select a.idAlergia, a.descripcionAlergia,a.ingredientesAlergicos from alergias a " +
            "inner join alergiasXcliente ac on ac.idAlergia = a.idAlergia " +
            "inner join clientes c on c.idCliente = ac.idCliente where c.idUsuario = " + idUsuario ;
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Alergia alergia = new Alergia();

                        alergia.setIdAlergia(rs.getInt("idAlergia"));
                        alergia.setDescripcionAlergia(rs.getString("descripcionAlergia"));
                        alergia.setIngredientesAlergicos(rs.getString("ingredientesAlergicos"));

                        listadoAlergias.add(alergia);
                    }
                    Alergia alergia = new Alergia();
                    if(listadoAlergias.size() == 0){
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                    } else if (listadoAlergias.size() == 1) {
                        listadoAlergias.add(alergia);
                        listadoAlergias.add(alergia);
                    }
                    else if (listadoAlergias.size() == 2) {
                        listadoAlergias.add(alergia);
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

        return listadoAlergias;
    }
}
