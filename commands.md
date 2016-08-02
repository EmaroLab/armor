#Commands

We assume that both *client_id* and *reference_name* fields are always specified.
The former can be omitted for non-manipulations commands but for safety 
and debugging reasons, it is strongly advised to always specify it.

*Commands* and *directives* are **case-insensitive**. *Argument list* is **case sensitive**.

**UNDEFINED** commands are functions planned for the future but not yet implemented.

**Type** args can be any ontology supported type such as *'String'*, *'Integer'*, and more.

##Manipulations
Commands that somehow change the state of an ontology reference are called
manipulation commands. If an identified client is mounted on a reference,
only such client will be allowed to run manipulation commands on such 
reference. Calls from clients with a different ID will fail and report an
error.

| Effect                          | Command | 1s spec.   | 2nd spec.  | args[0]      | args[1]       | args[2]        | args[3]        | args[4]        |
|---------------------------------|:-------:|:----------:|:----------:|:------------:|:-------------:|:--------------:|:--------------:|:--------------:|                                                  |         |            |            |              |               |                |                |                |
| Add ind to T                    | ADD     | IND        | ---        | ind name     |               |                |                |                |
| Add ind to a class              | ADD     | IND        | CLASS      | ind name     | cls name      |                |                |                |
| Add sub-cls to T                | ADD     | CLASS      | ---        | cls name     |               |                |                |                |
| Add sub-cls to super-cls        | ADD     | CLASS      | CLASS      | sub-cls name | super-cls name|                |                |                |
| UNDEFINED                       | ADD     | DATAPROP   | ---        |              |               |                |                |                |
| Add data property to ind        | ADD     | DATAPROP   | IND        | prop name    | ind name      | type           | value          |                |
| UNDEFINED                       | ADD     | DATAPROP   | DATAPROP   |              |               |                |                |                |
| UNDEFINED                       | ADD     | OBJECTPROP | ---        |              |               |                |                |                |
| add obj property to ind         | ADD     | OBJECTPROP | IND        | prop name    | ind name      | ind-value name |                |                |
| UNDEFINED                       | ADD     | OBJECTPROP | OBJECTPROP |              |               |                |                |                |
| Makes two individuals disjoint  | ADD     | DISJOINT   | IND        | ind1 name    | ind2 name     |                |                |                |
| Makes two classes disjoint      | ADD     | DISJOINT   | CLASS      | cls1 name    | cls2 name     |                |                |                |
|---------------------------------|:-------:|:----------:|:----------:|:------------:|:-------------:|:--------------:|:--------------:|:--------------:|
| Ind !€ OWLThings                                 | REMOVE  | IND        | ---        | ind Name     |               |                |                |                |
| Ind !€ cls                                       | REMOVE  | IND        | CLASS      | ind Name     | Cls Name      |                |                |                |
| Cls !€ OWLThings                                 | REMOVE  | CLASS      | ---        | cls Name     |               |                |                |                |
| Sub-Cls !€ super-cls                             | REMOVE  | CLASS      | CLASS      | Sub-cls Name | superCls Name |                |                |                |
|                                                  | REMOVE  | DATAPROP   | ---        |              |               |                |                |                |
| ind.!prop( value:type)                           | REMOVE  | DATAPROP   | IND        | prop Name    | ind Name      | type Name      | value Name     |                |
|                                                  | REMOVE  | DATAPROP   | DATAPROP   |              |               |                |                |                |
|                                                  | REMOVE  | OBJECTPROP | ---        |              |               |                |                |                |
| Ind.!prop( indValue)                             | REMOVE  | OBJECTPROP | IND        | prop Name    | ind Name      | indValue Name  |                |                |
|                                                  | REMOVE  | OBJECTPROP | OBJECTPROP |              |               |                |                |                |
|                                                  |         |            |            |              |               |                |                |                |
| ind.prop( new_value:type)                        | REPLACE | DATAPROP   | IND        | prop Name    | ind Name      | type Name      | New-value Name | Old-value Name |
| ind.prop( new_value:type)                        | REPLACE | OBJECTPROP | IND        | prop Name    | ind Name      | New-value Name | Old-value Name |                |
|                                                  |         |            |            |              |               |                |                |                |
| !old-Name                                        | RENAME  | IND        | --         | Old-Name     | New-Name      |                |                |                |
| !old-Name                                        | RENAME  | CLASS      | --         | Old-Name     | New-Name      |                |                |                |
| !old-Name                                        | RENAME  | DATAPROP   | --         | Old-Name     | New-Name      |                |                |                |
| !old-Name                                        | RENAME  | OBJECTPROP | --         | Old-Name     | New-Name      |                |                |                |
|                                                  |         |            |            |              |               |                |                |                |
| applies buffered changes          | APPLY   | --         | --         |              |               |                |                |                |
|                                                  |         |            |            |              |               |                |                |                |
|                                                  | SWRL    | ??         | ??         |              |               |                |                |                |
