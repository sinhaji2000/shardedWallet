package org.quoraapp.wallet.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SagaStep {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "saga_instance_id" , nullable = false)
    private Long sagaInstanceId ;

    @Column(name = "step_name" , nullable = false)
    private String stepName ;

    @Column(name = "status" , nullable = false)
    private StepStatus status;

    @Column(name = "step_data" , columnDefinition = "json")
    private String stepData ;
    

}
