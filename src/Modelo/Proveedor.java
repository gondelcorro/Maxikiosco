/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DelKo
 */
@Entity
@Table(name = "proveedor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p"),
    @NamedQuery(name = "Proveedor.findByProId", query = "SELECT p FROM Proveedor p WHERE p.proId = :proId"),
    @NamedQuery(name = "Proveedor.findByProNombre", query = "SELECT p FROM Proveedor p WHERE p.proNombre = :proNombre"),
    @NamedQuery(name = "Proveedor.findByProEstado", query = "SELECT p FROM Proveedor p WHERE p.proEstado = :proEstado")})
public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pro_id")
    private Integer proId;
    @Basic(optional = false)
    @Column(name = "pro_nombre")
    private String proNombre;
    @Column(name = "pro_estado")
    private Integer proEstado;

    public Proveedor() {
    }

    public Proveedor(Integer proId) {
        this.proId = proId;
    }

    public Proveedor(Integer proId, String proNombre) {
        this.proId = proId;
        this.proNombre = proNombre;
    }

    public Integer getProId() {
        return proId;
    }

    public void setProId(Integer proId) {
        this.proId = proId;
    }

    public String getProNombre() {
        return proNombre;
    }

    public void setProNombre(String proNombre) {
        this.proNombre = proNombre;
    }

    public Integer getProEstado() {
        return proEstado;
    }

    public void setProEstado(Integer proEstado) {
        this.proEstado = proEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proId != null ? proId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proveedor)) {
            return false;
        }
        Proveedor other = (Proveedor) object;
        if ((this.proId == null && other.proId != null) || (this.proId != null && !this.proId.equals(other.proId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Proveedor[ proId=" + proId + " ]";
    }
    
}
