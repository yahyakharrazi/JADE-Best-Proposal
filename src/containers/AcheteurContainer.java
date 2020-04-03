    package containers;

    import agents.AcheteurAgent;
    import jade.core.ProfileImpl;
    import jade.core.Runtime;
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
    import javafx.scene.control.ListView;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.VBox;
    import javafx.stage.Stage;

    public class AcheteurContainer extends Application {
        private AcheteurAgent acheteurAgent;
        ObservableList<String> observableList;
        @Override
        public void start(Stage primaryStage) throws Exception {
            startContainer();
            BorderPane borderPane=new BorderPane();
            Scene scene=new Scene(borderPane,400,400);
            VBox vBox=new VBox();
            observableList= FXCollections.observableArrayList();
            ListView<String> listViewMessages=new ListView<String>(observableList);
            vBox.getChildren().add(listViewMessages);
            vBox.setPadding(new Insets(10));
            borderPane.setCenter(vBox);
            primaryStage.setTitle("Acheteur");
            primaryStage.setScene(scene);
            primaryStage.show();

        }

        public static void main(String[] args) {
            launch(args);
        }
        public void startContainer(){
            Runtime runtime=Runtime.instance();
            ProfileImpl profileimpl=new ProfileImpl();
            profileimpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");//ceci est le main container
            AgentContainer container=runtime.createAgentContainer(profileimpl);
            AgentController agentController = null;
            try {
                agentController=container.createNewAgent("Acheteur","agents.AcheteurAgent",new Object[]{this});//envoi de l'interface graphique
                agentController.start();

            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        public void logMessage(ACLMessage aclMessage){
            Platform.runLater(()->{
                observableList.add(aclMessage.getContent()+"\n"+aclMessage.getSender().getName()+"\n");
            });
            // seulement pour remedier aux exceptions

        }

        public void setAcheteurAgent(AcheteurAgent acheteurAgent) {
            this.acheteurAgent = acheteurAgent;

        }
    }
