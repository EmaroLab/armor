package it.emarolab.armor;

import it.emarolab.amor.owlDebugger.OFGUI.GuiRunner;
import armor_msgs.*;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;


public class ARMORMainService extends AbstractNodeMain {

    public final static String DEFAULT_LOG_SAVING_PATH = ""; // TODO
    public final static Boolean DEFAULT_FULL_ENTITY_IDENTIFIER = false;
    public final static Boolean DEFAULT_SHOW_GUI = false;
    public final static Boolean DEFAULT_LOG_REFERENCES_CONTAINER = false;
    public final static Boolean DEFAULT_LOG_REFERENCES_INTERFACE = false;
    public final static Boolean DEFAULT_LOG_OWL_LIBRARY = false;
    public final static Boolean DEFAULT_LOG_REASONER_MONITOR = false;
    public final static Boolean DEFAULT_LOG_REASONER_EXPLANATION = false;
    public final static Boolean DEFAULT_LOG_OWL_MANIPULATOR = false;
    public final static Boolean DEFAULT_LOG_ONTOLOGY_REFERENCE = false;
    public final static Boolean DEFAULT_LOG_OWL_ENQUIRER = false;
    public final static Boolean DEFAULT_LOG_ONTOLOGY_EXPORTER = false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/perception2owl");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        ParameterTree params = connectedNode.getParameterTree();

        final String LOG_SAVING_PATH = params.getString("/armor/settings/log_path", DEFAULT_LOG_SAVING_PATH);
        final Boolean SHOW_GUI = params.getBoolean("/armor/settings/show_gui", DEFAULT_SHOW_GUI);
        final Boolean LOG_OWL_LIBRARY = params.getBoolean("/armor/settings/log_owl_lib", DEFAULT_LOG_OWL_LIBRARY);
        final Boolean LOG_OWL_ENQUIRER = params.getBoolean("/armor/settings/log_owl_enquirer", DEFAULT_LOG_OWL_ENQUIRER);
        final Boolean LOG_OWL_MANIPULATOR = params.getBoolean("/armor/settings/log_owl_manipulator", DEFAULT_LOG_OWL_MANIPULATOR);
        // TODO: for inputs
        final Boolean FULL_ENTITY_IDENTIFIER =
                params.getBoolean("/armor/settings/full_entity_iri", DEFAULT_FULL_ENTITY_IDENTIFIER);
        final Boolean LOG_REFERENCES_CONTAINER =
                params.getBoolean("/armor/settings/log_reference_container", DEFAULT_LOG_REFERENCES_CONTAINER);
        final Boolean LOG_REFERENCES_INTERFACE =
                params.getBoolean("/armor/settings/log_reference_interface", DEFAULT_LOG_REFERENCES_INTERFACE);
        final Boolean LOG_REASONER_MONITOR =
                params.getBoolean("/armor/settings/log_reasoner_monitor", DEFAULT_LOG_REASONER_MONITOR);
        final Boolean LOG_REASONER_EXPLANATION =
                params.getBoolean("/armor/settings/log_reasoner_explanation", DEFAULT_LOG_REASONER_EXPLANATION);
        final Boolean LOG_ONTOLOGY_REFERENCE =
                params.getBoolean("/armor/settings/log_ontology_reference", DEFAULT_LOG_ONTOLOGY_REFERENCE);
        final Boolean LOG_ONTOLOGY_EXPORTER =
                params.getBoolean("/armor/settings/log_ontology_exporter", DEFAULT_LOG_ONTOLOGY_EXPORTER);

        if (SHOW_GUI){
            connectedNode.getLog().info("Staring GUI.");
            new Thread(new GuiRunner()).start();
            connectedNode.getLog().info("GUI started.");
        }

        ARMORResourceManager.setLogging(connectedNode);

        ServiceServer<ArmorDirectiveRequest, ArmorDirectiveResponse> plan_to_moveit =
                connectedNode.newServiceServer("armor_interface_srv", ArmorDirective._TYPE,

                        new ServiceResponseBuilder<ArmorDirectiveRequest, ArmorDirectiveResponse>() {

                            @Override
                            public void
                            build(ArmorDirectiveRequest request, ArmorDirectiveResponse response) {

                                ARMORCommand command = new ARMORCommand(request, response,
                                        FULL_ENTITY_IDENTIFIER, connectedNode);
                                if (!command.getServiceResponse().getSuccess()) {
                                    response = command.executeCommand();
                                }else{
                                    response = command.getServiceResponse();  // catch invalid command
                                }

                            }
                        });
    }

    // For testing and debugging purposes only:

    public static void main(String argv[]) throws java.io.IOException {

        String[] args = { "it.emarolab.armor.ARMORMainService" };
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(args));
        NodeConfiguration nodeConfiguration = loader.build();
        ARMORMainService service = new ARMORMainService();

        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(service, nodeConfiguration);
    }
}