package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.inventorygenius.entity.PackingList;
import com.example.inventorygenius.repository.PackingListRepository;

@Service
public class PackingListService {

    @Autowired
    private PackingListRepository packingListRepository;

    public List<PackingList> getAllPickLists() {
        return packingListRepository.findAll();
    }

    public Optional<PackingList> getPickListById(Long id) {
        return packingListRepository.findById(id);
    }

    public PackingList createPickList(PackingList pickList) {
        return packingListRepository.save(pickList);
    }

    public PackingList updatePickList(Long id, PackingList updatedPickList) {
        // Check if the picklist with the given id exists
        if (packingListRepository.existsById(id)) {
            updatedPickList.setPackinglistId(id);
            return packingListRepository.save(updatedPickList);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }

    public void deletePickList(Long id) {
        // Check if the picklist with the given id exists
        if (packingListRepository.existsById(id)) {
            packingListRepository.deleteById(id);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }
}

