package biblioteca.azure_basic_app.controllers;

import biblioteca.azure_basic_app.models.Reservaciones;
import biblioteca.azure_basic_app.repository.ReservacionesRepository;
import biblioteca.azure_basic_app.components.JwtUtil;

import io.jsonwebtoken.Claims;

// ReservationController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservacionesRepository reservationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/seccion/{seccion}")
    public List<Reservaciones> getReservationsBySeccion(@PathVariable String seccion) {
        return reservationRepository.findBySeccion(seccion);
    }

    @PostMapping("/reserve")
    public Map<String, String> reserveSeat(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> reservation) {
        try {
            // Validar el token
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.validateToken(token);

            // Extraer datos del usuario desde el token
            String userId = claims.getSubject();
            String userName = claims.get("nombre", String.class);

            Reservaciones newReservation = new Reservaciones();
            newReservation.setId(reservation.get("id"));
            newReservation.setEstado(true);
            newReservation.setConfirmacion(false);
            newReservation.setUsuario(userId); // Asociar el usuario
            newReservation.setColumn(reservation.get("column"));
            newReservation.setRow(reservation.get("row"));
            newReservation.setSeccion(reservation.get("seccion"));

            // Guardar la reserva en la base de datos
            reservationRepository.save(newReservation);

            // Preparar la respuesta
            Map<String, String> response = new HashMap<>();
            response.put("id", userId);
            response.put("nombre", userName);
            response.put("estado", "true");
            response.put("confirmacion", "false");
            response.put("mensaje", "Reserva guardada con éxito");

            // Notificar mediante WebSocket
            messagingTemplate.convertAndSend("/topic/seats", newReservation);

            return response;
        } catch (Exception e) {
            // Manejo de errores
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        }
    }


    @DeleteMapping("/cancel/{id}")
    public void cancelReservation(@PathVariable String id) {
        reservationRepository.deleteById(id);
         // Notificar mediante WebSocket
        messagingTemplate.convertAndSend("/topic/seats", id);

        
    }

    //confirmacion de mi reserva
    @PutMapping("/confirm/{id}")
    public ResponseEntity<String> confirmReservation(@PathVariable String id) {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Map<String, String> userDetails = (Map<String, String>) authentication.getDetails();

            String userId = userDetails.get("id");
            // Buscar la reservación por ID y usuario
            Reservaciones reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

            
            // Confirmar la reserva
            reservation.setConfirmacion(true);
            reservationRepository.save(reservation);

            // Notificar mediante WebSocket
            messagingTemplate.convertAndSend("/topic/seats", reservation);

            return ResponseEntity.ok("Reserva confirmada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al confirmar la reserva: " + e.getMessage());
        }
    }


}