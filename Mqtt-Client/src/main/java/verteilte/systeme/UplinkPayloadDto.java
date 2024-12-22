package verteilte.systeme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UplinkPayloadDto {
    public ReceivedMessage uplink_message;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReceivedMessage {
        String frm_payload;

        public void setFrm_payload(String frm_payload) {
            this.frm_payload = new String(Base64.getDecoder().decode(frm_payload), StandardCharsets.UTF_8);
        }
    }
}


