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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DelKo
 */
@Entity
@Table(name = "art_ven")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArtVen.findAll", query = "SELECT a FROM ArtVen a"),
    @NamedQuery(name = "ArtVen.findByArtVenId", query = "SELECT a FROM ArtVen a WHERE a.artVenId = :artVenId"),
    @NamedQuery(name = "ArtVen.findByCant", query = "SELECT a FROM ArtVen a WHERE a.cant = :cant"),
    @NamedQuery(name = "ArtVen.findByPrecio", query = "SELECT a FROM ArtVen a WHERE a.precio = :precio"),
    @NamedQuery(name = "ArtVen.findByArtVenEstado", query = "SELECT a FROM ArtVen a WHERE a.artVenEstado = :artVenEstado")})
public class ArtVen implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "art_ven_id")
    private Integer artVenId;
    @Basic(optional = false)
    @Column(name = "cant")
    private float cant;
    @Basic(optional = false)
    @Column(name = "precio")
    private float precio;
    @Column(name = "art_ven_estado")
    private Integer artVenEstado;
    @JoinColumn(name = "ven_id", referencedColumnName = "ven_id")
    @ManyToOne(optional = false)
    private Ventas venId;
    @JoinColumn(name = "art_id", referencedColumnName = "art_id")
    @ManyToOne(optional = false)
    private Articulos artId;

    public ArtVen() {
    }

    public ArtVen(Integer artVenId) {
        this.artVenId = artVenId;
    }

    public ArtVen(Integer artVenId, float cant, float precio) {
        this.artVenId = artVenId;
        this.cant = cant;
        this.precio = precio;
    }

    public Integer getArtVenId() {
        return artVenId;
    }

    public void setArtVenId(Integer artVenId) {
        this.artVenId = artVenId;
    }

    public float getCant() {
        return cant;
    }

    public void setCant(float cant) {
        this.cant = cant;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public Integer getArtVenEstado() {
        return artVenEstado;
    }

    public void setArtVenEstado(Integer artVenEstado) {
        this.artVenEstado = artVenEstado;
    }

    public Ventas getVenId() {
        return venId;
    }

    public void setVenId(Ventas venId) {
        this.venId = venId;
    }

    public Articulos getArtId() {
        return artId;
    }

    public void setArtId(Articulos artId) {
        this.artId = artId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artVenId != null ? artVenId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArtVen)) {
            return false;
        }
        ArtVen other = (ArtVen) object;
        if ((this.artVenId == null && other.artVenId != null) || (this.artVenId != null && !this.artVenId.equals(other.artVenId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.ArtVen[ artVenId=" + artVenId + " ]";
    }
    
}
