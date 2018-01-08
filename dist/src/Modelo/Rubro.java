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
@Table(name = "rubro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rubro.findAll", query = "SELECT r FROM Rubro r"),
    @NamedQuery(name = "Rubro.findByRubId", query = "SELECT r FROM Rubro r WHERE r.rubId = :rubId"),
    @NamedQuery(name = "Rubro.findByRubNombre", query = "SELECT r FROM Rubro r WHERE r.rubNombre = :rubNombre"),
    @NamedQuery(name = "Rubro.findByRubEstado", query = "SELECT r FROM Rubro r WHERE r.rubEstado = :rubEstado")})
public class Rubro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rub_id")
    private Integer rubId;
    @Basic(optional = false)
    @Column(name = "rub_nombre")
    private String rubNombre;
    @Column(name = "rub_estado")
    private Integer rubEstado;

    public Rubro() {
    }

    public Rubro(Integer rubId) {
        this.rubId = rubId;
    }

    public Rubro(Integer rubId, String rubNombre) {
        this.rubId = rubId;
        this.rubNombre = rubNombre;
    }

    public Integer getRubId() {
        return rubId;
    }

    public void setRubId(Integer rubId) {
        this.rubId = rubId;
    }

    public String getRubNombre() {
        return rubNombre;
    }

    public void setRubNombre(String rubNombre) {
        this.rubNombre = rubNombre;
    }

    public Integer getRubEstado() {
        return rubEstado;
    }

    public void setRubEstado(Integer rubEstado) {
        this.rubEstado = rubEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rubId != null ? rubId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rubro)) {
            return false;
        }
        Rubro other = (Rubro) object;
        if ((this.rubId == null && other.rubId != null) || (this.rubId != null && !this.rubId.equals(other.rubId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Rubro[ rubId=" + rubId + " ]";
    }
    
}
