package pe.tecsup.tarea2.pedidos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Objeto de dominio puro: ni @Entity, ni @Component, ni nada de Spring.
 * No le importa si al final esto se guarda en Postgres, Mongo o un archivo.
 */
public class Pedido {

    private final Long id;
    private final int clienteId;
    private final String clienteNombre;
    private final int productoId;
    private final String productoNombre;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal total;
    private final LocalDateTime fechaRegistro;

    /** Constructor para un pedido nuevo: aquí vive la regla de negocio del total. */
    public Pedido(int clienteId, String clienteNombre, int productoId, String productoNombre,
                  int cantidad, BigDecimal precioUnitario) {
        this(null, clienteId, clienteNombre, productoId, productoNombre, cantidad,
                precioUnitario, precioUnitario.multiply(BigDecimal.valueOf(cantidad)), LocalDateTime.now());
    }

    /** Constructor completo: para reconstruir un pedido que ya existía en la base de datos. */
    public Pedido(Long id, int clienteId, String clienteNombre, int productoId, String productoNombre,
                  int cantidad, BigDecimal precioUnitario, BigDecimal total, LocalDateTime fechaRegistro) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
