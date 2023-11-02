package main.java.mx.uv.fei.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Validaciones {
    public static boolean esFechaValida(String fechaStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // No permitir fechas inválidas
            Date fecha = dateFormat.parse(fechaStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean esFechaDevolucionValida(Date fechaDevolucion, boolean esRestringido) {
        if (esRestringido) {
            // En caso de libro restringido, debe ser exactamente en la fecha
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Fecha de devolución en el siguiente día
            Date fechaLimite = calendar.getTime();
            return fechaDevolucion.equals(fechaLimite);
        } else {
            // En caso de libro no restringido, se permiten extensiones de 3 días
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 3); // Fecha de extensión
            Date fechaLimite = calendar.getTime();
            return fechaDevolucion.after(fechaLimite);
        }
    }

    public static boolean esLibroPerdido(Date fechaDevolucion) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaDevolucion);
        calendar.add(Calendar.MONTH, 1); // Fecha de pérdida en el siguiente mes
        Date fechaPerdida = calendar.getTime();
        Date fechaActual = new Date();
        return fechaActual.after(fechaPerdida);
    }
}

