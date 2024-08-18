package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p " +
            "where (:categoryId is null or :categoryId = 0 or p.category.id = :categoryId)" +
            " and (:keyword is null or :keyword = '' or p.name like %:keyword% or p.description like %:keyword%)")
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,Pageable pageable
    );
    @Query("select i from ProductImage i where i.product.id = :productId")
    Set<ProductImage> findImagesByProductId(@Param("productId") Long productId);

    @Query("select p from Product p where p.id in :ids")
    List<Product> findProductByIds(@Param("ids") List<Long> ids);
}
