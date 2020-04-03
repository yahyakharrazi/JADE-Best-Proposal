package agents;

import containers.AcheteurContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {
    private AcheteurContainer acheteurContainer;
    private AID[] vendeurs;
    @Override
    protected void setup() {
        //recepure l'argument et ensuite verifie si un seul argument et transmis
        if(getArguments().length==1){
            acheteurContainer= (AcheteurContainer) getArguments()[0];
            acheteurContainer.setAcheteurAgent(this);
        }
        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            int cpt=0;
            private List<ACLMessage> replies=new ArrayList<ACLMessage>();
            @Override
            public void action() {
                /*MessageTemplate messageTemplate=MessageTemplate.MatchPerformative(ACLMessage.AGREE);//messages a accepter sont que ceux qui sont de type AGREE
                //plus de restrictions sur les messages
                messageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),MessageTemplate.and(MessageTemplate.MatchContent("this"),messageTemplate.MatchLanguage("Eng")));
                //ceci peut etre remplacer par un switch sur aclMessage
                ACLMessage aclMessage=receive(messageTemplate);//accepter les messages de message template*/
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST:
                            ACLMessage aclMessageToSellers=new ACLMessage(ACLMessage.CFP);
                            aclMessageToSellers.setContent(aclMessage.getContent());
                            //un consomateur demande un livre : l'acheteur diffuse le message a tous les agents vendeurs
                            for (int i = 0; i < vendeurs.length; i++) {
                                aclMessageToSellers.addReceiver(vendeurs[i]);
                            }
                            send(aclMessageToSellers);
                            break;
                        case ACLMessage.PROPOSE:
                            cpt++;
                            replies.add(aclMessage);
                            if (cpt==vendeurs.length) {
                                ACLMessage bestoffer=replies.get(0);
                                double mini = Double.parseDouble(replies.get(0).getContent());//mini c'est le premier offre

                                for (ACLMessage offre : replies) {
                                    double prix = Double.parseDouble(offre.getContent());
                                    if (prix < mini) {
                                        bestoffer=offre;
                                        mini=prix;
                                    }

                                }
                                ACLMessage aclMessageAccept=bestoffer.createReply();
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                aclMessageAccept.setContent(aclMessage.getContent());
                                send(aclMessageAccept);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage3=new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage3.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            aclMessage3.setContent(aclMessage.getContent());
                            send(aclMessage3);
                            break;
                        default:

                            break;
                    }
                    acheteurContainer.logMessage(aclMessage);
//                    ACLMessage reply=aclMessage.createReply();
//                    reply.setContent("OK pour "+aclMessage.getContent());
//                    send(reply);
//                    ACLMessage aclMessagex=new ACLMessage(ACLMessage.CFP);
//                    aclMessagex.setContent(aclMessage.getContent());//contenu du message venant
//                    aclMessagex.addReceiver(new AID("Vendeur",AID.ISLOCALNAME));//l'agent vendeur n'existe pas ... donc on recoit des messages
//                    send(aclMessagex);
                }
                else{
                    block();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,6000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setName("Vente-livres");
                serviceDescription.setType("Transaction");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results= DFService.search(myAgent,dfAgentDescription);
                    vendeurs = new AID[results.length];
                    for (int i = 0; i < vendeurs.length; i++) {
                        vendeurs[i]=results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
