package Negocio;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDefaultScriptlet;


public class CalculaStock extends JRDefaultScriptlet {  // EXTIENDE DE ESA CLASE PARA PODER USARLO COMO SCRIPLET EN IREPORT

    Connection conexion = null;
    Statement st = null;
    ResultSet rs = null;

    public Integer calcularStock(Long codArt) {

        int cantActual = 0;
         try {
            conexion = Conexion.getConexion();
            st = conexion.createStatement();
            rs = st.executeQuery("SELECT sto_cantActual FROM Stock WHERE sto_codigo_art='"+codArt+"' ORDER BY sto_id DESC LIMIT 1");
            while(rs.next()){
                cantActual=Integer.parseInt(rs.getString("sto_cantActual"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(CalculaStock.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(CalculaStock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return cantActual;
    }
}