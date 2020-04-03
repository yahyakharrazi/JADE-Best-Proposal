package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {

    private ConsumerAgent consumerAgent;
    ObservableList<String> observableList;
    public static void main(String[] args) {
        launch(args);

    }
    public void startcontainer(){
        Runtime runtime=Runtime.instance();
        ProfileImpl profileimpl=new ProfileImpl();
        profileimpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profileimpl);
        AgentController agentController = null;
        try {
            agentController=container.createNewAgent("Consumer","agents.ConsumerAgent",new Object[]{this});
            agentController.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startcontainer();
        BorderPane borderPane=new BorderPane();
        HBox hBox=new HBox();
        Label label=new Label("Livre");
        TextField textField=new TextField();
        Button button=new Button("Acheter");
        hBox.getChildren().addAll(label,textField,button);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        borderPane.setTop(hBox);
        VBox vBox=new VBox();
        observableList=FXCollections.observableArrayList();
        ListView<String> listViewMessages=new ListView<String>(observableList);
        vBox.getChildren().add(listViewMessages);
        vBox.setPadding(new Insets(10));
        borderPane.setCenter(vBox);
        Scene scene=new Scene(borderPane,300,300);
        primaryStage.setTitle("Consumer");
        primaryStage.setScene(scene);
        primaryStage.show();
        button.setOnAction(evt->{
            String livre=textField.getText().toString();
            //observableList.add(livre);
            GuiEvent event=new GuiEvent(this,1);
            event.addParameter(livre);
            consumerAgent.onGuiEvent(event);
        });
    }

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent()+"\n"+aclMessage.getSender().getName()+"\n");
        });    }
}