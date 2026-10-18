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
        Usuario usuario = this.usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        Juego juego = new Juego();
        juego.setTitulo(titulo);
        juego.setDescripcion(descripcion);
        juego.setPrecio(precio);
        juego.setCategoria(categoria);
        juegoService.guardarJuego(juego, portada);
        juego.setUsuario(usuario);

        // Redirigimos a la lista de juegos (Patrón PRG - Post/Redirect/Get)
        return "redirect:/juegos";
    }

    @PostMapping("/juegos/elimiar/{id}")
    public String eliminarJuego(@PathVariable Long id){
        juegoService.eliminarJuego(id);
        return "redirect:/juegos";

    }

    @GetMapping("/juegos/editar/{id}")
    public String editarJuego(@PathVariable Long id, Model model){
        Juego juego = juegoService.buscarPorId(id);
        model.addAttribute("juego", juego);
                return "redirect:/formulario";
    }
    @PostMapping("/juegos/editar/{id}")
    public String editarJuego(
                @PathVariable Long id,
                @RequestParam("titulo") String titulo,
                @RequestParam("descripcion") String descripcion,
                @RequestParam("precio") Double precio,
                @RequestParam("categoria") String categoria,
                @RequestParam(value = "portada", required = false) MultipartFile portada) {
        Juego juego = this.juegoService.buscarPorId(id);
        juego.setTitulo(titulo);
        juego.setDescripcion(descripcion);
        juego.setPrecio(precio);
        juego.setCategoria(categoria);
        juegoService.guardarJuego(juego, portada);


        // Redirigimos a la lista de juegos (Patrón PRG - Post/Redirect/Get)
        return "redirect:/juegos";
    }
    @GetMapping({"/mis-juegos"})
    public String misjuegos(Model model){
        model.addAttribute("usuarios", this.usuarioService.obtenerUsuarioLogueado());
        model.addAttribute("juegos", juegoService.listarTodos());
        return "mis-juegos";
    }

}
