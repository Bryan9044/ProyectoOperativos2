/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import java.util.ArrayList;
import Modelo.BCP;
import Modelo.CPU;
/**dios
 * 
 *
 * @author bryan
 */
public class Dispatcher {
    ArrayList<BCP> processes;
    BCP currentProcedure;
    CPU currentCPU;
    
    public Dispatcher(ArrayList<BCP> processes, BCP currentProcedure, CPU currentCPU) {
        this.processes = processes;
        this.currentProcedure = currentProcedure;
        this.currentCPU = currentCPU;
                
    }

    public void saveContext() {
        if (currentCPU == null || currentProcedure == null) {
            return;
        }

        currentProcedure.setAX(currentCPU.getRegister("AX"));
        currentProcedure.setBX(currentCPU.getRegister("BX"));
        currentProcedure.setCX(currentCPU.getRegister("CX"));
        currentProcedure.setDX(currentCPU.getDX());
        currentProcedure.setAC(currentCPU.getRegister("AC"));
        currentProcedure.setPC(currentCPU.getRegister("PC"));
        currentProcedure.setIR(currentCPU.getIR());
        currentProcedure.setStatus("Interrumpido");
    }

    public void loadContext(BCP nextProcess) {
        if (currentCPU == null || nextProcess == null) {
            return;
        }

        currentCPU.setRegisterValue("AX", nextProcess.getAX());
        currentCPU.setRegisterValue("BX", nextProcess.getBX());
        currentCPU.setRegisterValue("CX", nextProcess.getCX());
        currentCPU.setDX(nextProcess.getDX());
        currentCPU.setRegisterValue("AC", nextProcess.getAC());
        currentCPU.setLoadPC(nextProcess.getPC());
        currentCPU.setIR(nextProcess.getIR());
        nextProcess.setStatus("Ejecutando");
    }

    public BCP dispatch(BCP nextProcess) {
        if (currentProcedure != null) {
            saveContext();
            if (!"Finished".equalsIgnoreCase(currentProcedure.getStatus())) {
                currentProcedure.setStatus("Preparado");
            }
        }

        if (nextProcess == null) {
            currentProcedure = null;
            return null;
        }

        loadContext(nextProcess);
        nextProcess.setStatus("Running");
        currentProcedure = nextProcess;
        return currentProcedure;
    }

    public BCP getCurrentProcedure() {
        return currentProcedure;
    }

    public CPU getCurrentCPU() {
        return currentCPU;
    }

    public void setCurrentCPU(CPU currentCPU) {
        this.currentCPU = currentCPU;
    }
    
    public void setCurrentProcedure(BCP currentProcedure) {
        this.currentProcedure = currentProcedure;
    }    
    
}
