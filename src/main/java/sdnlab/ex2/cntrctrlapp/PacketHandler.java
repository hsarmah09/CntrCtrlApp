package sdnlab.ex2.cntrctrlapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Future;
import org.opendaylight.*;
import org.opendaylight.controller.*;

import org.opendaylight.controller.hosttracker.IfHostListener;//
import org.opendaylight.controller.hosttracker.IfIptoHost;//
import org.opendaylight.controller.hosttracker.hostAware.HostNodeConnector;
import org.opendaylight.controller.sal.packet.ARP;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.packet.IPv4;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.action.Flood;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.match.MatchField;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.utils.NetUtils;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.routing.IRouting;
import org.opendaylight.controller.sal.core.Path;
import org.opendaylight.controller.sal.core.Edge;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.topologymanager.ITopologyManager;
import org.opendaylight.controller.hosttracker.hostAware.HostNodeConnector;
import org.opendaylight.controller.hosttracker.IfHostListener;
import org.opendaylight.controller.hosttracker.IfIptoHost;


import java.lang.System.*;
import java.net.InetAddress;


@SuppressWarnings("unused")

public class PacketHandler implements IListenDataPacket {
    private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);
    private IDataPacketService dataPacketService;
	private IFlowProgrammerService flowService;
	private ITopologyManager topologyManagerService;
	private ISwitchManager switchManagerService;
	private IfIptoHost ifIPtoHostService;
	private IRouting routingService;
	
    
  
    
    /*
     * Sets a reference to the requested DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
     * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    
    //arpCache to store the arp cache
    private static HashMap<byte[], byte[]> arpCache = new HashMap<byte[], byte[]>();
    //Node IDs and connector IDs which are related to the given topology
    private static final String[] NODE_ID = {"00:00:00:00:00:00:00:02", "00:00:00:00:00:00:00:03","00:00:00:00:00:00:00:04" };
    private static final String[] CONNECTOR_ID = {"1","2","3"};
    private String[] ts={"s2-eth4","s3-eth4","s4-eth4"};
    private String[] ts1={"s2-eth1","s2-eth2","s2-eth3"};
    private String[] ts2={"s3-eth1","s3-eth2","s3-eth3"};
    private String[] ts3={"s4-eth1","s4-eth2","s4-eth3"};
    private String[] ts4={"s1-eth1","s1-eth2","s1-eth3"};

    public void setDataPacketService(IDataPacketService dataPacketService) {
    	log.trace("Set DataPacketService.");
		this.dataPacketService = dataPacketService;
	
    }
    
    public void setFlowProgrammerService(IFlowProgrammerService S)
    {
    	this.flowService=S;
    }
    /*
     * Unsets DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
     * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    void unsetDataPacketService(IDataPacketService dataPacketService) {
        log.trace("Removed DataPacketService.");

        if (this.dataPacketService == dataPacketService) {
            this.dataPacketService = null;
        }
    }
    
    
    //Unsets FlowProgrammerService
    void unsetFlowProgrammerService(IFlowProgrammerService S) {
        log.trace("Removed FlowProgrammerService.");
        if (this.flowService == S) {
            this.flowService = null;
        }
    }
    
	//Sets a reference to the requested TopologyManagerService//
	void setTopologyManagerService(ITopologyManager t) {
		log.trace("Set TopologyManagerService.");

		topologyManagerService = t;
	}

	//Unsets TopologyManagerService>//
	void unsetTopologyManagerService(ITopologyManager t) {
		log.trace("Removed TopologyManagerService.");

		if (topologyManagerService == t) {
			topologyManagerService = null;
		}
	}
    
    
    //Sets a reference to the requested SwitchManagerService//
	void setSwitchManagerService(ISwitchManager s) {
		log.trace("Set SwitchManagerService.");

		switchManagerService = s;
	}

	//Unsets SwitchManagerService
	void unsetSwitchManagerService(ISwitchManager s) {
		log.trace("Removed SwitchManagerService.");

		if (switchManagerService == s) {
			switchManagerService = null;
		}
	}
    
    
    
    //Sets a reference to the requested IfIptoHostService//
	void setHostTrackerService(IfIptoHost i) {
		log.trace("Set IfIptoHostService.");

		ifIPtoHostService = i;
	}

	//Unsets IfIptoHostService//
	void unsetHostTrackerService(IfIptoHost i) {
		log.trace("Removed IfIptoHostService.");

		if (ifIPtoHostService == i) {
			ifIPtoHostService = null;
		}
	}
	void setRoutingService(IRouting s) {
		log.trace("Set RoutingService.");
		routingService = s;
	}

	/**
	 * Unsets DataPacketService
	 */
	void unsetRoutingService(IRouting s) {
		log.trace("Removed RoutingService.");

		if (routingService == s) {
			dataPacketService = null;
		}
	}
    
	@Override
	public PacketResult receiveDataPacket(RawPacket inPkt) {
		log.trace("Received data packet.");
		//System.out.println("packet received");
		
		// Use DataPacketService to decode the packet.
		Packet layer2pkt = dataPacketService.decodeDataPacket(inPkt);
		NodeConnector conn_in=inPkt.getIncomingNodeConnector();
		Node nod_in=conn_in.getNode();
		
		
		InetAddress srcAddr=null;
		InetAddress dstip = null;
		
		if (layer2pkt instanceof Ethernet) {
			
			Object layer3Pkt = layer2pkt.getPayload();
			if(layer3Pkt instanceof IPv4)
			{
				System.out.println("IP packet received");	        
				IPv4 ipv4pkt=(IPv4)layer3Pkt;
				srcAddr=NetUtils.getInetAddress(ipv4pkt.getSourceAddress());
				dstip = NetUtils.getInetAddress(ipv4pkt.getDestinationAddress());
				System.out.println("Packet receaived from " + srcAddr);
				System.out.println("Packet destination is " + dstip);
				Node srcNode=nod_in;
				NodeConnector tst1= switchManagerService.getNodeConnector(nod_in, ts[srcAddr.getAddress()[2]-2]);
				NodeConnector tst2= switchManagerService.getNodeConnector(nod_in, ts1[dstip.getAddress()[2]-2]);
				NodeConnector tst3= switchManagerService.getNodeConnector(nod_in, ts2[dstip.getAddress()[2]-2]);
				NodeConnector tst4= switchManagerService.getNodeConnector(nod_in, ts3[dstip.getAddress()[2]-2]);
				
				//checking that source and destination are in the same network					
				if(srcAddr.getAddress()[2]==dstip.getAddress()[2])  {
					
			 //checking that the packet is in the network 10.0.2.x and populating the flows       
	        if (srcAddr.getAddress()[2]==2){
	        	
	        InetAddress addr= null;
	        NodeConnector tst11 = null;
	        
	        try {
			
				for (int la=1;la<=3;la++)
				{
	        	if (srcAddr.getAddress()[3]==1|| la==1)
				 {
					 tst11	= switchManagerService.getNodeConnector(nod_in, ts1[0]);
					 addr = InetAddress.getByName("10.0.2.1");
				 }
				 if (srcAddr.getAddress()[3]==2|| la==2)
				 { tst11	= switchManagerService.getNodeConnector(nod_in, ts1[1]);
					addr = InetAddress.getByName("10.0.2.2");
				 }
				 if (srcAddr.getAddress()[3]==3|| la==3)
				 {tst11	= switchManagerService.getNodeConnector(nod_in, ts1[2]);
					 addr = InetAddress.getByName("10.0.2.3");
				 }					 				 
				 Match matcha=new Match();
					matcha.setField(MatchType.NW_DST,addr);
					matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
								        
			        List<Action> actionsbacka = new ArrayList<Action>();
				
			        actionsbacka.add(new Output(tst11));
			        Flow fBack2 = new Flow(matcha, actionsbacka);
			        Status statusa=flowService.addFlow(nod_in, fBack2);
			        System.out.println("Flow put in node" +nod_in);
			     
						
				}				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
	        
	        
			 //checking that the packet is in the network 10.0.3.x and populating the flows   
		        if (srcAddr.getAddress()[2]==3){
		        	
		        InetAddress addr= null;
		        NodeConnector tst11 = null;
		        
		        try {
				
					for (int la=1;la<=3;la++)
					{
		        	if (srcAddr.getAddress()[3]==1|| la==1)
					 {
						 tst11	= switchManagerService.getNodeConnector(nod_in, ts2[0]);
						 addr = InetAddress.getByName("10.0.3.1");
					 }
					 if (srcAddr.getAddress()[3]==2|| la==2)
					 { tst11	= switchManagerService.getNodeConnector(nod_in, ts2[1]);
						addr = InetAddress.getByName("10.0.3.2");
					 }
					 if (srcAddr.getAddress()[3]==3|| la==3)
					 {tst11	= switchManagerService.getNodeConnector(nod_in, ts2[2]);
						 addr = InetAddress.getByName("10.0.3.3");
					 }					 				 
					 Match matcha=new Match();
						matcha.setField(MatchType.NW_DST,addr);
						matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
									        
				        List<Action> actionsbacka = new ArrayList<Action>();
					
				        actionsbacka.add(new Output(tst11));
				        Flow fBack2 = new Flow(matcha, actionsbacka);
				        Status statusa=flowService.addFlow(nod_in, fBack2);
				        System.out.println("Flow put in node" +nod_in);
				     
							
					}				
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				 //checking that the packet is in the network 10.0.4.x and populating the flows   
		        if (srcAddr.getAddress()[2]==4){
		        	
			        InetAddress addr= null;
			        NodeConnector tst11 = null;
			        
			        try {
					
						for (int la=1;la<=3;la++)
						{
			        	if (srcAddr.getAddress()[3]==1|| la==1)
						 {
							 tst11	= switchManagerService.getNodeConnector(nod_in, ts3[0]);
							 addr = InetAddress.getByName("10.0.4.1");
						 }
						 if (srcAddr.getAddress()[3]==2|| la==2)
						 { tst11	= switchManagerService.getNodeConnector(nod_in, ts3[1]);
							addr = InetAddress.getByName("10.0.4.2");
						 }
						 if (srcAddr.getAddress()[3]==3|| la==3)
						 {tst11	= switchManagerService.getNodeConnector(nod_in, ts3[2]);
							 addr = InetAddress.getByName("10.0.4.3");
						 }					 				 
						 Match matcha=new Match();
							matcha.setField(MatchType.NW_DST,addr);
							matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
										        
					        List<Action> actionsbacka = new ArrayList<Action>();
						
					        actionsbacka.add(new Output(tst11));
					        Flow fBack2 = new Flow(matcha, actionsbacka);
					        Status statusa=flowService.addFlow(nod_in, fBack2);
					        System.out.println("Flow put in node" +nod_in);
					     
								

						}				
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
		        
 
		        InetAddress addr= null;
		        NodeConnector tst11 = null;
		        //Now configuring the switch 1 for all possible networks in our topology
		        try {
				
		        	Node ofNode = Node . fromString ( "OF|00:00:00:00:00:00:00:01" );
					for (int la=1;la<=9;la++)
					{
						
						if ( la==1)
						 {
							 tst11	= switchManagerService.getNodeConnector(ofNode, ts4[0]);
							 addr = InetAddress.getByName("10.0.2.1");
						 }
						 if (la==2)
						 { tst11	= switchManagerService.getNodeConnector(ofNode, ts4[0]);
							addr = InetAddress.getByName("10.0.2.2");
						 }
						 if (la==3)
						 {tst11	= switchManagerService.getNodeConnector(ofNode, ts4[0]);
							 addr = InetAddress.getByName("10.0.2.3");
						 }		
						
		        	if ( la==4)
					 {
						 tst11	= switchManagerService.getNodeConnector(ofNode, ts4[1]);
						 addr = InetAddress.getByName("10.0.3.1");
					 }
					 if (la==5)
					 { tst11	= switchManagerService.getNodeConnector(ofNode, ts4[1]);
						addr = InetAddress.getByName("10.0.3.2");
					 }
					 if (la==6)
					 {tst11	= switchManagerService.getNodeConnector(ofNode, ts4[1]);
						 addr = InetAddress.getByName("10.0.3.3");
					 }		
					 if ( la==7)
					 {
						 tst11	= switchManagerService.getNodeConnector(ofNode, ts4[2]);
						 addr = InetAddress.getByName("10.0.4.1");
					 }
					 if (la==8)
					 { tst11	= switchManagerService.getNodeConnector(ofNode, ts4[2]);
						addr = InetAddress.getByName("10.0.4.2");
					 }
					 if (la==9)
					 {tst11	= switchManagerService.getNodeConnector(ofNode, ts4[2]);
						 addr = InetAddress.getByName("10.0.4.3");
					 } 
					 
					 
					 Match matcha=new Match();
						matcha.setField(MatchType.NW_DST,addr);
						matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
									        
				        List<Action> actionsbacka = new ArrayList<Action>();
					
				        actionsbacka.add(new Output(tst11));
				        Flow fBack2 = new Flow(matcha, actionsbacka);
				        Status statusa=flowService.addFlow(ofNode, fBack2);
				        System.out.println("Flow put in node" +ofNode);
				     
	
					}				
				
		        } catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
		        
		        
				}
				
				//if source and destination are in different networks
				else{
					
					//now checking that the source host is in network 10.0.2.x
					 if (srcAddr.getAddress()[2]==2){
				        	
					        InetAddress addr= null;
					        NodeConnector tst11 = null;
					        
					        //now adding the flows one at a time (this is the reason for putting the separate if conditions in the for loop)
					        try {
							
					        
					        	Node ofNode = Node . fromString ( "OF|00:00:00:00:00:00:00:02" );
								for (int la=1;la<=6;la++)
								{
					        	if ( la==1)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									 addr = InetAddress.getByName("10.0.3.1");
								 }
								 if (la==2)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									addr = InetAddress.getByName("10.0.3.2");
								 }
								 if (la==3)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									 addr = InetAddress.getByName("10.0.3.3");
								 }		
								 if ( la==4)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									 addr = InetAddress.getByName("10.0.4.1");
								 }
								 if (la==5)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									addr = InetAddress.getByName("10.0.4.2");
								 }
								 if (la==6)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[0]);
									 addr = InetAddress.getByName("10.0.4.3");
								 } 
								 
								 
								 
								 
								 Match matcha=new Match();
									matcha.setField(MatchType.NW_DST,addr);
									matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
												        
							        List<Action> actionsbacka = new ArrayList<Action>();
								
							        actionsbacka.add(new Output(tst11));
							        Flow fBack2 = new Flow(matcha, actionsbacka);
							        Status statusa=flowService.addFlow(ofNode, fBack2);
							        System.out.println("Flow put in node" +ofNode);
							     
								
								}				
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
					        
			
					
					//now checking that the source host is in network 10.0.3.x
					 if (srcAddr.getAddress()[2]==3){
				        	
					        InetAddress addr= null;
					        NodeConnector tst11 = null;
					        
					        //now adding the flows one at a time (this is the reason for putting the separate if conditions in the for loop)
					        try {
							
					        	Node ofNode = Node . fromString ( "OF|00:00:00:00:00:00:00:03" );
								for (int la=1;la<=6;la++)
								{
					        	if ( la==1)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									 addr = InetAddress.getByName("10.0.2.1");
								 }
								 if (la==2)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									addr = InetAddress.getByName("10.0.2.2");
								 }
								 if (la==3)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									 addr = InetAddress.getByName("10.0.2.3");
								 }		
								 if ( la==4)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									 addr = InetAddress.getByName("10.0.4.1");
								 }
								 if (la==5)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									addr = InetAddress.getByName("10.0.4.2");
								 }
								 if (la==6)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[1]);
									 addr = InetAddress.getByName("10.0.4.3");
								 } 
								 
								 
								 
								 
								 Match matcha=new Match();
									matcha.setField(MatchType.NW_DST,addr);
									matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
												        
							        List<Action> actionsbacka = new ArrayList<Action>();
								
							        actionsbacka.add(new Output(tst11));
							        Flow fBack2 = new Flow(matcha, actionsbacka);
							        Status statusa=flowService.addFlow(ofNode, fBack2);
							        System.out.println("Flow put in node" +ofNode);
							     
										
							        System.out.println("Flow put in s1 " + addr);
								}				
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
					        
				
					//now checking that the source host is in network 10.0.4.x
					 if (srcAddr.getAddress()[2]==4){
				        	
					        InetAddress addr= null;
					        NodeConnector tst11 = null;
					        
					        //now adding the flows one at a time (this is the reason for putting the separate if conditions in the for loop)
					        try {
							
					        	Node ofNode = Node . fromString ( "OF|00:00:00:00:00:00:00:04" );
								for (int la=1;la<=6;la++)
								{
					        	if ( la==1)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									 addr = InetAddress.getByName("10.0.2.1");
								 }
								 if (la==2)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									addr = InetAddress.getByName("10.0.2.2");
								 }
								 if (la==3)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									 addr = InetAddress.getByName("10.0.2.3");
								 }		
								 if ( la==4)
								 {
									 tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									 addr = InetAddress.getByName("10.0.3.1");
								 }
								 if (la==5)
								 { tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									addr = InetAddress.getByName("10.0.3.2");
								 }
								 if (la==6)
								 {tst11	= switchManagerService.getNodeConnector(ofNode, ts[2]);
									 addr = InetAddress.getByName("10.0.3.3");
								 } 
								 
								 
								 
								 
								 Match matcha=new Match();
									matcha.setField(MatchType.NW_DST,addr);
									matcha.setField(MatchType.DL_TYPE, (short) 0x0800);	
												        
							        List<Action> actionsbacka = new ArrayList<Action>();
								
							        actionsbacka.add(new Output(tst11));
							        Flow fBack2 = new Flow(matcha, actionsbacka);
							        Status statusa=flowService.addFlow(ofNode, fBack2);
							        System.out.println("Flow put in node" +ofNode);
							     
										
							        
								}				
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
			
				
				}
		        
			}
			
			if (layer3Pkt instanceof ARP) {
				 /*Do I know the destination IP?
				If yes, forward the MAC address corresponding to that IP in incoming connector.
				else,
				forward it to controller so that it query the appropriate switch.
				need to put ARP table entries in a hash MAP which can be searched by the ip
				 */
	
				log.trace("ARP packet received");
				ARP arp = (ARP) layer3Pkt;
				if(arp.getOpCode()==ARP.REQUEST){
					log.trace("ARP request packet received");
		
					//add sender entry to ARP cache table
					arpCache.put(arp.getSenderProtocolAddress(), arp.getSenderHardwareAddress());
					byte[] targetIP=arp.getTargetProtocolAddress();
					
					//check if ARP cache contains requested IP 		        
					if(!arpCache.containsKey(targetIP)){
						log.trace("requested IP not available in ARP cache, forwarding request to target");
						forwardPacket((Ethernet)layer2pkt,NODE_ID[arp.getTargetProtocolAddress()[2]-2],CONNECTOR_ID[arp.getTargetProtocolAddress()[3]-1]);
	
					}
					else{
						log.trace("requested target IP is already present");
						
						//generate an arp response and forward it to the source
						ARP arpResponse = createArpResponse(arp, arpCache.get(targetIP));//method to generate an ARP response message
						Ethernet layer2Response = createEthernetResponse((Ethernet)layer2pkt, arpResponse);//getEthernetResponse is a method implemented to encode a generated ARP response in an ethernet frame
						
						/*accessing NODE_ID and CONNECTOR_ID from the arrays and then putting them into
						 * transmitPacket method to forward the packet, the method is implemented at the bottom
						*/
						forwardPacket((Ethernet)layer2Response, NODE_ID[arp.getTargetProtocolAddress()[2]-2], CONNECTOR_ID[arp.getTargetProtocolAddress()[3]-1]);
					}
				}
				if (arp.getOpCode()==ARP.REPLY){
					log.trace("arp response has been received and we are sending the response");
		
					//save the arp entry in the controller
					arpCache.put(arp.getSenderProtocolAddress(), arp.getSenderHardwareAddress());
					//send the packet to the requester
					forwardPacket((Ethernet)layer2pkt, NODE_ID[arp.getTargetProtocolAddress()[2]-2], CONNECTOR_ID[arp.getTargetProtocolAddress()[3]-1]);
				}
				
				return PacketResult.KEEP_PROCESSING;
			}
	        
		}
		
	
		// We did not process the packet -> let someone else do the job.
		return PacketResult.IGNORED;
	}
	
	/*
	 * Forwards a given Ethernet packet to the switch with the given NodeId and ConnectorID
	 */
	private void forwardPacket(Ethernet ethernetFrame, String nodeId, String connectorId) {

		RawPacket outPacket = dataPacketService.encodeDataPacket(ethernetFrame);
		Node node = Node.fromString("OF", nodeId);
		NodeConnector outgoingNodeConnector = NodeConnector.fromStringNoNode("OF", connectorId, node);
		outPacket.setOutgoingNodeConnector(outgoingNodeConnector);
		dataPacketService.transmitDataPacket(outPacket);
	}
	
	
	 // Creates a ARP response for an ARP request and a MacAddress	
	private ARP createArpResponse(ARP request, byte[] senderMacAddress){

		ARP response = new ARP();
		response.setOpCode(ARP.REPLY);
		response.setHardwareAddressLength(request.getHardwareAddressLength());
		response.setProtocolAddressLength(request.getProtocolAddressLength());
		response.setProtocolType(request.getProtocolType());
		response.setHardwareType(request.getHardwareType());
		response.setSenderProtocolAddress(request.getTargetProtocolAddress());
		response.setSenderHardwareAddress(senderMacAddress);
		response.setTargetProtocolAddress(request.getSenderProtocolAddress());
		response.setTargetHardwareAddress(request.getSenderHardwareAddress());

		return response;

	}
	
	
	//  Creates a Ethernet response from a Ethernet request and a ARP payload	 
	private Ethernet createEthernetResponse(Ethernet request, ARP payload) {
		
		Ethernet response = new Ethernet();
		response.setEtherType(request.getEtherType());	
		response.setDestinationMACAddress(request.getSourceMACAddress());
		response.setSourceMACAddress(payload.getSenderHardwareAddress());
		response.setPayload(payload);

		return response;
	}

}