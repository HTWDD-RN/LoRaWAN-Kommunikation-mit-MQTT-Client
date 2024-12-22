package verteilte.systeme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class DownlinkPayloadDto {
    public DownLink[] downlinks;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Setter
    @Getter
    @EqualsAndHashCode
    public static class DownLink {
        int f_port;
        String frm_payload;
        String priority;

        public void setFrm_payload(String time, Integer srNr) {
            String newPayload = time + ", srNr: " + srNr + " sent";
            this.frm_payload = new String(Base64.getEncoder().encode(newPayload.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        }
    }
}
