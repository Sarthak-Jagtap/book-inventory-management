package com.bookinventory.cart.service;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.dto.SelectedCartItem;
import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.entity.Inventory;
import com.bookinventory.inventory.entity.ShoppingCart;
import com.bookinventory.inventory.entity.ShoppingCartId;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.inventory.repository.InventoryRepository;
import com.bookinventory.inventory.repository.ShoppingCartRepository;
import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.entity.PurchaseLog;
import com.bookinventory.user.entity.PurchaseLogId;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.PurchaseLogRepository;
import com.bookinventory.user.repository.UserRepository;
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
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PurchaseLogRepository purchaseLogRepository;

    private final Map<String, SelectedCartItem> selectedItems = new HashMap<>();

    public CartServiceImpl(ShoppingCartRepository cartRepository,
                           InventoryRepository inventoryRepository,
                           BookConditionRepository bookConditionRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository,
                           PurchaseLogRepository purchaseLogRepository) {
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookConditionRepository = bookConditionRepository;
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
                    .orElseThrow(() -> new BadRequestException(
                            "Condition not found for rank: " + rank
                    ));

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
    public CartItemResponse addToCart(AddToCartRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));

        bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", request.getIsbn()));

        ShoppingCartId cartId = new ShoppingCartId(request.getUserId(), request.getIsbn());

        Optional<Inventory> selectedInventoryOptional =
                inventoryRepository.getFirstAvailableInventoryByIsbnAndRank(
                        request.getIsbn(),
                        request.getRank()
                );

        if (selectedInventoryOptional.isEmpty()) {
            throw new BadRequestException(
                    "Selected quality/rank is not available for isbn: " + request.getIsbn()
            );
        }

        Inventory selectedInventory = selectedInventoryOptional.get();

        BookCondition condition = bookConditionRepository.getConditionByRank(request.getRank())
                .orElseThrow(() -> new BadRequestException(
                        "Condition not found for selected rank: " + request.getRank()
                ));

        String selectionKey = buildSelectionKey(request.getUserId(), request.getIsbn());

        SelectedCartItem selectedCartItem = new SelectedCartItem(
                request.getUserId(),
                request.getIsbn(),
                selectedInventory.getInventoryId(),
                request.getRank(),
                condition.getPrice()
        );

        selectedItems.put(selectionKey, selectedCartItem);

        ShoppingCart cart;
        if (cartRepository.existsById(cartId)) {
            cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "CartItem",
                            "userId/isbn",
                            request.getUserId() + "/" + request.getIsbn()
                    ));
        } else {
            cart = new ShoppingCart();
            cart.setUserId(request.getUserId());
            cart.setIsbn(request.getIsbn());
            cart = cartRepository.save(cart);
        }

        return mapToCartItemResponse(cart);
    }

    @Override
    public List<CartItemResponse> getCartByUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<ShoppingCart> cartList = cartRepository.findByUserId(userId);
        List<CartItemResponse> responseList = new ArrayList<>();

        for (ShoppingCart cart : cartList) {
            responseList.add(mapToCartItemResponse(cart));
        }

        return responseList;
    }

    @Override
    public List<CartViewResponse> getCartViewByUser(Integer userId) {
        List<ShoppingCart> cartList = cartRepository.findByUserId(userId);
        List<CartViewResponse> responseList = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        String userName = user.getFirstName() + " " + user.getLastName();

        for (ShoppingCart cart : cartList) {
            Book book = bookRepository.findById(cart.getIsbn())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", cart.getIsbn()));

            String selectionKey = buildSelectionKey(cart.getUserId(), cart.getIsbn());
            SelectedCartItem selectedCartItem = selectedItems.get(selectionKey);

            if (selectedCartItem == null) {
                responseList.add(new CartViewResponse(
                        cart.getUserId(),
                        userName,
                        cart.getIsbn(),
                        book.getTitle(),
                        false,
                        null,
                        null,
                        null
                ));
            } else {
                BookCondition condition = bookConditionRepository.getConditionByRank(selectedCartItem.getRank())
                        .orElseThrow(() -> new BadRequestException(
                                "Condition not found for rank: " + selectedCartItem.getRank()
                        ));

                responseList.add(new CartViewResponse(
                        cart.getUserId(),
                        userName,
                        cart.getIsbn(),
                        book.getTitle(),
                        true,
                        selectedCartItem.getRank(),
                        condition.getDescription(),
                        selectedCartItem.getPrice()
                ));
            }
        }

        return responseList;
    }

    @Override
    public void removeFromCart(Integer userId, String isbn) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        ShoppingCartId cartId = new ShoppingCartId(userId, isbn);

        if (!cartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("CartItem", "userId/isbn", userId + "/" + isbn);
        }

        cartRepository.deleteById(cartId);
        selectedItems.remove(buildSelectionKey(userId, isbn));
    }

    @Override
    public CheckoutResponse checkoutAll(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<ShoppingCart> cartList = cartRepository.findByUserId(userId);

        if (cartList.isEmpty()) {
            return new CheckoutResponse(false, "Cart is empty");
        }

        for (ShoppingCart cart : cartList) {
            String selectionKey = buildSelectionKey(cart.getUserId(), cart.getIsbn());
            SelectedCartItem selectedCartItem = selectedItems.get(selectionKey);

            if (selectedCartItem == null) {
                throw new BadRequestException(
                        "Selection missing for isbn: " + cart.getIsbn() + ". Please reselect quality before checkout."
                );
            }
        }

        for (ShoppingCart cart : cartList) {
            String selectionKey = buildSelectionKey(cart.getUserId(), cart.getIsbn());
            SelectedCartItem selectedCartItem = selectedItems.get(selectionKey);

            Inventory inventory = inventoryRepository.findById(selectedCartItem.getInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory",
                            "inventoryId",
                            selectedCartItem.getInventoryId()
                    ));

            if (Boolean.TRUE.equals(inventory.getPurchased())) {
                throw new BadRequestException(
                        "Inventory item already purchased: " + inventory.getInventoryId()
                );
            }
        }

        for (ShoppingCart cart : cartList) {
            String selectionKey = buildSelectionKey(cart.getUserId(), cart.getIsbn());
            SelectedCartItem selectedCartItem = selectedItems.get(selectionKey);

            Inventory inventory = inventoryRepository.findById(selectedCartItem.getInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory",
                            "inventoryId",
                            selectedCartItem.getInventoryId()
                    ));

            inventory.setPurchased(true);
            inventoryRepository.save(inventory);

            PurchaseLogId purchaseLogId = new PurchaseLogId(userId, inventory.getInventoryId());

            PurchaseLog log = new PurchaseLog();
            log.setId(purchaseLogId);
            log.setUser(user);

            purchaseLogRepository.save(log);

            cartRepository.deleteById(new ShoppingCartId(cart.getUserId(), cart.getIsbn()));
            selectedItems.remove(selectionKey);
        }

        return new CheckoutResponse(true, "Checkout successful for all cart items.");
    }

    private String buildSelectionKey(Integer userId, String isbn) {
        return userId + "_" + isbn;
    }

    private CartItemResponse mapToCartItemResponse(ShoppingCart cart) {
        return new CartItemResponse(cart.getUserId(), cart.getIsbn());
    }
}