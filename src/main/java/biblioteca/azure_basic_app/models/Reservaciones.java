package biblioteca.azure_basic_app.models;


// Reservation.java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reservaciones")
public class Reservaciones {
    @Id
    private String id;
    private String column;
    private String row;
    private boolean estado;
    private boolean confirmacion;
    private String seccion;
    private String usuario;

    public Reservaciones() {
    }

    public Reservaciones(String id, String column, String row, boolean estado, boolean confirmacion, String seccion, String usuario) {
        this.id = id;
        this.column = column;
        this.row = row;
        this.estado = estado;
        this.confirmacion = confirmacion;
        this.seccion = seccion;
        this.usuario = usuario;
    }


    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public boolean isEstado() {
        return estado;
    }

    

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(boolean confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}