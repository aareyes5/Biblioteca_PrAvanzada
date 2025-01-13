package biblioteca.azure_basic_app.repository;


import biblioteca.azure_basic_app.models.Usuarios;

// UserRepository.java
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuarios, String> {}