/*
Universidad Virtual del Estado de Guanajuato.
Módulo: Tópicos Avanzados de Programación.
Reto 5. Programación Concurrente e Hilos.
Nombre: Julio Arturo Córdova Cú.
Matrícula: 23028189.
Nombre del Asesor: Andrés Espinal Jiménez.
Fecha de Elaboración: 24/03/2025
*/
package carreraatletica;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.sound.sampled.*;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;

public class AthleticRaceInterface extends JFrame {
    // Datos persistentes
    private static final String DATA_FILE = "race_data.dat";
    private List<RaceHistory> historial = new ArrayList<>();
    
    private List<Runner> runners = new ArrayList<>();
    private List<ThreadRunner> threads = new ArrayList<>();
    private List<Runner> finishedRunners = new ArrayList<>();
    private long raceStartTime;
    
    // Componentes multimedia
    private Clip backgroundMusic;
    private boolean soundsEnabled = true;
    
    // Componentes de la interfaz
    private JTextField nameField;
    private JButton registerButton;
    private JTextArea registeredArea;
    private JTextArea resultsArea;
    private JButton startButton, resetButton, exitButton;
    
    // Componentes adicionales
    private JProgressBar[] progressBars = new JProgressBar[5];
    private JComboBox<String> categoryCombo;
    private JTabbedPane tabbedPane;
    private JLabel timeLabel;
    private javax.swing.Timer raceTimer;
    
    public AthleticRaceInterface() {
        // Configuración de JavaTattoo
        try {
            UIManager.setLookAndFeel(new AluminiumLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Carrera Atlética Premium");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Cargar historial
        loadHistory();
        
        initComponents();
        initMenu();
        initSounds();
        
        // Centrar ventana
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Panel de registro
        JPanel registerPanel = new JPanel(new BorderLayout(10, 10));
        registerPanel.add(createRegisterPanel(), BorderLayout.NORTH);
        registerPanel.add(createRunnersPanel(), BorderLayout.CENTER);
        
        // Panel de progreso
        JPanel progressPanel = createProgressPanel();
        
        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        
        tabbedPane.addTab("Carrera", registerPanel);
        tabbedPane.addTab("Progreso", progressPanel);
        tabbedPane.addTab("Estadísticas", statsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(createResultsPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Corredores"));
        
        categoryCombo = new JComboBox<>(new String[]{"Principiante", "Intermedio", "Avanzado"});
        
        nameField = new JTextField(20);
        nameField.setToolTipText("Ingrese nombre del corredor...");
        nameField.addActionListener(e -> registerRunner());
        
        registerButton = new JButton("Registrar");
        registerButton.addActionListener(e -> registerRunner());
        
        panel.add(new JLabel("Nombre:"));
        panel.add(nameField);
        panel.add(new JLabel("Categoría:"));
        panel.add(categoryCombo);
        panel.add(registerButton);
        
        return panel;
    }
    
    private JPanel createRunnersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Corredores Registrados"));
        
        registeredArea = new JTextArea(10, 40);
        registeredArea.setEditable(false);
        registeredArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        panel.add(new JScrollPane(registeredArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Iniciar Carrera");
        resetButton = new JButton("Reiniciar");
        exitButton = new JButton("Terminar");
        
        startButton.addActionListener(e -> startRace());
        resetButton.addActionListener(e -> resetRace());
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Progreso en Tiempo Real"));
        
        for (int i = 0; i < 5; i++) {
            progressBars[i] = new JProgressBar(0, 100);
            progressBars[i].setStringPainted(true);
            progressBars[i].setForeground(new Color(50, 150, 250));
            panel.add(progressBars[i]);
        }
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        historial.forEach(race -> {
            race.getResults().forEach((name, time) -> {
                dataset.addValue(time, name, race.getDate());
            });
        });
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Historial de Carreras", "Corredores", "Tiempo (seg)", dataset);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        resultsArea = new JTextArea(8, 60);
        resultsArea.setEditable(false);
        
        timeLabel = new JLabel("Tiempo: 00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem saveItem = new JMenuItem("Guardar Historial");
        JMenuItem loadItem = new JMenuItem("Cargar Historial");
        
        saveItem.addActionListener(e -> saveHistory());
        loadItem.addActionListener(e -> loadHistory());
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        
        JMenu configMenu = new JMenu("Configuración");
        JCheckBoxMenuItem soundItem = new JCheckBoxMenuItem("Sonidos", soundsEnabled);
        soundItem.addActionListener(e -> soundsEnabled = soundItem.getState());
        
        JMenu themeMenu = new JMenu("Temas");
        ButtonGroup themeGroup = new ButtonGroup();
        
        JRadioButtonMenuItem aluminiumTheme = new JRadioButtonMenuItem("Aluminium", true);
        JRadioButtonMenuItem aeroTheme = new JRadioButtonMenuItem("Aero");
        JRadioButtonMenuItem graphiteTheme = new JRadioButtonMenuItem("Graphite");
        
        aluminiumTheme.addActionListener(e -> changeTheme("Aluminium"));
        aeroTheme.addActionListener(e -> changeTheme("Aero"));
        graphiteTheme.addActionListener(e -> changeTheme("Graphite"));
        
        themeGroup.add(aluminiumTheme);
        themeGroup.add(aeroTheme);
        themeGroup.add(graphiteTheme);
        
        themeMenu.add(aluminiumTheme);
        themeMenu.add(aeroTheme);
        themeMenu.add(graphiteTheme);
        
        configMenu.add(soundItem);
        configMenu.add(themeMenu);
        
        menuBar.add(fileMenu);
        menuBar.add(configMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void initSounds() {
        try {
            // Cerrar clips anteriores si existen
            if (backgroundMusic != null) {
                backgroundMusic.close();
            }
            
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                getClass().getResource("/sounds/background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audio);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Cerrar el stream después de usarlo
            audio.close();
        } catch (Exception e) {
            System.out.println("Error cargando sonidos: " + e.getMessage());
        }
    }
    
    // Agregar método para liberar recursos
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
        // Liberar otros recursos si es necesario
    }
    
    private void changeTheme(String theme) {
        try {
            switch(theme) {
                case "Aero":
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
                    break;
                case "Graphite":
                    UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
                    break;
                default:
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(DATA_FILE))) {
            oos.writeObject(historial);
            JOptionPane.showMessageDialog(this, "Historial guardado correctamente");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error guardando historial: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadHistory() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
                historial = (List<RaceHistory>) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error cargando historial: " + e.getMessage());
            }
        }
    }
    
    // Métodos públicos para ThreadRunner
    public void updateProgressBar(int index, int progress, String runnerName) {
        SwingUtilities.invokeLater(() -> {
            progressBars[index].setValue(progress);
            progressBars[index].setString(runnerName + " (" + progress + "%)");
        });
    }

    public synchronized void addFinishedRunner(Runner runner) {
        finishedRunners.add(runner);
        finishedRunners.sort(Comparator.comparingLong(Runner::getFinishTime));
        showPartialResults();
    
        if (allRunnersFinished()) {
            showFinalResults();
        }
    }
    
    public boolean allRunnersFinished() {
        return finishedRunners.size() == runners.size();
    }
    
    private void showPartialResults() {
        SwingUtilities.invokeLater(() -> {
            resultsArea.setText("Carrera en progreso...\n\n");
            int position = 1;
            for (Runner r : finishedRunners) {
                long timeTaken = (r.getFinishTime() - raceStartTime) / 1000;
                resultsArea.append(position++ + "° - " + r.getName() + 
                               " (" + r.getCategory() + ") - Tiempo: " + 
                               timeTaken + " segundos\n");
            }
        });
    }
    
    // Cambiar de private a public
    public void showFinalResults() {
        SwingUtilities.invokeLater(() -> {
            resultsArea.setText("🏁 Resultados Finales 🏁\n\n");
            int position = 1;
            for (Runner runner : finishedRunners) {
                long timeTaken = (runner.getFinishTime() - raceStartTime) / 1000;
                resultsArea.append(position++ + "° - " + runner.getName() + 
                               " (" + runner.getCategory() + ") - Tiempo: " + 
                            timeTaken + " segundos\n");
            }
            
            if (raceTimer != null) {
                raceTimer.stop();
            }
            saveRaceToHistory();
           tabbedPane.setSelectedIndex(2);
        });
    }
    
    private void registerRunner() {
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        
        if (name.isEmpty()) {
            showError("Debe ingresar un nombre");
            return;
        }
        
        if (runners.size() >= 5) {
            showError("Máximo 5 corredores permitidos");
            return;
        }
        
        Runner runner = new Runner(name, category);
        runners.add(runner);
        registeredArea.append(runners.size() + " - " + runner + "\n");
        nameField.setText("");
        nameField.requestFocus();
        
        if (runners.size() == 5) {
            startButton.setEnabled(true);
        }
        
        playSound("/sounds/add.wav");
    }
    
    private void startRace() {
        if (runners.size() < 5) {
            showError("Debe registrar 5 corredores primero");
            return;
        }
        
        finishedRunners.clear();
        threads.clear();
        raceStartTime = System.currentTimeMillis();
        resultsArea.setText("¡Carrera iniciada!\n\n");
        
        startRaceTimer();
        playSound("/sounds/start.wav");
        
        for (int i = 0; i < runners.size(); i++) {
            Runner runner = runners.get(i);
            ThreadRunner thread = new ThreadRunner(runner, resultsArea, this, i);
            threads.add(thread);
            thread.start();
        }
    }
    
    private void startRaceTimer() {
        if (raceTimer != null && raceTimer.isRunning()) {
            raceTimer.stop();
        }
        
        raceTimer = new javax.swing.Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - raceStartTime) / 1000;
            long minutes = elapsed / 60;
            long seconds = elapsed % 60;
            timeLabel.setText(String.format("Tiempo: %02d:%02d", minutes, seconds));
        });
        
        raceTimer.start();
    }
    
    private void saveRaceToHistory() {
        Map<String, Long> results = new HashMap<>();
        for (Runner runner : finishedRunners) {
            results.put(runner.getName(), (runner.getFinishTime() - raceStartTime) / 1000);
        }
        historial.add(new RaceHistory(new Date(), results));
    }
    
    private void resetRace() {
        runners.clear();
        threads.clear();
        finishedRunners.clear();
        registeredArea.setText("");
        resultsArea.setText("");
        nameField.setText("");
        
        for (JProgressBar bar : progressBars) {
            bar.setValue(0);
            bar.setString("");
        }
        
        if (raceTimer != null) {
            raceTimer.stop();
            timeLabel.setText("Tiempo: 00:00");
        }
        
        startButton.setEnabled(false);
        nameField.requestFocus();
        playSound("/sounds/reset.wav");
    }
    
    private void playSound(String soundFile) {
        if (!soundsEnabled) return;
        
        new Thread(() -> {
            try {
                AudioInputStream audio = AudioSystem.getAudioInputStream(
                    getClass().getResource(soundFile));
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start();
            } catch (Exception e) {
                System.out.println("Error reproduciendo sonido: " + e.getMessage());
            }
        }).start();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        playSound("/sounds/error.wav");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AthleticRaceInterface frame = new AthleticRaceInterface();
            frame.setVisible(true);
        });
    }
}
