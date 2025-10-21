# 🛒 Shopping Cart Implementation Exercise

## Overview  
This project is a refactored Java Spring Boot implementation of a basic shopping cart system.  
It demonstrates clean code practices, proper object-oriented design, and clear documentation, turning a simple coding exercise into production-ready, maintainable code.

## Objectives  
- Improve readability, maintainability, and correctness of the original code.  
- Preserve all existing business functionality (adding items and calculating totals).  
- Apply best practices such as encapsulation, precision with `BigDecimal`, and thread-safe design.  
- Provide detailed Javadoc documentation for clarity and maintainability.  

## Improvements  
- Introduced `CartItem` and `ShoppingCart` classes for cleaner structure.  
- Replaced `double` with `BigDecimal` for accurate financial calculations.  
- Used `ConcurrentHashMap` to ensure thread-safe operations.  
- Reduced code duplication and improved method clarity.  
- Added comprehensive inline documentation following Java standards.  

## API Endpoints  

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **POST** | `/shop/addItem` | Adds an item to a cart (creates one if missing). |
| **GET** | `/shop/getTotal` | Returns the total value of the cart. |

### Example Request  
```bash
POST /shop/addItem?cartId=cart1&itemName=Book&price=120.50&quantity=2
````

### Example Response

```bash
Item added successfully. Current total: 241.00
```

## Technologies

* Java 25
* Spring Boot
* RESTful API design principles

