package pe.tecsup.tarea2.clientes.endpoint;

import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pe.tecsup.tarea2.clientes.Cliente;
import pe.tecsup.tarea2.clientes.ObtenerClienteRequest;
import pe.tecsup.tarea2.clientes.ObtenerClienteResponse;
import pe.tecsup.tarea2.clientes.dto.RandomUserResponse;
import pe.tecsup.tarea2.clientes.dto.RandomUserResult;

@Endpoint
public class ClienteEndpoint {

    private static final String NAMESPACE_URI = "http://shanira.tarea2/clientes";
    private static final String RANDOM_USER_API = "https://randomuser.me/api/?seed=cliente%d&nat=es,us";

    private final RestTemplate restTemplate;

    public ClienteEndpoint(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "obtenerClienteRequest")
    @ResponsePayload
    public ObtenerClienteResponse obtenerCliente(@RequestPayload ObtenerClienteRequest request) {
        int id = request.getId();

        ObtenerClienteResponse response = new ObtenerClienteResponse();
        Cliente cliente = new Cliente();
        cliente.setId(id);

        try {
            // El "seed" hace que el mismo id siempre devuelva la misma persona simulada
            String url = String.format(RANDOM_USER_API, id);
            RandomUserResponse apiResponse = restTemplate.getForObject(url, RandomUserResponse.class);

            if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
                RandomUserResult persona = apiResponse.getResults().get(0);
                cliente.setNombre(persona.getName().getFirst() + " " + persona.getName().getLast());
                cliente.setEmail(persona.getEmail());
            } else {
                cliente.setNombre("Cliente Demo " + id);
                cliente.setEmail("cliente" + id + "@demo.pe");
            }
        } catch (Exception ex) {
            // Si la API externa falla (sin internet, rate limit, etc.) no se cae el servicio
            cliente.setNombre("Cliente Demo " + id);
            cliente.setEmail("cliente" + id + "@demo.pe");
        }

        response.setCliente(cliente);
        return response;
    }
}
