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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



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

    @PostMapping ("/Reservar")
    //hacer una reserva con el envio de un body con datos de la reserva
    public ResponseEntity<?> hacerReserva(@RequestHeader("Authorization") String authHeader,
        @RequestBody Reservaciones reservacion) {
        // Validar el token
        try{
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autorización no proporcionado o inválido.");
            }

            String token = authHeader.substring(7); // Eliminar "Bearer " del token
            Claims claims = jwtUtil.validateToken(token);
            String userId = claims.getSubject();

            //verificar que el usuario tiene el mismo id que el usuario que hizo la reserva
            if (!userId.equals(reservacion.getUsuario())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No autorizado");
                    
            }
            //verificar si el haciento tiene la bandera de reserva activa
            if (reservacion.isEstado()) {
                // Obtener las reservas del usuario
                List<Reservaciones> reservations = reservationRepository.findByUsuario(userId);

                // Buscar si el asiento ya está reservado por el usuario
                for (Reservaciones reservation : reservations) {
                    if (reservation.getColumn().equals(reservacion.getColumn()) &&
                        reservation.getRow().equals(reservacion.getRow()) &&
                        reservation.getSeccion().equals(reservacion.getSeccion())) {

                        if (reservation.isEstado()) {
                            return ResponseEntity.status(HttpStatus.CONFLICT)
                                    .body("El asiento ya está reservado");
                        } else {
                            //coger el haciento selecionado y cancelar la reserva
                            Reservaciones cancel = reservationRepository.findByColumnAndRowAndSeccion(reservacion.getColumn(), reservacion.getRow(), reservacion.getSeccion())
                                    .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));

                            // Cancelar la reserva del asiento
                            cancel.setEstado(false);
                            reservationRepository.save(cancel);
                            // Notificar mediante WebSocket
                            messagingTemplate.convertAndSend("/topic/seats", reservacion);
                            return ResponseEntity.ok("Reserva cancelada exitosamente");
                        }
                    }
                }
            } else {

                //Coger los datos actuales del haciento y actualizarlos a reservado
                Reservaciones reservation = reservationRepository.findByColumnAndRowAndSeccion(reservacion.getColumn(), reservacion.getRow(), reservacion.getSeccion())
                        .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));
                reservation.setUsuario(userId);
                reservation.setEstado(true);
                reservationRepository.save(reservation);
                // Notificar mediante WebSocket
                messagingTemplate.convertAndSend("/topic/seats", reservacion);
                return ResponseEntity.ok("Asiento reservado exitosamente");
            }

        }  catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al cargar los asientos: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Solicitud no procesada correctamente");
    }

    //cargar asientos o crearlos en la db 
    @PostMapping("/cargar_crearHcaientos")
    public ResponseEntity<?> cargarHacientos(
            @RequestHeader("Authorization") String authHeader, // Capturar el header de autorización
            @RequestBody Map<String, String> seccion) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autorización no proporcionado o inválido.");
            }

            String token = authHeader.substring(7); // Eliminar "Bearer " del token
            Claims claims = jwtUtil.validateToken(token);
            String userId = claims.getSubject();

            // Verificar si el usuario es válido
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autorizado.");
            }

            // Continuar con la lógica de cargar/crear asientos
            if (reservationRepository.findBySeccion(seccion.get("seccion")).isEmpty()) {
                // Crear los 20 asientos de la sección
                for (int k = 1; k <= 20; k++) {
                    String id_servicio = seccion.get("seccion");
                    Reservaciones reservation = new Reservaciones();
                    reservation.setId(k + "_" + id_servicio);
                    reservation.setSeccion(id_servicio);
                    for (int i = 1; i <= 4; i++) {
                        reservation.setColumn(String.valueOf(i));
                        for (int j = 1; j <= 5; j++) {
                            reservation.setRow(String.valueOf(j));
                            reservation.setEstado(false);
                            reservation.setUsuario("");
                            reservationRepository.save(reservation);
                        }
                    }
                }
            }

            // Obtener los asientos creados/existentes
            List<Reservaciones> reservations = reservationRepository.findBySeccion(seccion.get("seccion"));

            // Convertir los objetos a JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(reservations);

            return ResponseEntity.ok(jsonResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar los asientos: " + e.getMessage());
        }
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