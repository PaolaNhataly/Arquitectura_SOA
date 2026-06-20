package pe.tecsup.tarea2.pedidos.domain;

public class ClienteInfo {

    private final int id;
    private final String nombre;
    private final String email;

    public ClienteInfo(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
