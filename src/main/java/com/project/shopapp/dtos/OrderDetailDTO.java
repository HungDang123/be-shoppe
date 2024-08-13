package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("product_id")
    private Long productId;
    @Min(value = 0,message = "Price must be >= 0")
    private Float price;
    @JsonProperty("number_of_products")
    @Min(value = 1,message = "Number of products must be >= 1")
    private int numberOfProducts;
    @JsonProperty("total_money")
    @Min(value = 0,message = "Price must be >= 0")
    private Float totalMoney;
    private String color;
}
