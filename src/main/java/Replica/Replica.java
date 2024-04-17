
package Replica;

import AjouterLigneFichier.AjouterLigneFichier ;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Replica {
    private static final String EXCHANGE_NAME = "WRITE";

    public static void main(String[] argv) throws Exception {

        //initializing the ajouterLigneFichier
        String path = "Replica/rep"+argv[0];
        AjouterLigneFichier ajouterLigneFichier = new AjouterLigneFichier(path);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection(argv[0]);
        Channel channel = connection.createChannel(); // Corrected method name

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println("Hello here replica "+argv[0]+" server , you can see the message received and they are automatically stocked in rep "+argv[0]+"  :");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message);

            // ajouter la ligne dans le fichier convenable
            ajouterLigneFichier.ajouterLigne(message);

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
