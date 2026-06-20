package pe.tecsup.tarea2.pedidos.application.port;

import pe.tecsup.tarea2.pedidos.domain.ProductoInfo;

public interface ProductoGatewayPort {

    ProductoInfo obtenerProducto(int productoId);
}
