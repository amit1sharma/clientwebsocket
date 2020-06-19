package amt.du.clientwebsocket.ws;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


//import javax.websocket.ClientEndpoint;
import javax.websocket.*;
import javax.websocket.MessageHandler.Whole;

import org.apache.log4j.Logger;
import org.apache.tomcat.websocket.Constants;

/**
 * ChatServer Client
 *
 * @author Jasir Jamal
 */
//@ClientEndpoint
public class WebsocketSSLClientEndpoint /*extends Endpoint*/ {
	
	Logger log = Logger.getLogger(WebsocketSSLClientEndpoint.class);
	

    Session userSession = null;
    public MessageHandler messageHandler;

    public WebsocketSSLClientEndpoint(URI endpointURI, String keyStorePath, String keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException {
 
    	ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().build();
    	clientEndpointConfig.getUserProperties().put(Constants.SSL_TRUSTSTORE_PROPERTY, keyStorePath);
   	    clientEndpointConfig.getUserProperties().put(Constants.SSL_TRUSTSTORE_PWD_PROPERTY, keyStorePassword);
    
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
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
        log.info("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("closing websocket");
        this.userSession = null;
    }

   

    /**
     * register message handler
     *
     * @param msgHandler
     * @return 
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
	
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     * @author Jasir Jamal
     */
    public static interface MessageHandler {

        public void handleMessage(String message) throws IOException, InterruptedException;
    }

	/* (non-Javadoc)
	 * @see javax.websocket.Endpoint#onOpen(javax.websocket.Session, javax.websocket.EndpointConfig)
	 */
//	@Override
    @OnOpen
	public void onOpen(Session arg0, EndpointConfig arg11) {
		
		   log.info("opening websocket in Endpoint");
	        this.userSession = arg0;
	        arg0.addMessageHandler(new Whole<String>() {
	             public void onMessage(String text) {
	                 log.info("websocket message:"+text);
	                 if (messageHandler != null) {
                     try {
						messageHandler.handleMessage(text);
					} catch (IOException e) {						
						e.printStackTrace();
					} catch (InterruptedException e) {						
						e.printStackTrace();
					}
	                 }
	             }
	         });
	       
	}


}