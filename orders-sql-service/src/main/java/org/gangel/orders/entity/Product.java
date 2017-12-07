package org.gangel.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper=false, of="id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id; 
    
    @Column(nullable=false)
    private String title;
    
    @Column(nullable=false, length = 8000)    
    private String description;
    
    @Column(nullable=false, columnDefinition="numeric(10,2)")
    private double price;

}
