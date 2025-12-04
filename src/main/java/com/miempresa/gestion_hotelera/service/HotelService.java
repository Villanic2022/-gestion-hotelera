package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.HotelRequest;
import com.miempresa.gestion_hotelera.dto.HotelResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.mapper.HotelMapper;
import com.miempresa.gestion_hotelera.repository.HotelRepository;
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

public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final TenantUtil tenantUtil;

    // Helper: trae el hotel del cliente actual o tira 404
    private Hotel getHotelDelClienteActual(Long id) {
        Cliente cliente = tenantUtil.getClienteActual();
        return hotelRepository.findByIdAndCliente_Id(id, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel no encontrado"));
    }

    // CREATE
    public HotelResponse crear(HotelRequest request) {
        Cliente cliente = tenantUtil.getClienteActual();

        Hotel hotel = hotelMapper.toEntity(request);
        hotel.setCliente(cliente);  // 👈 el hotel “pertenece” a ese cliente

        Hotel guardado = hotelRepository.save(hotel);
        return hotelMapper.toResponse(guardado);
    }

    // READ (all) - solo hoteles del cliente actual
    public List<HotelResponse> listar() {
        Cliente cliente = tenantUtil.getClienteActual();

        return hotelRepository.findByCliente_Id(cliente.getId())
                .stream()
                .map(hotelMapper::toResponse)
                .collect(Collectors.toList());
    }

    // READ (by ID)
    public HotelResponse obtenerPorId(Long id) {
        Hotel hotel = getHotelDelClienteActual(id);
        return hotelMapper.toResponse(hotel);
    }

    // UPDATE
    public HotelResponse actualizar(Long id, HotelRequest request) {
        Hotel hotel = getHotelDelClienteActual(id);

        hotel.setNombre(request.getNombre());
        hotel.setDireccion(request.getDireccion());
        hotel.setCiudad(request.getCiudad());
        hotel.setPais(request.getPais());
        hotel.setTelefono(request.getTelefono());
        hotel.setEmail(request.getEmail());
        hotel.setActivo(request.getActivo());

        Hotel guardado = hotelRepository.save(hotel);
        return hotelMapper.toResponse(guardado);
    }

    // DELETE
    public void eliminar(Long id) {
        Cliente cliente = tenantUtil.getClienteActual();

        if (!hotelRepository.existsByIdAndCliente_Id(id, cliente.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel no encontrado");
        }
        hotelRepository.deleteById(id);
    }
}
