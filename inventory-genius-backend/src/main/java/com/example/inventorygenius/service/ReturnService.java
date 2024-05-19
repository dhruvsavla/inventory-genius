package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Return;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.ReturnRepository;
import com.example.inventorygenius.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    // Method to add a new item
    public Return addReturn(Return stock) {
        return returnRepository.save(stock);
    }

    // Method to get all items
    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    public void deleteReturnById(Long id) {
        returnRepository.deleteById(id);
    }

    public Return updateStock(Long id, Return stockDetails) {
        Return stock = returnRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

                stock.setDate(stockDetails.getDate());
                stock.setSkucode(stockDetails.getSkucode());
                stock.setPortal(stockDetails.getPortal());
                stock.setOrderNo(stockDetails.getOrderNo());
                stock.setReturnCode(stockDetails.getReturnCode());
                stock.setTrackingNumber(stockDetails.getTrackingNumber());
                stock.setOkStock(stockDetails.getOkStock());
                stock.setSentForTicketOn(stockDetails.getSentForTicketOn());
                stock.setSentForRaisingTicketOn(stockDetails.getSentForRaisingTicketOn());

        return returnRepository.save(stock);
    }

}
