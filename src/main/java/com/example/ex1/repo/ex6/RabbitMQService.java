package com.example.ex1.repo.ex6;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQService {
    private static final String HOST = "localhost";
    private static Connection connection;

    public static Channel getChannel() throws Exception {
        if (connection == null || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            connection = factory.newConnection();
        }
        return connection.createChannel();
    }

    public static void declareTopic(String topicName) throws Exception {
        Channel channel = getChannel();
        channel.exchangeDeclare(topicName, "fanout", true);
    }

    public static void publish(String topicName, String message) throws Exception {
        Channel channel = getChannel();
        channel.basicPublish(topicName, "", null, message.getBytes());
    }
}
