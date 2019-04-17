/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

import static bonachera.romero.sergio.practica.pkg2.BonacheraRomeroSergioPractica2.MARCOS_MAXIMOS;
import static bonachera.romero.sergio.practica.pkg2.BonacheraRomeroSergioPractica2.MARCOS_MINIMOS;
import static bonachera.romero.sergio.practica.pkg2.BonacheraRomeroSergioPractica2.NUM_MARCOS;
import static bonachera.romero.sergio.practica.pkg2.BonacheraRomeroSergioPractica2.SIN_MARCOS;
import static bonachera.romero.sergio.practica.pkg2.BonacheraRomeroSergioPractica2.TipoPeticion.CARGA;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Sergio Bonachera
 */
public class Monitor {

    public static int PAGINA_NULA = -1;
    public static int FIRST = 0;

    private int marcosLibres;

    private ArrayList<Peticion> listaPeticiones;
    private ArrayList<Memoria> listaMarcosProceso;
    private ArrayList<Integer> listaLiberacionMarcos;

    private Lock exm;
    private Condition pendienteInicio;
    private Condition pendienteMarco;
    
    private double fallosDePagina;
    private double asignaciones;

    
    public Monitor() {
        marcosLibres = NUM_MARCOS;

        listaPeticiones = new ArrayList();
        listaMarcosProceso = new ArrayList();
        listaLiberacionMarcos = new ArrayList();

        exm = new ReentrantLock();
        pendienteInicio = exm.newCondition();
        pendienteMarco = exm.newCondition();
        
        fallosDePagina = 0;
        asignaciones = 0;
        
    }

    public void solicitarCarga(int idProceso) throws InterruptedException {
        exm.lock();

        try {
            Peticion peticion = new Peticion(idProceso, BonacheraRomeroSergioPractica2.TipoPeticion.CARGA, PAGINA_NULA);
            listaPeticiones.add(peticion);
            pendienteInicio.await();
        } finally {
            exm.unlock();
        }
    }

    public void solicitarMarco(int idProceso, int pagina) throws InterruptedException {
        exm.lock();

        try {
            Peticion peticion = new Peticion(idProceso, BonacheraRomeroSergioPractica2.TipoPeticion.FALLO_PAGINA, pagina);
            listaPeticiones.add(peticion);
            pendienteMarco.await();
        } finally {
            exm.unlock();
        }
    }

    public boolean paginaEnMemoria(int idProceso, int pagina) {
        exm.lock();

        try {
            Memoria memoriaProceso = listaMarcosProceso.get(encuentraProceso(idProceso));
            return memoriaProceso.enMemoria(pagina);
        } finally {
            exm.unlock();
        }
    }

    public void solicitarLiberacion(int idProceso) {
        exm.lock();
        System.out.println("El proceso: " + idProceso + " solicita la LIBERACION, hay " + listaLiberacionMarcos.size() + " procesos junto a el");

        try {
            Memoria memoriaProceso = listaMarcosProceso.remove(encuentraProceso(idProceso));
            int numMarcos = memoriaProceso.getNumMarcos();
            listaLiberacionMarcos.add(numMarcos);
        } finally {
            exm.unlock();
        }
    }

    public void liberarMarcos() {
            exm.lock();

        try {
            if(!listaLiberacionMarcos.isEmpty())
                System.out.println("                                                               SE VAN A LIBERAR MARCOS DE " + listaLiberacionMarcos.size() + " PROCESOS");                
            while (!listaLiberacionMarcos.isEmpty()) {
                int numMarcos = listaLiberacionMarcos.remove(FIRST);
                marcosLibres += numMarcos;
                System.out.println("                                                               SE VAN A LIBERAR " + numMarcos + " , quedan " + marcosLibres);
            }
        }finally {
            exm.unlock();
        }
    }

    public void asignarMarcos() {
        exm.lock();

        try {
            if (!listaPeticiones.isEmpty()) {
                Peticion peticion = listaPeticiones.get(FIRST);
                if ((peticion.getTipo() == CARGA) && (marcosLibres >= MARCOS_MINIMOS)) {
                    asignacionInicio();
                    asignaciones += 2;
                } else {
                    asignacionFalloPagina();
                    fallosDePagina++; //Se obvian las dos primeras paginas de la carga, dado que no se consideran fallo de pagina
                }
            }
        } finally {
            exm.unlock();
        }
    }

    private void asignacionInicio() {
        exm.lock();

        try {
            Peticion peticion = listaPeticiones.remove(FIRST);
            marcosLibres -= MARCOS_MINIMOS;
            int idProceso = peticion.getIdProceso();
            System.out.println("El proceso: " + idProceso + " obtendrá las dos primeras paginas, quedan: " + marcosLibres + " marcos libres");
            ArrayList<Integer> listaMarcos = new ArrayList();
            Memoria memoriaProceso = new Memoria(idProceso, listaMarcos);
            memoriaProceso.setMarco(BonacheraRomeroSergioPractica2.PRIMERA);
            memoriaProceso.setMarco(BonacheraRomeroSergioPractica2.SEGUNDA);

            listaMarcosProceso.add(memoriaProceso);
            pendienteInicio.signal();
        } finally {
            exm.unlock();
        }
    }

    private void asignacionFalloPagina() {
        exm.lock();

        try {
            Peticion peticion = listaPeticiones.remove(encuentraFalloPagina());
            int idProceso = peticion.getIdProceso();
            int pagina = peticion.getPagina();
            Memoria memoriaProceso = listaMarcosProceso.remove(encuentraProceso(idProceso));
            //System.out.println("FALLO DE PAGINA PROCESO: " + idProceso);
            if ((marcosLibres > SIN_MARCOS) && (memoriaProceso.getNumMarcos() < MARCOS_MAXIMOS)) {
                memoriaProceso.setMarco(pagina);
                asignaciones++;
                marcosLibres--;
                //System.out.println("                                 SE AÑADE UNA P: " + idProceso + " quedan: " + marcosLibres + " marcos libres");

            } else {
                memoriaProceso.reemplazoPagina(pagina);
                //System.out.println("SE REEMPLAZA UNA: " + idProceso);
            }
            listaMarcosProceso.add(memoriaProceso);
            pendienteMarco.signal();
        } finally {
            exm.unlock();
        }
    }

    private int encuentraProceso(int id) {
        int index = 0;
        boolean encontrado = false;
        int i = 0;
        while (!encontrado) {
            if (i < listaMarcosProceso.size()) {
                if (listaMarcosProceso.get(i).getIdProceso() == id) {
                    index = i;
                    encontrado = true;
                }
            }
            i++;
        }
        return index;
    }

    private int encuentraFalloPagina() {
        int index = 0;
        boolean encontrado = false;
        int i = 0;
        while (!encontrado) {
            if (i < listaPeticiones.size()) {
                if (listaPeticiones.get(i).getTipo() == BonacheraRomeroSergioPractica2.TipoPeticion.FALLO_PAGINA) {
                    index = i;
                    encontrado = true;
                }
            }
            i++;
        }
        return index;
    }

    public double getFallosDePagina() {
        return fallosDePagina;
    }

    public double getAsignaciones() {
        return asignaciones;
    }
}
