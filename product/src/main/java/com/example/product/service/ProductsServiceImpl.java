package com.example.product.service;

import com.example.product.models.Products;
import com.example.product.repository.ProductsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ProductsServiceImpl implements IProductsService{

    private final ProductsDao productDao;

    @Override
    public Flux<Products> getProduct() {
        return productDao.findAll();
    }

    @Override
    public Mono<Products> saveProduct(Products products) {
        return productDao.save(products);
    }

    @Override
    public Mono<Void> deleteProduct(Products products) {
        return productDao.delete(products);
    }

    @Override
    public Mono<Products> findByDescription(String description) {
        return productDao.findByDescription(description);
    }

    @Override
    public Mono<Products> findById(String id) {
        return productDao.findById(id);
    }

    @Override
    public Flux<Products> findByType(String type) {
        return productDao.findByType(type);
    }
}
