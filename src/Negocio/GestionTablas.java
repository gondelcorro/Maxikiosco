package Negocio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class GestionTablas {

    public void editarCabeceraTabla(JTable tabla, DefaultTableModel dtm, String[] titulosCabecera) {

        JTableHeader cabeceraTabla = tabla.getTableHeader();
        Font fuente = new Font("Tahoma", 1, 15);
        cabeceraTabla.setFont(fuente);
        dtm.setColumnIdentifiers(titulosCabecera);
        tabla.setFont(new Font("Tahoma", 0, 14));
        tabla.setModel(dtm);
    }

    public void darFormatoTabla(JTable tabla) {

        TableCellRenderer tcr = tabla.getTableHeader().getDefaultRenderer();
        tabla.setDefaultRenderer(Object.class, new formatoTabla(tcr));
    }

    public void fijarAnchoColumnasTabla(JTable tabla, int[] vectorAncho) {

        for (int i = 0; i < tabla.getColumnCount(); i++) {

            tabla.getColumnModel().getColumn(i).setPreferredWidth(vectorAncho[i]);
        }
    }

    public void ocultarColumnaTabla(JTable tabla, int columna) {

        tabla.getColumnModel().getColumn(columna).setMinWidth(0);
        tabla.getTableHeader().getColumnModel().getColumn(columna).setMaxWidth(0);
        tabla.getTableHeader().getColumnModel().getColumn(columna).setMinWidth(0);
    }

    public void limparTabla(JTable tabla, DefaultTableModel dtm) {

        int i, f;
        f = tabla.getRowCount();
        if (f > 0) {
            for (i = 0; i < f; i++) {
                dtm.removeRow(0);
            }
        }
    }

    class formatoTabla extends DefaultTableCellRenderer {

        TableCellRenderer tcr;

        public formatoTabla(TableCellRenderer renderer) {
            tcr = renderer;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {

            // PARA CENTRAR LAS CELDAS
            javax.swing.JComponent wth = (javax.swing.JComponent) tcr.getTableCellRendererComponent(table, value, selected, focused, row, column);
            javax.swing.JLabel label = (javax.swing.JLabel) wth;
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setFont(new Font("Tahoma", Font.BOLD, 14));
            setHorizontalAlignment(SwingConstants.CENTER);

            // SI LA TABLA SE LLAMA TABLAARTICULOS
            try {
                if (table.getName().equalsIgnoreCase("tablaStock")) {
                    int x = row / 2;
                    if (x * 2 == row) {
                        float stock = Float.parseFloat(String.valueOf(table.getValueAt(row, 3)));
                        if (stock <= 0) {
                            this.setBackground(new Color(255, 102, 102)); //Rojo claro
                        } else {
                            // setBackground(new Color(255, 228, 179));
                            setBackground(Color.white);
                        }
                    } else {
                        float stock = Float.parseFloat(String.valueOf(table.getValueAt(row, 3)));
                        if (stock <= 0) {
                            this.setBackground(new Color(255, 102, 102)); //Rojo claro
                        } else {
                            // setBackground(new Color(255, 228, 179));
                            setBackground(new Color(255, 228, 179));
                        }
                    }
                } else {
                    int x = row / 2;
                    if (x * 2 == row) {
                        setBackground(Color.white);
                    } else {
                        setBackground(new Color(255, 228, 179));
                    }
                }
            } catch (NullPointerException e) {

                int x = row / 2;
                if (x * 2 == row) {
                    setBackground(Color.white);
                } else {
                    setBackground(new Color(255, 228, 179));
                }
            }
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);

            return this;
        }
    }

}
