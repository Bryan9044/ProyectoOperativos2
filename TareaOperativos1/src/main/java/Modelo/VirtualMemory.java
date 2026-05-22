/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author bryan
 */
public class VirtualMemory {
    Map<Integer, String> storage;
    int totalSize;
    Map<String, int[]> processPositions = new LinkedHashMap<>();
    int nextFreePosition = 0;
    List<int[]> freeSlots = new ArrayList<>();
    Map<String, BCP> swappedProcesses = new LinkedHashMap<>();  
    
    public VirtualMemory(int totalSize) {
        this.storage = new HashMap<>();
        this.totalSize = totalSize;
    }
    
    public void addInstruction(int position, String instruction) throws Exception {
        if (position >= totalSize) {
            throw new Exception("Error: La posición no puede sobre pasar el limite de la memoria virtual");
        }
        if (instruction == null || instruction.isBlank()) {
            throw new Exception("Error: La instrucción no puede ser vacia.");
        }
        storage.put(position, instruction);
    }
    
    public int loadProcess(String fileName, ArrayList<String[]> instructions) throws Exception {
        int size = instructions.size();
        
        for (int i = 0; i < freeSlots.size(); i++) {
            int[] slot = freeSlots.get(i);
            if (slot[1] - slot[0] + 1 >= size) {
                freeSlots.remove(i);
                int startPos = slot[0];
                for (int j = 0; j < size; j++) {
                    storage.put(startPos + j, instructions.get(j)[0]);
                }
                processPositions.put(fileName, new int[]{startPos, startPos + size - 1});
                return startPos;
            }
        }
        
        if (nextFreePosition + size > totalSize) {
            throw new Exception("No hay espacio en memoria virtual para: " + fileName);
        }
        int startPos = nextFreePosition;
        for (String[] instruction : instructions) {
            storage.put(nextFreePosition, instruction[0]);
            nextFreePosition++;
        }
        processPositions.put(fileName, new int[]{startPos, nextFreePosition - 1});
        return startPos;
    }

    public Map<Integer, String> getProcessInstructions(String fileName) {
        int[] range = processPositions.get(fileName);
        if (range == null) return null;
        Map<Integer, String> instructions = new HashMap<>();
        for (int i = range[0]; i <= range[1]; i++) {
            instructions.put(i, storage.get(i));
        }
        return instructions;
    }

    public void freeProcess(String fileName) {
        int[] range = processPositions.get(fileName);
        if (range == null) return;
        for (int i = range[0]; i <= range[1]; i++) {
            storage.remove(i);
        }
        freeSlots.add(new int[]{range[0], range[1]}); 
        processPositions.remove(fileName);
    }

    public boolean hasProcess(String fileName) {
        return processPositions.containsKey(fileName);
    }

    public boolean hasSpace(int size) {
        return nextFreePosition + size <= totalSize;
    }

    public int getFreeSpace() {
        return totalSize - nextFreePosition;
    }    
    
    
    public Map<Integer, String> getInstructions() {
        return storage;
    }
    
    public String getPosition(int position) {
        return storage.getOrDefault(position, "");
    }
    
    public int getTotalSize() {
        return totalSize;
    }

    public void clear() {
        storage.clear();
    }
    
    public int getUsedSpace() {
        return nextFreePosition;
    }    
    
    public Map<String, int[]> getProcessPositions() {
        return processPositions;
    }    
    
  
     
    public boolean swapOut(BCP bcp) throws Exception {
        if (bcp == null) {
            throw new Exception("Error: No se puede hacer swap de un proceso nulo");
        }
        
        if (swappedProcesses.containsKey(bcp.getFileName())) {
            throw new Exception("Error: El proceso " + bcp.getFileName() + " ya está en memoria virtual");
        }
        
        swappedProcesses.put(bcp.getFileName(), bcp);
        System.out.println("Proceso " + bcp.getID() + " (" + bcp.getFileName() + ") enviado a memoria virtual");
        return true;
    }
    

    public BCP swapIn(String fileName) throws Exception {
        if (!swappedProcesses.containsKey(fileName)) {
            throw new Exception("Error: El proceso " + fileName + " no está en memoria virtual");
        }
        
        BCP bcp = swappedProcesses.get(fileName);
        System.out.println("Proceso " + bcp.getID() + " (" + fileName + ") traído desde memoria virtual a RAM");
        return bcp;
    }

    
    public boolean hasProcessInVirtual(String fileName) {
        return swappedProcesses.containsKey(fileName);
    }
    

    public boolean removeFromVirtual(String fileName) {
        if (swappedProcesses.containsKey(fileName)) {
            swappedProcesses.remove(fileName);
            System.out.println("Proceso " + fileName + " eliminado de memoria virtual");
            return true;
        }
        return false;
    }
    

    public BCP getProcessFromVirtual(String fileName) {
        return swappedProcesses.get(fileName);
    }
    

    public boolean hasSwappedProcesses() {
        return !swappedProcesses.isEmpty();
    }
    

    public int getSwappedProcessCount() {
        return swappedProcesses.size();
    }
    

    public Map<String, BCP> getSwappedProcesses() {
        return new HashMap<>(swappedProcesses);
    }
}
