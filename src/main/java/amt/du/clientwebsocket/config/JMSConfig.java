package amt.du.clientwebsocket.config;

import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageListerner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.annotation.PostConstruct;

@Configuration
@EnableJms
public class JMSConfig {
	
	Logger log = Logger.getLogger(JMSConfig.class);
	
	@Value("${jms.listener.count}")
	private int jmsListenerCount;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private WebSocketClientConnectionMaker connectionmaker;
	
	private static final int INTERVAL_BETWEEN_CONNECTION_CREATION_INMILLIS = 5000;
	
	@PostConstruct
	public void initListener(){
		jmsListenerCount = jmsListenerCount>0?jmsListenerCount:1;
		System.out.println("number of connections to be created."+jmsListenerCount);
		for(int i=0;i<jmsListenerCount;i++){
			try {
				Thread.sleep(INTERVAL_BETWEEN_CONNECTION_CREATION_INMILLIS);
				ImmediatePaymentMessageListerner ipml = applicationContext.getBean(ImmediatePaymentMessageListerner.class);
				if(ipml==null || ipml.getWebsocketClientEndpoint()==null){
					log.error("Error creating websocket connection.");
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
