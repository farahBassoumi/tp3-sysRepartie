package sendFinout;
import com. rabbitmq. client. ConnectionFactory;
import com. rabbitmq.client.Connection;
import com. rabbitmq. client.Channel;

public class SendFinout {
    private String EXCHANGE_NAME ;

    public SendFinout(String EXCHANGE_NAME){
        this.EXCHANGE_NAME = EXCHANGE_NAME ;
    }

    public void send(String message) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));

        }
    }

}