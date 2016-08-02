#Commands

We assume that both *client_id* and *reference_name* fields are always specified.
The former can be omitted for non-manipulations commands but for safety 
and debugging reasons, it is strongly advised to always specify it.

+ *Commands* and *directives* are **case-insensitive**. *Argument list* is **case sensitive**.
+ **UNDEFINED** commands are functions planned for the future but not yet implemented.
+ **Type** args can be any ontology supported type such as *'String'*, *'Integer'*, and more.
+ If you refer to an element that does no yet exist, it will be created 
(e.g. if you add an individual to a non-existing class, the class will be
created automatically)

##Manipulations
Commands that somehow change the state of an ontology reference are called
manipulation commands. If an identified client is mounted on a reference,
only such client will be allowed to run manipulation commands on such 
reference. Calls from clients with a different ID will fail and report an
error.

| Effect                          | Command     | 1s spec.       | 2nd spec.      | args[0]      | args[1]       | args[2]        | args[3]        | args[4]        |
|---------------------------------|:-----------:|:--------------:|:--------------:|:------------:|:-------------:|:--------------:|:--------------:|:--------------:|
| Add ind to T                    | **ADD**     | **IND**        |                | ind name     |               |                |                |                |
| Add ind to a class              | **ADD**     | **IND**        | **CLASS**      | ind name     | cls name      |                |                |                |
| Add sub-cls to T                | **ADD**     | **CLASS**      |                | cls name     |               |                |                |                |
| Add sub-cls to super-cls        | **ADD**     | **CLASS**      | **CLASS**      | sub-cls name | super-cls name|                |                |                |
| UNDEFINED                       | **ADD**     | **DATAPROP**   |                |              |               |                |                |                |
| Add data property to ind        | **ADD**     | **DATAPROP**   | **IND**        | prop name    | ind name      | type           | value          |                |
| UNDEFINED                       | **ADD**     | **DATAPROP**   | **DATAPROP**   |              |               |                |                |                |
| UNDEFINED                       | **ADD**     | **OBJECTPROP** |                |              |               |                |                |                |
| add obj property to ind         | **ADD**     | **OBJECTPROP** | **IND**        | prop name    | ind name      | ind-value name |                |                |
| UNDEFINED                       | **ADD**     | **OBJECTPROP** | **OBJECTPROP** |              |               |                |                |                |
| Makes two individuals disjoint  | **ADD**     | **DISJOINT**   | **IND**        | ind1 name    | ind2 name     |                |                |                |
| Makes two classes disjoint      | **ADD**     | **DISJOINT**   | **CLASS**      | cls1 name    | cls2 name     |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| Remove ind from T               | **REMOVE**  | **IND**        |                | ind name     |               |                |                |                |
| Remove ind from cls             | **REMOVE**  | **IND**        | **CLASS**      | ind name     | cls Name      |                |                |                |
| Remove cls from T               | **REMOVE**  | **CLASS**      |                | cls name     |               |                |                |                |
| remove sub-cls from super-cls   | **REMOVE**  | **CLASS**      | **CLASS**      | sub-cls name | super-cls Name|                |                |                |
| UNDEFINED                       | **REMOVE**  | **DATAPROP**   |                |              |               |                |                |                |
| Remove dataprop from ind        | **REMOVE**  | **DATAPROP**   | **IND**        | prop name    | ind name      | type           | value          |                |
| UNDEFINED                       | **REMOVE**  | **DATAPROP**   | **DATAPROP**   |              |               |                |                |                |
| UNDEFINED                       | **REMOVE**  | **OBJECTPROP** |                |              |               |                |                |                |
| Remove objectprop from ind      | **REMOVE**  | **OBJECTPROP** | **IND**        | prop name    | ind name      | ind-value name |                |                |
| UNDEFINED                       | **REMOVE**  | **OBJECTPROP** | **OBJECTPROP** |              |               |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| Replace ind dataprop            | **REPLACE** | **DATAPROP**   | **IND**        | prop name    | ind name      | type           | new-value      | old-value      |
| Replace ind objectprop          | **REPLACE** | **OBJECTPROP** | **IND**        | prop Name    | ind Name      | new-value Name | old-value Name |                |
|                                 |             |                |                |              |               |                |                |                |
| Rename ind                      | **RENAME**  | **IND**        |                | old-name     | new-name      |                |                |                |
| Rename cls                      | **RENAME**  | **CLASS**      |                | old-Name     | new-name      |                |                |                |
| Rename dataprop                 | **RENAME**  | **DATAPROP**   |                | old-Name     | new-name      |                |                |                |
| Rename objectprop               | **RENAME**  | **OBJECTPROP** |                | old-Name     | new-name      |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| applies buffered changes        | **APPLY**   |                |                |              |               |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| UNDEFINED                       | **SWRL**    |                |                |              |               |                |                |                |