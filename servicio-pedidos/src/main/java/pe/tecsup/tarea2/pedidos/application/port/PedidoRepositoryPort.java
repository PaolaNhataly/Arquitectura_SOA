package pe.tecsup.tarea2.pedidos.application.port;

import pe.tecsup.tarea2.pedidos.domain.Pedido;

import java.util.List;

/**
 * Puerto de salida. El caso de uso depende de ESTA interfaz, no de JPA
 * ni de Postgres directamente — eso es lo que invierte la dependencia.
 */
public interface PedidoRepositoryPort {

    Pedido guardar(Pedido pedido);

    List<Pedido> listarTodos();
}
