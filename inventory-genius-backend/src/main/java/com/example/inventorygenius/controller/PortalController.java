package com.example.inventorygenius.controller;

import com.example.inventorygenius.entity.Portal;
import com.example.inventorygenius.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/portals")
public class PortalController {

    @Autowired
    private PortalService portalService;

    @GetMapping
    public ResponseEntity<List<String>> getAllPortalNames() {
        List<String> portalNames = portalService.getAllPortalNames();
        return ResponseEntity.ok(portalNames);
    }

    @PostMapping
    public ResponseEntity<Portal> createPortal(@RequestBody Portal portal) {
        Portal createdPortal = portalService.createPortal(portal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortal);
    }
}
