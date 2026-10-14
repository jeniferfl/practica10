package com.example.GameVault.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "juegos")
public class Juego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "portada_url")
    private String portadaUrl;

    @Column(nullable = false)
    private Double   precio;

    @Column(nullable = false)
    private String categoria;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;



}
