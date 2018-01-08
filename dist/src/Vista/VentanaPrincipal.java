package Vista;

import Controladores.ArtVenJpaController;
import Controladores.ArticulosJpaController;
import Controladores.CajaJpaController;
import Controladores.ProveedorJpaController;
import Controladores.RubroJpaController;
import Controladores.UsuarioJpaController;
import Controladores.VentasJpaController;
import Controladores.exceptions.NonexistentEntityException;
import Modelo.ArtVen;
import Modelo.Articulos;
import Modelo.Caja;
import Modelo.Proveedor;
import Modelo.Rubro;
import Modelo.Usuario;
import Modelo.Ventas;
import Negocio.FechaYHora;
import Negocio.GestionArticulos;
import Negocio.GestionTablas;
import Negocio.GestionVentas;
import Negocio.EditorTablaConBoton;
import Negocio.GestionStock;
import Negocio.GestionCaja;
import Negocio.GestionReportes;
import Negocio.GestionUsuarios;
import Negocio.TablaConBoton;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class VentanaPrincipal extends javax.swing.JFrame {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");
    private String usuario;
    private int tipoUsuario;
    String rutaReporte = "";
    String nombreTarjeta = "";

    public VentanaPrincipal(String usuario, int tipoUsuario) {
        initComponents();
        this.usuario = usuario;
        this.tipoUsuario = tipoUsuario;
        this.setSize(1024, 768);
        this.setLocationRelativeTo(null);
        // this.setExtendedState(this.MAXIMIZED_BOTH); // PARA INICIAR MAXIMIZADO EL FRAME
        inicializarTablas();
        jTextFieldCodigo.requestFocus();
        jTextFieldTotal.setEditable(false);
        verificarUsuario();
    }

    private void inicializarTablas() {

        GestionTablas gestionTabla = new GestionTablas();
        gestionTabla.darFormatoTabla(jTableCarrito);
        //PRIMERO LOS TITULOS Y LUEGO EL ANCHO DE CADA COLUMNA
        String titulos1[] = {"CODIGO", "DESCRIPCION", "MARCA", "PRECIO", "CANTITDAD", "SUBTOTAL", "TIPO VENTA"};
        gestionTabla.editarCabeceraTabla(jTableCarrito, (DefaultTableModel) jTableCarrito.getModel(), titulos1);
        int[] ancho1 = {30, 210, 50, 50, 50, 50, 0};
        gestionTabla.fijarAnchoColumnasTabla(jTableCarrito, ancho1);
        gestionTabla.ocultarColumnaTabla(jTableCarrito, 6);
        gestionTabla.limparTabla(jTableCarrito, (DefaultTableModel) jTableCarrito.getModel());

        gestionTabla.darFormatoTabla(jTableArticulos);
        String titulos2[] = {"CODIGO", "DESCRIPCION", "MARCA", "$ COMPRA", "$ VENTA", "RUBRO", "PROVEEDOR"};
        gestionTabla.editarCabeceraTabla(jTableArticulos, (DefaultTableModel) jTableArticulos.getModel(), titulos2);
        int[] ancho2 = {60, 160, 80, 50, 50, 70, 70};
        gestionTabla.fijarAnchoColumnasTabla(jTableArticulos, ancho2);

        gestionTabla.darFormatoTabla(jTableStock);
        String titulos3[] = {"CODIGO", "DESCRIPCION", "MARCA", "STOCK"};
        gestionTabla.editarCabeceraTabla(jTableStock, (DefaultTableModel) jTableStock.getModel(), titulos3);
        int[] ancho3 = {60, 180, 90, 40};
        gestionTabla.fijarAnchoColumnasTabla(jTableStock, ancho3);

        gestionTabla.darFormatoTabla(jTableHistorialStock);
        String titulos5[] = {"CODIGO", "DESCRIPCION", "MARCA", "STOCK", "RECARGA", "FECHA", "VENTAS"};
        gestionTabla.editarCabeceraTabla(jTableHistorialStock, (DefaultTableModel) jTableHistorialStock.getModel(), titulos5);
        int[] ancho5 = {60, 180, 90, 50, 60, 50, 50};
        gestionTabla.fijarAnchoColumnasTabla(jTableHistorialStock, ancho5);

        gestionTabla.darFormatoTabla(jTableVentas);
        String titulos4[] = {"ID VENTA", "MONTO", "PAGO", "FECHA", "CAJA N°", "VENDEDOR", "DETALLE"};
        gestionTabla.editarCabeceraTabla(jTableVentas, (DefaultTableModel) jTableVentas.getModel(), titulos4);
        int[] ancho4 = {50, 80, 80, 80, 80, 80, 80};
        gestionTabla.fijarAnchoColumnasTabla(jTableVentas, ancho4);
        TableColumn columna;
        columna = jTableVentas.getColumnModel().getColumn(6);
        columna.setCellEditor(new EditorTablaConBoton(jTableVentas, (DefaultTableModel) jTableVentas.getModel(), jDialogVentas));
        columna.setCellRenderer(new TablaConBoton(true));

        gestionTabla.darFormatoTabla(jTableCaja);
        String titulos6[] = {"CAJA N°", "ENCARGADO", "CAJA INICIAL", "CAJA FINAL", "ESTADO", "DETALLE"};
        gestionTabla.editarCabeceraTabla(jTableCaja, (DefaultTableModel) jTableCaja.getModel(), titulos6);
        int[] ancho6 = {70, 60, 80, 90, 95, 80};
        gestionTabla.fijarAnchoColumnasTabla(jTableCaja, ancho6);
        TableColumn columna1;
        columna1 = jTableCaja.getColumnModel().getColumn(5);
        columna1.setCellEditor(new EditorTablaConBoton(jTableCaja, (DefaultTableModel) jTableCaja.getModel(), jDialogCaja));
        columna1.setCellRenderer(new TablaConBoton(true));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jDialogArticulos = new javax.swing.JDialog();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArticulos = new javax.swing.JTable();
        jButtonNuevoArticulo = new javax.swing.JButton();
        jButtonModificarArticulo = new javax.swing.JButton();
        jButtonEliminarArticulo = new javax.swing.JButton();
        jTextFieldBusquedaArticulo = new javax.swing.JTextField();
        jButtonReponerStock = new javax.swing.JButton();
        jDialogVentas = new javax.swing.JDialog();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTableVentas = new javax.swing.JTable();
        jButtonEliminarVenta = new javax.swing.JButton();
        jTextFieldBusquedaVentas = new javax.swing.JTextField();
        jDialogStock = new javax.swing.JDialog();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableStock = new javax.swing.JTable();
        jTextFieldBusquedaStock = new javax.swing.JTextField();
        jButtonReponerStock1 = new javax.swing.JButton();
        jButtonHistorialStock = new javax.swing.JButton();
        jDialogCaja = new javax.swing.JDialog();
        jPanel15 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldFecha = new javax.swing.JTextField();
        jTextFieldCajaNum = new javax.swing.JTextField();
        jTextFieldEncargado = new javax.swing.JTextField();
        jTextFieldHora = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jButtonApyCierreCaja = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTableCaja = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTextFieldCajaInicial = new javax.swing.JTextField();
        jTextFieldTotalIngresos = new javax.swing.JTextField();
        jTextFieldTotalEgresos = new javax.swing.JTextField();
        jTextFieldDebeHaber = new javax.swing.JTextField();
        jTextFieldHay = new javax.swing.JTextField();
        jDialogOpciones = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabelOpcionesDeReportes = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        arbolReportes = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableUsuarios = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldUsuario = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jComboBoxTipoUsuario = new javax.swing.JComboBox();
        jButtonRegistrarUsuario = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPasswordFieldContraseña = new javax.swing.JPasswordField();
        jButtonEliminarUsuario = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jRadioButtonRespaldar = new javax.swing.JRadioButton();
        jRadioButtonRestaurar = new javax.swing.JRadioButton();
        jTextFieldNombreArchivo = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonAceptar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jButtonSeleccionar = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jDialogAcercaDe = new javax.swing.JDialog();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jDialogABMArticulo = new javax.swing.JDialog();
        jPanel11 = new javax.swing.JPanel();
        jComboBoxRubro = new javax.swing.JComboBox();
        jTextFieldCodigoArticulo = new javax.swing.JTextField();
        jTextFieldMarcaArticulo = new javax.swing.JTextField();
        jTextFieldPrecioCompra = new javax.swing.JTextField();
        jTextFieldDescArticulo = new javax.swing.JTextField();
        jButtonRegistrarArticulo = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabelPrecioKilo = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextAreaDetalle = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jTextFieldPrecioVenta = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextFieldPorcUtilidad = new javax.swing.JTextField();
        jComboBoxProveedor = new javax.swing.JComboBox();
        jButtonNuevoRubro = new javax.swing.JButton();
        jButtonNuevoProveedor = new javax.swing.JButton();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jMenuItemEliminar = new javax.swing.JMenuItem();
        jPopupMenuEliminar = new javax.swing.JPopupMenu();
        jDialogRegistrarVenta = new javax.swing.JDialog();
        jPanel12 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldTotal = new javax.swing.JTextField();
        jTextFieldPagaCon = new javax.swing.JTextField();
        jButtonGuardarVenta = new javax.swing.JButton();
        jLabelVuelto = new javax.swing.JLabel();
        jComboBoxFormaPago = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jDialogHistorialStock = new javax.swing.JDialog();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTableHistorialStock = new javax.swing.JTable();
        jTextFieldBusquedaHistorialStock = new javax.swing.JTextField();
        jButtonReponerStock4 = new javax.swing.JButton();
        jPopupMenuDetalleArt = new javax.swing.JPopupMenu();
        jMenuItemDetalleArt = new javax.swing.JMenuItem();
        jPopupMenuCerrarCaja = new javax.swing.JPopupMenu();
        jMenuItemCerrarCaja = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jButtonArticulos = new javax.swing.JButton();
        jButtonVentas = new javax.swing.JButton();
        jButtonStock = new javax.swing.JButton();
        jButtonCaja = new javax.swing.JButton();
        jButtonOpciones = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCarrito = new javax.swing.JTable();
        jLabelTotal = new javax.swing.JLabel();
        jLabelTotalCarrito = new javax.swing.JLabel();
        jTextFieldCodigo = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelDescripcionArt = new javax.swing.JLabel();
        jButtonPresupuesto = new javax.swing.JButton();
        jButtonRegistrarVenta = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemCerrarSesion = new javax.swing.JMenuItem();
        jMenuItemSalir = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemManual = new javax.swing.JMenuItem();
        jMenuItemAcercaDe = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jDialogArticulos.setTitle("ARTICULOS");
        jDialogArticulos.setModal(true);
        jDialogArticulos.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialogArticulosWindowClosing(evt);
            }
        });

        jTableArticulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "DESCRIPCION", "MARCA", "PRECIO", "PRECIO KILO", "PESO", "DETALLE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableArticulos.setName("TablaArticulos"); // NOI18N
        jTableArticulos.setRowHeight(22);
        jTableArticulos.getTableHeader().setReorderingAllowed(false);
        jTableArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableArticulosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableArticulos);
        if (jTableArticulos.getColumnModel().getColumnCount() > 0) {
            jTableArticulos.getColumnModel().getColumn(0).setResizable(false);
            jTableArticulos.getColumnModel().getColumn(1).setResizable(false);
            jTableArticulos.getColumnModel().getColumn(2).setResizable(false);
            jTableArticulos.getColumnModel().getColumn(3).setResizable(false);
            jTableArticulos.getColumnModel().getColumn(4).setResizable(false);
            jTableArticulos.getColumnModel().getColumn(5).setResizable(false);
        }

        jButtonNuevoArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonNuevoArticulo.setText("Nuevo");
        jButtonNuevoArticulo.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonNuevoArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevoArticuloActionPerformed(evt);
            }
        });

        jButtonModificarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonModificarArticulo.setText("Modificar");
        jButtonModificarArticulo.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonModificarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModificarArticuloActionPerformed(evt);
            }
        });

        jButtonEliminarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonEliminarArticulo.setText("Eliminar");
        jButtonEliminarArticulo.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonEliminarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarArticuloActionPerformed(evt);
            }
        });

        jTextFieldBusquedaArticulo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextFieldBusquedaArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusquedaArticuloKeyReleased(evt);
            }
        });

        jButtonReponerStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonReponerStock.setText("Reponer Stock");
        jButtonReponerStock.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonReponerStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReponerStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButtonReponerStock, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonNuevoArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonModificarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jButtonEliminarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jTextFieldBusquedaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jTextFieldBusquedaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNuevoArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonModificarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEliminarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReponerStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jDialogArticulosLayout = new javax.swing.GroupLayout(jDialogArticulos.getContentPane());
        jDialogArticulos.getContentPane().setLayout(jDialogArticulosLayout);
        jDialogArticulosLayout.setHorizontalGroup(
            jDialogArticulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogArticulosLayout.setVerticalGroup(
            jDialogArticulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogArticulosLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jDialogVentas.setTitle("VENTAS");
        jDialogVentas.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialogVentasWindowClosing(evt);
            }
        });

        jTableVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID VENTA", "MONTO", "FECHA", "HORA", "TURNO", "VENDEDOR", "DETALLE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableVentas.setName("tablaVentas"); // NOI18N
        jTableVentas.setRowHeight(22);
        jTableVentas.getTableHeader().setReorderingAllowed(false);
        jTableVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableVentasMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(jTableVentas);
        if (jTableVentas.getColumnModel().getColumnCount() > 0) {
            jTableVentas.getColumnModel().getColumn(0).setResizable(false);
            jTableVentas.getColumnModel().getColumn(1).setResizable(false);
            jTableVentas.getColumnModel().getColumn(2).setResizable(false);
            jTableVentas.getColumnModel().getColumn(3).setResizable(false);
            jTableVentas.getColumnModel().getColumn(4).setResizable(false);
            jTableVentas.getColumnModel().getColumn(5).setResizable(false);
        }

        jButtonEliminarVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonEliminarVenta.setText("Eliminar");
        jButtonEliminarVenta.setEnabled(false);
        jButtonEliminarVenta.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonEliminarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarVentaActionPerformed(evt);
            }
        });

        jTextFieldBusquedaVentas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextFieldBusquedaVentas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusquedaVentasKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                        .addGroup(jPanel18Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jButtonEliminarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(35, 35, 35)))
                    .addComponent(jTextFieldBusquedaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jTextFieldBusquedaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(jButtonEliminarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jDialogVentasLayout = new javax.swing.GroupLayout(jDialogVentas.getContentPane());
        jDialogVentas.getContentPane().setLayout(jDialogVentasLayout);
        jDialogVentasLayout.setHorizontalGroup(
            jDialogVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogVentasLayout.setVerticalGroup(
            jDialogVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogVentasLayout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jDialogStock.setTitle("STOCK");
        jDialogStock.setModal(true);
        jDialogStock.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialogStockWindowClosing(evt);
            }
        });

        jTableStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "DESCRIPCION", "MARCA", "PRECIO", "Título 5", "Título 6", "Título 7"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableStock.setName("tablaStock"); // NOI18N
        jTableStock.setRowHeight(22);
        jTableStock.getTableHeader().setReorderingAllowed(false);
        jTableStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableStockMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(jTableStock);
        if (jTableStock.getColumnModel().getColumnCount() > 0) {
            jTableStock.getColumnModel().getColumn(0).setResizable(false);
            jTableStock.getColumnModel().getColumn(1).setResizable(false);
            jTableStock.getColumnModel().getColumn(2).setResizable(false);
            jTableStock.getColumnModel().getColumn(3).setResizable(false);
            jTableStock.getColumnModel().getColumn(4).setResizable(false);
            jTableStock.getColumnModel().getColumn(5).setResizable(false);
            jTableStock.getColumnModel().getColumn(6).setResizable(false);
        }

        jTextFieldBusquedaStock.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextFieldBusquedaStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusquedaStockKeyReleased(evt);
            }
        });

        jButtonReponerStock1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonReponerStock1.setText("Reponer Stock");
        jButtonReponerStock1.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonReponerStock1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReponerStock1ActionPerformed(evt);
            }
        });

        jButtonHistorialStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonHistorialStock.setText("Historial");
        jButtonHistorialStock.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonHistorialStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHistorialStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButtonHistorialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonReponerStock1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jTextFieldBusquedaStock, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jTextFieldBusquedaStock, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReponerStock1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHistorialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jDialogStockLayout = new javax.swing.GroupLayout(jDialogStock.getContentPane());
        jDialogStock.getContentPane().setLayout(jDialogStockLayout);
        jDialogStockLayout.setHorizontalGroup(
            jDialogStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogStockLayout.setVerticalGroup(
            jDialogStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogStockLayout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jDialogCaja.setTitle("CAJA");

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DATOS DE TURNO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("FECHA:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("CAJA NUMERO:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("ENCARGADO:");

        jTextFieldFecha.setEditable(false);
        jTextFieldFecha.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldFecha.setPreferredSize(new java.awt.Dimension(6, 22));

        jTextFieldCajaNum.setEditable(false);
        jTextFieldCajaNum.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldCajaNum.setPreferredSize(new java.awt.Dimension(6, 22));

        jTextFieldEncargado.setEditable(false);
        jTextFieldEncargado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldEncargado.setPreferredSize(new java.awt.Dimension(6, 22));

        jTextFieldHora.setEditable(false);
        jTextFieldHora.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldHora.setPreferredSize(new java.awt.Dimension(6, 22));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("HORA:");

        jButtonApyCierreCaja.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonApyCierreCaja.setText("Caja");
        jButtonApyCierreCaja.setPreferredSize(new java.awt.Dimension(77, 26));
        jButtonApyCierreCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApyCierreCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jButtonApyCierreCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldCajaNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldEncargado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(30, 30, 30))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jTextFieldFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHora, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldCajaNum, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTextFieldEncargado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonApyCierreCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jTableCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Título 2", "Title 2", "Title 3", "Título 5", "Título 6"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCaja.setName("tablaCaja"); // NOI18N
        jScrollPane10.setViewportView(jTableCaja);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DATOS DE CAJA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setText("CAJA INICIAL:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("TOTAL INGRESOS:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("TOTAL EGRESOS:");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("DEBE HABER:");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setText("HAY:");

        jTextFieldCajaInicial.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldCajaInicial.setText("0.0");
        jTextFieldCajaInicial.setPreferredSize(new java.awt.Dimension(6, 22));
        jTextFieldCajaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCajaInicialKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCajaInicialKeyTyped(evt);
            }
        });

        jTextFieldTotalIngresos.setEditable(false);
        jTextFieldTotalIngresos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldTotalIngresos.setText("0.0");
        jTextFieldTotalIngresos.setPreferredSize(new java.awt.Dimension(6, 22));
        jTextFieldTotalIngresos.setRequestFocusEnabled(false);
        jTextFieldTotalIngresos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldTotalIngresosKeyTyped(evt);
            }
        });

        jTextFieldTotalEgresos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldTotalEgresos.setText("0.0");
        jTextFieldTotalEgresos.setPreferredSize(new java.awt.Dimension(6, 22));
        jTextFieldTotalEgresos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldTotalEgresosKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldTotalEgresosKeyTyped(evt);
            }
        });

        jTextFieldDebeHaber.setEditable(false);
        jTextFieldDebeHaber.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldDebeHaber.setText("0.0");
        jTextFieldDebeHaber.setPreferredSize(new java.awt.Dimension(6, 22));

        jTextFieldHay.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldHay.setText("0.0");
        jTextFieldHay.setPreferredSize(new java.awt.Dimension(6, 22));
        jTextFieldHay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldHayKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldHayKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldHay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldDebeHaber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel24)
                                .addComponent(jLabel25))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldTotalEgresos, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldTotalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                            .addComponent(jLabel23)
                            .addGap(18, 18, 18)
                            .addComponent(jTextFieldCajaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextFieldCajaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextFieldTotalIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextFieldTotalEgresos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextFieldDebeHaber, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextFieldHay, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialogCajaLayout = new javax.swing.GroupLayout(jDialogCaja.getContentPane());
        jDialogCaja.getContentPane().setLayout(jDialogCajaLayout);
        jDialogCajaLayout.setHorizontalGroup(
            jDialogCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogCajaLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jDialogCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jDialogCajaLayout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30)
                        .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        jDialogCajaLayout.setVerticalGroup(
            jDialogCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogCajaLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jDialogCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(38, 38, 38)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        jDialogOpciones.setTitle("OPCIONES");

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel4.setToolTipText("");
        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabelOpcionesDeReportes.setFont(new java.awt.Font("Mangal", 1, 16)); // NOI18N
        jLabelOpcionesDeReportes.setText(" Opciones de Reportes");
        jLabelOpcionesDeReportes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        arbolReportes.setFont(new java.awt.Font("Mangal", 0, 14)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Reportes");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Artículos");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Listado completo de Articulos");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Informe de Articulos por Rubro");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Informe de Articulos por Proveedor");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Ventas");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Listado de Ventas Mensual");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Listado de Ventas con Tarjeta");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Listado de Ventas por Mayor");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Stock");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Informe de Stock");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Historial de Reposiciones");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Caja");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Planillas de Caja");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Estadistica Mensual");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        arbolReportes.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        arbolReportes.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                arbolReportesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(arbolReportes);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelOpcionesDeReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addGap(127, 127, 127))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabelOpcionesDeReportes)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addGap(35, 35, 35))
        );

        jTabbedPane1.addTab("Informes", null, jPanel4, "");

        jPanel5.setToolTipText("");
        jPanel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPanel13FocusGained(evt);
            }
        });

        jTableUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nº", "USUARIO", "TIPO"
            }
        ));
        jScrollPane5.setViewportView(jTableUsuarios);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("USUARIO:");

        jTextFieldUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("CONTRASEÑA:");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setText("TIPO DE USUARIO:");

        jComboBoxTipoUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBoxTipoUsuario.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- SELECCIONE --", "Administrador", "Personal" }));

        jButtonRegistrarUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonRegistrarUsuario.setText("Registrar");
        jButtonRegistrarUsuario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRegistrarUsuario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRegistrarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarUsuarioActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton10.setText("Cancelar");
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jPasswordFieldContraseña.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jButtonRegistrarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPasswordFieldContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel17)
                        .addComponent(jLabel15)
                        .addComponent(jLabel27)
                        .addComponent(jTextFieldUsuario)
                        .addComponent(jComboBoxTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jPasswordFieldContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(jButtonRegistrarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(93, Short.MAX_VALUE))
        );

        jButtonEliminarUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonEliminarUsuario.setText("Eliminar");
        jButtonEliminarUsuario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEliminarUsuario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEliminarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarUsuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jButtonEliminarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(45, 45, 45))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEliminarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73))))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Usuarios", jPanel5);

        jPanel6.setToolTipText("");

        buttonGroup1.add(jRadioButtonRespaldar);
        jRadioButtonRespaldar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jRadioButtonRespaldar.setText("RESPALDAR");

        buttonGroup1.add(jRadioButtonRestaurar);
        jRadioButtonRestaurar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jRadioButtonRestaurar.setText("RESTAURAR");

        jTextFieldNombreArchivo.setEditable(false);
        jTextFieldNombreArchivo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jButtonAceptar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonAceptar.setText("Aceptar");
        jButtonAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAceptarActionPerformed(evt);
            }
        });

        jButtonCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonSeleccionar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonSeleccionar.setText("Seleccionar");
        jButtonSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSeleccionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jRadioButtonRespaldar)
                                .addGap(30, 30, 30)
                                .addComponent(jRadioButtonRestaurar))
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                    .addComponent(jTextFieldNombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addComponent(jButtonAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonRespaldar)
                    .addComponent(jRadioButtonRestaurar))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSeleccionar))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Respaldo", jPanel6);

        javax.swing.GroupLayout jDialogOpcionesLayout = new javax.swing.GroupLayout(jDialogOpciones.getContentPane());
        jDialogOpciones.getContentPane().setLayout(jDialogOpcionesLayout);
        jDialogOpcionesLayout.setHorizontalGroup(
            jDialogOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogOpcionesLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 867, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jDialogOpcionesLayout.setVerticalGroup(
            jDialogOpcionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogOpcionesLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jDialogAcercaDe.setTitle("ACERCA DE...");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ecommerce-1024x569.png"))); // NOI18N

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("SISTEMA DE GESTIÓN DE VENTAS\nSistema operativo: Windows 8.1\nServidor de BD: MySql 5.5\nMVJ: JRE 1.8\nAutor: Gonzalo del Corro\nVersión: 1.1\n");
        jScrollPane6.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(jLabel5))
                .addGap(20, 20, 20))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jDialogAcercaDeLayout = new javax.swing.GroupLayout(jDialogAcercaDe.getContentPane());
        jDialogAcercaDe.getContentPane().setLayout(jDialogAcercaDeLayout);
        jDialogAcercaDeLayout.setHorizontalGroup(
            jDialogAcercaDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogAcercaDeLayout.setVerticalGroup(
            jDialogAcercaDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogAcercaDeLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialogABMArticulo.setTitle("NUEVO ARTICULO");
        jDialogABMArticulo.setModal(true);
        jDialogABMArticulo.setResizable(false);

        jComboBoxRubro.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- SELECCIONE -" }));

        jTextFieldCodigoArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCodigoArticuloKeyTyped(evt);
            }
        });

        jTextFieldPrecioCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPrecioCompraKeyTyped(evt);
            }
        });

        jButtonRegistrarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonRegistrarArticulo.setText("Ok");
        jButtonRegistrarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarArticuloActionPerformed(evt);
            }
        });
        jButtonRegistrarArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonRegistrarArticuloKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("CODIGO:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("RUBRO:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("DESCRIPCION:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("MARCA:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("COSTO:");

        jLabelPrecioKilo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelPrecioKilo.setText("DETALLE: (Opcional)");

        jTextAreaDetalle.setColumns(4);
        jTextAreaDetalle.setLineWrap(true);
        jTextAreaDetalle.setRows(3);
        jTextAreaDetalle.setWrapStyleWord(true);
        jTextAreaDetalle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaDetalleKeyReleased(evt);
            }
        });
        jScrollPane11.setViewportView(jTextAreaDetalle);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("PROVEEDOR:");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setText("PRECIO DE VENTA:");

        jTextFieldPrecioVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPrecioVentaKeyTyped(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel35.setText("UTILIDAD:");

        jTextFieldPorcUtilidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPorcUtilidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPorcUtilidadKeyTyped(evt);
            }
        });

        jComboBoxProveedor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- SELECCIONE -" }));

        jButtonNuevoRubro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonNuevoRubro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/add.png"))); // NOI18N
        jButtonNuevoRubro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevoRubroActionPerformed(evt);
            }
        });
        jButtonNuevoRubro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonNuevoRubroKeyPressed(evt);
            }
        });

        jButtonNuevoProveedor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonNuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/add.png"))); // NOI18N
        jButtonNuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevoProveedorActionPerformed(evt);
            }
        });
        jButtonNuevoProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonNuevoProveedorKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(jButtonRegistrarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jComboBoxProveedor, 0, 198, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNuevoProveedor))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jTextFieldPrecioCompra))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jTextFieldPorcUtilidad)))
                    .addComponent(jTextFieldCodigoArticulo)
                    .addComponent(jTextFieldMarcaArticulo)
                    .addComponent(jTextFieldDescArticulo)
                    .addComponent(jScrollPane11)
                    .addComponent(jTextFieldPrecioVenta)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel18)
                    .addComponent(jLabel34)
                    .addComponent(jLabelPrecioKilo)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jComboBoxRubro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNuevoRubro)))
                .addGap(50, 50, 50))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCodigoArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDescArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jTextFieldMarcaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPorcUtilidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxRubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNuevoRubro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNuevoProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelPrecioKilo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jButtonRegistrarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jDialogABMArticuloLayout = new javax.swing.GroupLayout(jDialogABMArticulo.getContentPane());
        jDialogABMArticulo.getContentPane().setLayout(jDialogABMArticuloLayout);
        jDialogABMArticuloLayout.setHorizontalGroup(
            jDialogABMArticuloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogABMArticuloLayout.setVerticalGroup(
            jDialogABMArticuloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jMenuItemEliminar.setText("Eliminar");
        jMenuItemEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEliminarActionPerformed(evt);
            }
        });

        jPopupMenuEliminar.setMinimumSize(new java.awt.Dimension(36, 36));

        jDialogRegistrarVenta.setTitle("REGISTRAR VENTA");
        jDialogRegistrarVenta.setModal(true);
        jDialogRegistrarVenta.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                jDialogRegistrarVentaWindowOpened(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("FangSong", 1, 40)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("VUELTO: $");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("TOTAL:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("PAGA CON:");

        jTextFieldTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTextFieldPagaCon.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldPagaCon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPagaConKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPagaConKeyTyped(evt);
            }
        });

        jButtonGuardarVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonGuardarVenta.setText("Guardar");
        jButtonGuardarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarVentaActionPerformed(evt);
            }
        });
        jButtonGuardarVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonGuardarVentaKeyPressed(evt);
            }
        });

        jLabelVuelto.setFont(new java.awt.Font("FangSong", 1, 40)); // NOI18N
        jLabelVuelto.setForeground(new java.awt.Color(204, 0, 0));
        jLabelVuelto.setText("0.00");

        jComboBoxFormaPago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBoxFormaPago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Efectivo", "Crédito", "Débito", "Por Mayor" }));
        jComboBoxFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFormaPagoActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("FORMA PAGO:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldTotal)
                    .addComponent(jComboBoxFormaPago, 0, 190, Short.MAX_VALUE)
                    .addComponent(jTextFieldPagaCon)
                    .addComponent(jButtonGuardarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabelVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(35, 35, 35)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextFieldPagaCon, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(jButtonGuardarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout jDialogRegistrarVentaLayout = new javax.swing.GroupLayout(jDialogRegistrarVenta.getContentPane());
        jDialogRegistrarVenta.getContentPane().setLayout(jDialogRegistrarVentaLayout);
        jDialogRegistrarVentaLayout.setHorizontalGroup(
            jDialogRegistrarVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDialogRegistrarVentaLayout.setVerticalGroup(
            jDialogRegistrarVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDialogHistorialStock.setTitle("HISTORIAL STOCK");
        jDialogHistorialStock.setAlwaysOnTop(true);
        jDialogHistorialStock.setModal(true);

        jTableHistorialStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "DESCRIPCION", "MARCA", "PRECIO", "PRECIO KILO", "PESO", "null"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableHistorialStock.setRowHeight(22);
        jTableHistorialStock.getTableHeader().setReorderingAllowed(false);
        jTableHistorialStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableHistorialStockMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(jTableHistorialStock);
        if (jTableHistorialStock.getColumnModel().getColumnCount() > 0) {
            jTableHistorialStock.getColumnModel().getColumn(0).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(1).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(2).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(3).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(4).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(5).setResizable(false);
            jTableHistorialStock.getColumnModel().getColumn(6).setResizable(false);
        }

        jTextFieldBusquedaHistorialStock.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextFieldBusquedaHistorialStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusquedaHistorialStockKeyReleased(evt);
            }
        });

        jButtonReponerStock4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonReponerStock4.setText("Stock");
        jButtonReponerStock4.setPreferredSize(new java.awt.Dimension(40, 26));
        jButtonReponerStock4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReponerStock4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButtonReponerStock4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 552, Short.MAX_VALUE))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jTextFieldBusquedaHistorialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jTextFieldBusquedaHistorialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(jButtonReponerStock4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jDialogHistorialStockLayout = new javax.swing.GroupLayout(jDialogHistorialStock.getContentPane());
        jDialogHistorialStock.getContentPane().setLayout(jDialogHistorialStockLayout);
        jDialogHistorialStockLayout.setHorizontalGroup(
            jDialogHistorialStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialogHistorialStockLayout.setVerticalGroup(
            jDialogHistorialStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogHistorialStockLayout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jMenuItemDetalleArt.setText("Aqui tendria que poner los detalles!!");

        jMenuItemCerrarCaja.setText("Cerrar caja");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MAXIKIOSCO ZONA SUR - SISTEMA DE GESTIÓN DE VENTAS");
        setMinimumSize(new java.awt.Dimension(1024, 768));

        jButtonArticulos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonArticulos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/basket_full.png"))); // NOI18N
        jButtonArticulos.setMnemonic('1');
        jButtonArticulos.setText("ARTICULOS");
        jButtonArticulos.setToolTipText("ALT + 1");
        jButtonArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonArticulosActionPerformed(evt);
            }
        });

        jButtonVentas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/total_plan_cost.png"))); // NOI18N
        jButtonVentas.setMnemonic('2');
        jButtonVentas.setText("VENTAS");
        jButtonVentas.setToolTipText("ALT + 2");
        jButtonVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVentasActionPerformed(evt);
            }
        });

        jButtonStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/baggage_cart_box.png"))); // NOI18N
        jButtonStock.setMnemonic('3');
        jButtonStock.setText("STOCK");
        jButtonStock.setToolTipText("ALT + 3");
        jButtonStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStockActionPerformed(evt);
            }
        });

        jButtonCaja.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/safe.png"))); // NOI18N
        jButtonCaja.setMnemonic('4');
        jButtonCaja.setText("CAJA");
        jButtonCaja.setToolTipText("ALT + 4");
        jButtonCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCajaActionPerformed(evt);
            }
        });

        jButtonOpciones.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonOpciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/applications-system.png"))); // NOI18N
        jButtonOpciones.setMnemonic('5');
        jButtonOpciones.setText("OPCIONES");
        jButtonOpciones.setToolTipText("ALT + 5");
        jButtonOpciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpcionesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCaja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonStock, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonVentas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonArticulos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(jButtonArticulos, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addComponent(jButtonVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addComponent(jButtonStock, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addComponent(jButtonCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addComponent(jButtonOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(100, 100, 100))
        );

        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        jTableCarrito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "CODIGO", "DESCRIPCION", "MARCA", "PRECIO", "CANTIDAD", "SUBTOTAL", "TIPO VENTA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarrito.setName("TablaCarrito"); // NOI18N
        jTableCarrito.setRowHeight(23);
        jTableCarrito.getTableHeader().setReorderingAllowed(false);
        jTableCarrito.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCarritoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCarrito);

        jLabelTotal.setFont(new java.awt.Font("FangSong", 1, 44)); // NOI18N
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotal.setText("TOTAL: $");

        jLabelTotalCarrito.setFont(new java.awt.Font("FangSong", 1, 44)); // NOI18N
        jLabelTotalCarrito.setForeground(new java.awt.Color(204, 51, 0));
        jLabelTotalCarrito.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelTotalCarrito.setText("0.00");

        jTextFieldCodigo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCodigoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCodigoKeyTyped(evt);
            }
        });

        jSpinner1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("FangSong", 1, 17)); // NOI18N
        jLabel1.setText("CODIGO:");

        jLabel6.setFont(new java.awt.Font("FangSong", 1, 17)); // NOI18N
        jLabel6.setText("CANTIDAD:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jLabelTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelTotalCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelTotalCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabelDescripcionArt.setFont(new java.awt.Font("FangSong", 0, 30)); // NOI18N
        jLabelDescripcionArt.setForeground(new java.awt.Color(204, 51, 0));
        jLabelDescripcionArt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jButtonPresupuesto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonPresupuesto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/document_prepare.png"))); // NOI18N
        jButtonPresupuesto.setMnemonic('a');
        jButtonPresupuesto.setText("PRESUPUESTO");
        jButtonPresupuesto.setToolTipText("ALT + A");
        jButtonPresupuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPresupuestoActionPerformed(evt);
            }
        });

        jButtonRegistrarVenta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButtonRegistrarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/accept.png"))); // NOI18N
        jButtonRegistrarVenta.setMnemonic('a');
        jButtonRegistrarVenta.setText("REGISTRAR");
        jButtonRegistrarVenta.setToolTipText("ALT + A");
        jButtonRegistrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDescripcionArt, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonPresupuesto)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDescripcionArt, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonPresupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Maxikiosco Zona Sur - Sistema de Gestión de Ventas @ Todos los Derechos Reservados");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jMenu3.setText("Archivo");

        jMenuItemCerrarSesion.setText("Cerrar sesion");
        jMenuItemCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCerrarSesionActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemCerrarSesion);

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemSalir);

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Ayuda");

        jMenuItemManual.setText("Manual de Usuario");
        jMenu4.add(jMenuItemManual);

        jMenuItemAcercaDe.setText("Acerca de...");
        jMenuItemAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAcercaDeActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemAcercaDe);

        jMenuBar2.add(jMenu4);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(jLabel4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonArticulosActionPerformed
        jTextFieldBusquedaArticulo.setText("");
        new GestionArticulos().cargarTablaArticulos(jTableArticulos);
        busquedaRapida(jTextFieldBusquedaArticulo, jTableArticulos);
        abrirDialogo(jDialogArticulos, 924, 660);
    }//GEN-LAST:event_jButtonArticulosActionPerformed

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jButtonOpcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpcionesActionPerformed
        new GestionUsuarios().cargarTablaUsuarios(jTableUsuarios);
        new GestionReportes(arbolReportes);
        abrirDialogo(jDialogOpciones, 800, 660);
        jLabelOpcionesDeReportes.requestFocus();
    }//GEN-LAST:event_jButtonOpcionesActionPerformed

    private void jButtonVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVentasActionPerformed
        new GestionVentas().cargarTablaVentas(jTableVentas);
        jDialogVentas.setModal(false);
        abrirDialogo(jDialogVentas, 950, 660);
    }//GEN-LAST:event_jButtonVentasActionPerformed

    private void jButtonStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStockActionPerformed
        jTextFieldBusquedaStock.setText("");
        new GestionStock().cargarTablaStock(jTableStock);
        busquedaRapida(jTextFieldBusquedaStock, jTableStock);
        abrirDialogo(jDialogStock, 950, 660);
    }//GEN-LAST:event_jButtonStockActionPerformed

    private void jButtonCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCajaActionPerformed

        Boolean cajaAbierta = new GestionCaja().verificarAperturaDeCaja();
        if (!cajaAbierta) { // SI LA CAJA ESTA CERRADA
            FechaYHora fecYHora = new FechaYHora();
            String fecha = fecYHora.fechaActual();
            jTextFieldFecha.setText(fecha);
            jTextFieldHora.setText(fecYHora.horaActual());
            GestionCaja caja = new GestionCaja();
            jTextFieldCajaNum.setText(String.valueOf(caja.obtenerUltimaCaja() + 1)); // MUESTRO LA SIGUIENTE CAJA QUE SE TENDRIA Q ABRIR
            jTextFieldEncargado.setText(usuario);
            jTextFieldTotalIngresos.setText("0.0");
            jTextFieldDebeHaber.setText("0.0");
            jTextFieldCajaInicial.setText("0.0");
            jTextFieldHay.setText("0.0");
            jButtonApyCierreCaja.setText("Abrir caja");
            new GestionCaja().cargarTablaCaja(jTableCaja);
            jTextFieldTotalIngresos.setRequestFocusEnabled(false);
            abrirDialogo(jDialogCaja, 900, 660);
        } else { // SI LA CAJA ESTA ABIERTA
            FechaYHora fecYHora = new FechaYHora();
            String fecha = fecYHora.fechaActual();
            jTextFieldFecha.setText(fecha);
            jTextFieldHora.setText(fecYHora.horaActual());
            GestionCaja caja = new GestionCaja();
            jTextFieldCajaNum.setText(String.valueOf(caja.obtenerUltimaCaja())); // OBTENGO EL NUM DE LA ULTIMA CAJA Y CALCULO EL TOTAL INGRESADO
            jTextFieldEncargado.setText(usuario);
            float ti = obtenerTotalIngresos(caja.obtenerUltimaCaja());
            ti = (float) ((int) (ti * 100.0) / 100.0);
            jTextFieldTotalIngresos.setText("" + ti);
            float debeHaber = Float.parseFloat(jTextFieldCajaInicial.getText()) + ti - Float.parseFloat(jTextFieldTotalEgresos.getText());
            jTextFieldDebeHaber.setText("" + debeHaber);
            jTextFieldCajaInicial.setText("0.0");
            jTextFieldHay.setText("0.0");
            jButtonApyCierreCaja.setText("Cerrar caja");
            new GestionCaja().cargarTablaCaja(jTableCaja);
            jTextFieldTotalIngresos.setRequestFocusEnabled(false);
            abrirDialogo(jDialogCaja, 900, 660);
        }
    }//GEN-LAST:event_jButtonCajaActionPerformed

    private void arbolReportesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_arbolReportesValueChanged
        new GestionReportes().verReporteSeleccionado(arbolReportes);
    }//GEN-LAST:event_arbolReportesValueChanged

    private void jButtonAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAceptarActionPerformed

    }//GEN-LAST:event_jButtonAceptarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed

    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSeleccionarActionPerformed

    }//GEN-LAST:event_jButtonSeleccionarActionPerformed

    private void jButtonRegistrarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarUsuarioActionPerformed
        String contrasenia = JOptionPane.showInputDialog(null, "INGRESE SU CONTRASEÑA");
        try {
            EntityManager em = emf.createEntityManager();
            Query consulta = em.createNamedQuery("Usuario.findByUsuNombre");
            consulta.setParameter("usuNombre", usuario);
            Usuario usu = (Usuario) consulta.getSingleResult(); // OBTENGO EL USU LOGEADO
            if (contrasenia.equals(usu.getUsuContrasenia())) { // SI LAS CONTRASEÑAS SON IGUALES
                Usuario usuario = new Usuario();
                usuario.setUsuNombre(jTextFieldUsuario.getText());
                usuario.setUsuContrasenia(jPasswordFieldContraseña.getText()); // PARA QUE GUARDE EL TEXTO Q PONE EL USUARIO Y NO EL CIFRADO
                usuario.setUsuTipo(jComboBoxTipoUsuario.getSelectedIndex());
                UsuarioJpaController usuarioControlador = new UsuarioJpaController(emf);
                usuarioControlador.create(usuario);
                new GestionUsuarios().cargarTablaUsuarios(jTableUsuarios);
            } else {
                JOptionPane.showMessageDialog(null, "CONTRESEÑA INCORRECTA", "ATENCION", 1);
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButtonRegistrarUsuarioActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        jTextFieldUsuario.setText("");
        jPasswordFieldContraseña.setText("");
        jComboBoxTipoUsuario.setSelectedIndex(0);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItemAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAcercaDeActionPerformed
        abrirDialogo(jDialogAcercaDe, 355, 420);
    }//GEN-LAST:event_jMenuItemAcercaDeActionPerformed

    private void jButtonNuevoArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevoArticuloActionPerformed
        ABMArticulos(evt);
    }//GEN-LAST:event_jButtonNuevoArticuloActionPerformed

    private void jButtonModificarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModificarArticuloActionPerformed
        ABMArticulos(evt);
    }//GEN-LAST:event_jButtonModificarArticuloActionPerformed

    private void ABMArticulos(ActionEvent evento) {
        cargarListaRubro();
        cargarListaProveedor();
        if (evento.getActionCommand().equalsIgnoreCase("nuevo")) {
            jDialogABMArticulo.setTitle("NUEVO ARTICULO");
            limpiarCamposArticulos();
            abrirDialogo(jDialogABMArticulo, 400, 665);
            jTextFieldBusquedaArticulo.requestFocus();
        }
        if (evento.getActionCommand().equalsIgnoreCase("modificar")) {
            jDialogABMArticulo.setTitle("MODIFICAR ARTICULO");
            limpiarCamposArticulos();
            int filaVista = jTableArticulos.getSelectedRow();
            try {
                int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
                } else {
                    obtenerDatosArticulo(filaSeleccionada);
                    abrirDialogo(jDialogABMArticulo, 400, 665);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
            }
        }
    }
    private void jButtonRegistrarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarArticuloActionPerformed
        if (jDialogABMArticulo.getTitle().equalsIgnoreCase("NUEVO ARTICULO")) {
            Boolean ok = validarCamposArticulos();
            if (ok) {
                Articulos articulo = setearDatosArticuloNuevo();
                ArticulosJpaController artControlador = new ArticulosJpaController(emf);
                try {
                    jDialogABMArticulo.setAlwaysOnTop(false);
                    Articulos art = buscarArtPorCodigo(Long.parseLong(jTextFieldCodigoArticulo.getText()));
                    if (art != null) {
                        JOptionPane.showMessageDialog(null, "El artículo ya existe");
                    } else {
                        artControlador.create(articulo);
                        new GestionArticulos().cargarTablaArticulos(jTableArticulos);
                        jDialogABMArticulo.dispose();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (jDialogABMArticulo.getTitle().equalsIgnoreCase("MODIFICAR ARTICULO")) {
            int filaVista = jTableArticulos.getSelectedRow();
            int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
            } else {
                ArticulosJpaController artControlador = new ArticulosJpaController(emf);
                Boolean ok = validarCamposArticulos();
                if (ok) {
                    Articulos articulo = setearDatosArticuloModificado();
                    try {
                        artControlador.edit(articulo);
                        jDialogABMArticulo.dispose();
                        String texto = jTextFieldBusquedaArticulo.getText();
                        jDialogArticulosWindowClosing(null);
                        jButtonArticulosActionPerformed(null);
                        jTextFieldBusquedaArticulo.setText(texto);
                        busquedaRapida(jTextFieldBusquedaArticulo, jTableArticulos);
                        jTextFieldBusquedaArticulo.requestFocus();
                    } catch (Exception ex) {
                        Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonRegistrarArticuloActionPerformed

    private Boolean validarCamposArticulos() {
        Boolean ok = false;
        if (jTextFieldCodigoArticulo.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE EL CODIGO DE ARTICULO", "ATENCION", 1);
        } else if (jTextFieldDescArticulo.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE LA DESCRIPCION DEL ARTICULO", "ATENCION", 1);
        } else if (jTextFieldPrecioCompra.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE EL COSTO DEL ARTICULO", "ATENCION", 1);
        } else if (jTextFieldPorcUtilidad.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE LA UTILIDAD DEL ARTICULO", "ATENCION", 1);
        } else if (jTextFieldPrecioVenta.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE EL PRECIO DE VENTA DEL ARTICULO", "ATENCION", 1);
        } else {
            ok = true;
        }
        return ok;
    }

    private void verificarUsuario() {

        if (tipoUsuario == 1) { // USUARIO ADMINISTRADOR
            jButtonEliminarArticulo.setEnabled(true);
            jButtonModificarArticulo.setEnabled(true);
            jButtonEliminarVenta.setEnabled(false);
            jButtonRegistrarUsuario.setEnabled(true);
            jButtonEliminarUsuario.setEnabled(true);
            jButtonNuevoArticulo.setEnabled(true);
            jButtonReponerStock.setEnabled(true);
            jButtonReponerStock1.setEnabled(true);
        } else {// USUARIO EMPLEADO
            jButtonEliminarArticulo.setEnabled(false);
            jButtonModificarArticulo.setEnabled(false);
            jButtonEliminarVenta.setEnabled(false);
            jButtonRegistrarUsuario.setEnabled(false);
            jButtonEliminarUsuario.setEnabled(false);
            jButtonNuevoArticulo.setEnabled(false);
            jButtonReponerStock.setEnabled(false);
            jButtonReponerStock1.setEnabled(false);
        }
    }

    private Articulos setearDatosArticuloModificado() {
        int filaVista = jTableArticulos.getSelectedRow();
        int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
        Articulos articulo = buscarArtPorCodigo(Long.parseLong(String.valueOf(jTableArticulos.getModel().getValueAt(filaSeleccionada, 0))));
        articulo.setArtId(articulo.getArtId());
        articulo.setArtCodigo(Long.parseLong(jTextFieldCodigoArticulo.getText()));
        articulo.setArtDescripcion(jTextFieldDescArticulo.getText());
        articulo.setArtMarca(jTextFieldMarcaArticulo.getText());
        articulo.setArtPrecioCompra(Float.parseFloat(jTextFieldPrecioCompra.getText()));
        articulo.setArtPorcUtil(Float.parseFloat(jTextFieldPorcUtilidad.getText()));
        articulo.setArtPrecioVenta(Float.parseFloat(jTextFieldPrecioVenta.getText()));
        articulo.setArtRubro(jComboBoxRubro.getSelectedItem().toString());
        articulo.setArtProveedor(jComboBoxProveedor.getSelectedItem().toString());
        articulo.setArtDetalle(jTextAreaDetalle.getText());
        articulo.setArtEstado(1);
        return articulo;
    }

    private Articulos setearDatosArticuloNuevo() {

        Articulos articulo = new Articulos();
        articulo.setArtCodigo(Long.parseLong(jTextFieldCodigoArticulo.getText()));
        articulo.setArtDescripcion(jTextFieldDescArticulo.getText());
        articulo.setArtMarca(jTextFieldMarcaArticulo.getText());
        articulo.setArtPrecioCompra(Float.parseFloat(jTextFieldPrecioCompra.getText()));
        articulo.setArtPorcUtil(Float.parseFloat(jTextFieldPorcUtilidad.getText()));
        articulo.setArtPrecioVenta(Float.parseFloat(jTextFieldPrecioVenta.getText()));
        articulo.setArtRubro(jComboBoxRubro.getSelectedItem().toString());
        articulo.setArtProveedor(jComboBoxProveedor.getSelectedItem().toString());
        articulo.setArtDetalle(jTextAreaDetalle.getText());
        articulo.setArtEstado(1);
        return articulo;

    }

    private Articulos obtenerDatosArticulo(int artSeleccionado) {
        Articulos articulo = buscarArtPorCodigo(Long.parseLong(String.valueOf(jTableArticulos.getModel().getValueAt(artSeleccionado, 0))));
        jTextFieldCodigoArticulo.setText("" + articulo.getArtCodigo());
        jTextFieldDescArticulo.setText(articulo.getArtDescripcion());
        jTextFieldMarcaArticulo.setText(articulo.getArtMarca());
        jTextFieldPrecioCompra.setText("" + articulo.getArtPrecioCompra());
        jTextFieldPorcUtilidad.setText("" + articulo.getArtPorcUtil());
        jTextFieldPrecioVenta.setText("" + articulo.getArtPrecioVenta());
        jTextAreaDetalle.setText("" + articulo.getArtDetalle());
        jComboBoxRubro.setSelectedItem(articulo.getArtRubro());
        jComboBoxProveedor.setSelectedItem(articulo.getArtProveedor());
        return articulo;
    }

    private void cargarTablaStock() {
        new GestionTablas().limparTabla(jTableArticulos, (DefaultTableModel) jTableArticulos.getModel());
        ArticulosJpaController artControlador = new ArticulosJpaController(emf);
        List<Articulos> listaDeArticulos = artControlador.findArticulosEntities();
        DefaultTableModel dtmArticulos = (DefaultTableModel) jTableArticulos.getModel();
        String datos[] = new String[dtmArticulos.getColumnCount()];
        for (int i = 0; i < listaDeArticulos.size(); i++) {
            Articulos articulos = listaDeArticulos.get(i);
            datos[0] = String.valueOf(articulos.getArtCodigo());
            datos[1] = articulos.getArtDescripcion();
            datos[2] = articulos.getArtMarca();
            datos[3] = String.valueOf(articulos.getArtPrecioCompra());
            datos[4] = String.valueOf(articulos.getArtPrecioVenta());
            datos[5] = String.valueOf(articulos.getArtRubro());
            datos[6] = String.valueOf(articulos.getArtProveedor());
            datos[7] = String.valueOf(articulos.getArtDetalle());
            dtmArticulos.addRow(datos);
        }
    }

    private Articulos buscarArtPorCodigo(long codigo) {
        EntityManager em = emf.createEntityManager();
        Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
        consulta.setParameter("artCodigo", codigo);
        Articulos articulo = null;
        try {
            articulo = (Articulos) consulta.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("El resultado de la consulta no arroja ningun articulo");
        } catch (NonUniqueResultException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR ACCEDER. EL CODIGO DE ARTICULO DEBE SER UNICO", "ATENCION", 1);
            articulo.setArtCodigo(codigo); // ESTO ES PORQ SI EXISTE PERO SON VARIOS.. ENTONCS LE CARGO ALGO PARA Q NO SEA NULO
        }
        return articulo;
    }

    private void jButtonEliminarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarArticuloActionPerformed

        int filaVista = jTableArticulos.getSelectedRow();
        int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
        } else {
            int opcion = JOptionPane.showConfirmDialog(null, "NO PODRA SEGUIR OPERANDO CON EL ARTICULO. ¿DESEA CONTINUAR?", "ATENCION", 1);
            if (opcion == 0) {
                ArticulosJpaController artControlador = new ArticulosJpaController(emf);
                Articulos articulo = buscarArtPorCodigo((Long.parseLong(String.valueOf(jTableArticulos.getModel().getValueAt(filaSeleccionada, 0)))));
                articulo.setArtEstado(0);
                try {
                    artControlador.edit(articulo);
                    jDialogArticulosWindowClosing(null);
                    jButtonArticulosActionPerformed(null);
                    jTextFieldBusquedaArticulo.requestFocus();
                } catch (Exception ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButtonEliminarArticuloActionPerformed

    private void jTextFieldBusquedaArticuloKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusquedaArticuloKeyReleased
        busquedaRapida(jTextFieldBusquedaArticulo, jTableArticulos);
    }//GEN-LAST:event_jTextFieldBusquedaArticuloKeyReleased

    private void jTableArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableArticulosMouseClicked
        jPopupMenuDetalleArt.removeAll();
        if (evt.getClickCount() == 2) {
            float total = new GestionArticulos().añadirArtAlCarro(jTableArticulos, jTableCarrito, jLabelDescripcionArt);
            jLabelTotalCarrito.setText("" + total);
            jDialogArticulos.dispose();
            jTextFieldCodigo.requestFocus();
        }
        if (evt.getButton() == MouseEvent.BUTTON3) { // CLICK DERECHO
            int filaVista = jTableArticulos.getSelectedRow();
            int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
            if (filaSeleccionada == -1) {
            } else {
                jPopupMenuDetalleArt.add(jMenuItemDetalleArt);
                jTableArticulos.add(jPopupMenuDetalleArt);
                jPopupMenuDetalleArt.setLocation(evt.getXOnScreen(), evt.getYOnScreen());
                long codigoArt = Long.parseLong(String.valueOf(jTableArticulos.getModel().getValueAt(filaSeleccionada, 0)));
                try {
                    EntityManager em = emf.createEntityManager();
                    Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
                    consulta.setParameter("artCodigo", codigoArt);
                    Articulos articulo = (Articulos) consulta.getSingleResult();
                    if (articulo != null) { //SI EXISTE EL ARTICULO
                        jMenuItemDetalleArt.setText("DETALLE: " + articulo.getArtDetalle());
                        jPopupMenuDetalleArt.setVisible(true);
                    }
                } catch (NoResultException ex) {
                    JOptionPane.showMessageDialog(null, "ARTICULO INEXISTENTE", "Atención", 1);
                    jTextFieldCodigo.setText("");
                }
            }
        }
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1) { // SI ES CLICK IZQ Y UN SOLO CLICK
            jPopupMenuDetalleArt.setVisible(false);
        }
    }//GEN-LAST:event_jTableArticulosMouseClicked

    private void jMenuItemEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEliminarActionPerformed
        int filaVista = jTableCarrito.getSelectedRow();
        int filaSeleccionada = jTableCarrito.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
        } else {
            DefaultTableModel dtm = (DefaultTableModel) jTableCarrito.getModel();
            dtm.removeRow(filaSeleccionada);
            float total = new GestionArticulos().actualizarTotal(jTableCarrito);
            jLabelTotalCarrito.setText("" + total);
            jLabelDescripcionArt.setText("");
            jPopupMenuEliminar.setVisible(false);
            jSpinner1.setValue(1);
            jTextFieldCodigo.setText("");
        }
    }//GEN-LAST:event_jMenuItemEliminarActionPerformed

    private void jButtonReponerStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReponerStockActionPerformed
        GestionStock stock = new GestionStock();
        stock.reponerStock(jTableArticulos);
    }//GEN-LAST:event_jButtonReponerStockActionPerformed

    private void jTableStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStockMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableStockMouseClicked

    private void jTextFieldBusquedaStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusquedaStockKeyReleased
        busquedaRapida(jTextFieldBusquedaStock, jTableStock);
    }//GEN-LAST:event_jTextFieldBusquedaStockKeyReleased

    private void jButtonReponerStock1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReponerStock1ActionPerformed
        int filaVista = jTableStock.getSelectedRow();
        try {
            int filaSeleccionada = jTableStock.convertRowIndexToModel(filaVista);
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
            } else {
                GestionStock miStock = new GestionStock();
                miStock.reponerStock(jTableStock);
                miStock.cargarTablaStock(jTableStock);
                String texto = jTextFieldBusquedaStock.getText();
                jDialogStockWindowClosing(null);
                jButtonStockActionPerformed(null);
                jTextFieldBusquedaStock.setText(texto);
                busquedaRapida(jTextFieldBusquedaStock, jTableStock);
                jTextFieldBusquedaArticulo.requestFocus();
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
        }
    }//GEN-LAST:event_jButtonReponerStock1ActionPerformed

    private void jTableVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableVentasMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableVentasMouseClicked

    private void jButtonEliminarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarVentaActionPerformed
        int filaVista = jTableVentas.getSelectedRow();
        int filaSeleccionada = jTableVentas.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UNA VENTA", "ATENCION", 1);
        } else {

            int opcion = JOptionPane.showConfirmDialog(null, "AL ELIMINAR UNA VENTA SE BORRARAN TODAS SUS DEPENDENCIAS (ARTICULOS VENDIDOS)");
            if (opcion == 0) {
                try {
                    VentasJpaController ventasControlador = new VentasJpaController(emf);
                    ArtVenJpaController artVenControlador = new ArtVenJpaController(emf);
                    int idVenta = Integer.parseInt(String.valueOf(jTableVentas.getModel().getValueAt(filaSeleccionada, 0)));
                    EntityManager em = emf.createEntityManager();
                    Query consulta = em.createNamedQuery("Ventas.findByVenId");
                    consulta.setParameter("venId", idVenta);
                    Ventas venta = (Ventas) consulta.getSingleResult();
                    venta.setVenEstado(0);
                    List<ArtVen> artVenList = venta.getArtVenList();
                    for (int i = 0; i < artVenList.size(); i++) {
                        ArtVen artVen = artVenList.get(i);
                        artVen.setArtVenEstado(0);
                        artVenControlador.edit(artVen);
                    }
                    ventasControlador.edit(venta);
                    new GestionVentas().cargarTablaVentas(jTableVentas);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButtonEliminarVentaActionPerformed

    private void jTextFieldBusquedaVentasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusquedaVentasKeyReleased
        busquedaRapida(jTextFieldBusquedaVentas, jTableVentas);
    }//GEN-LAST:event_jTextFieldBusquedaVentasKeyReleased

    private void jTextFieldPagaConKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPagaConKeyReleased
        if (!jTextFieldPagaCon.getText().isEmpty()) {
            float vuelto = 0;
            try {
                vuelto = Float.parseFloat(jTextFieldPagaCon.getText()) - Float.parseFloat(jTextFieldTotal.getText());
                if (evt.getKeyCode() == evt.VK_ENTER) {
                    jButtonGuardarVenta.requestFocus();
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "FORMATO NO VÁLIDO", "Error", 1);
            }
            vuelto = (float) ((int) (vuelto * 100.0) / 100.0);
            jLabelVuelto.setText("" + vuelto);
        } else {
            jLabelVuelto.setText("0.00");
        }
    }//GEN-LAST:event_jTextFieldPagaConKeyReleased

    private void jButtonGuardarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarVentaActionPerformed
        guardarVenta();
    }//GEN-LAST:event_jButtonGuardarVentaActionPerformed

    private void jTextFieldPrecioCompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPrecioCompraKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldPrecioCompraKeyTyped

    private void jTextFieldCodigoArticuloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCodigoArticuloKeyTyped
        permitirSoloNum(evt);
    }//GEN-LAST:event_jTextFieldCodigoArticuloKeyTyped

    private void jButtonGuardarVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonGuardarVentaKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            guardarVenta();
        }
    }//GEN-LAST:event_jButtonGuardarVentaKeyPressed

    private void jTextFieldPagaConKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPagaConKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldPagaConKeyTyped

    private void jButtonHistorialStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHistorialStockActionPerformed
        jDialogStock.dispose();
        new GestionStock().cargarTablaHistorialStock(jTableHistorialStock);
        abrirDialogo(jDialogHistorialStock, 950, 660);
    }//GEN-LAST:event_jButtonHistorialStockActionPerformed

    private void jTableHistorialStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableHistorialStockMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableHistorialStockMouseClicked

    private void jTextFieldBusquedaHistorialStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusquedaHistorialStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBusquedaHistorialStockKeyReleased

    private void jButtonReponerStock4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReponerStock4ActionPerformed
        jDialogHistorialStock.dispose();
        abrirDialogo(jDialogStock, 950, 660);
    }//GEN-LAST:event_jButtonReponerStock4ActionPerformed

    private void jComboBoxFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFormaPagoActionPerformed
        System.out.println("TOTAL: " + jTextFieldTotal.getText());
        float total = Float.parseFloat(jLabelTotalCarrito.getText());
        if (jComboBoxFormaPago.getSelectedIndex() == 1) { // CREDITO 10% MAS
            nombreTarjeta = JOptionPane.showInputDialog(null, "Nombre de su tarjeta:");
            float totalNuevo = total + (total * 10) / 100;
            jTextFieldTotal.setText("" + totalNuevo);
        }
        if (jComboBoxFormaPago.getSelectedIndex() == 0 || jComboBoxFormaPago.getSelectedIndex() == 2) { // EFECTIVO O DEBITO
            jTextFieldTotal.setText("" + total);
        }
        if (jComboBoxFormaPago.getSelectedIndex() == 3) { // AL POR MAYOR 5% MENOS
            float totalNuevo = total - (total * 5) / 100;
            jTextFieldTotal.setText("" + totalNuevo);
        }
    }//GEN-LAST:event_jComboBoxFormaPagoActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged

    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jPanel13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel13FocusGained
        new GestionUsuarios().cargarTablaUsuarios(jTableUsuarios);
    }//GEN-LAST:event_jPanel13FocusGained

    private void jMenuItemCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCerrarSesionActionPerformed
        this.setVisible(false);
        new VentanaLogin().setVisible(true);
    }//GEN-LAST:event_jMenuItemCerrarSesionActionPerformed

    private void jButtonEliminarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarUsuarioActionPerformed
        int filaVista = jTableUsuarios.getSelectedRow();
        int filaSeleccionada = jTableUsuarios.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "SELECCIONE UN USUARIO", "ATENCION", 1);
        } else {
            String contrasenia = JOptionPane.showInputDialog(null, "INGRESE SU CONTRASEÑA");
            UsuarioJpaController usuControlador = new UsuarioJpaController(emf);
            try {
                EntityManager em = emf.createEntityManager();
                Query consulta = em.createNamedQuery("Usuario.findByUsuNombre");
                consulta.setParameter("usuNombre", usuario);
                Usuario usu = (Usuario) consulta.getSingleResult(); // OBTENGO EL USU LOGEADO
                if (contrasenia.equals(usu.getUsuContrasenia())) { // SI LAS CONTRASEÑAS SON IGUALES
                    int idUsuario = Integer.parseInt(String.valueOf(jTableUsuarios.getModel().getValueAt(filaSeleccionada, 0))); // TOMO EL ID DEL USUARIO QUE QUIERO ELIMIMNAR
                    consulta = em.createNamedQuery("Usuario.findByUsuId");
                    consulta.setParameter("usuId", idUsuario);
                    Usuario usu1 = (Usuario) consulta.getSingleResult();
                    if (usu.getUsuNombre().equals(usu1.getUsuNombre())) { // SI LOS USUARIOS SON IGUALES
                        JOptionPane.showMessageDialog(null, "EL USUARIO QUE INTENTA ELIMINAR ESTA LOGEADO. CIERRE SESION E INTENTE INGRESAR CON OTRO USUARIO ADMINISTRADOR", "ATENCION", 1);
                    } else {
                        usuControlador.destroy(idUsuario);
                        new GestionUsuarios().cargarTablaUsuarios(jTableUsuarios);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "CONTRESEÑA INCORRECTA", "ATENCION", 1);
                }
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_jButtonEliminarUsuarioActionPerformed

    private void jTextFieldCajaInicialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCajaInicialKeyReleased
        calcularDebeHaber(evt, jTextFieldCajaInicial, jTextFieldTotalEgresos);
    }//GEN-LAST:event_jTextFieldCajaInicialKeyReleased

    private void jTextFieldCajaInicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCajaInicialKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldCajaInicialKeyTyped

    private void jTextFieldTotalEgresosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTotalEgresosKeyReleased
        calcularDebeHaber(evt, jTextFieldCajaInicial, jTextFieldTotalEgresos);
    }//GEN-LAST:event_jTextFieldTotalEgresosKeyReleased

    private void jTextFieldTotalEgresosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTotalEgresosKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldTotalEgresosKeyTyped

    private void jButtonApyCierreCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApyCierreCajaActionPerformed
        if (jButtonApyCierreCaja.getText().equalsIgnoreCase("abrir caja")) {
            abrirCaja();
        } else {
            cerraCaja();
        }
    }//GEN-LAST:event_jButtonApyCierreCajaActionPerformed

    private void jTextFieldHayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldHayKeyReleased
        if (evt.getKeyCode() == evt.VK_ENTER) {
            jButtonApyCierreCaja.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldHayKeyReleased

    private void jTextFieldHayKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldHayKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldHayKeyTyped

    private void jTextFieldPrecioVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPrecioVentaKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldPrecioVentaKeyTyped

    private void jDialogArticulosWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogArticulosWindowClosing
        jPopupMenuDetalleArt.setVisible(false);
        jPopupMenuDetalleArt.removeAll();
        jTextFieldBusquedaArticulo.setText("");
        busquedaRapida(jTextFieldBusquedaArticulo, jTableArticulos);
        jTextFieldCodigo.requestFocus();
    }//GEN-LAST:event_jDialogArticulosWindowClosing

    private void jTextFieldPorcUtilidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPorcUtilidadKeyReleased
        float costo = Float.parseFloat(jTextFieldPrecioCompra.getText());
        float utilidad = Float.parseFloat(jTextFieldPorcUtilidad.getText());
        float total = ((costo * utilidad) / 100) + costo;
        total = (float) ((int) (total * 100.0) / 100.0);
        jTextFieldPrecioVenta.setText("" + total);
    }//GEN-LAST:event_jTextFieldPorcUtilidadKeyReleased

    private void jTextFieldPorcUtilidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPorcUtilidadKeyTyped
        permitirSoloNumYPunto(evt);
    }//GEN-LAST:event_jTextFieldPorcUtilidadKeyTyped

    private void jTextFieldTotalIngresosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTotalIngresosKeyTyped

    }//GEN-LAST:event_jTextFieldTotalIngresosKeyTyped

    private void jDialogStockWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogStockWindowClosing
        jTextFieldBusquedaStock.setText("");
        busquedaRapida(jTextFieldBusquedaStock, jTableStock);
        jTextFieldCodigo.requestFocus();
    }//GEN-LAST:event_jDialogStockWindowClosing

    private void jDialogVentasWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogVentasWindowClosing
        jTextFieldBusquedaVentas.setText("");
        busquedaRapida(jTextFieldBusquedaVentas, jTableVentas);
        jTextFieldCodigo.requestFocus();
    }//GEN-LAST:event_jDialogVentasWindowClosing

    private void jButtonPresupuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPresupuestoActionPerformed
        if (jTableCarrito.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "DEBE CARGAR AL MENOS UN ITEM PARA CONFECCIONAR EL PRESUPUESTO", "MENSAJE", 1);
        } else {
            try {

                List<String> paramArt = new ArrayList<String>();
                List<String> paramCant = new ArrayList<String>();
                List<String> paramPrecio = new ArrayList<String>();
                String paramTotal = jLabelTotalCarrito.getText();
                int i, f;
                f = jTableCarrito.getRowCount();
                if (f > 0) {
                    for (i = 0; i < f; i++) {
                        paramArt.add(String.valueOf(jTableCarrito.getModel().getValueAt(i, 1)));
                        paramCant.add(String.valueOf(jTableCarrito.getModel().getValueAt(i, 4)));
                        paramPrecio.add(String.valueOf(jTableCarrito.getModel().getValueAt(i, 3)));
                    }
                }
                JasperReport reporte = JasperCompileManager.compileReport("src/reportes/presupuesto.jrxml");
                Map parametros = new HashMap();
                parametros.put("paramArt", paramArt);
                parametros.put("paramCant", paramCant);
                parametros.put("paramPrecio", paramPrecio);
                parametros.put("paramTotal", paramTotal);

                JasperPrint print = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource()); //ENVIO PARAMETROS Q SON LISTAS ENTONCES VA JREMPTYDATASOURCE
                JasperViewer visor = new JasperViewer(print, false); // EL FALSE ES PARA QUE NO CIERRE LA APLICACION AL SALIR
                visor.setTitle("src/reportes/presupuesto.jrxml".toUpperCase());
                visor.setZoomRatio((float) 1);
                visor.setVisible(true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(null, "ERROR EN EL REPORTE: " + ex);
            }
        }
    }//GEN-LAST:event_jButtonPresupuestoActionPerformed

    private void jButtonRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarVentaActionPerformed
        jTextFieldTotal.setText(jLabelTotalCarrito.getText());
        abrirDialogo(jDialogRegistrarVenta, 420, 450);
        jLabelVuelto.setText("0.00");
        jTextFieldPagaCon.setText("");
        jTextFieldPagaCon.requestFocus();
    }//GEN-LAST:event_jButtonRegistrarVentaActionPerformed

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        int filaVista = jTableCarrito.getSelectedRow();
        int filaSeleccionada = jTableCarrito.convertRowIndexToModel(filaVista);
        if (filaSeleccionada == -1) {
        } else {
            jTableCarrito.getModel().setValueAt(jSpinner1.getValue(), filaSeleccionada, 4);
            int cant = Integer.parseInt(jSpinner1.getValue().toString());
            float subTotal = cant * Float.parseFloat((String) jTableCarrito.getModel().getValueAt(filaSeleccionada, 3));
            jTableCarrito.getModel().setValueAt(subTotal, filaSeleccionada, 5);
            float total = new GestionArticulos().actualizarTotal(jTableCarrito);
            jLabelTotalCarrito.setText("" + total);
        }
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jTextFieldCodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCodigoKeyTyped
         permitirSoloNum(evt);
    }//GEN-LAST:event_jTextFieldCodigoKeyTyped

    private void jTextFieldCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCodigoKeyReleased
        String codigo = jTextFieldCodigo.getText();
        if (codigo.length() == 13) { // LONGITUD DEL CODIGO DE BARRAS UNIDIMENSIONAL EAN DE LA UNION EUROPEA, ACEPTADO MUNDIALMENTE PARA ART. DE COMERCIOS Y SUPERMERCADOS
            long codigoNumerico = Long.parseLong(codigo);
            try {
                EntityManager em = emf.createEntityManager();
                Query consulta = em.createNamedQuery("Articulos.findByArtCodigo");
                consulta.setParameter("artCodigo", codigoNumerico);
                Articulos articulo = (Articulos) consulta.getSingleResult();
                if (articulo != null) { //SI EXISTE EL ARTICULO
                    float total = new GestionArticulos().buscarArticulo(codigoNumerico, jTableCarrito, jLabelDescripcionArt);
                    jLabelTotalCarrito.setText("" + total);
                    jTextFieldCodigo.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "ARTICULO INEXISTENTE", "Atención", 1);
                    jTextFieldCodigo.setText("");
                }
            } catch (NoResultException ex) {
                JOptionPane.showMessageDialog(null, "ARTICULO INEXISTENTE", "Atención", 1);
                jTextFieldCodigo.setText("");
            }
        }

    }//GEN-LAST:event_jTextFieldCodigoKeyReleased

    private void jTableCarritoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCarritoMouseClicked
        jPopupMenuEliminar.removeAll();
        jPopupMenuEliminar.setVisible(false);
        if (evt.getButton() == MouseEvent.BUTTON1) { // CLICK IZQUIERDO
            int filaVista = jTableCarrito.getSelectedRow();
            int filaSeleccionada = jTableCarrito.convertRowIndexToModel(filaVista);
            if (filaSeleccionada == -1) {
            } else {
                int cant = Integer.parseInt(String.valueOf(jTableCarrito.getModel().getValueAt(filaSeleccionada, 4)));
                jSpinner1.setValue(cant);
                jLabelDescripcionArt.setText(String.valueOf(jTableCarrito.getModel().getValueAt(filaSeleccionada, 1)));
            }
        }
        if (evt.getButton() == MouseEvent.BUTTON3) { // CLICK DERECHO
            int filaVista = jTableCarrito.getSelectedRow();
            int filaSeleccionada = jTableCarrito.convertRowIndexToModel(filaVista);
            if (filaSeleccionada == -1) {
            } else {
                jPopupMenuEliminar.add(jMenuItemEliminar);
                // jTableCarrito.setComponentPopupMenu(jPopupMenuEliminar);
                jPopupMenuEliminar.setLocation(evt.getXOnScreen(), evt.getYOnScreen());
                jPopupMenuEliminar.setVisible(true);
            }
        }
    }//GEN-LAST:event_jTableCarritoMouseClicked

    private void jTextAreaDetalleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaDetalleKeyReleased
        if (evt.getKeyCode() == evt.VK_ENTER) {
            jButtonRegistrarArticulo.requestFocus();
        }
    }//GEN-LAST:event_jTextAreaDetalleKeyReleased

    private void jButtonRegistrarArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonRegistrarArticuloKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            if (jDialogABMArticulo.getTitle().equalsIgnoreCase("NUEVO ARTICULO")) {
                Articulos articulo = setearDatosArticuloNuevo();
                ArticulosJpaController artControlador = new ArticulosJpaController(emf);
                try {
                    jDialogABMArticulo.setAlwaysOnTop(false);
                    Articulos art = buscarArtPorCodigo(Long.parseLong(jTextFieldCodigoArticulo.getText()));
                    if (art != null) {
                        JOptionPane.showMessageDialog(null, "El artículo ya existe");
                    } else {
                        artControlador.create(articulo);
                        new GestionArticulos().cargarTablaArticulos(jTableArticulos);
                        jDialogABMArticulo.dispose();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jDialogABMArticulo.getTitle().equalsIgnoreCase("MODIFICAR ARTICULO")) {
                int filaVista = jTableArticulos.getSelectedRow();
                int filaSeleccionada = jTableArticulos.convertRowIndexToModel(filaVista);
                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(null, "SELECCIONE UN ITEM", "ATENCION", 1);
                } else {
                    ArticulosJpaController artControlador = new ArticulosJpaController(emf);
                    Articulos articulo = setearDatosArticuloModificado();
                    try {
                        artControlador.edit(articulo);
                        jDialogABMArticulo.dispose();
                        String texto = jTextFieldBusquedaArticulo.getText();
                        jDialogArticulosWindowClosing(null);
                        jButtonArticulosActionPerformed(null);
                        jTextFieldBusquedaArticulo.setText(texto);
                        busquedaRapida(jTextFieldBusquedaArticulo, jTableArticulos);
                        jTextFieldBusquedaArticulo.requestFocus();
                    } catch (Exception ex) {
                        Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

    }//GEN-LAST:event_jButtonRegistrarArticuloKeyPressed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        jTextFieldUsuario.setText("");
        jPasswordFieldContraseña.setText("");
        jComboBoxTipoUsuario.setSelectedIndex(0);
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void jButtonNuevoRubroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevoRubroActionPerformed
        String rubro = String.valueOf(JOptionPane.showInputDialog(null, "Ingrese un nuevo rubro:"));
        if (rubro.equals("")) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del rubro");
        } else if (!rubro.equals("null")) {
            try {
                RubroJpaController rubController = new RubroJpaController(emf);
                Rubro miRubro = new Rubro();
                miRubro.setRubNombre(rubro);
                miRubro.setRubEstado(1);
                rubController.create(miRubro);
                cargarListaRubro();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "El rubro ya existe");
            }
        }
    }//GEN-LAST:event_jButtonNuevoRubroActionPerformed

    private void jButtonNuevoRubroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonNuevoRubroKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonNuevoRubroKeyPressed

    private void jButtonNuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevoProveedorActionPerformed
        String proveedor = String.valueOf(JOptionPane.showInputDialog(null, "Ingrese un nuevo proveedor:"));
        if (proveedor.equals("")) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del proveedor");
        } else if (!proveedor.equals("null")) {
            try {
                ProveedorJpaController proveedorJpaController = new ProveedorJpaController(emf);
                Proveedor miProveedor = new Proveedor();
                miProveedor.setProNombre(proveedor);
                miProveedor.setProEstado(1);
                proveedorJpaController.create(miProveedor);
                cargarListaProveedor();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "El proveedor ya existe");
            }
        }
    }//GEN-LAST:event_jButtonNuevoProveedorActionPerformed

    private void jButtonNuevoProveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonNuevoProveedorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonNuevoProveedorKeyPressed

    private void jDialogRegistrarVentaWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialogRegistrarVentaWindowOpened
        jTextFieldPagaCon.requestFocus();
    }//GEN-LAST:event_jDialogRegistrarVentaWindowOpened

    private void cargarListaRubro() {
        jComboBoxRubro.removeAllItems();
        jComboBoxRubro.addItem("-- SELECCIONE --");
        jComboBoxRubro.setSelectedIndex(0);
        RubroJpaController rubControlador = new RubroJpaController(emf);
        List<Rubro> listaRubros = rubControlador.findRubroEntities();

        for (int i = 0; i < listaRubros.size(); i++) {
            Rubro rubro = listaRubros.get(i);
            jComboBoxRubro.addItem(rubro.getRubNombre());
        }
    }

    private void cargarListaProveedor() {
        jComboBoxProveedor.removeAllItems();
        jComboBoxProveedor.addItem("-- SELECCIONE --");
        jComboBoxProveedor.setSelectedIndex(0);
        ProveedorJpaController provControlador = new ProveedorJpaController(emf);
        List<Proveedor> listaProveedores = provControlador.findProveedorEntities();

        for (int i = 0; i < listaProveedores.size(); i++) {
            Proveedor proveedor = listaProveedores.get(i);
            jComboBoxProveedor.addItem(proveedor.getProNombre());
        }
    }

    private void calcularDebeHaber(java.awt.event.KeyEvent evt, JTextField campoTexto, JTextField campoTexto1) {
        if (!campoTexto.getText().isEmpty() && !campoTexto1.getText().isEmpty()) {
            try {
                float debeHaber = Float.parseFloat(campoTexto.getText()) + Float.parseFloat(jTextFieldTotalIngresos.getText()) - Float.parseFloat(campoTexto1.getText());
                if (evt.getKeyCode() == evt.VK_ENTER) {
                    debeHaber = (float) ((int) (debeHaber * 100.0) / 100.0);
                    jTextFieldDebeHaber.setText("" + debeHaber);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "FORMATO NO VÁLIDO", "Error", 1);
            }
        }
    }

    private void cerraCaja() {

        boolean abierta = new GestionCaja().verificarAperturaDeCaja();
        if (abierta) {
            Caja miCaja = null;
            int ultimaCaja = new GestionCaja().obtenerUltimaCaja();
            EntityManager em = emf.createEntityManager();
            Query consulta = em.createNamedQuery("Caja.findByCajId");
            consulta.setParameter("cajId", ultimaCaja);
            miCaja = (Caja) consulta.getSingleResult();
            int estado = miCaja.getCajEstado();
            if (estado == 0) { // SI NO FUE CERRADA ANTERIORMENTE
                try {
                    //Caja miCaja = new Caja(); // aqui no es new, tengo q buscar la caja correspondiente y editarla
                    miCaja.setCajFecha(jTextFieldFecha.getText());
                    miCaja.setCajHora(jTextFieldHora.getText());
                    miCaja.setCajTurno(jTextFieldCajaNum.getText());
                    miCaja.setCajEncargado(jTextFieldEncargado.getText());
                    miCaja.setCajInicial(Float.parseFloat(jTextFieldCajaInicial.getText()));
                    miCaja.setCajIngresos(Float.parseFloat(jTextFieldTotalIngresos.getText()));
                    miCaja.setCajEgresos(Float.parseFloat(jTextFieldTotalEgresos.getText()));
                    miCaja.setCajFinal(Float.parseFloat(jTextFieldDebeHaber.getText()));
                    miCaja.setCajHay(Float.parseFloat(jTextFieldHay.getText()));
                    CajaJpaController cajaControlador = new CajaJpaController(emf);
                    miCaja.setCajEstado(1); // ESTADO CERRADA
                    cajaControlador.edit(miCaja);// seria un UPDATE
                    JOptionPane.showMessageDialog(null, "CAJA CERRADA EXITOSAMENTE");
                    new GestionCaja().cargarTablaCaja(jTableCaja);

                    // ACTUALIZO EL PANEL DE LA CAJA CERRADA
                    FechaYHora fecYHora = new FechaYHora();
                    String fecha = fecYHora.fechaActual();
                    jTextFieldFecha.setText(fecha);
                    jTextFieldHora.setText(fecYHora.horaActual());
                    GestionCaja caja = new GestionCaja();
                    jTextFieldCajaNum.setText(String.valueOf(caja.obtenerUltimaCaja() + 1)); // MUESTRO LA SIGUIENTE CAJA QUE SE TENDRIA Q ABRIR
                    jTextFieldEncargado.setText(usuario);
                    jTextFieldTotalIngresos.setText("0.0");
                    jTextFieldDebeHaber.setText("0.0");
                    jTextFieldCajaInicial.setText("0.0");
                    jTextFieldHay.setText("0.0");
                    jButtonApyCierreCaja.setText("Abrir caja");

                } catch (Exception ex) {
                    Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (estado == 1) {
                JOptionPane.showMessageDialog(null, "LA CAJA YA FUE CERRADA");
            }
        } else {
            JOptionPane.showMessageDialog(null, "PRIMERO DEBE ABRIR LA CAJA");
            abrirCaja();
        }
    }

    private void abrirCaja() {
        FechaYHora fec = new FechaYHora();
        String fechaActual = fec.fechaActual();
        String turno = fec.obtenerTurno();
        String hora = fec.horaActual();
        Caja miCaja = new Caja();
        miCaja.setCajFecha(fechaActual);
        miCaja.setCajHora(hora);
        miCaja.setCajTurno(turno);
        miCaja.setCajEncargado(usuario);
        miCaja.setCajInicial(Float.parseFloat(jTextFieldCajaInicial.getText()));
        miCaja.setCajIngresos(Float.parseFloat(jTextFieldTotalIngresos.getText()));
        miCaja.setCajEgresos(Float.parseFloat(jTextFieldTotalEgresos.getText()));
        miCaja.setCajFinal(Float.parseFloat(jTextFieldDebeHaber.getText()));
        miCaja.setCajHay(Float.parseFloat(jTextFieldHay.getText()));
        miCaja.setCajEstado(0); // ESTADO ABIERTA
        CajaJpaController cajaControlador = new CajaJpaController(emf);
        cajaControlador.create(miCaja); // ABRO LA CAJA CREO UN REGISTRO
        JOptionPane.showMessageDialog(null, "CAJA ABIERTA EXITOSAMENTE");
        new GestionCaja().cargarTablaCaja(jTableCaja);

        // ACTUALIZO EL PANEL DE LA CAJA ABIERTA
        FechaYHora fecYHora = new FechaYHora();
        String fecha = fecYHora.fechaActual();
        jTextFieldFecha.setText(fecha);
        jTextFieldHora.setText(fecYHora.horaActual());
        GestionCaja caja = new GestionCaja();
        jTextFieldCajaNum.setText(String.valueOf(caja.obtenerUltimaCaja())); // OBTENGO EL NUM DE LA ULTIMA CAJA Y CALCULO EL TOTAL INGRESADO
        jTextFieldEncargado.setText(usuario);
        float ti = obtenerTotalIngresos(caja.obtenerUltimaCaja());
        ti = (float) ((int) (ti * 100.0) / 100.0);
        jTextFieldTotalIngresos.setText("" + ti);
        float debeHaber = Float.parseFloat(jTextFieldCajaInicial.getText()) + ti - Float.parseFloat(jTextFieldTotalEgresos.getText());
        jTextFieldCajaInicial.setText("0.0");
        jTextFieldHay.setText("0.0");
        jTextFieldDebeHaber.setText("" + debeHaber);
        jButtonApyCierreCaja.setText("Cerrar caja");

    }

    private float obtenerTotalIngresos(int numUltimaCaja) {
        float totalIngresos = 0;
        EntityManager em = emf.createEntityManager();
        Query consulta = em.createNativeQuery("SELECT ven_monto FROM ventas WHERE ven_caja='" + numUltimaCaja + "' ");
        List<Float> listaVentas = (List<Float>) consulta.getResultList();
        if (!listaVentas.isEmpty()) {
            for (int i = 0; i < listaVentas.size(); i++) {
                totalIngresos = totalIngresos + listaVentas.get(i);
            }
        }
        return totalIngresos;
    }

    private void guardarVenta() {
        if (jTableCarrito.getModel().getRowCount() == -0) {
            JOptionPane.showMessageDialog(null, "DEBE CARGAR AL MENOS UN ITEM PARA REGISTRAR LA VENTA", "MENSAJE", 1);
        } else {
            Boolean abierta = new GestionCaja().verificarAperturaDeCaja();
            if (abierta) { // SI CAJA ABIERTA 
                Boolean cajaAbierta = new GestionCaja().verificarAperturaDeCaja();
                if (cajaAbierta) { // SI LA CAJA ESTA ABIERTA
                    FechaYHora fecyHora = new FechaYHora();
                    EntityManager em = emf.createEntityManager();
                    Query consulta = em.createQuery("SELECT max(c.cajId) FROM Caja c");
                    VentasJpaController venControlador = new VentasJpaController(emf);
                    Ventas ventas = new Ventas();
                    ventas.setVenMonto(Float.parseFloat(jTextFieldTotal.getText()));
                    ventas.setVenformaPago(jComboBoxFormaPago.getSelectedIndex());
                    ventas.setVentipoTarjeta(nombreTarjeta);
                    ventas.setVenFecha(fecyHora.fechaActual());
                    ventas.setVenHora(fecyHora.horaActual());
                    ventas.setVenTurno(fecyHora.obtenerTurno());
                    ventas.setVenUsuario(usuario);
                    int idUltCaja = Integer.parseInt(String.valueOf(consulta.getSingleResult()));
                    ventas.setVenCaja(idUltCaja); // EN LA VENTA GUARDO EL ID DE LA CAJA Q YA FUE ABIERTA
                    ventas.setVenEstado(1);
                    venControlador.create(ventas);
                    new GestionVentas().registrarArtPorVentaYActStock(jTableCarrito, ventas);
                    jDialogRegistrarVenta.dispose();
                    new GestionTablas().limparTabla(jTableCarrito, (DefaultTableModel) jTableCarrito.getModel());
                    jLabelTotalCarrito.setText("0.0");
                    jLabelVuelto.setText("");
                    jLabelDescripcionArt.setText("");
                    jSpinner1.setValue(0);
                } else {
                    JOptionPane.showMessageDialog(null, "LA CAJA YA FUE CERRADA. NO SE PUEDEN REGISTRAR VENTAS", "MENSAJE", 1);
                }
            } else {
                JOptionPane.showMessageDialog(null, "DEBE ABRIR LA CAJA ANTES DE REGISTRAR VENTAS", "MENSAJE", 1);
                //abrirCaja();
            }
        }
    }

    private void abrirDialogo(JDialog dialogo, int ancho, int alto) {
        dialogo.setSize(ancho, alto);
        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
    }

    private void limpiarCamposArticulos() {
        jTextFieldCodigoArticulo.setText("");
        jTextFieldDescArticulo.setText("");
        jTextFieldMarcaArticulo.setText("");
        jTextFieldPrecioCompra.setText("");
        jTextFieldPorcUtilidad.setText("");
        jTextFieldPrecioVenta.setText("");
        jComboBoxRubro.setSelectedIndex(0);
        jComboBoxProveedor.setSelectedIndex(0);
        jTextAreaDetalle.setText("");
    }

    private void busquedaRapida(JTextField campoTexto, JTable tabla) {

        String text = campoTexto.getText();
        if (text.equals("")) {
            tabla.setRowSorter(null);
            //sorter.setModel(tabla.getModel());
        } else {
            TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabla.getModel());
            //sorter.setModel(tabla.getModel());
            sorter.setRowFilter(RowFilter.regexFilter("(?i).*" + text + ".*"));
            tabla.setRowSorter(sorter);
        }
    }

    public void permitirSoloNum(java.awt.event.KeyEvent evt) {
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if (((caracter < '0') || (caracter > '9')) && (caracter != '\b')) /*corresponde a BACK_SPACE*/ {
            evt.consume();  // ignorar el evento de teclado
        }
    }

    public void permitirSoloNumYPunto(java.awt.event.KeyEvent evt) {
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if (((caracter < '0') || (caracter > '9')) && (caracter != '.')) {
            evt.consume();  // ignorar el evento de teclado
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbolReportes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButtonAceptar;
    private javax.swing.JButton jButtonApyCierreCaja;
    private javax.swing.JButton jButtonArticulos;
    private javax.swing.JButton jButtonCaja;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonEliminarArticulo;
    private javax.swing.JButton jButtonEliminarUsuario;
    private javax.swing.JButton jButtonEliminarVenta;
    private javax.swing.JButton jButtonGuardarVenta;
    public javax.swing.JButton jButtonHistorialStock;
    private javax.swing.JButton jButtonModificarArticulo;
    public javax.swing.JButton jButtonNuevoArticulo;
    private javax.swing.JButton jButtonNuevoProveedor;
    private javax.swing.JButton jButtonNuevoRubro;
    private javax.swing.JButton jButtonOpciones;
    private javax.swing.JButton jButtonPresupuesto;
    private javax.swing.JButton jButtonRegistrarArticulo;
    private javax.swing.JButton jButtonRegistrarUsuario;
    private javax.swing.JButton jButtonRegistrarVenta;
    public javax.swing.JButton jButtonReponerStock;
    public javax.swing.JButton jButtonReponerStock1;
    public javax.swing.JButton jButtonReponerStock4;
    private javax.swing.JButton jButtonSeleccionar;
    private javax.swing.JButton jButtonStock;
    private javax.swing.JButton jButtonVentas;
    private javax.swing.JComboBox jComboBoxFormaPago;
    private javax.swing.JComboBox jComboBoxProveedor;
    private javax.swing.JComboBox jComboBoxRubro;
    private javax.swing.JComboBox jComboBoxTipoUsuario;
    private javax.swing.JDialog jDialogABMArticulo;
    private javax.swing.JDialog jDialogAcercaDe;
    private javax.swing.JDialog jDialogArticulos;
    private javax.swing.JDialog jDialogCaja;
    private javax.swing.JDialog jDialogHistorialStock;
    private javax.swing.JDialog jDialogOpciones;
    private javax.swing.JDialog jDialogRegistrarVenta;
    private javax.swing.JDialog jDialogStock;
    private javax.swing.JDialog jDialogVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescripcionArt;
    private javax.swing.JLabel jLabelOpcionesDeReportes;
    private javax.swing.JLabel jLabelPrecioKilo;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JLabel jLabelTotalCarrito;
    private javax.swing.JLabel jLabelVuelto;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItemAcercaDe;
    private javax.swing.JMenuItem jMenuItemCerrarCaja;
    private javax.swing.JMenuItem jMenuItemCerrarSesion;
    private javax.swing.JMenuItem jMenuItemDetalleArt;
    private javax.swing.JMenuItem jMenuItemEliminar;
    private javax.swing.JMenuItem jMenuItemManual;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordFieldContraseña;
    private javax.swing.JPopupMenu jPopupMenuCerrarCaja;
    private javax.swing.JPopupMenu jPopupMenuDetalleArt;
    private javax.swing.JPopupMenu jPopupMenuEliminar;
    private javax.swing.JRadioButton jRadioButtonRespaldar;
    private javax.swing.JRadioButton jRadioButtonRestaurar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableArticulos;
    private javax.swing.JTable jTableCaja;
    private javax.swing.JTable jTableCarrito;
    private javax.swing.JTable jTableHistorialStock;
    private javax.swing.JTable jTableStock;
    private javax.swing.JTable jTableUsuarios;
    private javax.swing.JTable jTableVentas;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextAreaDetalle;
    private javax.swing.JTextField jTextFieldBusquedaArticulo;
    private javax.swing.JTextField jTextFieldBusquedaHistorialStock;
    private javax.swing.JTextField jTextFieldBusquedaStock;
    private javax.swing.JTextField jTextFieldBusquedaVentas;
    private javax.swing.JTextField jTextFieldCajaInicial;
    private javax.swing.JTextField jTextFieldCajaNum;
    private javax.swing.JTextField jTextFieldCodigo;
    private javax.swing.JTextField jTextFieldCodigoArticulo;
    private javax.swing.JTextField jTextFieldDebeHaber;
    private javax.swing.JTextField jTextFieldDescArticulo;
    private javax.swing.JTextField jTextFieldEncargado;
    private javax.swing.JTextField jTextFieldFecha;
    private javax.swing.JTextField jTextFieldHay;
    private javax.swing.JTextField jTextFieldHora;
    private javax.swing.JTextField jTextFieldMarcaArticulo;
    private javax.swing.JTextField jTextFieldNombreArchivo;
    private javax.swing.JTextField jTextFieldPagaCon;
    private javax.swing.JTextField jTextFieldPorcUtilidad;
    private javax.swing.JTextField jTextFieldPrecioCompra;
    private javax.swing.JTextField jTextFieldPrecioVenta;
    private javax.swing.JTextField jTextFieldTotal;
    private javax.swing.JTextField jTextFieldTotalEgresos;
    private javax.swing.JTextField jTextFieldTotalIngresos;
    private javax.swing.JTextField jTextFieldUsuario;
    // End of variables declaration//GEN-END:variables
}
