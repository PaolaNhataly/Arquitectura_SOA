package pe.tecsup.tarea2.pedidos.application.port;

import pe.tecsup.tarea2.pedidos.domain.ClienteInfo;

/**
 * Puerto de salida. Hoy lo implementa un adaptador SOAP; si servicio-clientes
 * cambiara a REST mañana, este puerto no se toca, solo su implementación.
 */
public interface ClienteGatewayPort {

    ClienteInfo obtenerCliente(int clienteId);
}
