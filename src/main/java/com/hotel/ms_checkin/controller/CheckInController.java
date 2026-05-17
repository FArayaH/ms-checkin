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

@Slf4j // Agregamos Lombok para los logs estructurados
@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    //  ENDPOINT 1: REALIZAR CHECK-IN (POST)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Como es Check-In, asumimos que solo el staff (ADMIN) lo hace
    public ResponseEntity<?> realizarCheckIn(@Valid @RequestBody CheckInRequestDTO requestDTO, HttpServletRequest request) {

        // Formato de log exacto al de tus compañeras
        log.info("[CHECKIN] POST /api/v1/checkin - realizar check-in");

        // Extraer el token de la petición por si se necesita
        String token = request.getHeader("Authorization");

        try {
            CheckInResponseDTO response = checkInService.registrarEntrada(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    //  ENDPOINT 2: REALIZAR CHECK-OUT (PUT)
    @PutMapping("/checkout/{reservaId}")
    @PreAuthorize("hasRole('ADMIN')") // Solo el staff debería poder hacer el check-out
    public ResponseEntity<?> realizarCheckOut(@PathVariable Long reservaId) {

        // Log estructurado con la variable del ID
        log.info("[CHECKIN] PUT /api/v1/checkin/checkout/{} - realizar check-out", reservaId);

        try {
            CheckInResponseDTO response = checkInService.registrarSalida(reservaId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}