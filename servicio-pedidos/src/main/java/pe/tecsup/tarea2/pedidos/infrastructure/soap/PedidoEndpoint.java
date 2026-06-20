package pe.tecsup.tarea2.pedidos.infrastructure.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pe.tecsup.tarea2.pedidos.application.ListarPedidosUseCase;
import pe.tecsup.tarea2.pedidos.application.RegistrarPedidoUseCase;
import pe.tecsup.tarea2.pedidos.contract.ListarPedidosRequest;
import pe.tecsup.tarea2.pedidos.contract.ListarPedidosResponse;
import pe.tecsup.tarea2.pedidos.contract.Pedido;
import pe.tecsup.tarea2.pedidos.contract.RegistrarPedidoRequest;
import pe.tecsup.tarea2.pedidos.contract.RegistrarPedidoResponse;

/**
 * Adaptador de entrada: traduce SOAP <-> casos de uso. No calcula nada,
 * no decide nada, no toca la base de datos. Solo traduce.
 */
@Endpoint
public class PedidoEndpoint {

    private static final String NAMESPACE_URI = "http://shanira.tarea2/pedidos";

    private final RegistrarPedidoUseCase registrarPedidoUseCase;
    private final ListarPedidosUseCase listarPedidosUseCase;

    public PedidoEndpoint(RegistrarPedidoUseCase registrarPedidoUseCase,
                           ListarPedidosUseCase listarPedidosUseCase) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
        this.listarPedidosUseCase = listarPedidosUseCase;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registrarPedidoRequest")
    @ResponsePayload
    public RegistrarPedidoResponse registrarPedido(@RequestPayload RegistrarPedidoRequest request) {
        pe.tecsup.tarea2.pedidos.domain.Pedido pedido = registrarPedidoUseCase.ejecutar(
                request.getClienteId(), request.getProductoId(), request.getCantidad());

        RegistrarPedidoResponse response = new RegistrarPedidoResponse();
        response.setPedido(toContrato(pedido));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarPedidosRequest")
    @ResponsePayload
    public ListarPedidosResponse listarPedidos(@RequestPayload ListarPedidosRequest request) {
        ListarPedidosResponse response = new ListarPedidosResponse();
        listarPedidosUseCase.ejecutar().forEach(p -> response.getPedido().add(toContrato(p)));
        return response;
    }

    private Pedido toContrato(pe.tecsup.tarea2.pedidos.domain.Pedido pedido) {
        Pedido contrato = new Pedido();
        contrato.setId(pedido.getId().intValue());
        contrato.setClienteId(pedido.getClienteId());
        contrato.setClienteNombre(pedido.getClienteNombre());
        contrato.setProductoId(pedido.getProductoId());
        contrato.setProductoNombre(pedido.getProductoNombre());
        contrato.setCantidad(pedido.getCantidad());
        contrato.setPrecioUnitario(pedido.getPrecioUnitario());
        contrato.setTotal(pedido.getTotal());
        contrato.setFechaRegistro(pedido.getFechaRegistro().toString());
        return contrato;
    }
}
