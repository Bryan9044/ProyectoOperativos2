/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.HashMap;

/**
 *
 * @author bryan
 */
public class EntryFile {
    String name;
    int index;
    int startPosition;
    int lastInt;
    int memoryStart;  // estos 2 para los procesos 
    int memoryEnd; 

   public EntryFile(String name, int index, int startPosition, int lastInt) {
        this.name = name;
        this.index = index;
        this.startPosition = startPosition;
        this.lastInt = lastInt; 
        this.memoryStart = 0;
        this.memoryEnd = 0;
    }

    public int getStartPosition() {
        return startPosition;
    }


    public int getLastInt() {
        return lastInt;
    }

    public int getIndex() {
        return index;
    }
    
    public void setMemoryStart(int memoryStart) {
        this.memoryStart = memoryStart;
    }

    public void setMemoryEnd(int memoryEnd) {
        this.memoryEnd = memoryEnd;
    }

    public int getMemoryStart() {
        return memoryStart;
    }

    public int getMemoryEnd() {
        return memoryEnd;
    }
    
    public String getName() {
        return name;
    }
   
    @Override
    public String toString() {
        return "EntryFile{name='" + name + "', index=" + index +
               ", start=" + startPosition + ", last=" + lastInt + "}";
    }   
   
}
