package com.example.product.controllerTest;

import com.example.product.Data;
import com.example.product.controllers.ProductsController;
import com.example.product.models.Products;
import com.example.product.service.ProductsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductControllerTest {

    private static WebTestClient webTestClient;
    @Mock
    private static ProductsServiceImpl productsService;

    @BeforeAll
    public static void setUp(){
        productsService = mock(ProductsServiceImpl.class);
        webTestClient = WebTestClient.bindToController(new ProductsController(productsService))
                .configureClient()
                .baseUrl("/products")
                .build();
    }

    @Test
    void getProductsTest() {

        Flux<Products> pro = Flux.just(Data.getList());

        when(productsService.getProduct()).thenReturn(pro);

        Flux<Products> respBody = webTestClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() //200
                .returnResult(Products.class)
                .getResponseBody();

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNext(Data.getList())
                .verifyComplete();
    }

    @Test
    void saveProductTest(){

        Products products = Data.getList();

        when(productsService.saveProduct(products)).thenReturn(Mono.just(products));

        webTestClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(products), Products.class)
                .exchange()
                .expectStatus().isCreated(); //201
    }

    @Test
    void deleteProductTest() throws Exception{

        when(productsService.deleteProduct("12233d")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/12233d")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void findByIdProductTest() throws Exception{

        Mono<Products> products = Mono.just(Data.getList());
        when(productsService.findById(any())).thenReturn(products);

        Flux<Products> respBody = webTestClient.get().uri("/getById/12233d")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Products.class)
                .getResponseBody();

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getId().equals("12233d"))
                .verifyComplete();
    }

    @Test
    void searchProductTest() throws Exception{
        Mono<Products> products = Mono.just(Data.getList());
        when(productsService.findByDescription(any())).thenReturn(products);

        Flux<Products> respBody = webTestClient.get().uri("/Cuenta Ahorro")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Products.class)
                .getResponseBody();

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getDescription().equals("Cuenta Ahorro"))
                .verifyComplete();
    }

    @Test
    void searchTypeProductTest() throws Exception{
        Flux<Products> products = Flux.just(Data.getList());
        when(productsService.findByType(any())).thenReturn(products);

        Flux<Products> respBody = webTestClient.get().uri("/search/Pasivos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Products.class)
                .getResponseBody();

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getType().equals("Pasivos"))
                .expectComplete();
    }
}
