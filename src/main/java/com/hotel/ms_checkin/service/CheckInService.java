package com.hotel.ms_checkin.service;

import com.hotel.ms_checkin.client.HabitacionWebClient;
import com.hotel.ms_checkin.client.ReservaWebClient;
import com.hotel.ms_checkin.dto.CheckInRequestDTO;
import com.hotel.ms_checkin.dto.CheckInResponseDTO;
import com.hotel.ms_checkin.dto.ReservaDTO;
import com.hotel.ms_checkin.model.CheckIn;
import com.hotel.ms_checkin.repository.CheckInRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final ReservaWebClient reservaWebClient;
    private final HabitacionWebClient habitacionWebClient;

    // MÉTODO 1: CHECK-IN (ENTRADA)
    @Transactional
    public CheckInResponseDTO registrarCheckIn(CheckInRequestDTO dto, String token) {
        log.info("[CHECKIN] Validando reserva ID: {}", dto.getReservaId());

        ReservaDTO reserva = reservaWebClient.obtenerReservaPorId(dto.getReservaId(), token);
        if (reserva == null) {
            throw new IllegalArgumentException("Error: La reserva no existe.");
        }

        log.info("[CHECKIN] Ocupando habitacion ID: {}", reserva.getHabitacionId());
        habitacionWebClient.actualizarDisponibilidad(reserva.getHabitacionId(), false, token);

        log.info("[CHECKIN] Cambiando reserva a EN_CURSO");
        reservaWebClient.actualizarEstadoReserva(reserva.getId(), "EN_CURSO", token);

        CheckIn checkIn = new CheckIn();
        checkIn.setReservaId(reserva.getId());
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setEstado("REALIZADO");
        CheckIn guardado = checkInRepository.save(checkIn);

        return new CheckInResponseDTO(guardado.getId(), guardado.getReservaId(),
                guardado.getFechaHoraCheckIn(), null, guardado.getEstado());
    }

    // MÉTODO 2: CHECK-OUT (SALIDA)
    @Transactional
    public CheckInResponseDTO registrarSalida(Long reservaId, String token) {
        log.info("[CHECKOUT] Iniciando proceso de salida para reserva ID: {}", reservaId);

        CheckIn checkIn = checkInRepository.findByReservaId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No hay un check-in activo para esta reserva."));

        if (checkIn.getFechaHoraCheckOut() != null) {
            throw new IllegalStateException("El check-out ya fue realizado para esta reserva.");
        }

        ReservaDTO reserva = reservaWebClient.obtenerReservaPorId(reservaId, token);
        if (reserva == null) {
            throw new IllegalArgumentException("Error al comunicarse con el servicio de reservas.");
        }

        log.info("[CHECKOUT] Liberando habitacion ID: {}", reserva.getHabitacionId());
        habitacionWebClient.actualizarDisponibilidad(reserva.getHabitacionId(), true, token);

        log.info("[CHECKOUT] Cambiando reserva a FINALIZADA");
        reservaWebClient.actualizarEstadoReserva(reservaId, "FINALIZADA", token);

        checkIn.setFechaHoraCheckOut(LocalDateTime.now());
        checkIn.setEstado("FINALIZADO");

        CheckIn actualizado = checkInRepository.save(checkIn);

        return new CheckInResponseDTO(
                actualizado.getId(),
                actualizado.getReservaId(),
                actualizado.getFechaHoraCheckIn(),
                actualizado.getFechaHoraCheckOut(),
                actualizado.getEstado()
        );
    }
}