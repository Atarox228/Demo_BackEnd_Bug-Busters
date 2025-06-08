package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.servicios.CoordenadaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class CoordenadaServiceTest {
    @Autowired
    private CoordenadaService coordenadaService;
    @Autowired
    private CoordenadaDAOMongo coordenadaDAO;

    private CoordenadaMongo coord1;
    private CoordenadaMongo coord2;

    @BeforeEach
    void setup() {
        coordenadaDAO.deleteAll();
        coord1 = new CoordenadaMongo(new GeoJsonPoint(-58.0, -34.5), "ESPIRITU", 1L);
        coord2 = new CoordenadaMongo(new GeoJsonPoint(-58.5, -34.6), "ESPIRITU", 2L);
        coordenadaDAO.saveAll(List.of(coord1, coord2));
    }

    @Test
    void testActualizarCoordenadasExistentes() {
        GeoJsonPoint nuevoPunto = new GeoJsonPoint(-59.0, -35.0);
        List<Long> ids = Arrays.asList(1L, 2L);

        List<CoordenadaMongo> antes = coordenadaDAO.findByEntityTypeAndEntityIdIn("ESPIRITU", ids);
        assertThat(antes).hasSize(2);
        assertThat(antes).extracting("punto").doesNotContain(nuevoPunto);

        coordenadaService.actualizarCoordenadas("ESPIRITU", ids, nuevoPunto);

        List<CoordenadaMongo> despues = coordenadaDAO.findByEntityTypeAndEntityIdIn("ESPIRITU", ids);
        assertThat(despues).hasSize(2);
        assertThat(despues).extracting("punto").allMatch(p -> p.equals(nuevoPunto));
    }

    @Test
    void testActualizarCoordenadaExistente() {
        GeoJsonPoint nuevoPunto = new GeoJsonPoint(-60.0, -35.0);

        coordenadaService.actualizarOCrearCoordenada("MEDIUM", 1L, nuevoPunto);

        Optional<CoordenadaMongo> actualizada = coordenadaDAO.findByEntityTypeAndEntityId("MEDIUM", 1L);
        assertThat(actualizada).isPresent();
        assertThat(actualizada.get().getPunto()).isEqualTo(nuevoPunto);
    }

    @Test
    void testActualizarCoordenadaCreaUnaNueva() {
        GeoJsonPoint nuevoPunto = new GeoJsonPoint(-60.0, -35.0);

        coordenadaService.actualizarOCrearCoordenada("MEDIUM", 55L, nuevoPunto);

        Optional<CoordenadaMongo> actualizada = coordenadaDAO.findByEntityTypeAndEntityId("MEDIUM", 55L);
        assertThat(actualizada).isPresent();
        assertThat(actualizada.get().getPunto()).isEqualTo(nuevoPunto);
    }

    @AfterEach
    void clear() {
        coordenadaDAO.deleteAll();
    }
}