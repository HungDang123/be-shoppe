package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
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
