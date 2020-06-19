package amt.du.clientwebsocket.ws;

import org.apache.log4j.Logger;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

@ClientEndpoint
public class WebsocketClientEndpoint {
	
	Logger log = Logger.getLogger(WebsocketClientEndpoint.class);
	
	private boolean isPongAwated = false;
	private Long lastPingSent = 0l;
	private long lastPongReceivedTime= 0l;
	
	
	public long timeout=6000;
	
	

	public boolean isConnectionUp() {
//		return connectionUp;
		return this.userSession.isOpen();
	}





	public WebsocketClientEndpoint(){
		
	}

    private Session userSession = null;
    private MessageHandler messageHandler;
    public WebsocketClientEndpoint(URI endpointURI) {
        try {
        	System.out.println("connect.log "+endpointURI.getPath());
            log.info(endpointURI.getPath());
        	WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
        	log.error("Error connecting Websocket server",e);
            throw new RuntimeException(e);
        }
     }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     * @throws IOException 
     * @throws DeploymentException 
     * @throws URISyntaxException 
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) throws DeploymentException, IOException, URISyntaxException {
    	synchronized (WebsocketClientEndpoint.class) {
    		log.info(this + " connection open : "+this.userSession.isOpen() +" || "+userSession.isOpen());
        	this.userSession = userSession;
        	/*this.userSession = null;
        	this.connectionUp=false;*/
//        	log.info(this+" closing websocket reason : "+reason.getReasonPhrase() + " reason code : "+reason.getCloseCode());
		}
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
        	log.info("message recevied from CB : "+ message);
            this.messageHandler.handleMessage(message);
        }
    }
    
    @OnMessage
    public void onPong(PongMessage pongMessage){
    	if(pongMessage!=null){
        	isPongAwated = false;
        	
        	String str = new String(pongMessage.getApplicationData().array());
        	long pingTime = Long.parseLong(str);
        	log.info(this + "pong message received is : "+pingTime);
        	lastPongReceivedTime = System.currentTimeMillis();
        	long timeDelayInMili = lastPongReceivedTime - pingTime;
        	System.out.println("Pong time delay"+timeDelayInMili);
        	if(timeDelayInMili>timeout){
        		try {
					this.userSession.close();
				} catch (IOException e) {
					log.error(this+" unable to close connection",e);
				}
        	}
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     * @throws IOException 
     * @throws DeploymentException 
     */
    public void sendMessage(String message) throws Exception {
    	/*if(this.userSession==null){
    		log.info("Connection found closed connecting again");
//    		connectWS();
    	}*/
    	if(this.userSession!=null && this.userSession.isOpen()){
	    	try{
		    	synchronized (WebsocketClientEndpoint.class) {
		    		this.userSession.getBasicRemote().sendText(message);
				}
	    	}catch(Throwable th){
	    		log.error(this+" failed sending message",th);
	    		throw th;
	    	}
    	}else{
    		throw new Exception("Unable to send message connection is closed.");
    	}
    }
    
    public void sendPing() throws IllegalArgumentException, IOException{
    	try{
    		log.info(this +"last ping sent is :before  "+ lastPingSent);
       
        	
    		if(!this.isPongAwated){
		    	synchronized (WebsocketClientEndpoint.class) {
		    		lastPingSent = System.currentTimeMillis();
		    		this.userSession.getBasicRemote().sendPing(ByteBuffer.wrap(lastPingSent.toString().getBytes()));
		    		log.info(this +"last ping sent is : "+ lastPingSent);
		    		this.isPongAwated = true;
//		        	log.info(this+" connectionUp : "+this.isConnectionUp());
				}
    		} else{
    			long timeDelayInMili = System.currentTimeMillis() - lastPingSent ;
            	if(timeDelayInMili>timeout){
            		log.info("pong not received for more than 5 seconds for ping : "+lastPingSent);
            		try {
            			log.info("closing connection "+ this );
    					this.userSession.close();
    				} catch (IOException e) {
    					log.error(this+" unable to close connection",e);
    				}
            	}
    		}
    	}catch(Throwable th){
    		log.error(this + " failed sending ping message",th);
    		throw th;
    	}
//    	this.userSession.getAsyncRemote().sendPing(ByteBuffer.wrap("health-check".getBytes()));
    	
    }
    

    @Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		this.userSession.close();
		log.info("connection to CB closed."+this.userSession.isOpen());
	}



	/**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}