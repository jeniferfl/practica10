package com.example.GameVault.controller;

import com.example.GameVault.model.Usuario;
import com.example.GameVault.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de autenticación: registro e inicio de sesión.
 */
@Controller
public class AuthController {
    @Autowired
    private UsuarioService usuarioService;
    // --- VISTAS GET ---
    @GetMapping("/login")
    public String mostrarLogin() { return "login"; }
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }
    // --- PROCESOS POST ---
    @PostMapping("/registro")
    public String procesarRegistro(Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
        return "redirect:/login?registrado=true";
    }
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                HttpSession session, Model model) {
        Usuario usuario = usuarioService.autenticarUsuario(email, password);
        if (usuario != null) {
            // ¡ÉXITO! Guardamos su "gafete" en la sesión del servidor
            session.setAttribute("usuario_id", usuario.getId());
            return "redirect:/mis-juegos";
        } else {
            model.addAttribute("error", "Credenciales inválidas.");
            return "login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destruimos el gafete
        return "redirect:/";
    }
}

