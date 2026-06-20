package pe.tecsup.tarea2.productos.endpoint;

import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pe.tecsup.tarea2.productos.ObtenerProductoRequest;
import pe.tecsup.tarea2.productos.ObtenerProductoResponse;
import pe.tecsup.tarea2.productos.Producto;
import pe.tecsup.tarea2.productos.dto.ProductoApiDto;

import java.math.BigDecimal;

@Endpoint
public class ProductoEndpoint {

    private static final String NAMESPACE_URI = "http://shanira.tarea2/productos";
    // Fake Store API: catálogo real con ids del 1 al 20
    private static final String FAKE_STORE_API = "https://fakestoreapi.com/products/%d";

    private final RestTemplate restTemplate;

    public ProductoEndpoint(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "obtenerProductoRequest")
    @ResponsePayload
    public ObtenerProductoResponse obtenerProducto(@RequestPayload ObtenerProductoRequest request) {
        int id = request.getId();

        ObtenerProductoResponse response = new ObtenerProductoResponse();
        Producto producto = new Producto();
        producto.setId(id);

        try {
            String url = String.format(FAKE_STORE_API, id);
            ProductoApiDto apiProducto = restTemplate.getForObject(url, ProductoApiDto.class);

            if (apiProducto != null && apiProducto.getTitle() != null) {
                producto.setNombre(apiProducto.getTitle());
                producto.setCategoria(apiProducto.getCategory());
                producto.setPrecio(BigDecimal.valueOf(apiProducto.getPrice()));
            } else {
                producto.setNombre("Producto Demo " + id);
                producto.setCategoria("general");
                producto.setPrecio(BigDecimal.valueOf(9.99));
            }
        } catch (Exception ex) {
            // Si la API externa falla o el id no existe (válidos: 1-20), no se cae el servicio
            producto.setNombre("Producto Demo " + id);
            producto.setCategoria("general");
            producto.setPrecio(BigDecimal.valueOf(9.99));
        }

        response.setProducto(producto);
        return response;
    }
}
