package amt.du.clientwebsocket.config;

import amt.du.clientwebsocket.cryptoProcessor.CBWSRequestCreator;
import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageSender;
import amt.du.clientwebsocket.ws.WebsocketClientEndpoint;
import amt.du.clientwebsocket.ws.WebsocketSSLClientEndpoint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class WebSocketClientConnectionMaker {
	
	Logger log = Logger.getLogger(WebSocketClientConnectionMaker.class);
	
	@Autowired
	private ImmediatePaymentMessageSender messageSender;
	
	@Autowired
	private CBWSRequestCreator cBWSRequestCreator;
	
	@Value("${cb.websocket.url}")
	private String cbWebSocketUrl;
	
	@Value("${cb.keystore.path}")
	private String keyStorePath;
	
	@Value("${cb.keystore.password}")
	private String keyStorePassword;
	
	@Value("${cb.websocket.protocol}")
	private String cbProtocol;
	
	@Value("${cb.websocket.domain}")
	private String cbDomain;
	
	@Value("${cb.websocket.uri}")
	private String cbURI;
	
	@Value("${pong.timeout}")
	private long timeout;
	
	{
		log.info("WebSocketClientConnectionMaker object created");
	}
	
	
//	@Bean
	public WebsocketSSLClientEndpoint websocketSSLClientEndpoint() throws Exception{
		try {
			log.info("Trying to establish secured connection to CB");
			String requestParams=cBWSRequestCreator.createWSConnectionRequest();
			cbWebSocketUrl+=requestParams;
			log.info("url with all params is ");
			log.info(""+cbWebSocketUrl);
            WebsocketSSLClientEndpoint clientEndPoint = new WebsocketSSLClientEndpoint(new URI(cbWebSocketUrl),keyStorePath, keyStorePassword);
            log.info("secured connection to cb successfull. registering message handler");
            clientEndPoint.addMessageHandler(new WebsocketSSLClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                	messageSender.sendMessage(message);
                	log.info("message sent to queue"+ message);
                }
            });
            return clientEndPoint;
        } catch (URISyntaxException ex) { 
            System.err.println("URISyntaxException exception: " + ex.getMessage());
            return null;
        }
	}

	public WebsocketClientEndpoint connectAndReturn() throws Exception{
		try {
			System.out.println("WebSocketClientConnectionMaker Trying to establish connection to CB");
			log.info("Trying to establish connection to CB");
			
			String requestParams=cBWSRequestCreator.createWSConnectionRequest();
			System.out.println("WebSocketClientConnectionMaker request created to connect to CB");
			//URI endpointURI = new URI(cbProtocol, cbDomain, cbURI + requestParams, null, null);
			
		URI endpointURI = new URI(cbProtocol, cbDomain, cbURI, requestParams, null);
			
			/*String url = cbWebSocketUrl + requestParams;
			URI endpointURI = new URI(url);*/
			log.info(cbURI+requestParams);
			WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(endpointURI);
			
			clientEndPoint.timeout=timeout;
			System.out.println("WebsocketClientEndpoint timeout"+clientEndPoint.timeout);
			System.out.println("WebSocketClientConnectionMaker connection to cb successfull");
			log.info("connection to cb successfull. registering message handler");
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                	System.out.println("WebSocketClientConnectionMaker message received by websocket and sending to Outgoing queue ");
                	log.info("message received by websocket and sending to Outgoing queue : "+ message);
                	messageSender.sendMessage(message);
                }
            });
            System.out.println("message handler added.");
            return clientEndPoint;
        } catch (Exception ex) { 
        	ex.printStackTrace();
            log.info("URISyntaxException exception: " ,ex);
            return null;
        }
	}
}
