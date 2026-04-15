package com.bookinventory.user.service;

import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.dto.PurchaseLogRequestDTO;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.entity.PurchaseLog;
import com.bookinventory.user.entity.PurchaseLogId;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.PurchaseLogRepository;
import com.bookinventory.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseLogServiceImpl implements PurchaseLogService {

    private final PurchaseLogRepository purchaseLogRepository;
    private final UserRepository        userRepository;

    // Constructor injection
    public PurchaseLogServiceImpl(PurchaseLogRepository purchaseLogRepository,
                                  UserRepository userRepository) {
        this.purchaseLogRepository = purchaseLogRepository;
        this.userRepository        = userRepository;
    }

    // Convert PurchaseLog entity -> DTO
    private PurchaseLogResponseDTO convertToDTO(PurchaseLog purchaseLog) {
        PurchaseLogResponseDTO dto = new PurchaseLogResponseDTO();
        dto.setUserId(purchaseLog.getId().getUserId());
        dto.setInventoryId(purchaseLog.getId().getInventoryId());

        if (purchaseLog.getUser() != null) {
            dto.setUserFirstName(purchaseLog.getUser().getFirstName());
            dto.setUserLastName(purchaseLog.getUser().getLastName());
            dto.setUserName(purchaseLog.getUser().getUserName());
        }
        return dto;
    }

    // ADD PURCHASE
    @Override
    @Transactional
    public PurchaseLogResponseDTO addPurchase(PurchaseLogRequestDTO dto) {

        Integer userId      = dto.getUserId();
        Integer inventoryId = dto.getInventoryId();

        // 1. Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId));

        // 2. Check if this purchase already exists (duplicate prevention)
        if (purchaseLogRepository.existsById_UserIdAndId_InventoryId(
                userId, inventoryId)) {
            throw new BadRequestException(
                    "User with ID " + userId +
                    " has already purchased inventory item with ID " + inventoryId);
        }

        // 3. Build composite key and PurchaseLog entity
        PurchaseLogId purchaseLogId = new PurchaseLogId(userId, inventoryId);
        PurchaseLog   purchaseLog   = new PurchaseLog(purchaseLogId, user);

        // 4. Save and return
        PurchaseLog saved = purchaseLogRepository.save(purchaseLog);
        return convertToDTO(saved);
    }
    
    // GET PURCHASE HISTORY BY USER
    @Override
    public List<PurchaseLogResponseDTO> getPurchasesByUser(Integer userId) {

        // Validate user exists first
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        List<PurchaseLog> purchases =
                purchaseLogRepository.findPurchasesByUserId(userId);

        List<PurchaseLogResponseDTO> dtoList = new ArrayList<>();
        for (PurchaseLog purchase : purchases) {
            dtoList.add(convertToDTO(purchase));
        }
        return dtoList;
    }

    // GET ONLY INVENTORY IDs PURCHASED BY A USER
    @Override
    public List<Integer> getInventoryIdsByUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }
        return purchaseLogRepository.findInventoryIdsByUserId(userId);
    }

    // CHECK IF USER HAS PURCHASED A SPECIFIC ITEM
    @Override
    public boolean hasPurchased(Integer userId, Integer inventoryId) {
        return purchaseLogRepository
                .existsById_UserIdAndId_InventoryId(userId, inventoryId);
    }

    // COUNT PURCHASES BY USER
    @Override
    public long getPurchaseCount(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }
        return purchaseLogRepository.countById_UserId(userId);
    }

    // GET ALL PURCHASES FOR A SPECIFIC INVENTORY ITEM
    @Override
    public List<PurchaseLogResponseDTO> getPurchasesByInventory(Integer inventoryId) {

        List<PurchaseLog> purchases =
                purchaseLogRepository.findById_InventoryId(inventoryId);

        List<PurchaseLogResponseDTO> dtoList = new ArrayList<>();
        for (PurchaseLog purchase : purchases) {
            dtoList.add(convertToDTO(purchase));
        }
        return dtoList;
    }
    
    // In PurchaseLogServiceImpl — add:
    @Override
    public List<PurchaseLogResponseDTO> getAllPurchases() {
        List<PurchaseLogResponseDTO> result = new ArrayList<>();
        for (PurchaseLog p : purchaseLogRepository.findAll())
            result.add(convertToDTO(p));
        return result;
    }
}