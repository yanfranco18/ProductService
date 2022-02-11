package com.example.product.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Products")
public class Products {

    @Id
    private String id;
    private String nameProduct;
    private String numberCard;
    private String typeProduct;
    private String accountNumber;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date createDate;



}
