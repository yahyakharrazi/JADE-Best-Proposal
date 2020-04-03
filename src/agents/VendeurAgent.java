package agents;

import containers.VendeurContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    VendeurContainer gui;
    @Override
    protected void setup() {
        if(getArguments().length==1){
        gui= (VendeurContainer) getArguments()[0];
        gui.setVendeurAgent(this);
        }
        ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    gui.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()){

                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);//changer le performative de CFP a Propose
                            reply.setContent(""+(500+new Random().nextInt(1000)));
                            send(reply);
                            break;
                            case ACLMessage.ACCEPT_PROPOSAL:
                                ACLMessage aclMessage2=aclMessage.createReply();
                                aclMessage2.setPerformative(ACLMessage.AGREE);//stock and stuff
                                aclMessage2.setContent(aclMessage.getContent());
                                send(aclMessage2);
                                break;
                    }
                }else
                    block();
            }
        });
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                dfAgentDescription.setName(getAID());
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setName("Vente-livres");
                serviceDescription.setType("Transaction");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent,dfAgentDescription);
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

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
