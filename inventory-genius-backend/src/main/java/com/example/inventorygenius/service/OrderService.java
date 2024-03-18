package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Method to add a new item
    public Order addOrder(Order order) {
        // Create new Item entities within the transaction
        List<Item> newItems = new ArrayList<>();
        for (Item item : order.getItems()) {
            if (item.getItemId() == null) { // Check if item is new
                newItems.add(item);
            }
        }

        // Now you can safely persist the Order entity
        return orderRepository.save(order);
    }

    // Method to get all items
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    public Order updateOrder(Long orderId, Order updatedOrder) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();

            existingOrder.setDate(updatedOrder.getDate());
            existingOrder.setOrderNo(updatedOrder.getOrderNo());
            existingOrder.setPortal(updatedOrder.getPortal());
            existingOrder.setPortalOrderNo(updatedOrder.getPortalOrderNo());
            existingOrder.setPortalOrderLineId(updatedOrder.getPortalOrderLineId());
            existingOrder.setPortalSKU(updatedOrder.getPortalSKU());
            existingOrder.setSellerSKU(updatedOrder.getSellerSKU());
            existingOrder.setProductDescription(updatedOrder.getProductDescription());
            existingOrder.setQty(updatedOrder.getQty());
            existingOrder.setShipByDate(updatedOrder.getShipByDate());
            existingOrder.setDispatched(updatedOrder.getDispatched());
            existingOrder.setCourier(updatedOrder.getCourier());

            return orderRepository.save(existingOrder);
        } else {
            return null;
        }
    }

}
