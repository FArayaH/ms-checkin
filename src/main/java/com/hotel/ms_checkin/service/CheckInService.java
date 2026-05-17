package com.hotel.ms_checkin.service;

import com.hotel.ms_checkin.dto.CheckInRequestDTO;
import com.hotel.ms_checkin.dto.CheckInResponseDTO;
import com.hotel.ms_checkin.model.CheckIn;
import com.hotel.ms_checkin.repository.CheckInRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Adiós @Autowired, inyectamos de forma segura por constructor
@Slf4j // Agregamos los logs estructurados
public class CheckInService {

    // Al usar @RequiredArgsConstructor, debe ser "private final"
    private final CheckInRepository checkInRepository;

    //  MÉTODO 1: REALIZAR CHECK-IN
    @Transactional // Indicamos que vamos a modificar la base de datos
    public CheckInResponseDTO registrarEntrada(CheckInRequestDTO requestDTO) {

        log.info("[CHECKIN_SERVICE] Validando si la reserva ID {} ya tiene un check-in activo...", requestDTO.getReservaId());
        Optional<CheckIn> checkInExistente = checkInRepository.findByReservaIdAndEstado(requestDTO.getReservaId(), "ACTIVO");

        if (checkInExistente.isPresent()) {
            log.warn("Falla en Check-In: La reserva ID {} ya se encuentra ACTIVA", requestDTO.getReservaId());
            throw new IllegalStateException("Error Esta reserva ya tiene un Check-In activo en el hotel");
        }

        log.info("[CHECKIN_SERVICE] Generando nuevo Check-In para reserva ID {}", requestDTO.getReservaId());
        CheckIn nuevoCheckIn = new CheckIn();
        nuevoCheckIn.setReservaId(requestDTO.getReservaId());
        nuevoCheckIn.setFechaHoraCheckIn(LocalDateTime.now());
        nuevoCheckIn.setEstado("ACTIVO");

        CheckIn guardado = checkInRepository.save(nuevoCheckIn);
        log.info("[CHECKIN_SERVICE] Check-In guardado exitosamente con ID {}", guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    //  MÉTODO 2: REALIZAR CHECK-OUT
    @Transactional // Indicamos que vamos a modificar la base de datos
    public CheckInResponseDTO registrarSalida(Long reservaId) {

        log.info("[CHECKIN_SERVICE] Iniciando proceso de Check-Out para reserva ID {}", reservaId);
        CheckIn checkInActivo = checkInRepository.findByReservaIdAndEstado(reservaId, "ACTIVO")
                .orElseThrow(() -> {
                    log.error("Falla en Check-Out: No se encontró registro activo para la reserva ID {}", reservaId);
                    return new IllegalArgumentException("Error No se puede hacer Check-Out porque el huésped no está en el hotel");
                });

        checkInActivo.setFechaHoraCheckOut(LocalDateTime.now());
        checkInActivo.setEstado("FINALIZADO");

        log.info("[CHECKIN_SERVICE] Actualizando estado a FINALIZADO y guardando hora de salida...");
        CheckIn actualizado = checkInRepository.save(checkInActivo);

        return convertirAResponseDTO(actualizado);
    }

    // Método auxiliar (sin @Transactional porque no toca la BD directamente, solo mapea)
    private CheckInResponseDTO convertirAResponseDTO(CheckIn checkIn) {
        return new CheckInResponseDTO(
                checkIn.getId(),
                checkIn.getReservaId(),
                checkIn.getFechaHoraCheckIn(),
                checkIn.getFechaHoraCheckOut(),
                checkIn.getEstado()
        );
    }
}