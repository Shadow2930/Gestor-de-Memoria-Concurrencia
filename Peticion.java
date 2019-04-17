/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

/**
 *
 * @author Sergio Bonachera
 */
public class Peticion {
    private int idProceso;
    private BonacheraRomeroSergioPractica2.TipoPeticion tipo;
    private int pagina;

    public Peticion(int idProceso, BonacheraRomeroSergioPractica2.TipoPeticion tipo, int pagina) {
        this.idProceso = idProceso;
        this.tipo = tipo;
        this.pagina = pagina;
    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdPeticion(int idProceso) {
        this.idProceso = idProceso;
    }

    public BonacheraRomeroSergioPractica2.TipoPeticion getTipo() {
        return tipo;
    }

    public void setTipo(BonacheraRomeroSergioPractica2.TipoPeticion tipo) {
        this.tipo = tipo;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }
    
    
}
