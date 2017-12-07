package org.gangel.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gangel.common.services.AbstractEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper=false, of="id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(uniqueConstraints = 
    @UniqueConstraint(columnNames = {"order_id", "lineNumber"}, name = "UNIQUE_ORDER_NUMBER")
)
public class OrderItem extends AbstractEntity<Long> implements Comparable<OrderItem>{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id; 
    
    @Column(nullable=false)
    private Integer lineNumber; 
    
    @ManyToOne(optional=false, cascade=CascadeType.ALL)
    private Product product;
    
    @ManyToOne(optional=false, cascade=CascadeType.ALL)
    private Orders order;
    
    @Column(nullable=false, columnDefinition="int default 1")
    private int quantity = 1;
    
    @Column(nullable=false, columnDefinition="numeric(10,2)")
    private double amount;

    @Override
    public int compareTo(OrderItem o) {
        if (o == null) {
            return -1;
        }
        int result = id != null ? id.compareTo(o.id) : 1;
        if (result == 0) {
            result = lineNumber != null ? lineNumber.compareTo(o.lineNumber) : 1;
        }
        return result;
    }
        
}
