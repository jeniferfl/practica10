package com.example.GameVault.controller;


import com.example.GameVault.model.Juego;
import com.example.GameVault.model.Usuario;
import com.example.GameVault.service.JuegoService;
import com.example.GameVault.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class GameController {
    @Autowired
    JuegoService juegoService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/fragments-demo")
    public String fragments(){
        return "fragments-demo";
    }

    @GetMapping({"/","/juegos"})
    public String juegos(Model model){
        model.addAttribute("juegos", juegoService.listarTodos());
        return "juegos";
    }
    @GetMapping("/juegos/nuevo")
    public String formulario(Model model) {
        model.addAttribute("juego", new Juego());
        return "formulario";
    }

    @PostMapping("/juegos")
    public String guardarJuego(@RequestParam("titulo") String titulo,
                               @RequestParam("descripcion") String descripcion,
                               @RequestParam("precio") Double precio,
                               @RequestParam("categoria") String categoria,
                               @RequestParam("portada") MultipartFile portada) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        Juego juego = new Juego();
        juego.setTitulo(titulo);
        juego.setDescripcion(descripcion);
        juego.setPrecio(precio);
        juego.setCategoria(categoria);
        juego.setUsuario(usuario);


        juegoService.guardarJuego(juego, portada);

        return "redirect:/juegos";
    }

    @GetMapping("/juegos/editar/{id}")
    public String editarJuego(@PathVariable Long id, Model model) {

        Juego juego = juegoService.obtenerJuegoPorId(id);
        Usuario logueado = usuarioService.obtenerUsuarioLogueado();

        // Verificar que el usuario haya iniciado sesión
        if (logueado == null) {
            return "redirect:/login";
        }

        // Verificar que el juego exista
        if (juego == null) {
            return "redirect:/mis-juegos";
        }

        // Verificar que el juego tenga propietario
        if (juego.getUsuario() == null) {
            return "redirect:/mis-juegos";
        }

        // Verificar que el juego pertenezca al usuario
        if (!juego.getUsuario().getId().equals(logueado.getId())) {
            return "redirect:/mis-juegos";
        }

        model.addAttribute("juego", juego);
        return "formulario";
    }


    @PostMapping("/juegos/editar/{id}")
    public String actualizarJuego(
            @PathVariable Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "portada", required = false)
            MultipartFile portada) {

        Usuario logueado = usuarioService.obtenerUsuarioLogueado();

        // Verificar la sesión
        if (logueado == null) {
            return "redirect:/login";
        }

        Juego juegoExistente = juegoService.obtenerJuegoPorId(id);

        // Verificar que el juego exista
        if (juegoExistente == null) {
            return "redirect:/mis-juegos";
        }

        // Verificar que tenga propietario
        if (juegoExistente.getUsuario() == null) {
            return "redirect:/mis-juegos";
        }

        // Verificar que pertenezca al usuario
        if (!juegoExistente.getUsuario().getId()
                .equals(logueado.getId())) {
            return "redirect:/mis-juegos";
        }

        // Actualizar los datos del juego
        juegoExistente.setTitulo(titulo);
        juegoExistente.setDescripcion(descripcion);
        juegoExistente.setPrecio(precio);
        juegoExistente.setCategoria(categoria);

        // Guardar los cambios
        juegoService.guardarJuego(juegoExistente, portada);

        return "redirect:/mis-juegos";
    }


    @PostMapping("/juegos/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id) {

        Juego juego = juegoService.obtenerJuegoPorId(id);
        Usuario logueado = usuarioService.obtenerUsuarioLogueado();

        if (juego == null || juego.getUsuario() == null || logueado == null) {
            return "redirect:/mis-juegos";
        }

        if (juego.getUsuario().getId().equals(logueado.getId())) {
            juegoService.eliminarJuego(juego.getId());
        }

        return "redirect:/mis-juegos";
    }


    @GetMapping("/mis-juegos")
    public String misjuegos(Model model) {
        Usuario usuario = this.usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", juegoService.listarTodos());

        return "mis-juegos";
    }

}
