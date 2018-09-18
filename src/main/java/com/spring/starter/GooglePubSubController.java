package com.spring.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spring.starter.PubSubApplication.PubsubOutboundGateway;



@RestController
@RequestMapping("pubs")
public class GooglePubSubController {

	@Autowired
	PubsubOutboundGateway messageGatewuy;

	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	public String publishMessage(@RequestBody MyAppGCPMessage message) {
		messageGatewuy.sendToPubsub(message.toString());
		return "Message published to Google Pub/Sub successfully";
	}
}