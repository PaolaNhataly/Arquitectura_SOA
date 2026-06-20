package pe.tecsup.tarea2.esb.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import pe.tecsup.tarea2.esb.dto.RegistrarPedidoRequestBody;
import pe.tecsup.tarea2.esb.processor.ClienteResponseProcessor;
import pe.tecsup.tarea2.esb.processor.ListarPedidosResponseProcessor;
import pe.tecsup.tarea2.esb.processor.ParseRegistrarPedidoRequestProcessor;
import pe.tecsup.tarea2.esb.processor.PedidoResponseProcessor;
import pe.tecsup.tarea2.esb.processor.ProductoResponseProcessor;

import java.util.List;

/**
 * Esta clase ES el bus: define qué rutas existen y a qué servicio
 * SOAP apunta cada una. El cliente final solo conoce las rutas de aquí
 * (/esb/clientes, /esb/clientes/{id}, /esb/productos, /esb/productos/{id},
 * /esb/pedidos); nunca llama directo a los servicios de atrás. De cara
 * al cliente todo es JSON; puertas adentro el ESB sigue hablando SOAP.
 */
@Component
public class EsbRoutes extends RouteBuilder {

    // Rango de ids usado por los endpoints de "listar varios" (demo)
    private static final List<Integer> IDS_DEMO = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    private static final String SOAP_CLIENTE_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:cli=\"http://shanira.tarea2/clientes\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>"
            + "<cli:obtenerClienteRequest><cli:id>${header.id}</cli:id></cli:obtenerClienteRequest>"
            + "</soapenv:Body></soapenv:Envelope>";

    private static final String SOAP_PRODUCTO_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:prod=\"http://shanira.tarea2/productos\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>"
            + "<prod:obtenerProductoRequest><prod:id>${header.id}</prod:id></prod:obtenerProductoRequest>"
            + "</soapenv:Body></soapenv:Envelope>";

    private static final String SOAP_LISTAR_PEDIDOS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:ped=\"http://shanira.tarea2/pedidos\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body><ped:listarPedidosRequest/></soapenv:Body></soapenv:Envelope>";

    private static final String SOAP_REGISTRAR_PEDIDO_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:ped=\"http://shanira.tarea2/pedidos\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>"
            + "<ped:registrarPedidoRequest>"
            + "<ped:clienteId>${header.clienteId}</ped:clienteId>"
            + "<ped:productoId>${header.productoId}</ped:productoId>"
            + "<ped:cantidad>${header.cantidad}</ped:cantidad>"
            + "</ped:registrarPedidoRequest>"
            + "</soapenv:Body></soapenv:Envelope>";

    @Override
    public void configure() {

        // --- Manejo de errores centralizado ---
        onException(Exception.class)
                .handled(true)
                .log("ERROR en el ESB: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                .setHeader("Content-Type", constant("application/json"))
                .setBody(simple("{\"error\": \"No se pudo completar la solicitud\", "
                        + "\"detalle\": \"${exception.message}\"}"));

        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.off)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "ESB Tarea 2 - SOA con Apache Camel")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true");

        // --- Catálogo de entrada ---
        rest("/esb")
                .id("esb-api")
                .description("Bus de servicios: recibe JSON y enruta a los 3 servicios SOAP de atrás")
                .consumes("application/json")
                .produces("application/json")

                .get("/clientes")
                    .description("Lista clientes de ejemplo (ids 1 al 10), vía servicio-clientes")
                    .to("direct:rutaListarClientes")

                .get("/clientes/{id}")
                    .description("Consulta un cliente por id (enruta a servicio-clientes, SOAP)")
                    .param().name("id").type(RestParamType.path).dataType("integer").endParam()
                    .to("direct:rutaClientes")

                .get("/productos")
                    .description("Lista productos de ejemplo (ids 1 al 10), vía servicio-productos")
                    .to("direct:rutaListarProductos")

                .get("/productos/{id}")
                    .description("Consulta un producto por id (enruta a servicio-productos, SOAP)")
                    .param().name("id").type(RestParamType.path).dataType("integer").endParam()
                    .to("direct:rutaProductos")

                .get("/pedidos")
                    .description("Lista todos los pedidos ya registrados (enruta a servicio-pedidos, SOAP)")
                    .to("direct:rutaListarPedidos")

                .post("/pedidos")
                    .description("Registra un pedido nuevo; servicio-pedidos consulta a su vez "
                            + "a servicio-clientes y servicio-productos para enriquecerlo")
                    .type(RegistrarPedidoRequestBody.class)
                    .to("direct:rutaRegistrarPedido");

        // ======================== CLIENTES ========================

        // Lógica base: consulta UN cliente por id (se reutiliza abajo)
        from("direct:rutaClienteUno")
                .routeId("ruta-cliente-uno")
                .log("ESB -> consultando servicio-clientes, id=${header.id}")
                .removeHeaders("CamelHttp*")
                .setBody(simple(SOAP_CLIENTE_TEMPLATE))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/xml; charset=UTF-8"))
                .to("http://servicio-clientes:8081/ws?bridgeEndpoint=true")
                .process(new ClienteResponseProcessor());

        // GET /esb/clientes/{id} -> un solo cliente, en JSON
        from("direct:rutaClientes")
                .routeId("ruta-clientes")
                .to("direct:rutaClienteUno")
                .marshal().json(JsonLibrary.Jackson);

        // GET /esb/clientes -> llama rutaClienteUno 10 veces (ids 1-10) y junta el array
        from("direct:rutaListarClientes")
                .routeId("ruta-listar-clientes")
                .removeHeaders("CamelHttp*")
                .setBody(constant(IDS_DEMO))
                .split(body())
                    .aggregationStrategy(AggregationStrategies.groupedBody())
                    .setHeader("id", body())
                    .to("direct:rutaClienteUno")
                .end()
                .marshal().json(JsonLibrary.Jackson);

        // ======================== PRODUCTOS ========================

        from("direct:rutaProductoUno")
                .routeId("ruta-producto-uno")
                .log("ESB -> consultando servicio-productos, id=${header.id}")
                .removeHeaders("CamelHttp*")
                .setBody(simple(SOAP_PRODUCTO_TEMPLATE))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/xml; charset=UTF-8"))
                .to("http://servicio-productos:8082/ws?bridgeEndpoint=true")
                .process(new ProductoResponseProcessor());

        from("direct:rutaProductos")
                .routeId("ruta-productos")
                .to("direct:rutaProductoUno")
                .marshal().json(JsonLibrary.Jackson);

        from("direct:rutaListarProductos")
                .routeId("ruta-listar-productos")
                .removeHeaders("CamelHttp*")
                .setBody(constant(IDS_DEMO))
                .split(body())
                    .aggregationStrategy(AggregationStrategies.groupedBody())
                    .setHeader("id", body())
                    .to("direct:rutaProductoUno")
                .end()
                .marshal().json(JsonLibrary.Jackson);

        // ======================== PEDIDOS ========================

        from("direct:rutaListarPedidos")
                .routeId("ruta-listar-pedidos")
                .log("ESB -> enrutando a servicio-pedidos (listar)")
                .removeHeaders("CamelHttp*")
                .setBody(constant(SOAP_LISTAR_PEDIDOS))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/xml; charset=UTF-8"))
                .to("http://servicio-pedidos:8083/ws?bridgeEndpoint=true")
                .process(new ListarPedidosResponseProcessor())
                .marshal().json(JsonLibrary.Jackson);

        from("direct:rutaRegistrarPedido")
                .routeId("ruta-registrar-pedido")
                .log("ESB -> enrutando a servicio-pedidos (registrar)")
                .removeHeaders("CamelHttp*")
                .process(new ParseRegistrarPedidoRequestProcessor())
                .setBody(simple(SOAP_REGISTRAR_PEDIDO_TEMPLATE))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/xml; charset=UTF-8"))
                .to("http://servicio-pedidos:8083/ws?bridgeEndpoint=true")
                .process(new PedidoResponseProcessor())
                .marshal().json(JsonLibrary.Jackson);
    }
}
