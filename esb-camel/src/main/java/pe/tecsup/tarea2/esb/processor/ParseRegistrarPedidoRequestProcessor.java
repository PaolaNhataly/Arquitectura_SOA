package pe.tecsup.tarea2.esb.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * El cliente manda JSON: { "clienteId": 1, "productoId": 5, "cantidad": 2 }.
 * Esto lo lee y lo deja en headers para que la plantilla SOAP los use.
 */
public class ParseRegistrarPedidoRequestProcessor implements Processor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String json = exchange.getIn().getBody(String.class);
        JsonNode node = MAPPER.readTree(json);

        exchange.getIn().setHeader("clienteId", node.get("clienteId").asInt());
        exchange.getIn().setHeader("productoId", node.get("productoId").asInt());
        exchange.getIn().setHeader("cantidad", node.get("cantidad").asInt());
    }
}
