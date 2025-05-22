package com.example.intentoPrueba.controller;

import com.example.intentoPrueba.model.usuario;
import com.example.intentoPrueba.service.usuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class usuarioController {

    @Autowired
    private usuarioService usuarioService;

    @GetMapping("/cargar")
    public String cargarDatos() {
        usuarioService.cargarDatos();
        return "Datos cargados correctamente.";
    }

    // 1. Usuarios inactivos
    @GetMapping("/inactivos")
    public List<usuario> obtenerInactivos() {
        return usuarioService.obtenerInactivos();
    }

    // 2. Usuarios que siguen a al menos la mitad de inactivos
    @GetMapping("/muchos-inactivos")
    public List<usuario> obtenerUsuariosConMuchosInactivos() {
        return usuarioService.obtenerUsuariosConMuchosInactivosSeguidos();
    }

    // 3. Usuario(s) con más seguidores
    @GetMapping("/mas-seguido")
    public List<usuario> obtenerUsuariosConMasSeguidores() {
        return usuarioService.obtenerUsuariosConMasSeguidores();
    }

    @GetMapping("/ultimos-conectados")
    public List<usuario> ultimosConectados() {
        return usuarioService.obtenerUltimosConectados();
    }

    @GetMapping("/mas-populares")
    public List<usuario> masPopulares() {
        return usuarioService.obtenerUsuariosMasPopulares();
    }

    @GetMapping("/inactivos-mas-seguidos")
    public List<usuario> inactivosMasSeguidos() {
        return usuarioService.obtenerInactivosMasSeguidos();
    }

}

