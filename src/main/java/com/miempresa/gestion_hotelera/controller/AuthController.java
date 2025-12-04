package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.*;
import com.miempresa.gestion_hotelera.entity.PasswordResetToken;
import com.miempresa.gestion_hotelera.repository.PasswordResetTokenRepository;
import com.miempresa.gestion_hotelera.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetTokenRepository passwordResetTokenRepository; // 👈 AGREGAR

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // 👇 NUEVO
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    // 👇 NUEVO
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password-form")
    public ResponseEntity<String> processPasswordResetForm(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        try {
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                        .body("Las contraseñas no coinciden");
            }

            // Usar tu servicio existente
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setToken(token);
            request.setNewPassword(newPassword);

            authService.resetPassword(request);

            return ResponseEntity.ok("Contraseña actualizada exitosamente");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
    @GetMapping("/reset-password")
    public ResponseEntity<String> showPasswordResetForm(@RequestParam("token") String token) {
        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<h1>Token inválido</h1>");
        }

        if (resetToken.get().getExpiracion().isBefore(LocalDateTime.now()) || resetToken.get().getUsado()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<h1>Token expirado o ya utilizado</h1>");
        }

        // Construir HTML sin usar .formatted()
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='es'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Resetear Contraseña - Gestión Hotelera</title>");
        html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css' rel='stylesheet'>");
        html.append("<style>");
        html.append("body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }");
        html.append(".card { box-shadow: 0 15px 35px rgba(0,0,0,0.1); border: none; border-radius: 15px; }");
        html.append(".btn-primary { background: linear-gradient(45deg, #667eea, #764ba2); border: none; }");
        html.append(".btn-primary:hover { background: linear-gradient(45deg, #5a6fd8, #6a4190); }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='row justify-content-center align-items-center min-vh-100'>");
        html.append("<div class='col-md-6 col-lg-4'>");
        html.append("<div class='card'>");
        html.append("<div class='card-header text-center bg-white border-0 pt-4'>");
        html.append("<i class='bi bi-shield-lock text-primary' style='font-size: 3rem;'></i>");
        html.append("<h3 class='mt-3 text-primary'>Resetear Contraseña</h3>");
        html.append("<p class='text-muted'>Gestión Hotelera</p>");
        html.append("</div>");
        html.append("<div class='card-body p-4'>");
        html.append("<form method='post' action='/api/auth/reset-password-form'>");
        html.append("<input type='hidden' name='token' value='").append(token).append("'>");
        html.append("<div class='mb-3'>");
        html.append("<label for='newPassword' class='form-label'>");
        html.append("<i class='bi bi-key-fill me-2'></i>Nueva Contraseña");
        html.append("</label>");
        html.append("<input type='password' class='form-control form-control-lg' id='newPassword' name='newPassword' placeholder='Ingresa tu nueva contraseña' minlength='6' required>");
        html.append("<div class='form-text'>Mínimo 6 caracteres</div>");
        html.append("</div>");
        html.append("<div class='mb-4'>");
        html.append("<label for='confirmPassword' class='form-label'>");
        html.append("<i class='bi bi-key-fill me-2'></i>Confirmar Contraseña");
        html.append("</label>");
        html.append("<input type='password' class='form-control form-control-lg' id='confirmPassword' name='confirmPassword' placeholder='Confirma tu nueva contraseña' minlength='6' required>");
        html.append("</div>");
        html.append("<button type='submit' class='btn btn-primary btn-lg w-100 mb-3'>");
        html.append("<i class='bi bi-check-circle-fill me-2'></i>Cambiar Contraseña");
        html.append("</button>");
        html.append("</form>");
        html.append("</div>");
        html.append("<div class='card-footer text-center bg-white border-0 pb-4'>");
        html.append("<small class='text-muted'>¿Recordaste tu contraseña? ");
        html.append("<a href='/login' class='text-primary text-decoration-none'>Iniciar Sesión</a>");
        html.append("</small>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<script>");
        html.append("document.getElementById('confirmPassword').addEventListener('input', function() {");
        html.append("const password = document.getElementById('newPassword').value;");
        html.append("const confirmPassword = this.value;");
        html.append("if (password !== confirmPassword) {");
        html.append("this.setCustomValidity('Las contraseñas no coinciden');");
        html.append("} else {");
        html.append("this.setCustomValidity('');");
        html.append("}");
        html.append("});");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html.toString());
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no encontrado");
            }

            String token = authHeader.substring(7);
            // Aquí debes usar tu lógica de verificación JWT existente
            // Por ejemplo: jwtService.validateToken(token)

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no encontrado");
            }

            String token = authHeader.substring(7);
            // Obtener el usuario desde el token
            // Usuario usuario = jwtService.getUserFromToken(token);

            // Por ahora devolver algo simple
            return ResponseEntity.ok().body("Perfil del usuario");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // En este caso simple, el logout se maneja en el frontend
        // eliminando el token del localStorage
        return ResponseEntity.ok().body("Logout exitoso");
    }




}
