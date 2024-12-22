package verteilte.systeme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;

import static org.junit.Assert.assertEquals;

public class DownlinkPayloadDtoTest {

    @Test
    public void setCorrectPayload() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DownlinkPayloadDto dto = new DownlinkPayloadDto();

        DownlinkPayloadDto.DownLink downlink = new DownlinkPayloadDto.DownLink();
        var time = DateFormat.getInstance().format(System.currentTimeMillis());
        downlink.setFrm_payload(time, 1);
        downlink.priority = "NORMAL";
        downlink.f_port = 1;

        dto.downlinks = new DownlinkPayloadDto.DownLink[]{downlink};

        byte[] bytes = mapper.writeValueAsBytes(dto);

        assertEquals(dto, mapper.readValue(bytes, DownlinkPayloadDto.class));
    }
}