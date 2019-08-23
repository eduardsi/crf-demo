package awsm.infra.jackson;

import org.springframework.http.MediaType;

public interface MsgPack {

  MediaType MIME = new MediaType("application", "x-msgpack");

}
