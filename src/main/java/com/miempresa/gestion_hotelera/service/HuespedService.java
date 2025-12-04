package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.HuespedRequest;
import com.miempresa.gestion_hotelera.dto.HuespedResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Huesped;
import com.miempresa.gestion_hotelera.mapper.HuespedMapper;
import com.miempresa.gestion_hotelera.repository.HuespedRepository;
import com.miempresa.gestion_hotelera.security.TenantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HuespedService {

    private final HuespedRepository huespedRepository;
    private final HuespedMapper mapper;
    private final TenantUtil tenantUtil;

    // ===== HELPERS =====

    private Cliente getClienteActual() {
        return tenantUtil.getClienteActual();
    }

    private Huesped getHuespedDelClienteActual(Long id) {
        Cliente cliente = getClienteActual();
        return huespedRepository.findByIdAndCliente_Id(id, cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Huésped no encontrado"));
    }

    // ===== CREATE =====

    public HuespedResponse crear(HuespedRequest request) {
        Cliente cliente = getClienteActual();

        Huesped entity = mapper.toEntity(request);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setCliente(cliente);          // 👈 asociamos al cliente actual

        Huesped guardado = huespedRepository.save(entity);
        return mapper.toResponse(guardado);
    }

    // ===== READ (all) =====

    public List<HuespedResponse> listar() {
        Cliente cliente = getClienteActual();

        return huespedRepository.findByCliente_Id(cliente.getId())
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ===== READ (by ID) =====

    public HuespedResponse obtener(Long id) {
        Huesped entity = getHuespedDelClienteActual(id);
        return mapper.toResponse(entity);
    }

    // ===== UPDATE =====

    public HuespedResponse actualizar(Long id, HuespedRequest request) {
        Huesped entity = getHuespedDelClienteActual(id);

        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
        entity.setPais(request.getPais());
        entity.setTipoDocumento(request.getTipoDocumento());
        entity.setNumeroDocumento(request.getNumeroDocumento());
        entity.setNotas(request.getNotas());

        Huesped guardado = huespedRepository.save(entity);

        return mapper.toResponse(guardado);
    }

    // ===== DELETE =====

    public void eliminar(Long id) {
        Cliente cliente = getClienteActual();

        if (!huespedRepository.existsByIdAndCliente_Id(id, cliente.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Huésped no encontrado");
        }

        huespedRepository.deleteById(id);
    }
}
