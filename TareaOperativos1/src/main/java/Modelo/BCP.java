/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

/**
 *
 * @author bryan
 */
public class BCP {
    String id;
    String status;
    int PC;
    int AX;
    int BX;
    int CX;
    String DX;
    int AC;
    String IR;
    int memoryStart; 
    int memoryEnd;
    int processSize;
    String Priority;
    
    int[] stack = new int[5];
    int stackPointer = -1;
    
    int cpuID;
    int startTime;
    int elapsedTime;
    int totalTime;
    
    List<String> openFiles = new ArrayList<>();
    BCP next;
    String fileName;
    
    
    LocalDateTime  startInstant;
    LocalDateTime endInstant;
    
public BCP(String id, String status, int memoryStart, int memoryEnd, 
           int processSize, String priority, int cpuID, int totalTime) {
    this.id = id;
    this.status = status;      
    this.PC = 0;                
    this.AX = 0;
    this.BX = 0;
    this.CX = 0;
    this.DX = "";
    this.AC = 0;
    this.IR = "";
    this.memoryStart = memoryStart;
    this.memoryEnd = memoryEnd;
    this.processSize = processSize;
    this.Priority = priority;
    this.cpuID = cpuID;
    this.startTime = 1;         
    this.elapsedTime = 1;
    this.totalTime = totalTime; 
    this.stackPointer = -1;
    this.openFiles = new ArrayList<>();
    this.next = null;
    this.fileName = "";
}


    public String getID() {
        return id;
    }
    
    public String getStatus() {
        return status;
    }
    
    
    public int getPC() {
        return PC;
    }
    
    public int getAX() {
        return AX;
    }
    
    public int getBX() {
        return BX;
    } 
    
    public int getCX() {
        return CX;
    }
    
    public String getDX() {
        return DX;
    }  
    
    public int getAC() {
        return AC;
    }   
    
    public String getPriority() {
        return Priority;
    }


    public String getIR() {
        return IR;
    }
    
    public int getMemoryStart() {
        return memoryStart;
    }  
    
    public int getMemoryEnd() {
        return memoryEnd;
    }    
    
    public String getFileName() {
        return fileName;
    }
    
    public int getProcessSize() {
        return processSize;
    }
    
    public int getCpuID(){
        return cpuID;   
    }
    
    public int getStartTime(){
        return startTime;   
    }

    public int getElapsedTime(){
        return elapsedTime;   
    }

    public int getTotalTime(){
        return totalTime;   
    }    
    
 
    public void setAC(int value) {
    AC = value;  
    }

    public void setAX(int value) {
    AX = value;  
    }

    public void setBX(int value) {
    BX = value; 
    }

    public void setCX(int value) {
    CX = value;  
    }

    public void setDX(String value) {
        DX = value;  
    }

    public void setPC() {
    PC++; 
    }
    
    public void setPriority(String value) {
    Priority = value; 
    }
    
    public void setPC(int value) {
    PC = value;
    }
    
    public void setIR(String value) {
    IR = value;  
    }
 
    public void setStatus(String value) {
    status = value;  
    }
    
    public void setFileName(String file) {
        fileName = file;
    }
    
    // Metodos para mi manejo de archivos 
    public void openFile(String fileName) {
        if (!openFiles.contains(fileName)) {
            openFiles.add(fileName);
        }
    }

    public void closeFile(String fileName) {
        openFiles.remove(fileName);
    }

    public boolean isFileOpen(String fileName) {
        return openFiles.contains(fileName);
    }

    public List<String> getOpenFiles() {
        return openFiles;
    }
    // Metodos para mi manejo de archivos

    
    // Metodos para la pila
    public void push(int value) throws Exception {
        if (stackPointer >= 4) throw new Exception("Error: desbordamiento de pila");
        stack[++stackPointer] = value;
    }

    public int pop() throws Exception {
        if (stackPointer < 0) throw new Exception("Error: pila vacía");
        return stack[stackPointer--];
    }    
    public int getStackPointer() {
        return stackPointer;
    }
    
    public void printStack() {
        System.out.println("StackPointer actual: " + stackPointer);
        for (int i = 0; i <= stackPointer; i++) {
            System.out.println("Posición " + i + ": " + stack[i]);
        }
    }
    
    
    // Metodos para la pila 
    
    
    public void addTime(int value){
        elapsedTime += value;
    }
    
    public void setFinalTime(int startTime, int elapsedTime) {
        totalTime = startTime + elapsedTime;
    }
    
    
    
    public void markStart() {
        this.startInstant = LocalDateTime.now();
    }

    public void markEnd() {
        this.endInstant = LocalDateTime.now();
    }

    public String getStartFormatted() {
        return startInstant.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getEndFormatted() {
        return endInstant.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public long getDurationSeconds() {
        return Duration.between(startInstant, endInstant).getSeconds();
    }   
    
    
    public void setNext(BCP next) {
        this.next = next;
    }

    public String getNextBCPId() {
        return (next != null) ? next.getID() : "null";
    }  
    
    public void setMemoryStart(int pos) {
        memoryStart = pos;
    }
    
    public void setMemoryEnd(int pos) {
        memoryEnd = pos;
    }    
    
    
    @Override
    public String toString() {
        return "BCP {" +
               "id='" + id + '\'' +
               ", status='" + status + '\'' +
               ", PC=" + PC +
               ", AX=" + AX +
               ", BX=" + BX +
               ", CX=" + CX +
               ", DX=" + DX +
               ", AC=" + AC +
               ", IR='" + IR + '\'' +
               ", memoryStart=" + memoryStart +
               ", memoryEnd=" + memoryEnd +
               ", processSize=" + processSize +
               ", Priority='" + Priority + '\'' +
               ", stackPointer=" + stackPointer +
               ", cpuID=" + cpuID +
               ", startTime=" + startTime +
               ", elapsedTime=" + elapsedTime +
               ", totalTime=" + totalTime +
               ", fileName='" + fileName + '\'' +
               ", openFiles=" + openFiles +
               '}';
    }
    
    
}
