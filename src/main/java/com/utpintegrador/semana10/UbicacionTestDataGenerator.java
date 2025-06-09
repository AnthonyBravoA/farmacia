package com.utpintegrador.semana10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generador de datos de prueba para objetos Ubicacion.
 * Proporciona una forma sencilla de crear ubicaciones ficticias para testing.
 */
public class UbicacionTestDataGenerator {

    private final Random random = new Random();

    // Arrays con datos predefinidos para generar ubicaciones realistas.
    private final String[] distritos = {"Miraflores", "San Isidro", "Barranco", "Santiago de Surco", "La Molina", "San Miguel", "Cercado de Lima"};
    private final String[] nombresCalles = {"Av. Pardo", "Calle Berlin", "Jr. de la Unión", "Av. El Polo", "Calle Los Sauces", "Av. La Marina", "Paseo de la República"};
    private final String[] tiposVia = {"Av.", "Calle", "Jr.", "Psje."};

    /**
     * Genera una lista de ubicaciones de prueba.
     *
     * @param count El número de ubicaciones a generar.
     * @return Una lista de objetos Ubicacion con datos ficticios.
     */
    public List<Ubicacion> generateTestUbicaciones(int count) {
        List<Ubicacion> ubicaciones = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // Genera latitud y longitud aleatorias para una zona aproximada de Lima.
            // Latitud para Lima: -11.5 a -12.5
            // Longitud para Lima: -76.5 a -77.5
            double latitud = -12.0 + (random.nextDouble() * 0.5 - 0.25); // Rango de ~-12.25 a -11.75
            double longitud = -77.0 + (random.nextDouble() * 0.5 - 0.25); // Rango de ~-77.25 a -76.75

            // Selecciona un distrito aleatorio.
            String distrito = distritos[random.nextInt(distritos.length)];

            // Genera una dirección combinando tipo de vía, nombre de calle y número aleatorio.
            String tipoVia = tiposVia[random.nextInt(tiposVia.length)];
            String nombreCalle = nombresCalles[random.nextInt(nombresCalles.length)];
            int numeroCalle = random.nextInt(900) + 100; // Números de calle entre 100 y 999
            String direccion = tipoVia + " " + nombreCalle + " " + numeroCalle;

            // Crea la instancia de Ubicacion.
            Ubicacion ubicacion = new Ubicacion(latitud, longitud, distrito, direccion);

            ubicaciones.add(ubicacion);
        }
        return ubicaciones;
    }
}
