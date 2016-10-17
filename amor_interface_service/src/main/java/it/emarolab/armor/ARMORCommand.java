package it.emarolab.armor;

import it.emarolab.amor.owlInterface.OWLEnquirer;
import it.emarolab.amor.owlInterface.OWLReferences;
import armor_msgs.ArmorDirectiveRequest;
import armor_msgs.ArmorDirectiveResponse;
import it.emarolab.amor.owlInterface.OWLReferencesInterface.OWLReferencesContainer;
import org.ros.internal.message.Message;
import org.ros.internal.message.RawMessage;
import org.ros.message.MessageFactory;
import org.ros.message.MessageFactoryProvider;
import org.ros.node.ConnectedNode;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLClass;
import it.emarolab.amor.owlDebugger.Logger;
import armor_msgs.*;
import org.semanticweb.owlapi.util.StringAnnotationVisitor;

import java.util.*;

/**
 * Project: a ROS Multi Ontology Reference - aRMOR <br>
 * File: .../src/aMOR.owlInterface/OWLLibrary.java <br>
 *
 * @author Alessio Capitanelli <br><br>
 * DIBRIS emaroLab,<br>
 * University of Genoa. <br>
 * Feb 10, 2016 <br>
 * License: GPL v2 <br><br>
 *
 * <p>
 * ARMORCommand is an interface to aMOR. It parses the client request and uses
 * aMOR to apply changes or query an ontology reference. It is mainly implemented
 * as a switch case. Cases correspond to every combination of Commands, Primary
 * Directives and Secondary Directives. Documentation of every case input and output
 * can be found on the project page at https://github.com/EmaroLab.
 * </p>
 *
 * @see
 *
 *
 * @version 2.0
 */

class ARMORCommand {

    private final static String COMMAND_DELIMITER = "_"; //regex reserved chars like + must be escaped (\\+)
    private String fullCommand;
    private String referenceName;
    private String clientName;
    private List<String> args;
    private ArmorDirectiveResponse response;
    private Boolean fullIRIName;
    private ConnectedNode connectedNode;

    ARMORCommand(ArmorDirectiveRequest request, ArmorDirectiveResponse response,
                        final Boolean fullIRIName, final ConnectedNode connectedNode){

        String PrimaryCommandSpec = "";
        String SecondaryCommandSpec = "";

        if (request.getReferenceName() == null){
            //TODO: log error, set error response then return
            setResponse(false, 201, "Invalid request. No reference name specified.");
            System.out.println("Invalid request. No reference name specified.");
            throw new AssertionError();
        }

        if (request.getCommand() == null){
            //TODO: log error, set error response then return
            setResponse(false, 202, "Invalid request. No command specified.");
            System.out.println("Invalid request. No command specified.");
            throw new AssertionError();
        }

        if (request.getPrimaryCommandSpec() != null){
            PrimaryCommandSpec = request.getPrimaryCommandSpec();
        }

        if (request.getSecondaryCommandSpec() != null){
            SecondaryCommandSpec = request.getSecondaryCommandSpec();
        }

        this.fullCommand = request.getCommand() + COMMAND_DELIMITER +
                PrimaryCommandSpec + COMMAND_DELIMITER + SecondaryCommandSpec;
        this.referenceName = request.getReferenceName();
        this.clientName = request.getClientName();
        this.args = request.getArgs();
        this.response = response;
        this.fullIRIName = fullIRIName;
        this.connectedNode = connectedNode;

    }

    ArmorDirectiveResponse getServiceResponse() {
        return response;
    }

    ArmorDirectiveResponse executeCommand(){

        OWLReferences ontoRef = (OWLReferences)OWLReferencesContainer.getOWLReferences(referenceName);
        String formattedCommand = fullCommand.toUpperCase().replaceAll(COMMAND_DELIMITER, "_");
        OWLReferenceCommandsEnum commandSwitch;
        try {
            commandSwitch = OWLReferenceCommandsEnum.valueOf(formattedCommand);
        }catch (IllegalArgumentException e){
            commandSwitch = OWLReferenceCommandsEnum.INVALID_COMMAND;
        }

        Object castedObj; // temporary object used for value casting
        String errorMessage;

        switch(commandSwitch) {

            /////////////////  SYSTEM UTILITIES COMMANDS  /////////////////

            case INVALID_COMMAND:
                // Catches invalid commands.
                setResponse(false, 205, fullCommand + " is not a valid command.");
                return  response;

            case CREATE__:
                // Create a new ontology file
                // TODO: ver1
                return response;

            case DELETE__:
                // Delete an ontology file
                // TODO: ver1
                return response;

            case MOUNT__:
                // Mount a client on an existing OWLReference to allow manipulation commands
                // TODO: catch errors
                if (ARMORResourceManager.tryMountClient(clientName, referenceName)) {
                    setResponse(true, 0, "");
                } else {
                    errorMessage = clientName + " cannot be mounted on " + referenceName
                            + ". Another client is already mounted on it.";
                    setResponse(false, 203, errorMessage);
                    connectedNode.getLog().error(errorMessage);
                }
                return response;

            case UNMOUNT__:
                // Unmount a client from an OWLReference
                ARMORResourceManager.unmountClientFromOntology(clientName, referenceName);
                setResponse(true, 0, "");
                return response;

            case SAVE__:
                // Save an OWLReference to file
                // args[ String referenceName ]
                if (!args.get(0).equals("")) {
                    ontoRef.saveOntology(false, args.get(0));
                } else {
                    ontoRef.saveOntology(false);
                }
                // TODO: catch error
                setResponse(true, 0, "");
                return response;

            case SAVE_INFERENCE_:
                // Save an OWLReference to file including inferences
                // args[ String filePath ]
                if (!args.get(0).equals("")) {
                    ontoRef.saveOntology(true, args.get(0));
                } else {
                    ontoRef.saveOntology(true);
                }
                // TODO: catch error
                setResponse(true, 0, "");
                return response;

            case LOAD_FILE_:
                // Load an OWLReference from and ontology file
                // args[ String filePath, String iriPath, Boolean bufferedManipulation,
                //       String reasoner, Boolean bufferedReasoner ]
                ontoRef = ARMORResourceManager.loadOntologyFromFile(referenceName, args);
                ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(args.get(2)));
                // TODO: catch error
                setResponse(true, 0, "");
                return response;

            case LOAD_FILE_MOUNTED:
                // Load an OWLReference from and ontology file and mount the client on it
                // args[ String filePath, String iriPath, String bufferedManipulation,
                //       String reasoner, Boolean bufferedReasoner ]
                ontoRef = ARMORResourceManager.loadOntologyFromFile(referenceName, args);
                ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(args.get(2)));
                // TODO: catch error
                if (ARMORResourceManager.tryMountClient(clientName, referenceName)) {
                    setResponse(true, 0, "");
                }else{
                    errorMessage = "Reference loaded from file but " + clientName + " cannot be mounted on reference "
                            + referenceName + ". Another client is already mounted on it.";
                    setResponse(false, 203, errorMessage);
                    connectedNode.getLog().error(errorMessage);
                }
                return response;

            case LOAD_WEB_:
                // Load an OWLReference from the web
                // args[ String filePath, String iriPath, String bufferedManipulation,
                //       String reasoner, Boolean bufferedFlag ]
                ontoRef = ARMORResourceManager.loadOntologyFromWeb(referenceName, args);
                ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(args.get(2)));
                // TODO: catch error
                setResponse(true, 0, "");
                return response;

            case LOAD_WEB_MOUNTED:
                // Load an OWLReference from the web and mount the client on it
                // args[ String filePath, String iriPath, String bufferedManipulation,
                //       String reasoner, Boolean bufferedFlag ]
                ontoRef = ARMORResourceManager.loadOntologyFromWeb(referenceName, args);
                ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(args.get(2)));
                // TODO: catch error
                if (ARMORResourceManager.tryMountClient(clientName, referenceName)){
                    setResponse(true, 0, "");
                }else {
                    errorMessage = "Reference loaded from web but " +  clientName + " cannot mounted on reference "
                            + referenceName + ". Another client is already mounted on it.";
                    setResponse(false, 203, errorMessage);
                    connectedNode.getLog().error(errorMessage);
                }
                return response;

            case DROP__:
                // Drop an OWLReference from ARMOR
                ARMORResourceManager.removeOntology(referenceName);
                setResponse(true, 0, "");
                return response;

            case LOG_FILE_ON:
                // Start logging to file or change the logging file
                // args [ String logFilePath ]
                Logger.setPrintOnFile(args.get(0));
                setResponse(true, 0, "");
                return response;

            case LOG_FILE_OFF:
                // Stop logging to file
                Logger.setPrintOnFile(Logger.NOFILEPRINTING_KeyWord);
                setResponse(true, 0, "");
                return response;

            case LOG_SCREEN_ON:
                // Start logging to screen
                Logger.setPrintOnConsole(true);
                setResponse(true, 0, "");
                return response;

            case LOG_SCREEN_OFF:
                // Stop logging on screen
                Logger.setPrintOnConsole(false);
                setResponse(true, 0, "");
                return response;

            /////////////////       QUERY COMMANDS       /////////////////

            case QUERY_IND_:
                //Check an individual exists
                //args[ String ind_name ]
                Set<OWLClass> candidates = ontoRef.getIndividualClasses(args.get(0));
                List<String> candidatesList = getStringListFromQuery(candidates, ontoRef);
                if (candidates.size() > 0) {
                    setResponse(true, 0, "", candidatesList);
                }else{
                    setResponse(false, 0, "", candidatesList);
                }
                return response;

            case QUERY_IND_CLASS:
                // Queries all individuals belonging to a class.
                // args[ String className ]
                Set<OWLNamedIndividual> individuals = ontoRef.getIndividualB2Class(args.get(0));
                List<String> individualsList = getStringListFromQuery(individuals, ontoRef);
                setResponse(true, 0, "", individualsList);
                return response;

            case QUERY_DATAPROP_IND:
                // Queries all data property values belonging to an individual
                // args[ String propertyName, String indName ]
                Set<OWLLiteral> values = ontoRef.getDataPropertyB2Individual(args.get(1), args.get(0));
                List<String> valueList = getStringListFromQuery(values, ontoRef);
                setResponse(true, 0, "", valueList);
                return response;

            case QUERY_DATAPROP_CLASS:
                // TODO: ver1
                return response;

            case QUERY_OBJECTPROP_IND:
                // Queries all object property arguments belonging to an individual
                // args[ String propertyName, String indName ]
                Set<OWLNamedIndividual> indValues = ontoRef.getObjectPropertyB2Individual(args.get(1), args.get(0));
                List<String> valueObjectList = getStringListFromQuery(indValues, ontoRef);
                setResponse(true, 0, "", valueObjectList);
                return response;

            case QUERY_OBJECTPROP_CLASS:
                // TODO: ver1
                return response;

            case QUERY_CLASS_IND:
                // Queries all classes an individual belongs to
                // args[ String indName ]
                Set<OWLClass> classes = ontoRef.getIndividualClasses(args.get(0));
                List<String> classList = getStringListFromQuery(classes, ontoRef);
                setResponse(true, 0, "", classList);
                return response;

            case QUERY_CLASS_CLASS:
                // Queries al subclasses of a class
                // args[ String superclassName ]
                Set<OWLClass> subClasses = ontoRef.getSubClassOf(args.get(0));
                List<String> subclassesList = getStringListFromQuery(subClasses, ontoRef);
                setResponse(true, 0, "", subclassesList);
                return response;

            case QUERY_DATAPROP_DATAPROP:
                // TODO: ver1
                return response;

            case QUERY_OBJECTPROP_OBJECTPROP:
                // TODO: ver1
                return response;

            case QUERY_IND_DATAPROP:
                // Queries all data properties belonging to an individual
                // args[ String indName ]
                Set<OWLEnquirer.DataPropertyRelatios> dataProps = ontoRef.getDataPropertyB2Individual(args.get(0));
                Set<OWLDataProperty> dataObjects = new HashSet<>();
                for (OWLEnquirer.DataPropertyRelatios prop : dataProps){
                    dataObjects.add(prop.getProperty());}
                List<String> allDataList = getStringListFromQuery(dataObjects, ontoRef);
                setResponse(true, 0, "", allDataList);
                return response;

            case QUERY_IND_OBJECTPROP:
                // Queries all object properties belonging to an individual
                // args[ String indName ]
                Set<OWLEnquirer.ObjectPropertyRelatios> objectProps
                        = ontoRef.getObjectPropertyB2Individual(args.get(0));
                Set<OWLObjectProperty> propObjects = new HashSet<>();
                for (OWLEnquirer.ObjectPropertyRelatios prop : objectProps){
                    propObjects.add(prop.getProperty());}
                List<String> allObjectlist = getStringListFromQuery(propObjects, ontoRef);
                setResponse(true, 0, "", allObjectlist);
                return response;

            case QUERY_SPARQL_:
                // Executes a SPARQL query and returns the result as a string. Optionally, timeout can be set.
                // args[ String query, string timeout ]
                // OR args[ String prefix, string select, string where string timeout ]
                List<Map<String, String>> result = new ArrayList<>();

                if      (args.size() == 1){result = ontoRef.sparql2Msg(args.get(0), null);}
                else if (args.size() == 2){result = ontoRef.sparql2Msg(args.get(0), Long.valueOf(args.get(1)));}
                else if (args.size() == 3){result = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), null);}
                else if (args.size() == 4){result = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), Long.valueOf(args.get(3)));}

                List<String> sparqlResponse = new ArrayList<>();
                sparqlResponse.add(result.toString().substring(1, result.size() - 2));
                setResponse(true, 0, "", sparqlResponse);
                return response;

            case QUERY_SPARQL_FORMATTED:
                // Executes a SPARQL query and returns the result as a string. Optionally, timeout can be set.
                // args[ String query, string timeout ]
                // OR args[ String prefix, string select, string where string timeout ]
                List<Map<String, String>> result_f = new ArrayList<>();

                if      (args.size() == 1){result_f = ontoRef.sparql2Msg(args.get(0), null);}
                else if (args.size() == 2){result_f = ontoRef.sparql2Msg(args.get(0), Long.valueOf(args.get(1)));}
                else if (args.size() == 3){result_f = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), null);}
                else if (args.size() == 4){result_f = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), Long.valueOf(args.get(3)));}

                List<QueryItem> items = response.getSparqlQueriedObjects();

                result_f.forEach(
                        item->item.forEach((k, v)->{
                            QueryItem msgItem = connectedNode.getTopicMessageFactory().newFromType(QueryItem._TYPE);
                            msgItem.setKey(k);
                            msgItem.setValue(v);
                            items.add(msgItem);
                        }));

                setResponseSparql(true, 0, "", items);
                return response;
        }

        if (!ARMORResourceManager.isAvailable(clientName, referenceName)){
            errorMessage = clientName + " cannot connect to " + referenceName
                    + ". Another client is already mounted on it.";
            setResponse(false, 102, errorMessage);
            connectedNode.getLog().error(errorMessage);
            return response;
        }

        switch (commandSwitch){

            /////////////////    MANIPULATION COMMANDS    /////////////////

            /* WARNING: a client must be mounted on the ontology to get query
                        commands executed. All manipulation requests on an ontology
                        from any service but the mounted one will be ignored.
            */

            /// ADD //////////////////////////////////////////////////////

            case APPLY__:
                ontoRef.applyOWLManipulatorChanges();
                setResponse(true, 0, "");
                return response;

            case REASON__:
                // Run the reasoner
                ontoRef.synchroniseReasoner();
                setResponse(true, 0, "");
                return response;

            case ADD_IND_:
                // Add an individual to an OWLReference
                // args[ String indName ]
                ontoRef.addIndividual(args.get(0));
                setResponse(true, 0, "");
                return response;

            case ADD_CLASS_:
                // Add a class to an OWLReference
                // args[ String className ]
                ontoRef.addClass(args.get(0));
                setResponse(true, 0, "");
                return response;

            case ADD_DATAPROP_:
                // Define a data property in an OWLReference
                // TODO: ver1
                return response;

            case ADD_OBJECTPROP_:
                // Define an object property in an OWLReference
                // TODO: ver1
                return response;

            case ADD_IND_CLASS:
                // Add an individual to a class defined in current reference
                // args[ String indName, String clsName ]
                ontoRef.addIndividualB2Class(args.get(0), args.get(1));
                setResponse(true, 0, "");
                return response;

            case ADD_CLASS_CLASS:
                // Add an class to a class defined in current reference
                // args[ String subclassName, String superclass Name ]
                ontoRef.addSubClassOf(args.get(1), args.get(0));
                setResponse(true, 0, "");
                return response;

            case ADD_DATAPROP_IND:
                // Add a data property to an individual in current reference
                // args[ String propName, String indName, String typename, String value ]
                castedObj = valueTypeCasting(args.get(2), args.get(3));
                if (castedObj != null) {
                    ontoRef.addDataPropertyB2Individual
                            (args.get(1), args.get(0), castedObj);
                    setResponse(true, 0, "");
                }else{
                    // TODO: log
                    errorMessage = "Value type "+ args.get(2) + " not supported. "
                            + args.get(0) + "not added to " + args.get(1) + ".";
                    connectedNode.getLog().error(errorMessage);
                    setResponse(true, 101, errorMessage);
                }
                return response;

            case ADD_OBJECTPROP_IND:
                // Add a data property to an individual in current reference
                // args[ String propName, String indName, String valueIndividual ]
                ontoRef.addObjectPropertyB2Individual(args.get(1), args.get(0), args.get(2));
                setResponse(true, 0, "");
                return response;

            case ADD_DATAPROP_DATAPROP:
                // Add a data sub-property to an existing data property
                // TODO: ver1
                return response;

            case ADD_OBJECTPROP_OBJECTPROP:
                // Add a object sub-property to an existing object property
                // TODO: ver1
                return response;

            case ADD_DISJOINT_IND:
                ontoRef.makeDisjointIndividualName(new HashSet<String>(args));
                setResponse(true, 0, "");
                return response;

            case ADD_DISJOINT_CLASS:
                ontoRef.makeDisjointClassName(new HashSet<String>(args));
                setResponse(true, 0, "");
                return response;

            /// REMOVE ///////////////////////////////////////////////////

            case REMOVE_IND_:
                // Remove individual from current OWLReference
                // args[ String indName ]
                ontoRef.removeIndividual(args.get(0));
                setResponse(true, 0, "");
                return response;

            case REMOVE_CLASS_:
                // Remove class from current OWLReference
                // args[ String clsName ]
                ontoRef.removeClass(args.get(0));
                setResponse(true, 0, "");
                return response;

            case REMOVE_DATAPROP_:
                // TODO: ver1
                return response;

            case REMOVE_OBJECTPROP_:
                // TODO: ver1
                return response;

            case REMOVE_IND_CLASS:
                // Remove an individual from the set of individual belonging to a class
                // args[ String indName, String clsName ]
                ontoRef.removeIndividualB2Class(args.get(0), args.get(1));
                // TODO: add warning no element to remove
                setResponse(true, 0, "");
                return response;

            case REMOVE_CLASS_CLASS:
                // Remove a class from the subclasses set of a super-class
                // args[ String subclassName, String superclassName ]
                ontoRef.removeSubClassOf(args.get(1), args.get(0));
                // TODO: add warning no class to remove
                setResponse(true, 0, "");
                return response;

            case REMOVE_DATAPROP_IND:
                // Remove a datapropery from an individual
                // args[ String propertyName, String indName, String valueType, String value ]
                castedObj = valueTypeCasting(args.get(2), args.get(3));
                if (castedObj != null) {
                    ontoRef.removeDataPropertyB2Individual
                            (args.get(1), args.get(0), castedObj);
                    setResponse(true, 0, "");
                }else{
                    // TODO: log
                    errorMessage = "Value type "+ args.get(2) + " not supported. "
                            + args.get(0) + "not removed from " + args.get(1) + ".";
                    connectedNode.getLog().error(errorMessage);
                    setResponse(true, 101, errorMessage);
                }
                return response;

            case REMOVE_OBJECTPROP_IND:
                // Remove an objectproperty from an individual
                // args[ String propertyName, String indName, String valueIndividual ]
                ontoRef.removeObjectPropertyB2Individual(args.get(1), args.get(0), args.get(2));
                setResponse(true, 0, "");
                return response;

            case REMOVE_DATAPROP_DATAPROP:
                // TODO: ver1
                return response;

            case REMOVE_OBJECTPROP_OBJECTPROP:
                // TODO: ver1
                return response;

            /// REPLACE //////////////////////////////////////////////////

            case REPLACE_DATAPROP_IND:
                // Replace the value of an individual data property
                // args[ String propertyName, String individualName,
                //       String valueType, String newValue, String oldValue ]
                // TODO: rework method replaceDataProperty to get String
                castedObj = valueTypeCasting(args.get(2), args.get(4));
                if (castedObj != null) {
                    ontoRef.replaceDataProperty(ontoRef.getOWLIndividual(args.get(1)),
                            ontoRef.getOWLDataProperty(args.get(0)),
                            ontoRef.getOWLLiteral(castedObj),
                            ontoRef.getOWLLiteral(valueTypeCasting(args.get(2), args.get(3))));
                    setResponse(true, 0, "");
                }else{
                    // TODO: log
                    errorMessage = "Value type "+ args.get(2) + " not supported. "
                            + args.get(0) + "not replaced from " + args.get(1) + ".";
                    connectedNode.getLog().error(errorMessage);
                    setResponse(false, 101, errorMessage);
                }
                return response;

            case REPLACE_OBJECTPROP_IND:
                // Replace the value of an individual data property
                // args[ String propertyName, String individualName,
                //       String newValue, String oldValue ]
                // TODO: rework method replaceObjectProperty to get String
                ontoRef.replaceObjectProperty(ontoRef.getOWLIndividual(args.get(1)),
                        ontoRef.getOWLObjectProperty(args.get(0)),
                        ontoRef.getOWLIndividual(args.get(3)),
                        ontoRef.getOWLIndividual(args.get(2)));
                setResponse(true, 0, "");
                return response;

            /// RENAME ///////////////////////////////////////////////////

            case RENAME_IND_:
                // Rename an individual
                // args[ String oldName, String newName ]
                ontoRef.renameEntity(ontoRef.getOWLIndividual(args.get(0)), args.get(1));
                setResponse(true, 0, "");
                return response;

            case RENAME_CLASS_:
                // Rename a class
                // args[ String oldName, String newName ]
                ontoRef.renameEntity(ontoRef.getOWLClass(args.get(0)), args.get(1));
                setResponse(true, 0, "");
                return response;

            case RENAME_DATAPROP_:
                // Rename a data property
                // args[ String oldName,, String newName ]
                ontoRef.renameEntity(ontoRef.getOWLDataProperty(args.get(0)), args.get(1));
                setResponse(true, 0, "");
                return response;

            case RENAME_OBJECT_PROP:
                // Rename an object property
                // args[ String oldName, String newName ]
                ontoRef.renameEntity(ontoRef.getOWLObjectProperty(args.get(0)), args.get(1));
                setResponse(true, 0, "");
                return response;

            /// SWRL /////////////////////////////////////////////////////

            case SWRL__:
                // Online SWRL support
                // TODO: ver1
                return response;

        }

        setResponse(false, 204, "Unexpected command. Somehow a recognized command " +
                "has not fallen in any execution case.");
        return  response;
    }


    private void setResponse(Boolean success, int exitCode, String errorMessage){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setQueriedObjects(new ArrayList<String>());

        if (!referenceName.equals("")) {
            OWLReferences ontoRef = (OWLReferences) OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef.getReasoner() != null) {
                response.setIsConsistent(ontoRef.getReasoner().isConsistent());
            }
        }
    }

    private void setResponse(Boolean success, int exitCode, String errorMessage, List<String> queriedObjects){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setQueriedObjects(queriedObjects);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef = (OWLReferences) OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef.getReasoner() != null) {
                response.setIsConsistent(ontoRef.getReasoner().isConsistent());
            }
        }
    }

    private void setResponseSparql(Boolean success, int exitCode, String errorMessage, List<QueryItem> queriedObjects){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setSparqlQueriedObjects(queriedObjects);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef = (OWLReferences) OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef.getReasoner() != null) {
                response.setIsConsistent(ontoRef.getReasoner().isConsistent());
            }
        }
    }
    
    private List<String> getStringListFromQuery(Set<?> queriedResults, OWLReferences ontoRef){
        if (!fullIRIName){
            return new ArrayList<>(ontoRef.getOWLObjectName(queriedResults));
        }else{
            List<String> resultList = new ArrayList<String>();
            Iterator<?> queriedResultIt = queriedResults.iterator();
            while (queriedResultIt.hasNext()){
                resultList.add(queriedResultIt.next().toString());
            }
            return resultList;
        }
    }


    private static Object valueTypeCasting(String type, String value){
        switch(type.toUpperCase()){
            case "STRING":
                return value;
            case "INT":
            case "INTEGER":
                return Integer.valueOf(value);
            case "FLOAT":
                return Float.valueOf(value);
            case "LONG":
                return Long.valueOf(value);
            case "DOUBLE":
                return Double.valueOf(value);
            case "BOOLEAN":
            case "BOOL":
                return Boolean.valueOf(value);
            // TODO: add default case OWLLiteral
        }
        return null;
    }

    private enum OWLReferenceCommandsEnum {

        /////////////////  SYSTEM UTILITIES COMMANDS  /////////////////

        INVALID_COMMAND,
        CREATE__,
        DELETE__,
        MOUNT__,
        UNMOUNT__,
        SAVE__,
        SAVE_INFERENCE_,
        LOAD_FILE_,
        LOAD_FILE_MOUNTED,
        LOAD_WEB_,
        LOAD_WEB_MOUNTED,
        DROP__,
        LOG_FILE_ON,
        LOG_FILE_OFF,
        LOG_SCREEN_ON,
        LOG_SCREEN_OFF,

        /////////////////       QUERY COMMANDS       /////////////////

        //  Only "simple" queries allowed.
        //  Compound queries should be processed from the client side.

        QUERY_IND_,
        QUERY_IND_CLASS,
        QUERY_DATAPROP_IND,
        QUERY_DATAPROP_CLASS,
        QUERY_OBJECTPROP_IND,
        QUERY_OBJECTPROP_CLASS,
        QUERY_CLASS_IND,
        QUERY_CLASS_CLASS,
        QUERY_DATAPROP_DATAPROP,
        QUERY_OBJECTPROP_OBJECTPROP,
        QUERY_IND_DATAPROP,
        QUERY_IND_OBJECTPROP,
        QUERY_SPARQL_,
        QUERY_SPARQL_FORMATTED,

        /////////////////    MANIPULATION COMMANDS    /////////////////

        APPLY__,
        REASON__,

        ADD_IND_,
        ADD_CLASS_,
        ADD_DATAPROP_,
        ADD_OBJECTPROP_,
        ADD_IND_CLASS,
        ADD_CLASS_CLASS,
        ADD_DATAPROP_IND,
        ADD_OBJECTPROP_IND,
        ADD_DATAPROP_DATAPROP,
        ADD_OBJECTPROP_OBJECTPROP,
        ADD_DISJOINT_IND,
        ADD_DISJOINT_CLASS,


        REMOVE_IND_,
        REMOVE_CLASS_,
        REMOVE_DATAPROP_,
        REMOVE_OBJECTPROP_,
        REMOVE_IND_CLASS,
        REMOVE_CLASS_CLASS,
        REMOVE_DATAPROP_IND,
        REMOVE_OBJECTPROP_IND,
        REMOVE_DATAPROP_DATAPROP,
        REMOVE_OBJECTPROP_OBJECTPROP,

        REPLACE_DATAPROP_IND,
        REPLACE_OBJECTPROP_IND,
        RENAME_IND_,
        RENAME_CLASS_,
        RENAME_DATAPROP_,
        RENAME_OBJECT_PROP,

        SWRL__;
    }
}