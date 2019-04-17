/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sergio Bonachera
 */
public class Proceso implements Callable<Resultado> {

    public static int MIN_NUM_EJECUCIONES = 8;
    public static int HOLGURA_NUM_EJECUCIONES = 5;
    public static int MIN_TIEMPO_CICLO = 1;
    public static int HOLGURA_TIEMPO_CICLO = 2;
    public static int MIN_NUM_PAGINAS = 4;
    public static int HOLGURA_NUM_PAGINAS = 5;

    private int idProceso;
    private Monitor monitor;

    public Proceso(int idProceso, Monitor monitor) {
        this.idProceso = idProceso;
        this.monitor = monitor;
    }

    @Override
    public Resultado call() throws Exception {
        Resultado res = new Resultado(idProceso);
        try {
            Random rand = new Random();
            int fallopagina = 0;
            monitor.solicitarCarga(idProceso);
            System.out.println("PROCESO: " + idProceso + " CARGADO en memoria");

            int totalEjecuciones = generarEjecucionesProceso();
            int totalPaginas = generarPaginasProceso();
            System.out.println("Proceso: "+ idProceso + " ejecuciones = " + totalEjecuciones + " paginas = " + totalPaginas);
            int ejecucion = 0;
            do {
                int pagina = generarPaginaEjecucion(totalPaginas);
                if (!monitor.paginaEnMemoria(idProceso, pagina)) {
                    fallopagina++;
                    monitor.solicitarMarco(idProceso, pagina);
                }
                ejecucion++;
                //System.out.println("Proceso: " + idProceso + " ejecucion: " + ejecucion + " pagina: " + pagina);
                TimeUnit.SECONDS.sleep(MIN_TIEMPO_CICLO + rand.nextInt(HOLGURA_TIEMPO_CICLO));
            } while (ejecucion < totalEjecuciones);
            monitor.solicitarLiberacion(idProceso);
            
            Date finEjecucion = new Date();
            res.setNumFallos(fallopagina);
            res.setTiempoEjecucion(finEjecucion);
            
            System.out.println("FINALIZA la ejecucion del PROCESO: " + idProceso);
        } catch (InterruptedException ex) {
        }
        return res;
    }

    public int generarEjecucionesProceso() {
        Random rand = new Random();
        int ejecuciones = MIN_NUM_EJECUCIONES + rand.nextInt(HOLGURA_NUM_EJECUCIONES);

        return ejecuciones;
    }

    public int generarPaginasProceso() {
        Random rand = new Random();
        int paginas = MIN_NUM_PAGINAS + rand.nextInt(HOLGURA_NUM_PAGINAS);

        return paginas;
    }

    public int generarPaginaEjecucion(int totalPaginas) {
        Random rand = new Random();
        int pag = 1 + rand.nextInt(totalPaginas);

        return pag;
    }
}
