package com.hotel.ms_checkin.controller;

import com.hotel.ms_checkin.dto.CheckInRequestDTO;
import com.hotel.ms_checkin.dto.CheckInResponseDTO;
import com.hotel.ms_checkin.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkin")
public class CheckInController {
    @Autowired
    private CheckInService checkInService;

    // 🏨 ENDPOINT 1: REALIZAR CHECK-IN (POST)
    @PostMapping
    public ResponseEntity<?> realizarCheckIn(@Valid @RequestBody CheckInRequestDTO requestDTO, BindingResult result) {

        // Verificamos si el DTO viene vacío
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            CheckInResponseDTO response = checkInService.registrarEntrada(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            // Captura si la reserva ya tiene check-in activo
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    // 🚪 ENDPOINT 2: REALIZAR CHECK-OUT (PUT)
    // Usamos PUT porque estamos actualizando un registro que ya existe (poniéndole hora de salida)
    @PutMapping("/checkout/{reservaId}")
    public ResponseEntity<?> realizarCheckOut(@PathVariable Long reservaId) {
        try {
            CheckInResponseDTO response = checkInService.registrarSalida(reservaId);
            return ResponseEntity.ok(response); // 200 OK
        } catch (IllegalArgumentException e) {
            // Captura si el huésped no está en el hotel
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

}
