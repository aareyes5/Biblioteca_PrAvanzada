package biblioteca.azure_basic_app.repository;


import biblioteca.azure_basic_app.models.Secciones;

// SectionRepository.java
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SecccionesRepository extends MongoRepository<Secciones, String> {

    //cojer una lista por nombres
    List<Secciones> findByNombre(String nombre);

    //cojer una lista por horario de inicio
    List<Secciones> findByHorarioInicio(String horarioInicio);

    //cojer una lista por horario de fin
    List<Secciones> findByHorarioFin(String horarioFin);

    //cojer una lista por nombre y horario de inicio
    List<Secciones> findByNombreAndHorarioInicio(String nombre, String horarioInicio);

    //cojer el orario de inicio y fin por nombre
    List<Secciones> findByNombreAndHorarioInicioAndHorarioFin(String nombre, String horarioInicio, String horarioFin);
    


}