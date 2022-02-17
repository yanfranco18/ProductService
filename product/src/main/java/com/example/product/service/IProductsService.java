package com.example.product.service;

import com.example.product.models.Products;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductsService {

        public Flux<Products> getProduct();

        public Mono<Products> saveProduct(Products products);

        public Mono<Void> deleteProduct(Products products);

        public Mono<Products> findByDescription(String description);

        public Mono<Products> findById(String id);

        public Flux<Products> findByType(String type);


}





