package com.enmasse.Cart_Service.repository;

import com.enmasse.Cart_Service.model.CartItem;
import com.enmasse.Cart_Service.service.CartItemDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Using redis originally and automatically backing up data to progresql to avoid story
 */

@Repository
public class CartItemRepository {
    private static final String KEY = "cart_item";
    private final RedisTemplate<String, CartItem> redisTemplate;
    private final CartItemDbService cartItemDbService;
    @Autowired
    public CartItemRepository(RedisTemplate<String, CartItem> redisTemplate, CartItemJpaRepository cartItemJpaRepository, CartItemDbService cartItemDbService) {
        this.redisTemplate = redisTemplate;
        this.cartItemDbService = cartItemDbService;
        redisTemplate.expire(KEY, 1L, TimeUnit.DAYS);
    }

    public void save(CartItem cartItem, String userId) {
        redisTemplate.opsForHash().put(userId.toString(), cartItem.getProductId(), cartItem);
        cartItemDbService.saveOrUpdate(cartItem, userId);
    }

    public CartItem findByUserIdAndProductId(String userId, String productId) {
        return (CartItem)redisTemplate.opsForHash().get(userId.toString(), productId);
    }

    public List<CartItem> findAllByUserId(String userId) {
        return redisTemplate.opsForHash().values(userId.toString()).stream()
                .map(cartItem -> (CartItem) cartItem)
                .collect(Collectors.toList());
    }

    public void incrementQuantity(String userId, String productId) {
        CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(userId.toString(), productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            redisTemplate.opsForHash().put(userId.toString(), productId, cartItem);
            cartItemDbService.saveOrUpdate(cartItem, userId);

        }
        else {
            throw new RuntimeException("Cart item not found");
        }
    }

    public void decrementQuantity(String userId, String productId) {
        CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(userId.toString(), productId);
        if (cartItem != null && cartItem.getQuantity() > 0) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            redisTemplate.opsForHash().put(userId.toString(), productId, cartItem);
            cartItemDbService.saveOrUpdate(cartItem, userId);
        }
        else {
            throw new RuntimeException("Cart item not found");
        }
    }

    public void delete(String userId, String productId) {
        redisTemplate.opsForHash().delete(userId.toString(), productId);
        cartItemDbService.deleteByUserIdAndProductId(userId, productId);
    }
}
