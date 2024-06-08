package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.inventorygenius.repository.PickListDataRepository;
import com.example.inventorygenius.entity.PickListData;

import java.util.List;

@Service
public class PickListDataService {

    @Autowired
    private PickListDataRepository pickListDataRepository;

    // Get all picklist data
    public List<PickListData> getAllPickListData() {
        return pickListDataRepository.findAll();
    }

    // Get picklist data by ID
    public PickListData getPickListDataById(Long pickListId) {
        return pickListDataRepository.findById(pickListId).orElse(null);
    }

    // Add new picklist data
    public PickListData addPickListData(PickListData pickListData) {
        return pickListDataRepository.save(pickListData);
    }

    // Update picklist data
    public PickListData updatePickListData(Long pickListId, PickListData pickListData) {
        pickListData.setPickListId(pickListId);
        return pickListDataRepository.save(pickListData);
    }

    // Delete picklist data
    public void deletePickListData(Long pickListId) {
        pickListDataRepository.deleteById(pickListId);
    }

    public List<PickListData> deletePickListDataByPickListNumber(Long pickListNumber) {
        // Find the list of PickListData objects by pickListNumber
        List<PickListData> pickListDataList = pickListDataRepository.findByPickListNumber(pickListNumber);
        
        // Check if the list is not empty
        if (!pickListDataList.isEmpty()) {
            // Delete all PickListData objects in the list
            pickListDataRepository.deleteAll(pickListDataList);
            
            // Return the list of deleted PickListData objects
            return pickListDataList;
        } else {
            // Throw an exception if no PickListData objects are found with the given pickListNumber
            throw new IllegalArgumentException("No picklist data found with the provided picklist number: " + pickListNumber);
        }
    }
    
}
