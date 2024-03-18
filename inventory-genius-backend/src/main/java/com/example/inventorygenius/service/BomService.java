package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.repository.BomRepository;
import java.util.function.Supplier;
import java.util.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BomService {

    @Autowired
    private BomRepository bomRepository;

    public Bom addBom(Bom bom) {
        List<Item> newItems = new ArrayList<>();
        for (Item item : bom.getBomItems()) {
            if (item.getItemId() == null) {
                newItems.add(item);
            }
        }

        return bomRepository.save(bom);
    }

    public List<Bom> getAllBoms() {
        return bomRepository.findAll();
    }

    public Bom updateBom(Long id, Bom bomDetails) {
        Bom bom = bomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

        bom.setSKUCode(bomDetails.getSKUCode());
        bom.setBomItem(bomDetails.getBomItem());
        bom.setQty(bomDetails.getQty());

        return bomRepository.save(bom);
    }

    public void deleteBomById(Long id) {
        bomRepository.deleteById(id);
    }

}
