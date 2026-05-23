/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import Modelo.BCP;
import java.util.LinkedList; 
import Modelo.VirtualMemory;
import Modelo.SecondaryMemory;

/**
 *
 * @author bryan
 */
// Clase solicitada según lo que vimos en el grupo de Operativos por el uso de FIFO
    // Aunque sea un monoproceso...
public class Scheduler {
    LinkedList<BCP> queue = new LinkedList<>();
    LinkedList<BCP> Waitingqueue = new LinkedList<>();
    
    public void addProcess(BCP bcp) {
        if (!queue.isEmpty()) {
            queue.getLast().setNext(bcp); 
        }
        queue.add(bcp);
    }
    
    public void addWaitingProcess(BCP bcp) {
        Waitingqueue.add(bcp); 
    }
    
    public BCP nextProcessWaiting() {
        return Waitingqueue.poll(); 
    }    

    public BCP nextProcess() {
        return queue.poll(); 
    }

    public BCP peekProcess() {
        return queue.isEmpty() ? null : queue.peek();
    }
    
    public BCP promoteWaitingToReady() {
        if (hasWaitingProcesses() && canAddToReady()) {
            BCP promoted = nextProcessWaiting();
            addProcess(promoted);
            promoted.setPriority("Alta");
            System.out.println("Proceso promovido de espera a listo: " + promoted.getID());
            return promoted;
        } else {
            if (!canAddToReady()) {
                System.out.println("La cola principal está llena, no se puede promover.");
            } else if (!hasWaitingProcesses()) {
                System.out.println("No hay procesos en espera para promover.");
            }
            return null;
        }
    }
    
    public void promoteWaitingIfReady(SecondaryMemory secondaryMemory) {
        BCP nextWaiting = peekWaiting();
        if (nextWaiting == null) return;
 
        if (!secondaryMemory.hasProcessInVirtual(nextWaiting.getFileName())) {
            promoteWaitingToReady();
            System.out.println("Proceso promovido a listo: " + nextWaiting.getFileName());
        }
    }   
    
    public boolean hasWaitingProcesses() {
        return !Waitingqueue.isEmpty();
    }
    public boolean hasProcesses() {
        return !queue.isEmpty();
    }
    
    public boolean canAddToReady() {
        return !itsBusy();
    }
    
    public LinkedList<BCP> getQueue() {
        return queue;
    }
    
    public LinkedList<BCP> getWaitingQueue() {
        return Waitingqueue;
    }
    
    public boolean itsBusy() {
        int large = queue.size();

        if (large >= 5) {
            return true;
        }
        else {
            return false;

        }
    }

    public BCP findWaitingByFileName(String fileName) {
        for (BCP b : Waitingqueue) {
            if (b.getFileName().equals(fileName)) {
                return b;
            }
        }
        return null;
    }    
    
    
    public BCP peekWaiting() {
        return Waitingqueue.isEmpty() ? null : Waitingqueue.peek();
    }


    
  
}
