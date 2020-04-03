package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
    private transient ConsumerContainer gui;
    @Override
    protected void setup() {
        if(getArguments().length==1) {
            gui = (ConsumerContainer) getArguments()[0];
            gui.setConsumerAgent(this);
        }

        ParallelBehaviour pb=new ParallelBehaviour();
        pb.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage=receive();
                if(aclMessage!=null){
                    if(aclMessage.getPerformative()==ACLMessage.CONFIRM)
                        gui.logMessage(aclMessage);
                }
                else{
                    block();
                }
            }
        });

        addBehaviour(pb);
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if(guiEvent.getType()==1){
            String livre=  guiEvent.getParameter(0).toString();
            ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Acheteur",AID.ISLOCALNAME));
            send(aclMessage);
        }

    }
}
