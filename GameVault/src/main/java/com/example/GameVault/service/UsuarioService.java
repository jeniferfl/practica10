package com.example.GameVault.service;

import com.example.GameVault.model.Usuario;
import com.example.GameVault.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Lógica de usuarios: registro y autenticación.
 */
@Service
public class UsuarioService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpSession session;

    @Autowired
    private UsuarioRepository usuarioRepository;


    /**
     * Registra un usuario guardando su contraseña cifrada con BCrypt.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptamos la contraseña con BCrypt antes de guardar en la BD
        usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        return usuarioRepository.save(usuario);
    }


    /**
     * Devuelve el usuario si el correo existe y la contraseña coincide; si no, null.
     */
    public Usuario autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificamos el texto ingresado contra el Hash de la BD
            if (passwordEncoder.matches(password, usuario.getContrasenia())) {
                return usuario;
            }
        }
        return null; // Credenciales inválidas
    }

    public Usuario obtenerUsuarioLogueado() {
        Long usuarioId = (Long) session.getAttribute("usuario_id");
        if (usuarioId != null) {
            return usuarioRepository.findById(usuarioId).orElse(null);
        }
        return null;
    }



}
