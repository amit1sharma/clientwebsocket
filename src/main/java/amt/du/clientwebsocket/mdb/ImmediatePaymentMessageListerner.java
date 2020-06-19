package amt.du.clientwebsocket.mdb;

import amt.du.clientwebsocket.config.WebSocketClientConnectionMaker;
import amt.du.clientwebsocket.observer.WebSocketObserver;
import amt.du.clientwebsocket.services.FinderBean;
import amt.du.clientwebsocket.ws.WebsocketClientEndpoint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.DeploymentException;
import java.io.IOException;

/**
 * this listens to our queue and push data to CB web socket connection
 * @author tp21037220
 *
 */
@Component
@Scope("prototype")
public class ImmediatePaymentMessageListerner implements WebSocketObserver {
	
	Logger log = Logger.getLogger(ImmediatePaymentMessageListerner.class);
	
	@Autowired
    private FinderBean finderBean;
		
//	@Autowired
	private WebsocketClientEndpoint websocketClientEndpoint;
	
	public WebsocketClientEndpoint getWebsocketClientEndpoint() {
		return websocketClientEndpoint;
	}
	
	@Autowired
	private WebSocketClientConnectionMaker connectionmaker;
	
	@PostConstruct
	public void setWSClient() throws Exception{
		System.out.println("ImmediatePaymentMessageListerner before connection creation");
		finderBean.register(this);
		websocketClientEndpoint = connectionmaker.connectAndReturn();
		System.out.println(this+" ImmediatePaymentMessageListerner new instance created");
		log.info(this+"new instance created");
	}
	
	/**
	 * 
	 * @param message
	 * @throws DeploymentException
	 * @throws IOException
	 * @throws Exception
	 */
	@JmsListener(destination = "${mdb.queue.to.listen}")
	public void processMessage(String message) throws Exception{
		log.info(websocketClientEndpoint + " message received from queue and sending to CB websocket client : "+message);
		websocketClientEndpoint.sendMessage(message);
		log.info(websocketClientEndpoint + " message sent to CB websocket server");
	}
	
	@Override
	public void updateWebSocketClientObject(WebsocketClientEndpoint client) {
		this.websocketClientEndpoint = client;
	}

}
