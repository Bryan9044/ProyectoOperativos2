
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Modelo;

/**
 *
 * @author bryan
 */
public class CPU {
    int AX;
    int BX;
    int CX;
    String DX;
    int AC;
    String IR;
    int PC;
    int AH;
    String AL;
    int flag;
    

    public CPU(int AX, int BX, int CX, String DX, int AC, String IR, int PC) {
        this.AX = AX;
        this.BX = BX;
        this.CX = CX;
        this.DX = DX;
        this.AC = AC;
        this.IR = IR;
        this.PC = PC;
    }
    
    public void setRegisterValue(String register, int number) {
        if (register.contains("AX")) {
            AX = number;
        }

        if (register.contains("BX")) {
            BX = number;
        }

        if (register.contains("CX")) {
            CX = number;
        } 
        
        if (register.contains("DX")) {
            DX = Integer.toString(number);
        }      
        
        if (register.contains("AC")) {
            AC = number;
        }  
    
        if (register.contains("PC")) {
            PC = number;
        }  
        
        if (register.contains("AH")) {
            AH = number;
        }
    }
    public void setSubValue(String register, int number){
        if (register.contains("AX")) {
            AX -= number;
        }

        if (register.contains("BX")) {
            BX -= number;
        }

        if (register.contains("CX")) {
            CX -= number;
        } 
        
        if (register.contains("DX")) {
            try {
                int currentValue = Integer.parseInt(DX);
                currentValue -= number;
                DX = Integer.toString(currentValue);
            } catch (NumberFormatException e) {
                System.out.println("DX no contiene un número válido para restar");
            }
        }      
        
        if (register.contains("AC")) {
            AC -= number;
        }  
    
        if (register.contains("PC")) {
            PC -= number;
        }  
    }
    
    
    public void setStoreValue(String register){
        if (register.contains("AX")) {
            AX = AC;
        }

        if (register.contains("BX")) {
            BX = AC;
        }

        if (register.contains("CX")) {
            CX = AC;
        } 
        
        if (register.contains("DX")) {
            DX = Integer.toString(AC);
        }      
  
    }
    
    public void setIR(String memory) {
        IR = "";
        IR = memory;
    }
    
    public String getIR() {
        return IR;
    }
    
    public int getPC() {
        return PC;
    }
    
    public String getDX() {
        return DX;
    }
    
    public void setPC(int value) {
        PC = value;
    }
    
    public void setDX(String value) {
        DX = value;
    }
    
    public int getRegisterValue(String register) {
        if(register.equals("AX")) return AX;
        if(register.equals("BX")) return BX;
        if(register.equals("CX")) return CX;
        if(register.equals("DX")) {
            return Integer.parseInt(DX);
        } 
        return 0;
    }   
    
    public int getRegister(String register) {
        if(register.contains("AC")) {
            return AC;
        }
        if(register.contains("AX")) {
            return AX;
        }
        if(register.contains("BX")) {
            return BX;
        }
        if(register.contains("CX")) {
            return CX;
        }
        if(register.contains("DX")) {
            return Integer.parseInt(DX);
        }
        if(register.contains("PC")) {
            return PC;
        }
        if(register.contains("AH")){
            return AH;
        }
        if(register.contains("AL")){
            return Integer.parseInt(AL);
        }
        return 0;
    }
    
    
    public void setLoadAC(int value) {
    AC = value;  
    }

    public void setLoadAX(int value) {
    AX = value;  
    }

    public void setLoadBX(int value) {
    BX = value; 
    }

    public void setLoadCX(int value) {
    CX = value;  
    }

    public void setLoadDX(int value) {
    DX = Integer.toString(value);  
    }

    public void setLoadPC(int value) {
    PC = value;  
    }

    public int getAC() {
        return AC;
    }
    
 
    public void addOneToRegister(String register) throws Exception {
        if (register.equalsIgnoreCase("AC")) {
            AC += 1;
        } else if (register.equalsIgnoreCase("AX")) {
            AX += 1;
        } else if (register.equalsIgnoreCase("BX")) {
            BX += 1;
        } else if (register.equalsIgnoreCase("CX")) {
            CX += 1;
        } else if (register.equalsIgnoreCase("DX")) {
            try {
            if (DX == ""){
                DX = "0";
            }
            int currentNumber = Integer.parseInt(DX);
            currentNumber += 1;
            DX = Integer.toString(currentNumber);
            } catch (NumberFormatException e){
                System.out.println("DX no contiene un número válido para sumar");
            }                    
        } else {
            throw new Exception("No puede poner un registro que no sea: AC, AX, BX, CX, DX");
        }
    }
    
    public void lessOneToRegister(String register) throws Exception {
        if (register.equalsIgnoreCase("AC")) {
            AC -= 1;
        } else if (register.equalsIgnoreCase("AX")) {
            AX -= 1;
        } else if (register.equalsIgnoreCase("BX")) {
            BX -= 1;
        } else if (register.equalsIgnoreCase("CX")) {
            CX -= 1;
        } else if (register.equalsIgnoreCase("DX")) {
            try {
            if (DX == ""){
                DX = "0";
            }
            int currentNumber = Integer.parseInt(DX);
            currentNumber -= 1;
            DX = Integer.toString(currentNumber);
            } catch (NumberFormatException e){
                System.out.println("DX no contiene un número válido para restar");
            }
        } else {
            throw new Exception("No puede poner un registro que no sea: AC, AX, BX, CX, DX");
        }
    }
    
    
    public void addPC(int value) {
        PC += value;
    }
    
    public void swapRegister(String reg1, String reg2) throws Exception {
        int val1 = getRegister(reg1);
        int val2 = getRegister(reg2);

        // Aqui intercambio ya 
        setRegisterValue(reg1, val2);
        setRegisterValue(reg2, val1);
    }
    
    
    public void setAL(String value) {
        AL = value;
    }

    public String getAL() {
        return AL;
    }

    public int getALAsInt() {
        try {
            return Integer.parseInt(AL);
        } catch (NumberFormatException e) {
            System.out.println("AL no contiene un número válido");
            return 0;
        }
    } 
    
    public void setAH(int value) {
        AH = value;
    }

    public int getAH() {
        return AH;
    }

    public int getFlag() {
        return flag;
    }
    
    public int setFlag(int number) {
        return flag = number;   
    }
    
    
    
    
    @Override
    public String toString() {
        return "CPU {" +
               "AX=" + AX +
               ", BX=" + BX +
               ", CX=" + CX +
               ", DX=" + DX +
               ", AC=" + AC +
               ", AH=" + AH +
               ", AL=" + AL +
               ", IR='" + IR + '\'' +
               ", PC=" + PC +
               '}';
    }

   
}
