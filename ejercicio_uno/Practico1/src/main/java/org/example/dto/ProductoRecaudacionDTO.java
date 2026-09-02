package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductoRecaudacionDTO {
    private int idProducto;
    private String nombre;
    private double recaudacion;
}
