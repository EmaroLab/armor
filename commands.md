# Commands

We assume that both *client_id* and *reference_name* fields are always specified.
The former can be omitted for non-manipulations commands but for safety 
and debugging reasons, it is strongly advised to always specify it.

Some quick remarks:

+ *Commands* and *directives* are **case-insensitive**. *Argument list* is **case sensitive**.
+ **UNDEFINED** commands are functions planned for the future but not yet implemented.
+ **Type** args can be any ontology supported type such as *'String'*, *'Integer'*, and more.
+ If you refer to an element that does no yet exist, it will be created 
(e.g. if you add an individual to a non-existing class, the class will be
created automatically).

**If you need a command that does not yet exists, please contact us or open an issue.**

Also, it is easy to create your own commands:
+ Add a method to perform the desired operation in one of the static classes ```ARMORCommandManipulation```, ```ARMORCommandQuery``` or ```ArmorCommandsUtility```.
+ Add the method and a unique id to the map in ```ARMORCommandExecutive``` 
(e.g., ```"SOME_RANDOM_COMMAND"```)

## Manipulations
Commands that somehow change the state of an ontology reference are called
manipulation commands. If an identified client is mounted on a reference,
only such client will be allowed to run manipulation commands on such 
reference. Calls from clients with a different ID will fail and report an
error.

| Effect                          | Command     | 1s spec.       | 2nd spec.      | args[0]      | args[1]       | args[2]        | args[3]        | args[4]    |
|---------------------------------|:-----------:|:--------------:|:--------------:|:------------:|:-------------:|:--------------:|:--------------:|:----------:|
| Add ind to T                    |   ADD       |   IND          | -              | ind name     | -             | -              | -              | -          |
| Add ind to cls                  |   ADD       |   IND          |   CLASS        | ind name     | cls name      | -              | -              | -          |
| Add sub-cls to T                |   ADD       |   CLASS        | -              | cls name     | -             | -              | -              | -          |
| Add sub-cls to cls              |   ADD       |   CLASS        |   CLASS        | sub-cls name | cls name      | -              | -              | -          |
| UNDEFINED                       |   ADD       |   DATAPROP     | -              | -            | -             | -              | -              | -          |
| Add data property to ind        |   ADD       |   DATAPROP     |   IND          | prop name    | ind name      | type           | value          | -          |
| UNDEFINED                       |   ADD       |   DATAPROP     |   DATAPROP     | -            | -             | -              | -              | -          |
| UNDEFINED                       |   ADD       |   OBJECTPROP   | -              | -            | -             | -              | -              | -          |
| Add obj property to ind         |   ADD       |   OBJECTPROP   |   IND          | prop name    | ind name      | value name     | -              | -          |
| UNDEFINED                       |   ADD       |   OBJECTPROP   |   OBJECTPROP   | -            | -             | -              | -              | -          |
| Add min cardinality to class    |   ADD       |   MIN          |   CARDINALITY  | class name   | property name | cardinality    | valueType      | -          |
|                                 |             |                |                |              |               |                |                |            |
| Makes all individuals disjoint  |  DISJOINT   |   IND          | -              | ind1 name    | ind2 name     | ...            | ...            | ...        |
| Makes all classes disjoint      |  DISJOINT   |   CLASS        | -              | cls1 name    | cls2 name     | ...            | ...            | ...        |
| Makes inds of a class disjoint  |  DISJOINT   |   IND          |   CLASS        | cls name     | -             | -              | -              | -          |
| Makes class' subclasses disjoint|  DISJOINT   |   CLASS        |   CLASS        | cls name     | -             | -              | -              | -          |
|                                 |             |                |                |              |               |                |                |            |
| Convert superclasses to def.    |    MAKE     |   EQUIVALENT   |   CLASS        | cls Name     | -             | -              | -              | -          |
|                                 |             |                |                |              |               |                |                |            |
| Remove ind from T               |   REMOVE    |   IND          | -              | ind name     | -             | -              | -              | -          |
| Remove ind from cls             |   REMOVE    |   IND          |   CLASS        | ind name     | cls Name      | -              | -              | -          |
| Remove cls from T               |   REMOVE    |   CLASS        | -              | cls name     | -             | -              | -              | -          |
| Remove sub-cls from super-cls   |   REMOVE    |   CLASS        |   CLASS        | sub-cls name | super-cls Name| -              | -              | -          |
| UNDEFINED                       |   REMOVE    |   DATAPROP     | -              | -            | -             | -              | -              | -          |
| Remove dataprop from ind        |   REMOVE    |   DATAPROP     |   IND          | prop name    | ind name      | type           | value          | -          |
| UNDEFINED                       |   REMOVE    |   DATAPROP     |   DATAPROP     | -            | -             | -              | -              | -          |
| UNDEFINED                       |   REMOVE    |   OBJECTPROP   | -              | -            | -             | -              | -              | -          |
| Remove objectprop from ind      |   REMOVE    |   OBJECTPROP   |   IND          | prop name    | ind name      | value name     | -              | -          |
| UNDEFINED                       |   REMOVE    |   OBJECTPROP   |   OBJECTPROP   | -            | -             | -              | -              | -          |
|                                 |             |                |                |              |               |                |                |            |
| Replace ind dataprop            |   REPLACE   |   DATAPROP     |   IND          | prop name    | ind name      | type           | new-value      | old-value  |
| Replace ind objectprop          |   REPLACE   |   OBJECTPROP   |   IND          | prop name    | ind name      | new-value name | old-value name | -          |
|                                 |             |                |                |              |               |                |                |            |
| Rename ind                      |   RENAME    |   IND          | -              | old-name     | new-name      | -              | -              | -          |
| Rename cls                      |   RENAME    |   CLASS        | -              | old-Name     | new-name      | -              | -              | -          |
| Rename dataprop                 |   RENAME    |   DATAPROP     | -              | old-Name     | new-name      | -              | -              | -          |
| Rename objectprop               |   RENAME    |   OBJECTPROP   | -              | old-Name     | new-name      | -              | -              | -          |
|                                 |             |                |                |              |               |                |                |            |
| Applies buffered changes        |   APPLY     | -              | -              | -            | -             | -              | -              | -          |
| Run buffered reasoner           |   REASON    | -              | -              | -            | -             | -              | -              | -          |
|                                 |             |                |                |              |               |                |                |            |
| UNDEFINED                       |   SWRL      | -              | -              | -            | -             | -              | -              | -          |

## Queries

The `QUERY` command and its specifications are used to query knowledge from
the ontology. Results are returned in the service response field  `queried_objects`.
They can be run freely by any client even if another client is already 
mounted on the reference.

**NOTE:** query operations can occupy the ARMOR server for relatively long
time. Hence, it is good practice not to run queries on ontologies while 
they are being used in performance sensitive processes. An alternative
and stricter version of the mount system may be implemented in future to
enforce this practice.

| `queried_objects`                                        | Command | 1st spec.  | 2nd spec.   | args[0]                         | args[1]                    |
|:--------------------------------------------------------:|:-------:|:----------:|:-----------:|:-------------------------------:|:--------------------------:|
| Check if an individual exists <sup>[1](#1)</sup>         | QUERY   | IND        | -           | ind name                        | -                          |
| All individuals belonging to a cls                       | QUERY   | IND        | CLASS       | cls name                        | -                          |
| UNDEFINED                                                | QUERY   | DATAPROP   | CLASS       | -                               | -                          |
| UNDEFINED                                                | QUERY   | OBJECTPROP | CLASS       | -                               | -                          |
| All values of a data property belonging to an individual | QUERY   | DATAPROP   | IND         | prop name                       | ind name                   |
| All values of a data property belonging to an individual | QUERY   | OBJECTPROP | IND         | prop name                       | ind name                   |
| All classes belonging to a super-class                   | QUERY   | CLASS      | CLASS       | super-cls name                  | -                          |
| All class restrictions <sup>[2](#2)</sup>                | QUERY   | CLASS      | RESTRICTIONS| cls name                        | -                          |
| UNDEFINED                                                | QUERY   | DATAPROP   | DATAPROP    | -                               | -                          |
| UNDEFINED                                                | QUERY   | OBJECTPROP | OBJECTPROP  | -                               | -                          |
| All data property belonging to an individual             | QUERY   | IND        | DATAPROP    | ind name                        | -                          |
| All object property belonging to an individual           | QUERY   | IND        | OBJECTPROP  | ind name                        | -                          |
| All classes an individual belongs to <sup>[3](#3)</sup>  | QUERY   | CLASS      | IND         | ind name                        | bottom  <sup>[3](#3)</sup> |
| Query using SPARQL syntax                                | QUERY   | SPARQL     | -           | sparql query <sup>[4](#4)</sup> | timeout <sup>[4](#4)</sup> |
| Query using SPARQL syntax                                | QUERY   | SPARQL     | FORMATTED   | sparql query <sup>[4](#4)</sup> | timeout <sup>[4](#4)</sup> |

## Utilities

These commands are used to load and save the ontology, toggle the logging
utilities and more.

| Effects                                     | Command | 1st spec. | 2nd spec. | args[0]  | args[1] | args[2]                      | args[3]                     | args[4]                    |
|:-------------------------------------------:|:-------:|:---------:|:---------:|:--------:|:-------:|:----------------------------:|:---------------------------:|:--------------------------:|
| UNDEFINED                                   | CREATE  | -         | -         | -        | -       | -                            | -                           | -                          |
|                                             |         |           |           |          |         |                              |                             |                            |
| Create OWLReferences                        | LOAD    | FILE      | -         | filepath | iri     | man. flag <sup>[5](#6)</sup> | reasoner <sup>[6](#6)</sup> | r. flag <sup>[7](#7)</sup> |
| Create OWLReferences                        | LOAD    | WEB       | -         | filepath | iri     | man. flag <sup>[5](#6)</sup> | reasoner <sup>[6](#6)</sup> | r. flag <sup>[7](#7)</sup> |
| Create OWLReferences                        | LOAD    | FILE      | MOUNTED   | filepath | iri     | man. flag <sup>[5](#6)</sup> | reasoner <sup>[6](#6)</sup> | r. flag <sup>[7](#7)</sup> |
| Create OWLReferences                        | LOAD    | WEB       | MOUNTED   | filepath | iri     | man. flag <sup>[5](#6)</sup> | reasoner <sup>[6](#6)</sup> | r. flag <sup>[7](#7)</sup> |
|                                             |         |           |           |          |         |                              |                             |                            |
| Save ontology on file                       | SAVE    | -         | -         | filepath | -       | -                            | -                           | -                          |
| Save ontology with inferences               | SAVE    | INFERENCE | -         | filepath | -       | -                            | -                           | -                          |
|                                             |         |           |           |          |         |                              |                             |                            |
| Mount client on ref. <sup>[8](#8)</sup>     | MOUNT   | -         | -         | -        | -       | -                            | -                           | -                          |
| Unmount client from ref. <sup>[8](#8)</sup> | UNMOUNT | -         | -         | -        | -       | -                            | -                           | -                          |
|                                             |         |           |           |          |         |                              |                             |                            |
| Log to file                                 | LOG     | FILE      | ON        | filepath | -       | -                            | -                           | -                          |
| Stop logging to file                        | LOG     | FILE      | OFF       | -        | -       | -                            | -                           | -                          |
| Log to screen                               | LOG     | SCREEN    | ON        | -        | -       | -                            | -                           | -                          |
| Stop logging to screen                      | LOG     | SCREEN    | OFF       | -        | -       | -                            | -                           | -                          |
| Gets the list of loaded references          | GET     | ALL       | REFS      | -        | -       | -                            | -                           | -                          |
| Gets the client id mounted on a reference   | GET     | REF       | CLIENT    | refName  | -       | -                            | -                           | -                          |

<a name="1">[1]</a>: Returns a list of candidates. You can check the size of the list to
check if an individual exists. Size equal to 0 means the individual does 
not exist.

<a name="2">[2]</a>: Returns a list of strings. Each entry is a restriction such as *forall* 
or *exists* axioms. 

<a name="3">[3]</a>: Returns all classes an individual belongs to, unless the second argument is *"true"*. 
In which case it returns only the bottom concept. If not specified, it is assumed false.

<a name="4">[4]</a>: Timeout is optional. It will return all solutions found
until the query times out. SPARQL query can also be expressed as separate
 PREFIX, SELECT and WHERE query sections. Takes from 1 to 4 arguments depending on the case. 
 `QUERY_SPARQL` returns the queried objects in the `queried_objects` field
 while `QUERY_SPARQL_FORMATTED` returns it in `sparql_queried_objects`. 

<a name="5">[5]</a>: **Manipulation flag:** if true, all manipulations on the reference
will be buffered instead of being executed immediately. You can apply all
buffered manipulations running the `APPLY` command.

<a name="6">[6]</a>: **Reasoner:** "HERMIT", "PELLET", "FACT", "SNOROCKET". Case insensitive.
If you add a new reasoner to AMOR, you can call it by just writing the name.
It will fail if a non-defined reasoner is called.

<a name="7">[7]</a>: **Reasoner flag:** defines if reasoning should be buffered or not.
If it is, you have to run `REASON` command to run the reasoner.

<a name="8">[8]</a>: **Mount/Unmount:** Each client to the ARMOR service should identify
itself though the request `client_name` field. `MOUNT` assigns a client 
id to a reference, and only clients identifying themselves with such id
can run manipulation commands on such reference. This distinction between
`client` and `client_name` is useful when many nodes cooperates toward 
the same goal (e.g. many perception nodes may need to write on the ontology
at the same time). If you want to be sure a node has exclusive access to 
a reference, try assigning a unique ID such as its UID.
Note that if no client is mounted on a reference, then any client can 
manipulate such reference.
