package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException;
    OrderResponse getOrderById(Long id) throws DataNotFoundException;
    OrderResponse updateOrder(Long id,OrderDTO orderDTO) throws DataNotFoundException;
    List<OrderResponse> getOrders(Long id);
    void deleteOrder(Long id);
}
