package com.example.intentoPrueba.service;

import com.example.intentoPrueba.model.usuario;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class usuarioService {

    @Getter
    private List<usuario> usuarios = new ArrayList<>();
    private Map<String, usuario> mapaPorId = new HashMap<>();

    public void cargarDatos() {
        List<usuario> lista1 = leerCsv("dataset1.csv");
        List<usuario> lista2 = leerCsv("dataset2.csv");

        Map<String, usuario> porCorreo = new HashMap<>();

        for (usuario u : lista1) {
            porCorreo.put(u.getCorreo(), u);
        }

        for (usuario u : lista2) {
            usuario existente = porCorreo.get(u.getCorreo());
            if (existente != null) {
                List<String> nuevaLista = new ArrayList<>(existente.getSiguiendo());
                nuevaLista.addAll(u.getSiguiendo());
                existente.setSiguiendo(nuevaLista);

                if (u.getUltimaConexion().isAfter(existente.getUltimaConexion())) {
                    existente.setUltimaConexion(u.getUltimaConexion());
                }
            } else {
                porCorreo.put(u.getCorreo(), u);
            }
        }

        usuarios = new ArrayList<>(porCorreo.values());

        for (usuario u : usuarios) {
            mapaPorId.put(u.getId(), u);
        }

        for (usuario u : usuarios) {
            for (String seguidoId : u.getSiguiendo()) {
                usuario seguido = mapaPorId.get(seguidoId);
                if (seguido != null) {
                    seguido.setCantidadSeguidores(seguido.getCantidadSeguidores() + 1);
                }
            }
        }
    }

    private List<usuario> leerCsv(String archivo) {
        List<usuario> resultado = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(archivo))))) {

            String linea;
            reader.readLine(); // saltar encabezado

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length < 3) continue;

                String id = partes[0].trim();
                String correo = partes[1].trim();
                LocalDate ultimaConexion;

                try {
                    ultimaConexion = LocalDate.parse(partes[2].trim(), formatter);
                } catch (Exception e) {
                    System.out.println("Fecha inválida para usuario: " + correo + " → " + partes[2]);
                    continue;
                }

                List<String> siguiendo = new ArrayList<>();
                if (partes.length > 3 && !partes[3].trim().equals("0")) {
                    siguiendo = new ArrayList<>(Arrays.asList(partes[3].trim().split(",")));
                }

                usuario u = new usuario();
                u.setId(id);
                u.setCorreo(correo);
                u.setUltimaConexion(ultimaConexion);
                u.setSiguiendo(siguiendo);

                resultado.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    // 1. Usuarios inactivos
    public List<usuario> obtenerInactivos() {
        LocalDate corte = LocalDate.of(2019, 10, 17);
        return usuarios.stream()
                .filter(u -> u.getUltimaConexion().isBefore(corte))
                .collect(Collectors.toList());
    }

    // 2. Usuarios que siguen a muchos inactivos
    public List<usuario> obtenerUsuariosConMuchosInactivosSeguidos() {
        LocalDate corte = LocalDate.of(2019, 10, 17);

        return usuarios.stream()
                .filter(u -> {
                    List<String> siguiendo = u.getSiguiendo();
                    if (siguiendo.isEmpty()) return false;

                    long inactivosSeguidos = siguiendo.stream()
                            .map(mapaPorId::get)
                            .filter(Objects::nonNull)
                            .filter(seg -> seg.getUltimaConexion().isBefore(corte))
                            .count();

                    return inactivosSeguidos >= siguiendo.size() / 2.0;
                })
                .collect(Collectors.toList());
    }

    // 3. Usuarios con más seguidores
    public List<usuario> obtenerUsuariosConMasSeguidores() {
        int maxSeguidores = usuarios.stream()
                .mapToInt(usuario::getCantidadSeguidores)
                .max()
                .orElse(0);

        return usuarios.stream()
                .filter(u -> u.getCantidadSeguidores() == maxSeguidores)
                .collect(Collectors.toList());
    }

    // 4. Últimos 10 conectados
    public List<usuario> obtenerUltimosConectados() {
        return usuarios.stream()
                .sorted(Comparator.comparing(usuario::getUltimaConexion).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    // 5. Más populares
    public List<usuario> obtenerUsuariosMasPopulares() {
        int maxSeguidores = usuarios.stream()
                .mapToInt(usuario::getCantidadSeguidores)
                .max()
                .orElse(0);

        return usuarios.stream()
                .filter(u -> u.getCantidadSeguidores() == maxSeguidores)
                .collect(Collectors.toList());
    }

    // 6. Inactivos con más seguidores
    public List<usuario> obtenerInactivosMasSeguidos() {
        LocalDate corte = LocalDate.of(2019, 10, 17);

        List<usuario> inactivos = usuarios.stream()
                .filter(u -> u.getUltimaConexion().isBefore(corte))
                .collect(Collectors.toList());

        int max = inactivos.stream()
                .mapToInt(usuario::getCantidadSeguidores)
                .max()
                .orElse(0);

        return inactivos.stream()
                .filter(u -> u.getCantidadSeguidores() == max)
                .collect(Collectors.toList());
    }
}
