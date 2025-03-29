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

import java.util.Random;

public class Runner {
    private String name;
    private String category;
    private int speed;
    private long finishTime;
    
    public Runner(String name, String category) {
        this.name = name;
        this.category = category;
        this.speed = calculateSpeed(category);
    }
    
    private int calculateSpeed(String category) {
        Random rand = new Random();
        switch(category) {
            case "Avanzado": return rand.nextInt(15) + 1; // 1-15
            case "Intermedio": return rand.nextInt(20) + 5; // 5-25
            default: return rand.nextInt(25) + 10; // 10-35 (Principiantes más lentos)
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
    
    public long getFinishTime() {
        return finishTime;
    }
    
    @Override
    public String toString() {
        return name + " (" + category + ") - Velocidad: " + speed;
    }
}
