package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            emailService.enviarEmailResetPassword(email, "TEST123TOKEN");
            return ResponseEntity.ok("Email enviado exitosamente a: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al enviar email: " + e.getMessage());
        }
    }

    @PostMapping("/confirmacion")
    public ResponseEntity<String> testConfirmacion(@RequestParam String email, @RequestParam String nombre) {
        try {
            emailService.enviarEmailConfirmacion(email, nombre);
            return ResponseEntity.ok("Email de confirmación enviado a: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/email-simple")
    public ResponseEntity<String> testEmailSimple(@RequestParam String email) {
        try {
            emailService.enviarEmailResetPassword(email, "TOKEN_DE_PRUEBA");
            return ResponseEntity.ok("Email enviado correctamente a " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
