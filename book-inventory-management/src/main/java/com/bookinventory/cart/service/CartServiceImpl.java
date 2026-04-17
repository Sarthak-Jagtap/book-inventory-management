package com.bookinventory.cart.service;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutItemRequest;
import com.bookinventory.cart.dto.CheckoutRequest;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.entity.Inventory;
import com.bookinventory.inventory.entity.ShoppingCart;
import com.bookinventory.inventory.entity.ShoppingCartId;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.inventory.repository.InventoryRepository;
import com.bookinventory.inventory.repository.ShoppingCartRepository;
import com.bookinventory.user.entity.PurchaseLog;
import com.bookinventory.user.entity.PurchaseLogId;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.PurchaseLogRepository;
import com.bookinventory.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final BookConditionRepository conditionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PurchaseLogRepository purchaseLogRepository;

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           InventoryRepository inventoryRepository,
                           BookConditionRepository conditionRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository,
                           PurchaseLogRepository purchaseLogRepository) {
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
        this.conditionRepository = conditionRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.purchaseLogRepository = purchaseLogRepository;
    }

    @Override
    public List<CartOptionResponse> getCartOptionsByIsbn(String isbn) {
        bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

        List<Inventory> inventoryList = inventoryRepository.getAvailableInventoryByIsbn(isbn);

        if (inventoryList == null || inventoryList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Long> countMap = new HashMap<>();

        for (Inventory inv : inventoryList) {
            Integer rank = inv.getRanks();
            if (rank != null) {
                countMap.put(rank, countMap.getOrDefault(rank, 0L) + 1);
            }
        }

        List<CartOptionResponse> responseList = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : countMap.entrySet()) {
            BookCondition condition = conditionRepository
                    .getConditionByRank(entry.getKey())
                    .orElseThrow(() -> new BadRequestException("Condition not found for rank: " + entry.getKey()));

            CartOptionResponse res = new CartOptionResponse();
            res.setIsbn(isbn);
            res.setRank(entry.getKey());
            res.setCondition(condition.getDescription());
            res.setPrice(condition.getPrice());
            res.setAvailableCount(entry.getValue());

            responseList.add(res);
        }

        responseList.sort(Comparator.comparing(CartOptionResponse::getRank));
        return responseList;
    }

    @Override
    public CartItemResponse addToCart(AddToCartRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));

        bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", request.getIsbn()));

        ShoppingCartId id = new ShoppingCartId(request.getUserId(), request.getIsbn());

        if (cartRepository.existsById(id)) {
            throw new BadRequestException("Item already in cart");
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(request.getUserId());
        cart.setIsbn(request.getIsbn());

        cartRepository.save(cart);

        return new CartItemResponse(request.getUserId(), request.getIsbn());
    }

    @Override
    public List<CartItemResponse> getCartByUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<ShoppingCart> cartList = cartRepository.findByUserId(userId);
        List<CartItemResponse> responseList = new ArrayList<>();

        for (ShoppingCart cart : cartList) {
            responseList.add(new CartItemResponse(cart.getUserId(), cart.getIsbn()));
        }

        return responseList;
    }

    @Override
    public List<CartViewResponse> getCartViewByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<ShoppingCart> cartList = cartRepository.findByUserId(userId);
        List<CartViewResponse> responseList = new ArrayList<>();

        String userName = user.getFirstName() + " " + user.getLastName();

        for (ShoppingCart cart : cartList) {
            Book book = bookRepository.findById(cart.getIsbn())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", cart.getIsbn()));

            List<CartOptionResponse> options = getCartOptionsByIsbn(cart.getIsbn());

            CartViewResponse response = new CartViewResponse();
            response.setUserId(cart.getUserId());
            response.setUserName(userName);
            response.setIsbn(cart.getIsbn());
            response.setBookTitle(book.getTitle());
            response.setQualityOptions(options);

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public void removeFromCart(Integer userId, String isbn) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        ShoppingCartId id = new ShoppingCartId(userId, isbn);

        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException("CartItem", "id", id);
        }

        cartRepository.deleteById(id);
    }

    @Override
@Transactional
public CheckoutResponse checkoutAll(CheckoutRequest request) {
    if (request == null || request.getUserId() == null) {
        throw new BadRequestException("User ID is required");
    }

    Integer userId = request.getUserId();

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

    List<ShoppingCart> cartItems = cartRepository.findByUserId(userId);

    if (cartItems == null || cartItems.isEmpty()) {
        return new CheckoutResponse(false, "Cart is empty");
    }

    Map<String, Integer> selectedRankByIsbn = new HashMap<>();
    if (request.getItems() != null) {
        for (CheckoutItemRequest item : request.getItems()) {
            if (item != null && item.getIsbn() != null && item.getRank() != null) {
                selectedRankByIsbn.put(item.getIsbn(), item.getRank());
            }
        }
    }

    for (ShoppingCart cartItem : cartItems) {
        Integer selectedRank = selectedRankByIsbn.get(cartItem.getIsbn());
        if (selectedRank == null) {
            return new CheckoutResponse(false, "Select quality choice for cart items");
        }
    }

    List<Inventory> inventoriesToPurchase = new ArrayList<>();

    for (ShoppingCart cartItem : cartItems) {
        Integer selectedRank = selectedRankByIsbn.get(cartItem.getIsbn());

        Inventory inventory = inventoryRepository
                .getFirstAvailableInventoryByIsbnAndRank(cartItem.getIsbn(), selectedRank)
                .orElseThrow(() -> new BadRequestException(
                        "No available copy for selected quality/rank: " + selectedRank + " for isbn: " + cartItem.getIsbn()
                ));

        inventoriesToPurchase.add(inventory);
    }

    for (Inventory inventory : inventoriesToPurchase) {
        inventory.setPurchased(true);
        inventoryRepository.save(inventory);

        PurchaseLogId purchaseLogId = new PurchaseLogId(userId, inventory.getInventoryId());
        PurchaseLog purchaseLog = new PurchaseLog(purchaseLogId, user);
        purchaseLogRepository.save(purchaseLog);
    }

    for (ShoppingCart cartItem : cartItems) {
        cartRepository.delete(cartItem);
    }

    return new CheckoutResponse(true, "Checkout successful for all cart items.");
}
}