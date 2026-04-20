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

        ShoppingCartId cartId = new ShoppingCartId(request.getUserId(), request.getIsbn());

        if (cartRepository.existsById(cartId)) {
            throw new BadRequestException("Book already exists in cart for this user");
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(request.getUserId());
        cart.setIsbn(request.getIsbn());
        cart = cartRepository.save(cart);

        return new CartItemResponse(cart.getUserId(), cart.getIsbn());
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
            throw new ResourceNotFoundException("CartItem", "userId/isbn", userId + "/" + isbn);
        }

        cartRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CheckoutResponse checkoutAll(CheckoutRequest request) {
        if (request == null || request.getUserId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("User ID and items are required for checkout");
        }

        Integer userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<String> processedIsbns = new ArrayList<>();
        int successCount = 0;

        for (CheckoutItemRequest item : request.getItems()) {
            String isbn = item.getIsbn();
            Integer rank = item.getRank();

            if (isbn == null || rank == null) continue;

            // 1. Find and mark inventory as purchased
            Inventory inventory = inventoryRepository
                    .getFirstAvailableInventoryByIsbnAndRank(isbn, rank)
                    .orElse(null);

            if (inventory == null) {
                // If one item fails, we could either fail the whole transaction or skip.
                // Given the transactional nature and user request, failing with a clear message is better.
                throw new BadRequestException("No available copy for ISBN: " + isbn + " with rank: " + rank);
            }

            inventory.setPurchased(true);
            inventoryRepository.save(inventory);

            // 2. Update Purchase Log
            PurchaseLogId logId = new PurchaseLogId(userId, inventory.getInventoryId());
            PurchaseLog log = new PurchaseLog(logId, user);
            purchaseLogRepository.save(log);

            // 3. Remove from Cart
            ShoppingCartId cartId = new ShoppingCartId(userId, isbn);
            if (cartRepository.existsById(cartId)) {
                cartRepository.deleteById(cartId);
            }

            successCount++;
        }

        return new CheckoutResponse(true, "Successfully checked out " + successCount + " items.");
    }
}