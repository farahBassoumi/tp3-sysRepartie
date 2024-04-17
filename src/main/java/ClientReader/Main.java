package ClientReader;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import sendFinout.*;
import AjouterLigneFichier.AjouterLigneFichier;

import com.rabbitmq.client.*;

public class Main {
    private static final String EXCHANGE_NAME = "READCLIENT";
    private static final String QUEUE_NAME = "Reader"; // Replace with the queue name used by ReplicaClientRead

    public static void main(String[] args) throws Exception {
        // initializing the scanner
        Scanner scanner = new Scanner(System.in);

        // initializing the AjouterLigneFichier
        AjouterLigneFichier ajoutLigne = new AjouterLigneFichier("ClientReader");

        // initializing the sendFinout class
        SendFinout sendFinout = new SendFinout("READ");

        // Set up RabbitMQ connection and channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);;
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        System.out.println("Hello! You are the reader customer. \n Write ‘Read Last’ to read the last line :\n ");

        String message;

        AtomicBoolean processMessages = new AtomicBoolean(true); // Flag to control message processing

        // Create a consumer to process messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws java.io.IOException {
                if (processMessages.get()) {
                    String receivedMessage = new String(body, "UTF-8");
                    System.out.println("Received message from ReplicaClientRead: " + receivedMessage);

                    // writing it in the file fichier.txt in the repository ClientWriter
                    ajoutLigne.ajouterLigne(receivedMessage);
                    processMessages.set(false); // Stop processing further messages
                }
                else {
                    System.out.print("\n");
                }
            }
        };

        // Consume messages from the queue
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // Wait for user input (program will continue running)
        while (true) {
            message = scanner.nextLine();
            sendFinout.send(message);
            processMessages.set(true); // Allow processing of next message
        }
    }
}