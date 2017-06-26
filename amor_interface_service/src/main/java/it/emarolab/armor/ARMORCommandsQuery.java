package it.emarolab.armor;

import armor_msgs.ArmorDirectiveReq;
import armor_msgs.ArmorDirectiveRes;
import armor_msgs.QueryItem;
import it.emarolab.amor.owlInterface.*;
import org.apache.jena.query.QueryCancelledException;
import org.ros.node.ConnectedNode;
import org.semanticweb.owlapi.model.*;
import java.util.*;
import static it.emarolab.armor.ARMORCommandsUtils.*;

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
 * This class contains static methods to perform queries on an ontology.
 * </p>
 *
 * @version 2.0
 */

public class ARMORCommandsQuery {

    /////////////////       QUERY COMMANDS       /////////////////


    static ArmorDirectiveRes queryInd(ArmorDirectiveReq request, ArmorDirectiveRes response, Boolean fullIRIName) {
        // Checks an individual exists
        // <pre><code>args[ String ind_name ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<OWLClass> candidates = ontoRef.getIndividualClasses(request.getArgs().get(0));
        List<String> candidatesList = getStringListFromQuery(candidates, ontoRef, fullIRIName);
        if (candidates.size() > 0) {
            setResponse(request.getReferenceName(), true, 0, "", candidatesList, response);
        } else {
            setResponse(request.getReferenceName(), false, 0, "", candidatesList, response);
        }
        return response;
    }

    static ArmorDirectiveRes queryIndB2Class(ArmorDirectiveReq request, ArmorDirectiveRes response, Boolean fullIRIName) {
        // Queries all individuals belonging to a class.
        // <pre><code>args[ String className ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<OWLNamedIndividual> individuals = ontoRef.getIndividualB2Class(request.getArgs().get(0));
        List<String> individualsList = getStringListFromQuery(individuals, ontoRef, fullIRIName);

        setResponse(request.getReferenceName(), true, 0, "", individualsList, response);
        return response;
    }

    static ArmorDirectiveRes queryDatapropValuesB2Ind(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                      Boolean fullIRIName) {
        // Queries all data property values belonging to an individual
        // <pre><code>args[ String propertyName, String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<OWLLiteral> values = ontoRef.getDataPropertyB2Individual(request.getArgs().get(1), request.getArgs().get(0));
        List<String> valueList = getStringListFromQuery(values, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", valueList, response);
        return response;
    }

    static ArmorDirectiveRes queryObjectpropValuesB2Ind(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                        Boolean fullIRIName) {
        // Queries all object property arguments belonging to an individual
        // <pre><code>args[ String propertyName, String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        List<String> args = request.getArgs();
        Set<OWLNamedIndividual> indValues = ontoRef.getObjectPropertyB2Individual(args.get(1), args.get(0));
        List<String> valueObjectList = getStringListFromQuery(indValues, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", valueObjectList, response);
        return response;
    }

    static ArmorDirectiveRes queryIndDefiningClasses(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                     Boolean fullIRIName) {
        // Queries all classes an individual belongs to
        // Gets only the bottom class if onlyBottom is equal to "true"
        // <pre><code>args[ String indName, Boolean onlyBottom ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        List<String> classList = new ArrayList<>();
        if (!Boolean.valueOf(request.getArgs().get(1))) {
            Set<OWLClass> classes = ontoRef.getIndividualClasses(request.getArgs().get(0));
            classList = getStringListFromQuery(classes, ontoRef, fullIRIName);
        } else {
            classList.add(ontoRef.getOWLObjectName(ontoRef.getOnlyBottomType(request.getArgs().get(0))));
        }

        setResponse(request.getReferenceName(), true, 0, "", classList, response);
        return response;
    }

    static ArmorDirectiveRes querySubclasses(ArmorDirectiveReq request, ArmorDirectiveRes response, Boolean fullIRIName) {
        // Queries al subclasses of a class
        // <pre><code>args[ String superclassName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<OWLClass> subClasses = ontoRef.getSubClassOf(request.getArgs().get(0));
        List<String> subclassesList = getStringListFromQuery(subClasses, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", subclassesList, response);
        return response;
    }

    static ArmorDirectiveRes queryClassRestrictions(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                    Boolean fullIRIName) {
        // Return the set of restrictions for a given class
        // <pre><code>args[ String className]
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<SemanticRestriction.ApplyingRestriction> restrictions =
                ontoRef.getRestrictions(ontoRef.getOWLClass(request.getArgs().get(0)));
        List<String> restrictionsList = getStringListFromQuery(restrictions, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", restrictionsList, response);
        return response;
    }


    static ArmorDirectiveRes queryDatapropsB2Ind(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                 Boolean fullIRIName) {
        // Queries all data properties belonging to an individual
        // <pre><code>args[ String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<DataPropertyRelations> dataProps = ontoRef.getDataPropertyB2Individual(request.getArgs().get(0));
        Set<OWLDataProperty> dataObjects = new HashSet<>();
        for (DataPropertyRelations prop : dataProps) {
            dataObjects.add(prop.getProperty());
        }
        List<String> allDataList = getStringListFromQuery(dataObjects, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", allDataList, response);
        return response;
    }

    static ArmorDirectiveRes queryObjectpropsB2Ind(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                   Boolean fullIRIName) {
        // Queries all object properties belonging to an individual
        // <pre><code>args[ String indName ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        Set<ObjectPropertyRelations> objectProps
                = ontoRef.getObjectPropertyB2Individual(request.getArgs().get(0));
        Set<OWLObjectProperty> propObjects = new HashSet<>();
        for (ObjectPropertyRelations prop : objectProps) {
            propObjects.add(prop.getProperty());
        }
        List<String> allObjectlist = getStringListFromQuery(propObjects, ontoRef, fullIRIName);
        setResponse(request.getReferenceName(), true, 0, "", allObjectlist, response);
        return response;
    }

    static ArmorDirectiveRes querySPARQL(ArmorDirectiveReq request, ArmorDirectiveRes response, Boolean fullIRIName,
                                         ConnectedNode connectedNode) {
        // Executes a SPARQL query and returns the result as a string. Optionally, timeout can be set.
        // <pre><code>args[ String query, string timeout ]</pre></code>
        // OR args[ String prefix, string select, string where string timeout ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        List<String> args = request.getArgs();
        List<Map<String, String>> result = new ArrayList<>();
        Boolean timedout = false;
        try {
            if (args.size() == 1) {
                result = ontoRef.sparql2Msg(args.get(0), null);
            } else if (args.size() == 2) {
                result = ontoRef.sparql2Msg(args.get(0), Long.valueOf(args.get(1)));
            } else if (args.size() == 3) {
                result = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), null);
            } else if (args.size() == 4) {
                result = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), Long.valueOf(args.get(3)));
            }
        } catch (QueryCancelledException e) {
            timedout = true;
            connectedNode.getLog().error("SPARQL query timed out. %s", e);
            System.out.println("SPARQL query timed out.");
        }

        List<String> sparqlResponse = new ArrayList<>();
        sparqlResponse.add(result.toString().replace("[", "").replace("]", ""));
        setResponseSparql(request.getReferenceName(), true, 0, "",
                timedout, sparqlResponse, response);
        return response;
    }

    static ArmorDirectiveRes querySPARQLFormatted(ArmorDirectiveReq request, ArmorDirectiveRes response,
                                                  Boolean fullIRIName, ConnectedNode connectedNode) {
        // Executes a SPARQL query and returns the result as a string. Optionally, timeout can be set.
        // <pre><code>args[ String query, string timeout ]</pre></code>
        // OR args[ String prefix, string select, string where string timeout ]</pre></code>
        OWLReferences ontoRef = (OWLReferences)
                OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(request.getReferenceName());
        List<String> args = request.getArgs();
        List<Map<String, String>> result_f = new ArrayList<>();
        Boolean timedoutF = false;
        try {
            if (args.size() == 1) {
                result_f = ontoRef.sparql2Msg(args.get(0), null);
            } else if (args.size() == 2) {
                result_f = ontoRef.sparql2Msg(args.get(0), Long.valueOf(args.get(1)));
            } else if (args.size() == 3) {
                result_f = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), null);
            } else if (args.size() == 4) {
                result_f = ontoRef.sparql2Msg(args.get(0) + args.get(1) + args.get(2), Long.valueOf(args.get(3)));
            }
        } catch (QueryCancelledException e) {
            timedoutF = true;
            connectedNode.getLog().error("SPARQL query timed out. %s", e);
            System.out.println("SPARQL query timed out.");
        }

        List<QueryItem> items = response.getSparqlQueriedObjects();

        result_f.forEach(
                item -> item.forEach((k, v) -> {
                    QueryItem msgItem = connectedNode.getTopicMessageFactory().newFromType(QueryItem._TYPE);
                    msgItem.setKey(k);
                    msgItem.setValue(v);
                    items.add(msgItem);
                }));

        setResponseSparqlF(request.getReferenceName(), true, 0, "", timedoutF, items, response);
        return response;
    }
}
