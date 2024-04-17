
package ClientReaderV2;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Vector;

import sendFinout.*;
import AjouterLigneFichier.AjouterLigneFichier;

import com.rabbitmq.client.*;

public class Main {
    private static final String EXCHANGE_NAME = "READCLIENTV2";
    private static final String QUEUE_NAME = "Readerv2";

    public static void main(String []args) throws Exception{
        // initializing the scanner
        Scanner scanner = new Scanner(System.in);

        // initializing the AjouterLigneFichier
        AjouterLigneFichier ajoutLigne = new AjouterLigneFichier("ClientReaderV2");

        //initializing the sendFinout class
        SendFinout sendFinout = new SendFinout("READV2");

        // Set up RabbitMQ connection and channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        System.out.println("Hello! You are the reader customer v2. \n write ‘Read All’ to read the all lines :\n ");

        String message;
        Vector<String> lines = new Vector<>();
        AtomicInteger messageCount = new AtomicInteger();
        while(true){

            // Read user input
            message = scanner.nextLine();

            // sending it to all the channels connected to the exchange READV2
            sendFinout.send(message);

            channel.basicConsume(QUEUE_NAME, false, (consumerTag, delivery) -> {

                String receivedMessage = new String(delivery.getBody(), "UTF-8");

                //addind the result to lines
                lines.add(receivedMessage);

                // Increase message count
                messageCount.getAndIncrement();

                // Acknowledge the message
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                // Check if reached 3 messages, then cancel consumer
                if (messageCount.get() >= 3) {
                    channel.basicCancel(consumerTag);

                    System.out.println("these lines wil be aded to the ClientReaderV2/fichier.txt : ");

                    String[] lines1 = lines.get(0).split("\\n") ;
                    String[] lines2 = lines.get(1).split("\\n") ;
                    String[] lines3 = lines.get(2).split("\\n");
                    int size1 = lines1.length;
                    int size2 = lines2.length;
                    int size3 = lines3.length;

                    int i=0,j=0,k=0 ;
                    while((i<size1)&&(j<size2)&&(k<size3)){
                        if(lines1[i].equals(lines2[j]) )
                        {
                            if(lines1[i].equals(lines3[k]) ){
                                k++;
                            }
                            System.out.println(lines1[i]);
                            ajoutLigne.ajouterLigne(lines1[i]);
                            i++;
                            j++;
                        }
                        else if(lines1[i].equals(lines3[k]) ){
                            System.out.println(lines1[i]);
                            ajoutLigne.ajouterLigne(lines1[i]);
                            i++;
                            k++;
                        }
                        else if(lines2[j].equals(lines3[k]) ){
                            System.out.println(lines2[j]);
                            ajoutLigne.ajouterLigne(lines2[j]);
                            j++;
                            k++;
                        }
                        else{
                            System.out.println("error there are not two servers that have the same line");
                            i++;
                            j++;
                            k++;
                        }

                    }
                }

            }, consumerTag -> {
            });


        }
    }
}
