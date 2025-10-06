package com.example.demo.application.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final DatoIndicadorRepository datoIndicadorRepository;
    private final PaisRepository paisRepository;
    private final UserRepository userRepository;

    @Override
    public List<String> validateUniqueNames(List<ValidatedDataRow> rows) {
        List<String> errors = new ArrayList<>();
        if (rows == null) {
            return errors;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> uniqueInFile = new HashSet<>();
        for (ValidatedDataRow row : rows) {
            String uniqueKey = row.getPaisNombre() + "-" + row.getName() + "-" + row.getAnio();
            if (!uniqueInFile.add(uniqueKey)) {
                errors.add("La combinación de país, indicador y año '" + uniqueKey + "' está duplicada en el archivo.");
                continue; // Skip to next row
            }

            Optional<Pais> paisOpt = paisRepository.findByNombrePais(row.getPaisNombre());
            if (paisOpt.isEmpty()) {
                errors.add("El país '" + row.getPaisNombre() + "' no fue encontrado.");
                continue; // Skip to next row
            }

            Pais pais = paisOpt.get();
            boolean exists = datoIndicadorRepository.existsByPaisAndTipoIndicadorAndAnioAndUser(pais, row.getName(), row.getAnio(), user);
            if (exists) {
                errors.add("El dato para el país '" + row.getPaisNombre() + "', indicador '" + row.getName() + "' y año '" + row.getAnio() + "' ya existe en la base de datos.");
            }
        }
        return errors;
    }

    @Override
    public List<String> validateValueSum(List<ValidatedDataRow> rows) {
        List<String> errors = new ArrayList<>();
        if (rows == null) {
            return errors;
        }
        long sum = 0;
        for (ValidatedDataRow row : rows) {
            sum += row.getValue();
        }
        if (sum > 1000000) {
            errors.add("La suma de todos los valores ( " + sum + ") no puede ser mayor que 1,000,000.");
        }
        return errors;
    }
}