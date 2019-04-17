/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bonachera.romero.sergio.practica.pkg2;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio Bonachera
 */
public class Gestor implements Runnable{
    private Monitor monitor;

    public Gestor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        System.out.println("SE INICIA la ejecucion del GESTOR DE MEMORIA");
        boolean control = true;
        
        while(control){
            if(Thread.currentThread().interrupted()){
                control = false;
            }
            monitor.liberarMarcos();
            monitor.asignarMarcos();
            
            try {
                TimeUnit.MILLISECONDS.sleep(750);
            } catch (InterruptedException ex) {
                control = false;
            }
        }
        System.out.println("FINALIZA la ejecucion del GESTOR DE MEMORIA");
    }
    
    
}
