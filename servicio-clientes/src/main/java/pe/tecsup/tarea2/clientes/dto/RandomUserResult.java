package pe.tecsup.tarea2.clientes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUserResult {

    private RandomUserName name;
    private String email;

    public RandomUserName getName() {
        return name;
    }

    public void setName(RandomUserName name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
