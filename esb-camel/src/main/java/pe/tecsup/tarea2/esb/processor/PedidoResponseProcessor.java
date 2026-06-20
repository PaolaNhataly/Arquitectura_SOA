package pe.tecsup.tarea2.esb.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class PedidoResponseProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String xml = exchange.getIn().getBody(String.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        XPath xpath = XPathFactory.newInstance().newXPath();
        Map<String, Object> pedido = new LinkedHashMap<>();
        pedido.put("id", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='id']/text()", doc));
        pedido.put("clienteId", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='clienteId']/text()", doc));
        pedido.put("clienteNombre", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='clienteNombre']/text()", doc));
        pedido.put("productoId", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='productoId']/text()", doc));
        pedido.put("productoNombre", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='productoNombre']/text()", doc));
        pedido.put("cantidad", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='cantidad']/text()", doc));
        pedido.put("precioUnitario", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='precioUnitario']/text()", doc));
        pedido.put("total", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='total']/text()", doc));
        pedido.put("fechaRegistro", xpath.evaluate("//*[local-name()='pedido']/*[local-name()='fechaRegistro']/text()", doc));

        exchange.getIn().setBody(pedido);
    }
}
