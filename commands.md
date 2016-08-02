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

| <sub>Effect                          | Command     | 1s spec.       | 2nd spec.      | args[0]      | args[1]       | args[2]        | args[3]        | args[4]        |
|---------------------------------|:-----------:|:--------------:|:--------------:|:------------:|:-------------:|:--------------:|:--------------:|:--------------:|
| <sub>Add ind to T                   </sub> | **ADD**     | **IND**        |                | <sub>ind name    </sub> |                    |                |                |                |
| <sub>Add ind to a class             </sub> | **ADD**     | **IND**        | **CLASS**      | <sub>ind name    </sub> | <sub>cls name     </sub> |                |                |                |
| <sub>Add sub-cls to T               </sub> | **ADD**     | **CLASS**      |                | <sub>cls name    </sub> |                    |                |                |                |
| <sub>Add sub-cls to super-cls       </sub> | **ADD**     | **CLASS**      | **CLASS**      | <sub>sub-cls name</sub> | <sub>super-cls name </sub>|                |                |                |
| <sub>UNDEFINED                      </sub> | **ADD**     | **DATAPROP**   |                |                   |                    |                |                |                |
| <sub>Add data property to ind       </sub> | **ADD**     | **DATAPROP**   | **IND**        | <sub>prop name   </sub> | <sub>ind name     </sub> | <sub>type     </sub> | <sub>value    </sub> |                |
| <sub>UNDEFINED                      </sub> | **ADD**    </sub> | **DATAPROP**   | **DATAPROP**   |                   |                    |                |                |                |
| <sub>UNDEFINED                      </sub> | **ADD**     | **OBJECTPROP** |                |                   |                    |                |                |                |
| <sub>add obj property to ind        </sub> | **ADD**     | **OBJECTPROP** | **IND**        | <sub>prop name   </sub> | <sub>ind name     </sub> | <sub>ind-value name</sub> |                |                |
| <sub>UNDEFINED                      </sub> | **ADD**     | **OBJECTPROP** | **OBJECTPROP** |              |               |                |                |                |
| <sub>Makes two individuals disjoint </sub> | **ADD**     | **DISJOINT**   | **IND**        | <sub>ind1 name   </sub> | <sub>ind2 name    </sub> |                |                |                |
| <sub>Makes two classes disjoint     </sub> | **ADD**     | **DISJOINT**   | **CLASS**      | <sub>cls1 name   </sub> | <sub>cls2 name    </sub> |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| <sub>Remove ind from T              </sub> | **REMOVE**  | **IND**        |                | <sub>ind name    </sub> |               |                |                |                |
| <sub>Remove ind from cls            </sub> | **REMOVE**  | **IND**        | **CLASS**      | <sub>ind name    </sub> | <sub>cls Name     </sub> |                |                |                |
| <sub>Remove cls from T              </sub> | **REMOVE**  | **CLASS**      |                | <sub>cls name    </sub> |               |                |                |                |
| <sub>remove sub-cls from super-cls  </sub> | **REMOVE**  | **CLASS**      | **CLASS**      | <sub>sub-cls name</sub> | <sub>super-cls Name|               </sub> |                |                |
| <sub>UNDEFINED                      </sub> | **REMOVE**  | **DATAPROP**   |                |              |               |                |                |                |
| <sub>Remove dataprop from ind       </sub> | **REMOVE**  | **DATAPROP**   | **IND**        | <sub>prop name   </sub> | <sub>ind name     </sub> | <sub>type          </sub> | <sub>value         </sub> |                |
| <sub>UNDEFINED                      </sub> | **REMOVE**  | **DATAPROP**   | **DATAPROP**   |              |               |                |                |                |
| <sub>UNDEFINED                      </sub> | **REMOVE**  | **OBJECTPROP** |                |              |               |                |                |                |
| <sub>Remove objectprop from ind     </sub> | **REMOVE**  | **OBJECTPROP** | **IND**        | <sub>prop name   </sub> | <sub>ind name     </sub> | <sub>ind-value name</sub> |                |                |
| <sub>UNDEFINED                      </sub> | **REMOVE**  | **OBJECTPROP** | **OBJECTPROP** |              |               |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| <sub>Replace ind dataprop           </sub> | **REPLACE** | **DATAPROP**   | **IND**        | <sub>prop name   </sub> | <sub>ind name     </sub> | <sub>type          </sub> | <sub>new-value     </sub> | <sub>old-value     </sub> |
| <sub>Replace ind objectprop         </sub> | **REPLACE** | **OBJECTPROP** | **IND**        | <sub>prop Name   </sub> | <sub>ind Name     </sub> | <sub>new-value Name</sub> | <sub>old-value Name</sub> |                |
|                                 |             |                |                |              |               |                |                |                |
| <sub>Rename ind                     </sub> | **RENAME**  | **IND**        |                | <sub>old-name    </sub> | <sub>new-name     </sub> |                |                |                |
| <sub>Rename cls                     </sub> | **RENAME**  | **CLASS**      |                | <sub>old-Name    </sub> | <sub>new-name     </sub> |                |                |                |
| <sub>Rename dataprop                </sub> | **RENAME**  | **DATAPROP**   |                | <sub>old-Name    </sub> | <sub>new-name     </sub> |                |                |                |
| <sub>Rename objectprop              </sub> | **RENAME**  | **OBJECTPROP** |                | <sub>old-Name    </sub> | <sub>new-name     </sub> |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| <sub>Applies buffered changes       </sub> | **APPLY**   |                |                |              |               |                |                |                |
|                                 |             |                |                |              |               |                |                |                |
| <sub>UNDEFINED                      </sub> | **SWRL**    |                |                |              |               |                |                |                |
