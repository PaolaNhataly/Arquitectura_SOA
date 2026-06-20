package pe.tecsup.tarea2.pedidos.domain;

import java.math.BigDecimal;

public class ProductoInfo {

    private final int id;
    private final String nombre;
    private final BigDecimal precio;

    public ProductoInfo(int id, String nombre, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
