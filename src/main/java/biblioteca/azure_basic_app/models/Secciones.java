package biblioteca.azure_basic_app.models;

// Section.java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sections")
public class Secciones {
    @Id
    private String id;
    private String nombre;
    private String horarioInicio;
    private String horarioFin;



    public Secciones() {
    }

    public Secciones(String id, String nombre, String horarioInicio, String horarioFin) {
        this.id = id;
        this.nombre = nombre;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
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

    public String getHorarioInicio() {
        return horarioInicio;
    }


    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(String horarioFin) {
        this.horarioFin = horarioFin;
    }


}