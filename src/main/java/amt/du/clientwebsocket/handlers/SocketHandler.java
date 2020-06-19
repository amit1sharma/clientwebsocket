package amt.du.clientwebsocket.handlers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
	
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		
		System.out.println("in SocketHandler");
		System.out.println("message received on server : "+ message.getPayload());
		
		session.sendMessage(new TextMessage("Hello user"));
		
		/*for(WebSocketSession webSocketSession : sessions) {
			System.out.println(message.getPayload());
			webSocketSession.sendMessage(new TextMessage("Hello user !"));
		}*/
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//the messages will be broadcasted to all users.
		sessions.add(session);
	}
}