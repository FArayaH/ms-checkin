package com.hotel.ms_checkin.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HabitacionWebClient {
    private final WebClient.Builder webClientBuilder;

    public HabitacionWebClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public void actualizarDisponibilidad(Long id, boolean disponible, String token) {
        try {
            webClientBuilder.build().put()
                    .uri(uriBuilder -> uriBuilder.scheme("http").host("localhost").port(8083)
                            .path("/api/v1/habitaciones/" + id + "/disponibilidad")
                            .queryParam("disponible", disponible).build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve().bodyToMono(Void.class).block();
        } catch (Exception e) { System.out.println("Error habitacion: " + e.getMessage()); }
    }
}