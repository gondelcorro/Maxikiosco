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
@Table(name = "caja")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Caja.findAll", query = "SELECT c FROM Caja c"),
    @NamedQuery(name = "Caja.findByCajId", query = "SELECT c FROM Caja c WHERE c.cajId = :cajId"),
    @NamedQuery(name = "Caja.findByCajFecha", query = "SELECT c FROM Caja c WHERE c.cajFecha = :cajFecha"),
    @NamedQuery(name = "Caja.findByCajHora", query = "SELECT c FROM Caja c WHERE c.cajHora = :cajHora"),
    @NamedQuery(name = "Caja.findByCajTurno", query = "SELECT c FROM Caja c WHERE c.cajTurno = :cajTurno"),
    @NamedQuery(name = "Caja.findByCajEncargado", query = "SELECT c FROM Caja c WHERE c.cajEncargado = :cajEncargado"),
    @NamedQuery(name = "Caja.findByCajInicial", query = "SELECT c FROM Caja c WHERE c.cajInicial = :cajInicial"),
    @NamedQuery(name = "Caja.findByCajIngresos", query = "SELECT c FROM Caja c WHERE c.cajIngresos = :cajIngresos"),
    @NamedQuery(name = "Caja.findByCajEgresos", query = "SELECT c FROM Caja c WHERE c.cajEgresos = :cajEgresos"),
    @NamedQuery(name = "Caja.findByCajFinal", query = "SELECT c FROM Caja c WHERE c.cajFinal = :cajFinal"),
    @NamedQuery(name = "Caja.findByCajHay", query = "SELECT c FROM Caja c WHERE c.cajHay = :cajHay"),
    @NamedQuery(name = "Caja.findByCajEstado", query = "SELECT c FROM Caja c WHERE c.cajEstado = :cajEstado")})
public class Caja implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "caj_id")
    private Integer cajId;
    @Basic(optional = false)
    @Column(name = "caj_fecha")
    private String cajFecha;
    @Basic(optional = false)
    @Column(name = "caj_hora")
    private String cajHora;
    @Basic(optional = false)
    @Column(name = "caj_turno")
    private String cajTurno;
    @Basic(optional = false)
    @Column(name = "caj_encargado")
    private String cajEncargado;
    @Basic(optional = false)
    @Column(name = "caj_Inicial")
    private float cajInicial;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "caj_Ingresos")
    private Float cajIngresos;
    @Column(name = "caj_Egresos")
    private Float cajEgresos;
    @Column(name = "caj_final")
    private Float cajFinal;
    @Column(name = "caj_hay")
    private Float cajHay;
    @Column(name = "caj_estado")
    private Integer cajEstado;

    public Caja() {
    }

    public Caja(Integer cajId) {
        this.cajId = cajId;
    }

    public Caja(Integer cajId, String cajFecha, String cajHora, String cajTurno, String cajEncargado, float cajInicial) {
        this.cajId = cajId;
        this.cajFecha = cajFecha;
        this.cajHora = cajHora;
        this.cajTurno = cajTurno;
        this.cajEncargado = cajEncargado;
        this.cajInicial = cajInicial;
    }

    public Integer getCajId() {
        return cajId;
    }

    public void setCajId(Integer cajId) {
        this.cajId = cajId;
    }

    public String getCajFecha() {
        return cajFecha;
    }

    public void setCajFecha(String cajFecha) {
        this.cajFecha = cajFecha;
    }

    public String getCajHora() {
        return cajHora;
    }

    public void setCajHora(String cajHora) {
        this.cajHora = cajHora;
    }

    public String getCajTurno() {
        return cajTurno;
    }

    public void setCajTurno(String cajTurno) {
        this.cajTurno = cajTurno;
    }

    public String getCajEncargado() {
        return cajEncargado;
    }

    public void setCajEncargado(String cajEncargado) {
        this.cajEncargado = cajEncargado;
    }

    public float getCajInicial() {
        return cajInicial;
    }

    public void setCajInicial(float cajInicial) {
        this.cajInicial = cajInicial;
    }

    public Float getCajIngresos() {
        return cajIngresos;
    }

    public void setCajIngresos(Float cajIngresos) {
        this.cajIngresos = cajIngresos;
    }

    public Float getCajEgresos() {
        return cajEgresos;
    }

    public void setCajEgresos(Float cajEgresos) {
        this.cajEgresos = cajEgresos;
    }

    public Float getCajFinal() {
        return cajFinal;
    }

    public void setCajFinal(Float cajFinal) {
        this.cajFinal = cajFinal;
    }

    public Float getCajHay() {
        return cajHay;
    }

    public void setCajHay(Float cajHay) {
        this.cajHay = cajHay;
    }

    public Integer getCajEstado() {
        return cajEstado;
    }

    public void setCajEstado(Integer cajEstado) {
        this.cajEstado = cajEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cajId != null ? cajId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Caja)) {
            return false;
        }
        Caja other = (Caja) object;
        if ((this.cajId == null && other.cajId != null) || (this.cajId != null && !this.cajId.equals(other.cajId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Caja[ cajId=" + cajId + " ]";
    }
    
}
