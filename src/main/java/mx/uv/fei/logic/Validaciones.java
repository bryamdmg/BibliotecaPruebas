package main.java.mx.uv.fei.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Validaciones {
    public static boolean esFechaValida(String fechaStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            Date fecha = dateFormat.parse(fechaStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean esFechaDevolucionValida(Date fechaDevolucion, boolean esRestringido) {
        if (esRestringido) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date fechaLimite = calendar.getTime();
            return fechaDevolucion.equals(fechaLimite);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            Date fechaLimite = calendar.getTime();
            return fechaDevolucion.after(fechaLimite);
        }
    }

    public static boolean esLibroPerdido(Date fechaDevolucion) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaDevolucion);
        calendar.add(Calendar.MONTH, 1);
        Date fechaPerdida = calendar.getTime();
        Date fechaActual = new Date();
        return fechaActual.after(fechaPerdida);
    }
}

