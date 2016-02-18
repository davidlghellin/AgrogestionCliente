package utils;

import conexion.ConexionBBDD;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Clase auxiliar para facilitar el manejor de JTable y su correspondientes
 * actualizaciones
 *
 * @author David López González
 */
public class UtilisSql
{

    /**
     * Método para comprobar si el usario está en la BBDD
     *
     * @param user
     * @param pass
     * @param frame
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void loginUser(String user, String pass, JFrame frame) throws ClassNotFoundException, SQLException
    {
        ConexionBBDD c = new ConexionBBDD();
        String DNI = null;
        String bbddPass = null;
        String tipo = null;
        try
        {
            ResultSet rs = c.hacerConsulta("SELECT DNI, Contrasenya,Tipo FROM TTrabajador WHERE DNI = \"" + user + "\";");
            rs.next();
            DNI = rs.getString(1);
            bbddPass = rs.getString(2);
            tipo = rs.getString(3);
            if (pass.equals(bbddPass) && tipo.equals("root"))
            {
                //TODO
                System.out.println("root");
                frame.dispose();
            } else if (pass.equals(bbddPass) && tipo.equals("normal"))
            {
                //TODO
                System.out.println("root");
                frame.dispose();
            } else
            {
                JOptionPane.showMessageDialog(frame, "Contraseña no válida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(frame, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Método genérico para rellenar un Jtable a partir de una consulta SQL, se
     * encarga solo de rellenar con los campos apropiados
     *
     * @param consulta
     * @param table
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws Exception
     */
    public static JTable rellenarJTable(String consulta, JTable table) throws ClassNotFoundException, SQLException, Exception
    {
        JTable jtable = table;
        ConexionBBDD c = new ConexionBBDD();
        ResultSet rs = c.hacerConsulta(consulta);
        DefaultTableModel modelo = (DefaultTableModel) jtable.getModel();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cantidadColumnas;

        cantidadColumnas = rsmd.getColumnCount();

        modelo.setColumnCount(0);
        modelo.setRowCount(0);
        for (int i = 1; i <= cantidadColumnas; i++)
        {
            modelo.addColumn(rsmd.getColumnLabel(i));
        }
        while (rs.next())
        {
            Object[] fila = new Object[cantidadColumnas];
            for (int i = 0; i < cantidadColumnas; i++)
            {
                fila[i] = rs.getObject(i + 1);
            }
            modelo.addRow(fila);
        }
        rs.close();
        c.cerrarConexion();
        System.out.println(consulta);
        return jtable;
    }

    /**
     * Método genérico para borrar el elemento selecionado del jtable La clave
     * primaria es String
     *
     * @param nombreTabla Nombre de la tabla en el base de datos
     * @param table JTable donde se visualizará los datos
     * @throws Exception
     */
    public static void borrar(String nombreTabla, JTable table) throws Exception
    {
        JTable jtable = table;
        ConexionBBDD c = new ConexionBBDD();
        String SQLConsulta = "SELECT * FROM " + nombreTabla + ";";
        ResultSet rs = c.hacerConsulta(SQLConsulta);
        ResultSetMetaData rsmd = rs.getMetaData();
        String id = null;
        try
        {
            id = (String) jtable.getValueAt(jtable.getSelectedRow(), 0);
            System.out.println(id + "__ID__");
            try
            {
                String SQLBorrar = "DELETE FROM " + nombreTabla + " WHERE " + rsmd.getColumnName(1) + " = \"" + id + "\";";
                System.out.println(SQLBorrar);
                c = new ConexionBBDD();
                if (!c.hacerBorrado(SQLBorrar))
                {
                    JOptionPane.showInternalMessageDialog(jtable.getRootPane(), "Hace referencia a otra tabla, revise  las otras tablas");
                }
                actualizarJtable(jtable, nombreTabla);
            } catch (Exception e)
            {
                // JOptionPane.showInternalMessageDialog(jtable.getRootPane(), "Hace referencia a otra tabla");
                Logger.getLogger(UtilisSql.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(jtable.getRootPane(), "Tiene que selecionar la fila a modificar");
        }
        c.cerrarConexion();
    }

    /**
     * Método genérico para borrar el elemento selecionado del jtable La clave
     * primaria es int
     *
     * @param nombreTabla Nombre de la tabla en el base de datos
     * @param table JTable donde se visualizará los datos
     * @throws Exception
     */
    public static void borrar(String nombreTabla, JTable table, int i) throws Exception
    {
        JTable jtable = table;
        ConexionBBDD c = new ConexionBBDD();
        String SQLConsulta = "SELECT * FROM " + nombreTabla + ";";
        ResultSet rs = c.hacerConsulta(SQLConsulta);
        ResultSetMetaData rsmd = rs.getMetaData();
        int id;
        try
        {
            id = (int) jtable.getValueAt(jtable.getSelectedRow(), 0);
            System.out.println(id + "__ID__");
            try
            {
                String SQLBorrar = "DELETE FROM " + nombreTabla + " WHERE " + rsmd.getColumnName(1) + " = \"" + id + "\";";
                System.out.println(SQLBorrar);
                c = new ConexionBBDD();
                c.hacerBorrado(SQLBorrar);
                actualizarJtable(jtable, nombreTabla);
            } catch (Exception e)
            {
                JOptionPane.showInternalMessageDialog(jtable.getRootPane(), "Hace referencia a otra tabla");
                //  Logger.getLogger(UtilisSql.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(jtable.getRootPane(), "Tiene que selecionar la fila a modificar");
        }
        c.cerrarConexion();
    }

    /**
     * Método para actualizar el JTable correspondiente
     *
     * @param jtable
     * @param tabla
     * @throws SQLException
     * @throws Exception
     */
    public static void actualizarJtable(JTable jtable, String tabla) throws SQLException, Exception
    {
        jtable = utils.UtilisSql.rellenarJTable("SELECT * FROM " + tabla + ";", jtable);
    }
}
