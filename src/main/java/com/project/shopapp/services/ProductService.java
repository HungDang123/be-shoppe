package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->new DataNotFoundException("Category is not exist"));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .url(productDTO.getThumbnail())
                .category(category)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest request) {
        return productRepository.searchProducts(categoryId,keyword,request).map(ProductResponse::fromProduct);
    }

    @Override
    public Product getProductById(long id) throws DataNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        Set<ProductImage> images = productRepository.findImagesByProductId(id);
        product.setProductImages(images);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existProduct = getProductById(id);
       if(existProduct!=null){
           Category category = categoryRepository.findById(productDTO.getCategoryId())
                   .orElseThrow(()->new DataNotFoundException("Category is not exist"));
           existProduct.setName(productDTO.getName());
           existProduct.setPrice(productDTO.getPrice());
           existProduct.setCategory(category);
           existProduct.setDescription(productDTO.getDescription());
           existProduct.setUrl(productDTO.getThumbnail());
           return productRepository.save(existProduct);
       }
       return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optional = productRepository.findById(id);
        if(optional.isPresent()){
            productRepository.deleteById(id);
        }
    }

    @Override
    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    @Transactional
    public ProductImage createProductImage(Long id,ProductImageDTO productImageDTO) throws Exception {
        Product existProduct = productRepository.findById(id).orElseThrow(
                ()->new DataNotFoundException("Product is not exist : "+productImageDTO.getProduct())
        );
        ProductImage productImage = ProductImage
                .builder()
                .product(existProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // không cho 1 sản phẩm iinsert quá 5 ảnh
      int size = productImageRepository.findByProductId(existProduct.getId()).size() ;
      if(size>=5){
          throw new InvalidParamException("Number of images must be <5");
      }
      return productImageRepository.save(productImage);
    }

    @Override
    public List<ProductResponse> findProductByIds(List<Long> ids) {
        List<Product> products = productRepository.findProductByIds(ids);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            ProductResponse productResponse = ProductResponse
                    .builder()
                    .name(p.getName())
                    .price(p.getPrice())
                    .description(p.getDescription())
                    .url(p.getUrl())
                    .categoryId(p.getCategory().getId())
                    .build();
            productResponses.add(productResponse);
        }
        return productResponses;
    }
}
