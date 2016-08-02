#Commands

We assume that both *client_id* and *reference_name* fields are always specified.
The former can be omitted for non-manipulations commands but for safety 
and debugging reasons, it is strongly advised to always specify it.

*Commands* and *directives* are **case-insensitive**. *Argument list* is **case sensitive**.

##Manipulations
Commands that somehow change the state of an ontology reference are called
manipulation commands. If an identified client is mounted on a reference,
only such client will be allowed to run manipulation commands on such 
reference. Calls from clients with a different ID will fail and report an
error.

| Effect | Command | 1st spec. | 2nd spec. | arg[0] | arg[1] | arg[2] | arg[3] | arg[4] |
| :----: | :-----: | :-------: | :-------: | :----: | :----: | :----: | :----: | :----: |
| Add ind to T | **ADD** | **IND**   | --- | *ind name* | - | - | - | - |
| Add ind to a class | **ADD** | **IND**   | **CLASS** | *ind name* | *class name* | - | - | - |
| Add class to T | **ADD** | **CLASS** | --- | *class name* | - | - | - | - |
| Add subclass to superclass | **ADD** | **CLASS** | **CLASS** | *subclass name* | *superclass name* | - | - | - |
| UNDEFINED IN ARMOR | **ADD** | **DATAPROP** | - | - | - | - |
| Add dataprop to ind | **ADD** | **DATAPROP** | **IND** | *prop name* | *ind name * | *type* | *value* | 