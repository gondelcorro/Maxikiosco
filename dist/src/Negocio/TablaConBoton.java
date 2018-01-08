package Negocio;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class TablaConBoton extends JLabel implements TableCellRenderer {

    boolean isBordered = true;
    Icon icono = new ImageIcon("src/Imagenes/magnifier.png");

    public TablaConBoton(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
        // Va a mostrar el botón solo en la última fila.
        // de otra forma muestra un espacio en blanco.
        int i =0;
        while (i< table.getModel().getRowCount()) {
            i++;
            return new JButton(icono);     
        }
        return this; 
    }

}
   
