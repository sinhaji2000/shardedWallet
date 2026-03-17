package org.quoraapp.wallet.entities;


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
public class SagaStep {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "saga_instance_id" , nullable = false)
    private Long sagaInstanceId ;

    @Column(name = "step_name" , nullable = false)
    private String stepName ;

    @Column(name = "status" , nullable = false)
    private SagaStatus status ;

    @Column(name = "step_data" , columnDefinition = "json")
    private String stepData ;
    

}
