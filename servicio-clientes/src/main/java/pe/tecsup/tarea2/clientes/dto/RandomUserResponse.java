package pe.tecsup.tarea2.clientes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUserResponse {

    private List<RandomUserResult> results;

    public List<RandomUserResult> getResults() {
        return results;
    }

    public void setResults(List<RandomUserResult> results) {
        this.results = results;
    }
}
