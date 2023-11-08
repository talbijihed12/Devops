package tn.esprit.devops_project.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.devops_project.entities.Product;
import tn.esprit.devops_project.entities.ProductCategory;
import tn.esprit.devops_project.entities.Stock;
import tn.esprit.devops_project.repositories.ProductRepository;
import tn.esprit.devops_project.repositories.StockRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.config.name=application-test")
@ActiveProfiles
class ProductServiceImplTest {

    @Mock
    private StockRepository mockstockRepository;
    @Mock
    private ProductRepository mockproductRepository;

    @Autowired
    private StockRepository stockRepositoryy;

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private StockServiceImpl stockService;
    @Autowired
    private ProductRepository productRepository;
    @Mock
    private StockServiceImpl mockstockService;
    @InjectMocks
    private ProductServiceImpl mockproductServicee;

    @Test
    void testAddProduct() {
        Stock stock=new Stock();
        stock.setIdStock(1L);
        stock.setTitle("jihed");
        stockService.addStock(stock);
        Stock existingStock = stockRepositoryy.findById(stock.getIdStock())
                .orElseThrow(() -> new RuntimeException("stock not found"));
        Product product = new Product();
        product.setTitle("products");
        product.setPrice(19.99f);
        product.setQuantity(10);
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setStock(existingStock);
        Product addedProduct = productService.addProduct(product, stock.getIdStock());
        assertNotNull(addedProduct.getIdProduct(), "Product id should not be null");
        assertEquals("products", addedProduct.getTitle(), "product title is incorrect");
        assertEquals(19.99f, addedProduct.getPrice(), 0.01, "Product price is incorrect");
        assertEquals(10, addedProduct.getQuantity(), "Product quantity is incorrect");
        assertEquals(ProductCategory.ELECTRONICS, addedProduct.getCategory(), "Product category is incorrect");
        assertEquals(stock.getIdStock(), addedProduct.getStock().getIdStock(), "Product should be associated with the correct stock");
    }
    @Test
    void testAddProduct2(){
        Long existingStockId = 1L;

        Stock existingStock = stockRepositoryy.findById(existingStockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        Product product = new Product();
        product.setTitle("Original Product");
        product.setPrice(29.99f);
        product.setQuantity(5);
        product.setCategory(ProductCategory.BOOKS);
        product.setStock(existingStock);
        Product addedProduct = productService.addProduct(product, existingStockId);
        addedProduct.setTitle("Modified Product");
        addedProduct.setPrice(39.99f);
        addedProduct.setQuantity(15);
        addedProduct.setCategory(ProductCategory.CLOTHING);
        Product updatedProduct = productService.updateProduct(addedProduct);
        assertNotNull(updatedProduct.getIdProduct(), "Product id should not be null");
        assertEquals("Modified Product", updatedProduct.getTitle(), "Product title is incorrect after modification");
        assertEquals(39.99f, updatedProduct.getPrice(), 0.01, "Product price is incorrect after modification");
        assertEquals(15, updatedProduct.getQuantity(), "Product quantity is incorrect after modification");
        assertEquals(ProductCategory.CLOTHING, updatedProduct.getCategory(), "Product category is incorrect after modification");
        assertEquals(existingStockId, updatedProduct.getStock().getIdStock(), "Product should be associated with the correct stock");
        Product originalProductInStock = stockRepositoryy.findById(existingStockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"))
                .getProducts()
                .stream()
                .filter(p -> p.getIdProduct().equals(addedProduct.getIdProduct()))
                .findFirst()
                .orElse(null);


    }
    @Test
    void testAddProduct3(){
        Stock stock = new Stock();
        stock.setTitle("Stock11");
        stockRepositoryy.save(stock);
        Product product = new Product();
        product.setTitle("Test Product");
        product.setPrice(10.0f);
        product.setQuantity(20);
        product.setCategory(ProductCategory.ELECTRONICS);
        Product savedProduct = productService.addProduct(product, stock.getIdStock());

        assertNotNull(savedProduct.getIdProduct(), "Product should have an ID");
        assertTrue(savedProduct.getPrice() >= 10.0f && savedProduct.getPrice() <= 20.0f, "Price should be within the expected range");
        assertTrue(savedProduct.getQuantity() > 10, "Quantity should be greater than 10");
        assertNotNull(savedProduct.getCategory(), "Category should not be null");
        assertEquals(stock.getIdStock(), savedProduct.getStock().getIdStock(), "Product should be associated with the correct stock");
        assertTrue(savedProduct.getTitle().contains("Test"), "Title should contain 'Test'");
    }
    @Test
    @Transactional
    void testAddProduct4() {
        Stock newStock = new Stock();
        newStock.setTitle("Stock4");
        Stock savedStock = stockRepositoryy.save(newStock);
        Product product = new Product();
        product.setTitle("Product10");
        product.setPrice(-70.99f);
        product.setQuantity(50);
        product.setCategory(ProductCategory.CLOTHING);
        product.setStock(savedStock);

        Product addedProduct = productService.addProduct(product, savedStock.getIdStock());

        assertNotNull(addedProduct, "Adding a product with a negative price should not return null");
        assertEquals("Product10", addedProduct.getTitle(), "Product title is incorrect after addition");
        assertEquals(-70.99f, addedProduct.getPrice(), 0.01, "Product price is incorrect after addition");
        assertEquals(50, addedProduct.getQuantity(), "Product quantity is incorrect after addition");
        assertEquals(ProductCategory.CLOTHING, addedProduct.getCategory(), "Product category is incorrect after addition");
        assertEquals(savedStock.getIdStock(), addedProduct.getStock().getIdStock(), "Product should be associated with the correct stock");

    }

    @Test
    @Transactional
    void testAddProductMock() {
        Product product = new Product();
        product.setIdProduct(1L);
        product.setTitle("Nouveau Product");
        product.setPrice(10.0f);
        product.setQuantity(100);
        product.setCategory(ProductCategory.ELECTRONICS);
        Stock stock = new Stock();
        stock.setIdStock(1L);
        when(mockstockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(mockproductRepository.save(any(Product.class))).thenReturn(product);
        Product addedProduct = mockproductServicee.addProduct(product, 1L);
        assertNotNull(addedProduct);
        assertEquals(1L, addedProduct.getIdProduct().longValue());
        assertEquals("Nouveau Product", addedProduct.getTitle());
        assertEquals(10.0f, addedProduct.getPrice(), 0.001);
        assertEquals(100, addedProduct.getQuantity());
        assertEquals(ProductCategory.ELECTRONICS, addedProduct.getCategory());

    }

    @Test
    @Transactional
    void testAddProductMock2() {
        Product product = new Product();
        product.setTitle("Product");
        product.setPrice(10.0f);
        product.setQuantity(50);
        product.setCategory(ProductCategory.ELECTRONICS);
        Stock stock = new Stock();
        stock.setIdStock(1L);
        when(mockstockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(mockproductRepository.save(product)).thenReturn(product);
        Product addedProduct = mockproductServicee.addProduct(product, 1L);
        assertNotNull(addedProduct); // Ensure the added product is not null

        assertEquals("Product", addedProduct.getTitle()); // Check the title
        assertEquals(10.0f, addedProduct.getPrice(), 0.01); // Check the price with delta
        assertEquals(50, addedProduct.getQuantity()); // Check the quantity
        assertEquals(ProductCategory.ELECTRONICS, addedProduct.getCategory()); // Check the category

        assertSame(stock, addedProduct.getStock()); // Check if the stock in the product is the same as the one we created
        verify(mockstockRepository, times(1)).findById(1L);
        verify(mockproductRepository, times(1)).save(product);
        System.out.println(product);


    }




    @Test
    void retrieveProduct() {
        Product product = new Product();
        product.setTitle("Test");
        product.setPrice(19.99f);
        product.setQuantity(10);
        product.setCategory(ProductCategory.ELECTRONICS);
        Stock stock = new Stock();
        stock.setTitle("Stock1");
        stockService.addStock(stock);
        Long stockId = stock.getIdStock();
        product = productService.addProduct(product, stockId);
        Product retrievedProduct = productService.retrieveProduct(product.getIdProduct());
        assertNotNull(retrievedProduct, "Retrieved product should not be null");
        assertEquals(product.getIdProduct(), retrievedProduct.getIdProduct(), "Retrieved product ID should match the expected ID");
        assertEquals(ProductCategory.ELECTRONICS, retrievedProduct.getCategory(), "Retrieved product category should be ELECTRONICS");

    }
    @Test
    void retrieveProduct2() {
        Product product = new Product();
        product.setTitle("Test2");
        product.setPrice(29.99f);
        product.setQuantity(5);
        product.setCategory(ProductCategory.CLOTHING);
        Stock stock = new Stock();
        stock.setTitle("Stock2");
        stockService.addStock(stock);
        Long stockId = stock.getIdStock();
        product = productService.addProduct(product, stockId);
        Product retrievedProduct = productService.retrieveProduct(product.getIdProduct());

        assertNotNull(retrievedProduct, "Retrieved product should not be null");
        Product finalProduct = product;
        assertAll("Retrieved Product",
                () -> assertEquals(finalProduct.getIdProduct(), retrievedProduct.getIdProduct(), "ID should match"),
                () -> assertNotSame(finalProduct, retrievedProduct, "References should not be the same"),
                () -> assertSame(ProductCategory.CLOTHING, retrievedProduct.getCategory(), "Category should be CLOTHING"),
                () -> assertNotEquals(0, retrievedProduct.getPrice(), "Price should not be zero"),
                () -> assertTrue(retrievedProduct.getQuantity() > 0, "Quantity should be greater than zero"),
                () -> assertFalse(retrievedProduct.getTitle().isEmpty(), "Title should not be empty")
        );

    }
    @Test
     void testRetrieveProduct() {
        Product expectedProduct = new Product();
        expectedProduct.setIdProduct(1L);
        when(mockproductRepository.findById(1L)).thenReturn(Optional.of(expectedProduct));
        Product retrievedProduct = mockproductServicee.retrieveProduct(1L);
        verify(mockproductRepository).findById(1L);
        assertEquals(expectedProduct, retrievedProduct);
    }

    @Test
    void retrieveAllProductMock1() {
        Product product1 = new Product();
        product1.setIdProduct(1L);

        Product product2 = new Product();
        product2.setIdProduct(2L);
        when(mockproductRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = mockproductServicee.retreiveAllProduct();
        verify(mockproductRepository).findAll();
        assertEquals(2, products.size());
        assertThat(products).hasSize(2);
    }

    @Test
    void retrieveAllProductMock2() {
        when(mockproductRepository.findAll()).thenReturn(Collections.emptyList());
        List<Product> products = mockproductServicee.retreiveAllProduct();

        verify(mockproductRepository).findAll();
        assertTrue(products.isEmpty());
        assertThat(products).isEmpty();
    }

    @Test
    void retrieveAllProductAutowired() {
        List<Product> products = productService.retreiveAllProduct();

        assertNotNull(products);
    }

    @Test
    void retrieveProductByCategory() {
        Product product1 = new Product();
        product1.setIdProduct(1L);
        product1.setCategory(ProductCategory.ELECTRONICS);

        Product product2 = new Product();
        product2.setIdProduct(2L);
        product2.setCategory(ProductCategory.CLOTHING);
        when(mockproductRepository.findByCategory(ProductCategory.ELECTRONICS)).thenReturn(List.of(product1));
        List<Product> products = mockproductServicee.retrieveProductByCategory(ProductCategory.ELECTRONICS);
       verify(mockproductRepository).findByCategory(ProductCategory.ELECTRONICS);
        assertEquals(1, products.size());
        assertThat(products).hasSize(1);
    }


    @Test
    void retrieveProductByCategory2() {

        List<Product> booksProducts = productService.retrieveProductByCategory(ProductCategory.ELECTRONICS);

        assertThat(booksProducts)
                .isNotEmpty()
                .allMatch(product -> product.getCategory() == ProductCategory.ELECTRONICS);
    }

    @Test
    void deleteProduct() {

        Stock stock = new Stock();
        stock.setTitle("Stock");
        stock.setIdStock(1L);
        mockstockService.addStock(stock);
        Product product = new Product();
        product.setIdProduct(1L);
        product.setStock(stock);
        when(mockstockRepository.findById(1L)).thenReturn(Optional.of(stock));
        mockproductServicee.addProduct(product, stock.getIdStock());
        mockproductServicee.deleteProduct(product.getIdProduct());
        Optional<Product> deletedProduct = mockproductRepository.findById(product.getIdProduct());
        assertFalse(deletedProduct.isPresent());

    }
    @Test
    void deleteProductWithAutowired1() {
        Product product = new Product("Product 1", 100.0f, 10, ProductCategory.ELECTRONICS);
        productRepository.save(product);
         productService.deleteProduct(product.getIdProduct());

         assertFalse(productRepository.existsById(product.getIdProduct()));
    }
    @Test
    void deleteExistingProductWithAutowired() {
        Product product = new Product(100L,"Product 1", 100.0f, 10, ProductCategory.ELECTRONICS);
        productService.addProduct(product,1L);
        productService.deleteProduct(product.getIdProduct());
        assertFalse(productRepository.existsById(product.getIdProduct()));
    }

    @Test
    void retreiveProductStock() {
        Stock stock =new Stock();
        stock.setIdStock(1L);
        stock.setTitle("jihed");
        stockService.addStock(stock);
        Stock existingStock = stockRepositoryy.findById(stock.getIdStock()).orElse(null);

        assertNotNull(existingStock);

        List<Product> products = productRepository.findByStockIdStock(stock.getIdStock());
        System.out.println(products);

        assertFalse(products.isEmpty());
    }
    @Test
    void retrieveProductsMock() {
        Stock stock = new Stock();
        stock.setIdStock(1L);

        Product product1 = new Product();
        product1.setIdProduct(1L);
        product1.setStock(stock);

        Product product2 = new Product();
        product2.setIdProduct(2L);
        product2.setStock(stock);

        when(mockproductRepository.findByStockIdStock(stock.getIdStock())).thenReturn(Arrays.asList(product1, product2));
        List<Product> products = mockproductRepository.findByStockIdStock(stock.getIdStock());
        verify(mockproductRepository).findByStockIdStock(stock.getIdStock());

        assertEquals(2, products.size());
        System.out.println(products);
    }

}