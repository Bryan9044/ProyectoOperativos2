/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author bryan
 */
public class Memory {
   Map<Integer, String> memory;
   int finalSizeUser;
   int startSizeUser;
   public Memory(int finalSizeUser,int startSizeUser) {
        this.memory = new HashMap<>();
        this.finalSizeUser = finalSizeUser;
        this.startSizeUser = startSizeUser;
        // preguntar al profe si el de 0 a 19 también es mpdificable
    }
   
   public void writeMemory(int position, String value) throws Exception {
       if(position >= finalSizeUser) {
           throw new Exception("Error: La posición no puede sobre pasar el limite de la memoria para usuario");
       }
       if(position < startSizeUser) {
           throw new Exception("Error: La posición no puede ser menor al minimo de la memoria para usuario");   
       }
       memory.put(position, value);
       
   }
   
   public void writeMemoryKernel(int position, String value) throws Exception {
       memory.put(position, value);  
   }   
   
    public Map<Integer, String> getMemory() {
        return memory;
    }
    
    public String getPosition(int position) {
        return memory.getOrDefault(position, "");
    }
    
    public int getTotalSize() {
        return finalSizeUser;
    }
    
    
} 
