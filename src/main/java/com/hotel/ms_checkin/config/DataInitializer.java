package com.hotel.ms_checkin.config;

import com.hotel.ms_checkin.model.CheckIn;
import com.hotel.ms_checkin.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner{
    @Autowired
    private CheckInRepository checkInRepository;

    @Override
    public void run(String... args) throws Exception {

        // Si la tabla está vacía, metemos datos de prueba
        if (checkInRepository.count() == 0) {

            // 1. Un Check-In de alguien que sigue en el hotel (Entró ayer)
            CheckIn c1 = new CheckIn();
            c1.setReservaId(1L);
            c1.setFechaHoraCheckIn(LocalDateTime.now().minusDays(1));
            c1.setEstado("ACTIVO");
            checkInRepository.save(c1);

            // 2. Un Check-In de alguien que ya hizo Check-Out (Entró hace 3 días, salió ayer)
            CheckIn c2 = new CheckIn();
            c2.setReservaId(2L);
            c2.setFechaHoraCheckIn(LocalDateTime.now().minusDays(3));
            c2.setFechaHoraCheckOut(LocalDateTime.now().minusDays(1));
            c2.setEstado("FINALIZADO");
            checkInRepository.save(c2);

            System.out.println(">>> BD de Check-In vacía. Datos de prueba cargados con éxito.");
        } else {
            System.out.println(">>> La base de datos ya tiene Check-Ins. Omitiendo carga inicial.");
        }
    }

}
