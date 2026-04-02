package org.quoraapp.wallet.service.saga.step;


import java.util.List;

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

        // SagaStep sagaStepDB =
        // sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId,
        // StepStatus.PENDING)
        // .stream()
        // .filter(s -> s.getStepName().equals(stepName))
        // .findFirst()
        // .orElse(SagaStep.builder()
        // .sagaInstanceId(sagaInstanceId)
        // .stepName(stepName)
        // .status(StepStatus.PENDING)
        // .build()
        // );

        SagaStep sagaStepDB = sagaStepRepository
                .findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.PENDING)
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
                sagaStepDB.markAsRunning(); // Store the current SagaContext in the step data
                sagaStepRepository.save(sagaStepDB) ;  // update the status to running in db ; 
                boolean succes = step.execute(sagaContext);


                if(succes){
                    sagaStepDB.markAsCompleted();
                    
                    sagaStepRepository.save(sagaStepDB) ;  // update the status to completed in db ; 

                    sagaInstance.setCurrentStep(stepName); // update the current step in the SagaInstance , step we just completed 
                    sagaInstance.markAsRunning(); // update the status of the SagaInstance to running if it was in
                                                  // started state
                    sagaInstanceRepository.save(sagaInstance) ; // update the SagaInstance in the database with the new current step and status


                    log.info("Executed step '{}' for SagaInstance ID: {}", stepName, sagaInstanceId);
                    return true ; 
                }else{
                    sagaStepDB.markAsFailed();
                    sagaStepRepository.save(sagaStepDB) ;  // update the status to failed in db ; 
                    log.error("Failed to execute step '{}' for SagaInstance ID: {}", stepName, sagaInstanceId);
                    return false ;
                }
            }catch(Exception e){
                sagaStepDB.markAsFailed();
                sagaStepRepository.save(sagaStepDB) ;  // update the status to failed in db ; 
                log.error(e.getMessage()); 
                return false ;
            }
       // return false ;

    }


    @Override
    @Transactional
    public boolean compensateStep(Long sagaInstanceId, String stepName) {
        
        // 3. take the context from the SagaInstance and convert it to SagaContext
        // object

        // 4. call the compensate method of the SagaStep with the SagaContext

        // 1. find the SagaInstance from the database
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));

        // 2. find the SagaStep form the database with the given stepName and
        // SagaInstanceId from the database
        SagaStepInterface step = sagaStepFactory.getSagaStep(stepName);

        if (step == null) {
            throw new RuntimeException("SagaStep not found with name: " + stepName);
        }

        SagaStep sagaStepDB = sagaStepRepository
                .findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.COMPLETED)
                .orElse(null // no step found in db with status completed , so we cannot compensate it
                );

        if (sagaStepDB.getId() == null) {
            log.info("No completed step found for compensation with name '{}' for SagaInstance ID: {}", stepName,
                    sagaInstanceId);
            return true; // if the step is not found in db with status completed , it means it was never
                         // executed successfully , so we can consider it as compensated
        }

        try {
            SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
            sagaStepDB.markAsCompensating(); // Store the current SagaContext in the step data
            sagaStepRepository.save(sagaStepDB); // update the status to running in db ;
            boolean succes = step.compensate(sagaContext);

            if (succes) {
                sagaStepDB.markAsCompensated();

                sagaStepRepository.save(sagaStepDB); // update the status to completed in db ;

                log.info(stepName);
                return true;

            } else {
                sagaStepDB.markAsFailed();
                sagaStepRepository.save(sagaStepDB); // update the status to failed in db ;
                log.error("Failed to execute step '{}' for SagaInstance ID: {}", stepName, sagaInstanceId);
                return false;
            }
        } catch (Exception e) {
            sagaStepDB.markAsFailed();
            sagaStepRepository.save(sagaStepDB); // update the status to failed in db ;
            log.error(e.getMessage());
            return false;
        }

     }

    @Override
    @Transactional
    public SagaInstance getSagaInstance(Long sagaInstanceId) {

        return sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));
    }

    @Override
    @Transactional
    public void compensateSaga(Long sagaInstanceId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));

        sagaInstance.markAsCompensating(); // update the status of the SagaInstance to compensating
        sagaInstanceRepository.save(sagaInstance); // update the SagaInstance in the database with the new status

        List<SagaStep> completedSteps = sagaStepRepository.findCompletedSagaStepsBySagaInstanceId(sagaInstanceId);

        boolean allCompensated = true;
        for (SagaStep competedStep : completedSteps) {
            boolean succes = this.compensateStep(sagaInstanceId, competedStep.getStepName());
            if (!succes) {
                allCompensated = false;
                log.error("Failed to compensate step '{}' for SagaInstance ID: {}", competedStep.getStepName(),
                        sagaInstanceId);
            }
        }

        if (allCompensated) {
            sagaInstance.markAsCompensated(); // update the status of the SagaInstance to compensated
            sagaInstanceRepository.save(sagaInstance); // update the SagaInstance in the database with the new status
            log.info("Successfully compensated all steps for SagaInstance ID: {}", sagaInstanceId);
        } else {
            log.error("Failed to compensate all steps for SagaInstance ID: {}", sagaInstanceId);
        }
        
    }

    @Override
    @Transactional
    public void completeSaga(Long sagaInstanceId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));

        sagaInstance.markAsCompleted();
        sagaInstanceRepository.save(sagaInstance); // update the SagaInstance in the database with the new status
        
    }

    @Override
    @Transactional
    public void failSaga(Long sagaInstanceId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(() -> new RuntimeException("SagaInstance not found with ID: " + sagaInstanceId));

        sagaInstance.markAsFailed();
        compensateSaga(sagaInstanceId);
        sagaInstanceRepository.save(sagaInstance); // update the SagaInstance in the database with the new status
    }
    
}
