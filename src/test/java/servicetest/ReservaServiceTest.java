package servicetest;

import com.miempresa.gestion_hotelera.dto.ReservaCreateRequest;
import com.miempresa.gestion_hotelera.entity.*;
import com.miempresa.gestion_hotelera.repository.*;
import com.miempresa.gestion_hotelera.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ReservaServiceTest {

    @Autowired private ReservaService reservaService;

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private TipoHabitacionRepository tipoHabitacionRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private HuespedRepository huespedRepository;

    private Hotel hotel;
    private TipoHabitacion tipo;
    private Habitacion habitacion;
    private Huesped huespedTitular;

    @BeforeEach
    void setUp() {
        // ===== Crear cliente + usuario y simular login =====
        Cliente cliente = Cliente.builder()
                .nombre("Cliente Test")
                .email("cliente@test.com")
                .activo(true)
                .build();
        clienteRepository.save(cliente);

        Rol rolRecepcion = rolRepository.findByNombre("RECEPCION")
                .orElseGet(() -> rolRepository.save(Rol.builder().nombre("RECEPCION").build()));

        Usuario usuario = Usuario.builder()
                .usuario("testuser")
                .passwordHash("dummy")
                .nombre("Test")
                .apellido("User")
                .email("testuser@test.com")
                .activo(true)
                .cliente(cliente)
                .roles(Set.of(rolRecepcion))
                .build();
        usuarioRepository.save(usuario);

        // Simular usuario logueado para TenantUtil
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        usuario.getUsuario(), null, List.of())
        );

        // ===== Crear hotel del cliente =====
        hotel = Hotel.builder()
                .nombre("Hotel Test")
                .ciudad("Ciudad")
                .pais("Pais")
                .activo(true)
                .cliente(cliente)
                .build();
        hotelRepository.save(hotel);

        // ===== Crear tipo de habitación =====
        tipo = TipoHabitacion.builder()
                .hotel(hotel)
                .nombre("Cabaña")
                .descripcion("Cabaña familiar")
                .capacidadBase(2)
                .capacidadMax(4)
                .activo(true)
                .build();
        tipoHabitacionRepository.save(tipo);

        // ===== Crear habitación =====
        habitacion = Habitacion.builder()
                .hotel(hotel)
                .tipoHabitacion(tipo)
                .codigo("CAB-1")
                .piso("PB")
                .estado("DISPONIBLE")
                .activo(true)
                .build();
        habitacionRepository.save(habitacion);

        // ===== Crear huésped titular =====
        huespedTitular = Huesped.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .build();
        huespedTitular = huespedRepository.save(huespedTitular);
    }

    private ReservaCreateRequest buildReservaRequest(LocalDate checkIn, LocalDate checkOut) {
        ReservaCreateRequest req = new ReservaCreateRequest();
        req.setHotelId(hotel.getId());
        req.setTipoHabitacionId(tipo.getId());
        req.setHabitacionId(habitacion.getId());
        req.setHuespedTitularId(huespedTitular.getId());
        req.setAcompanianteIds(List.of());
        req.setCheckIn(checkIn);
        req.setCheckOut(checkOut);
        req.setAdultos(2);
        req.setNinos(0);
        req.setCanal("DIRECTO");
        req.setPrecioTotal(BigDecimal.valueOf(100000));
        req.setMoneda("ARS");
        req.setComentariosCliente("Test reserva");
        return req;
    }

    @Test
    void noDebePermitirReservasSolapadasEnLaMismaHabitacion() {
        // Reserva 1: 10 al 15 de enero
        ReservaCreateRequest req1 = buildReservaRequest(
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
        );
        reservaService.crear(req1);

        // Reserva 2 solapada: 12 al 18 de enero
        ReservaCreateRequest req2 = buildReservaRequest(
                LocalDate.of(2025, 1, 12),
                LocalDate.of(2025, 1, 18)
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> reservaService.crear(req2)
        );

        assertThat(ex.getStatusCode().value()).isEqualTo(409); // CONFLICT
    }

    @Test
    void habitacionDisponibleDebeSerFalseCuandoHayReservaSolapada() {
        // Creamos una reserva
        ReservaCreateRequest req = buildReservaRequest(
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
        );
        reservaService.crear(req);

        boolean disponible = reservaService.habitacionDisponible(
                habitacion.getId(),
                LocalDate.of(2025, 1, 12),
                LocalDate.of(2025, 1, 13)
        );

        assertThat(disponible).isFalse();
    }

    @Test
    void habitacionDisponibleDebeSerTrueCuandoNoHaySuperposicion() {
        // Reserva del 10 al 15
        ReservaCreateRequest req = buildReservaRequest(
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15)
        );
        reservaService.crear(req);

        // Consultamos una ventana totalmente fuera del rango
        boolean disponible = reservaService.habitacionDisponible(
                habitacion.getId(),
                LocalDate.of(2025, 1, 16),
                LocalDate.of(2025, 1, 18)
        );

        assertThat(disponible).isTrue();
    }
}
