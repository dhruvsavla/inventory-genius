package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemSupplierService {
    private final ItemRepository itemRepository;
    private StorageService storageService;
    private StockService stockService;

    @Autowired
    public ItemSupplierService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Autowired
    public void setStorageService(@Lazy StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    public void setStockService(@Lazy StockService stockService) {
        this.stockService = stockService;
    }

    public List<Item> getAllItems(){
        return itemRepository.findAll();
    }
    
    public Item getItemBySKUCode(String skuCode) {
        System.out.println("item found in repo = " + itemRepository.findBySKUCode(skuCode));
        return itemRepository.findBySKUCode(skuCode);
    }

    public Item updateItem(Long itemId, Item updatedItem) {
        Optional<Item> existingItemOptional = itemRepository.findById(itemId);
    
        if (existingItemOptional.isPresent()) {
            Item existingItem = existingItemOptional.get();
            System.out.println("Existing Item Storages: " + existingItem.getStorages().size());
    
            // Capture old SKUCode if it changes
            String oldSKUCode = existingItem.getSKUCode();
            String newSKUCode = updatedItem.getSKUCode();
    
            // Update properties of the existing item with the new values
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPackOf(updatedItem.getPackOf());
            existingItem.setParentSKU(updatedItem.getParentSKU());
            existingItem.setBarcode(updatedItem.getBarcode());
            existingItem.setSKUCode(updatedItem.getSKUCode());
            List<Storage> storages = existingItem.getStorages();
                System.out.println("storages.size = " + storages.size());
                for (Storage s : storages) {
                    System.out.println("itm service sku" + s.getSkucode());
                    s.setSkucode(newSKUCode);
                    storageService.updateStorage(s.getStorageId(), s);
                }
            List<Stock> stocks = existingItem.getStockEntries();
            for (Stock stock : stocks){
                stock.setSkucode(newSKUCode);
                stockService.updateStock(stock.getStockId(), stock);
            }
            existingItem.setGroup1(updatedItem.getGroup1());
            existingItem.setGroup2(updatedItem.getGroup2());
            existingItem.setGroup3(updatedItem.getGroup3());
            existingItem.setSizeRange(updatedItem.getSizeRange());
            existingItem.setSize(updatedItem.getSize());
            existingItem.setUnit(updatedItem.getUnit());
            existingItem.setSellerSKUCode(updatedItem.getSellerSKUCode());
            existingItem.setSellingPrice(updatedItem.getSellingPrice());
            existingItem.setMrp(updatedItem.getMrp());
            existingItem.setImg(updatedItem.getImg());
            existingItem.setSuppliers(updatedItem.getSuppliers());
            existingItem.setStorages(updatedItem.getStorages());
            existingItem.setBoms(updatedItem.getBoms());
            existingItem.setStockEntries(updatedItem.getStockEntries());
            existingItem.setOrders(updatedItem.getOrders());
            existingItem.setBomItems(updatedItem.getBomItems());
    
            Item savedItem = itemRepository.save(existingItem);
            
            // Flush persistence context to synchronize state
            //itemRepository.flush();
    
            // If SKUCode changes, update dependent items
            if (!oldSKUCode.equals(newSKUCode)) {
                System.out.println("in the if");
                updateDependentItems(oldSKUCode, newSKUCode);
            }
            
            return savedItem;
        } else {
            throw new RuntimeException("Item not found with id: " + itemId);
        }
    }
    

    private List<Item> getDependentItems(String parentSKU) {
        return itemRepository.findByParentSKU(parentSKU);
    }

    private void updateDependentItems(String oldSKUCode, String newSKUCode) {
        List<Item> dependentItems = getDependentItems(oldSKUCode);
        for (Item dependentItem : dependentItems) {
            dependentItem.setParentSKU(newSKUCode); // Update the parent SKU
            itemRepository.save(dependentItem);
        }
    }

    public Item findItemsBySellerSKUAndDescription(String sellerSKU, String description) {
        return itemRepository.findBySellerSKUCodeAndDescription(sellerSKU, description);
    }
}
