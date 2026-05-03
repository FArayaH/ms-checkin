package com.hotel.ms_checkin.service;


import com.hotel.ms_checkin.dto.CheckInRequestDTO;
import com.hotel.ms_checkin.dto.CheckInResponseDTO;
import com.hotel.ms_checkin.model.CheckIn;
import com.hotel.ms_checkin.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CheckInService {
    @Autowired
    private CheckInRepository checkInRepository;

    //  MÉTODO 1: REALIZAR CHECK-IN
    public CheckInResponseDTO registrarEntrada(CheckInRequestDTO requestDTO) {

        Optional<CheckIn> checkInExistente = checkInRepository.findByReservaIdAndEstado(requestDTO.getReservaId(), "ACTIVO");

        if (checkInExistente.isPresent()) {
            throw new IllegalStateException("Error Esta reserva ya tiene un Check-In activo en el hotel");
        }

        CheckIn nuevoCheckIn = new CheckIn();
        nuevoCheckIn.setReservaId(requestDTO.getReservaId());
        nuevoCheckIn.setFechaHoraCheckIn(LocalDateTime.now());
        nuevoCheckIn.setEstado("ACTIVO");

        CheckIn guardado = checkInRepository.save(nuevoCheckIn);

        return convertirAResponseDTO(guardado);
    }

    //  MÉTODO 2: REALIZAR CHECK-OUT
    public CheckInResponseDTO registrarSalida(Long reservaId) {

        CheckIn checkInActivo = checkInRepository.findByReservaIdAndEstado(reservaId, "ACTIVO")
                .orElseThrow(() -> new IllegalArgumentException("Error No se puede hacer Check-Out porque el huésped no está en el hotel"));

        checkInActivo.setFechaHoraCheckOut(LocalDateTime.now());
        checkInActivo.setEstado("FINALIZADO");

        CheckIn actualizado = checkInRepository.save(checkInActivo);

        return convertirAResponseDTO(actualizado);
    }

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
