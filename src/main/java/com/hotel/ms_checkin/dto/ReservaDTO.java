package com.hotel.ms_checkin.dto;

import lombok.Data;

@Data
public class ReservaDTO {
    private Long id;
    private Long habitacionId;
    private String estado;
}