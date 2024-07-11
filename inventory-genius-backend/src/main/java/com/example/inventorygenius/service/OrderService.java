package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.PackingList;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.repository.OrderRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockService stockService;

    // @Autowired
    // private PickListService pickListService;

    // @Autowired
    // private PackingListService packListService;

    // Method to add a new item
    public Order addOrder(Order order) {
        // Create new Item entities within the transaction
        List<Item> newItems = new ArrayList<>();
        for (Item item : order.getItems()) {
            if (item.getItemId() == null) { // Check if item is new
                newItems.add(item);
            }
            newItems.add(item);
        }

        // Now you can safely persist the Order entity
        return orderRepository.save(order);
    }

    // Method to get all items
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
    }

    public void deleteOrderById(Long id) {
        Order order = findById(id);
        Stock stock = new Stock();
        stock.setDate(order.getDate());
        stock.setSkucode(order.getItems().get(0).getSKUCode());
        stock.setAddQty(String.valueOf(order.getQty()));
        stock.setSubQty("0");
        stock.setItem(order.getItems().get(0));

        stock.setSource("order");
        stock.setMessage("order deleted");
        stock.setNumber("order no = " + String.valueOf(order.getOrderNo()));

        stockService.addStock(stock);
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
            existingOrder.setCancel(updatedOrder.getCancel());
            existingOrder.setItems(updatedOrder.getItems());
            existingOrder.setAwbNo(updatedOrder.getAwbNo());
            existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
            existingOrder.setItemPortalMapping(updatedOrder.getItemPortalMapping());
            existingOrder.setPicklist(updatedOrder.getPicklist());
            
            return orderRepository.save(existingOrder);
        } else {
            return null;
        }
    }

    public List<Order> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    public List<Order> findNotDispatchedOrders(){
        List<Order> orders = getAllOrders();
        List<Order> notDispatchedOrders = new ArrayList<>();

        for (Order o : orders) {
            if (!o.getOrderStatus().equals("dispatched")){
                notDispatchedOrders.add(o);
            }
        }
        return notDispatchedOrders;
    }
    
    public List<Order> findNotPackedOrders(){
        List<Order> orders = getAllOrders();
        List<Order> notPackedOrders = new ArrayList<>();

        for (Order o : orders) {
            if (!o.getOrderStatus().equals("dispatched") && !o.getOrderStatus().equals("packed")){
                notPackedOrders.add(o);
            }
        }
        return notPackedOrders;
    }

    public List<Order> findByAwbNo(String AwbNo){
        return orderRepository.findByAwbNo(AwbNo);
    }
}
