package com.enmasse.Cart_Service.controller;

import com.enmasse.Cart_Service.dto.CartItemRequest;
import com.enmasse.Cart_Service.dto.CartResponse;
import com.enmasse.Cart_Service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    @Autowired
   private CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addToCart(@RequestBody CartItemRequest cartItemRequest, @RequestParam(name="userId") Long userId) {
        return cartService.addToCart(cartItemRequest, userId);
    }

    @GetMapping
    public CartResponse getCartItems(@RequestParam(name="userId") Long userId) {
        return cartService.getCartItems(userId);
    }

    @GetMapping("/{productId}/increment")
    public CartResponse incrementQuantity(@RequestParam(name="userId") Long userId, @PathVariable String productId) {
        return cartService.incrementQuantity(userId, productId);
    }

    @GetMapping("/{productId}/decrement")
    public CartResponse decrementQuantity(@RequestParam(name="userId") Long userId, @PathVariable String productId) {
        return cartService.decrementQuantity(userId, productId);
    }

    @DeleteMapping("/{productId}")
    public CartResponse delete(@RequestParam(name="userId") Long userId, @PathVariable String productId) {
        return cartService.removeFromCart(userId, productId);
    }

}
