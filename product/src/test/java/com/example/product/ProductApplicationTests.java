package com.example.product;

import com.example.product.controllers.ProductsController;
import com.example.product.models.Products;
import com.example.product.service.ProductsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

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

		Flux<Products> pro = Flux.just(new Products("12233d", "Cuenta Ahorro", "456367568765", "Pasivos",new Date(2022-02-16)),
				new Products("12245f", "Tarjeta Credito", "456757567654", "Activos",new Date(2022-02-16)));
		when(service.getProduct()).thenReturn(pro);

		Flux<Products> respBody = webTestClient.get().uri("/products")
				.exchange()
				.expectStatus().isOk()
				.returnResult(Products.class)
				.getResponseBody();

		StepVerifier.create(respBody)
				.expectSubscription()
				.expectNext(new Products("12233d", "Cuenta Ahorro", "456367568765", "Pasivos",new Date(2022-02-16)))
				.expectNext(new Products("12245f", "Tarjeta Credito", "456757567654", "Activos",new Date(2022-02-16)))
				.verifyComplete();
	}

	@Test
	public void saveProductTest(){

		Products products = new Products("12233d", "Cuenta Ahorro", "456367568765", "Pasivos",new Date());

		when(service.saveProduct(products)).thenReturn(Mono.just(products));

		webTestClient.post().uri("/products")
				.body(Mono.just(products), Products.class)
				.exchange()
				.expectStatus().isCreated();
	}

	@Test
	public void deleteProductTest() throws Exception{

		/*Mono<Products> idProduct = service.findById("12233d");

		Products product = ;


		when(service.deleteProduct(product)).thenReturn(Mono.empty());

		webTestClient.delete().uri("/products/12233d")
				.exchange()
				.expectStatus().isNoContent();*/

	}

}
