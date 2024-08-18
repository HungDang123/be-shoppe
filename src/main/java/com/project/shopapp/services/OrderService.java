package com.project.shopapp.services;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User does not exist"));
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper-> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.pending);
        LocalDate shoppingDate = orderDTO.getShoppingDate() == null ? LocalDate.now() : orderDTO.getShoppingDate();
        if (shoppingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shopping date must be at least today!");
        }
        order.setOrderDate(new Date());
        order.setShoppingDate(shoppingDate);
        order.setActive(true);
        orderRepository.save(order);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO :orderDTO.getCartItem()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            Long productId = cartItemDTO.getProduct_id();
            int quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId)
                    .orElseThrow(()-> new DataNotFoundException("Product "+productId+ " is not exist"));
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProduct(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        modelMapper.typeMap(Order.class,OrderResponse.class);
        return modelMapper.map(order, OrderResponse.class);
    }



    @Override
    public OrderResponse getOrderById(Long id) throws DataNotFoundException {
        Order existing =  orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order is not exist"));
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(existing,orderResponse);
//        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
//        for(OrderDetail detail : existing.getOrderDetails()){
//            OrderDetailResponse response = new OrderDetailResponse();
//            new ModelMapper().map(detail, response);
//            orderDetailResponses.add(response);
//        }
//        orderResponse.setOrderDetails(orderDetailResponses);
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order existing =  orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order is not exist"));
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User does not exist"));

        modelMapper.map(orderDTO, existing);
        if(!orderDTO.getStatus().equals(OrderStatus.delivered)){
            existing.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        }
        existing.setUser(user);
        existing.setId(id);
        orderRepository.save(existing);
        modelMapper.typeMap(Order.class,OrderResponse.class);
        return modelMapper.map(existing,OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getOrders(Long id) {
        List<Order> orders = orderRepository.findByUserId(id);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order o : orders){
            OrderResponse orderResponse = new OrderResponse();
            modelMapper.map(o,orderResponse);
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        orderOptional.ifPresent(order-> order.setActive(false));
        orderOptional.ifPresent(orderRepository::save);
    }
}
