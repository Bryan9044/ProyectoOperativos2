/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import Controlador.Parser;
import Modelo.Memory;
import Modelo.BCP;
import Modelo.CPU;
import Modelo.SecondaryMemory; 
import Modelo.EntryFile;
import Controlador.Scheduler;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import Controlador.ProcessStats;
import java.util.List;
import Modelo.VirtualMemory;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.util.Map;
import Controlador.Dispatcher;


/**
 *
 * @author bryan
 */
public class pagPrincipal extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(pagPrincipal.class.getName());
    Parser parser;
    Memory memory;
    BCP bcp;
    CPU cpu;
    SecondaryMemory secondaryMemory;
    VirtualMemory virtualMemory;
    Dispatcher dispatcher;
    Scheduler scheduler = new Scheduler();
    EntryFile entryFile;
    int instructionNumber = 1;
    int actualInstruction = 20;
    int actualRow = -1; 
    int indexFile = 1; 
    int secondaryIndex = 0;
    int lastIndex = 0;
    boolean activeFile = false;
    List<ProcessStats> statsList = new ArrayList<>();
    int startIndex = 0;
    int finalIndex = 19;
    
    int startIndexNormal = 100;
    
    boolean userFinishTyping = false;
    CountDownLatch latch;
    
    // Estas solo son de paso a paso 
    int currentProcessIndex = 0;  
    int currentLastIndex = 0;     
    EntryFile currentEntry = null; 
    boolean processRunning = false; 
    boolean executingAllProcesses = false;
    // Estas solo son de paso a paso 
    int bcpKernelIndex = 0;
    java.util.Map<String, Integer> bcpKernelPositions = new java.util.HashMap<>();
  

    List<int[]> freeKernelSlots = new ArrayList<>();   // huecos vacios en el kernel bueno libres
    List<int[]> freeMemorySlots = new ArrayList<>();    // huecos vacios en memoria
    
    boolean waitingForInput = false;
        int readyAdmissionCount = 0;
    
    static final int KERNEL_MAX = 100;
    static final int KERNEL_SLOT_SIZE = 20;   
    int lastInputValue = 0;
    
    /**
     * Creates new form pagPrincipal
     */
                
    DefaultListModel<String> procesosModel = new DefaultListModel<>();
    
    public pagPrincipal() {
        initComponents();
        loadConfig();
        dispatcher = new Dispatcher(new ArrayList<>(), null, null);
        parser = new Parser(this, dispatcher);         
        ProcessesList.setModel(procesosModel);
            readyAdmissionCount = 0;

        
        
        
    jTextArea2.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode != KeyEvent.VK_ENTER && 
                keyCode != KeyEvent.VK_BACK_SPACE &&
                (keyCode < KeyEvent.VK_0 || keyCode > KeyEvent.VK_9) &&
                (keyCode < KeyEvent.VK_NUMPAD0 || keyCode > KeyEvent.VK_NUMPAD9)) {
                e.consume(); 
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String text = jTextArea2.getText();
                String[] lines = text.split("\n");
                String last = lines[lines.length - 1].trim();
                try {
                    int valor = Integer.parseInt(last);
                    if (valor < 0 || valor > 255) {
                        jTextArea2.append("\nError: valor fuera de rango, ingrese entre 0 y 255");
                    } else {
                        cpu.setRegisterValue("DX", valor);
                        lastInputValue = valor;
                        jTextArea2.append("\nValor guardado en DX: " + valor + "\n");
                        jTextArea2.setEditable(false);
                        userFinishTyping = true;
                        latch.countDown(); 
                    }
                } catch (NumberFormatException ex) {
                    jTextArea2.append("\nError: entrada no válida");
                }
            }
        }
    });

    
   
    
    }
    
    private void highlightCurrentInstruction(int memoryPosition) {
     DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
     for (int i = 0; i < model.getRowCount(); i++) {
         Object pos = model.getValueAt(i, 0);
         if (pos != null && Integer.parseInt(pos.toString()) == memoryPosition) {
             jTable2.setRowSelectionInterval(i, i); 
             jTable2.scrollRectToVisible(jTable2.getCellRect(i, 0, true)); 
             break;
         }
     }
    } 
    public boolean isUserFinishTyping() {
        return userFinishTyping;
    }

    public void resetUserFinishTyping() {
        userFinishTyping = false;
    }  
    
    public int getLastInputValue() {
        return lastInputValue;
    }
    

    private void updateDiskTable() {
        DefaultTableModel modelDisco = (DefaultTableModel) jTableDisk.getModel();
        modelDisco.setRowCount(0);

        List<EntryFile> entries = new ArrayList<>(secondaryMemory.getFiles().values());
        entries.sort((a, b) -> Integer.compare(a.getStartPosition(), b.getStartPosition()));

        for (EntryFile entry : entries) {
            String fileName = entry.getName();
            int start = entry.getStartPosition();
            int end = entry.getLastInt();

            modelDisco.addRow(new Object[]{fileName, "Índice: " + entry.getIndex()});

            for (int pos = start; pos <= end; pos++) {
                String instruction = secondaryMemory.getInstructions().get(pos);
                if (instruction != null) {
                    modelDisco.addRow(new Object[]{instruction, pos});
                }
            }
        }
    }
    
    

    private void updateBCP() {
        PC.setText("PC: " + bcp.getPC());
        AX.setText("AX: " + bcp.getAX());
        BX.setText("BX: " + bcp.getBX());
        CX.setText("CX: " + bcp.getCX());
        DX.setText("DX: " + bcp.getDX());
        AC.setText("AC: " + bcp.getAC());
        IR.setText("IR: " + bcp.getIR());
        ID.setText("ID: " + bcp.getID());
        NextBCP.setText("Next BCP: " + bcp.getNextBCPId());
        STATUS.setText("Status: " + bcp.getStatus());
        MEMORYSTART.setText("Memory Start: " + bcp.getMemoryStart());
        MEMORYEND.setText("Memory end: " + bcp.getMemoryEnd());
        Priority.setText("Priority: " + bcp.getPriority());
        Stack.setText("Stack: " + bcp.getStackPointer());
        OpenFiles.setText("Open files: " + bcp.getOpenFiles());
        ProcessSize.setText("Process Size: " + bcp.getProcessSize());
        CPU.setText("CPU: " + bcp.getCpuID());
        StartTime.setText("Start Time: " + bcp.getStartTime());
        ElapsedTime.setText("Elapsed Time: " + bcp.getElapsedTime());
        FinalTime.setText("Final time: " + bcp.getTotalTime());
    } 
    
    
    private void loadBCPInMemory(BCP bcp, int basePos) throws Exception {
        memory.writeMemoryKernel(basePos, "ID: " + bcp.getID());
        memory.writeMemoryKernel(basePos + 1, "Status: " + bcp.getStatus());
        memory.writeMemoryKernel(basePos + 2, "PC: " + bcp.getPC());
        memory.writeMemoryKernel(basePos + 3, "AX: " + bcp.getAX());
        memory.writeMemoryKernel(basePos + 4, "BX: " + bcp.getBX());
        memory.writeMemoryKernel(basePos + 5, "CX: " + bcp.getCX());
        memory.writeMemoryKernel(basePos + 6, "DX: " + bcp.getDX());
        memory.writeMemoryKernel(basePos + 7, "AC: " + bcp.getAC());
        memory.writeMemoryKernel(basePos + 8, "IR: " + bcp.getIR());
        memory.writeMemoryKernel(basePos + 9, "Priority: " + bcp.getPriority());
        memory.writeMemoryKernel(basePos + 10, "MemoryStart: " + bcp.getMemoryStart());
        memory.writeMemoryKernel(basePos + 11, "MemoryEnd: " + bcp.getMemoryEnd());
        memory.writeMemoryKernel(basePos + 12, "CPU: " + bcp.getCpuID());
        memory.writeMemoryKernel(basePos + 13, "NextBCP: " + bcp.getNextBCPId());
        memory.writeMemoryKernel(basePos + 14, "StartTime: " + bcp.getStartTime());
        memory.writeMemoryKernel(basePos + 15, "ElapsedTime: " + bcp.getElapsedTime());
        memory.writeMemoryKernel(basePos + 16, "FinalTime: " + bcp.getTotalTime());
        memory.writeMemoryKernel(basePos + 17, "Stack: " + bcp.getStackPointer());
        memory.writeMemoryKernel(basePos + 18, "OpenFiles" + bcp.getOpenFiles());
        memory.writeMemoryKernel(basePos + 19, "ProcessSize: " + bcp.getProcessSize());
    }
    
    private boolean isThereSpaceInRAM(int processSize) {
        // Ver si cabe en un hueco
        for (int[] slot : freeMemorySlots) {
            int slotSize = slot[1] - slot[0] + 1;
            if (slotSize >= processSize) {
                return true; 
            }
        }
        // Si no buscar en otro espacio
        return (startIndexNormal + processSize) <= memory.getTotalSize();
    }
    
    public void moveProcessToVirtual(BCP bcp) {
        try {
            virtualMemory.swapOut(bcp);
            bcp.setStatus("Interrumpido - Virtual");
            SwingUtilities.invokeLater(() -> {
                updateBCP();
                updateMemoryTable();
            });
            System.out.println("Proceso movido a virtual por interrupción: " + bcp.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreProcessFromVirtual(BCP bcp) {
        try {
            virtualMemory.removeFromVirtual(bcp.getFileName());
            bcp.setStatus("Ejecutando");
            SwingUtilities.invokeLater(() -> {
                updateBCP();
                updateMemoryTable();
            });
            System.out.println("Proceso restaurado de virtual: " + bcp.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    private int getAvailableKernelSlot() {
        if (!freeKernelSlots.isEmpty()) {
            int recycled = freeKernelSlots.remove(0)[0];
            System.out.println("Reutilizando slot kernel: " + recycled);
            return recycled;
        }
        if (bcpKernelIndex + KERNEL_SLOT_SIZE > KERNEL_MAX) {
            return -1; // Si no hay espacio devuelvo un -1
        }
        int slot = bcpKernelIndex;
        bcpKernelIndex += KERNEL_SLOT_SIZE;
        return slot;
    }
    
    private int getAvailableMemorySlot(int size) {
        for (int i = 0; i < freeMemorySlots.size(); i++) {
            int[] slot = freeMemorySlots.get(i);
            int slotSize = slot[1] - slot[0] + 1;
            if (slotSize >= size) {
                int start = slot[0];
                int end = slot[1];

                if (slotSize == size) {
                    freeMemorySlots.remove(i);
                } else {
                    freeMemorySlots.set(i, new int[]{start + size, end});
                }
                return start; // busca hueco vacio
            }
        }
        int slot = startIndexNormal;
        startIndexNormal += size;
        return slot;
    }

    private void addFreeMemorySlot(int start, int end) {
        if (start < 100 || end < start) {
            return;
        }

        freeMemorySlots.add(new int[]{start, end});

        freeMemorySlots.sort((a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        for (int[] current : freeMemorySlots) {
            if (merged.isEmpty()) {
                merged.add(new int[]{current[0], current[1]});
                continue;
            }

            int[] last = merged.get(merged.size() - 1);
            if (current[0] <= last[1] + 1) {
                last[1] = Math.max(last[1], current[1]);
            } else {
                merged.add(new int[]{current[0], current[1]});
            }
        }

        freeMemorySlots.clear();
        freeMemorySlots.addAll(merged);


        if (startIndexNormal < memory.getTotalSize()) {
            for (int[] slot : freeMemorySlots) {
                if (slot[1] + 1 == startIndexNormal) {
                    slot[1] = memory.getTotalSize() - 1;
                    startIndexNormal = memory.getTotalSize();
                    break;
                }
            }
        }
    }

    private boolean loadProcessInstructionsToRAM(BCP process) throws Exception {
        if (process == null) return false;

        EntryFile entry = secondaryMemory.getFiles().get(process.getFileName());
        if (entry == null) {
            throw new Exception("No se encontro EntryFile para el proceso: " + process.getFileName());
        }

        int processSize = process.getProcessSize();
        if (!isThereSpaceInRAM(processSize)) {
            return false;
        }

        int memStart = getAvailableMemorySlot(processSize);
        for (int i = 0; i < processSize; i++) {
            int diskPos = entry.getStartPosition() + i;
            String instruction = secondaryMemory.getInstructions().get(diskPos);
            if (instruction == null) {
                throw new Exception("Instruccion nula en memoria secundaria para indice: " + diskPos);
            }
            memory.writeMemory(memStart + i, instruction);
        }

        entry.setMemoryStart(memStart);
        entry.setMemoryEnd(memStart + processSize - 1);
        process.setMemoryStart(memStart);
        process.setMemoryEnd(memStart + processSize - 1);
        process.setPC(memStart);
        process.setStatus("Preparado");

        System.out.println("Proceso cargado a RAM desde secundaria: " + process.getFileName() +
            "  " + memStart + "-" + (memStart + processSize - 1));
        return true;
    }

    private boolean ensureKernelSlotForProcess(BCP process) throws Exception {
        if (process == null) {
            return false;
        }

        if (bcpKernelPositions.containsKey(process.getID())) {
            return true;
        }

        int kernelSlot = getAvailableKernelSlot();
        if (kernelSlot == -1) {
            return false;
        }

        loadBCPInMemory(process, kernelSlot);
        bcpKernelPositions.put(process.getID(), kernelSlot);
        return true;
    }

    private boolean promoteWaitingProcessesIfPossible() throws Exception {
        if (!scheduler.canAddToReady() || !scheduler.hasWaitingProcesses()) {
            return false;
        }

        BCP waiting = scheduler.peekWaiting();
        if (waiting == null) {
            return false;
        }

        scheduler.nextProcessWaiting();
        waiting.setStatus("Preparado");
        scheduler.addProcess(waiting);
        ensureKernelSlotForProcess(waiting);
        System.out.println("Promovido de WAITING a READY sin cargar RAM: " + waiting.getFileName());
        return true;
    }
    
    
    private void updateMemoryTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        for (int i = 0; i < memory.getTotalSize(); i++) {
            String value = memory.getPosition(i);
            if (value != null && !value.isEmpty()) {
                model.addRow(new Object[]{i, value});
            }
        }

        if (virtualMemory.hasSwappedProcesses()) {
            model.addRow(new Object[]{"---", "=== EN VIRTUAL  ==="});
            for (Map.Entry<String, BCP> entry : virtualMemory.getSwappedProcesses().entrySet()) {
                model.addRow(new Object[]{"VIRTUAL", entry.getKey() + " - " + entry.getValue().getStatus()});
            }
        }
    } 
    
    
    private void freeProcessMemory(BCP bcp) throws Exception {
        Integer kernelPos = bcpKernelPositions.get(bcp.getID());
        if (kernelPos != null) {
            for (int i = kernelPos; i < kernelPos + 20; i++) {
                memory.writeMemoryKernel(i, "");
            }
            freeKernelSlots.add(new int[]{kernelPos, kernelPos + 19});
            bcpKernelPositions.remove(bcp.getID()); 
        }

        EntryFile entry = secondaryMemory.getFiles().get(bcp.getFileName());
        if (entry != null && entry.getMemoryStart() >= 100) { // Para que no vaya al kernel
            for (int i = entry.getMemoryStart(); i <= entry.getMemoryEnd(); i++) {
                memory.writeMemory(i, "");
            }
            addFreeMemorySlot(entry.getMemoryStart(), entry.getMemoryEnd());
        }
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        PC = new javax.swing.JLabel();
        IR = new javax.swing.JLabel();
        BX = new javax.swing.JLabel();
        AX = new javax.swing.JLabel();
        AC = new javax.swing.JLabel();
        CX = new javax.swing.JLabel();
        DX = new javax.swing.JLabel();
        ID = new javax.swing.JLabel();
        STATUS = new javax.swing.JLabel();
        MEMORYSTART = new javax.swing.JLabel();
        MEMORYEND = new javax.swing.JLabel();
        Priority = new javax.swing.JLabel();
        Stack = new javax.swing.JLabel();
        OpenFiles = new javax.swing.JLabel();
        ProcessSize = new javax.swing.JLabel();
        CPU = new javax.swing.JLabel();
        NextBCP = new javax.swing.JLabel();
        FinalTime = new javax.swing.JLabel();
        ElapsedTime = new javax.swing.JLabel();
        StartTime = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        ProcessesList = new javax.swing.JList<>();
        jButton6 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableDisk = new javax.swing.JTable();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("Cargar archivos");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/carpetaArchivos.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setOpaque(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Proyecto#1 - MiniPC");

        jTable2.setBackground(new java.awt.Color(255, 255, 204));
        jTable2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 204)));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posicion", "Valor memoria"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setShowGrid(true);
        jScrollPane2.setViewportView(jTable2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("BCP ACTUAL"));

        PC.setText("PC:");

        IR.setText("IR:");

        BX.setText("BX:");

        AX.setText("AX:");

        AC.setText("AC:");

        CX.setText("CX:");

        DX.setText("DX:");

        ID.setText("ID:");

        STATUS.setText("Status:");

        MEMORYSTART.setText("MemoryStart:");

        MEMORYEND.setText("MemoryEnd:");

        Priority.setText("Priority");

        Stack.setText("Stack");

        OpenFiles.setText("OpenFiles");

        ProcessSize.setText("ProcessSize");

        CPU.setText("CPU:");

        NextBCP.setText("NextBCP");

        FinalTime.setText("FinalTime:");

        ElapsedTime.setText("ElapsedTime:");

        StartTime.setText("StartTime:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Priority)
                            .addComponent(Stack)
                            .addComponent(OpenFiles)
                            .addComponent(ProcessSize))
                        .addGap(0, 342, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CX, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(STATUS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(IR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(MEMORYSTART, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(MEMORYEND, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(196, 196, 196)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(NextBCP)
                                    .addComponent(CPU))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ElapsedTime)
                                    .addComponent(FinalTime)
                                    .addComponent(StartTime))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CPU)
                    .addComponent(ID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(STATUS)
                    .addComponent(NextBCP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PC, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StartTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AX)
                    .addComponent(ElapsedTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BX)
                    .addComponent(FinalTime))
                .addGap(12, 12, 12)
                .addComponent(CX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(IR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(MEMORYSTART)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(MEMORYEND)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Priority)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Stack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OpenFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProcessSize)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ID.getAccessibleContext().setAccessibleDescription("");

        jButton1.setBackground(new java.awt.Color(255, 255, 153));
        jButton1.setText("Ejecutar");
        jButton1.setToolTipText("");
        jButton1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 0)));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 153));
        jButton3.setForeground(new java.awt.Color(51, 0, 0));
        jButton3.setText("Paso a paso");
        jButton3.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 0)));
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 255, 153));
        jButton5.setForeground(new java.awt.Color(51, 0, 0));
        jButton5.setText("Limpiar");
        jButton5.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 0)));
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1434, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 529, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1422, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        ProcessesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(ProcessesList);

        jButton6.setBackground(new java.awt.Color(255, 255, 153));
        jButton6.setForeground(new java.awt.Color(51, 0, 0));
        jButton6.setText("Estadisticas");
        jButton6.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 0)));
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTableDisk.setBackground(new java.awt.Color(255, 255, 204));
        jTableDisk.setBorder(new javax.swing.border.MatteBorder(null));
        jTableDisk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Valor", "Posición disco"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableDisk.setShowGrid(true);
        jScrollPane6.setViewportView(jTableDisk);
        if (jTableDisk.getColumnModel().getColumnCount() > 0) {
            jTableDisk.getColumnModel().getColumn(0).setResizable(false);
            jTableDisk.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(601, 601, 601)
                                .addComponent(jLabel2))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(352, 352, 352)
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jScrollPane3)
                                            .addComponent(jScrollPane4))))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton3)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(23, 23, 23)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(52, 52, 52)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 588, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jButton3)
                                    .addComponent(jButton5)
                                    .addComponent(jButton6)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel1))
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(99, 99, 99)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    
    public void loadNextWaitingBCP() throws Exception {
        BCP nextWaiting = scheduler.peekWaiting();
        if (nextWaiting == null) return; // Si no hay nada más devolvemos

        int kernelSlot = getAvailableKernelSlot();
        if (kernelSlot == -1) return; // Por si no hay esapcio

        // Cargamos BCP
        loadBCPInMemory(nextWaiting, kernelSlot);
        bcpKernelPositions.put(nextWaiting.getID(), kernelSlot);
    }
    
    public void loadConfig(){
        try {
            // Prueba para cargar archivo json
            System.out.println("Ola llegue a la prueba");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File("MemoryConfiguration.JSON"));
            
            int principalSize = root.get("PrincipalMemorySize").asInt();
            int secondaryMemorySize = root.get("SecondaryMemorySize").asInt();
            int virtualMemorySize = root.get("VirtualMemory").asInt();
            System.out.println("Principal: " + principalSize);
            System.out.println("Secundaria: " + secondaryMemorySize);
            System.out.println("Virtual: " + virtualMemorySize);
            
            memory = new Memory(principalSize,20); // En vez del 20 debo calcular todo el tamaño que va a tener
            // Con los BCP PERO ESO LO CAMBIO DESPUÉS CUANDO YA TENGA TODOS LOS ATRIBUTOS
            secondaryMemory = new SecondaryMemory(secondaryMemorySize);
            virtualMemory = new VirtualMemory(virtualMemorySize);
        } catch (IOException ex) {
            System.getLogger(pagPrincipal.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    
    public void readFileContent(String file, String fileName) throws Exception {
        System.out.println("Hola ya eNtre");
        ArrayList<String[]> instructions = new ArrayList<>();
        File f = new File(file);
        int staticIndex = secondaryIndex;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;

            String[] test = line.trim().replace(",","").split(" ");
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
            
            String inst = test[0] + " " + destiny + " " + source;
            
            System.out.println("Hola soy la inst" + inst);
            secondaryMemory.addInstructions(secondaryIndex, inst);
            lastIndex = secondaryIndex;
            secondaryIndex++;
            System.out.println("sifunciona");
            System.out.println(secondaryMemory.getInstructions());
            

            }
            entryFile = new EntryFile(fileName, indexFile, staticIndex, lastIndex);
            procesosModel.addElement("Archivo: " + entryFile.getName() + " - Índice: " + entryFile.getIndex());
            indexFile++;
        }
    }
    
    
    
    public void executeProcesses() throws Exception {
        System.out.println("Entre hpta");

        while (scheduler.hasProcesses() || scheduler.hasWaitingProcesses()) {
            if (!scheduler.hasProcesses()) {
                System.out.println("READY vacia. Intentando promover desde WAITING. WAITING actual: "
                    + scheduler.getWaitingQueue().size());
                boolean promoted = promoteWaitingProcessesIfPossible();
                if (!promoted && scheduler.hasWaitingProcesses()) {
                    System.out.println("No se pudo promover ningun proceso en espera por falta de RAM disponible.");
                }
            }

            if (!scheduler.hasProcesses()) {
                System.out.println("Ejecucion detenida. READY=" + scheduler.getQueue().size()
                    + " WAITING=" + scheduler.getWaitingQueue().size());
                break;
            }

            BCP process = scheduler.peekProcess();
            if (process == null) break;

            ensureKernelSlotForProcess(process);

            bcp = process; 
            cpu = new CPU(0, 0, 0, "", 0, "", bcp.getPC());
            dispatcher.setCurrentCPU(cpu);        
            dispatcher.setCurrentProcedure(bcp);
            bcp.setStatus("Ejecutando");
            bcp.markStart();
            parser.resetStopFlag();

            String fileName = process.getFileName();
            EntryFile entry = secondaryMemory.getFiles().get(fileName);

            if (entry == null) {
                throw new Exception("No se encontro el archivo del proceso en disco: " + fileName);
            }

            boolean inRam = entry.getMemoryStart() >= 100 && !memory.getPosition(entry.getMemoryStart()).isEmpty();
            if (!inRam) {
                if (!loadProcessInstructionsToRAM(process)) {
                    System.out.println("No hay RAM para el proceso al frente de READY: " + process.getFileName());
                    break;
                }
            }

            scheduler.nextProcess();

            int firstIndex = entry.getStartPosition();
            int lastIndex = entry.getLastInt();

            System.out.println("Ejecutando proceso: " + fileName);
            System.out.println("Primer índice: " + firstIndex);
            System.out.println("Último índice: " + lastIndex);

            for (int i = firstIndex; i <= lastIndex; i++) {
                if (parser.shouldStop()) break;
                String instructions = secondaryMemory.obtainInstructions(i);

                int memPos = entry.getMemoryStart() + (i - firstIndex);
                cpu.setLoadPC(memPos + 1);
                bcp.setPC(memPos + 1);
                System.out.println("BCP PC justo antes del invokeLater: " + bcp.getPC());

                System.out.println("memPos calculado: " + memPos + " i=" + i + " firstIndex=" + firstIndex);

                parser.loadInstructions(instructions, memory, cpu, bcp, entry);

                int kernelPos = bcpKernelPositions.getOrDefault(bcp.getID(), -1);
                if (kernelPos != -1) {
                    loadBCPInMemory(bcp, kernelPos);
                }
                if (parser.shouldJump()) { // El menos es porque el for ya suma uno de más y después se sale de más
                    i = parser.getJumpTarget() - entry.getMemoryStart() + entry.getStartPosition() - 1 ;                   
                    parser.resetJump();
                }
                

                final int pos = memPos;
                SwingUtilities.invokeLater(() -> {
                        System.out.println("BCP PC dentro del invokeLater: " + bcp.getPC());

                    updateMemoryTable();
                    updateBCP();
                    highlightCurrentInstruction(pos);                    
                });
                Thread.sleep(1000);
            }

            bcp.setStatus("Finalizado");
            bcp.markEnd();
            int kernelPos = bcpKernelPositions.getOrDefault(bcp.getID(), -1);
            if (kernelPos != -1) {
                loadBCPInMemory(bcp, kernelPos); 
            }            
            SwingUtilities.invokeLater(() -> updateBCP());
            
            
            finishProcess(bcp);
            updateDiskTable();
            boolean promotedAfterFinish = promoteWaitingProcessesIfPossible();
            System.out.println("Post-finish " + bcp.getFileName() + ": READY=" + scheduler.getQueue().size()
                + " WAITING=" + scheduler.getWaitingQueue().size()
                + " PROMOTED=" + promotedAfterFinish);
            for (ProcessStats s : statsList) {
                System.out.println(s);
            }         
        }
    }
    

    private void sendToWaiting(BCP bcpToWait) throws Exception {
        if (bcpToWait == null) {
            throw new Exception("Error: No se puede enviar proceso nulo a espera");
        }
        
        try {
            dispatcher.saveContext();           
            virtualMemory.swapOut(bcpToWait);
            freeProcessMemory(bcpToWait);
            
            bcpToWait.setStatus("Esperando");
            scheduler.addWaitingProcess(bcpToWait);         
            System.out.println("Proceso " + bcpToWait.getID() + " enviado a ESPERA");
            updateMemoryTable();
            
        } catch (Exception e) {
            System.out.println("Error en sendToWaiting: " + e.getMessage());
            throw e;
        }
    }
    

    private void promoteFromWaiting(BCP waitingBCP) throws Exception {
        if (waitingBCP == null) {
            throw new Exception("Error: No se puede promover proceso nulo");
        }
        
        try {
            if (!virtualMemory.hasProcessInVirtual(waitingBCP.getFileName())) {
                throw new Exception("Proceso " + waitingBCP.getFileName() + " no encontrado en virtual");
            }
            
            BCP restoredBCP = virtualMemory.swapIn(waitingBCP.getFileName());
            
            int originalBasePos = restoredBCP.getMemoryStart();
            
            if (isPositionFree(originalBasePos, restoredBCP.getProcessSize())) {
                loadBCPInMemory(restoredBCP, originalBasePos);
                System.out.println("Restaurado en posición original: " + originalBasePos);
            } else {
                int newBasePos = getAvailableMemorySlot(restoredBCP.getProcessSize());
                if (newBasePos < 100) {
                    newBasePos = startIndexNormal;
                    startIndexNormal += restoredBCP.getProcessSize();
                }
                restoredBCP.setMemoryStart(newBasePos);
                restoredBCP.setMemoryEnd(newBasePos + restoredBCP.getProcessSize() - 1);
                loadBCPInMemory(restoredBCP, newBasePos);
                System.out.println("Restaurado en nueva posición: " + newBasePos);
            }
            
            restoredBCP.setStatus("Listo");
            scheduler.addProcess(restoredBCP);
            
            System.out.println("Proceso " + restoredBCP.getID() + " promovido a LISTO");
            updateMemoryTable();
            
        } catch (Exception e) {
            System.out.println("Error en promoteFromWaiting: " + e.getMessage());
            throw e;
        }
    }
    
    private void finishProcess(BCP finishedBCP) throws Exception {
        if (finishedBCP == null) {
            throw new Exception("Error: No se puede terminar proceso nulo");
        }
        
        try {
            dispatcher.saveContext();
            
            finishedBCP.setStatus("Finalizado");
            finishedBCP.markEnd();
            
            if (virtualMemory.hasProcessInVirtual(finishedBCP.getFileName())) {
                virtualMemory.removeFromVirtual(finishedBCP.getFileName());
            }
            
            freeProcessMemory(finishedBCP);
            
            ProcessStats stats = new ProcessStats(
                finishedBCP.getFileName(),
                finishedBCP.getStartFormatted(),
                finishedBCP.getEndFormatted(),
                finishedBCP.getDurationSeconds()
            );
            statsList.add(stats);
            
            System.out.println("Proceso " + finishedBCP.getID() + " terminado y liberado");
            updateMemoryTable();
            
        } catch (Exception e) {
            System.out.println("Error en finishProcess: " + e.getMessage());
            throw e;
        }
    }
    

    private boolean isPositionFree(int basePos, int size) {
        for (int i = basePos; i < basePos + size; i++) {
            if (!memory.getPosition(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }   
    
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos ASM", "asm");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFile = fileChooser.getSelectedFiles();
            
            java.util.Arrays.sort(selectedFile, (a, b) -> {
                String na = a.getName().replaceAll("[^0-9]", "");
                String nb = b.getName().replaceAll("[^0-9]", "");
                if (na.isEmpty() || nb.isEmpty()) return a.getName().compareTo(b.getName());
                return Integer.parseInt(na) - Integer.parseInt(nb);
            });            
            
            for (File file : selectedFile) {
                System.out.println("Archivo: " + file.getName());
                String filePath = file.getAbsolutePath();
                String fileName = file.getName();

                try {
                    // Validamos el archivo
                    parser.ReadASM(filePath);
                    int confirm = JOptionPane.showConfirmDialog(
                        this, "El archivo es válido, quiere continuar?",
                        "Archivo válido", JOptionPane.YES_NO_OPTION);

                    if (confirm != JOptionPane.YES_OPTION) continue;
                    
                    // Reviso si cabe en disco primero
                    int instruccionesActuales = secondaryMemory.getInstructions().size();
                    ArrayList<String[]> preview = parser.parseOnly(filePath);

                    int maxProcessSizeInRAM = memory.getTotalSize() - 100;
                    if (preview.size() > maxProcessSizeInRAM) {
                        JOptionPane.showMessageDialog(this,
                            "El archivo '" + fileName + "' contiene demasiadas instrucciones (" + preview.size()
                                + "). El maximo permitido para cargar en RAM es " + maxProcessSizeInRAM
                                + ". El proceso sera descartado.",
                            "Proceso demasiado grande", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    if (instruccionesActuales + preview.size() > secondaryMemory.getTotalSize()) {
                        JOptionPane.showMessageDialog(this,
                            "Disco lleno, no se pueden aceptar más procesos",
                            "Sin espacio en disco", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }                    

                    // Se leen instrucciones al disco
                    readFileContent(filePath, fileName);
                    activeFile = true;
                    secondaryMemory.addFile(fileName, entryFile);

                    //  Se parsea el archivo
                    EntryFile entry = secondaryMemory.getFiles().get(fileName);
                    int processSize = preview.size();

                    boolean goesToReady = readyAdmissionCount < 5;
                    if (goesToReady) {
                        readyAdmissionCount++;
                    }
                    boolean loadNowToRAM = isThereSpaceInRAM(processSize);
                    int memStart = 0;

                    if (goesToReady && loadNowToRAM) {
                        memStart = getAvailableMemorySlot(processSize);
                        parser.loadASM(filePath, memory, memStart);
                        entry.setMemoryStart(memStart);
                        entry.setMemoryEnd(memStart + processSize - 1);
                        System.out.println("Proceso cargado en RAM: " + fileName);
                        System.out.println("=== MEMORIA RAM ===");
                        System.out.println("Start: " + memStart + " End: " + (memStart + processSize - 1));
                        System.out.println("==================");
                    } else {
                        entry.setMemoryStart(0);
                        entry.setMemoryEnd(0);
                        if (!goesToReady) {
                            JOptionPane.showMessageDialog(this,
                                "La cola READY ya tiene 5 procesos. El archivo '" + fileName + "' queda en espera.",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "El archivo '" + fileName + "' entra en READY pero aun no cabe en RAM; se cargara cuando le toque ejecutarse.",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                    // Creamos BCP
                    String instructionID = "Ins" + instructionNumber;
                    instructionNumber++;
                    cpu = new CPU(0, 0, 0, "", 0, "", memStart);
                    dispatcher = new Dispatcher(new ArrayList<>(), null, cpu);

                    String priority = goesToReady ? "alta" : "baja";

                    bcp = new BCP(instructionID, goesToReady ? "Preparado" : "Esperando",
                                  memStart, memStart + processSize - 1,
                                  processSize, priority, 1, 0);
                    bcp.setPC(memStart);
                    bcp.setFileName(fileName);

                    if (goesToReady) {
                        scheduler.addProcess(bcp);
                    } else {
                        scheduler.addWaitingProcess(bcp);
                    }
                    // Se carga el BCP apra el kernel
                    int kernelSlot = getAvailableKernelSlot();
                    if (kernelSlot == -1) {
                        JOptionPane.showMessageDialog(this,
                            "No hay espacio en kernel para mas procesos. El proceso se mantendra en cola y en disco.",
                            "Kernel lleno", JOptionPane.WARNING_MESSAGE);
                    } else {
                        loadBCPInMemory(bcp, kernelSlot);
                        bcpKernelPositions.put(bcp.getID(), kernelSlot);
                    }

                    updateMemoryTable();
                    updateDiskTable();
                    System.out.println("Instrucciones: " + processSize);
                    System.out.println("Cola READY: " + scheduler.getQueue().size()
                        + " | Cola WAITING: " + scheduler.getWaitingQueue().size());

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Por favor ingrese un número válido",
                        "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo cargar el archivo '" + fileName + "': " + ex.getMessage(),
                        "Error al cargar archivo", JOptionPane.ERROR_MESSAGE);
                    System.getLogger(pagPrincipal.class.getName())
                        .log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        }        
    }//GEN-LAST:event_jButton2ActionPerformed

    
    
        public void showInScreen(String message) {
            SwingUtilities.invokeLater(() -> jTextArea2.append(message + "\n"));
        }    
        
        
        public int showInScreenNumber(String message){
            SwingUtilities.invokeLater(() -> {
                jTextArea2.append(message + "\n");
                jTextArea2.setEditable(true);
            });
            userFinishTyping = false;
            latch = new CountDownLatch(1); 
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return cpu.getRegisterValue("DX");
        }
        
        public void enableUserInput() {
            jTextArea2.setEditable(true);
            resetUserFinishTyping();
            latch = new CountDownLatch(1);
        }

        public boolean isExecutingAllProcesses() {
           return executingAllProcesses;
       }  
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if (!activeFile) {
        JOptionPane.showMessageDialog(this,
            "Debe cargar un archivo primero",
            "Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (executingAllProcesses) {
        JOptionPane.showMessageDialog(this,
            "Ya hay una ejecución en progreso",
            "Info",
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    executingAllProcesses = true;
    jButton1.setEnabled(false);

    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {
            executeProcesses();
            return null;
        }

        @Override
        protected void done() {
            executingAllProcesses = false;
            jButton1.setEnabled(true);
            try {
                get();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(pagPrincipal.this,
                    "Error al ejecutar procesos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    worker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if(activeFile != true){
            JOptionPane.showMessageDialog(this, 
                "Debe cargar un archivo primero", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
            
        }         
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea limpiar? Se perderá el progreso actual.",
            "Confirmar limpieza",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );  
        
        if (confirm == JOptionPane.YES_OPTION){
            ((DefaultTableModel) jTable2.getModel()).setRowCount(0);
            ((DefaultTableModel) jTableDisk.getModel()).setRowCount(0);
            procesosModel.clear(); 
            activeFile = false;
            
            loadConfig();
            bcp = null;
            cpu = null;

            scheduler.getQueue().clear();
            scheduler.getWaitingQueue().clear();
            readyAdmissionCount = 0;
            
            for (String fileName : new ArrayList<>(virtualMemory.getSwappedProcesses().keySet())) {
                virtualMemory.removeFromVirtual(fileName);
            }
            

            instructionNumber = 1;
            actualInstruction = 20;
            actualRow = -1;
            indexFile = 1;
            secondaryIndex = 0;
            lastIndex = 0;
            startIndexNormal = 100;
            startIndex = 0;
            finalIndex = 19;
            bcpKernelIndex = 0;

            freeKernelSlots.clear();
            freeMemorySlots.clear();
            bcpKernelPositions.clear();

            virtualMemory.clear();

            statsList.clear();

            processRunning = false;
            executingAllProcesses = false;
            currentProcessIndex = 0;
            currentLastIndex = 0;
            currentEntry = null;
            readyAdmissionCount = 0; 
            
            
            ID.setText("ID:");
            STATUS.setText("Status:");
            PC.setText("PC:");
            AX.setText("AX:");
            BX.setText("BX:");
            CX.setText("CX:");
            DX.setText("DX:");
            AC.setText("AC:");
            IR.setText("IR:");
            MEMORYSTART.setText("MemoryStart:");
            MEMORYEND.setText("MemoryEnd:");
            Priority.setText("Priority:");
            Stack.setText("Stack:");
            OpenFiles.setText("Open Files:");
            ProcessSize.setText("Process Size:");
            procesosModel.clear(); 
            CPU.setText("CPU:");      
            FinalTime.setText("Final time:");
            ElapsedTime.setText("Elapsed time:");
            StartTime.setText("Start time:");              
            jTextArea2.setText(""); 
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!activeFile) { 
            JOptionPane.showMessageDialog(this, "Debe cargar un archivo primero", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!processRunning) {
                if (!scheduler.hasProcesses()) {
                    promoteWaitingProcessesIfPossible();
                    if (!scheduler.hasProcesses()) {
                        JOptionPane.showMessageDialog(this, "No hay procesos pendientes", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                bcp = scheduler.peekProcess();
                if (bcp == null) {
                    JOptionPane.showMessageDialog(this, "No hay procesos pendientes", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                ensureKernelSlotForProcess(bcp);
                currentEntry = secondaryMemory.getFiles().get(bcp.getFileName());
                boolean inRam = currentEntry != null
                    && currentEntry.getMemoryStart() >= 100
                    && !memory.getPosition(currentEntry.getMemoryStart()).isEmpty();
                if (!inRam) {
                    if (!loadProcessInstructionsToRAM(bcp)) {
                        JOptionPane.showMessageDialog(this, "No hay RAM disponible para el proceso que viene de ready.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                scheduler.nextProcess();
                cpu = new CPU(0, 0, 0, "", 0, "", bcp.getPC());
                dispatcher.setCurrentCPU(cpu);        
                dispatcher.setCurrentProcedure(bcp);                
                bcp.setStatus("Ejecutando");
                bcp.markStart();
                parser.resetStopFlag();
                currentProcessIndex = currentEntry.getStartPosition();
                currentLastIndex = currentEntry.getLastInt();
                processRunning = true;
            }

            if (parser.shouldStop() || currentProcessIndex > currentLastIndex) {
                int kernelPos = bcpKernelPositions.getOrDefault(bcp.getID(), -1);
                if (kernelPos != -1) {
                    loadBCPInMemory(bcp, kernelPos); 
                }                 
                processRunning = false;
                
                finishProcess(bcp);
                promoteWaitingProcessesIfPossible();
                updateBCP();
                updateMemoryTable();
                updateDiskTable();
                highlightCurrentInstruction(cpu.getRegister("PC") - 1);
                
                for (ProcessStats s : statsList) {
                    System.out.println(s);
                }                
                
                JOptionPane.showMessageDialog(this, "Proceso finalizado", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (parser.isWaitingForInput()) {
                if (!isUserFinishTyping()) {
                    return; // esperamos hasta que escribamos el número 
                } else {
                    parser.setWaitingForInput(false);
                    resetUserFinishTyping();
                }
            }            

            String instruction = secondaryMemory.obtainInstructions(currentProcessIndex);
            int memPos = currentEntry.getMemoryStart() + (currentProcessIndex - currentEntry.getStartPosition());
            cpu.setLoadPC(memPos + 1);
            bcp.setPC(memPos + 1);
            parser.loadInstructions(instruction, memory, cpu, bcp, currentEntry);
            int kernelPos = bcpKernelPositions.getOrDefault(bcp.getID(), -1);
            if (kernelPos != -1) {
                loadBCPInMemory(bcp, kernelPos);
            }
            if (parser.shouldJump()) {

                currentProcessIndex = parser.getJumpTarget() - currentEntry.getMemoryStart() + currentEntry.getStartPosition();
                parser.resetJump();
            } else {
                currentProcessIndex++;
            }

            updateBCP();
            updateMemoryTable();
            highlightCurrentInstruction(cpu.getRegister("PC") - 1);
            
            

        } catch (Exception e) {
            bcp.setStatus("Finalizado");
            bcp.markEnd();
            processRunning = false;

            int kernelPos = bcpKernelPositions.getOrDefault(bcp.getID(), -1);
            if (kernelPos != -1) {
                try {
                    loadBCPInMemory(bcp, kernelPos);
                } catch (Exception ex) {
                    ex.printStackTrace(); 
                }
            }

            try {
                finishProcess(bcp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error al terminar proceso: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                try {
                    promoteWaitingProcessesIfPossible();
                } catch (Exception e2) {
                    e2.printStackTrace(); 
                    JOptionPane.showMessageDialog(this,
                        "Error al promover procesos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }

                updateBCP();
                updateMemoryTable();
                highlightCurrentInstruction(cpu.getRegister("PC") - 1);

                JOptionPane.showMessageDialog(this, e.getMessage(), "Finalizaste", JOptionPane.INFORMATION_MESSAGE);
        }

    
    }//GEN-LAST:event_jButton3ActionPerformed

    
    public void showStatistics() {
        // Columnas de arriba puedo cambiarlas o dejarlas más bonitas
        String[] columns = {"Proceso", "Inicio", "Fin", "Duración (s)"};

        // Datos de mi clase
        Object[][] data = new Object[statsList.size()][4];
        for (int i = 0; i < statsList.size(); i++) {
            ProcessStats s = statsList.get(i);
            data[i][0] = s.getProcessName();
            data[i][1] = s.getStartTime();
            data[i][2] = s.getEndTime();
            data[i][3] = s.getDurationSeconds();
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        JDialog dialog = new JDialog(this, "Estadísticas de procesos", true);
        dialog.add(scrollPane);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }    


    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    showStatistics();        
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new pagPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AC;
    private javax.swing.JLabel AX;
    private javax.swing.JLabel BX;
    private javax.swing.JLabel CPU;
    private javax.swing.JLabel CX;
    private javax.swing.JLabel DX;
    private javax.swing.JLabel ElapsedTime;
    private javax.swing.JLabel FinalTime;
    private javax.swing.JLabel ID;
    private javax.swing.JLabel IR;
    private javax.swing.JLabel MEMORYEND;
    private javax.swing.JLabel MEMORYSTART;
    private javax.swing.JLabel NextBCP;
    private javax.swing.JLabel OpenFiles;
    private javax.swing.JLabel PC;
    private javax.swing.JLabel Priority;
    private javax.swing.JLabel ProcessSize;
    private javax.swing.JList<String> ProcessesList;
    private javax.swing.JLabel STATUS;
    private javax.swing.JLabel Stack;
    private javax.swing.JLabel StartTime;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTableDisk;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
