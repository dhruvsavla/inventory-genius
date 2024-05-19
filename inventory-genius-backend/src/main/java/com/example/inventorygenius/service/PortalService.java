package com.example.inventorygenius.service;

import com.example.inventorygenius.entity.Portal;
import com.example.inventorygenius.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortalService {

    @Autowired
    private PortalRepository portalRepository;

    public List<String> getAllPortalNames() {
        return portalRepository.findAll().stream()
                .map(Portal::getPortalName)
                .collect(Collectors.toList());
    }

    public Portal createPortal(Portal portal) {
        return portalRepository.save(portal);
    }
}
