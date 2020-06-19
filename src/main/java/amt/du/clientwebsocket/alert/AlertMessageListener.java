package amt.du.clientwebsocket.alert;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AlertMessageListener {
	
	Logger log = Logger.getLogger(AlertMessageListener.class);
	
//	@JmsListener(destination = "${alert.queue}")
	public void processMessage(String message) throws Exception{
		log.info("alert queue : "+message);
	}
}
