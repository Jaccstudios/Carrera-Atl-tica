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

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ThreadRunner extends Thread {
    private final Runner runner;
    private final JTextArea resultsArea;
    private final AthleticRaceInterface raceInterface;
    private final int runnerIndex;
    private final long startTime;
    
    public ThreadRunner(Runner runner, JTextArea resultsArea, 
                       AthleticRaceInterface raceInterface, int runnerIndex) {
        this.runner = runner;
        this.resultsArea = resultsArea;
        this.raceInterface = raceInterface;
        this.runnerIndex = runnerIndex;
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
public void run() {
        try {
            int totalSleep = runner.getSpeed() * 1000;
            int interval = totalSleep / 100;
        
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(interval);
                final int progress = i;
            
                SwingUtilities.invokeLater(() -> {
                    raceInterface.updateProgressBar(runnerIndex, progress, runner.getName());
                });
            }
        
            long finishTime = System.currentTimeMillis();
            runner.setFinishTime(finishTime);
        
            SwingUtilities.invokeLater(() -> {
                raceInterface.addFinishedRunner(runner);
                // Verificación adicional para seguridad
                if (raceInterface.allRunnersFinished()) {
                raceInterface.showFinalResults();
                }
            });
        
        } catch (InterruptedException e) {
            System.out.println("Carrera interrumpida para " + runner.getName());
            Thread.currentThread().interrupt();
        }
    }
}
