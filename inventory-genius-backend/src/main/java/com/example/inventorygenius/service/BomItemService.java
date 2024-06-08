package com.example.inventorygenius.service;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.repository.BomItemRepository;
import com.example.inventorygenius.repository.BomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BomItemService {

    @Autowired
    private BomItemRepository bomItemRepository;

    @Autowired
    private BomRepository bomRepository;

    public BomItem createBomItem(Long bomId, BomItem bomItem) {
        Optional<Bom> optionalBom = bomRepository.findById(bomId);
        if (optionalBom.isPresent()) {
            Bom bom = optionalBom.get();
            bomItem.setBom(bom);
            return bomItemRepository.save(bomItem);
        } else {
            throw new RuntimeException("Bom not found with id " + bomId);
        }
    }

    public List<BomItem> getBomItems() {
        return bomItemRepository.findAll();
    }

    public BomItem getBomItemById(Long bomItemId) {
        return bomItemRepository.findById(bomItemId)
                .orElseThrow(() -> new RuntimeException("BomItem not found with id " + bomItemId));
    }

    public BomItem updateBomItem(Long bomItemId, BomItem bomItemDetails) {
        BomItem bomItem = getBomItemById(bomItemId);
        bomItem.setBomItem(bomItemDetails.getBomItem());
        bomItem.setQty(bomItemDetails.getQty());
        return bomItemRepository.save(bomItem);
    }

    public void deleteBomItem(Long bomItemId) {
        BomItem bomItem = getBomItemById(bomItemId);
        bomItemRepository.delete(bomItem);
    }
}
