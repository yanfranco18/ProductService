package com.example.product.service;

import com.example.product.models.Products;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductsService {

        public Flux<Products> findAll();

        public Mono<Products> save(Products products);

        public Mono<Void> delete(Products products);

        public Mono<Products> findByNameProduct(String nameProduct);

        public Mono<Products> findById(String id);

        public Flux<Products> findByTypeProduct(String typeProduct);


}





