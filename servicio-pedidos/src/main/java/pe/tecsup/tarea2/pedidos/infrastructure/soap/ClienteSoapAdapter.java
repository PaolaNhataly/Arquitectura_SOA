package pe.tecsup.tarea2.pedidos.infrastructure.soap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import pe.tecsup.tarea2.pedidos.application.port.ClienteGatewayPort;
import pe.tecsup.tarea2.pedidos.domain.ClienteInfo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Adaptador de salida: implementa ClienteGatewayPort consumiendo el
 * contrato SOAP de servicio-clientes. Si ese servicio pasara a ser
 * REST mañana, solo se reescribe esta clase.
 */
@Component
public class ClienteSoapAdapter implements ClienteGatewayPort {

    private static final String URL = "http://servicio-clientes:8081/ws";
    private static final String TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:cli=\"http://shanira.tarea2/clientes\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>"
            + "<cli:obtenerClienteRequest><cli:id>%d</cli:id></cli:obtenerClienteRequest>"
            + "</soapenv:Body></soapenv:Envelope>";

    private final RestTemplate restTemplate;

    public ClienteSoapAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ClienteInfo obtenerCliente(int clienteId) {
        String requestXml = String.format(TEMPLATE, clienteId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> entity = new HttpEntity<>(requestXml, headers);

        try {
            String responseXml = restTemplate.postForObject(URL, entity, String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(responseXml)));

            XPath xpath = XPathFactory.newInstance().newXPath();
            String nombre = xpath.evaluate("//*[local-name()='nombre']/text()", doc);
            String email = xpath.evaluate("//*[local-name()='email']/text()", doc);

            return new ClienteInfo(clienteId, nombre, email);
        } catch (Exception ex) {
            return new ClienteInfo(clienteId, "Cliente " + clienteId + " (no verificado)", "desconocido@demo.pe");
        }
    }
}
