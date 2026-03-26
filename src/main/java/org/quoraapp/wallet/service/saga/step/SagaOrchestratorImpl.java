package org.quoraapp.wallet.service.saga.step;


import org.quoraapp.wallet.entities.SagaInstance;
import org.quoraapp.wallet.entities.SagaStatus;
import org.quoraapp.wallet.entities.SagaStep;
import org.quoraapp.wallet.entities.StepStatus;
import org.quoraapp.wallet.repositories.SagaInstanceRepository;
import org.quoraapp.wallet.repositories.SagaStepRepository;
import org.quoraapp.wallet.service.saga.SagaContext;
import org.quoraapp.wallet.service.saga.SagaStepInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator {
    
    private final ObjectMapper objectMapper ;
    private final SagaInstanceRepository sagaInstanceRepository ;
    private final SagaStepRepository sagaStepRepository ;
    private final SagaStepFactory sagaStepFactory ;


    @Override
    @Transactional
    public Long startSaga(SagaContext context) {
        // TODO Auto-generated method stub

        try{

            String contextJson = objectMapper.writeValueAsString(context);  // Convert the SagaContext to JSON string
            
            SagaInstance sagaInstance = SagaInstance.builder()
                .status(SagaStatus.STARTED)
                .context(contextJson)
                
                .build();
                sagaInstance = sagaInstanceRepository.save(sagaInstance);  // Save the new SagaInstance to the database
                log.info("Started new Saga with ID: {}", sagaInstance.getId());
                return sagaInstance.getId();  // Return the ID of the newly created SagaInstance
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        
    }

    @Override
    @Transactional
    public boolean executeStep(Long sagaInstanceId, String stepName) {
        // TODO Auto-generated method stub

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));
        SagaStepInterface step = sagaStepFactory.getSagaStep(stepName) ;

        if(step == null){
            throw new RuntimeException("SagaStep not found with name: " + stepName);
        }

        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.PENDING) 
            .stream()
            .filter(s -> s.getStepName().equals(stepName))
            .findFirst()
            .orElse(SagaStep.builder()
                .sagaInstanceId(sagaInstanceId)
                .stepName(stepName)
                .status(StepStatus.PENDING)
                .build()
            );

            if(sagaStepDB.getId() == null){
                sagaStepDB = sagaStepRepository.save(sagaStepDB);
            }

            try{
                SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class) ;
                sagaStepDB.setStatus(StepStatus.RUNNING);  // Store the current SagaContext in the step data
                sagaStepRepository.save(sagaStepDB) ;  // update the status to running in db ; 
                boolean succes = step.execute(sagaContext);


                if(succes){
                    sagaStepDB.setStatus(StepStatus.COMPLETED);
                    
                    sagaStepRepository.save(sagaStepDB) ;  // update the status to completed in db ; 

                    sagaInstance.setCurrentStep(stepName); // update the current step in the SagaInstance , step we just completed 
                    sagaInstance.setStatus(SagaStatus.RUNNING);
                    sagaInstanceRepository.save(sagaInstance) ; // update the SagaInstance in the database with the new current step and status


                    log.info("Executed step '{}' for SagaInstance ID: {}", stepName, sagaInstanceId);
                    return true ; 
                }else{
                    sagaStepDB.setStatus(StepStatus.FAILED);
                    sagaStepRepository.save(sagaStepDB) ;  // update the status to failed in db ; 
                    log.error("Failed to execute step '{}' for SagaInstance ID: {}", stepName, sagaInstanceId);
                    return false ;
                }
            }catch(Exception e){
                sagaStepDB.setStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB) ;  // update the status to failed in db ; 
                log.error(e.getMessage()); 
                return false ;
            }
       // return false ;

    }

    @Override
    @Transactional
    public boolean compensateStep(Long sagaInstanceId, String stepName) {
        
        //1. find the SagaInstance  from the database
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));

        //2. find the SagaStep form the database with the given stepName and SagaInstanceId from the database
        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.COMPLETED)
            .stream()
            .filter(s -> s.getStepName().equals(stepName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Completed SagaStep not found with name: " + stepName + " for SagaInstance ID: " + sagaInstanceId));


        //3. take the context from the SagaInstance and convert it to SagaContext object

        //4. call the compensate method of the SagaStep with the SagaContext
        return false;
    }

    @Override
    public SagaInstance getSagaInstance(Long sagaInstanceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void compensateSaga(Long sagaInstanceId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void completeSaga(Long sagaInstanceId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void failSaga(Long sagaInstanceId) {
        // TODO Auto-generated method stub
    }
    
}
