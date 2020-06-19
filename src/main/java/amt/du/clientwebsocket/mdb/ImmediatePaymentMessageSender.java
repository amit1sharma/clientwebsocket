package amt.du.clientwebsocket.mdb;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class ImmediatePaymentMessageSender {
	
	Logger log = Logger.getLogger(ImmediatePaymentMessageSender.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${mdb.queue.to.send}")
	private String queueName;

	/*@Autowired
	public ImmediatePaymentMessageSender(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}*/

	public void sendMessage(final String message) {
		sendMessageToQueue(queueName, message);
	}

	public void sendMessageToQueue(String qName, final String message) {
		jmsTemplate.send(qName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}
}