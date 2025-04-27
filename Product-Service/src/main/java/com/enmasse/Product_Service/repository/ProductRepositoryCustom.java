package com.enmasse.Product_Service.repository;

import com.enmasse.Product_Service.dto.ProductFilterRequestDto;
import com.enmasse.Product_Service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> findByFilters(Pageable pageable, ProductFilterRequestDto filter);

}
