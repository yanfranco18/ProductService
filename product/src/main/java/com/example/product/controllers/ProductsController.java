package com.example.product.controllers;

import com.example.product.models.Products;
import com.example.product.service.IProductsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {

    private final IProductsService productService;

    //@CircuitBreaker, name(va el nombre de la instancia usado en la configuracion yml, "items")
    //fallbackMethod, permite manejar el error, mediante un metodo definido
    //anotacion para el timeout - @TimeLimiter name(va el nombre de la instancia usado en la configuracion yml, "items")
    //ComplatebleFuture<"tipo">, es envolver una llamada asincrona, represeta futura que ocurre en el tiempo, maneja un generic
    //supplyAsync, permite envolver la llamada en una futura asincrona del tiempo
    //metodo buscar por typeProduct
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @GetMapping("/search/{type}")
    public Flux<Products> searchType(@PathVariable String type){
        return productService.findByType(type);
    }

    //Metodo listar, usando response entity para manejar la respuesta del status y la respuesta del body
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @GetMapping
    public Mono<ResponseEntity<Flux<Products>>> getProduct(){
        log.info("iniciando lista");
        return Mono.just(
                //manejo de la respuesta http
                ResponseEntity.ok()
                        //mostrar en el body mediante json
                        .contentType(MediaType.APPLICATION_JSON)
                        //mostrando en el body la respuesta
                        .body(productService.getProduct()));
    }

    //Metodo para eliminar
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct (@PathVariable String id){

        return productService.deleteProduct(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    //Metodo para editar, pasamos por el requestBody el producto a modificar
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Products>> editProduct (@RequestBody Products products, @PathVariable String id){
        //buscamos el id para obtener el product
        return productService.findById(id)
                //A traves del flatMap actualizamos los campos para modificar
                .flatMap(p ->{
                    p.setDescription(products.getDescription());
                    p.setType(products.getType());
                    p.setNumber(products.getNumber());
                    return productService.saveProduct(p);
                })
                //Utilizando el Map cambiamos la respuesta de Mono a un ResponseEntity
                //mediante created pasamos la uri, y con concat concatenemos el id
                .map(p -> ResponseEntity.created(URI.create("/products/".concat(p.getId())))
                        //Modificamos la respeusta en el body con el contentType
                        .contentType(MediaType.APPLICATION_JSON)
                        //Y pasamos el producto modificado
                        .body(p))
                //para manejar el error si el producto no existe, y build para generar la respuesta sin cuerpo
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //metodo crear
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @PostMapping
    public Mono<ResponseEntity<Products>> saveProduct (@RequestBody Products products){
        //validamos la fecha en caso venga fecha, asigamos la fecha
        if(products.getCreateDate()==null){
            products.setCreateDate(new Date());
        }
        //ahora guardamos el producto, mediante map, cambiamos el flujo de tipo mono a un responseEntity
        return productService.saveProduct(products)
                //mostramos el estado en el http, indicamos la uri del producto se crea
                .map(p -> ResponseEntity.created(URI.create("/products/".concat(p.getId())))
                //Modificamos la respuesta en el body con el contentType
                        .contentType(MediaType.APPLICATION_JSON)
                        //Y pasamos el producto creado
                        .body(p));
    }

    //metodo buscar por description
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @GetMapping("/{description}")
    public Mono<ResponseEntity<Products>> search(@PathVariable String description){
        //buscamos el tipo de producto
        return productService.findByDescription(description)
                //mostramos la respuesta
                .map(p -> ResponseEntity.ok()
                        //Modificamos la respuesta en el body con el contentType
                        .contentType(MediaType.APPLICATION_JSON)
                        //devolvemos el objeto obtenido
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //metodo buscar por id
    @CircuitBreaker(name="products", fallbackMethod = "fallback")
    @TimeLimiter(name="products")
    @GetMapping("/getById/{id}")
    public Mono<ResponseEntity<Products>> getById(@PathVariable String id){
        return productService.findById(id)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //metodo para manejar el error
    private String fallback(HttpServerErrorException ex) {
        return "Response 200, fallback method for error:  " + ex.getMessage();
    }

}
