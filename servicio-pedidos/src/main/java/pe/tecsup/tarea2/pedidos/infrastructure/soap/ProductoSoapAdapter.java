package pe.tecsup.tarea2.pedidos.infrastructure.soap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import pe.tecsup.tarea2.pedidos.application.port.ProductoGatewayPort;
import pe.tecsup.tarea2.pedidos.domain.ProductoInfo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.math.BigDecimal;

@Component
public class ProductoSoapAdapter implements ProductoGatewayPort {

    private static final String URL = "http://servicio-productos:8082/ws";
    private static final String TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:prod=\"http://shanira.tarea2/productos\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>"
            + "<prod:obtenerProductoRequest><prod:id>%d</prod:id></prod:obtenerProductoRequest>"
            + "</soapenv:Body></soapenv:Envelope>";

    private final RestTemplate restTemplate;

    public ProductoSoapAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ProductoInfo obtenerProducto(int productoId) {
        String requestXml = String.format(TEMPLATE, productoId);

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
            String precioTexto = xpath.evaluate("//*[local-name()='precio']/text()", doc);

            BigDecimal precio = (precioTexto == null || precioTexto.isBlank())
                    ? BigDecimal.ZERO
                    : new BigDecimal(precioTexto);

            return new ProductoInfo(productoId, nombre, precio);
        } catch (Exception ex) {
            return new ProductoInfo(productoId, "Producto " + productoId + " (no verificado)", BigDecimal.ZERO);
        }
    }
}
