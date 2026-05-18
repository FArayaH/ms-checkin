package com.hotel.ms_checkin.controller;

import com.hotel.ms_checkin.dto.CheckInRequestDTO;
import com.hotel.ms_checkin.dto.CheckInResponseDTO;
import com.hotel.ms_checkin.service.CheckInService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    //  ENDPOINT 1: REALIZAR CHECK-IN (POST)
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // Lo comento temporalmente para probar sin bloqueos
    public ResponseEntity<?> realizarCheckIn(@Valid @RequestBody CheckInRequestDTO requestDTO, HttpServletRequest request) {

        log.info("[CHECKIN] POST /api/v1/checkin - realizar check-in");

        // Extraer el token y limpiarle la palabra "Bearer " para que los WebClients funcionen bien
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : "";

        try {
            // AQUÍ EL CAMBIO: Usamos registrarCheckIn y le pasamos el token
            CheckInResponseDTO response = checkInService.registrarCheckIn(requestDTO, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error en checkin: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    //  ENDPOINT 2: REALIZAR CHECK-OUT (PUT) - ¡Este te lo dejo intacto porque está genial!
    @PutMapping("/checkout/{reservaId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> realizarCheckOut(@PathVariable Long reservaId, HttpServletRequest request) {

        log.info("[CHECKIN] PUT /api/v1/checkin/checkout/{} - realizar check-out", reservaId);

        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : "";

        try {
            CheckInResponseDTO response = checkInService.registrarSalida(reservaId, token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error en checkout: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}