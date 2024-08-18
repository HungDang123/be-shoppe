package com.project.shopapp.dtos;

import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO{
    private Long product_id;
    private int quantity;
}
