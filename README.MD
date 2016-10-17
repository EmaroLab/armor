#ARMOR
###A ROS Multi-Ontology Reference

**ARMOR** is a powerful and versatile management system for single and multi-ontology architectures under ROS. It allows
to load, query and modify multiple ontologies and requires very little knowledge of OWL APIs and Java. Despite its ease 
of use, ARMOR provides a large share of OWL APIs functions and capabilities in a simple server-client architecture and
offers increased flexibility compared to previous ontological systems running under ROS.

ARMOR is based on **AMOR** - a Multi-Ontology Reference library ([here](https://github.com/EmaroLab/multi_ontology_reference) the link to the ROS library version used by ARMOR).
AMOR is a complete library of helper functions for OWL ontologies with some remarkable features.
  
  + Support for multiple ontologies.
  + Ensures thread-safe ontology manipulations.
  + Implements a wide range of functions: from simple "add an individual" to more esoteric "modify an SWRL rule".
  + Excellent performances which can be further customized based on user needs.
  + Ontology debug GUI available (currently working but not stable).
  + Extensive logging.
  + Out-of-the-box support for most reasoners. Easy to add more.
  + Based on latest standards: Java 8 and OWL API 5.
  + Support for SPARQL queries. 
  
ARMOR's main goal is to ease the use of ontologies in robotics by bringing these features to developers working with ROS.
Not everyone in the ROS community is a Java developer, even more rarely someone used the low-level OWL API. 
As a consequence, we decided to hide this complexity inside of ARMOR. 
  
ARMOR offers a service-client interface for the vast majority of AMOR functions, with some notable exceptions of rare 
use. As a rule of thumb, as long as you create your own ontological architecture offline and use ARMOR at runtime to 
populate, modify and query it, you should be good to go.

It adds the following features on top of AMOR:
  
  + **_Flexible mount system_**, allows one or more ROS clients to identify themselves and mount on an ontology reference to
   prevent other processes from modifying its state. Excluded processes can still query the locked ontologies to avoid 
   waiting for long times.
  + **_Easy debugging_**, allows online toggling of terminal/file logging as well as turn on AMOR debug GUI.
  
Legacy support for OWL api 3.4.5 is available on the [OWL-api3 branch](https://github.com/EmaroLab/armor/tree/OWL-api3). Use it in conjunction with [AMOR OWL-api3 branch](https://github.com/EmaroLab/multi_ontology_reference/tree/OWL-api3).

##Running ARMOR
Launching rosjava nodes can be a convoluted operation. To make things easier, after building the package for the first time go to the project directory and run the following command:

`./gradlew deployApp`

This is necessary only the first time you build the package or after you hard clean your workspace. You can then launch ARMOR from the command line:

`rosrun armor execute it.emarolab.armor.ARMORMainService`

Or from a launchfile:

`<node pkg="armor" type="execute" name="armor_service" args="it.emarolab.armor.ARMORMainService"/>`
  
##Dependencies
  
  + [Rosjava](http://wiki.ros.org/rosjava) - to natively run Java code (i.e. *OWL API*) inside a ROS architecture.
  + [AMOR](https://github.com/EmaroLab/multi_ontology_reference) - includes OWL APIs, used for manipulations and querying.
  + [armor_msgs](https://github.com/EmaroLab/armor_msgs) - messages used by ARMOR.
  
##Client
  
  Once ARMOR is running, you can call it fom any client node written in your preferred language (C++, Python or Java if 
  you have Rosjava installed). As an example, you can have a look at [armor_py](https://github.com/EmaroLab/armor_py_api), a Python library to easily modify and
  query an ontology from a Python script. Of course, if you are working under Python you can use *armor_py* itself. 
  The library is still work in progress but it should expand quickly as more functions are needed.
  
  If you feel brave, you are comfortable with Java and you need some advanced feature and increased control over your 
  system, consider to incorporate [**AMOR**](https://github.com/EmaroLab/multi_ontology_reference) in your code instead
  of using ARMOR.
  
##Rosparams

| Rosparam name            | Type   | Function                                             | Default   |
|:------------------------:|:------:|:----------------------------------------------------:|:---------:|
| `FULL_ENTITY_IDENTIFIER` | `Bool` | If **True**, queries returns entities with full IRI. | **False** |
  
##Message structure
  
```
rosservice call /armor_interface_srv "client_name: ''
reference_name: ''
command: ''
primary_command_spec: ''
secondary_command_spec: ''
args: ['']"   
```

###Request

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| *client_name*            | *optional*, node id                                             |
| *reference_name*         | name of the ontology to work on                                 |
| *command*                | command to execute (e.g. ADD, LOAD ...)                         |
| *primary_command_spec*   | *optional*, primary command specification (e.g. IND, FILE, ...) |
| *secondary_command_spec* | *optional*, secondary command specification                     |
| *args*                   | *optional*, list of arguments (e.g. list of individuals to add) |
  
The complete list of **commands** and **directives** can be found [here](https://github.com/EmaroLab/armor/blob/master/commands.md).

###Response

| Field                    | arg                                                             |
| ------------------------ | --------------------------------------------------------------- |
| *success*                | *bool*                                                          |
| *exit_code*              | *int32*                                                         |
| *error_description*      | *string*                                                        |
| *is_consistent*          | *optional*, *bool*, returns reference consistency               |
| *queried_objects*        | *optional*, *list string*, returns list of queried objects      |
| *sparql_queried_objects* | *optional*, *list QueryItem* (key-value couples)                |

###Error codes

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
