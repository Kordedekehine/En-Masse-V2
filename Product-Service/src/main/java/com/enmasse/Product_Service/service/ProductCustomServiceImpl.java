package com.enmasse.Product_Service.service;

import com.enmasse.Product_Service.dto.ProductFilterRequestDto;
import com.enmasse.Product_Service.entity.Product;
import com.enmasse.Product_Service.repository.ProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductCustomServiceImpl implements ProductRepositoryCustom {

    @Autowired
    private EntityManager entityManager;


    @Override
    public Page<Product> findByFilters(Pageable pageable, ProductFilterRequestDto filter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.category() != null) {
            predicates.add(cb.equal(root.get("category"), filter.category()));
        }

        if (filter.brandId() != null) {
            predicates.add(cb.equal(root.get("brand").get("id"), filter.brandId()));
        }

        if (filter.storeId() != null) {
            predicates.add(cb.equal(root.get("storeId"), filter.storeId()));
        }

        if (filter.partOfNameOrDescription() != null) {
            String pattern = "%" + filter.partOfNameOrDescription().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            ));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdOn"))); // optional sorting

        // Query for result
        TypedQuery<Product> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        List<Product> results = query.getResultList();
        return new PageImpl<>(results, pageable, total);
    }
}
