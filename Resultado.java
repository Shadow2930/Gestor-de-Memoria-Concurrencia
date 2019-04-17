/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

import java.util.Date;

/**
 *
 * @author Sergio Bonachera
 */
public class Resultado {
    private final Date tiempoInicio;
    private Date tiempoEjecucion;
    private long duracion;
    private int numFallos;
    private final int idProceso;

    public Resultado(int idProceso) {
        this.tiempoInicio = new Date();
        this.idProceso = idProceso;
    } 

    @Override
    public String toString() {
        return "Proceso: "+ idProceso + " \n---------Tiempo de Inicio = " + tiempoInicio + " \n---------Tiempo de Ejecucion = " + duracion + " \n---------numFallos = " + numFallos;
    }

    public Date getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public void setTiempoEjecucion(Date tiempoEjecucion) {
        this.tiempoEjecucion = tiempoEjecucion;
        this.duracion = (tiempoEjecucion.getTime()-tiempoInicio.getTime())/1000;
    }

    public int getNumFallos() {
        return numFallos;
    }

    public void setNumFallos(int numFallos) {
        this.numFallos = numFallos;
    }

    public Date getTiempoInicio() {
        return tiempoInicio;
    }

    public int getIdProceso() {
        return idProceso;
    }
    
    
    
}
