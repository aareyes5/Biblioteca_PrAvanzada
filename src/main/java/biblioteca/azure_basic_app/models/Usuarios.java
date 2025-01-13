package biblioteca.azure_basic_app.models;

// User.java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuarios {
    @Id
    private String id;
    private String nombre;


    public Usuarios() {
    }

    public Usuarios(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}