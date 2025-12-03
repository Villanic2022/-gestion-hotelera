package com.miempresa.gestion_hotelera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordResetController {

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordPage(@RequestParam String token) {
        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Reset Password</title>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; max-width: 400px; margin: 50px auto; padding: 20px; }" +
                "input { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px; }" +
                "button { background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Restablecer Contraseña</h2>" +
                "<form action='/api/auth/reset-password' method='POST'>" +
                "<input type='hidden' name='token' value='" + token + "'>" +
                "<label>Nueva contraseña:</label>" +
                "<input type='password' name='newPassword' required>" +
                "<label>Confirmar contraseña:</label>" +
                "<input type='password' name='confirmPassword' required>" +
                "<button type='submit'>Cambiar Contraseña</button>" +
                "</form>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }
}
