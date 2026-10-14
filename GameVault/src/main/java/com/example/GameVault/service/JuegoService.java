package com.example.GameVault.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.GameVault.model.Juego;
import com.example.GameVault.repository.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JuegoService {


    // Inyección de dependencias. Spring nos proporciona la instancia del repositorio
    // de forma automática, sin que nosotros hagamos 'new JuegoRepository()'.
    @Autowired
    private JuegoRepository juegoRepository;
    private Cloudinary cloudinary;




    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";


    public List<Juego> listarTodos() {
        // En lugar de devolver una lista en memoria, vamos a la base de datos MySQL
        return juegoRepository.findAll();
    }


    public void guardarJuego(Juego juego, MultipartFile portada) {
        String urlImagen = "default.png";


        if (!portada.isEmpty()) {
            // OPCIÓN 1: Subida Local (Como estaba antes, pero movido al servicio)
            // urlImagen = guardarImagenLocal(portada);

            // OPCIÓN 2: Subida a Cloudinary
            urlImagen = guardarImagenLocal(portada);
        }


        juego.setPortadaUrl(urlImagen);

        // Guardamos el objeto en la base de datos
        juegoRepository.save(juego);
    }


    /**
     * Muestra de un método de búsqueda personalizado.
     */
    public List<Juego> buscarPorTitulo(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return listarTodos();
        }
        return juegoRepository.findByTituloContainingIgnoreCase(palabra);
    }


    /**
     * Lógica para guardar la imagen localmente en la carpeta del proyecto.
     */
    private String guardarImagenLocal(MultipartFile portada) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }


            String nombreArchivo = UUID.randomUUID().toString() + "_" + portada.getOriginalFilename();
            Path filePath = uploadPath.resolve(nombreArchivo);
            Files.copy(portada.getInputStream(), filePath);


            return nombreArchivo; // Retornamos solo el nombre (ej. "123_foto.png")
        } catch (IOException e) {
            e.printStackTrace();
            return "default.png";
        }
    }

    public void eliminarJuego(Long id) {
        juegoRepository.deleteById(id);

    }
    public Juego buscarPorId(Long id) {
        return juegoRepository.findById(id).orElse(null);
    }

    /**
     * Lógica para subir la imagen a Cloudinary.
     * Retorna la URL segura de la imagen alojada en la nube.
    }*/
    private String guardarImagenCloudinary(MultipartFile portada) {
        try {
            // Sube el archivo a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(portada.getBytes(), ObjectUtils.emptyMap());
            // Obtiene la URL pública generada por Cloudinary
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            System.err.println("Error al subir a Cloudinary. Revisa tus credenciales.");
            e.printStackTrace();
            return "default.png";
        }
    }

}









