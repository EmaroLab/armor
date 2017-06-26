package it.emarolab.armor;

import armor_msgs.ArmorDirectiveReq;
import armor_msgs.ArmorDirectiveRes;
import it.emarolab.amor.owlDebugger.Logger;
import it.emarolab.amor.owlInterface.*;
import org.ros.node.ConnectedNode;
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
 * This class contains static methods to perform utility operations on an ontology.
 * </p>
 *
 * @version 2.0
 */


class ARMORCommandUtility {


    /////////////////  SYSTEM UTILITIES COMMANDS  /////////////////


    static ArmorDirectiveRes invalidCommand(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Catches invalid commands.
        setResponse(request.getReferenceName(), false, 205, "Invalid command.", response);
        return response;
    }

    static ArmorDirectiveRes mount(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // Mount a client on an existing OWLReference to allow manipulation commands
        // TODO: catch errors
        String clientName = request.getClientName();
        String referenceName = request.getReferenceName();
        if (ARMORResourceManager.tryMountClient(clientName, referenceName)) {
            setResponse(referenceName, true, 0, "", response);
        } else {
            String errorMessage = clientName + " cannot be mounted on " + referenceName
                    + ". Another client is already mounted on it.";
            setResponse(referenceName, false, 203, errorMessage, response);
            connectedNode.getLog().error(errorMessage);
        }
        return response;
    }

    static ArmorDirectiveRes unmount(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Unmount a client from an existing OWLReference
        String clientName = request.getClientName();
        String referenceName = request.getReferenceName();
        ARMORResourceManager.unmountClientFromOntology(clientName, referenceName);
        setResponse(referenceName, true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes save(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Save the manipulations operated on the OWLReference in request
        // <pre><code>args[ String filepath ]</pre></code>
        String referenceName = request.getReferenceName();
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        if (!request.getArgs().get(0).equals("")) {
            ontoRef.saveOntology(false, request.getArgs().get(0));
        } else {
            ontoRef.saveOntology(false);
        }
        // TODO: catch error
        setResponse(referenceName, true, 0, "", response);
        return response;
    }


    static ArmorDirectiveRes saveWithInferences(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Save the manipulations operated on the OWLReference in request, including inferred axioms
        // <pre><code>args[ String filepath ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        String referenceName = request.getReferenceName();
        if (!request.getArgs().get(0).equals("")) {
            ontoRef.saveOntology(true, request.getArgs().get(0));
        } else {
            ontoRef.saveOntology(true);
        }
        // TODO: catch error
        setResponse(referenceName, true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes loadFromFile(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Load an OWLReference from and ontology file
        // <pre><code>args[ String filePath, String iriPath, Boolean bufferedManipulation,
        // String reasoner, Boolean bufferedReasoner ]</pre></code>
        String referenceName = request.getReferenceName();
        OWLReferences ontoRef = ARMORResourceManager.loadOntologyFromFile(referenceName, request.getArgs());
        ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(request.getArgs().get(2)));
        // TODO: catch error
        setResponse(referenceName, true, 0, "", response);
        return response;
    }


    static ArmorDirectiveRes loadFromFileMounted(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // Load an OWLReference from and ontology file and mount the client on it
        // <pre><code>args[ String filePath, String iriPath, String bufferedManipulation,
        // String reasoner, Boolean bufferedReasoner ]</pre></code>
        String clientName = request.getClientName();
        String referenceName = request.getReferenceName();
        OWLReferences ontoRef = ARMORResourceManager.loadOntologyFromFile(referenceName, request.getArgs());
        ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(request.getArgs().get(2)));
        // TODO: catch error
        if (ARMORResourceManager.tryMountClient(clientName, referenceName)) {
            setResponse(referenceName, true, 0, "", response);
        } else {
            String errorMessage = "Reference loaded from file but " + clientName + " cannot be mounted on reference "
                    + referenceName + ". Another client is already mounted on it.";
            setResponse(referenceName, false, 203, errorMessage, response);
            connectedNode.getLog().error(errorMessage);
        }
        return response;
    }

    static ArmorDirectiveRes loadFromWeb(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Load an OWLReference from the web
        // <pre><code>args[ String filePath, String iriPath, String bufferedManipulation,
        // String reasoner, Boolean bufferedFlag ]</pre></code>
        String referenceName = request.getReferenceName();
        OWLReferences ontoRef = ARMORResourceManager.loadOntologyFromWeb(referenceName, request.getArgs());
        ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(request.getArgs().get(2)));
        // TODO: catch error
        setResponse(referenceName, true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes loadFromWebMounted(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // Load an OWLReference from the web and mount the client on it
        // <pre><code>args[ String filePath, String iriPath, String bufferedManipulation,
        // String reasoner, Boolean bufferedFlag ]</pre></code>
        String clientName = request.getClientName();
        String referenceName = request.getReferenceName();
        OWLReferences ontoRef = ARMORResourceManager.loadOntologyFromWeb(referenceName, request.getArgs());
        ontoRef.setOWLManipulatorBuffering(Boolean.valueOf(request.getArgs().get(2)));
        // TODO: catch error
        if (ARMORResourceManager.tryMountClient(clientName, referenceName)) {
            setResponse(referenceName, true, 0, "", response);
        } else {
            String errorMessage = "Reference loaded from web but " + clientName + " cannot mounted on reference "
                    + referenceName + ". Another client is already mounted on it.";
            setResponse(referenceName, false, 203, errorMessage, response);
            connectedNode.getLog().error(errorMessage);
        }
        return response;
    }

    static ArmorDirectiveRes getAllReferences(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Returns the list of all references currently available
        Set<String> refs = ARMORResourceManager.getOntologiesNames();
        List<String> refsList = new ArrayList<>();
        refsList.addAll(refs);
        setResponse(request.getReferenceName(), true, 0, "", refsList, response);
        return response;
    }

    static ArmorDirectiveRes getClients(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // return the clients mounted on a reference
        List<String> clientList = new ArrayList<>();
        String client = ARMORResourceManager.getOntologyMountedClient(request.getArgs().get(0));
        clientList.add(client);
        setResponse(request.getReferenceName(), true, 0, "", clientList, response);
        return response;
    }


    static ArmorDirectiveRes drop(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Drop an OWLReference from ARMOR
        ARMORResourceManager.removeOntology(request.getReferenceName());
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes logToFileOn(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Start logging to file or change the logging file
        // <pre><code>args [ String logFilePath ]</pre></code>
        Logger.setPrintOnFile(request.getArgs().get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes logToFileOff(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Stop logging to file
        Logger.setPrintOnFile(Logger.NOFILEPRINTING_KeyWord);
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes logToScreenOn(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Start logging to screen
        Logger.setPrintOnConsole(true);
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes logToScreenOff(ArmorDirectiveReq request, ArmorDirectiveRes response) {
        // Stop logging on screen
        Logger.setPrintOnConsole(false);
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }
}
