package com.example.product.serviceTest;

import com.example.product.Data;
import com.example.product.models.Products;
import com.example.product.repository.ProductsDao;
import com.example.product.service.ProductsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private static ProductsDao productsDao;
    private static ProductsServiceImpl productsService;

    @BeforeAll
    public static void setUp(){
        productsDao = mock(ProductsDao.class);
        productsService = new ProductsServiceImpl(productsDao);
    }

    @Test
    void getProductsTest() {

        Flux<Products> pro = Flux.just(Data.getList());

        when(productsService.getProduct()).thenReturn(pro);

        Flux<Products> respBody = productsService.getProduct();

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNext(Data.getList())
                .verifyComplete();
    }

    @Test
    void findByIdProductTest() throws Exception{

        Mono<Products> products = Mono.just(Data.getList());
        when(productsService.findById(any())).thenReturn(products);

        Mono<Products> respBody = productsService.findById(any());

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getId().equals("12233d"))
                .verifyComplete();
    }

    @Test
    void searchProductTest() throws Exception{
        Mono<Products> products = Mono.just(Data.getList());
        when(productsService.findByDescription(any())).thenReturn(products);

        Mono<Products> respBody = productsService.findByDescription(any());

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getDescription().equals("Cuenta Ahorro"))
                .verifyComplete();
    }

    @Test
    void searchTypeProductTest() throws Exception{
        Flux<Products> products = Flux.just(Data.getList());
        when(productsService.findByType(any())).thenReturn(products);

        Flux<Products> respBody = productsService.findByType(any());

        StepVerifier.create(respBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getType().equals("Pasivos"))
                .expectComplete();
    }

    @Test
    void saveProductTest(){

        Products products = Data.getList();

        when(productsService.saveProduct(products)).thenReturn(Mono.just(products));

        Mono<Products> respBody = productsService.saveProduct(products);

        StepVerifier.create(respBody)
                .expectSubscription()
                //.expectNext(Data.getList())
                .expectComplete();
    }
}
