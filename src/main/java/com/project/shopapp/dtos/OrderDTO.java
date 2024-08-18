package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.OrderStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User id must be > 0")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("order_date")
    private LocalDate orderDate;
    private String status;
    @JsonProperty("total_money")
    @Min(value = 1, message = "Total money must be >= 0")
    private Float totalMoney;
    @JsonProperty("shopping_method")
    private String shoppingMethod;
    @JsonProperty("shopping_address")
    private String shoppingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("shopping_date")
    private LocalDate shoppingDate;
    @JsonProperty("cart_item")
    private List<CartItemDTO> cartItem;
}
