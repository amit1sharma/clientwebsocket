package amt.du.clientwebsocket.alert;

import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageListerner;
import amt.du.clientwebsocket.services.FinderBean;
import amt.du.clientwebsocket.socketConstants.WebSocketConstants;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Set;

@Component
public class ConnectivityChecker{
	
	Logger log = Logger.getLogger(ConnectivityChecker.class);
	
	@Autowired
    private FinderBean finderBean;
	
	@Autowired
	private AlertMessageSender alertMessageSender;
	
	@Value("${alert.message}")
	private String alertMessage;
	
	@Scheduled(cron="${alert.check.connectivity.frequency.cron}")
	public void checkHeartBeat(){
		Set<ImmediatePaymentMessageListerner> allListener = finderBean.getAllPrototypeTypeBeans();
		int deadConnectionCount = 0;
		for(ImmediatePaymentMessageListerner obj : allListener){
			if(obj!=null){
				if(obj.getWebsocketClientEndpoint()!=null){
					if(!obj.getWebsocketClientEndpoint().isConnectionUp()){
						deadConnectionCount++;
						log.warn(obj.getWebsocketClientEndpoint()+" connection is down");
					}
				} else {
					deadConnectionCount++;
//					log.warn(" connection never established");
				}
			}
		}
		if(deadConnectionCount>0){
			try{
				String message = MessageFormat.format(alertMessage, deadConnectionCount);
				log.warn(message);
				log.warn("sending alert to alert queue");
				alertMessageSender.sendAlert(getJsonString(message));
			} catch(Exception e){
				log.error("Something wrong happened.", e);
			}
			log.warn("alert to alert queue sent");
		}
	}
	@SuppressWarnings("unchecked")
	private JSONObject getJsonString(String errorDescription){
		JSONObject json = new JSONObject();
		json.put(WebSocketConstants.ALERT_ERROR_REASON, errorDescription);
		json.put(WebSocketConstants.ALERT_MODULE, WebSocketConstants.ALERT_MODULE_VALUE);
		json.put(WebSocketConstants.ALERT_TYPE, WebSocketConstants.ALERT_TYPE_VALUE_WS);
		return json;
	}

}
