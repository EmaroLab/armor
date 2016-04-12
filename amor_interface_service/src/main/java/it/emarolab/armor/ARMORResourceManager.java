package it.emarolab.armor;

import java.util.*;

import it.emarolab.amor.owlDebugger.OFGUI.ClassExchange;
import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.amor.owlInterface.OWLReferencesInterface;
import it.emarolab.amor.owlInterface.OWLReferencesInterface.OWLReferencesContainer;

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
 * It is used by {@link #ARMORCommand} whenever an ontology is loaded or dropped.
 * </p>
 *
 * @see
 *
 *
 * @version 2.0
 */

public class ARMORResourceManager {

    private static HashMap<String, String> MountedOntologiesTable = new HashMap<>();

    private ARMORResourceManager(){
        throw new AssertionError();
    }

    public static OWLReferences loadOntologyFromFile(String referenceName, List<String> args){
        OWLReferences ontoRef;
        if (getOntologiesNames().contains(referenceName)){
            // TODO: logging
            ontoRef = (OWLReferences)OWLReferencesContainer.getOWLReferences(referenceName);
            System.out.println("The ontology you are trying to add is already in the database.");
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
            MountedOntologiesTable.put(referenceName, "none");
        }
        return ontoRef;
    }

    public static OWLReferences loadOntologyFromWeb(String referenceName, List<String> args){
        OWLReferences ontoRef;
        if (getOntologiesNames().contains(referenceName)){
            // TODO: logging
            ontoRef = (OWLReferences)OWLReferencesContainer.getOWLReferences(referenceName);
            System.out.println("The ontology you are trying to add is already in the database.");
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
            MountedOntologiesTable.put(referenceName, "none");
        }
        return ontoRef;
    }

    public static void removeOntology(String referenceName){
        try {
            OWLReferencesInterface ontology = OWLReferencesContainer.getOWLReferences(referenceName);
            ontology.finalize();
        }catch(Throwable e){
            // TODO: log
        }
        MountedOntologiesTable.remove(referenceName);
    }

    public static Boolean tryMountClient(String clientName, String referenceName) {
        // Tries to mount a client on an ontology.
        // It fails if the ontology already exists or another client is already mounted on it.
        // It succeeds but warns the user, if the client is already mount on the ontology.
        if (getOntologiesNames().contains(referenceName)) {
            Set<String> activeOntologies = getInactiveOntologiesNames();
            if (activeOntologies.contains(referenceName)) {
                MountedOntologiesTable.put(referenceName, clientName);
                return true;
            } else {
                if (MountedOntologiesTable.get(referenceName).equals(clientName)) {
                    // TODO: logging
                    System.out.println(clientName + " is already mounted on " + referenceName + ".");
                    return true;
                } else {
                    // TODO: logging
                    String currentClient = MountedOntologiesTable.get(referenceName);
                    System.out.println(currentClient + "is currently mounted on " + referenceName + ". Please unmount "
                            + currentClient + "before trying to mount another client.");
                    return false;
                }
            }
        } else {
            // TODO: logging
            System.out.println("No OWLReference initialized with this name:" + referenceName);
            return false;
        }
    }

    public static void unmountClientFromAll(String clientName){
        Set<String> ontologies = MountedOntologiesTable.keySet();
        Integer interruptedConnections = 0;
        for (String ontology : ontologies){
            if (MountedOntologiesTable.get(ontology).equals(clientName)){
                MountedOntologiesTable.put(ontology, "none");
            }
            interruptedConnections += 1;
        }
        // TODO: logging
        System.out.println(clientName + " disconnected from " + interruptedConnections.toString() + " ontologies.");
    }

    public static void unmountClientFromOntology(String clientName, String referenceName){
        if (MountedOntologiesTable.get(referenceName).equals(clientName)){
            MountedOntologiesTable.put(referenceName,"none");
        }else{
            // TODO: logging
            System.out.println("No client " + clientName + " mounted on " + referenceName + ".");
        }
    }

    public static Set<String> getOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : MountedOntologiesTable.keySet()){
            ontologies.add(ontology);
        }
        return ontologies;
    }

    public static Set<String> getActiveOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : MountedOntologiesTable.keySet()){
            String client = MountedOntologiesTable.get(ontology);
            if (!client.equals("none")) {
                ontologies.add(ontology);
            }
        }
        return ontologies;
    }

    public static Set<String> getInactiveOntologiesNames(){
        Set<String> ontologies = new HashSet<>();
        for (String ontology : MountedOntologiesTable.keySet()){
            String client = MountedOntologiesTable.get(ontology);
            if (client.equals("none")) {
                ontologies.add(ontology);
            }
        }
        return ontologies;
    }

    public static Set<String> getRegisteredClientsNames(){
        Set<String> clients = new HashSet<>();
        for (String client : MountedOntologiesTable.values()){
            if (!client.equals("none")){
                clients.add(client);
            }
        }
        return clients;
    }

    public static boolean isAvailable(String client, String ontology){
        return (MountedOntologiesTable.get(ontology).equals(client) ||
                MountedOntologiesTable.get(ontology).equals("none"));
    }

}
