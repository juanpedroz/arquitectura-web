package com.arqweb.integrador_tres.service.dto.carrera.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarreraRequestDTO {
    @NotBlank
    private String nombre;

    @NotNull
    @Positive
    private Long duracion;
}
