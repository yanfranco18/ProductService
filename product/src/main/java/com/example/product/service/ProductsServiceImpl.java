package com.example.product.service;

import com.example.product.models.Products;
import com.example.product.repository.ProductsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ProductsServiceImpl implements IProductsService{

    @Autowired
    private ProductsDao productDao;

    @Override
    public Flux<Products> findAll() {
        return productDao.findAll();
    }

    @Override
    public Mono<Products> save(Products products) {
        return productDao.save(products);
    }

    @Override
    public Mono<Void> delete(Products products) {
        return productDao.delete(products);
    }

    @Override
    public Mono<Products> findByNameProduct(String nameProduct) {
        return productDao.findByNameProduct(nameProduct);
    }

    @Override
    public Mono<Products> findById(String id) {
        return productDao.findById(id);
    }

    @Override
    public Flux<Products> findByTypeProduct(String typeProduct) {
        return productDao.findByTypeProduct(typeProduct);
    }
}
