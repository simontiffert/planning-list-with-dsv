# planning-list-with-dsv

A reproducer for issues with automatic delay until last in combination with dependencies DSV

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

## Reproduce

Issue with Enterprise version (parallel solving?)

```shell script
mvn quarkus:dev -Penterprise
```


Get the JSON for a demo
GET http://localhost:8080/api/demoData

Sent the JSON to start solving
POST http://localhost:8080/api/solve

## Issue

### 1. Normal/enterprise version fails with alignmentKey in SubTask.java#101

```
 java.lang.IllegalArgumentException: There are fixed dependency loops in the graph for variables
```

### 2. Enterprise version without ASSERT in solverConfig

```
 java.lang.ArrayIndexOutOfBoundsException: Index -1 out of bounds for length 45
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph.shiftFreeSlotAtIndexForwardBy(MNRDirectedGraph.java:576)
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph.updateRepresentativeAndOrderingAfterSplit(MNRDirectedGraph.java:535)
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph.updateRepresentativeAndOrderingAfterSplit(MNRDirectedGraph.java:388)
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph.splitStronglyConnectedComponents(MNRDirectedGraph.java:379)                                                                                                      
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph$BatchedEdgeChanges.commit(MNRDirectedGraph.java:802)                                                                                                             
        at ai.timefold.solver.enterprise.core.domain.variable.declarative.MNRDirectedGraph.commitChanges(MNRDirectedGraph.java:367)                                                                                                                         
        at ai.timefold.solver.core.impl.domain.variable.declarative.DefaultVariableReferenceGraph.updateChanged(DefaultVariableReferenceGraph.java:57)                                                                                                      
        at ai.timefold.solver.core.impl.domain.variable.declarative.DefaultShadowVariableSession.updateVariables(DefaultShadowVariableSession.java:38)                                                                                                      
        at ai.timefold.solver.core.impl.domain.variable.listener.support.VariableListenerSupport.triggerVariableListenersInNotificationQueues(VariableListenerSupport.java:361)                                                                             
        at ai.timefold.solver.core.impl.score.director.AbstractScoreDirector.triggerVariableListeners(AbstractScoreDirector.java:377)                                                                                                                       
        at ai.timefold.solver.core.impl.move.director.VariableChangeRecordingScoreDirector.undoChanges(VariableChangeRecordingScoreDirector.java:81)                                                                                                        
        at ai.timefold.solver.core.impl.move.director.EphemeralMoveDirector.close(EphemeralMoveDirector.java:52)                                                                                                                                            
        at ai.timefold.solver.core.impl.move.director.MoveDirector.executeTemporary(MoveDirector.java:216)                                                                                                                                                  
        at ai.timefold.solver.core.impl.score.director.AbstractScoreDirector.executeTemporaryMove(AbstractScoreDirector.java:326)                                                                                                                           
        at ai.timefold.solver.core.impl.localsearch.decider.LocalSearchDecider.doMove(LocalSearchDecider.java:117)                                                                                                                                          
        at ai.timefold.solver.core.impl.localsearch.decider.LocalSearchDecider.decideNextStep(LocalSearchDecider.java:96)                                                                                                                                   
        at ai.timefold.solver.core.impl.localsearch.DefaultLocalSearchPhase.solve(DefaultLocalSearchPhase.java:82)                                                                                                                                          
        at ai.timefold.solver.core.impl.solver.AbstractSolver.runPhases(AbstractSolver.java:82)                                                                                                                                                             
        at ai.timefold.solver.core.impl.solver.DefaultSolver.solve(DefaultSolver.java:195)                                                                                                                                                                  
        at ai.timefold.solver.core.impl.solver.DefaultSolverJob.call(DefaultSolverJob.java:131)                                                                                                                                                             
        at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)                                                                                                                                                                               
        at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)                                                                                                                                                        
        at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)                                                                                                                                                        
        at java.base/java.lang.Thread.run(Thread.java:1583)                                                                                          
```

