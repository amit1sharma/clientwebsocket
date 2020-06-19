package amt.du.clientwebsocket.controller;

import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MakeWebSocketRequest{
	
	@Autowired
	private ImmediatePaymentMessageSender immediatePaymentMessageSender;

	@ResponseBody
	@RequestMapping(value="/sendmq", method=RequestMethod.GET)
	public String sendMQ(@RequestParam(value="qName") String qName,@RequestParam(value="message") String message) throws Exception{
		immediatePaymentMessageSender.sendMessageToQueue(qName, message);
		return "message sent";
	}

	@ResponseBody
	@RequestMapping(value="/")
	public String home() {
		return "working fine";
	}

	
}