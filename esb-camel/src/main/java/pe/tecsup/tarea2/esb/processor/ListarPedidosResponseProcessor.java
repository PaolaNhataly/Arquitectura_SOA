package pe.tecsup.tarea2.esb.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListarPedidosResponseProcessor implements Processor {

    private static final String[] CAMPOS = {
            "id", "clienteId", "clienteNombre", "productoId", "productoNombre",
            "cantidad", "precioUnitario", "total", "fechaRegistro"
    };

    @Override
    public void process(Exchange exchange) throws Exception {
        String xml = exchange.getIn().getBody(String.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList pedidoNodes = (NodeList) xpath.evaluate(
                "//*[local-name()='listarPedidosResponse']/*[local-name()='pedido']",
                doc, XPathConstants.NODESET);

        List<Map<String, Object>> pedidos = new ArrayList<>();
        for (int i = 0; i < pedidoNodes.getLength(); i++) {
            Element pedidoEl = (Element) pedidoNodes.item(i);
            Map<String, Object> pedido = new LinkedHashMap<>();
            for (String campo : CAMPOS) {
                pedido.put(campo, textoDe(pedidoEl, campo));
            }
            pedidos.add(pedido);
        }

        exchange.getIn().setBody(pedidos);
    }

    private String textoDe(Element padre, String nombreLocal) {
        NodeList hijos = padre.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node nodo = hijos.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE && nombreLocal.equals(nodo.getLocalName())) {
                return nodo.getTextContent();
            }
        }
        return null;
    }
}
