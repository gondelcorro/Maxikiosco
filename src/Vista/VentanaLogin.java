package Vista;

import Controladores.UsuarioJpaController;
import Modelo.Usuario;
import java.awt.Color;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;

public class VentanaLogin extends javax.swing.JFrame {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");
    private String nombre;
    private String contraseña;
    private int tipoUsuario;

    public VentanaLogin() {
        initComponents();
        this.setLocationRelativeTo(null);
        crearSuperUsuario();
    }

    private void crearSuperUsuario() {
        UsuarioJpaController usuControlador = new UsuarioJpaController(emf);
        if (usuControlador.findUsuarioEntities().isEmpty()) {
            usuControlador.create(new Usuario(1, "admin", "admin", 1));
        }
    }

    private boolean login() {
        boolean ok = false;
        //UsuarioJpaController usuControlador = new UsuarioJpaController(emf);
        nombre = jTextFieldNombreUsuario.getText();
        contraseña = new String(jPasswordContraseña.getPassword());
        EntityManager em = emf.createEntityManager();
        Query consulta = em.createQuery("SELECT u FROM Usuario u");
        List<Usuario> listaUsuarios = consulta.getResultList();
        for (int i = 0; i < listaUsuarios.size(); i++) {
            Usuario usuario = listaUsuarios.get(i);
            if (usuario.getUsuNombre().equals(nombre)) {
                if (usuario.getUsuContrasenia().equals(contraseña)) { // COMPARO EL ARREGLO DE LAS 2 PASSWORD
                    tipoUsuario = usuario.getUsuTipo();
                    ok = true;
                    break;
                }
            }
        }
        return ok;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextFieldNombreUsuario = new javax.swing.JTextField();
        jPasswordContraseña = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonIngresar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("REGISTRO DE USUARIO");
        setResizable(false);

        jTextFieldNombreUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldNombreUsuario.setToolTipText("USUARIO");

        jPasswordContraseña.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPasswordContraseña.setToolTipText("CONTRASEÑA");
        jPasswordContraseña.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordContraseñaKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel1.setText("Nombre de usuario:");

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel2.setText("Contraseña:");

        jButtonIngresar.setBackground(new java.awt.Color(255, 255, 255));
        jButtonIngresar.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jButtonIngresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/boton_ingresar.jpg"))); // NOI18N
        jButtonIngresar.setToolTipText("INGRESAR");
        jButtonIngresar.setBorder(null);
        jButtonIngresar.setBorderPainted(false);
        jButtonIngresar.setContentAreaFilled(false);
        jButtonIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonIngresarMouseExited(evt);
            }
        });
        jButtonIngresar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jButtonIngresarMouseMoved(evt);
            }
        });
        jButtonIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIngresarActionPerformed(evt);
            }
        });
        jButtonIngresar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jButtonIngresarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jButtonIngresarFocusLost(evt);
            }
        });
        jButtonIngresar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonIngresarKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel3.setText("Bienvenido!");

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel4.setText("Por favor, ingrese sus datos de acceso.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addGap(47, 47, 47))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jTextFieldNombreUsuario)
                            .addComponent(jPasswordContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(65, 65, 65))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(47, 47, 47)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(14, 14, 14)
                .addComponent(jPasswordContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jButtonIngresar)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIngresarActionPerformed
        boolean login = login();
        if (login) {
            this.setVisible(false);
            new VentanaPrincipal(nombre, tipoUsuario).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "VERIFIQUE SUS DATOS");
        }
    }//GEN-LAST:event_jButtonIngresarActionPerformed

    private void jButtonIngresarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIngresarMouseExited
        jButtonIngresar.setBorderPainted(false);
        jButtonIngresar.setContentAreaFilled(false);
        jButtonIngresar.setBackground(Color.blue);
    }//GEN-LAST:event_jButtonIngresarMouseExited

    private void jButtonIngresarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonIngresarMouseMoved
        jButtonIngresar.setBorderPainted(true);
        jButtonIngresar.setBorder(new BevelBorder(0));
        jButtonIngresar.setBackground(Color.white);
    }//GEN-LAST:event_jButtonIngresarMouseMoved

    private void jButtonIngresarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jButtonIngresarFocusGained
        jButtonIngresar.setBorderPainted(true);
        jButtonIngresar.setBorder(new BevelBorder(0));
        jButtonIngresar.setBackground(Color.white);
    }//GEN-LAST:event_jButtonIngresarFocusGained

    private void jButtonIngresarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jButtonIngresarFocusLost
        jButtonIngresar.setBorderPainted(false);
        jButtonIngresar.setContentAreaFilled(false);
        jButtonIngresar.setBackground(Color.blue);
    }//GEN-LAST:event_jButtonIngresarFocusLost

    private void jPasswordContraseñaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordContraseñaKeyReleased
        if (evt.getKeyCode() == evt.VK_ENTER) {
            jButtonIngresar.requestFocus();
        }
    }//GEN-LAST:event_jPasswordContraseñaKeyReleased

    private void jButtonIngresarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonIngresarKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            boolean login = login();
            if (login) {
                this.setVisible(false);
                new VentanaPrincipal(nombre, tipoUsuario).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "VERIFIQUE SUS DATOS");
            }
        }
    }//GEN-LAST:event_jButtonIngresarKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonIngresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordContraseña;
    private javax.swing.JTextField jTextFieldNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
