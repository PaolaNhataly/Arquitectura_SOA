package pe.tecsup.tarea2.pedidos.application;

import org.springframework.stereotype.Service;
import pe.tecsup.tarea2.pedidos.application.port.PedidoRepositoryPort;
import pe.tecsup.tarea2.pedidos.domain.Pedido;

import java.util.List;

@Service
public class ListarPedidosUseCase {

    private final PedidoRepositoryPort pedidoRepository;

    public ListarPedidosUseCase(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> ejecutar() {
        return pedidoRepository.listarTodos();
    }
}
