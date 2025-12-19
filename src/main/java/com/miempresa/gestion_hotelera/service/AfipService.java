package com.miempresa.gestion_hotelera.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AfipService {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AfipFacturaRequest {
        private String cuitEmisor;
        private int puntoVenta;
        private String tipoComprobante;   // "B", "C", etc.
        private String fecha;             // formato AFIP: yyyyMMdd
        private String moneda;            // "PES"
        private BigDecimal importe;
        private String tipoDocReceptor;   // "80", "96", "99"
        private String nroDocReceptor;
        private String nombreReceptor;    // 👈 AGREGAR ESTA LÍNEA
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AfipFacturaResponse {
        private String cae;
        private String caeVencimiento;    // yyyy-MM-dd
        private long numeroComprobante;
    }

    // 👇 Config desde application.properties
    @Value("${afip.api.base-url}")
    private String baseUrl;

    @Value("${afip.api.access-token}")
    private String accessToken;

    @Value("${afip.api.environment}")
    private String environment; // "dev" o "prod"

    @Value("${afip.api.tax-id}")
    private String taxId; // CUIT que usa AfipSDK (en dev, el de pruebas)

    @Value("${afip.api.wsid}")
    private String wsid; // "wsfe"

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final Gson gson = new Gson();

    // -------- Helpers --------

    /** Limpia el CUIT dejando solo números */
    private String limpiarCuit(String cuit) {
        if (cuit == null) return null;
        return cuit.replaceAll("\\D", ""); // elimina todo lo que NO es dígito
    }

    // -------- DEMO --------

    /**
     * Demo: simula la respuesta de AFIP.
     * La dejamos por si la API real falla, para no romper el flujo.
     */
    public AfipFacturaResponse emitirFacturaDemo(AfipFacturaRequest req) {
        System.out.println("Emitiendo factura DEMO para CUIT " + req.getCuitEmisor()
                + " por importe " + req.getImporte()
                + " doc receptor " + req.getTipoDocReceptor() + "-" + req.getNroDocReceptor());

        return AfipFacturaResponse.builder()
                .cae("00000000000001")
                .caeVencimiento(
                        LocalDate.now().plusDays(10)
                                .format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                .numeroComprobante(1L)
                .build();
    }

    // -------- REAL AFIP / AfipSDK --------

    /**
     * Implementación real contra AfipSDK / ARCA.
     */
    public AfipFacturaResponse emitirFacturaAfip(AfipFacturaRequest req) throws Exception {
        // 1) Obtener Token/Sign para WSFE
        Map<String, Object> auth = obtenerAuth();

        String token = (String) auth.get("Token");
        String sign = (String) auth.get("Sign");

        // 2) Obtener próximo número de comprobante
        int nextNumber = obtenerProximoNumeroComprobante(token, sign, req);

        // 3) Crear factura FECAESolicitar
        return crearFacturaAfip(token, sign, req, nextNumber);
    }

    /**
     * POST /auth para obtener Token y Sign (AfipSDK).
     */
    private Map<String, Object> obtenerAuth() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("environment", environment); // "dev" o "prod"

        String cuitLimpio = limpiarCuit(taxId);
        body.put("tax_id", Long.parseLong(cuitLimpio));

        body.put("wsid", wsid); // "wsfe"

        String json = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/auth"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("AFIP AUTH status: " + response.statusCode());
        System.out.println("AFIP AUTH body: " + response.body());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error AFIP AUTH: " + response.body());
        }

        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(response.body(), type);

        // AfipSDK suele devolver { "token": "...", "sign": "...", ... }
        return Map.of(
                "Token", data.get("token"),
                "Sign", data.get("sign")
        );
    }

    /**
     * FECompUltimoAutorizado - obtener el último número de comprobante autorizado,
     * para saber cuál es el próximo.
     */
    private int obtenerProximoNumeroComprobante(String token, String sign, AfipFacturaRequest req)
            throws Exception {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("environment", environment);
        payload.put("wsid", wsid);
        payload.put("method", "FECompUltimoAutorizado");

        Map<String, Object> params = new LinkedHashMap<>();
        payload.put("params", params);

        Map<String, Object> auth = new LinkedHashMap<>();
        auth.put("Token", token);
        auth.put("Sign", sign);
        auth.put("Cuit", Long.parseLong(limpiarCuit(taxId))); // o req.getCuitEmisor()

        params.put("Auth", auth);
        params.put("PtoVta", req.getPuntoVenta());
        params.put("CbteTipo", mapTipoComprobanteToCbteTipo(req.getTipoComprobante()));

        String json = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/requests"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("AFIP UltimoComp status: " + response.statusCode());
        System.out.println("AFIP UltimoComp body: " + response.body());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error AFIP FECompUltimoAutorizado: " + response.body());
        }

        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(response.body(), type);

        Map<String, Object> result =
                (Map<String, Object>) data.get("FECompUltimoAutorizadoResult");
        if (result == null) {
            throw new RuntimeException("Respuesta inválida de FECompUltimoAutorizado: " + response.body());
        }

        Number cbteNro = (Number) result.get("CbteNro");
        int lastNumber = cbteNro != null ? cbteNro.intValue() : 0;
        return lastNumber + 1;
    }

    /**
     * FECAESolicitar - crea la factura y devuelve CAE.
     */
    private AfipFacturaResponse crearFacturaAfip(String token,
                                                 String sign,
                                                 AfipFacturaRequest req,
                                                 int nextVoucherNumber) throws Exception {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("environment", environment);
        payload.put("wsid", wsid);
        payload.put("method", "FECAESolicitar");

        Map<String, Object> params = new LinkedHashMap<>();
        payload.put("params", params);

        Map<String, Object> auth = new LinkedHashMap<>();
        auth.put("Token", token);
        auth.put("Sign", sign);
        auth.put("Cuit", Long.parseLong(limpiarCuit(taxId))); // o req.getCuitEmisor()

        params.put("Auth", auth);

        Map<String, Object> feCAEReq = new LinkedHashMap<>();
        params.put("FeCAEReq", feCAEReq);

        Map<String, Object> feCabReq = new LinkedHashMap<>();
        feCAEReq.put("FeCabReq", feCabReq);
        feCabReq.put("CantReg", 1);
        feCabReq.put("PtoVta", req.getPuntoVenta());
        feCabReq.put("CbteTipo", mapTipoComprobanteToCbteTipo(req.getTipoComprobante()));

        Map<String, Object> feDetReq = new LinkedHashMap<>();
        feCAEReq.put("FeDetReq", feDetReq);

        Map<String, Object> feCAERequest = new LinkedHashMap<>();
        feDetReq.put("FECAEDetRequest", feCAERequest);

        feCAERequest.put("Concepto", 1); // 1 = Productos
        feCAERequest.put("DocTipo", Integer.valueOf(req.getTipoDocReceptor()));
        feCAERequest.put("DocNro", Long.parseLong(req.getNroDocReceptor()));
        feCAERequest.put("CbteDesde", nextVoucherNumber);
        feCAERequest.put("CbteHasta", nextVoucherNumber);
        feCAERequest.put("CbteFch", Integer.parseInt(req.getFecha())); // yyyyMMdd

        // IVA simplificado: todo no gravado (para no mandar AlicIvas todavía)
        BigDecimal total = req.getImporte();
        feCAERequest.put("ImpTotal", total);
        feCAERequest.put("ImpTotConc", total);
        feCAERequest.put("ImpNeto", 0);
        feCAERequest.put("ImpOpEx", 0);
        feCAERequest.put("ImpIVA", 0);
        feCAERequest.put("ImpTrib", 0);
        feCAERequest.put("MonId", req.getMoneda());   // "PES"
        feCAERequest.put("MonCotiz", 1);

        // Condición IVA receptor: 5 = Consumidor Final
        feCAERequest.put("CondicionIVAReceptorId", 5);

        String json = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/requests"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("AFIP FECAESolicitar status: " + response.statusCode());
        System.out.println("AFIP FECAESolicitar body: " + response.body());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error AFIP FECAESolicitar: " + response.body());
        }

        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(response.body(), type);

        // ✅ Parsear donde realmente están los datos:
        Map<String, Object> feCAESolicitarResult =
                (Map<String, Object>) data.get("FECAESolicitarResult");
        Map<String, Object> feDetResp =
                (Map<String, Object>) feCAESolicitarResult.get("FeDetResp");
        var detList = (java.util.List<Map<String, Object>>) feDetResp.get("FECAEDetResponse");
        Map<String, Object> det0 = detList.get(0);

        String resultado = (String) det0.get("Resultado");
        String cae = (String) det0.get("CAE");
        String caeFchVto = (String) det0.get("CAEFchVto");

        Number cbteDesde = (Number) det0.get("CbteDesde");
        long numeroComprobante = cbteDesde != null ? cbteDesde.longValue() : nextVoucherNumber;

        System.out.println("Resultado detalle AFIP: " + resultado + ", CAE=" + cae);

        if (!"A".equalsIgnoreCase(resultado) || cae == null || cae.isEmpty()) {
            // No tiramos excepción, solo logueamos; FacturaService puede hacer fallback a DEMO
            System.err.println("Factura no aprobada por AFIP. Respuesta: " + response.body());

            return AfipFacturaResponse.builder()
                    .cae(null)
                    .caeVencimiento(null)
                    .numeroComprobante(numeroComprobante)
                    .build();
        }

        return AfipFacturaResponse.builder()
                .cae(cae)
                .caeVencimiento(
                        (caeFchVto != null && caeFchVto.length() == 8)
                                ? LocalDate.parse(caeFchVto, DateTimeFormatter.BASIC_ISO_DATE)
                                .format(DateTimeFormatter.ISO_LOCAL_DATE)
                                : caeFchVto
                )
                .numeroComprobante(numeroComprobante)
                .build();
    }

    private int mapTipoComprobanteToCbteTipo(String tipo) {
        if (tipo == null) return 6; // Factura B por defecto

        return switch (tipo.toUpperCase()) {
            case "A" -> 1;
            case "B" -> 6;
            case "C" -> 11;
            default -> 6;
        };
    }
}
