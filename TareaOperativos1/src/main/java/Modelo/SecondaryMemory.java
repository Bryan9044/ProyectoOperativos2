package Modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import Modelo.EntryFile;
import Modelo.BCP;

public class SecondaryMemory {

    Map<Integer, String> storage;       
    Map<String, EntryFile> fileIndex;   
    int totalSize;          
    int virtualSize;       
    int fileAreaEnd;        


    Map<String, BCP> swappedProcesses;  
    int swapNextFree;                   

    public SecondaryMemory(int totalSize, int virtualSize) {
        this.totalSize    = totalSize;
        this.virtualSize  = virtualSize;
        this.fileAreaEnd  = totalSize - virtualSize - 1;
        this.storage      = new HashMap<>();
        this.fileIndex    = new HashMap<>();
        this.swappedProcesses = new LinkedHashMap<>();
        this.swapNextFree = totalSize - virtualSize; // empieza justo después del área de archivos
    }


    public void addInstructions(int position, String instruction) throws Exception {
        if (position > fileAreaEnd) {
            throw new Exception("Error: posición " + position +
                " supera el área de archivos del disco (máx " + fileAreaEnd + ").");
        }
        if (instruction == null || instruction.isBlank()) {
            throw new Exception("Error: la instrucción no puede ser vacía.");
        }
        storage.put(position, instruction);
    }

    public Map<Integer, String> getInstructions() {
        return storage;
    }

    public String obtainInstructions(int index) {
        StringBuilder result = new StringBuilder();
        String instruction = storage.get(index);
        if (instruction != null) {
            System.out.println("Índice: " + index);
            result.append(instruction).append("\n");
        }
        return result.toString();
    }

    public void addFile(String file, EntryFile entryFile) throws Exception {
        if (file == null || file.isBlank()) {
            throw new Exception("Error: el nombre del archivo no puede ser vacío.");
        }
        fileIndex.put(file, entryFile);
    }

    public Map<String, EntryFile> getFiles() {
        return fileIndex;
    }


    public boolean swapOut(BCP bcp) throws Exception {
        if (bcp == null) {
            throw new Exception("Error: no se puede hacer swap de un proceso nulo.");
        }
        if (swappedProcesses.containsKey(bcp.getFileName())) {
            throw new Exception("Error: el proceso " + bcp.getFileName() + " ya está en swap.");
        }
        if (swapNextFree >= totalSize) {
            throw new Exception("Error: no hay espacio en el área de swap del disco.");
        }
        swappedProcesses.put(bcp.getFileName(), bcp);
        System.out.println("SwapOut → proceso " + bcp.getID() +
            " (" + bcp.getFileName() + ") enviado a swap [pos " + swapNextFree + "]");
        swapNextFree++;
        return true;
    }

    public BCP swapIn(String fileName) throws Exception {
        if (!swappedProcesses.containsKey(fileName)) {
            throw new Exception("Error: el proceso " + fileName + " no está en swap.");
        }
        BCP bcp = swappedProcesses.get(fileName);
        System.out.println("SwapIn  ← proceso " + bcp.getID() +
            " (" + fileName + ") traído desde swap a RAM.");
        return bcp;
    }

    public boolean removeFromVirtual(String fileName) {
        if (swappedProcesses.containsKey(fileName)) {
            swappedProcesses.remove(fileName);
            if (swapNextFree > totalSize - virtualSize) swapNextFree--;
            System.out.println("Proceso " + fileName + " eliminado del swap.");
            return true;
        }
        return false;
    }

    public boolean hasProcessInVirtual(String fileName) {
        return swappedProcesses.containsKey(fileName);
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

    public void clearSwap() {
        swappedProcesses.clear();
        swapNextFree = totalSize - virtualSize;
    }

    public int getTotalSize()    { return totalSize; }
    public int getVirtualSize()  { return virtualSize; }
    public int getFileAreaEnd()  { return fileAreaEnd; }

    public int getFreeFileSpace() {
        return (fileAreaEnd + 1) - storage.size();
    }

    /** Espacio libre en el área de swap. */
    public int getFreeSwapSpace() {
        return totalSize - swapNextFree;
    }
    
    public void clear() {
        storage.clear();
        fileIndex.clear();
        swappedProcesses.clear();
        swapNextFree = totalSize - virtualSize;
    }
    
    @Override
    public String toString() {
        return "SecondaryMemory {" +
               "totalSize=" + totalSize +
               ", virtualSize=" + virtualSize +
               ", fileAreaEnd=" + fileAreaEnd +
               ", archivos=" + fileIndex.size() +
               ", instrucciones=" + storage.size() +
               ", swapUsado=" + swappedProcesses.size() +
               ", swapLibre=" + getFreeSwapSpace() +
               '}';
    }
}