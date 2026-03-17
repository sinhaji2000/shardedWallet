package org.quoraapp.wallet.entities;

import java.lang.annotation.ElementType;

import org.apache.calcite.model.JsonType;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import groovy.transform.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "saga_instance")
public class SagaInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private SagaStatus status = SagaStatus.STARTED;

    // @Column(name = "payload" , nullable = false)
    // private String payload;

    @Type(JsonType.class)
    @Column(name = "context" , columnDefinition = "json")
    private String context ;

    @Column(name = "current_step" , nullable = false)
    private String currentStep ;


}
