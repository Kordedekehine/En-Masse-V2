package com.enmasse.Cart_Service.repository;


import com.enmasse.Cart_Service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(Long userId, String productId);
    List<CartItem> findAllByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, String productId);

}
