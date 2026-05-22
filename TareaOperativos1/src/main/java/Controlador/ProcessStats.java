/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author bryan
 */
public class ProcessStats {
    private String processName;
    private String startTime;
    private String endTime;
    private long durationSeconds;

    public ProcessStats(String processName, String startTime, String endTime, long durationSeconds) {
        this.processName = processName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
    }
    
    
    public String getProcessName(){
        return processName; 
    }

    public String getStartTime(){
        return startTime; 
    }

    public String getEndTime(){
        return endTime; 
    }

    public long getDurationSeconds(){
        return durationSeconds; 
    }
    
    
    
    
    
    
    
    
    @Override
    public String toString() {
        return "Proceso: " + processName +
               " | Inicio: " + startTime +
               " | Fin: " + endTime +
               " | Duración: " + durationSeconds + "s";
    }
}

