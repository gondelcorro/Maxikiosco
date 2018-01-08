package Negocio;

import Vista.VentanaLogin;
import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
//import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
//import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author gonzalo
 */
public class Principal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
                   // UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "ERROR: " + e);
                }
                ScriptTablas script = new ScriptTablas();
                try {
                    script.crearTablas();
                } catch (SQLException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
               new VentanaLogin().setVisible(true);  
               new DetectarCodigosRepetidos().detectarCodigosRepetidos();
            }
        });
    }

}
