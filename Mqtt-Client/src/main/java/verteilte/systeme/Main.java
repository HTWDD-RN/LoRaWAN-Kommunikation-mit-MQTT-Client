package verteilte.systeme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Main {
    static ObjectMapper objectMapper = new ObjectMapper();
    static String downlink = "{\"downlinks\":[{\"f_port\": 1,\"frm_payload\":\"vu8=\",\"priority\": \"HIGHEST\"}]}";

    public static void main(String[] args) throws JsonProcessingException {
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
                .callback(publish -> {
                    try {
                        System.out.println("Received message: " + objectMapper.readValue(publish.getPayloadAsBytes(), PayloadDto.class));
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
    }

    public static void publish(Mqtt3AsyncClient client) throws JsonProcessingException {
        client.publishWith()
                .topic("v3/project-seminar-lorawan@ttn/devices/lorawan-project-htw/down/replace")
                .payload(downlink.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_MOST_ONCE)
                .send()
                .whenComplete((mqtt3Publish, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Unsuccessful publish: " + throwable.getMessage());
                    } else {
                        System.out.println("Published");
                    }
                });
    }
}