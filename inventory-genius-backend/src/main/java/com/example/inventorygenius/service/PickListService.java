package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.repository.PickListRepository;

@Service
public class PickListService {

    @Autowired
    private PickListRepository pickListRepository;

    public List<PickList> getAllPickLists() {
        return pickListRepository.findAll();
    }

    public Optional<PickList> getPickListById(Long id) {
        return pickListRepository.findById(id);
    }

    public PickList createPickList(PickList pickList) {
        return pickListRepository.save(pickList);
    }

    public PickList updatePickList(Long id, PickList updatedPickList) {
        // Check if the picklist with the given id exists
        if (pickListRepository.existsById(id)) {
            updatedPickList.setPicklistId(id);
            return pickListRepository.save(updatedPickList);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }

    public void deletePickList(Long id) {
        // Check if the picklist with the given id exists
        if (pickListRepository.existsById(id)) {
            pickListRepository.deleteById(id);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }
}

