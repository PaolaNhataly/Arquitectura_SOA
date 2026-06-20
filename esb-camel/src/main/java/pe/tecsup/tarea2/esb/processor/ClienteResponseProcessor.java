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

/**
 * Toma el XML SOAP que devuelve servicio-clientes y lo convierte
 * en un Map simple (luego Camel lo serializa a JSON para el cliente final).
 */
public class ClienteResponseProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String xml = exchange.getIn().getBody(String.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        XPath xpath = XPathFactory.newInstance().newXPath();
        String id = xpath.evaluate("//*[local-name()='cliente']/*[local-name()='id']/text()", doc);
        String nombre = xpath.evaluate("//*[local-name()='nombre']/text()", doc);
        String email = xpath.evaluate("//*[local-name()='email']/text()", doc);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("nombre", nombre);
        result.put("email", email);

        exchange.getIn().setBody(result);
    }
}
