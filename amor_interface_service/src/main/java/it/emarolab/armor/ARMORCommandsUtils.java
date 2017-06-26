package it.emarolab.armor;
import armor_msgs.ArmorDirectiveRes;
import armor_msgs.QueryItem;
import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.amor.owlInterface.OWLReferencesInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 * This class contains utility methods for the CommandsManipulation classes.
 * </p>
 *
 * @version 2.0
 */


public class ARMORCommandsUtils {
    ////////////////////  OTHER CLASS METHODS  ////////////////////

    static void setResponse(String referenceName, Boolean success, int exitCode, String errorMessage,
                            ArmorDirectiveRes response){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setQueriedObjects(new ArrayList<String>());
        response.setTimeout(false);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef =
                    (OWLReferences) OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef != null && ontoRef.getOWLReasoner() != null) {
                response.setIsConsistent(ontoRef.getOWLReasoner().isConsistent());
            }
        }
    }

    static void setResponse(String referenceName, Boolean success, int exitCode, String errorMessage,
                            List<String> queriedObjects, ArmorDirectiveRes response){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setQueriedObjects(queriedObjects);
        response.setTimeout(false);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef =
                    (OWLReferences) OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef != null && ontoRef.getOWLReasoner() != null) {
                response.setIsConsistent(ontoRef.getOWLReasoner().isConsistent());
            }
        }
    }

    static void setResponseSparql(String referenceName, Boolean success, int exitCode, String errorMessage,
                                  Boolean timeout, List<String> queriedObjects, ArmorDirectiveRes response){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setQueriedObjects(queriedObjects);
        response.setTimeout(timeout);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef =
                    (OWLReferences) OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef != null && ontoRef.getOWLReasoner() != null) {
                response.setIsConsistent(ontoRef.getOWLReasoner().isConsistent());
            }
        }
    }

    static void setResponseSparqlF(String referenceName, Boolean success, int exitCode, String errorMessage,
                                   Boolean timeout, List<QueryItem> queriedObjects, ArmorDirectiveRes response){
        response.setSuccess(success);
        response.setExitCode(exitCode);
        response.setErrorDescription(errorMessage);
        response.setSparqlQueriedObjects(queriedObjects);
        response.setTimeout(timeout);

        if (!referenceName.equals("")) {
            OWLReferences ontoRef =
                    (OWLReferences) OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(referenceName);
            if (ontoRef != null && ontoRef.getOWLReasoner() != null) {
                response.setIsConsistent(ontoRef.getOWLReasoner().isConsistent());
            }
        }
    }

    static List<String> getStringListFromQuery(Set<?> queriedResults, OWLReferences ontoRef, Boolean fullIRIName){
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


    static Object valueTypeCasting(String type, String value){
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
