package it.emarolab.armor;

import org.ros.node.ConnectedNode;
import armor_msgs.*;
import java.util.*;
import static it.emarolab.armor.ARMORCommandsUtils.setResponse;

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
 * ARMORCommandExecutive is an interface to aMOR. It parses the requests to ARMOR
 * and calls the appropriate method of the static class ARMORCommandUtility.
 * Commands documentation can be found on the project page at https://github.com/EmaroLab.
 * </p>
 *
 * @see
 *
 *
 * @version 2.0
 */

@FunctionalInterface
interface ARMORCommandInterface<T>{
    T run();
}

class ARMORCommandExecutive {

    private final static String COMMAND_DELIMITER = "_"; //regex reserved chars like + must be escaped (\\+)
    private String fullCommand;
    private String referenceName;
    private ArmorDirectiveRes response;
    private Boolean fullIRIName;
    private ConnectedNode connectedNode;
    private Map<String, ARMORCommandInterface> commands = new HashMap<>();


    ARMORCommandExecutive(ArmorDirectiveReq request, ArmorDirectiveRes response,
                          final Boolean fullIRIName, final ConnectedNode connectedNode){

        // Parse command and directives

        this.response = response;
        this.fullIRIName = fullIRIName;
        this.connectedNode = connectedNode;
        this.response.setSuccess(true);       //true until call fails

        String PrimaryCommandSpec = "";
        String SecondaryCommandSpec = "";

        if (request.getReferenceName().equals("")){
            setResponse("", false, 201,
                    "Invalid request. No reference name specified.", response);
            connectedNode.getLog().error("Invalid request. No reference name specified.");
            this.response.setSuccess(false);
            return;
        }

        this.referenceName = request.getReferenceName();

        if (request.getCommand().equals("")){
            setResponse(this.referenceName,false, 202,
                    "Invalid request. No command specified.", response);
            connectedNode.getLog().error("Invalid request. No command specified.");
            this.response.setSuccess(false);
            return;
        }

        if (request.getPrimaryCommandSpec() != null){
            PrimaryCommandSpec = request.getPrimaryCommandSpec();
        }

        if (request.getSecondaryCommandSpec() != null){
            SecondaryCommandSpec = request.getSecondaryCommandSpec();
        }

        this.fullCommand = request.getCommand() + COMMAND_DELIMITER +
                PrimaryCommandSpec + COMMAND_DELIMITER + SecondaryCommandSpec;

        initializeCommands(request, response);
    }

    ArmorDirectiveRes executeCommand(){
        String formattedCommand = fullCommand.toUpperCase().replaceAll(COMMAND_DELIMITER, "_");
        try {
            commands.get(formattedCommand).run();
        }catch (NullPointerException e){
            setResponse(this.referenceName, false, 205,
                    "Malformed command. No Command/Specs combination match found. " +
                            "Please check your service requests and retry.", this.response);
        }
        return response;
    }

    ArmorDirectiveRes getServiceResponse() {
        return response;
    }

    private void initializeCommands(ArmorDirectiveReq request, ArmorDirectiveRes response){

        /////////////////  SYSTEM UTILITIES COMMANDS  /////////////////

        commands.put("INVALID_COMMAND_",         (() -> ARMORCommandUtility.invalidCommand                        (request, response)));
        commands.put("MOUNT__",                  (() -> ARMORCommandUtility.mount                                 (request, response, connectedNode)));
        commands.put("UNMOUNT__",                (() -> ARMORCommandUtility.unmount                               (request, response)));
        commands.put("SAVE__",                   (() -> ARMORCommandUtility.save                                  (request, response)));
        commands.put("SAVE_INFERENCE_",          (() -> ARMORCommandUtility.saveWithInferences                    (request, response)));
        commands.put("LOAD_FILE_",               (() -> ARMORCommandUtility.loadFromFile                          (request, response)));
        commands.put("LOAD_FILE_MOUNTED",        (() -> ARMORCommandUtility.loadFromFileMounted                   (request, response, connectedNode)));
        commands.put("LOAD_WEB_",                (() -> ARMORCommandUtility.loadFromWeb                           (request, response)));
        commands.put("LOAD_WEB_MOUNTED",         (() -> ARMORCommandUtility.loadFromWebMounted                    (request, response, connectedNode)));
        commands.put("GET_ALL_REFS",             (() -> ARMORCommandUtility.getAllReferences                      (request, response)));
        commands.put("GET_REF_CLIENT",           (() -> ARMORCommandUtility.getClients                            (request, response)));
        commands.put("DROP__",                   (() -> ARMORCommandUtility.drop                                  (request, response)));
        commands.put("LOG_FILE_ON",              (() -> ARMORCommandUtility.logToFileOn                           (request, response)));
        commands.put("LOG_FILE_OFF",             (() -> ARMORCommandUtility.logToFileOff                          (request, response)));
        commands.put("LOG_SCREEN_ON",            (() -> ARMORCommandUtility.logToScreenOn                         (request, response)));
        commands.put("LOG_SCREEN_OFF",           (() -> ARMORCommandUtility.logToScreenOff                        (request, response)));

        /////////////////       QUERY COMMANDS       /////////////////

        commands.put("QUERY_IND_",               (() -> ARMORCommandsQuery.queryInd                             (request, response, fullIRIName)));
        commands.put("QUERY_IND_CLASS",          (() -> ARMORCommandsQuery.queryIndB2Class                      (request, response, fullIRIName)));
        commands.put("QUERY_DATAPROP_IND",       (() -> ARMORCommandsQuery.queryDatapropValuesB2Ind             (request, response, fullIRIName)));
        commands.put("QUERY_OBJECTPROP_IND",     (() -> ARMORCommandsQuery.queryObjectpropValuesB2Ind           (request, response, fullIRIName)));
        commands.put("QUERY_CLASS_IND",          (() -> ARMORCommandsQuery.queryIndDefiningClasses              (request, response, fullIRIName)));
        commands.put("QUERY_CLASS_CLASS",        (() -> ARMORCommandsQuery.querySubclasses                      (request, response, fullIRIName)));
        commands.put("QUERY_CLASS_RESTRICTIONS", (() -> ARMORCommandsQuery.queryClassRestrictions               (request, response, fullIRIName)));
        commands.put("QUERY_IND_DATAPROP",       (() -> ARMORCommandsQuery.queryDatapropsB2Ind                  (request, response, fullIRIName)));
        commands.put("QUERY_IND_OBJECTPROP",     (() -> ARMORCommandsQuery.queryObjectpropsB2Ind                (request, response, fullIRIName)));
        commands.put("QUERY_SPARQL_",            (() -> ARMORCommandsQuery.querySPARQL                          (request, response, fullIRIName, connectedNode)));
        commands.put("QUERY_SPARQL_FORMATTED",   (() -> ARMORCommandsQuery.querySPARQLFormatted                 (request, response, fullIRIName, connectedNode)));

        /////////////////    MANIPULATION COMMANDS    /////////////////

        /// ADD ///////////////////////////////////////////////////////

        commands.put("APPLY__",                  (() -> ARMORCommandsManipulation.apply                         (request, response, connectedNode)));
        commands.put("REASON__",                 (() -> ARMORCommandsManipulation.reason                        (request, response, connectedNode)));
        commands.put("ADD_IND_",                 (() -> ARMORCommandsManipulation.addIndividual                 (request, response, connectedNode)));
        commands.put("ADD_CLASS_",               (() -> ARMORCommandsManipulation.addClass                      (request, response, connectedNode)));
        commands.put("ADD_IND_CLASS",            (() -> ARMORCommandsManipulation.addIndToClass                 (request, response, connectedNode)));
        commands.put("ADD_CLASS_CLASS",          (() -> ARMORCommandsManipulation.addClassToClass               (request, response, connectedNode)));
        commands.put("ADD_DATAPROP_IND",         (() -> ARMORCommandsManipulation.addDatapropToInd              (request, response, connectedNode)));
        commands.put("ADD_OBJECTPROP_IND",       (() -> ARMORCommandsManipulation.addObjectpropToInd            (request, response, connectedNode)));
        commands.put("ADD_CARDINALITY_MIN",      (() -> ARMORCommandsManipulation.addMinimumCardinality         (request, response, connectedNode)));
        commands.put("DISJOINT_IND_",            (() -> ARMORCommandsManipulation.makeIndividualsDisjoint       (request, response, connectedNode)));
        commands.put("DISJOINT_CLASS_",          (() -> ARMORCommandsManipulation.makeClassesDisjoint           (request, response, connectedNode)));
        commands.put("DISJOINT_IND_CLASS",       (() -> ARMORCommandsManipulation.makeClassIndividualsDisjoint  (request, response, connectedNode)));
        commands.put("DISJOINT_CLASS_CLASS",     (() -> ARMORCommandsManipulation.makeSubclassesDisjoint        (request, response, connectedNode)));
        commands.put("MAKE_EQUIVALENT_CLASS",    (() -> ARMORCommandsManipulation.makeEquivalentClass           (request, response, connectedNode)));

        /// REMOVE ////////////////////////////////////////////////////

        commands.put("REMOVE_IND_",              (() -> ARMORCommandsManipulation.removeIndividual              (request, response, connectedNode)));
        commands.put("REMOVE_CLASS_",            (() -> ARMORCommandsManipulation.removeClass                   (request, response, connectedNode)));
        commands.put("REMOVE_IND_CLASS",         (() -> ARMORCommandsManipulation.removeIndFromClass            (request, response, connectedNode)));
        commands.put("REMOVE_CLASS_CLASS",       (() -> ARMORCommandsManipulation.removeSubclassFromClass       (request, response, connectedNode)));
        commands.put("REMOVE_DATAPROP_IND",      (() -> ARMORCommandsManipulation.removeDatapropFromInd         (request, response, connectedNode)));
        commands.put("REMOVE_OBJECTPROP_IND",    (() -> ARMORCommandsManipulation.removeObjectpropFromInd       (request, response, connectedNode)));

        /// REPLACE ///////////////////////////////////////////////////

        commands.put("REPLACE_DATAPROP_IND",     (() -> ARMORCommandsManipulation.replaceDatapropValue          (request, response, connectedNode)));
        commands.put("REPLACE_OBJECTPROP_IND",   (() -> ARMORCommandsManipulation.replaceObjectpropValue        (request, response, connectedNode)));

        /// RENAME ////////////////////////////////////////////////////

        commands.put("RENAME_IND_",              (() -> ARMORCommandsManipulation.renameIndividual              (request, response, connectedNode)));
        commands.put("RENAME_CLASS_",            (() -> ARMORCommandsManipulation.renameClass                   (request, response, connectedNode)));
        commands.put("RENAME_DATAPROP_",         (() -> ARMORCommandsManipulation.renameDataprop                (request, response, connectedNode)));
        commands.put("RENAME_OBJECTPROP_",       (() -> ARMORCommandsManipulation.renameObjectprop              (request, response, connectedNode)));

    }
}