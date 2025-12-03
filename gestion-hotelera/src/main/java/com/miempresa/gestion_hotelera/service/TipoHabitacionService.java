package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.TipoHabitacionRequest;
import com.miempresa.gestion_hotelera.dto.TipoHabitacionResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.entity.TipoHabitacion;
import com.miempresa.gestion_hotelera.mapper.TipoHabitacionMapper;
import com.miempresa.gestion_hotelera.repository.HotelRepository;
import com.miempresa.gestion_hotelera.repository.TipoHabitacionRepository;
import com.miempresa.gestion_hotelera.security.TenantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HotelRepository hotelRepository;
    private final TipoHabitacionMapper mapper;
    private final TenantUtil tenantUtil;

    // ===== HELPERS MULTI-TENANT =====

    private Cliente getClienteActual() {
        return tenantUtil.getClienteActual();
    }

    private Hotel getHotelDelClienteActual(Long hotelId) {
        Cliente cliente = getClienteActual();
        return hotelRepository.findByIdAndCliente_Id(hotelId, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel no encontrado"));
    }

    private TipoHabitacion getTipoDelClienteActual(Long id) {
        Cliente cliente = getClienteActual();
        return tipoHabitacionRepository.findByIdAndHotel_Cliente_Id(id, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tipo de habitación no encontrado"));
    }

    // ===== CREATE =====

    public TipoHabitacionResponse crear(TipoHabitacionRequest request) {

        // El hotel DEBE ser del cliente actual
        Hotel hotel = getHotelDelClienteActual(request.getHotelId());

        TipoHabitacion entity = mapper.toEntity(request, hotel);
        TipoHabitacion guardado = tipoHabitacionRepository.save(entity);

        return mapper.toResponse(guardado);
    }

    // ===== READ (all) =====

    public List<TipoHabitacionResponse> listar() {
        Cliente cliente = getClienteActual();

        return tipoHabitacionRepository.findByHotel_Cliente_Id(cliente.getId())
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ===== READ (by ID) =====

    public TipoHabitacionResponse obtener(Long id) {
        TipoHabitacion entity = getTipoDelClienteActual(id);
        return mapper.toResponse(entity);
    }

    // ===== UPDATE =====

    public TipoHabitacionResponse actualizar(Long id, TipoHabitacionRequest request) {

        TipoHabitacion entity = getTipoDelClienteActual(id);

        // Hotel nuevo (si lo cambia) también debe ser del cliente actual
        Hotel hotel = getHotelDelClienteActual(request.getHotelId());

        entity.setHotel(hotel);
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setCapacidadBase(request.getCapacidadBase());
        entity.setCapacidadMax(request.getCapacidadMax());
        entity.setActivo(request.getActivo());

        TipoHabitacion guardado = tipoHabitacionRepository.save(entity);
        return mapper.toResponse(guardado);
    }

    // ===== DELETE =====

    public void eliminar(Long id) {
        Cliente cliente = getClienteActual();

        boolean existe = tipoHabitacionRepository.findByIdAndHotel_Cliente_Id(id, cliente.getId()).isPresent();
        if (!existe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de habitación no encontrado");
        }

        tipoHabitacionRepository.deleteById(id);
    }
}
