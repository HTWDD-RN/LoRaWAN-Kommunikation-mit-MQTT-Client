package verteilte.systeme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.UUID;

// TODO: measure RTT
// TODO: subscribe to sent and resend downlink
// TODO: test when compiling the sketch how much memory & flash is used / available.

public class Main {
    static ObjectMapper objectMapper = new ObjectMapper();
    // 576f726c6420736179732068656c6c6f -- world says hello
//    static String downlink = "{\"downlinks\":[{\"f_port\": 1,\"frm_payload\":\"V29ybGQgc2F5cyBoZWxsbw==\",\"priority\": \"NORMAL\"}]}";
    static DownlinkPayloadDto.DownLink downlink = new DownlinkPayloadDto.DownLink();
    static Integer srNr = 1;
    static HashMap<Integer, String> sentTime = new HashMap<>();

    public static void main(String[] args) throws JsonProcessingException {
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
                        System.out.println("Received message: " + objectMapper.readValue(uplinkMessage.getPayloadAsBytes(), UplinkPayloadDto.class));
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
        DownlinkPayloadDto dto = new DownlinkPayloadDto();
        var time = DateFormat.getInstance().format(System.currentTimeMillis());
        downlink.setFrm_payload(time, srNr);
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
                        System.out.println("Published");
                        srNr = srNr++; // TODO: check number increase
                        sentTime.put(srNr, time);
                    }
                });
    }
}