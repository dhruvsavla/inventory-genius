package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.repository.BomRepository;
import com.example.inventorygenius.repository.ItemRepository;

import java.util.function.Supplier;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BomService {

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Bom addBom(Bom bom) {
        Item item = itemRepository.findBySKUCode(bom.getSKUCode());

        if (item == null) {
            throw new RuntimeException("Item with SKUCode: " + bom.getSKUCode() + " not found!");
        }

        bom.getBomItems().add(item);

        item.getBoms().add(bom);

        return bomRepository.save(bom);
    }
    
    

    public List<Bom> getAllBoms() {
        return bomRepository.findAll();
    }

    public Bom updateBom(Long id, Bom bomDetails) {
        Bom bom = bomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

        bom.setSKUCode(bomDetails.getSKUCode());
        bom.setBomItems(bomDetails.getBomItems());
        bom.setItemsInBom(bomDetails.getItemsInBom());
        bom.setDefaultStartDate(bomDetails.getDefaultStartDate());
        bom.setDefaultEndDate(bomDetails.getDefaultEndDate());
        bom.setBomCode(bomDetails.getBomCode());

        return bomRepository.save(bom);
    }

    public void deleteBomById(Long id) {
        bomRepository.deleteById(id);
    }

    public Bom getBomByBomCode(String bomCode) {
        return bomRepository.findByBomCode(bomCode)
                .orElseThrow(() -> new RuntimeException("Bom not found with bomCode " + bomCode));
    }


}
