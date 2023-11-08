package tn.esprit.devops_project.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idProduct;
    String title;
    float price;
    int quantity;
    @Enumerated(EnumType.STRING)
    ProductCategory category;
    @ManyToOne
    @JsonIgnore
    Stock stock;

    public Product(String s, double v, int i, ProductCategory productCategory) {
        // This constructor is intentionally left empty, as there is no specific logic to perform.
        // If specific initialization is needed, it should be added in the future.
        // Alternatively, you can throw an UnsupportedOperationException if this constructor should not be used.
        // throw new UnsupportedOperationException("This constructor is not supported.");
    }

    public Product(long l, String s, double v, int i, ProductCategory productCategory) {
        // This constructor is intentionally left empty, as there is no specific logic to perform.
        // If specific initialization is needed, it should be added in the future.
        // Alternatively, you can throw an UnsupportedOperationException if this constructor should not be used.
        // throw new UnsupportedOperationException("This constructor is not supported.");
    }
}
