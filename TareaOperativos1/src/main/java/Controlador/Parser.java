/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import java.io.*;
import java.io.File;
import Modelo.Memory;
import Modelo.BCP;
import Modelo.CPU;
import Modelo.EntryFile;
import Vista.pagPrincipal;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
/**
 *
 * @author bryan
 */
public class Parser {
    private pagPrincipal window;
    private Dispatcher dispatcher;
    int stopProgram = 0;
    boolean waitingForInput = false;
    boolean interruptPaused = false;
    public Parser(pagPrincipal window,Dispatcher dispatcher){
        this.window = window;
        this.dispatcher = dispatcher;
    }
    
    public void show(String message) {
        window.showInScreen(message);
    }

    public boolean shouldStop() {
        return stopProgram == 1;
    }

    public void resetStopFlag() {
        stopProgram = 0;
    }

    int jumpTarget = -1;


    public boolean shouldJump() { return jumpTarget != -1; }
    public int getJumpTarget() { return jumpTarget; }
    public void resetJump() { jumpTarget = -1; }
 
    private boolean isValidRegister(String register){
        return register.equals("AX") || register.equals("BX") || register.equals("CX") || register.equals("DX") || register.equals("AC") 
                || register.equals("AH") || register.equals("AL") ; 
    }
 
    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    public void setWaitingForInput(boolean waiting) {
        this.waitingForInput = waiting;
    }

    public boolean isInterruptPaused() {
        return interruptPaused;
    }

    public void resetInterruptPaused() {
        interruptPaused = false;
    }
    
    
    
    public void EntryData(String action, String destiny, String source) throws Exception {
        String empty = "";
        switch(action) {
            case "MOV" -> {
                if(!isValidRegister(destiny)){
                    System.out.println(destiny + " Soy el registro");
                    throw new Exception("Error: registro destino no válido");
                } else {
                    // Caso hexadecimal
                    if (source.toUpperCase().endsWith("H")) {
                        int num = Integer.parseInt(source.replace("H",""), 16);
                        String binaryCode = "0011 " + getRegisterCode(destiny) + fromIntegerToBinary(String.valueOf(num));
                        System.out.println("BinaryCode " + binaryCode);
                    } else {
                        try {
                            // Caso decimal
                            int num = Integer.parseInt(source.trim());
                            String binaryCode = "0011 " + getRegisterCode(destiny) + fromIntegerToBinary(source);
                            System.out.println("BinaryCode " + binaryCode);
                        } catch (NumberFormatException e) {
                            // Caso texto
                            if (destiny.equalsIgnoreCase("DX")) {
                                System.out.println("MOV DX con texto: " + source.trim());
   
                            } else if (destiny.equalsIgnoreCase("AL")){
                                System.out.println("MOV AL con texto: " + source.trim());
                            } else {
                                throw new Exception("Error: valor no numérico para " + destiny);
                            }
                        }
                    }
                }
            }




            case "LOAD" -> {
                if(!isValidRegister(destiny)) {
                    throw new Exception("Error: registro destino no válido");
                }
                if(!source.equals(empty)){
                    throw new Exception("Error: Esta operación no necesita una segunda dirección");
                }
                else {
                    String firstPart = "0001 ";
                    String middlePart = getRegisterCode(destiny);
                    String lastPart = " 00000000";
                    String binaryCode = firstPart.concat(middlePart.concat(lastPart));
                    System.out.println("BinaryCode " + binaryCode);
                }
            }
            
            case "ADD" -> {
                if(!isValidRegister(destiny)) {
                    throw new Exception("Error: registro destino no válido");
                }
                if(!source.equals(empty)){
                    throw new Exception("Error: Esta operación no necesita una segunda dirección");
                }
                else {
                    String firstPart = "0101 ";
                    String middlePart = getRegisterCode(destiny);
                    String lastPart = " 00000000";
                    String binaryCode = firstPart.concat(middlePart.concat(lastPart));
                    System.out.println("BinaryCode " + binaryCode);
                }
            }
            case "SUB" -> {
                if(!isValidRegister(destiny)) {
                    throw new Exception("Error: registro destino no válido");
                }
                if(!source.equals(empty)){
                    throw new Exception("Error: Esta operación no necesita una segunda dirección");
                }
                else {
                    String firstPart = "0100 ";
                    String middlePart = getRegisterCode(destiny);
                    String lastPart = " 00000000";
                    String binaryCode = firstPart.concat(middlePart.concat(lastPart));
                    System.out.println("BinaryCode " + binaryCode);
                }
            }
            case "STORE" -> {
                if(!isValidRegister(destiny)) {
                    throw new Exception("Error: registro destino no válido");
                }
                if(!source.equals(empty)){
                    throw new Exception("Error: Esta operación no necesita una segunda dirección");
                }
                else {
                    String firstPart = "0010 ";
                    String middlePart = getRegisterCode(destiny);
                    String lastPart = " 00000000";
                    String binaryCode = firstPart.concat(middlePart.concat(lastPart));
                    System.out.println("BinaryCode " + binaryCode);                    
                }
            }
            
            case "INC" -> {
                if (!source.trim().isEmpty()) {
                    throw new Exception("La instruccion INC no puede tener dos registros");
                }


                if (destiny.trim().isEmpty()) {
                    
                } else {
                    if (!isValidRegister(destiny.trim())) {
                        throw new Exception("Registro no válido para INC");
                    }

                }
            }
                
            case "DEC" -> {
                if (!source.trim().isEmpty()) {
                    throw new Exception("La instruccion DEC no puede tener dos registros");
                }


                if (destiny.trim().isEmpty()) {
                    
                } else {
                    if (!isValidRegister(destiny.trim())) {
                        throw new Exception("Registro no válido para DEC");
                    }

                }           

            }
            
            case "SWAP" -> {
            
                if (destiny.trim().isEmpty()){
                    throw new Exception ("El primer registro no puede ser vacio");
                }
                if (source.trim().isEmpty()){
                    throw new Exception ("El segundo registro no puede ser vacio");
                }
                if (!isValidRegister(destiny.trim())){
                    throw new Exception("El primer registro no es válido para SWAP");    
                }
                if (!isValidRegister(source.trim())){
                    throw new Exception("El segundo registro no es válido para SWAP");    
                } 
                
                if (destiny.trim().equals(source)){
                    throw new Exception("No puede intercambiar los mismos registros");
                    }
                
            }

            case "INT" -> {
                if(destiny.trim().isEmpty()){
                    throw new Exception("La operación INT no puede venir vacia");
                }
                
                if(!source.trim().isEmpty()){
                    System.out.println("aqui" + source);
                    throw new Exception("Las unicas operaciones validas son INT20H INT10H e INT09H");
                }
                if(!destiny.trim().equalsIgnoreCase("20H") 
                        && !destiny.trim().equalsIgnoreCase("10H")
                        && !destiny.trim().equalsIgnoreCase("09H")
                        && !destiny.trim().equalsIgnoreCase("21H")) {
                    System.out.println("aqui" + destiny);
                    throw new Exception("Las unicas operaciones validas son INT20H INT10H, INT09H e INT21H");
                }
            }
            
            case "JMP" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación JMP no puede venir vacia");
                }
                if(!source.trim().isEmpty()) {
                    throw new Exception("La operacion solo tiene 2 argumentos");
                }
                
                if(destiny.trim().matches("\\d+")) {
                    int jumpNumber = Integer.parseInt(destiny.trim());
                    System.out.println("JMP a la instrucción número: " + jumpNumber);
                } else {
                    throw new Exception("El destino de JMP debe ser un número entero");
                }
            }
            
            case "CMP" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación CMP neceista dos registros");
                }
                if(source.trim().isEmpty()) {
                    throw new Exception("La operación CMP neceista dos registros");
                }
                
                if(!isValidRegister(destiny) || !isValidRegister(source)) {
                    throw new Exception("Solo se permiten registros validos");
                }
      
            }
            
            case "JE" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación JE no puede venir vacia");
                }
                if(!source.trim().isEmpty()) {
                    throw new Exception("La operacion solo tiene 2 argumentos");
                }
                
                if(destiny.trim().matches("\\d+")) {
                    int jumpNumber = Integer.parseInt(destiny.trim());
                    System.out.println("JE a la instrucción número: " + jumpNumber);
                } else {
                    throw new Exception("El destino de JE debe ser un número entero");
                }                    
            }
            
            case "JNE" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación JNE no puede venir vacia");
                }
                if(!source.trim().isEmpty()) {
                    throw new Exception("La operacion solo tiene 2 argumentos");
                }
                
                if(destiny.trim().matches("\\d+")) {
                    int jumpNumber = Integer.parseInt(destiny.trim());
                    System.out.println("JNE a la instrucción número: " + jumpNumber);
                } else {
                    throw new Exception("El destino de JNE debe ser un número entero");
                }                    
            }   
            
            case "PARAM" -> {
                String allParams = destiny.trim();
                if (!source.trim().isEmpty()) {
                    allParams += " " + source.trim();
                }

                if (allParams.trim().isEmpty()) {
                    throw new Exception("PARAM necesita al menos un valor");
                }

                String[] params = allParams.split("\\s+");

                if (params.length > 3) {
                    throw new Exception("PARAM acepta máximo 3 valores");
                }

                for (String param : params) {
                    try {
                        Integer.parseInt(param.trim());
                    } catch (NumberFormatException e) {
                        throw new Exception("Error: el valor '" + param + "' en PARAM no es numérico");
                    }
                }
            }
            
            case "PUSH" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación PUSH no puede venir vacia");
                }
                if(!source.trim().isEmpty()) {
                    throw new Exception("La operacion solo tiene 2 argumentos");
                }    
                
                if(!isValidRegister(destiny)) {
                    throw new Exception("Debe utilizar un registro AX");
                }
            } 
            
            case "POP" -> {
                if(destiny.trim().isEmpty()) {
                    throw new Exception("La operación POP no puede venir vacia");
                }
                if(!source.trim().isEmpty()) {
                    throw new Exception("La operacion solo tiene 2 argumentos");
                }    
                
                if(!isValidRegister(destiny)) {
                    throw new Exception("Debe utilizar un registro válido");
                }
            }             
            
            default -> { 
            throw new Exception("Error: No contiene ninguna operación válida");
            }
        
        }   
    }
    
    public void ReadASM(String asmFile) throws Exception {
        File f = new File(asmFile);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;

                String[] test = line.trim().replace(",","").split("\\s+");

                String destiny = "";
                String source = "";

                if(test.length > 1) {
                    destiny = test[1];
                }

                if(test.length > 2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < test.length; i++) {
                        if (i > 2) sb.append(" ");
                        sb.append(test[i]);
                    }
                    source = sb.toString();
                }

                EntryData(test[0], destiny, source);
            }

            System.out.println("Fin del archivo - madre mia willy");
        } catch (Exception e) {
            throw e;
        }
    }
    

    
    
    public void generateBinary(String action, String destiny, String source, CPU cpu , BCP bcp, EntryFile entry) throws Exception {
        switch(action) {
            
            case "MOV" -> {
                // AQUI ASIGNO LOS CASOS DE AH EN MOV
                if (source.toUpperCase().endsWith("H")) {
                    int num = Integer.parseInt(source.replace("H",""), 16);
                    cpu.setRegisterValue(destiny, num);
                    String ins = action +  " " + destiny + " " + source;
                    cpu.setIR(ins); // Este revisarlo
                }
                // caso DOS REGISTROS 
                else if(isValidRegister(destiny) && isValidRegister(source)){
                    String ins = action + " " + destiny + " " + source;
                    cpu.setIR(ins);
                    cpu.setRegisterValue(destiny,cpu.getRegister(source));
                    bcp.addTime(1);
                }else {
                    try {
                        // numero decimal
                        int num = Integer.parseInt(source.trim());
                        cpu.setRegisterValue(destiny, num);
                        bcp.addTime(1);
                        String ins = action + " " + destiny + " " + source;
                        cpu.setIR(ins);
                    } catch (NumberFormatException e) {
                        // Si no es número, lo tratamos como texto
                        if (destiny.equalsIgnoreCase("DX") ) {
                            String ins = action + " " + destiny + " " + source;
                            cpu.setIR(ins);
                            cpu.setDX(source.trim());
                        } else if (destiny.equalsIgnoreCase("AL")){
                            String ins = action + " " + destiny + " " + source;
                            cpu.setIR(ins);
                            cpu.setAL(source.trim());
                        } else {
                            System.out.println("Error: el valor no es un número válido para " + destiny);
                        }
                    }
                }
            }


                
           case "LOAD" -> {
               String instructions = action + " " + destiny + " " + source;
               cpu.setIR(instructions);               
               cpu.setLoadAC(cpu.getRegisterValue(destiny));               
               bcp.addTime(2);
               
            }
            
            
            case "ADD" -> {
               String instructions = action + " " + destiny + " " + source;
               cpu.setIR(instructions);                 
                cpu.setLoadAC(cpu.getRegister("AC") + cpu.getRegisterValue(destiny));
                bcp.addTime(3);
                     
            }
            case "SUB" -> {
               String instructions = action + " " + destiny + " " + source;
               cpu.setIR(instructions); 
                cpu.setLoadAC(cpu.getRegister("AC") - cpu.getRegisterValue(destiny)); 
                bcp.addTime(3);
                                
            }
            case "STORE" -> {
               String instructions = action + " " + destiny + " " + source;
               cpu.setIR(instructions); 
                cpu.setStoreValue(destiny);
                bcp.addTime(2);
                                
            }
            
            case "INC" -> {
                if (!source.trim().isEmpty()) {
                    throw new Exception("La instrucción INC no puede tener dos registros");
                }

                if (destiny.trim().isEmpty()) {
                    //  INC solo 
                    cpu.addOneToRegister("AC");
                    cpu.setIR("INC AC");
                    bcp.addTime(1);
                } else {
                    //  INC registro
                    if (!isValidRegister(destiny.trim())) {
                        throw new Exception("Registro no válido para INC");
                    }
                    cpu.addOneToRegister(destiny.trim());
                    bcp.addTime(1);
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions); 
                }
            }
            
            
            case "DEC" -> {
                if (!source.trim().isEmpty()) {
                    throw new Exception("La instrucción dec no puede tener dos registros");
                }

                if (destiny.trim().isEmpty()) {
                    //  DEC solo 
                    cpu.lessOneToRegister("AC");
                    cpu.setIR("DEC AC");
                    bcp.addTime(1);
                } else {
                    //  DEC registro
                    if (!isValidRegister(destiny.trim())) {
                        throw new Exception("Registro no válido para INC");
                    }
                    cpu.lessOneToRegister(destiny.trim());
                    bcp.addTime(1);
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions); 
                }
            }
            
            case "SWAP" -> {
                cpu.swapRegister(destiny, source);
                bcp.addTime(1);
               String instructions = action + " " + destiny + " " + source;
               cpu.setIR(instructions);             
            
                
            }
            
            case "INT" -> {
                if(destiny.trim().equalsIgnoreCase("20H")) {
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions);                      
                    dispatcher.saveContext();
                    window.moveProcessToVirtual(bcp);
                    System.out.println("Ola si llegue aqui");
                    stopProgram = 1;
                    cpu.setIR("INT 20H");
                    bcp.addTime(2);                   
                    window.showInScreen("Se finalizó el programa");
                    //dispatcher.loadContext(bcp); tecnicamente no me haría falta porque termina
                    return;
                }
                
                if(destiny.trim().equalsIgnoreCase("10H")) {
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions);                         
                    dispatcher.saveContext(); 
                    window.moveProcessToVirtual(bcp);
                    System.out.println("Debo imprimir en pantalla");
                    window.showInScreen("Registro DX: " + (cpu.getDX()));
                    
                    bcp.addTime(2);
                    window.restoreProcessFromVirtual(bcp);
                    dispatcher.loadContext(bcp);
                }
                if (destiny.trim().equalsIgnoreCase("09H")) {
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions);                     
                    System.out.println("Va a escribir un número");
                    dispatcher.saveContext();
                    window.moveProcessToVirtual(bcp);
                    if (window.isExecutingAllProcesses()) {
                        Thread.sleep(500);
                        // Este es si es ejecución  corrida
                        int value = window.showInScreenNumber(">> Ingrese un valor por favor");
                        cpu.setDX(Integer.toString(value));
                        System.out.println("Valor recibido: " + value);
                        dispatcher.loadContext(bcp);
                        bcp.addTime(value);
                    }  else {
                        // Caso paso a paso
                        waitingForInput = true;
                        SwingUtilities.invokeLater(() -> {
                            window.showInScreen(">> Ingrese un valor por favor en el área de texto");
                            window.enableUserInput();
                        });

                        new Thread(() -> {
                            try {
                                while (!window.isUserFinishTyping()) {
                                    Thread.sleep(100);
                                }
                                bcp.addTime(window.getLastInputValue());
                                System.out.println("Tiempo agregado por INT 09H paso a paso: " + window.getLastInputValue());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    window.restoreProcessFromVirtual(bcp);
                    dispatcher.loadContext(bcp);
                    cpu.setIR("INT 09H");
                    bcp.addTime(2);
                    interruptPaused = true;
                    return;
                }

                
                if(destiny.trim().equalsIgnoreCase("21H")) {
                    String instructions = action + " " + destiny + " " + source;
                    cpu.setIR(instructions);                     
                    dispatcher.saveContext();
                    window.moveProcessToVirtual(bcp);
                    System.out.println("El usuario va a realizar una acción");
                    String fileName = cpu.getDX(); 
                    int ah = cpu.getRegister("AH");  
                    System.out.println("Soy registro ah: " + ah);
                    bcp.addTime(5);
                    
                    switch(ah) {
                        case 0x3C -> {
                            
                            System.out.println("Entre a crear el archivo");
                            // Crear archivo
                            File newFile = new File(fileName);
                            if (!newFile.exists()) {
                                newFile.createNewFile();
                                window.showInScreen("Archivo creado: " + fileName);
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                
                            } else {
                                window.showInScreen("El archivo ya existe: " + fileName);
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                            }
                        }
                          
                        case 0x3D -> {
                            System.out.println("Voy a abrir un archivo");
                            String fileName2 = cpu.getDX();
                            int ah2 = cpu.getRegister("AH"); 
                            File file = new File(fileName2);
                            if (file.exists()) {
                                bcp.openFile(fileName2);
                                window.showInScreen("Archivo abierto: " + fileName2);
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                System.out.println(bcp.getOpenFiles());
                            } else {
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                window.showInScreen("Error: el archivo no existe: " + fileName2);
                            }
                        }
                        
                        case 0x4D -> {
                            System.out.println("Voy a leer un archivo");
                            String fileName3 = cpu.getDX();

                            if (bcp.isFileOpen(fileName3)) {
                                File file = new File(fileName3);
                                if (file.exists()) {
                                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                                        StringBuilder content = new StringBuilder();
                                        String line;
                                        while ((line = br.readLine()) != null) {
                                            content.append(line).append("\n");
                                        }

                                        if (content.length() > 0) {
                                            cpu.setAL(content.toString().trim()); // todo lo que tengo en AL
                                            window.showInScreen("Contenido de " + fileName3 + ":\n" + content.toString().trim());
                                            System.out.println("AL ahora contiene: " + cpu.getAL());
                                            window.restoreProcessFromVirtual(bcp);
                                            dispatcher.loadContext(bcp);
                                        } else {
                                            window.restoreProcessFromVirtual(bcp);
                                            dispatcher.loadContext(bcp);
                                            window.showInScreen("El archivo está vacío");
                                        }
                                    } catch (IOException e) {
                                        window.restoreProcessFromVirtual(bcp);
                                        dispatcher.loadContext(bcp);
                                        window.showInScreen("Error al leer el archivo: " + fileName3);
                                    }
                                } else {
                                    window.restoreProcessFromVirtual(bcp);
                                    dispatcher.loadContext(bcp);
                                    window.showInScreen("Error: el archivo no existe físicamente");
                                }
                            } else {
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                window.showInScreen("Error: el archivo no fue abierto con anterioridad");
                            }
                        }
                        
                        case 0x40 -> {
                            System.out.println("Voy a escribir un archivo");
                            String fileName3 = cpu.getDX();

                            if (bcp.isFileOpen(fileName3)) {
                                File file = new File(fileName3);
                                if (file.exists()) {
                                    try (FileWriter fw = new FileWriter(file, true)) { 
                                        String content = cpu.getAL(); // lo que tiene AL se escribe
                                        fw.write(content + "\n");
                                        window.showInScreen("Escrito en archivo: " + content);
                                        window.restoreProcessFromVirtual(bcp);
                                        dispatcher.loadContext(bcp);
                                        System.out.println("Se escribió en " + fileName3 + ": " + content);
                                    } catch (IOException e) {
                                        window.showInScreen("Error al escribir el archivo: " + fileName3);
                                    }
                                } else {
                                    window.restoreProcessFromVirtual(bcp);
                                    dispatcher.loadContext(bcp);
                                    window.showInScreen("Error: el archivo no existe físicamente");
                                }
                            } else {
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                window.showInScreen("Error: el archivo no fue abierto con anterioridad");
                            }
                        }
                        
                        case 0x41 -> {
                            System.out.println("Voy a eliminar un archivo");
                            String fileName3 = cpu.getDX();
                            if (bcp.isFileOpen(fileName3)) {
                                File file = new File(fileName3);
                                if (file.exists()) {
                                    bcp.closeFile(fileName3); // Primero quitado del BCP
                                    file.delete();            // Lo quitamos de los archivos del proyecto
                                    window.showInScreen("Archivo eliminado: " + fileName3);
                                    window.restoreProcessFromVirtual(bcp);
                                    dispatcher.loadContext(bcp);
                                    System.out.println("Archivo eliminado: " + fileName3);
                                } else {
                                    window.restoreProcessFromVirtual(bcp);
                                    dispatcher.loadContext(bcp);
                                    window.showInScreen("Error: el archivo no existe físicamente");
                                }
                            } else {
                                window.restoreProcessFromVirtual(bcp);
                                dispatcher.loadContext(bcp);
                                window.showInScreen("Error: el archivo no fue abierto con anterioridad");
                            }
                        }                       
                    }
                }
            }
            
            case "AH" -> {
                
            }
             
            case "JMP" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                System.out.println("Va a suceder un salto, aymimadre");
                System.out.println(bcp.getMemoryStart() + "hOLA1");
                System.out.println(bcp.getMemoryEnd() + "hola2");
                System.out.println(entry.getMemoryStart() + "hola3");
                System.out.println(entry.getMemoryEnd() + "hola4");
                int displacement = Integer.parseInt(destiny.trim()); // puede ser +2 o -2
                int absoluteTarget = cpu.getPC() + displacement; // posición actual + desplazamiento

                System.out.println("PC " + cpu.getPC());
                cpu.addPC(displacement);
                System.out.println("PC actual: " + cpu.getPC());
                System.out.println("Desplazamiento: " + displacement);
                System.out.println("Saltando a: " + absoluteTarget);

                if(absoluteTarget > entry.getMemoryEnd() || absoluteTarget < entry.getMemoryStart()) {
                    throw new Exception("Error: JMP fuera de los límites del proceso");
                }
                window.showInScreen("Instrucción JMP: " + displacement);
                bcp.addTime(2);
                jumpTarget = absoluteTarget;
  
                 
            }
            
            case "CMP" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                if( cpu.getRegister(destiny) == cpu.getRegister(source)) {
                    cpu.setFlag(0);
                    System.out.println(cpu.getFlag() + " flag");
                    bcp.addTime(2);
                    window.showInScreen("Resultado CMP: " + 0);
                }
                else if (cpu.getRegister(destiny) > cpu.getRegister(source)){
                    cpu.setFlag(1);
                    System.out.println(cpu.getFlag() + " flag");
                    bcp.addTime(2);
                    window.showInScreen("Resultado CMP: " + 1);
                }
                else {
                    cpu.setFlag(-1);
                    System.out.println(cpu.getFlag() + " flag");
                    bcp.addTime(2);
                    window.showInScreen("Resultado CMP: " + -1);
                }
                    interruptPaused = true;
                
            }
            
            case "JE" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                if (cpu.getFlag() == 0){
                    int displacement = Integer.parseInt(destiny.trim()); 
                    int absoluteTarget = cpu.getPC() + displacement; 
                    if(absoluteTarget > entry.getMemoryEnd() || absoluteTarget < entry.getMemoryStart()) {
                        throw new Exception("Error: el salto esta fuera de los límites del proceso");
                    }
                    window.showInScreen("Va a suceder un JE de: " + displacement + "saltos");
                    jumpTarget = absoluteTarget;
                    System.out.println("PC " + cpu.getPC());
                    cpu.addPC(displacement);
                    System.out.println("PC " + cpu.getPC());
                    bcp.addTime(2);
                }
                else {
                    window.showInScreen("El salto no se puede realizar porque la comparacion no es igual");
                }
            
            }
            
            
            case "JNE" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                if (cpu.getFlag() != 0){
                    int displacement = Integer.parseInt(destiny.trim()); 
                    int absoluteTarget = cpu.getPC() + displacement; 
                    if(absoluteTarget > entry.getMemoryEnd() || absoluteTarget < entry.getMemoryStart()) {
                        throw new Exception("Error: el salto esta fuera de los límites del proceso");
                    }
                    window.showInScreen("Va a suceder un JNE de: " + displacement + "saltos");
                    System.out.println("PC " + cpu.getPC());
                    cpu.addPC(displacement);
                    System.out.println("PC " + cpu.getPC());
                    jumpTarget = absoluteTarget;
                    bcp.addTime(2);
                }
                else {
                    window.showInScreen("El salto no se puede realizar porque la comparacion es igual");
                }            
            }
            
            case "PARAM" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                System.out.println("Stack pointer antes: " + bcp.getStackPointer());
                String allParams = destiny.trim();
                if (!source.trim().isEmpty()) {
                    allParams += " " + source.trim();
                }

                String[] params = allParams.split("\\s+");
                System.out.println("Parametros" + params);

                if (params.length > 3) {
                    throw new Exception("PARAM acepta máximo 3 valores");
                }

                for (String param : params) {
                    int value = Integer.parseInt(param.trim());
                    System.out.println("valores" + value);
                    bcp.push(value);
                }
                window.showInScreen("Parametros guardados en pila");
                bcp.addTime(3);
                bcp.printStack();
            }
            
            case "PUSH" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                try {
                    System.out.println("Registro AX a pila " + cpu.getRegisterValue("AX"));
                    int value = cpu.getRegisterValue("AX");
                    bcp.push(value); 
                    bcp.printStack();
                    cpu.setIR("PUSH AX");
                    bcp.addTime(1);
                    window.showInScreen("Push de: " + value + " guardado en pila");
                } catch (Exception e) {
                    window.showInScreen("!!: " + e.getMessage());
                }
            }

            case "POP" -> {
                String instructions = action + " " + destiny + " " + source;
                cpu.setIR(instructions);                  
                try {
                    int value = bcp.pop();
                    cpu.setRegisterValue(destiny, value);
                    cpu.setIR("POP " + destiny);
                    bcp.addTime(1);
                    window.showInScreen("POP de: " + value + " guardado en pila");
                    System.out.println("POP a pila guardado en " + destiny + " " + cpu.getRegisterValue(destiny));
                } catch (Exception e) {
                    window.showInScreen("!!: " + e.getMessage());
                }
            }

            

            
            
            default -> { 
                System.out.println("Action recibida: '" + action + "'");
            throw new Exception("La ejecución del proceso ha finalizado.");
            }
        }
    }
    
    
    
    
    public ArrayList<String[]> loadASM(String asmFile, Memory memory, int startCounter) throws Exception {
        ArrayList<String[]> instructions = new ArrayList<>();
        File f = new File(asmFile);
        int counter = startCounter;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;

            //String[] test = line.trim().replace(",","").split(" ");
            String[] test = line.trim().replace(",","").split("\\s+");
            String destiny = "";
            String source = "";
            

            if(test.length > 1) {
                System.out.println(test[1]);
                destiny = test[1];
            }

            if(test.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < test.length; i++) {
                    if (i > 2) sb.append(" ");
                    sb.append(test[i]);
                }
                source = sb.toString();
            }

            if (test[0].equalsIgnoreCase("INT") && test[1].trim().equalsIgnoreCase("20H")) {
                String binary = generateBinaryOnly(test[0], destiny, source);
                instructions.add(new String[]{line, binary});
                memory.writeMemory(counter, line);
                System.out.println("Guardando en posición " + counter + ": " + line);
                counter++;
                continue;
            }


            
            String binary = generateBinaryOnly(test[0], destiny, source); // POSIBLE CAMBIO AUQI 
            instructions.add(new String[]{line, binary});



            memory.writeMemory(counter, line);
            System.out.println("Guardando en posición " + counter + ": " + line);
            counter++;
           
            }
            


            System.out.println("Fin del archivo - madre mia willy");
        } catch (Exception e) {
            throw e;
        }
        
        return instructions;
    } 




    public void loadInstructions(String instruction, Memory memory, CPU cpu, BCP bcp, EntryFile entryFile) throws Exception {
        System.out.println("Ola entre aqui");
        //String[] test = instruction.trim().replace(",","").split(" ");
        String[] test = instruction.trim().replace(",","").split("\\s+");
        System.out.println("Primera " + test[0]);
        String destiny = "";
        String source = "";

        if(test.length > 1) {
            System.out.println(test[1]);
            destiny = test[1];
        }

        if(test.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < test.length; i++) {
                if (i > 2) sb.append(" ");
                sb.append(test[i]);
            }
            source = sb.toString();
        }
        

        generateBinary(test[0], destiny, source, cpu, bcp, entryFile);
        System.out.println("PC del CPU antes de sync: " + cpu.getPC()); // ← getPC()
        System.out.println("PC del BCP antes de sync: " + bcp.getPC());        

        bcp.setAX(cpu.getRegister("AX"));
        bcp.setBX(cpu.getRegister("BX"));
        bcp.setCX(cpu.getRegister("CX"));
        bcp.setDX(cpu.getDX());
        bcp.setAC(cpu.getRegister("AC"));
        bcp.setIR(cpu.getIR());
        bcp.setPC(cpu.getPC());
        System.out.println("Guardando en posición " + cpu.getPC() + ": " + instruction); // ← getPC()
        System.out.println("Cantidad de tiempo transcurrido: " + bcp.getElapsedTime());
        System.out.println("PC del BCP después de sync: " + bcp.getPC());

        
        
    }


    public ArrayList<String[]> parseOnly(String asmFile) throws Exception {
        ArrayList<String[]> instructions = new ArrayList<>();
        File f = new File(asmFile);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] test = line.trim().replace(",", "").split("\\s+");
                String destiny = "";
                String source = "";

                if (test.length > 1) destiny = test[1];
                if (test.length > 2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < test.length; i++) {
                        if (i > 2) sb.append(" ");
                        sb.append(test[i]);
                    }
                    source = sb.toString();
                }

                String binary = generateBinaryOnly(test[0], destiny, source);
                instructions.add(new String[]{line, binary});
            }
        }
        return instructions;
    }
 

    private String getRegisterCode(String register) {
        if(register.equals("AX")) {
            return "0001 ";
        }
        if(register.equals("BX")) {
            return "0010 ";
        }
        if(register.equals("CX")) {
            return "0011 ";
        }
        if(register.equals("DX")) {
            return "0100 ";
        }        
        else {
            return "El tipo de registro enviado no fue el correcto";
            // Quitar después aunque sino es ninguno de estos
            // Podría enviarme un código de error tipo 1111
        }
    };
    
    private String fromIntegerToBinary(String number){
        String itsNegative = "0";
        String zeros = "";
        String finalNumber = "";
        if(number.contains("-")){
            itsNegative = "1";
        }
        number = number.replace("-", ""); // Le quitamos el menos al numero negativo
        int integerNumber = Integer.parseInt(number);
        String binaryNumber = Integer.toBinaryString(integerNumber);
        int longNumber = 8 - binaryNumber.length(); // Obtengo el largo del número binario
        // Arranc0 en 1 porque ya tengo el bit del signo
        for(int i = 1; i < longNumber; i++){
            zeros += ("0");
        }
        finalNumber = itsNegative.concat(zeros.concat(binaryNumber));
        System.out.println(finalNumber + "Soy el numero binario");
        return finalNumber;
    }
    
    public void searchInstruction(CPU cpu, Memory memory, BCP bcp, EntryFile entry) throws Exception {
        String keyword = memory.getPosition(bcp.getPC());
        String[] test = keyword.trim().replace(",","").split(" ");
        System.out.println("PC actual: " + bcp.getPC());
        System.out.println("keyword: '" + keyword + "'");        
        System.out.println(test[0]);
        

        String destiny = "";
        String source = "";

        if(test.length > 1) {
            System.out.println(test[1]);
            destiny = test[1];
        }

        if(test.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < test.length; i++) {
                if (i > 2) sb.append(" ");
                sb.append(test[i]);
            }
            source = sb.toString();
        }

        generateBinary(test[0], destiny, source, cpu, bcp,  entry);
        if (shouldJump()) {
            bcp.setPC(entry.getMemoryStart() + jumpTarget - 1);
            cpu.setPC(bcp.getPC());
            resetJump();
            return;
        }

        if (shouldStop()) {
            bcp.setIR(cpu.getIR());
            return;
        }

      //  bcp.setPC();
      //  cpu.setPC(bcp.getPC());
      //  bcp.setAX(cpu.getRegister("AX"));
      //  bcp.setBX(cpu.getRegister("BX"));
      //  bcp.setCX(cpu.getRegister("CX"));
      //  bcp.setDX(cpu.getDX());
      //  bcp.setAC(cpu.getRegister("AC"));
      //  bcp.setIR(cpu.getIR());
        
      
    }  
    
    private String generateBinaryOnly(String action, String destiny, String source) throws Exception {
        switch(action) {
            case "MOV" -> {
                String firstPart = "0011 ";
                String middlePart = getRegisterCode(destiny);

                if (source.toUpperCase().endsWith("H")) {
                    // Hexadecimal
                    int num = Integer.parseInt(source.replaceAll("(?i)H$", ""), 16);
                    return firstPart.concat(middlePart).concat(fromIntegerToBinary(String.valueOf(num)));
                } else {
                    try {
                        // Decimal
                        Integer.parseInt(source.trim());
                        return firstPart.concat(middlePart).concat(fromIntegerToBinary(source));
                    } catch (NumberFormatException e) {
                        // texto
                        if (destiny.equalsIgnoreCase("DX")) {
                            return firstPart.concat(middlePart).concat("00000000"); // binario placeholder
                        }
                        else if(destiny.equalsIgnoreCase("AL")) {
                            return firstPart.concat(middlePart).concat("00000000");
                        
                        }else {
                            throw new Exception("Error: valor no numérico para " + destiny);
                        }
                    }
                }
            }
            case "LOAD" -> {
                return "0001 ".concat(getRegisterCode(destiny)).concat(" 00000000");
            }
            case "ADD" -> {
                return "0101 ".concat(getRegisterCode(destiny)).concat(" 00000000");
            }
            case "SUB" -> {
                return "0100 ".concat(getRegisterCode(destiny)).concat(" 00000000");
            }
            case "STORE" -> {
                return "0010 ".concat(getRegisterCode(destiny)).concat(" 00000000");
            }
            case "INC" -> {
                return "0000";
            }
           
            case "DEC" -> {
                return "00000";
            }
            
            case "SWAP" -> {
                return "00000";
            }
            
            case "INT" -> {
                return "111111";
            }
            
            case "AH" -> {
                return "3423423";
            }
            
            case "JMP" -> {
                return "21432423";
            }
            
            case "CMP" -> {
                return "1234";
            }
            
            case "JE" -> {
                return "1122443";
            }
            
            case "JNE" -> {
                return "53422";
            }
            
            case "PARAM" -> {
                return "134234";
            }
            
            case "PUSH" -> {
                return "312321412";
            }
            
            case "POP" -> {
                return "34342334";
            }
            

            default -> throw new Exception("Error: operación no válida");
        }
    }
    
    
    
}    


