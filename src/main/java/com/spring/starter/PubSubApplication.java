package com.spring.starter;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.google.cloud.pubsub.v1.AckReplyConsumer;

@SpringBootApplication
public class PubSubApplication {

	public static void main(String[] args) {
		SpringApplication.run(PubSubApplication.class, args);
	}
	
	// Create MessageChannel Bean for pubsubInputChannel
	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}

	// Create PubSubInboundChannelAdapter Bean for messageChannelAdapter
	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("pubsubInputChannel") MessageChannel inputChannel, PubSubOperations pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "testSubscriptions");
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	// Create Service Activator Bean for Pub sub input channel
	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public MessageHandler messageReceiver() {
		return message -> {
			System.out.println("Message Received::" + message.getPayload());
			AckReplyConsumer consumer = (AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
			consumer.ack();
		};
	}

	// Create Service Activator Bean for Pub sub output channel

	@Bean
	@ServiceActivator(inputChannel = "pubsubOutputChannel")
	public MessageHandler messageSender(PubSubOperations pubsubTemplate) {
		return new PubSubMessageHandler(pubsubTemplate, "testTopic");
	}

	// Create Messaging Gateway interface to publish the message
	@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
	public interface PubsubOutboundGateway {
		void sendToPubsub(String text);
	}
	
}
