/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Negocio;

import Controladores.CajaJpaController;
import Controladores.StockJpaController;
import Controladores.exceptions.NonexistentEntityException;
import Modelo.Caja;
import Modelo.Stock;
import Modelo.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gonzalo
 */
public class GestionCaja {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ComercioPU");

    public void cargarTablaCaja(JTable tablaCaja) {
        new GestionTablas().limparTabla(tablaCaja, (DefaultTableModel) tablaCaja.getModel());
        CajaJpaController cajaControlador = new CajaJpaController(emf);
        List<Caja> listaDeCajas = cajaControlador.findCajaEntities();
        DefaultTableModel dtmArticulos = (DefaultTableModel) tablaCaja.getModel();
        String datos[] = new String[dtmArticulos.getColumnCount()];
        for (int i = 0; i < listaDeCajas.size(); i++) {
            Caja caja = listaDeCajas.get(i);
            datos[0] = String.valueOf(caja.getCajId());
            datos[1] = String.valueOf(caja.getCajEncargado());
            datos[2] = String.valueOf(caja.getCajInicial());
            datos[3] = String.valueOf(caja.getCajFinal());
            int estado = caja.getCajEstado();
            if (estado == 0) {
                datos[4] = "ABIERTA";
            } else {
                datos[4] = "CERRADA";
            }
            dtmArticulos.addRow(datos);
        }
    }

    public int obtenerUltimaCaja() {

        int numUltimaCaja=0;
        EntityManager em = emf.createEntityManager();
        // SELECCIONA LA ULTIMA CAJA
        Query consulta = em.createQuery("SELECT max(c.cajId) FROM Caja c");
        try {
            numUltimaCaja = Integer.parseInt(String.valueOf(consulta.getSingleResult()));
        } catch (NoResultException noResultException) {
            JOptionPane.showMessageDialog(null, "No existe ninguna caja");
        }
        return numUltimaCaja;
    }

    public Boolean verificarAperturaDeCaja() {

        Boolean abierta = false;
        int ultimaCaja = obtenerUltimaCaja();
        Caja miCaja = new Caja();
        EntityManager em = emf.createEntityManager();
        // SELECCIONA LA ULTIMA CAJA
        Query consulta = em.createNamedQuery("Caja.findByCajId");
        consulta.setParameter("cajId", ultimaCaja);
        try {
            miCaja = (Caja)(consulta.getSingleResult());
            if(miCaja.getCajEstado()==0){
                abierta=true;
            }else{
                abierta=false;
            }
        } catch (NoResultException noResultException) {
            JOptionPane.showMessageDialog(null, "No existe ninguna caja");
        }
        return abierta;
    }
}
