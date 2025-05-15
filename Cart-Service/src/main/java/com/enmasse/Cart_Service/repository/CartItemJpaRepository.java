package com.enmasse.Cart_Service.repository;


import com.enmasse.Cart_Service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//RAMONTADA REPO
@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(String userId, String productId);
    List<CartItem> findAllByUserId(String userId);
    void deleteByUserIdAndProductId(String userId, String productId);

}
