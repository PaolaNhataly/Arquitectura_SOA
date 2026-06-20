package pe.tecsup.tarea2.pedidos.infrastructure.persistence;

import org.springframework.stereotype.Component;
import pe.tecsup.tarea2.pedidos.application.port.PedidoRepositoryPort;
import pe.tecsup.tarea2.pedidos.domain.Pedido;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de salida: implementa PedidoRepositoryPort usando Postgres.
 * Si mañana cambias de base de datos, solo se toca esta clase — ni el
 * dominio ni los casos de uso se enteran.
 */
@Component
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoJpaEntity entity = toEntity(pedido);
        PedidoJpaEntity guardado = jpaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public List<Pedido> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private PedidoJpaEntity toEntity(Pedido pedido) {
        PedidoJpaEntity entity = new PedidoJpaEntity();
        entity.setId(pedido.getId());
        entity.setClienteId(pedido.getClienteId());
        entity.setClienteNombre(pedido.getClienteNombre());
        entity.setProductoId(pedido.getProductoId());
        entity.setProductoNombre(pedido.getProductoNombre());
        entity.setCantidad(pedido.getCantidad());
        entity.setPrecioUnitario(pedido.getPrecioUnitario());
        entity.setTotal(pedido.getTotal());
        entity.setFechaRegistro(pedido.getFechaRegistro());
        return entity;
    }

    private Pedido toDomain(PedidoJpaEntity entity) {
        return new Pedido(
                entity.getId(),
                entity.getClienteId(),
                entity.getClienteNombre(),
                entity.getProductoId(),
                entity.getProductoNombre(),
                entity.getCantidad(),
                entity.getPrecioUnitario(),
                entity.getTotal(),
                entity.getFechaRegistro());
    }
}
