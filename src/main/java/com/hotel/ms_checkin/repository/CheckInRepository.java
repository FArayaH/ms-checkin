package com.hotel.ms_checkin.repository;
import com.hotel.ms_checkin.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    // Este método busca automáticamente en la BD por ID de reserva y por Estado
    Optional<CheckIn> findByReservaIdAndEstado(Long reservaId, String estado);
    Optional<CheckIn> findByReservaId(Long reservaId);
}
