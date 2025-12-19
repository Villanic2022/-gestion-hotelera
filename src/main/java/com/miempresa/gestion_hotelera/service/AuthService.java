package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.*;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.PasswordResetToken;
import com.miempresa.gestion_hotelera.entity.Rol;
import com.miempresa.gestion_hotelera.entity.Usuario;
import com.miempresa.gestion_hotelera.repository.ClienteRepository;
import com.miempresa.gestion_hotelera.repository.PasswordResetTokenRepository;
import com.miempresa.gestion_hotelera.repository.RolRepository;
import com.miempresa.gestion_hotelera.repository.UsuarioRepository;
import com.miempresa.gestion_hotelera.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ClienteRepository clienteRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;

    // ==================== LOGIN ====================

    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsuario(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Buscar Usuario en nuestra base con el cliente cargado
        Usuario usuario = usuarioRepository.findByUsuarioWithCliente(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        // 1) Usuario desactivado → no entra
        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario inactivo. Contactá al administrador del sistema."
            );
        }

        // 2) Cliente desactivado → cuenta suspendida
        Cliente cliente = usuario.getCliente();
        if (cliente == null || Boolean.FALSE.equals(cliente.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Cuenta suspendida. Comunicate con EasyCheck para regularizar el servicio."
            );
        }

        // 3) Suscripción vencida (si tiene fecha configurada)
        LocalDate hoy = LocalDate.now();
        if (cliente.getSuscripcionHasta() != null &&
                cliente.getSuscripcionHasta().isBefore(hoy)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Cuenta vencida. Comunicate con EasyCheck para renovar tu suscripción."
            );
        }

        // 4) Generar token JWT
        String token = jwtUtil.generateToken(userDetails);

        // Roles tal como vienen de Spring Security: ADMIN, RECEPCION, etc.
        List<String> rawRoles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        // Mapeamos a los nombres que espera el frontend: ROLE_ADMIN, ROLE_RECEPCION, ...
        List<String> roles = rawRoles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .collect(Collectors.toList());

        return new JwtResponse(token, username, roles);
    }

    // ==================== REGISTER ====================

    @Transactional
    public JwtResponse register(RegisterRequest request) {

        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }

        Rol rolBase = rolRepository.findByNombre("RECEPCION")
                .orElseThrow(() -> new RuntimeException("Rol RECEPCION no encontrado"));

        // nuevo cliente para este usuario
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre() + " " + request.getApellido())
                .email(request.getEmail())
                .activo(true)
                // Suscripción inicial: por ejemplo, 30 días desde hoy (podés ajustarlo o dejar null)
                .suscripcionHasta(LocalDate.now().plusDays(30))
                .build();
        clienteRepository.save(cliente);

        Usuario usuario = Usuario.builder()
                .usuario(request.getUsuario())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .activo(true)
                .cliente(cliente)
                .roles(Set.of(rolBase))
                .build();

        usuarioRepository.save(usuario);

        // Autologin tras registrarse: aprovecha el mismo mapping de roles de login()
        LoginRequest login = new LoginRequest();
        login.setUsuario(request.getUsuario());
        login.setPassword(request.getPassword());

        return login(login);
    }

    // ==================== FORGOT PASSWORD ====================

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un usuario con ese email")
                );

        String token = java.util.UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .expiracion(LocalDateTime.now().plusHours(1)) // válido 1 hora
                .usado(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Para pruebas, loguearlo en consola
        System.out.println("Token reset generado para " + usuario.getEmail() + ": " + token);

        // ⚠️ Idealmente este link debería apuntar a tu frontend (ej: https://panel.easycheck.com/reset-password?token=...)
        String link = "http://localhost:8080/api/auth/reset-password?token=" + token;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(usuario.getEmail());
        msg.setSubject("Recuperación de contraseña - Sistema de Cabañas");
        msg.setText("Hola " + usuario.getNombre() + ",\n\n"
                + "Para restablecer tu contraseña hacé clic en el siguiente enlace:\n"
                + link + "\n\n"
                + "Este enlace es válido por 1 hora.\n\n"
                + "Si no solicitaste este cambio, ignorá este mensaje.");

        mailSender.send(msg);
    }

    // ==================== RESET PASSWORD ====================

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Token inválido o no encontrado")
                );

        if (Boolean.TRUE.equals(resetToken.getUsado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token ya fue utilizado");
        }

        if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token ha expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
