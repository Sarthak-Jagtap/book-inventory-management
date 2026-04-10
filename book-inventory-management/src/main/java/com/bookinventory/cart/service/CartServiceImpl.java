package com.bookinventory.cart.service;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.dto.SelectedCartItem;
import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.entity.Inventory;
import com.bookinventory.inventory.entity.ShoppingCart;
import com.bookinventory.inventory.entity.ShoppingCartId;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.inventory.repository.InventoryRepository;
import com.bookinventory.inventory.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final BookConditionRepository bookConditionRepository;

    // temporary in-memory selection store
    // key format: userId + "_" + isbn
    private final Map<String, SelectedCartItem> selectedItems = new HashMap<>();

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           InventoryRepository inventoryRepository,
                           BookConditionRepository bookConditionRepository) {
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookConditionRepository = bookConditionRepository;
    }

    @Override
    public List<CartOptionResponse> getCartOptionsByIsbn(String isbn) {

        List<Inventory> inventoryList = inventoryRepository.getAvailableInventoryByIsbn(isbn);

        if (inventoryList == null || inventoryList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Long> rankCountMap = new HashMap<>();

        for (Inventory inventory : inventoryList) {
            Integer rank = inventory.getRanks();
            if (rank != null) {
                rankCountMap.put(rank, rankCountMap.getOrDefault(rank, 0L) + 1);
            }
        }

        List<CartOptionResponse> responseList = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : rankCountMap.entrySet()) {
            Integer rank = entry.getKey();
            Long count = entry.getValue();

            BookCondition condition = bookConditionRepository.getConditionByRank(rank)
                    .orElseThrow(() -> new RuntimeException("Condition not found for rank: " + rank));

            CartOptionResponse response = new CartOptionResponse();
            response.setIsbn(isbn);
            response.setRank(rank);
            response.setCondition(condition.getDescription());
            response.setPrice(condition.getPrice());
            response.setAvailableCount(count);

            responseList.add(response);
        }

        responseList.sort(Comparator.comparing(CartOptionResponse::getRank));
        return responseList;
    }

    @Override
    public ShoppingCart addToCart(AddToCartRequest request) {

        ShoppingCartId cartId = new ShoppingCartId(request.getUserId(), request.getIsbn());

        if (cartRepository.existsById(cartId)) {
            throw new RuntimeException("Book already exists in cart for this user");
        }

        Optional<Inventory> selectedInventoryOptional =
        		inventoryRepository.getFirstAvailableInventoryByIsbnAndRank(request.getIsbn(), request.getRank());

        if (selectedInventoryOptional.isEmpty()) {
            throw new RuntimeException("Selected quality/rank is not available");
        }

        Inventory selectedInventory = selectedInventoryOptional.get();

        BookCondition condition = bookConditionRepository.findById(request.getRank())
                .orElseThrow(() -> new RuntimeException("Condition not found for selected rank"));

        // temporary selection saved in memory only
        String selectionKey = buildSelectionKey(request.getUserId(), request.getIsbn());

        SelectedCartItem selectedCartItem = new SelectedCartItem(
                request.getUserId(),
                request.getIsbn(),
                selectedInventory.getInventoryId(),
                request.getRank(),
                condition.getPrice()
        );

        selectedItems.put(selectionKey, selectedCartItem);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(request.getUserId());
        cart.setIsbn(request.getIsbn());

        return cartRepository.save(cart);
    }

    @Override
    public List<ShoppingCart> getCartByUser(Integer userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public void removeFromCart(Integer userId, String isbn) {
        cartRepository.deleteById(new ShoppingCartId(userId, isbn));
        selectedItems.remove(buildSelectionKey(userId, isbn));
    }

    @Override
    public CheckoutResponse checkout(Integer userId, String isbn) {

        ShoppingCartId cartId = new ShoppingCartId(userId, isbn);

        if (!cartRepository.existsById(cartId)) {
            return new CheckoutResponse(false, "Book is not present in cart");
        }

        String selectionKey = buildSelectionKey(userId, isbn);
        SelectedCartItem selectedCartItem = selectedItems.get(selectionKey);

        // case 2: session/in-memory object lost
        if (selectedCartItem == null) {
            return new CheckoutResponse(
                    false,
                    "Selected quality option not available in current session. Please reselect price/quality and checkout again."
            );
        }

        Optional<Inventory> inventoryOptional = inventoryRepository.findById(selectedCartItem.getInventoryId());

        if (inventoryOptional.isEmpty()) {
            return new CheckoutResponse(false, "Selected inventory item no longer exists");
        }

        Inventory inventory = inventoryOptional.get();

        // revalidation before final checkout
        if (Boolean.TRUE.equals(inventory.getPurchased())) {
            return new CheckoutResponse(false, "Selected inventory item is already purchased. Please reselect another option.");
        }

        inventory.setPurchased(true);
        inventoryRepository.save(inventory);

        cartRepository.deleteById(cartId);
        selectedItems.remove(selectionKey);

        return new CheckoutResponse(
                true,
                "Checkout successful. Inventory updated and item removed from cart."
        );
    }

    private String buildSelectionKey(Integer userId, String isbn) {
        return userId + "_" + isbn;
    }
}