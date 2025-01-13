package biblioteca.azure_basic_app.repository;

import biblioteca.azure_basic_app.models.Reservaciones;
// ReservationRepository.java
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReservacionesRepository extends MongoRepository<Reservaciones, String> {
    List<Reservaciones> findBySeccion(String seccion);

    //econtrar por usuario
    List<Reservaciones> findByUsuario(String usuario);
}