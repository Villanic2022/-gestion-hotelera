package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.HabitacionRequest;
import com.miempresa.gestion_hotelera.dto.HabitacionResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Habitacion;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.entity.TipoHabitacion;
import com.miempresa.gestion_hotelera.mapper.HabitacionMapper;
import com.miempresa.gestion_hotelera.repository.HabitacionRepository;
import com.miempresa.gestion_hotelera.repository.HotelRepository;
import com.miempresa.gestion_hotelera.repository.TipoHabitacionRepository;
import com.miempresa.gestion_hotelera.security.TenantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HotelRepository hotelRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionMapper habitacionMapper;
    private final TenantUtil tenantUtil;

    // ===== HELPERS =====

    private Cliente getClienteActual() {
        return tenantUtil.getClienteActual();
    }

    private Hotel getHotelDelClienteActual(Long hotelId) {
        Cliente cliente = getClienteActual();
        return hotelRepository.findByIdAndCliente_Id(hotelId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel no encontrado"));
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

    // ===== CREATE =====
    @Transactional
    public HabitacionResponse crear(HabitacionRequest request) {
        // aseguramos que el hotel pertenece al cliente actual
        Hotel hotel = getHotelDelClienteActual(request.getHotelId());

        TipoHabitacion tipo = tipoHabitacionRepository.findById(request.getTipoHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tipo de habitación no encontrado"));

        // opcional: asegurarse que el tipo corresponde al mismo hotel
        if (!tipo.getHotel().getId().equals(hotel.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de habitación no pertenece al hotel indicado");
        }

        Habitacion habitacion = habitacionMapper.toEntity(request, hotel, tipo);
        Habitacion guardada = habitacionRepository.save(habitacion);

        return habitacionMapper.toResponse(guardada);
    }

    // ===== READ (all) =====
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listar() {
        Cliente cliente = getClienteActual();

        return habitacionRepository.findByHotel_Cliente_Id(cliente.getId())
                .stream()
                .map(habitacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ===== READ (by ID) =====
    @Transactional(readOnly = true)
    public HabitacionResponse obtener(Long id) {
        Habitacion habitacion = getHabitacionDelClienteActual(id);
        return habitacionMapper.toResponse(habitacion);
    }

    // ===== UPDATE =====
    @Transactional
    public HabitacionResponse actualizar(Long id, HabitacionRequest request) {
        Habitacion habitacion = getHabitacionDelClienteActual(id);

        // si quieren cambiar de hotel o tipo, validamos también
        Hotel hotel = getHotelDelClienteActual(request.getHotelId());

        TipoHabitacion tipo = tipoHabitacionRepository.findById(request.getTipoHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tipo de habitación no encontrado"));

        if (!tipo.getHotel().getId().equals(hotel.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de habitación no pertenece al hotel indicado");
        }

        habitacion.setHotel(hotel);
        habitacion.setTipoHabitacion(tipo);
        habitacion.setCodigo(request.getCodigo());
        habitacion.setPiso(request.getPiso());
        habitacion.setEstado(request.getEstado());
        habitacion.setActivo(request.getActivo());

        Habitacion guardada = habitacionRepository.save(habitacion);
        return habitacionMapper.toResponse(guardada);
    }

    // ===== DELETE =====
    @Transactional
    public void eliminar(Long id) {
        Habitacion habitacion = getHabitacionDelClienteActual(id);
        habitacionRepository.delete(habitacion);
    }
}
