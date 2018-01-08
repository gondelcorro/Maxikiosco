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
@Table(name = "stock")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stock.findAll", query = "SELECT s FROM Stock s"),
    @NamedQuery(name = "Stock.findByStoId", query = "SELECT s FROM Stock s WHERE s.stoId = :stoId"),
    @NamedQuery(name = "Stock.findByStoCodigoArt", query = "SELECT s FROM Stock s WHERE s.stoCodigoArt = :stoCodigoArt"),
    @NamedQuery(name = "Stock.findByStocantActual", query = "SELECT s FROM Stock s WHERE s.stocantActual = :stocantActual"),
    @NamedQuery(name = "Stock.findByStocantRecarga", query = "SELECT s FROM Stock s WHERE s.stocantRecarga = :stocantRecarga"),
    @NamedQuery(name = "Stock.findByStofechaRecarga", query = "SELECT s FROM Stock s WHERE s.stofechaRecarga = :stofechaRecarga"),
    @NamedQuery(name = "Stock.findByStocantVendida", query = "SELECT s FROM Stock s WHERE s.stocantVendida = :stocantVendida"),
    @NamedQuery(name = "Stock.findByStoEstado", query = "SELECT s FROM Stock s WHERE s.stoEstado = :stoEstado")})
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sto_id")
    private Integer stoId;
    @Basic(optional = false)
    @Column(name = "sto_codigo_art")
    private long stoCodigoArt;
    @Basic(optional = false)
    @Column(name = "sto_cantActual")
    private int stocantActual;
    @Column(name = "sto_cantRecarga")
    private Integer stocantRecarga;
    @Column(name = "sto_fechaRecarga")
    private String stofechaRecarga;
    @Column(name = "sto_cantVendida")
    private Integer stocantVendida;
    @Column(name = "sto_estado")
    private Integer stoEstado;

    public Stock() {
    }

    public Stock(Integer stoId) {
        this.stoId = stoId;
    }

    public Stock(Integer stoId, long stoCodigoArt, int stocantActual) {
        this.stoId = stoId;
        this.stoCodigoArt = stoCodigoArt;
        this.stocantActual = stocantActual;
    }

    public Integer getStoId() {
        return stoId;
    }

    public void setStoId(Integer stoId) {
        this.stoId = stoId;
    }

    public long getStoCodigoArt() {
        return stoCodigoArt;
    }

    public void setStoCodigoArt(long stoCodigoArt) {
        this.stoCodigoArt = stoCodigoArt;
    }

    public int getStocantActual() {
        return stocantActual;
    }

    public void setStocantActual(int stocantActual) {
        this.stocantActual = stocantActual;
    }

    public Integer getStocantRecarga() {
        return stocantRecarga;
    }

    public void setStocantRecarga(Integer stocantRecarga) {
        this.stocantRecarga = stocantRecarga;
    }

    public String getStofechaRecarga() {
        return stofechaRecarga;
    }

    public void setStofechaRecarga(String stofechaRecarga) {
        this.stofechaRecarga = stofechaRecarga;
    }

    public Integer getStocantVendida() {
        return stocantVendida;
    }

    public void setStocantVendida(Integer stocantVendida) {
        this.stocantVendida = stocantVendida;
    }

    public Integer getStoEstado() {
        return stoEstado;
    }

    public void setStoEstado(Integer stoEstado) {
        this.stoEstado = stoEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stoId != null ? stoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) object;
        if ((this.stoId == null && other.stoId != null) || (this.stoId != null && !this.stoId.equals(other.stoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Stock[ stoId=" + stoId + " ]";
    }
    
}
