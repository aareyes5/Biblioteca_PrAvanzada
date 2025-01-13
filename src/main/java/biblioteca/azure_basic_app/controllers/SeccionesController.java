package biblioteca.azure_basic_app.controllers;

import biblioteca.azure_basic_app.repository.SecccionesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/secciones")
public class SeccionesController {

    @Autowired
    private SecccionesRepository seccionesRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSecciones() {
        return ResponseEntity.ok(seccionesRepository.findAll());
    }   
    
}
