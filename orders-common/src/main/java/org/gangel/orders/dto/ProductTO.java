package org.gangel.orders.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@JsonRootName("product")
public class ProductTO implements Serializable {

    private static final long serialVersionUID = -913504105574560030L;

    private Long id; 
    
    private String title;
    
    private String description;
    
    private double price;
    
}
