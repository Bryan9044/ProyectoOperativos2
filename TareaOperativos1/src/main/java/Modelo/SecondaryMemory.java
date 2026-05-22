/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.util.HashMap;
import java.util.Map;
import Modelo.EntryFile;
/**
 *
 * @author bryan
 */
public class SecondaryMemory {
    Map<Integer, String> storage; // Aqui instrucciones del archivo
    int totalSize;
    Map<String, EntryFile> fileIndex; // Aqui archivo e indice 

   public SecondaryMemory(int totalSize) {
        this.storage = new HashMap<>();
        this.totalSize = totalSize;
        this.fileIndex = new HashMap<>();
        
    }
   
   public void addInstructions(int position, String instruction) throws Exception{
       
       if (position > totalSize){
           throw new Exception("Error: La posición no puede sobre pasar el limite de la memoria.");
       }
       
       if (instruction.contentEquals(" ")){
           throw new Exception("Error: La instrucción no puede ser vacia.");
       }
       storage.put(position, instruction);
    }
   
   public Map<Integer, String> getInstructions(){
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
   
   public void addFile(String file, EntryFile entryFile) throws Exception{  
       if (file.contentEquals(" ")){
           throw new Exception("Error: La instrucción no puede ser vacia.");
       }
       fileIndex.put(file, entryFile);
    }

    public Map<String, EntryFile> getFiles() {
        return fileIndex;
    }   
   
    public int getTotalSize() {
       return totalSize;
   }
  
    
    
}