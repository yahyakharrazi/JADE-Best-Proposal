package containers;

import agents.VendeurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurContainer extends Application {
    VendeurAgent vendeurAgent;
    ObservableList<String> observableList;
    AgentContainer agentContainer;

    public void setVendeurAgent(VendeurAgent vendeurAgent) {
        this.vendeurAgent = vendeurAgent;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        BorderPane borderPane=new BorderPane();
        primaryStage.setTitle("Vendeur");
        HBox hBox=new HBox();
        Label label=new Label("Nom de l'agent");
        TextField textFieldAgent=new TextField();
        Button buttonDeploy=new Button("Deploy");
        hBox.getChildren().addAll(label,textFieldAgent,buttonDeploy);
        hBox.setPadding(new Insets(10));
        borderPane.setTop(hBox);
        buttonDeploy.setOnAction(evt->{
            AgentController agentController= null;//envoi de l'interface graphique
            try {

                agentController = agentContainer.createNewAgent(textFieldAgent.getText().toString(),"agents.VendeurAgent",new Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });
        Scene scene=new Scene(borderPane,400,400);
        VBox vBox=new VBox();
        observableList= FXCollections.observableArrayList();
        ListView<String> listViewMessages=new ListView<String>(observableList);
        vBox.getChildren().add(listViewMessages);
        vBox.setPadding(new Insets(10));
        borderPane.setCenter(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startContainer() {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileimpl = new ProfileImpl();
        profileimpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer=runtime.createAgentContainer(profileimpl);



    }
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent()+"\n"+aclMessage.getSender().getName()+"\n");
        });
        // seulement pour remedier aux exceptions

    }
}
