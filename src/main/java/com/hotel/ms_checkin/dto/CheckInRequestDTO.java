package com.hotel.ms_checkin.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequestDTO {
    @NotNull(message = "Error: El ID de la reserva es obligatorio para realizar el Check-In")
    private Long reservaId;
}
