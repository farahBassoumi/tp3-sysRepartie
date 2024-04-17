package Replica;

import java.util.Vector;

import sendFinout.SendFinout ;
import LireTousFichier.ReadAllFile;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReplicaV2{

    public static String EXCHANGE_NAME = "READV2";

    public static void main(String[] argv) throws Exception {

        //initializing the readAllFile
        String path = "Replica/rep"+argv[0];
        ReadAllFile lireAF = new ReadAllFile(path);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection(argv[0]);
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println("Hello here replicaV2 "+argv[0]+" server , the read customer wanted to read  all lines !");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message);

            // Read all the file
            String lines = lireAF.read();

            SendFinout sn = new SendFinout("READCLIENTV2");

            try {
                sn.send(lines);
                System.out.println("this msg has been sended to the Client Reader :");
                System.out.println(lines);

            }catch (Exception e){
                System.out.println("replicav2 can't send  ! ");
            }

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}