package com.example.product.controllers;

import com.example.product.models.Products;
import com.example.product.service.IProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private CircuitBreakerFactory cbFactory;

    private final IProductsService productService;

    //Metodo listar, usando response entity para manejar la respuesta del status y la respuesta del body
    @GetMapping
    public Mono<ResponseEntity<Flux<Products>>>  getProduct(){
        log.info("iniciando lista");
        return Mono.just(
                //manejo de la respuesta http
                ResponseEntity.ok()
                        //mostrar en el body mediante json
                        .contentType(MediaType.APPLICATION_JSON)
                        //mostrando en el body la respuesta
                        .body(productService.findAll()));
    }

    //Metodo para eliminar
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id){
        //buscamos el id
        return productService.findById(id)
                //eliminamos el producto encontrado, a traves de un flatMap
                .flatMap(p -> { return productService.delete(p)
                        //Convertir la respuesta Mono<Void> en un entity de tipo Product, usando mono.just
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                    //Validamos si el producto existe en la base de datos
                }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    //Metodo para editar, pasamos por el requestBody el producto a modificar
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Products>> edit(@RequestBody Products products, @PathVariable String id){
        //buscamos el id para obtener el product
        return productService.findById(id)
                //A traves del flatMap actualizamos los campos para modificar
                .flatMap(p ->{
                    p.setNameProduct(products.getNameProduct());
                    p.setTypeProduct(products.getTypeProduct());
                    p.setNumberCard(products.getNumberCard());
                    return productService.save(p);
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
    @PostMapping
    public Mono<ResponseEntity<Products>> create(@RequestBody Products products){
        //validamos la fecha en caso venga fecha, asigamos la fecha
        if(products.getCreateDate()==null){
            products.setCreateDate(new Date());
        }
        //ahora guardamos el producto, mediante map, cambiamos el flujo de tipo mono a un responseEntity
        return productService.save(products)
                //mostramos el estado en el http, indicamos la uri del producto se crea
                .map(p -> ResponseEntity.created(URI.create("/products/".concat(p.getId())))
                //Modificamos la respuesta en el body con el contentType
                        .contentType(MediaType.APPLICATION_JSON)
                        //Y pasamos el producto creado
                        .body(p));
    }

    //metodo buscar por nameProduct
    @GetMapping("/{nameProduct}")
    public Mono<ResponseEntity<Products>> search(@PathVariable String nameProduct){
        //buscamos el tipo de producto
        return productService.findByNameProduct(nameProduct)
                //mostramos la respuesta
                .map(p -> ResponseEntity.ok()
                        //Modificamos la respuesta en el body con el contentType
                        .contentType(MediaType.APPLICATION_JSON)
                        //devolvemos el objeto obtenido
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //metodo buscar por typeProduct
    @GetMapping("/search/{typeProduct}")
    public Flux<Products> searchType(@PathVariable String typeProduct){
        return productService.findByTypeProduct(typeProduct);
    }


}
