package biblioteca.azure_basic_app.controllers;

import biblioteca.azure_basic_app.components.JwtUtil;
import biblioteca.azure_basic_app.repository.UsuarioRepository;
import biblioteca.azure_basic_app.models.Usuarios;

import java.util.HashMap;
import java.util.Map;


// AuthController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String cedula = loginRequest.get("id");
        String nombre = loginRequest.get("nombre");

        // Generar token JWT
        String token = jwtUtil.generateToken(cedula, nombre);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        // Verificar si el usuario ya existe
        if (usuarioRepository.findById(cedula).isPresent()) {
            response.put("mensaje", "Usuario ya registrado");
            response.put("usuario", nombre);
        } else {
            // Crear nuevo usuario
            Usuarios newuser = new Usuarios();
            newuser.setId(cedula);
            newuser.setNombre(nombre);
            usuarioRepository.save(newuser);

            response.put("mensaje", "Usuario nuevo registrado");
            response.put("usuario", nombre);
        }

        return response;
    }


    
}