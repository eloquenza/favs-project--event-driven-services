package edu.hsh.favs.project.escqrs.domains.products;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Objects;

@Table(value = "products")
public class Product implements Serializable {

    @Id
    private Long id;

    @Column(value = "name")
    private String name;

    // TODO: type migrate back to Money after figuring how to persist JavaMoney types in the DB
    @Column(value = "cost")
    private Long cost;

    public Product() {

    }

    public Product(Long id, String name, Long cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public Product setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Long getCost() {
        return cost;
    }

    public Product setCost(Long cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getId().equals(product.getId()) && getName().equals(product.getName()) && getCost().equals(product.getCost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCost());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                '}';
    }
}
