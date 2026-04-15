package com.bookinventory.inventory.service;

import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.inventory.dto.InventoryRequest;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.InventorySummaryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;
import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.entity.Inventory;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.inventory.repository.InventoryRepository;
import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryAdminServiceImpl implements InventoryAdminService {

    private final InventoryRepository inventoryRepository;
    private final BookConditionRepository bookConditionRepository;
    private final BookRepository bookRepository;

    public InventoryAdminServiceImpl(InventoryRepository inventoryRepository,
                                     BookConditionRepository bookConditionRepository,
                                     BookRepository bookRepository) {
        this.inventoryRepository = inventoryRepository;
        this.bookConditionRepository = bookConditionRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public InventoryResponse addInventory(InventoryRequest request) {
        validateInventoryRequest(request);

        bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", request.getIsbn()));

        BookCondition condition = bookConditionRepository.getConditionByRank(request.getRank())
                .orElseThrow(() -> new BadRequestException(
                        "Invalid rank. Book condition not found for rank: " + request.getRank()
                ));

        Inventory inventory = new Inventory();
        inventory.setIsbn(request.getIsbn());
        inventory.setRanks(request.getRank());
        inventory.setPurchased(request.getPurchased() != null ? request.getPurchased() : false);

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved, condition);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        List<InventoryResponse> responseList = new ArrayList<>();

        for (Inventory inventory : inventoryList) {
            responseList.add(mapToResponse(inventory));
        }

        responseList.sort(Comparator.comparing(InventoryResponse::getInventoryId));
        return responseList;
    }

    @Override
    public InventoryResponse getInventoryById(Integer inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "inventoryId", inventoryId));

        return mapToResponse(inventory);
    }

    @Override
    public List<InventoryResponse> getInventoryByIsbn(String isbn) {
        bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

        List<Inventory> inventoryList = inventoryRepository.getInventoryByIsbn(isbn);
        List<InventoryResponse> responseList = new ArrayList<>();

        for (Inventory inventory : inventoryList) {
            responseList.add(mapToResponse(inventory));
        }

        responseList.sort(Comparator.comparing(InventoryResponse::getInventoryId));
        return responseList;
    }

    @Override
    public List<InventoryResponse> getAvailableInventoryByIsbn(String isbn) {
        bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

        List<Inventory> inventoryList = inventoryRepository.getAvailableInventoryByIsbn(isbn);
        List<InventoryResponse> responseList = new ArrayList<>();

        for (Inventory inventory : inventoryList) {
            responseList.add(mapToResponse(inventory));
        }

        responseList.sort(Comparator.comparing(InventoryResponse::getInventoryId));
        return responseList;
    }

    @Override
    public List<InventorySummaryResponse> getInventorySummaryByIsbn(String isbn) {
        bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

        List<Inventory> inventoryList = inventoryRepository.getInventoryByIsbn(isbn);

        Map<Integer, Long> totalCountByRank = new HashMap<>();
        Map<Integer, Long> availableCountByRank = new HashMap<>();

        for (Inventory inventory : inventoryList) {
            Integer rank = inventory.getRanks();

            totalCountByRank.put(rank, totalCountByRank.getOrDefault(rank, 0L) + 1);

            if (!Boolean.TRUE.equals(inventory.getPurchased())) {
                availableCountByRank.put(rank, availableCountByRank.getOrDefault(rank, 0L) + 1);
            }
        }

        List<InventorySummaryResponse> responseList = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : totalCountByRank.entrySet()) {
            Integer rank = entry.getKey();
            Long totalCount = entry.getValue();
            Long availableCount = availableCountByRank.getOrDefault(rank, 0L);

            BookCondition condition = bookConditionRepository.getConditionByRank(rank)
                    .orElseThrow(() -> new BadRequestException(
                            "Book condition not found for rank: " + rank
                    ));

            responseList.add(new InventorySummaryResponse(
                    isbn,
                    rank,
                    condition.getDescription(),
                    condition.getFullDescription(),
                    condition.getPrice(),
                    availableCount,
                    totalCount
            ));
        }

        responseList.sort(Comparator.comparing(InventorySummaryResponse::getRank));
        return responseList;
    }

    @Override
    public InventoryResponse updateInventory(Integer inventoryId, UpdateInventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "inventoryId", inventoryId));

        if (request.getRank() != null) {
            bookConditionRepository.getConditionByRank(request.getRank())
                    .orElseThrow(() -> new BadRequestException(
                            "Invalid rank. Book condition not found for rank: " + request.getRank()
                    ));
            inventory.setRanks(request.getRank());
        }

        if (request.getPurchased() != null) {
            inventory.setPurchased(request.getPurchased());
        }

        Inventory updated = inventoryRepository.save(inventory);
        return mapToResponse(updated);
    }

    @Override
    public InventoryResponse markAsPurchased(Integer inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "inventoryId", inventoryId));

        if (Boolean.TRUE.equals(inventory.getPurchased())) {
            throw new BadRequestException("Inventory item already purchased: " + inventoryId);
        }

        inventory.setPurchased(true);
        Inventory updated = inventoryRepository.save(inventory);

        return mapToResponse(updated);
    }

    @Override
    public void deleteInventory(Integer inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "inventoryId", inventoryId));

        inventoryRepository.delete(inventory);
    }

    private void validateInventoryRequest(InventoryRequest request) {
        if (request.getIsbn() == null || request.getIsbn().isBlank()) {
            throw new BadRequestException("ISBN is required");
        }

        if (request.getRank() == null) {
            throw new BadRequestException("Rank is required");
        }
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        BookCondition condition = bookConditionRepository.getConditionByRank(inventory.getRanks())
                .orElseThrow(() -> new BadRequestException(
                        "Book condition not found for rank: " + inventory.getRanks()
                ));

        return mapToResponse(inventory, condition);
    }

    private InventoryResponse mapToResponse(Inventory inventory, BookCondition condition) {
        return new InventoryResponse(
                inventory.getInventoryId(),
                inventory.getIsbn(),
                inventory.getRanks(),
                inventory.getPurchased(),
                condition.getDescription(),
                condition.getFullDescription(),
                condition.getPrice()
        );
    }

    @Override
    public List<InventoryResponse> getInventoryByRank(Integer rank) {
        bookConditionRepository.getConditionByRank(rank)
                .orElseThrow(() -> new BadRequestException(
                        "Book condition not found for rank: " + rank
                ));

        List<Inventory> inventoryList = inventoryRepository.findByRanks(rank);
        List<InventoryResponse> responseList = new ArrayList<>();

        for (Inventory inventory : inventoryList) {
            responseList.add(mapToResponse(inventory));
        }

        return responseList;
    }
}