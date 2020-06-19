package amt.du.clientwebsocket.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

/**
 * this will connect to CB socket url and receive message from CB and sends to JMS queue
 * @author tp21037220
 *
 */
@Configuration
public class ConnectCB {
	
	Logger lg = Logger.getLogger(ConnectCB.class);
	
	/*@Autowired
	private WebSocketClientConnectionMaker connectionMaker;

	@Bean
	public WebsocketClientEndpoint websocketClientEndpoint() throws Exception{
		return connectionMaker.connectAndReturn();
	}*/
	
}
