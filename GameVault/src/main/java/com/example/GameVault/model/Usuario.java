package com.example.GameVault.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad Usuario: las personas que pueden iniciar sesión y tener juegos en la bóveda.
 * Se guarda en la tabla 'usuarios'.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    // unique = true: no puede haber dos cuentas con el mismo correo
    @Column(nullable = false, unique = true)
    private String email;

    // Se guarda cifrada con BCrypt (ver UsuarioService), nunca en texto plano
    @Column(nullable = false)
    private String contrasenia;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Juego> juegos;

}
