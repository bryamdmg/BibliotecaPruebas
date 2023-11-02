package main.java.mx.uv.fei.logic;

public class Libro {
    private int id;
    private String titulo;
    private boolean restringido;
    private String estado;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isRestringido() {
        return restringido;
    }

    public void setRestringido(boolean restringido) {
        this.restringido = restringido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

