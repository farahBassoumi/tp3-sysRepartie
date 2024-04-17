package Replica;

import sendFinout.SendFinout ;
import LireDernierLigne.LireDerniereLigneFichier;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReplicaClientRead {
    private static final String EXCHANGE_NAME = "READ";

    public static void main(String[] argv) throws Exception {

        //initializing the LireDernierLigneFichier
        String path = "Replica/rep"+argv[0];
        LireDerniereLigneFichier lireDL = new LireDerniereLigneFichier(path);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection(argv[0]);
        Channel channel = connection.createChannel(); // Corrected method name

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println("Hello here replica "+argv[0]+" server , the read customer wanted to read the last line !");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message+"is recieved from read customer ! ");

            // Read the last lmine from the file
            String ligneContent = lireDL.lireLigne();

            SendFinout sn = new SendFinout("READCLIENT");

            try {
                System.out.println("this msg has been sended to the Client Reader :"+ligneContent);
                sn.send(ligneContent);
            }catch (Exception e){
                System.out.println("replica can't send  ! ");
            }

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}