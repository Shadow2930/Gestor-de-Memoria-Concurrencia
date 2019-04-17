/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

import java.util.ArrayList;

/**
 *
 * @author Sergio Bonachera
 */
public class Memoria {
    public static int FIRST = 0;
    
    private int idProceso;
    private ArrayList<Integer> listaMarcos;

    public Memoria(int idProceso, ArrayList<Integer> listaMarcos) {
        this.idProceso = idProceso;
        this.listaMarcos = listaMarcos;
    }
    
    public void setMarco( int pagina ){
        listaMarcos.add(pagina);
    }
    
    public boolean enMemoria( int pagina ){
        return listaMarcos.contains(pagina);
    }
    
    public int getNumMarcos(){
        return listaMarcos.size();
    }
    
    public void reemplazoPagina( int pagina ){
        listaMarcos.remove(FIRST);
        listaMarcos.add(pagina);
    }
    
    public int getIdProceso(){
        return idProceso;
    }
}
