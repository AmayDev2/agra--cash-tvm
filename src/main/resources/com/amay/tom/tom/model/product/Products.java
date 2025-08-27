package com.amay.tom.model.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Data
@Accessors(chain = true)
public class Products {
    private ProductDTO[] productList;
}
