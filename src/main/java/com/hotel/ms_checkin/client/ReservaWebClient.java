package com.hotel.ms_checkin.client;

import com.hotel.ms_checkin.dto.ReservaDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReservaWebClient {
    private final WebClient.Builder webClientBuilder;

    public ReservaWebClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public ReservaDTO obtenerReservaPorId(Long id, String token) {
        try {
            return webClientBuilder.build().get()
                    .uri("http://localhost:8088/api/v1/reserva/" + id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve().bodyToMono(ReservaDTO.class).block();
        } catch (Exception e) { return null; }
    }

    public void actualizarEstadoReserva(Long id, String estado, String token) {
        try {
            webClientBuilder.build().put()
                    .uri(uriBuilder -> uriBuilder.scheme("http").host("localhost").port(8081)
                            .path("/api/v1/reserva/" + id + "/estado")
                            .queryParam("nuevoEstado", estado).build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve().bodyToMono(Void.class).block();
        } catch (Exception e) { System.out.println("Error reserva: " + e.getMessage()); }
    }
}