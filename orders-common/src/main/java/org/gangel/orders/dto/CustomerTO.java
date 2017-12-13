package org.gangel.orders.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@JsonRootName("customer")
public class CustomerTO implements Serializable {

    private static final long serialVersionUID = 2014593847764186917L;

    private Long id; 
    
    private String name;
    
    private String lastname;
    
    private String phone;
    
    private String email;    
}
