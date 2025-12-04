package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.DisponibilidadHotelResponse;
import com.miempresa.gestion_hotelera.dto.HabitacionResponse;
import com.miempresa.gestion_hotelera.dto.PagoResponse;
import com.miempresa.gestion_hotelera.dto.PagoSeniaRequest;
import com.miempresa.gestion_hotelera.dto.PagosReservaResponse;
import com.miempresa.gestion_hotelera.dto.ReservaCreateRequest;
import com.miempresa.gestion_hotelera.dto.ReservaResponse;
import com.miempresa.gestion_hotelera.entity.*;
import com.miempresa.gestion_hotelera.mapper.HabitacionMapper;
import com.miempresa.gestion_hotelera.mapper.ReservaMapper;
import com.miempresa.gestion_hotelera.repository.*;
import com.miempresa.gestion_hotelera.security.TenantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HotelRepository hotelRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final HuespedRepository huespedRepository;
    private final ReservaHuespedRepository reservaHuespedRepository;
    private final ReservaMapper reservaMapper;
    private final HabitacionMapper habitacionMapper;
    private final PagoRepository pagoRepository;
    private final TenantUtil tenantUtil;

    // ======== HELPERS MULTI-TENANT =========

    private Cliente getClienteActual() {
        return tenantUtil.getClienteActual();
    }

    private Hotel getHotelDelClienteActual(Long hotelId) {
        Cliente cliente = getClienteActual();
        return hotelRepository.findByIdAndCliente_Id(hotelId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel no encontrado"));
    }

    private Reserva getReservaDelClienteActual(Long reservaId) {
        Cliente cliente = getClienteActual();
        return reservaRepository.findById(reservaId)
                .filter(r -> r.getHotel() != null &&
                        r.getHotel().getCliente() != null &&
                        cliente.getId().equals(r.getHotel().getCliente().getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    }

    private Habitacion getHabitacionDelClienteActual(Long habitacionId) {
        Cliente cliente = getClienteActual();
        return habitacionRepository.findById(habitacionId)
                .filter(h -> h.getHotel() != null &&
                        h.getHotel().getCliente() != null &&
                        cliente.getId().equals(h.getHotel().getCliente().getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Habitación no encontrada"));
    }

    // ========= CREAR RESERVA =========
    @Transactional
    public ReservaResponse crear(ReservaCreateRequest req) {

        if (req.getCheckIn() == null || req.getCheckOut() == null ||
                !req.getCheckIn().isBefore(req.getCheckOut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas de check-in/out inválidas");
        }

        // hotel del cliente actual
        Hotel hotel = getHotelDelClienteActual(req.getHotelId());

        TipoHabitacion tipo = tipoHabitacionRepository.findById(req.getTipoHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de habitación no encontrado"));

        // Validar que el tipo de habitación pertenece al mismo hotel
        if (!tipo.getHotel().getId().equals(hotel.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de habitación no pertenece al hotel indicado");
        }

        Habitacion habitacion = null;
        if (req.getHabitacionId() != null) {
            habitacion = getHabitacionDelClienteActual(req.getHabitacionId());

            // Verificar que la habitación sea del mismo hotel
            if (!habitacion.getHotel().getId().equals(hotel.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La habitación no pertenece al hotel indicado");
            }

            // Verificar superposición
            List<String> estadosActivos = List.of(
                    EstadoReserva.PENDIENTE.name(),
                    EstadoReserva.CONFIRMADA.name(),
                    EstadoReserva.CHECKIN.name()
            );

            boolean existeSuperposicion = reservaRepository.existeSuperposicionReserva(
                    habitacion.getId(),
                    estadosActivos,
                    req.getCheckIn(),
                    req.getCheckOut()
            );
            if (existeSuperposicion) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La habitación ya está reservada en ese rango");
            }
        }

        Huesped titular = huespedRepository.findById(req.getHuespedTitularId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Huésped titular no encontrado"));

        List<Huesped> acompanantes = req.getAcompanianteIds() == null
                ? List.of()
                : req.getAcompanianteIds().stream()
                .map(id -> huespedRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Huésped acompañante no encontrado: " + id)))
                .collect(Collectors.toList());

        Reserva reserva = reservaMapper.toEntity(req, hotel, tipo, habitacion);

        // Estado inicial por defecto (por ahora lo dejamos así, o PENDIENTE según tu lógica)
        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }

        reserva.setCreadoEn(LocalDateTime.now());
        reserva.setActualizadoEn(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);

        // vincular huésped titular + acompañantes
        ReservaHuesped rhTitular = ReservaHuesped.builder()
                .reserva(guardada)
                .huesped(titular)
                .esTitular(true)
                .build();
        reservaHuespedRepository.save(rhTitular);

        for (Huesped acomp : acompanantes) {
            ReservaHuesped rh = ReservaHuesped.builder()
                    .reserva(guardada)
                    .huesped(acomp)
                    .esTitular(false)
                    .build();
            reservaHuespedRepository.save(rh);
        }

        List<Long> acompanianteIds = acompanantes.stream()
                .map(Huesped::getId)
                .collect(Collectors.toList());

        return reservaMapper.toResponse(guardada, titular.getId(), acompanianteIds);
    }

    // ========= LISTAR / OBTENER =========

    public List<ReservaResponse> listar() {
        Cliente cliente = getClienteActual();

        return reservaRepository.findByHotel_Cliente_Id(cliente.getId()).stream()
                .map(reserva -> {
                    List<ReservaHuesped> vinculos = reservaHuespedRepository.findAll()
                            .stream()
                            .filter(rh -> rh.getReserva().getId().equals(reserva.getId()))
                            .collect(Collectors.toList());

                    Long titularId = vinculos.stream()
                            .filter(ReservaHuesped::getEsTitular)
                            .map(rh -> rh.getHuesped().getId())
                            .findFirst()
                            .orElse(null);

                    List<Long> acompIds = vinculos.stream()
                            .filter(rh -> Boolean.FALSE.equals(rh.getEsTitular()))
                            .map(rh -> rh.getHuesped().getId())
                            .collect(Collectors.toList());

                    return reservaMapper.toResponse(reserva, titularId, acompIds);
                })
                .collect(Collectors.toList());
    }

    public ReservaResponse obtener(Long id) {
        Reserva reserva = getReservaDelClienteActual(id);

        List<ReservaHuesped> vinculos = reservaHuespedRepository.findAll()
                .stream()
                .filter(rh -> rh.getReserva().getId().equals(reserva.getId()))
                .collect(Collectors.toList());

        Long titularId = vinculos.stream()
                .filter(ReservaHuesped::getEsTitular)
                .map(rh -> rh.getHuesped().getId())
                .findFirst()
                .orElse(null);

        List<Long> acompIds = vinculos.stream()
                .filter(rh -> Boolean.FALSE.equals(rh.getEsTitular()))
                .map(rh -> rh.getHuesped().getId())
                .collect(Collectors.toList());

        return reservaMapper.toResponse(reserva, titularId, acompIds);
    }

    // ========= CAMBIOS DE ESTADO =========
    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = getReservaDelClienteActual(id);

        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setActualizadoEn(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);
        return obtener(guardada.getId());
    }
    @Transactional
    public ReservaResponse checkin(Long id) {
        Reserva reserva = getReservaDelClienteActual(id);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo reservas CONFIRMADAS pueden hacer check-in"
            );
        }

        reserva.setEstado(EstadoReserva.CHECKIN);
        reserva.setActualizadoEn(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);
        return obtener(guardada.getId());
    }
    @Transactional
    public ReservaResponse checkout(Long id) {
        Reserva reserva = getReservaDelClienteActual(id);

        if (reserva.getEstado() != EstadoReserva.CHECKIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo reservas en CHECKIN pueden hacer check-out"
            );
        }

        reserva.setEstado(EstadoReserva.CHECKOUT);
        reserva.setActualizadoEn(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);
        return obtener(guardada.getId());
    }

    // ========= DISPONIBILIDAD BÁSICA (por habitación) =========
    @Transactional
    public boolean habitacionDisponible(Long habitacionId, LocalDate checkIn, LocalDate checkOut) {
        Habitacion habitacion = getHabitacionDelClienteActual(habitacionId);

        List<String> estadosActivos = List.of(
                EstadoReserva.PENDIENTE.name(),
                EstadoReserva.CONFIRMADA.name(),
                EstadoReserva.CHECKIN.name()
        );
        return !reservaRepository.existeSuperposicionReserva(
                habitacion.getId(),
                estadosActivos,
                checkIn,
                checkOut
        );
    }

    // ========= DISPONIBILIDAD POR HOTEL (lista de habitaciones) =========
    public List<HabitacionResponse> disponibilidadHotel(Long hotelId,
                                                        LocalDate checkIn,
                                                        LocalDate checkOut) {

        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas de check-in/out inválidas");
        }

        Hotel hotel = getHotelDelClienteActual(hotelId);

        // Traer todas las habitaciones del hotel
        List<Habitacion> habitaciones = habitacionRepository.findByHotel_Id(hotel.getId());

        List<String> estadosActivos = List.of(
                EstadoReserva.PENDIENTE.name(),
                EstadoReserva.CONFIRMADA.name(),
                EstadoReserva.CHECKIN.name()
        );

        // Filtrar solo las que NO tienen superposición
        List<Habitacion> disponibles = habitaciones.stream()
                .filter(h -> !reservaRepository.existeSuperposicionReserva(
                        h.getId(),
                        estadosActivos,
                        checkIn,
                        checkOut
                ))
                .collect(Collectors.toList());

        // Mapear a DTO
        return disponibles.stream()
                .map(habitacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public DisponibilidadHotelResponse disponibilidadHotelResumen(Long hotelId,
                                                                  LocalDate checkIn,
                                                                  LocalDate checkOut) {
        List<HabitacionResponse> libres = disponibilidadHotel(hotelId, checkIn, checkOut);

        return DisponibilidadHotelResponse.builder()
                .tieneDisponibilidad(!libres.isEmpty())
                .cantidadDisponibles(libres.size())
                .habitaciones(libres)
                .build();
    }

    // ========= REGISTRAR SEÑA =========
    @Transactional
    public ReservaResponse registrarSenia(Long reservaId, PagoSeniaRequest req) {

        Reserva reserva = getReservaDelClienteActual(reservaId);

        if (reserva.getEstado() == EstadoReserva.CANCELADA ||
                reserva.getEstado() == EstadoReserva.CHECKOUT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar seña para una reserva cancelada o finalizada"
            );
        }

        // Crear pago
        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(req.getMonto())
                .moneda(req.getMoneda() != null ? req.getMoneda() : reserva.getMoneda())
                .metodo(req.getMetodo())
                .pagadoPorCanal(false)
                .referenciaPago(req.getReferenciaPago())
                .fechaPago(LocalDateTime.now())
                .build();

        pagoRepository.save(pago);

        // Calcular total pagado
        List<Pago> pagos = pagoRepository.findByReserva_Id(reservaId);

        BigDecimal totalPagado = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal precioTotal = reserva.getPrecioTotal() != null
                ? reserva.getPrecioTotal()
                : BigDecimal.ZERO;

        // ===== ACTUALIZAR ESTADO DE PAGO =====
        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            reserva.setEstadoPago(EstadoPago.PENDIENTE);
        } else if (precioTotal.compareTo(BigDecimal.ZERO) > 0 &&
                totalPagado.compareTo(precioTotal) >= 0) {
            // pago total
            reserva.setEstadoPago(EstadoPago.PAGADO);
        } else {
            // hay algo de plata pero no todo
            reserva.setEstadoPago(EstadoPago.SENIADO);
        }

        // ===== ACTUALIZAR ESTADO DE RESERVA =====
        // Regla de negocio: si hay seña (totalPagado > 0), la reserva queda CONFIRMADA
        if (totalPagado.compareTo(BigDecimal.ZERO) > 0 &&
                reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
        }

        reserva.setActualizadoEn(LocalDateTime.now());
        reservaRepository.save(reserva);

        return obtener(reserva.getId());
    }

    // ========= LISTAR PAGOS =========
    @Transactional
    public PagosReservaResponse listarPagosReserva(Long reservaId) {

        Reserva reserva = getReservaDelClienteActual(reservaId);

        List<Pago> pagos = pagoRepository.findByReserva_Id(reservaId);

        BigDecimal totalPagado = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal precioTotal = reserva.getPrecioTotal() != null
                ? reserva.getPrecioTotal()
                : BigDecimal.ZERO;

        BigDecimal saldoPendiente = precioTotal.subtract(totalPagado);
        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            saldoPendiente = BigDecimal.ZERO;
        }

        List<PagoResponse> pagoResponses = pagos.stream()
                .map(p -> PagoResponse.builder()
                        .id(p.getId())
                        .monto(p.getMonto())
                        .moneda(p.getMoneda())
                        .metodo(p.getMetodo())
                        .pagadoPorCanal(Boolean.TRUE.equals(p.getPagadoPorCanal()))
                        .referenciaPago(p.getReferenciaPago())
                        .fechaPago(p.getFechaPago())
                        .build()
                )
                .collect(Collectors.toList());

        return PagosReservaResponse.builder()
                .reservaId(reserva.getId())
                .totalPagado(totalPagado)
                .precioTotal(precioTotal)
                .saldoPendiente(saldoPendiente)
                .estadoPago(reserva.getEstadoPago() != null ? reserva.getEstadoPago().name() : null)
                .pagos(pagoResponses)
                .build();
    }
}
