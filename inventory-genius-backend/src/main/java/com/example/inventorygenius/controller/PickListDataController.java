package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.inventorygenius.service.PickListDataService;
import com.example.inventorygenius.service.PickListService;
import com.example.inventorygenius.entity.PickListData;

import java.util.List;

@RestController
@RequestMapping("/picklistdata")
public class PickListDataController {

    @Autowired
    private PickListDataService pickListDataService;

    @Autowired
    private PickListService pickListService;

    @GetMapping
    public List<PickListData> getAllPickListData() {
        return pickListDataService.getAllPickListData();
    }

    // Get picklist data by ID
    @GetMapping("/{pickListId}")
    public PickListData getPickListDataById(@PathVariable Long pickListId) {
        return pickListDataService.getPickListDataById(pickListId);
    }

    // Add new picklist data
    @PostMapping
    public PickListData addPickListData(@RequestBody PickListData pickListData) {
        return pickListDataService.addPickListData(pickListData);
    }

    // Update picklist data
    @PutMapping("/{pickListId}")
    public PickListData updatePickListData(@PathVariable Long pickListId, @RequestBody PickListData pickListData) {
        return pickListDataService.updatePickListData(pickListId, pickListData);
    }

    // Delete picklist data
    @DeleteMapping("/{pickListId}")
    public void deletePickListData(@PathVariable Long pickListId) {
        pickListDataService.deletePickListData(pickListId);
    }

    @DeleteMapping("/picklistnumber/{pickListNumber}")
    public void deletePickListDataByPickListNumber(@PathVariable Long pickListNumber) {
        List<PickListData> toDelete = pickListDataService.deletePickListDataByPickListNumber(pickListNumber);
        String bomCode = toDelete.get(0).getBomCode();
        pickListService.deletePickListByPickListNumber(pickListNumber, bomCode);
    }
}

