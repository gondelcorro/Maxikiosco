/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author DelKo
 */
@Entity
@Table(name = "ventas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ventas.findAll", query = "SELECT v FROM Ventas v"),
    @NamedQuery(name = "Ventas.findByVenId", query = "SELECT v FROM Ventas v WHERE v.venId = :venId"),
    @NamedQuery(name = "Ventas.findByVenFecha", query = "SELECT v FROM Ventas v WHERE v.venFecha = :venFecha"),
    @NamedQuery(name = "Ventas.findByVenHora", query = "SELECT v FROM Ventas v WHERE v.venHora = :venHora"),
    @NamedQuery(name = "Ventas.findByVenMonto", query = "SELECT v FROM Ventas v WHERE v.venMonto = :venMonto"),
    @NamedQuery(name = "Ventas.findByVenformaPago", query = "SELECT v FROM Ventas v WHERE v.venformaPago = :venformaPago"),
    @NamedQuery(name = "Ventas.findByVentipoTarjeta", query = "SELECT v FROM Ventas v WHERE v.ventipoTarjeta = :ventipoTarjeta"),
    @NamedQuery(name = "Ventas.findByVenTurno", query = "SELECT v FROM Ventas v WHERE v.venTurno = :venTurno"),
    @NamedQuery(name = "Ventas.findByVenUsuario", query = "SELECT v FROM Ventas v WHERE v.venUsuario = :venUsuario"),
    @NamedQuery(name = "Ventas.findByVenCaja", query = "SELECT v FROM Ventas v WHERE v.venCaja = :venCaja"),
    @NamedQuery(name = "Ventas.findByVenEstado", query = "SELECT v FROM Ventas v WHERE v.venEstado = :venEstado")})
public class Ventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ven_id")
    private Integer venId;
    @Basic(optional = false)
    @Column(name = "ven_fecha")
    private String venFecha;
    @Basic(optional = false)
    @Column(name = "ven_hora")
    private String venHora;
    @Basic(optional = false)
    @Column(name = "ven_monto")
    private float venMonto;
    @Basic(optional = false)
    @Column(name = "ven_formaPago")
    private int venformaPago;
    @Column(name = "ven_tipoTarjeta")
    private String ventipoTarjeta;
    @Column(name = "ven_turno")
    private String venTurno;
    @Column(name = "ven_usuario")
    private String venUsuario;
    @Basic(optional = false)
    @Column(name = "ven_caja")
    private int venCaja;
    @Column(name = "ven_estado")
    private Integer venEstado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venId")
    private List<ArtVen> artVenList;

    public Ventas() {
    }

    public Ventas(Integer venId) {
        this.venId = venId;
    }

    public Ventas(Integer venId, String venFecha, String venHora, float venMonto, int venformaPago, int venCaja) {
        this.venId = venId;
        this.venFecha = venFecha;
        this.venHora = venHora;
        this.venMonto = venMonto;
        this.venformaPago = venformaPago;
        this.venCaja = venCaja;
    }

    public Integer getVenId() {
        return venId;
    }

    public void setVenId(Integer venId) {
        this.venId = venId;
    }

    public String getVenFecha() {
        return venFecha;
    }

    public void setVenFecha(String venFecha) {
        this.venFecha = venFecha;
    }

    public String getVenHora() {
        return venHora;
    }

    public void setVenHora(String venHora) {
        this.venHora = venHora;
    }

    public float getVenMonto() {
        return venMonto;
    }

    public void setVenMonto(float venMonto) {
        this.venMonto = venMonto;
    }

    public int getVenformaPago() {
        return venformaPago;
    }

    public void setVenformaPago(int venformaPago) {
        this.venformaPago = venformaPago;
    }

    public String getVentipoTarjeta() {
        return ventipoTarjeta;
    }

    public void setVentipoTarjeta(String ventipoTarjeta) {
        this.ventipoTarjeta = ventipoTarjeta;
    }

    public String getVenTurno() {
        return venTurno;
    }

    public void setVenTurno(String venTurno) {
        this.venTurno = venTurno;
    }

    public String getVenUsuario() {
        return venUsuario;
    }

    public void setVenUsuario(String venUsuario) {
        this.venUsuario = venUsuario;
    }

    public int getVenCaja() {
        return venCaja;
    }

    public void setVenCaja(int venCaja) {
        this.venCaja = venCaja;
    }

    public Integer getVenEstado() {
        return venEstado;
    }

    public void setVenEstado(Integer venEstado) {
        this.venEstado = venEstado;
    }

    @XmlTransient
    public List<ArtVen> getArtVenList() {
        return artVenList;
    }

    public void setArtVenList(List<ArtVen> artVenList) {
        this.artVenList = artVenList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (venId != null ? venId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ventas)) {
            return false;
        }
        Ventas other = (Ventas) object;
        if ((this.venId == null && other.venId != null) || (this.venId != null && !this.venId.equals(other.venId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Ventas[ venId=" + venId + " ]";
    }
    
}
