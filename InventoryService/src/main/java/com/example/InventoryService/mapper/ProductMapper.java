package com.example.InventoryService.mapper;

import com.example.InventoryService.dto.ProductDto;
import com.example.InventoryService.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto productToProductDto(Product product);
    Product productDtoToProduct(ProductDto productDto);

}
