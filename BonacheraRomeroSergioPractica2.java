package bonachera.romero.sergio.practica.pkg2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sergio Bonachera
 */
public class BonacheraRomeroSergioPractica2 {

    public static int NUM_MARCOS = 20;
    public static int MARCOS_MINIMOS = 2;
    public static int SIN_MARCOS = 0;
    public static int MARCOS_MAXIMOS = 4;
    public static int PRIMERA = 1;
    public static int SEGUNDA = 2;

    public static int MINUTOS = 3;
    public static int CONVERSOR = 60000; //Conversor de minutos a milisegundos
    public static int MIN_TIEMPO_ENTRE_PROCESOS = 1;
    public static int HOLGURA_TIEMPO_ENTRE_PROCESOS = 3;

    public static enum TipoPeticion {
        CARGA, FALLO_PAGINA
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random rand = new Random();
        ExecutorService ejecucion = Executors.newCachedThreadPool();

        ArrayList<Future<Resultado>> listaTareas = new ArrayList();

        Monitor monitor = new Monitor();

        Gestor gestorMemoria = new Gestor(monitor);
        Future<?> gestor = ejecucion.submit(gestorMemoria);

        long fin = System.currentTimeMillis();
        fin = fin + MINUTOS * CONVERSOR;
        
        int idProceso = 0;
        while (System.currentTimeMillis() < fin) {
            Proceso proceso = new Proceso(idProceso, monitor);
            idProceso++;
            Future<Resultado> p = ejecucion.submit(proceso);
            listaTareas.add(p);
            
            int tiempo = MIN_TIEMPO_ENTRE_PROCESOS + rand.nextInt(HOLGURA_TIEMPO_ENTRE_PROCESOS);
            
            TimeUnit.SECONDS.sleep(tiempo);
        }
        System.out.println("SE VAN A CANCELAR LAS TAREAS");
        ejecucion.shutdown();
        
        gestor.cancel(true);
        
        double cont = 0;
        for (int i = 0; i < listaTareas.size(); i++) {
            if(!listaTareas.get(i).isDone()){
                cont++;
            }else{
                System.out.println(listaTareas.get(i).get().toString());
            }        
            listaTareas.get(i).cancel(true);
        }
        System.out.println("Se han cancelado: " + (int)cont + " tareas");
        System.out.println("La media de asignaciones de marcos es: " + (monitor.getAsignaciones() / (idProceso + 1))*100 + "%"); //Se consideran asignaciones cuando se añaden marcos al proceso, los fallos de pagina por reemplazo no se consideran
        System.out.println("La media de fallos de pagina es: " + (monitor.getAsignaciones() / monitor.getFallosDePagina())*100 + "%"); //Se consideran fallos de pagina cuando solicita en el curso de ejecucion una pagina que no dispone entre sus paginas cargadas
    }

}
