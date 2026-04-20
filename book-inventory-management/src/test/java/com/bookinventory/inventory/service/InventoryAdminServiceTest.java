package com.bookinventory.inventory.service;

import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;
import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.entity.Inventory;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryAdminServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookConditionRepository bookConditionRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private InventoryAdminServiceImpl inventoryAdminService;

    @Test
    void testUpdatePurchaseStatus_Success() {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1);
        inventory.setIsbn("1234567890123");
        inventory.setRanks(2);
        inventory.setPurchased(false);

        BookCondition condition = new BookCondition();
        condition.setRanks(2);
        condition.setDescription("Good");
        condition.setFullDescription("Good Condition");
        condition.setPrice(BigDecimal.valueOf(500));

        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setPurchased(true);

        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(bookConditionRepository.getConditionByRank(2)).thenReturn(Optional.of(condition));

        InventoryResponse response = inventoryAdminService.updatePurchaseStatus(1, request);

        assertNotNull(response);
        assertTrue(response.getPurchased());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testUpdatePurchaseStatus_NotFound() {
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setPurchased(true);

        when(inventoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryAdminService.updatePurchaseStatus(1, request));
    }

    @Test
    void testUpdatePurchaseStatus_MissingPurchasedField() {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1);
        inventory.setRanks(2);

        UpdateInventoryRequest request = new UpdateInventoryRequest();

        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));

        assertThrows(BadRequestException.class,
                () -> inventoryAdminService.updatePurchaseStatus(1, request));

        verify(inventoryRepository, never()).save(any());
    }
}