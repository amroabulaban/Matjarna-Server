package com.matjarna.facade.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.matjarna.dto.EntityDtoList;
import com.matjarna.dto.product.ProductDto;
import com.matjarna.dto.product.ProductFilters;
import com.matjarna.exception.ValidationException;
import com.matjarna.mapper.product.ProductMapper;
import com.matjarna.model.country.Country;
import com.matjarna.model.language.Language;
import com.matjarna.model.product.Product;
import com.matjarna.service.product.IProductService;

@Service
public class ProductFacade implements IProductFacade {

	@Autowired
	IProductService productService;

	@Autowired
	ProductMapper productMapper;

	@Override
	public ProductDto saveProduct(ProductDto productDto) {
		Product product = productMapper.toProduct(productDto);
		Product productReturned = productService.saveProduct(product);
		ProductDto productToReturn = productMapper.toDto(productReturned);
		return productToReturn;
	}

	@Override
	public boolean checkUniqueness(Long id, String code) {
		Product product = productService.getByCode(code);
		if (product == null || (id != null && product.getId() == id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public EntityDtoList<ProductDto, Product> getProducts(Pageable pageable, Language language, ProductFilters search,
			Country country) {
		Page<Product> page = productService.getProducts(pageable, language, search, country);
		EntityDtoList<ProductDto, Product> entityDtoList = new EntityDtoList<>(page, productMapper::toDto);
		return entityDtoList;
	}

	@Override
	public ProductDto getProductById(Long id, Country country, Language language) {
		Product product = productService.getProductById(id, country, language);
		if (product == null) {
			throw new ValidationException("Product was not found");
		}
		ProductDto productDto = productMapper.toDto(product);
		return productDto;
	}

	public void deleteProduct(Long id) {
		productService.deleteProduct(id);
	}

}
