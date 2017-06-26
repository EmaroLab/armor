package it.emarolab.armor;

import armor_msgs.ArmorDirectiveReq;
import armor_msgs.ArmorDirectiveRes;
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
 * This class contains static methods to perform manipulations on an ontology.
 * </p>
 *
 * @version 2.0
 */

public class ARMORCommandsManipulation {

    /////////////////    MANIPULATION COMMANDS    /////////////////

    /* WARNING: a client must be mounted on the ontology to get query
       commands executed. All manipulation requests on an ontology
       from any service but the mounted one will be ignored.*/

    private static ArmorDirectiveRes checkReferenceIsAvailable(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                               ConnectedNode connectedNode){
        // Checks if any client is mounted on the reference it is being manipulated.  If another
        // client is mounted returns with an error message. it is called before every manipulation.
        String errorMessage = request.getClientName() + " cannot connect to " + request.getReferenceName()
                + ". Another client is already mounted on it.";
        setResponse(request.getReferenceName(), false, 102, errorMessage, response);
        connectedNode.getLog().error(errorMessage);
        return response;
    }

    /// ADD //////////////////////////////////////////////////////

    static ArmorDirectiveRes apply(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // applies buffered manipulations if the manipulation buffer is active
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        ontoRef.applyOWLManipulatorChanges();
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes reason(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // Run the reasoner when buffered reasoning is active
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        ontoRef.synchronizeReasoner();
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes addIndividual(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                           ConnectedNode connectedNode) {
        // Add an individual to an OWLReference
        // <pre><code>args[ String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        ontoRef.addIndividual(request.getArgs().get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes addClass(ArmorDirectiveReq request, ArmorDirectiveRes response, ConnectedNode connectedNode) {
        // Add a class to an OWLReference
        // <pre><code>args[ String className ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        ontoRef.addClass(request.getArgs().get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes addIndToClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                           ConnectedNode connectedNode) {
        // Add an individual to a class defined in current reference
        // <pre><code>args[ String indName, String clsName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.addIndividualB2Class(args.get(0), args.get(1));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes addClassToClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                             ConnectedNode connectedNode) {
        // Add an class to a class defined in current reference
        // <pre><code>args[ String subclassName, String superclass Name ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.addSubClassOf(args.get(1), args.get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes addDatapropToInd(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                              ConnectedNode connectedNode) {
        // Add a data property to an individual in current reference
        // <pre><code>args[ String propName, String indName, String typename, String value ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        Object castedObj = valueTypeCasting(args.get(2), args.get(3));
        if (castedObj != null) {
            ontoRef.addDataPropertyB2Individual
                    (args.get(1), args.get(0), castedObj);
            setResponse(request.getReferenceName(), true, 0, "", response);
        } else {
            // TODO: log
            String errorMessage = "Value type " + args.get(2) + " not supported. "
                    + args.get(0) + "not added to " + args.get(1) + ".";
            connectedNode.getLog().error(errorMessage);
            setResponse(request.getReferenceName(),true, 101, errorMessage, response);
        }
        return response;
    }

    static ArmorDirectiveRes addObjectpropToInd(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                ConnectedNode connectedNode) {
        // Add a data property to an individual in current reference
        // <pre><code>args[ String propName, String indName, String valueIndividual ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.addObjectPropertyB2Individual(args.get(1), args.get(0), args.get(2));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }


    static ArmorDirectiveRes addMinimumCardinality(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                   ConnectedNode connectedNode) {
        // Add minimum cardinality to a class defining property
        // <pre><code>args[ String className, String propertyName, String cardinality, String valueType ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        SemanticRestriction.ApplyingCardinalityRestriction rest;
        rest = new SemanticRestriction.ClassRestrictedOnMinObject(
                ontoRef.getOWLClass(args.get(0)), ontoRef.getOWLClass(args.get(3)),
                ontoRef.getOWLObjectProperty(args.get(1)), Integer.valueOf(args.get(2)));
        ontoRef.addRestriction(rest);
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes makeIndividualsDisjoint(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                     ConnectedNode connectedNode) {
        // Makes the individuals of a list mutually disjoint
        // <pre><code>args[ String ind1, String ind2, ... ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.makeDisjointIndividualNames(new HashSet<String>(args));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes makeClassesDisjoint(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                 ConnectedNode connectedNode) {
        // Makes the classes of a list mutually disjoint
        // <pre><code>args[ String cls1, String cls2, ... ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.makeDisjointClassNames(new HashSet<String>(args));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes makeClassIndividualsDisjoint(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                          ConnectedNode connectedNode) {
        // Makes the individuals of a class mutually disjoint
        // <pre><code>args[ String ind1, String ind2, ... ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.makeDisjointIndividuals(ontoRef.getIndividualB2Class(args.get(0)));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes makeSubclassesDisjoint(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                    ConnectedNode connectedNode) {
        // Makes the subclasses of a class mutually disjoint
        // <pre><code>args[ String cls1, String cls2, ... ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.makeDisjointClasses(ontoRef.getSubClassOf(args.get(0)));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes makeEquivalentClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                 ConnectedNode connectedNode) {
        // Convert super classes axioms in a conjunction of expressions
        // in the class definition.
        // <pre><code>args[ String clsName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.convertSuperClassesToEquivalentClass(args.get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    /// REMOVE ///////////////////////////////////////////////////

    static ArmorDirectiveRes removeIndividual(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                              ConnectedNode connectedNode) {
        // Remove individual from current OWLReference
        // <pre><code>args[ String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.removeIndividual(args.get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes removeClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                         ConnectedNode connectedNode) {
        // Remove class from current OWLReference
        // <pre><code>args[ String clsName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.removeClass(args.get(0));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes removeIndFromClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                ConnectedNode connectedNode) {
        // Remove an individual from the set of individual belonging to a class
        // <pre><code>args[ String indName, String clsName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.removeIndividualB2Class(args.get(0), args.get(1));
        // TODO: add warning no element to remove
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes removeSubclassFromClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                     ConnectedNode connectedNode) {
        // Remove a class from the subclasses set of a super-class
        // <pre><code>args[ String subclassName, String superclassName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.removeSubClassOf(args.get(1), args.get(0));
        // TODO: add warning no class to remove
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes removeDatapropFromInd(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                   ConnectedNode connectedNode) {
        // Remove a datapropery from an individual
        // <pre><code>args[ String propertyName, String indName, String valueType, String value ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        Object castedObj = valueTypeCasting(args.get(2), args.get(3));
        if (castedObj != null) {
            ontoRef.removeDataPropertyB2Individual
                    (args.get(1), args.get(0), castedObj);
            setResponse(request.getReferenceName(), true, 0, "", response);
        } else {
            // TODO: log
            String errorMessage = "Value type " + args.get(2) + " not supported. "
                    + args.get(0) + "not removed from " + args.get(1) + ".";
            connectedNode.getLog().error(errorMessage);
            setResponse(request.getReferenceName(), true, 101, errorMessage, response);
        }
        return response;
    }

    static ArmorDirectiveRes removeObjectpropFromInd(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                     ConnectedNode connectedNode) {
        // Remove an objectproperty from an individual
        // <pre><code>args[ String propertyName, String indName, String valueIndividual ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.removeObjectPropertyB2Individual(args.get(1), args.get(0), args.get(2));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    /// REPLACE //////////////////////////////////////////////////

    static ArmorDirectiveRes replaceDatapropValue(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                  ConnectedNode connectedNode) {
        // Replace the value of an individual data property
        // <pre><code>args[ String propertyName, String individualName,
        //       String valueType, String newValue, String oldValue ]</pre></code>
        // TODO: rework method replaceDataProperty to get String
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        Object castedObj = valueTypeCasting(args.get(2), args.get(4));
        if (castedObj != null) {
            ontoRef.replaceDataProperty(ontoRef.getOWLIndividual(args.get(1)),
                    ontoRef.getOWLDataProperty(args.get(0)),
                    ontoRef.getOWLLiteral(castedObj),
                    ontoRef.getOWLLiteral(valueTypeCasting(args.get(2), args.get(3))));
            setResponse(request.getReferenceName(), true, 0, "", response);
        } else {
            // TODO: log
            String errorMessage = "Value type " + args.get(2) + " not supported. "
                    + args.get(0) + "not replaced from " + args.get(1) + ".";
            connectedNode.getLog().error(errorMessage);
            setResponse(request.getReferenceName(), false, 101, errorMessage, response);
        }
        return response;
    }

    static ArmorDirectiveRes replaceObjectpropValue(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                    ConnectedNode connectedNode) {
        // Replace the value of an individual data property
        // <pre><code>args[ String propertyName, String individualName,
        //       String newValue, String oldValue ]</pre></code>
        // TODO: rework method replaceObjectProperty to get String
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.replaceObjectProperty(ontoRef.getOWLIndividual(args.get(1)),
                ontoRef.getOWLObjectProperty(args.get(0)),
                ontoRef.getOWLIndividual(args.get(3)),
                ontoRef.getOWLIndividual(args.get(2)));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    /// RENAME ///////////////////////////////////////////////////

    static ArmorDirectiveRes renameIndividual(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                              ConnectedNode connectedNode) {
        // Rename an individual
        // <pre><code>args[ String oldName, String newName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.renameEntity(ontoRef.getOWLIndividual(args.get(0)), args.get(1));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes renameClass(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                         ConnectedNode connectedNode) {
        // Rename a class
        // <pre><code>args[ String oldName, String newName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.renameEntity(ontoRef.getOWLClass(args.get(0)), args.get(1));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes renameDataprop(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                            ConnectedNode connectedNode) {
        // Rename a data property
        // <pre><code>args[ String oldName,, String newName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.renameEntity(ontoRef.getOWLDataProperty(args.get(0)), args.get(1));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    static ArmorDirectiveRes renameObjectprop(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                              ConnectedNode connectedNode) {
        // Rename an object property
        // <pre><code>args[ String oldName, String newName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        checkReferenceIsAvailable(request, response, connectedNode);
        List<String> args = request.getArgs();
        ontoRef.renameEntity(ontoRef.getOWLObjectProperty(args.get(0)), args.get(1));
        setResponse(request.getReferenceName(), true, 0, "", response);
        return response;
    }

    private static List<String> getStringListFromQuery(Set<?> queriedResults, OWLReferences ontoRef, Boolean fullIRIName){
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
}
