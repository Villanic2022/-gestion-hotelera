package com.miempresa.gestion_hotelera.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarEmailResetPassword(String emailDestino, String token) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(emailDestino);
            mensaje.setSubject("Resetear contraseña - Gestión Hotelera");
            mensaje.setText("Para resetear tu contraseña, haz clic en el siguiente enlace:\n" +
                    "http://localhost:8080/reset-password?token=" + token + "\n\n" +
                    "Este enlace expirará en 1 hora.\n" +
                    "Si no solicitaste este cambio, ignora este correo.");

            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo electrónico", e);
        }
    }

    public void enviarEmailConfirmacion(String emailDestino, String nombreUsuario) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(emailDestino);
            mensaje.setSubject("Bienvenido a Gestión Hotelera");
            mensaje.setText("Hola " + nombreUsuario + ",\n\n" +
                    "Tu cuenta ha sido creada exitosamente en nuestro sistema de Gestión Hotelera.\n" +
                    "Ya puedes comenzar a usar la plataforma.\n\n" +
                    "Saludos,\n" +
                    "Equipo de Gestión Hotelera");

            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo de confirmación", e);
        }
    }
}
