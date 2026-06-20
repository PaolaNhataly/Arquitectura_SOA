package pe.tecsup.tarea2.pedidos.application;

import org.springframework.stereotype.Service;
import pe.tecsup.tarea2.pedidos.application.port.ClienteGatewayPort;
import pe.tecsup.tarea2.pedidos.application.port.PedidoRepositoryPort;
import pe.tecsup.tarea2.pedidos.application.port.ProductoGatewayPort;
import pe.tecsup.tarea2.pedidos.domain.ClienteInfo;
import pe.tecsup.tarea2.pedidos.domain.Pedido;
import pe.tecsup.tarea2.pedidos.domain.ProductoInfo;

/**
 * Caso de uso: registrar un pedido nuevo.
 * No sabe que existe SOAP, ni JPA, ni Postgres — solo conoce los puertos.
 * Esto es lo que en una sustentación puedes mostrar como "el dominio no
 * depende de ningún framework".
 */
@Service
public class RegistrarPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final ClienteGatewayPort clienteGateway;
    private final ProductoGatewayPort productoGateway;

    public RegistrarPedidoUseCase(PedidoRepositoryPort pedidoRepository,
                                   ClienteGatewayPort clienteGateway,
                                   ProductoGatewayPort productoGateway) {
        this.pedidoRepository = pedidoRepository;
        this.clienteGateway = clienteGateway;
        this.productoGateway = productoGateway;
    }

    public Pedido ejecutar(int clienteId, int productoId, int cantidad) {
        ClienteInfo cliente = clienteGateway.obtenerCliente(clienteId);
        ProductoInfo producto = productoGateway.obtenerProducto(productoId);

        Pedido nuevoPedido = new Pedido(
                clienteId, cliente.getNombre(),
                productoId, producto.getNombre(),
                cantidad, producto.getPrecio());

        return pedidoRepository.guardar(nuevoPedido);
    }
}
