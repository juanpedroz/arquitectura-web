package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ClientesFacturadosDTO {
    private int idCliente;
    private String nombre;
    private double totalFacturado;
}
