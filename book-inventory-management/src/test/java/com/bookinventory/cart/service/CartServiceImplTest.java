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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookConditionRepository conditionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseLogRepository purchaseLogRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testGetCartOptionsByIsbn() {
        Book book = new Book();
        book.setIsbn("111-111-11111-1");

        Inventory inventory1 = new Inventory();
        inventory1.setInventoryId(10);
        inventory1.setIsbn("111-111-11111-1");
        inventory1.setRanks(1);
        inventory1.setPurchased(false);

        Inventory inventory2 = new Inventory();
        inventory2.setInventoryId(11);
        inventory2.setIsbn("111-111-11111-1");
        inventory2.setRanks(1);
        inventory2.setPurchased(false);

        BookCondition condition = new BookCondition();
        condition.setRanks(1);
        condition.setDescription("Good");
        condition.setPrice(BigDecimal.valueOf(499));

        when(bookRepository.findById("111-111-11111-1")).thenReturn(Optional.of(book));
        when(inventoryRepository.getAvailableInventoryByIsbn("111-111-11111-1"))
                .thenReturn(List.of(inventory1, inventory2));
        when(conditionRepository.getConditionByRank(1)).thenReturn(Optional.of(condition));

        List<CartOptionResponse> result = cartService.getCartOptionsByIsbn("111-111-11111-1");

        assertEquals(1, result.size());
        assertEquals("111-111-11111-1", result.get(0).getIsbn());
        assertEquals(1, result.get(0).getRank());
        assertEquals("Good", result.get(0).getCondition());
        assertEquals(BigDecimal.valueOf(499), result.get(0).getPrice());
        assertEquals(2L, result.get(0).getAvailableCount());
    }

//    @Test
//void testAddToCart() {
//    AddToCartRequest request = new AddToCartRequest();
//    request.setUserId(1);
//    request.setIsbn("111-111-11111-1");
//
//    User user = new User();
//    user.setUserId(1);
//
//    Book book = new Book();
//    book.setIsbn("111-111-11111-1");
//
//    Inventory inventory = new Inventory();
//    inventory.setInventoryId(10);
//    inventory.setIsbn("111-111-11111-1");
//    inventory.setRanks(1);
//    inventory.setPurchased(false);
//
//    when(userRepository.findById(1)).thenReturn(Optional.of(user));
//    when(bookRepository.findById("111-111-11111-1")).thenReturn(Optional.of(book));
//    when(cartRepository.existsById(any(ShoppingCartId.class))).thenReturn(false);
//    when(inventoryRepository.getAvailableInventoryByIsbn("111-111-11111-1"))
//            .thenReturn(List.of(inventory));
//
//    CartItemResponse result = cartService.addToCart(request);
//
//    assertEquals(1, result.getUserId());
//    assertEquals("111-111-11111-1", result.getIsbn());
//    verify(cartRepository).save(any(ShoppingCart.class));
//}

    @Test
    void testAddToCart_Duplicate() {
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(1);
        request.setIsbn("111-111-11111-1");

        User user = new User();
        user.setUserId(1);

        Book book = new Book();
        book.setIsbn("111-111-11111-1");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookRepository.findById("111-111-11111-1")).thenReturn(Optional.of(book));
        when(cartRepository.existsById(any(ShoppingCartId.class))).thenReturn(true);

        assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testGetCartByUser() {
        User user = new User();
        user.setUserId(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));

        List<CartItemResponse> result = cartService.getCartByUser(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("111-111-11111-1", result.get(0).getIsbn());
    }

    @Test
    void testGetCartViewByUser() {
        User user = new User();
        user.setUserId(1);
        user.setFirstName("A");
        user.setLastName("B");

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        Book book = new Book();
        book.setIsbn("111-111-11111-1");
        book.setTitle("Sample Book");

        Inventory inventory = new Inventory();
        inventory.setInventoryId(10);
        inventory.setIsbn("111-111-11111-1");
        inventory.setRanks(1);
        inventory.setPurchased(false);

        BookCondition condition = new BookCondition();
        condition.setRanks(1);
        condition.setDescription("Good");
        condition.setPrice(BigDecimal.valueOf(499));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));
        when(bookRepository.findById("111-111-11111-1")).thenReturn(Optional.of(book));
        when(inventoryRepository.getAvailableInventoryByIsbn("111-111-11111-1"))
                .thenReturn(List.of(inventory));
        when(conditionRepository.getConditionByRank(1)).thenReturn(Optional.of(condition));

        List<CartViewResponse> result = cartService.getCartViewByUser(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("A B", result.get(0).getUserName());
        assertEquals("111-111-11111-1", result.get(0).getIsbn());
        assertEquals("Sample Book", result.get(0).getBookTitle());
        assertEquals(1, result.get(0).getQualityOptions().size());
        assertEquals(1, result.get(0).getQualityOptions().get(0).getRank());
    }

    @Test
    void testRemoveFromCart() {
        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.existsById(any(ShoppingCartId.class))).thenReturn(true);

        assertDoesNotThrow(() -> cartService.removeFromCart(1, "111-111-11111-1"));
        verify(cartRepository).deleteById(any(ShoppingCartId.class));
    }

    @Test
    void testCheckoutAll_EmptyCart() {
        User user = new User();
        user.setUserId(1);

        CheckoutRequest request = new CheckoutRequest(1, List.of());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of());

        CheckoutResponse result = cartService.checkoutAll(request);

        assertFalse(result.isSuccess());
        assertEquals("Cart is empty", result.getMessage());
    }

    @Test
    void testCheckoutAll_MissingSelection() {
        User user = new User();
        user.setUserId(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        CheckoutRequest request = new CheckoutRequest(1, List.of());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));

        CheckoutResponse result = cartService.checkoutAll(request);

        assertFalse(result.isSuccess());
        assertEquals("Select quality choice for cart items", result.getMessage());

        verify(cartRepository, never()).delete(any());
        verify(inventoryRepository, never()).save(any());
        verify(purchaseLogRepository, never()).save(any());
    }

    @Test
    void testCheckoutAll_NoAvailableInventoryForSelectedRank() {
        User user = new User();
        user.setUserId(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        CheckoutRequest request = new CheckoutRequest(
                1,
                List.of(new CheckoutItemRequest("111-111-11111-1", 1))
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));
        when(inventoryRepository.getFirstAvailableInventoryByIsbnAndRank("111-111-11111-1", 1))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cartService.checkoutAll(request));

        verify(cartRepository, never()).delete(any());
        verify(purchaseLogRepository, never()).save(any());
    }

    @Test
    void testCheckoutAll_Success() {
        User user = new User();
        user.setUserId(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        Inventory inventory = new Inventory();
        inventory.setInventoryId(10);
        inventory.setIsbn("111-111-11111-1");
        inventory.setRanks(1);
        inventory.setPurchased(false);

        CheckoutRequest request = new CheckoutRequest(
                1,
                List.of(new CheckoutItemRequest("111-111-11111-1", 1))
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));
        when(inventoryRepository.getFirstAvailableInventoryByIsbnAndRank("111-111-11111-1", 1))
                .thenReturn(Optional.of(inventory));

        CheckoutResponse result = cartService.checkoutAll(request);

        assertTrue(result.isSuccess());
        assertEquals("Successfully checked out 1 items.", result.getMessage());
        assertTrue(inventory.getPurchased());

        verify(inventoryRepository).save(inventory);
        verify(cartRepository).delete(cart);
        verify(purchaseLogRepository).save(any(PurchaseLog.class));
    }

    @Test
    void testCheckoutAll_SavesCorrectPurchaseLog() {
        User user = new User();
        user.setUserId(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(1);
        cart.setIsbn("111-111-11111-1");

        Inventory inventory = new Inventory();
        inventory.setInventoryId(10);
        inventory.setIsbn("111-111-11111-1");
        inventory.setRanks(1);
        inventory.setPurchased(false);

        CheckoutRequest request = new CheckoutRequest(
                1,
                List.of(new CheckoutItemRequest("111-111-11111-1", 1))
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1)).thenReturn(List.of(cart));
        when(inventoryRepository.getFirstAvailableInventoryByIsbnAndRank("111-111-11111-1", 1))
                .thenReturn(Optional.of(inventory));

        cartService.checkoutAll(request);

        ArgumentCaptor<PurchaseLog> purchaseLogCaptor = ArgumentCaptor.forClass(PurchaseLog.class);
        verify(purchaseLogRepository).save(purchaseLogCaptor.capture());

        PurchaseLog savedLog = purchaseLogCaptor.getValue();
        assertNotNull(savedLog.getId());
        assertEquals(1, savedLog.getId().getUserId());
        assertEquals(10, savedLog.getId().getInventoryId());
    }

    @Test
    void testCheckoutAll_UserNotFound() {
        CheckoutRequest request = new CheckoutRequest(
                999,
                List.of(new CheckoutItemRequest("111-111-11111-1", 1))
        );

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.checkoutAll(request));
    }
}