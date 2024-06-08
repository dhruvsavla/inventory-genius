package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.inventorygenius.service.PackingListDataService;
import com.example.inventorygenius.service.PackingListService;
import com.example.inventorygenius.entity.PackingListData;

import java.util.List;

@RestController
@RequestMapping("/packinglistdata")
public class PackingListDataController {

    @Autowired
    private PackingListDataService packingListDataService;

    @Autowired
    private PackingListService packingListService;

    @GetMapping
    public List<PackingListData> getAllPickListData() {
        return packingListDataService.getAllPackingListData();
    }

    // Get picklist data by ID
    @GetMapping("/{pickListId}")
    public PackingListData getPickListDataById(@PathVariable Long pickListId) {
        return packingListDataService.getPackingListDataById(pickListId);
    }

    // Add new picklist data
    @PostMapping
    public PackingListData addPackListData(@RequestBody PackingListData packListData) {
        return packingListDataService.addPackingListData(packListData);
    }

    // Update picklist data
    @PutMapping("/{pickListId}")
    public PackingListData updatePickListData(@PathVariable Long pickListId, @RequestBody PackingListData pickListData) {
        return packingListDataService.updatePackingListData(pickListId, pickListData);
    }

    // Delete picklist data
    @DeleteMapping("/{pickListId}")
    public void deletePickListData(@PathVariable Long pickListId) {
        packingListDataService.deletePackListData(pickListId);
    }

    @DeleteMapping("/packinglistData/{packListNumber}")
    public void deletePackListDataByNumber(@PathVariable Long packListNumber){
        packingListDataService.deletePickListDataByPickListNumber(packListNumber);
        packingListService.deleteByPackingListNumber(packListNumber);
    }
}

