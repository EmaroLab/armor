# ARMOR
### A ROS Multi-Ontology Reference

**ARMOR** is a powerful and versatile management system for single and multi-ontology architectures under ROS. It allows
to load, query and modify multiple ontologies and requires very little knowledge of OWL APIs and Java. Despite its ease 
of use, ARMOR provides a large share of OWL APIs functions and capabilities in a simple server-client architecture and
offers increased flexibility compared to previous ontological systems running under ROS.

ARMOR's main goal is to ease the use of ontologies in robotics by bringing these features to developers working under ROS.
Not everyone in the ROS community is a Java developer, it is even rarer to find someone familiar with OWL APIs. 
ARMOR hides such implementation details and speeds up the development of applications based on semantic knowledge.
Notable features include:
  
  + Support for multiple ontologies
  + Ensures thread-safe ontology manipulations
  + Implements a wide range of functions: from simple "add an individual" to more esoteric "modify an SWRL rule"
  + Excellent performances which can be further customized based on user needs
  + Ontology debug GUI available (currently working but not stable)
  + Extensive logging
  + Out-of-the-box support for most reasoners, easy to add more
  + Based on latest standards: Java 8 and OWL API 5
  + Support for SPARQL queries (Only with Pellet/Openllet)
 
ARMOR is based on **AMOR** - a Multi-Ontology Reference library (a ROS ready version is available [here](https://github.com/EmaroLab/multi_ontology_reference)), offering a service-client interface for the vast majority of its functions, with some exceptions of rarer use. As a rule of thumb, as long as you create your own ontology offline and use ARMOR at runtime to populate, modify and query it; you should be good to go.

ARMOR expands AMOR capabilities in the following ways:
  
  + **_Flexible mount system_**, allows one or more ROS clients to identify themselves and mount on an ontology reference to
   prevent other processes from modifying its state. Excluded processes can still query the locked ontologies to avoid 
   waiting for long times.
  + **_Easy debugging_**, allows online toggling of terminal/file logging as well as turn on AMOR debug GUI.
  
Legacy support for OWL api 3.4.5 is available on the [OWL-api3 branch](https://github.com/EmaroLab/armor/tree/OWL-api3). Use it in conjunction with [AMOR OWL-api3 branch](https://github.com/EmaroLab/multi_ontology_reference/tree/OWL-api3).

## Installation and dependencies

The latest stable release of this project and its direct dependencies (ARMOR, AMOR and the required ROS messages) can be downloaded from this [**repository**](https://github.com/EmaroLab/ros_multi_ontology_references.git).

Else, you can manually download the latest release of each module from the following repositories:

+ [**ARMOR**](https://github.com/EmaroLab/armor)
+ [**AMOR**](https://github.com/EmaroLab/multi_ontology_reference)
+ [**armor_msgs**](https://github.com/EmaroLab/armor_msgs)

To build both AMOR and ARMOR, you will need [**Rosjava**](http://wiki.ros.org/rosjava) installed.

## Running ARMOR
Launching rosjava nodes can be a convoluted operation. To make things easier, after building the package for the first time go to the project directory and run the following command:

`./gradlew deployApp`

This is necessary only the first time you build the package or after you hard clean your workspace. You can then launch ARMOR from the command line:

`rosrun armor execute it.emarolab.armor.ARMORMainService`

Or from a launchfile:

`<node pkg="armor" type="execute" name="armor_service" args="it.emarolab.armor.ARMORMainService"/>`
  
## Client
  
  Once ARMOR is running, you can call it fom any client node written in your preferred language (C++, Python or Java). As an example, you can have a look at [armor_py](https://github.com/EmaroLab/armor_py_api), a client library written in Python.
  The library currently implements only a small number of functions, but they are stable and more will be added as they are needed.
  
  If you feel brave, you are comfortable with Java and you need some advanced feature and increased control over your 
  system, consider to incorporate [AMOR](https://github.com/EmaroLab/multi_ontology_reference) in your code instead
  of using ARMOR.
  
## Rosparams

| Rosparam name            | Type   | Function                                             | Default   |
|:------------------------:|:------:|:----------------------------------------------------:|:---------:|
| `FULL_ENTITY_IDENTIFIER` | `Bool` | If **True**, queries returns entities with full IRI. | **False** |
|        `SHOW_GUI`        | `Bool` | If **True**, it shows AMOR's debug GUI.              | **False** |
  
## Message structure

Use `/armor_interface_srv` for single calls:  
  
```
rosservice call /armor_interface_srv "armor_request:
  client_name: ''
  reference_name: ''
  command: ''
  primary_command_spec: ''
  secondary_command_spec: ''
  args: ['']"  
```

Use `/armor_interface_serialized_srv` to execute multiple operations:

```
rosservice call /armor_interface_serialized_srv "armor_requests:
   - client_name: ''
     reference_name: ''
     command: ''
     primary_command_spec: ''
     secondary_command_spec: ''
     args: ['']"
```

### Request

`ArmorDirective.srv` request depends only on `ArmorDirectiveReq.msg`, whose fields are:

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| *client_name*            | *optional*, node id                                             |
| *reference_name*         | name of the ontology to work on                                 |
| *command*                | command to execute (e.g. ADD, LOAD ...)                         |
| *primary_command_spec*   | *optional*, primary command specification (e.g. IND, FILE, ...) |
| *secondary_command_spec* | *optional*, secondary command specification                     |
| *args*                   | *optional*, list of arguments (e.g. list of individuals to add) |

`ArmorDirective.srv` request depends on a list of `ArmorDirectiveReq.msg`.
  
The complete list of **commands** and **directives** can be found here: 

[**COMMANDS DOCUMENTATION**](https://github.com/EmaroLab/armor/blob/master/commands.md)

### Response

`ArmorDirective.srv` request depends only on `ArmorDirectiveRes.msg`, whose fields are:

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| *success*                | *bool*                                                          |
| *exit_code*              | *int32*                                                         |
| *error_description*      | *string*                                                        |
| *is_consistent*          | *optional*, *bool*, `true` if reference is consistent           |
| *timeout*                | *optional*, *bool*, `true` if SPARQL query timed out            |
| *queried_objects*        | *optional*, *list string*, returns list of queried objects      |
| *sparql_queried_objects* | *optional*, *list QueryItem* (key-value couples)                |

`ArmorDirective.srv` request depends on a list of `ArmorDirectiveRes.msg` plus the following fields.

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| *success*                | *bool*, false if at least one command fails                     |
| *is_consistent*          | *bool*, false if at the end the ontology is inconsistent        |                                                      |

**NOTE:** *sparql_queried_objects* is used only in `QUERY_SPARQL_FORMATTED` calls.

### Error codes

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| **0**                    | *Success*                                                       |
| **1XX**                  | *Warning*                                                       |
| **101**                  | *Unsupported data type*                                         |
| **102**                  | *Reference busy*                                                |
| **2XX**                  | *Error*                                                         |
| **201**                  | *Malformed request, unspecified reference name*                 |
| **202**                  | *Malformed request, unspecified command*                        |
| **203**                  | *Cannot mount client on specified reference*                    |
| **204**                  | *Debug only, command defined but not implemented*               |
| **205**                  | *Non-existing command/specification*                            |


## Publication
More details at:
- [AROW ROMAN 2017](https://www.researchgate.net/publication/318107154_A_ROS_multi-ontology_references_services_OWL_reasoners_and_application_prototyping_issues)
- [and related presentation](https://www.researchgate.net/publication/319483418_A_ROS_Multi-Ontology_References_Service_OWL_Reasoners_and_Application_Prototyping_Issues)

## Contacts
For comment, discussions or support refer to this git repository or contact us at:
 - [alessio.capitanelli@dibris.unige.it](mailto:alessio.capitanelli@dibris.unige.it),
 - [luca.buoncompagni@edu.unige.it](mailto:luca.buoncompagni@edu.unige.it).
