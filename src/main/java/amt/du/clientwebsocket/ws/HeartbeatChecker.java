package amt.du.clientwebsocket.ws;

import amt.du.clientwebsocket.config.WebSocketClientConnectionMaker;
import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageListerner;
import amt.du.clientwebsocket.services.FinderBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HeartbeatChecker{
	
	Logger log = Logger.getLogger(HeartbeatChecker.class);
	
	@Autowired
    private FinderBean finderBean;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private WebSocketClientConnectionMaker connectionmaker;
		

	@Scheduled(cron="${cb.healthcheck.frequency.cron}")
	public void checkHeartBeat(){
		
		Set<ImmediatePaymentMessageListerner> allListener = finderBean.getAllPrototypeTypeBeans();
		log.info("Number of connections : "+ allListener.size());
		for(ImmediatePaymentMessageListerner obj : allListener){
			try{
				WebsocketClientEndpoint ws = obj.getWebsocketClientEndpoint();
				if(ws!=null && ws.isConnectionUp()){
					ws.sendPing();
				} else{
					System.out.println("HeartbeatChecker connection is down waiting to get it up");
					log.info(obj + "connection is down waiting to get it up");
					WebsocketClientEndpoint clientEndPoint = connectionmaker.connectAndReturn();
					obj.updateWebSocketClientObject(clientEndPoint);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
	
	/*@Scheduled(cron="${cb.healthcheck.frequency.cron}")
	public void checkHeartBeat(){
		Map<String, ImmediatePaymentMessageListerner> beans = applicationContext.getBeansOfType(ImmediatePaymentMessageListerner.class);
		for(Map.Entry<String, ImmediatePaymentMessageListerner> entry : beans.entrySet()){
			try{
				ImmediatePaymentMessageListerner obj = entry.getValue();
				WebsocketClientEndpoint ws = obj.getWebsocketClientEndpoint();
				if(ws.isConnectionUp()){
					ws.sendPing();
				} else{
					log.info("ImmediatePaymentMessageListerner "+obj.getId()+ " connection is down waiting to get it up");
					WebsocketClientEndpoint clientEndPoint = connectionmaker.connectAndReturn();
					obj.updateWebSocketClientObject(clientEndPoint);
				}
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
	}*/
	
	/*@Scheduled(cron="${cb.healthcheck.frequency.cron}")
	public void checkHeartBeat() throws Exception{
		if(websocketClientEndpoint.isConnectionUp()){
			websocketClientEndpoint.sendPing();
		} else{
			log.info("connection is down waiting to get it up");
			synchronized (WebsocketClientEndpoint.class) {
				checkConnection();
			}
		}
	}
	public void checkConnection() throws Exception{
		WebsocketClientEndpoint clientEndPoint = connectionmaker.connectAndReturn();
		
		String requestParams=cBWSRequestCreator.createWSConnectionRequest();
		URI endpointURI = new URI(cbProtocol, cbDomain, cbURI + requestParams, null, null);
		WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(endpointURI);
		log.info("connection to cb successfull. registering message handler");
        clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
            public void handleMessage(String message) {
            	log.info("message received by websocket and sending to Outgoing queue : "+ message);
            	messageSender.sendMessage(message);
            }
        });
        try{
        	notifyObserver(clientEndPoint);
        }catch(Exception e){
        	this.websocketClientEndpoint = clientEndPoint;
        	log.error("unable to update observers",e);
        }
	}
	private void notifyObserver(WebsocketClientEndpoint client){
		
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

		final Set<BeanDefinition> classes = provider.findCandidateComponents("com.ws");

		for (BeanDefinition bean: classes) {
		    try {
				Class<?> clazz = Class.forName(bean.getBeanClassName());
				if(WebSocketObserver.class.isAssignableFrom(clazz)){
					WebSocketObserver wso = (WebSocketObserver)applicationContext.getBean(clazz);
					wso.updateWebSocketClientObject(client);
					log.info("updated websocket client for bean : "+bean.getBeanClassName());
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	@Override
	public void updateWebSocketClientObject(WebsocketClientEndpoint client) {
		 this.websocketClientEndpoint = client;
	}*/

}
