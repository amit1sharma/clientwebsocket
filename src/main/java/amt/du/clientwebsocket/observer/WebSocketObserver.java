package amt.du.clientwebsocket.observer;

import amt.du.clientwebsocket.ws.WebsocketClientEndpoint;

public interface WebSocketObserver {
	public WebsocketClientEndpoint getWebsocketClientEndpoint();

	public void updateWebSocketClientObject(WebsocketClientEndpoint client);
}
