package it.emarolab.armor;

import java.util.*;

import it.emarolab.amor.owlDebugger.OFGUI.ClassExchange;
import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.amor.owlInterface.OWLReferencesInterface;
import it.emarolab.amor.owlInterface.OWLReferencesInterface.OWLReferencesContainer;
import org.ros.node.ConnectedNode;

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
 * ARMORResourceManager class is used to load owl files and implement
 * a mount/unmount system. This can be used by users to ensure that multiple
 * nodes are not manipulating the same reference at the same time.
 * It is used by {@link ARMORCommandExecutive} whenever an ontology is loaded or dropped.
 * </p>
 *
 * @see
 *
 *
 * @version 2.0
 */

class ARMORResourceManager {

    private static HashMap<String, String> mountedOntologiesTable = new HashMap<>();
    private static ConnectedNode workingNode = null;

    private ARMORResourceManager(){
        throw new AssertionError();
    }

    static OWLReferences loadOntologyFromFile(String referenceName, List<String> args){
        OWLReferences ontoRef;
        if (getOntologiesNames().contains(referenceName)){
            ontoRef = (OWLReferences)OWLReferencesContainer.getOWLReferences(referenceName);
            logWarn("The ontology you are trying to add is already in the database.");
        }else{
            // args[ String filePath, String iriPath, Boolean bufferedManipulation,
            //       String reasoner, Boolean bufferedFlag ]
            switch (args.get(3).toUpperCase()){
                case(""):  //actually defaults to Pellet
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromFile
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("HERMIT"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromFileWithHermit
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("PELLET"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromFileWithPellet
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("FACT"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromFileWithFact
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("SNOROCKET"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromFileWithSnorocket
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                default:
                    ontoRef = OWLReferencesContainer.newOWLReferences
                            (referenceName, args.get(0), args.get(1), args.get(3), Boolean.valueOf(args.get(4)),
                                    OWLReferencesContainer.COMMAND_LOAD_FILE);

            }

            if (ClassExchange.getOntoNameObj() != null){
                ClassExchange.getOntoNameObj().setText(referenceName);}
            mountedOntologiesTable.put(referenceName, "none");
        }
        return ontoRef;
    }

    static OWLReferences loadOntologyFromWeb(String referenceName, List<String> args){
        OWLReferences ontoRef;
        if (getOntologiesNames().contains(referenceName)){
            ontoRef = (OWLReferences)OWLReferencesContainer.getOWLReferences(referenceName);
            logWarn("The ontology you are trying to add is already in the database.");
        }else{
            // args[ String filePath, String iriPath, Boolean bufferedManipulation,
            //       String reasoner, Boolean bufferedFlag ]
            switch (args.get(3).toUpperCase()){
                case(""):  //actually defaults to Pellet
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromWeb
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("HERMIT"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromWebWithHermit
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("PELLET"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromWebWithPellet
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("FACT"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromWebWithFact
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                case("SNOROCKET"):
                    ontoRef = OWLReferencesContainer.newOWLReferenceFromWebWithSnorocket
                            (referenceName, args.get(0), args.get(1), Boolean.valueOf(args.get(4)));
                    break;
                default:
                    ontoRef = OWLReferencesContainer.newOWLReferences
                            (referenceName, args.get(0), args.get(1), args.get(3), Boolean.valueOf(args.get(4)),
                                    OWLReferencesContainer.COMMAND_LOAD_WEB);
            }
            if (ClassExchange.getOntoNameObj() != null){
                ClassExchange.getOntoNameObj().setText(referenceName);}
            mountedOntologiesTable.put(referenceName, "none");
        }
        return ontoRef;
    }

    static void removeOntology(String referenceName){
        try {
            OWLReferencesInterface ontology = OWLReferencesContainer.getOWLReferences(referenceName);
            ontology.finalize();
        }catch(Throwable e){
            // TODO: log
        }
        mountedOntologiesTable.remove(referenceName);
    }

    static Boolean tryMountClient(String clientName, String referenceName) {
        // Tries to mount a client on an ontology. It fails if client is already
        // mounted on it or the reference does not exist.
        // It succeeds but warns the user, if the client is already mounted on the ontology.
        if (getOntologiesNames().contains(referenceName)) {
            Set<String> activeOntologies = getInactiveOntologiesNames();
            if (activeOntologies.contains(referenceName)) {
                mountedOntologiesTable.put(referenceName, clientName);
                return true;
            } else {
                if (mountedOntologiesTable.get(referenceName).equals(clientName)) {
                    logWarn(clientName + " is already mounted on " + referenceName + ".");
                    return true;
                } else {
                    String currentClient = mountedOntologiesTable.get(referenceName);
                    logError(currentClient + "is currently mounted on " + referenceName + ". Please unmount "
                            + currentClient + "before trying to mount another client.");
                    return false;
                }
            }
        } else {
            logError("No OWLReference initialized with this name:" + referenceName);
            return false;
        }
    }

    public static void unmountClientFromAll(String clientName){
        Set<String> ontologies = mountedOntologiesTable.keySet();
        Integer interruptedConnections = 0;
        for (String ontology : ontologies){
            if (mountedOntologiesTable.get(ontology).equals(clientName)){
                mountedOntologiesTable.put(ontology, "none");
            }
            interruptedConnections += 1;
        }
        logInfo(clientName + " disconnected from " + interruptedConnections.toString() + " ontologies.");
    }

    static void unmountClientFromOntology(String clientName, String referenceName){
        if (mountedOntologiesTable.get(referenceName).equals(clientName)){
            mountedOntologiesTable.put(referenceName,"none");
        }else{
            logWarn("No client " + clientName + " mounted on " + referenceName + ".");
        }
    }

    static Set<String> getOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : mountedOntologiesTable.keySet()){
            ontologies.add(ontology);
        }
        return ontologies;
    }

    public static Set<String> getActiveOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : mountedOntologiesTable.keySet()){
            String client = mountedOntologiesTable.get(ontology);
            if (!client.equals("none")) {
                ontologies.add(ontology);
            }
        }
        return ontologies;
    }

    static Set<String> getInactiveOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : mountedOntologiesTable.keySet()){
            String client = mountedOntologiesTable.get(ontology);
            if (client.equals("none")) {
                ontologies.add(ontology);
            }
        }
        return ontologies;
    }

    static String getOntologyMountedClient(String ontology){
        String client = mountedOntologiesTable.get(ontology);
        return client;
    }

    public static Set<String> getRegisteredClientsNames(){
        Set<String> clients = new HashSet<>();
        for (String client : mountedOntologiesTable.values()){
            if (!client.equals("none")){
                clients.add(client);
            }
        }
        return clients;
    }

    static boolean isAvailable(String client, String ontology){
        return (mountedOntologiesTable.get(ontology).equals(client) ||
                mountedOntologiesTable.get(ontology).equals("none"));
    }

    static void setLogging(final ConnectedNode connectedNode){
        workingNode = connectedNode;
    }

    private static void logInfo(String str){
        if (workingNode != null){
            workingNode.getLog().info(str);
        }
    }

    private static void logDebug(String str){
        if (workingNode != null){
            workingNode.getLog().debug(str);
        }
    }

    private static void logWarn(String str){
        if (workingNode != null){
            workingNode.getLog().warn(str);
        }
    }

    private static void logError(String str){
        if (workingNode != null){
            workingNode.getLog().error(str);
        }
    }

}
