package pe.tecsup.tarea2.esb.dto;

/**
 * Representa el cuerpo JSON que espera POST /esb/pedidos.
 * Se usa únicamente para que Camel genere el esquema en el OpenAPI
 * (y así Swagger UI muestre los campos editables) — el parseo real
 * del JSON lo sigue haciendo ParseRegistrarPedidoRequestProcessor.
 */
public class RegistrarPedidoRequestBody {

    private int clienteId;
    private int productoId;
    private int cantidad;

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
