/*
Universidad Virtual del Estado de Guanajuato.
Módulo: Tópicos Avanzados de Programación.
Reto 5. Programación Concurrente e Hilos.
Nombre: .
Matrícula: .
Nombre del Asesor: .
Fecha de Elaboración: / / .
*/
package carreraatletica;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class RaceHistory implements Serializable {
    private Date date;
    private Map<String, Long> results;
    
    public RaceHistory(Date date, Map<String, Long> results) {
        this.date = date;
        this.results = results;
    }
    
    public Date getDate() {
        return date;
    }
    
    public Map<String, Long> getResults() {
        return results;
    }
}
