package com.example.product;

import com.example.product.models.Products;

import java.util.Date;

public class Data {

    public static Products getList(){
        Products pro = new Products();
        pro.setId("12233d");
        pro.setDescription("Cuenta Ahorro");
        pro.setNumber("456367568765");
        pro.setType("Pasivos");
        pro.setMaxMovements(20);
        pro.setCommission(0.0);
        pro.setCreateDate(new Date(2022-02-16));
        return pro;
    }

}
