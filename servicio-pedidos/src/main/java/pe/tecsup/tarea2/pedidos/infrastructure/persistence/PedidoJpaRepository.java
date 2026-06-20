package pe.tecsup.tarea2.pedidos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Detalle técnico de Spring Data — el dominio nunca ve esta interfaz. */
public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Long> {
}
