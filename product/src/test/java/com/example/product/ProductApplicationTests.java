package com.example.product;

import com.example.product.controllers.ProductsController;
import com.example.product.models.Products;
import com.example.product.service.ProductsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ProductsController.class)
class ProductApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ProductsServiceImpl service;

	@Test
	public void getProductsTest() {

		Flux<Products> pro = Flux.just(Data.getList());

		when(service.getProduct()).thenReturn(pro);

		Flux<Products> respBody = webTestClient.get().uri("/products")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(Products.class)
				.getResponseBody();

		StepVerifier.create(respBody)
				.expectSubscription()
				.expectNext(Data.getList())
				.verifyComplete();
	}

	@Test
	public void saveProductTest(){

		Products products = Data.getList();

		when(service.saveProduct(products)).thenReturn(Mono.just(products));

		webTestClient.post().uri("/products")
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(products), Products.class)
				.exchange()
				.expectStatus().isCreated();
	}

	@Test
	public void deleteProductTest() throws Exception{

		when(service.deleteProduct("12233d")).thenReturn(Mono.empty());

		webTestClient.delete()
				.uri("/products/12233d")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNoContent();
	}

	@Test
	public void findByIdProductTest() throws Exception{

		Mono<Products> products = Mono.just(Data.getList());
		when(service.findById(any())).thenReturn(products);

		Flux<Products> respBody = webTestClient.get().uri("/products/getById/12233d")
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
	public void searchProductTest() throws Exception{
		Mono<Products> products = Mono.just(Data.getList());
		when(service.findByDescription(any())).thenReturn(products);

		Flux<Products> respBody = webTestClient.get().uri("/products/Cuenta Ahorro")
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
	public void searchTypeProductTest() throws Exception{
		Flux<Products> products = Flux.just(Data.getList());
		when(service.findByType(any())).thenReturn(products);

		Flux<Products> respBody = webTestClient.get().uri("/products/search/Pasivos")
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
