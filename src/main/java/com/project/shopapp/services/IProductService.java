package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest request);
    Product getProductById(long id) throws DataNotFoundException;
    Product updateProduct(long id,ProductDTO productDTO) throws DataNotFoundException;
    void deleteProduct(long id);
    boolean existByName(String name);
    ProductImage createProductImage(Long id, ProductImageDTO productImageDTO) throws Exception;
    List<ProductResponse> findProductByIds(List<Long> ids);
}
