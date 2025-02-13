package verteilte.systeme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class Main {
    static ObjectMapper objectMapper = new ObjectMapper();
    static DownlinkPayloadDto.DownLink downlink = new DownlinkPayloadDto.DownLink();
    static Integer srNr = 1;
    static HashMap<Integer, Long> sentTime = new HashMap<>();

    public static void main(String[] args) {
        downlink.priority = "NORMAL";
        downlink.f_port = 1;

        Mqtt3AsyncClient client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost("eu1.cloud.thethings.network")
                .serverPort(1883)
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username("project-seminar-lorawan@ttn")
                .password("NNSXS.7TEWV4RT4N6ONVL7AMUB6ARCSSQPHGZKFO2FYMQ.P3IHXIW2ZUMDBBTWAPKHRE4ANLFFK7QMYIF2YHX34A35CR2XW6YA".getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    System.out.println("Connected");
                    try {
                        publish(client);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });


        client.subscribeWith()
                .topicFilter("v3/project-seminar-lorawan@ttn/devices/lorawan-project-htw/up")
                .callback(uplinkMessage -> {
                    try {
                        var receivedMessage = objectMapper.readValue(uplinkMessage.getPayloadAsBytes(), UplinkPayloadDto.class);
                        var messageNr = receivedMessage.getSrNr();

                        if( messageNr == 999) {
                            System.out.println("Received Alive from the device");
                        } else {
                            var rtt = System.currentTimeMillis() - sentTime.get(messageNr);
                            var time = DateFormat.getInstance().format(rtt);
                            System.out.println("Received message: " + receivedMessage);
                            System.out.println("Measured RTT: " + time);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Error: " + throwable.getMessage());
                    } else {
                        System.out.println("Subscribed");
                    }
                });


        Timer timer = new Timer();

        // Step 2: Create a TimerTask subclass
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    publish(client);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Step 4: Schedule the TimerTask
        long delay = 300_000L; // Delay in milliseconds (5 min)
        long period = 300_000L; // Period in milliseconds (5 min)

        // Schedule the task to run after a delay and then repeatedly at a fixed interval
        timer.schedule(task, delay, period);
    }

    public static void publish(Mqtt3AsyncClient client) throws JsonProcessingException {
        DownlinkPayloadDto dto = new DownlinkPayloadDto();
        var currentTime = System.currentTimeMillis();
        var formattedTime = DateFormat.getInstance().format(currentTime);
        downlink.setFrm_payload(formattedTime, srNr);
        dto.downlinks = new DownlinkPayloadDto.DownLink[]{downlink};

        client.publishWith()
                .topic("v3/project-seminar-lorawan@ttn/devices/lorawan-project-htw/down/push")
                .payload(objectMapper.writeValueAsBytes(dto))
                .qos(MqttQos.AT_MOST_ONCE)
                .send()
                .whenComplete((mqtt3Publish, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Unsuccessful publish: " + throwable.getMessage());
                    } else {
                        System.out.println("Published message nr: " + srNr);
                        sentTime.put(srNr, currentTime);
                        srNr++;
                    }
                });
    }
}