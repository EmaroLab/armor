package it.emarolab.armor;

import it.emarolab.amor.owlDebugger.OFGUI.GuiRunner;
import armor_msgs.*;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;

import java.util.ArrayList;
import java.util.List;


public class ARMORMainService extends AbstractNodeMain {

    private final static Boolean DEFAULT_FULL_ENTITY_IDENTIFIER = false;
    private final static Boolean DEFAULT_SHOW_GUI = false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/perception2owl");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        ParameterTree params = connectedNode.getParameterTree();

        final Boolean SHOW_GUI = params.getBoolean("/armor/settings/show_gui", DEFAULT_SHOW_GUI);
        final Boolean FULL_ENTITY_IDENTIFIER =
                params.getBoolean("/armor/settings/full_entity_iri", DEFAULT_FULL_ENTITY_IDENTIFIER);

        if (SHOW_GUI){
            connectedNode.getLog().info("Staring GUI.");
            new Thread(new GuiRunner()).start();
            connectedNode.getLog().info("GUI started.");
        }

        ARMORResourceManager.setLogging(connectedNode);

        // Callback for ArmorDirective.srv calls (single operation)

        ServiceServer<ArmorDirectiveRequest, ArmorDirectiveResponse> armorCallback =
                connectedNode.newServiceServer("armor_interface_srv", ArmorDirective._TYPE,
                        (request, response) -> {
                            ARMORCommandExecutive command = new ARMORCommandExecutive(
                                    request.getArmorRequest(), response.getArmorResponse(),
                                    FULL_ENTITY_IDENTIFIER, connectedNode);
                            if (command.getServiceResponse().getSuccess()) {
                                response.setArmorResponse(command.executeCommand());
                            }else{
                                response.setArmorResponse(command.getServiceResponse());   // catch invalid command
                            }
                        });

        // Callback for ArmorDirectiveList.srv (multiple operations)

        ServiceServer<ArmorDirectiveListRequest, ArmorDirectiveListResponse> armorCallbackSerial =
                connectedNode.newServiceServer("armor_interface_serialized_srv", ArmorDirectiveList._TYPE,
                        (request, response) -> {
                            Boolean success = true;
                            Boolean isConsistent = true;
                            List<ArmorDirectiveRes> results = new ArrayList<ArmorDirectiveRes>();

                            // create empty response to be filled by ARMORCommandExecutive
                            NodeConfiguration nodeConf = NodeConfiguration.newPrivate();
                            MessageFactory msgFactory = nodeConf.getTopicMessageFactory();
                            ArmorDirectiveRes result = msgFactory.newFromType(ArmorDirectiveRes._TYPE);

                            for (int i = 0; i < request.getArmorRequests().size(); i++) {
                                ARMORCommandExecutive command = new ARMORCommandExecutive(
                                        request.getArmorRequests().get(i),
                                        result,
                                        FULL_ENTITY_IDENTIFIER, connectedNode);
                                if (!command.getServiceResponse().getSuccess()) {
                                    results.add(command.executeCommand());
                                } else {
                                    results.add(command.getServiceResponse());  // catch invalid command
                                }
                                isConsistent = command.getServiceResponse().getIsConsistent();
                                if (!command.getServiceResponse().getIsConsistent() && success) success = false;
                            }
                            response.setArmorResponses(results);
                            response.setIsConsistent(isConsistent);
                            response.setSuccess(success);
                        });
    }

//     For testing and debugging purposes only
//     You can use this main as entry point in an IDE (e.g., IDEA) to run a debugger

    public static void main(String argv[]) throws java.io.IOException {

        String[] args = { "it.emarolab.armor.ARMORMainService" };
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(args));
        NodeConfiguration nodeConfiguration = loader.build();
        ARMORMainService service = new ARMORMainService();

        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(service, nodeConfiguration);
    }
}