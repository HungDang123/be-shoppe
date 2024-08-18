package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse extends BaseResponse{
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("order_date")
    private LocalDateTime orderDate;
    private OrderStatus status;
    @JsonProperty("total_money")
    private Float totalMoney;
    @JsonProperty("shopping_method")
    private String shoppingMethod;
    @JsonProperty("shopping_address")
    private String shoppingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("shopping_date")
    private LocalDate shoppingDate;
    @JsonProperty("is_active")
    private boolean active;
    @JsonProperty("order_detail")
    private List<OrderDetail> orderDetails;
}
