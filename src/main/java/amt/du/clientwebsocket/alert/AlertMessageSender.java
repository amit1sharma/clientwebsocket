package amt.du.clientwebsocket.alert;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class AlertMessageSender {
	
	/**
	 * Intentionally using ConnectivityChecker so as to write logs into alert log file
	 */
	Logger log = Logger.getLogger(AlertMessageSender.class);
	
	@Value("${alert.queue}")
	private String alertQueueName;
	
	@Autowired
	private JmsTemplate alertJmsTemplate;
	
	public void sendAlert(JSONObject message){
		String str = message.toJSONString();
		sendAlert(alertQueueName, str);
		log.warn("message sent is : "+ str);
	}
	
	public void sendAlert(String qName, String message){
		alertJmsTemplate.send(qName,new MessageCreator() {
	          @Override
	          public Message createMessage(Session session) throws JMSException {
	              return session.createTextMessage(message);
	          }
	      });
	}
}
