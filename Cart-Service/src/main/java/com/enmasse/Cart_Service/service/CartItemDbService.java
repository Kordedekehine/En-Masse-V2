package com.enmasse.Cart_Service.service;

import com.enmasse.Cart_Service.model.CartItem;
import com.enmasse.Cart_Service.repository.CartItemJpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemDbService {

  private final   CartItemJpaRepository cartItemJpaRepository;

    public CartItemDbService(CartItemJpaRepository cartItemJpaRepository) {
        this.cartItemJpaRepository = cartItemJpaRepository;
    }

    @Async
    public void saveOrUpdate(CartItem cartItem, Long userId) {
        Optional<CartItem> existing = cartItemJpaRepository.findByUserIdAndProductId(userId, cartItem.getProductId());
        existing.ifPresentOrElse(
                item -> {
                    item.setQuantity(cartItem.getQuantity());
                    cartItemJpaRepository.save(item);
                },
                () -> {
                    cartItem.setUserId(userId);
                    cartItemJpaRepository.save(cartItem);
                }
        );
    }

    @Async
    public void deleteByUserIdAndProductId(Long userId, String productId) {
        cartItemJpaRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
