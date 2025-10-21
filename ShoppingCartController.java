package com.example.shop;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a single item in a shopping cart.
 * Encapsulates the item's name, unit price, and quantity.
 * Provides functionality to calculate subtotal and ensures
 * valid quantity values.
 */
class CartItem {

    private final String itemName;
    private final BigDecimal price;
    private int quantity;

    /**
     * Constructs a CartItem with a given name, price, and quantity.
     *
     * @param itemName the name of the product
     * @param price    the unit price of the product (must be non-negative)
     * @param quantity the number of units being purchased (must be positive)
     * @throws IllegalArgumentException if price or quantity are invalid
     */
    public CartItem(String itemName, BigDecimal price, int quantity) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    /** @return the product name */
    public String getItemName() {
        return itemName;
    }

    /** @return the unit price */
    public BigDecimal getPrice() {
        return price;
    }

    /** @return the current quantity in cart */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Increases the item quantity by a given amount.
     *
     * @param amount additional quantity to add (must be positive)
     */
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Calculates the total price for this item (price * quantity).
     *
     * @return subtotal for this item
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}

/**
 * Represents a shopping cart containing multiple {@link CartItem}s.
 * Provides operations for adding items and calculating totals.
 */
class ShoppingCart {

    // Use a map keyed by item name to easily update quantities
    private final Map<String, CartItem> items = new ConcurrentHashMap<>();

    /**
     * Adds an item to the cart. If the item already exists,
     * increments its quantity instead of creating a new entry.
     *
     * @param itemName the name of the product
     * @param price    the unit price of the product
     * @param quantity the quantity to add
     */
    public void addItem(String itemName, BigDecimal price, int quantity) {
        items.merge(itemName,
                new CartItem(itemName, price, quantity),
                (existingItem, newItem) -> {
                    existingItem.addQuantity(newItem.getQuantity());
                    return existingItem;
                });
    }

    /**
     * Computes the total cost of all items currently in the cart.
     *
     * @return total value of the cart
     */
    public BigDecimal calculateTotal() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns all items currently in the cart.
     *
     * @return unmodifiable view of cart contents
     */
    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }
}

/**
 * REST controller that exposes shopping cart operations via HTTP endpoints.
 * <p>
 * This simplified implementation stores cart data in memory.
 * In production, this would typically be backed by a database or caching layer.
 */
@RestController
@RequestMapping("/shop")
public class ShoppingCartController {

    /**
     * Thread-safe in-memory storage for shopping carts.
     * Key: cartId (e.g., session or user ID)
     * Value: corresponding {@link ShoppingCart} instance.
     */
    private final Map<String, ShoppingCart> carts = new ConcurrentHashMap<>();

    /**
     * Adds an item to a user's shopping cart. If the cart does not exist,
     * it is created automatically.
     *
     * @param cartId   unique identifier for the shopping cart
     * @param itemName the name of the item
     * @param price    unit price of the item
     * @param quantity number of units to add
     * @return confirmation message with the updated cart total
     */
    @PostMapping("/addItem")
    public String addItem(@RequestParam("cartId") String cartId,
            @RequestParam("itemName") String itemName,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity) {

        // Retrieve or create a new shopping cart
        ShoppingCart cart = carts.computeIfAbsent(cartId, id -> new ShoppingCart());

        // Add the item to the cart
        cart.addItem(itemName, BigDecimal.valueOf(price), quantity);

        // Compute updated total
        BigDecimal total = cart.calculateTotal();

        System.out.println("Cart " + cartId + " total: " + total);

        return String.format("Item added successfully. Current total: %s", total);
    }

    /**
     * Retrieves the total price for all items in the specified cart.
     *
     * @param cartId the unique identifier of the shopping cart
     * @return a message displaying the current total, or an error if the cart is
     *         missing
     */
    @GetMapping("/getTotal")
    public String getTotal(@RequestParam("cartId") String cartId) {
        ShoppingCart cart = carts.get(cartId);

        if (cart == null) {
            return "Error: Cart not found.";
        }

        BigDecimal total = cart.calculateTotal();
        return String.format("Current total: %s", total);
    }
}
