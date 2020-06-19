package amt.du.clientwebsocket.config;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

//@Configuration
public class AlertJmsConfig {
	
	@Value("${alert.jndi}")
	private String alertJndiName;

	@Bean("alertJmsTemplate")
    public JmsTemplate alertJmsTemplate() throws JMSException, IllegalArgumentException, NamingException {
        return new JmsTemplate(alertConnectionFactory());
    }
	
	@Bean("alertConnectionFactory")
	public ConnectionFactory alertConnectionFactory() throws IllegalArgumentException, NamingException{
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName(alertJndiName);
		jndiObjectFactoryBean.setJndiTemplate(alertJndiTemplate());
		jndiObjectFactoryBean.afterPropertiesSet();
		return (ConnectionFactory)jndiObjectFactoryBean.getObject();
	}
	
	@Bean("alertJndiTemplate")
	public JndiTemplate alertJndiTemplate(){
		JndiTemplate jndiTemplate = new JndiTemplate();
		jndiTemplate.setEnvironment(getEnvProperties());
		return jndiTemplate;
	}
	
	private Properties getEnvProperties() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
//		env.put(Context.PROVIDER_URL, providerUrl);
		return env;
	}
}
