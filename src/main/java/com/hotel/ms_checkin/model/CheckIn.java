package com.hotel.ms_checkin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación lógica con el ms-reserva
    private Long reservaId;

    // Usamos LocalDateTime porque en un hotel importa la fecha y la hora exacta
    private LocalDateTime fechaHoraCheckIn;

    private LocalDateTime fechaHoraCheckOut;

    // Guardará "ACTIVO" (en el hotel) o "FINALIZADO" (ya se fue)
    private String estado;
}
