package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.repository.OrderRepository;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.StockCountService;
import com.example.inventorygenius.service.StockService;
import com.example.inventorygenius.service.PickListService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired 
    private StockService stockService;

    @Autowired 
    private StockCountService stockCountService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @Autowired
    private PickListService pickListService;

    @PostMapping
    public ResponseEntity<Order> addItem(@RequestBody Order order) {
        Order newOrder = orderService.addOrder(order);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllItems() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
    System.out.println("Received request to update order with ID: " + orderId);

    Optional<Order> existingOrderOptional = orderRepository.findById(orderId);

    if (existingOrderOptional.isPresent()) {
        Order existingOrder = existingOrderOptional.get();
        System.out.println("Existing order found: " + existingOrder);

        // Update properties of the existing item with the new values
        existingOrder.setCourier(updatedOrder.getCourier());
        existingOrder.setDate(updatedOrder.getDate());
        existingOrder.setDispatched(updatedOrder.getDispatched());
        existingOrder.setOrderNo(updatedOrder.getOrderNo());
        existingOrder.setPortal(updatedOrder.getPortal());
        existingOrder.setPortalOrderLineId(updatedOrder.getPortalOrderLineId());
        existingOrder.setPortalOrderNo(updatedOrder.getPortalOrderNo());
        existingOrder.setPortalSKU(updatedOrder.getPortalSKU());
        existingOrder.setProductDescription(updatedOrder.getProductDescription());
        existingOrder.setQty(updatedOrder.getQty());
        existingOrder.setShipByDate(updatedOrder.getShipByDate());
        existingOrder.setSellerSKU(updatedOrder.getSellerSKU());
        existingOrder.setCancel(updatedOrder.getCancel());
        existingOrder.setItems(updatedOrder.getItems());
        existingOrder.setAwbNo(updatedOrder.getAwbNo());

        System.out.println("Updated order: " + existingOrder);

        // Check if the order has been canceled
        if (updatedOrder.getCancel().equals("Order Canceled") && updatedOrder.getPicklist() == null) {
            System.out.println("Order is canceled. Updating stock.");
            Stock stock = new Stock();
            Item item = itemSupplierService.getItemBySKUCode(updatedOrder.getItems().get(0).getSKUCode());

            if(item == null) {
                System.out.println("Item is null for SKUCode: " + updatedOrder.getItems().get(0).getSKUCode());
            } else {
                System.out.println("Item found: " + item);
            }

            if (item != null && item.getBoms().size() > 0) {
                stock.setDate(new Date());
                stock.setSkucode(item.getParentSKU());
                stock.setSubQty("0");

                for (Bom bom : item.getBoms()) {
                    for (BomItem bomItem : bom.getItemsInBom()) {
                        if (bomItem.getItem().getSKUCode().equals(item.getParentSKU())) {
                            stock.setAddQty(String.valueOf(updatedOrder.getQty() * Double.parseDouble(bomItem.getQty())));
                        } else {
                            Stock s = new Stock();
                            s.setDate(new Date());
                            s.setSubQty("0");
                            s.setItem(item);
                            s.setAddQty(String.valueOf(updatedOrder.getQty() * Double.parseDouble(bomItem.getQty())));
                            s.setSkucode(bomItem.getBomItem());
                            s.setSource("Order");
                            s.setMessage("Order Cancelled");
                            s.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));

                            System.out.println("Adding stock for BOM item: " + s);
                            stockService.addStock(s);
                        }
                    }
                }

                stock.setItem(updatedOrder.getItems().get(0));
                stock.setSource("Order");
                stock.setMessage("Order Cancelled");
                stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
            } else {
                stock.setDate(new Date());
                stock.setSkucode(updatedOrder.getItems().get(0).getSKUCode());
                stock.setSubQty("0");
                stock.setAddQty(String.valueOf(updatedOrder.getQty()));
                stock.setItem(updatedOrder.getItems().get(0));
                stock.setSource("Order");
                stock.setMessage("Order Cancelled");
                stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
            }

            System.out.println("Adding stock: " + stock);
            stockService.addStock(stock);

            StockCount sc = new StockCount();
            if (updatedOrder.getItems().get(0).getBoms().size() > 0) {
                for (Bom b : updatedOrder.getItems().get(0).getBoms()) {
                    for (BomItem bomItem : b.getItemsInBom()) {
                        if (bomItem.getItem().getSKUCode().equals(updatedOrder.getItems().get(0).getParentSKU())) {
                            sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getParentSKU());
                            sc.setCount(sc.getCount() + updatedOrder.getQty() * Double.parseDouble(bomItem.getQty()));
                        } else {
                            StockCount scBom = stockCountService.getStockCountBySKUCode(bomItem.getItem().getSKUCode());
                            scBom.setCount(scBom.getCount() + updatedOrder.getQty() * Double.parseDouble(bomItem.getQty()));
                            System.out.println("Updating stock count for BOM item: " + scBom);
                            stockCountService.updateStockCount(scBom);
                        }
                    }
                }
            } else {
                sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
                sc.setCount(sc.getCount() + updatedOrder.getQty());
            }
            System.out.println("Updating stock count1: " + sc);
            stockCountService.updateStockCount(sc);
        }

        if (updatedOrder.getCancel().equals("Order Not Canceled")) {
            System.out.println("Order is not canceled. Updating stock.");
            Stock stock = new Stock();
            Item item = itemSupplierService.getItemBySKUCode(updatedOrder.getItems().get(0).getSKUCode());

            if(item == null) {
                System.out.println("Item is null for SKUCode: " + updatedOrder.getItems().get(0).getSKUCode());
            } else {
                System.out.println("Item found: " + item);
            }

            if (item != null && item.getBoms().size() > 0) {
                stock.setDate(new Date());
                stock.setSkucode(item.getParentSKU());
                stock.setAddQty("0");

                for (Bom bom : item.getBoms()) {
                    for (BomItem bomItem : bom.getItemsInBom()) {
                        if (bomItem.getBomItem().equals(item.getParentSKU())) {
                            stock.setSubQty(String.valueOf(updatedOrder.getQty() * Double.parseDouble(bomItem.getQty())));
                        } else {
                            Stock s = new Stock();
                            s.setDate(new Date());
                            s.setAddQty("0");
                            s.setItem(item);
                            s.setSubQty(String.valueOf(updatedOrder.getQty() * Double.parseDouble(bomItem.getQty())));
                            s.setSkucode(bomItem.getBomItem());
                            s.setSource("Order");
                            s.setMessage("Order Not Cancelled");
                            s.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));

                            System.out.println("Adding stock for BOM item: " + s);
                            stockService.addStock(s);
                        }
                    }
                }

                stock.setItem(updatedOrder.getItems().get(0));
                stock.setSource("Order");
                stock.setMessage("Order Not Cancelled");
                stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
            } else {
                stock.setDate(new Date());
                stock.setSkucode(updatedOrder.getItems().get(0).getSKUCode());
                stock.setAddQty("0");
                stock.setSubQty(String.valueOf(updatedOrder.getQty()));
                stock.setItem(updatedOrder.getItems().get(0));
                stock.setSource("Order");
                stock.setMessage("Order Not Cancelled");
                stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
            }

            System.out.println("Adding stock: " + stock);
            stockService.addStock(stock);

            // StockCount sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
            // System.out.println("stock count sku = " + sc.getItem().getSKUCode());
            // if (updatedOrder.getItems().get(0).getBoms().size() > 0) {
            //     for (Bom b : updatedOrder.getItems().get(0).getBoms()) {
            //         for (BomItem bomItem : b.getItemsInBom()) {
            //             if (bomItem.getBomItem().equals(updatedOrder.getItems().get(0).getParentSKU())) {
            //                 sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getParentSKU());
            //                 if (existingOrder.getQty() != updatedOrder.getQty()) {
            //                     sc.setCount(sc.getCount() + updatedOrder.getQty() * Double.parseDouble(bomItem.getQty()));
            //                 }
            //             } else {
            //                 StockCount scBom = stockCountService.getStockCountBySKUCode(bomItem.getBomItem());
            //                 if (existingOrder.getQty() != updatedOrder.getQty()) {
            //                     scBom.setCount(scBom.getCount() + updatedOrder.getQty() * Double.parseDouble(bomItem.getQty()));
            //                 }
            //                 System.out.println("Updating stock count for BOM item: " + scBom);
            //                 stockCountService.updateStockCount(scBom);
            //             }
            //         }
            //     }
            // }
            // System.out.println("Updating stock count2: " + sc);
            // stockCountService.updateStockCount(sc);
        }

        // Save the updated item
        System.out.println("Saving updated order: " + existingOrder);
        Order savedOrder = orderRepository.save(existingOrder);

        System.out.println("Updated order saved: " + savedOrder);
        return ResponseEntity.ok(savedOrder);
    } else {
        System.out.println("Order not found for ID: " + orderId);
        return ResponseEntity.notFound().build();
    }
}



    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") Long id) {
        System.out.println("deleted");
        orderService.deleteOrderById(id);
    }

    private final OrderRepository orderRepository;

     @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/with-items")
    public List<Order> getOrdersWithItems() {
        return orderRepository.findAllWithItems();
    }

     

    

    @PutMapping("/scan/dispatch")
    public ResponseEntity<?> updateOrderStatusDispatch(@RequestParam String awb) {
        List<Order> updatedOrders = new ArrayList<>();
        List<Order> allOrders = orderService.getAllOrders();
        
        if (allOrders == null || allOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found.");
        }

        for (Order order : allOrders) {
            String orderAwbNo = order.getAwbNo();
            if (orderAwbNo != null && orderAwbNo.equals(awb)) {
                if (order.getOrderStatus().equals("packed")) {
                    order.setOrderStatus("dispatched");
                    orderService.updateOrder(order.getOrderId(), order);
                    updatedOrders.add(order);
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Order with AWB " + awb + " is not packed.");
                }
            }
        }

        if (updatedOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders with AWB " + awb + " found.");
        }

        return ResponseEntity.ok(updatedOrders);
    }


    @PutMapping("/scan/pack")
    public ResponseEntity<?> updateOrderStatusPack(@RequestParam String awb) {
        List<Order> updatedOrders = new ArrayList<>();
        List<Order> allOrders = orderService.getAllOrders();
        
        if (allOrders == null || allOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found.");
        }

        for (Order order : allOrders) {
            String orderAwbNo = order.getAwbNo();
            if (orderAwbNo != null && orderAwbNo.equals(awb)) {
                if (order.getOrderStatus().equals("packinglist generated")) {
                    order.setOrderStatus("packed");
                    orderService.updateOrder(order.getOrderId(), order);
                    updatedOrders.add(order);
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Packing list for Order with AWB " + awb + " is not generated.");
                }
            }
        }

        if (updatedOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders with AWB " + awb + " found.");
        }

        return ResponseEntity.ok(updatedOrders);
    }

}
