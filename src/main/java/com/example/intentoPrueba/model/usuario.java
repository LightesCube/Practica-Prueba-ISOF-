package com.example.intentoPrueba.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class usuario {
    private String id;
    private String correo;
    private LocalDate ultimaConexion;
    private List<String> siguiendo = new ArrayList<>();
    private int cantidadSeguidores = 0;
}
