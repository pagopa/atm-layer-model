package it.gov.pagopa.atmlayer.service.model.model;

import io.vertx.core.buffer.Buffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class BufferWithMedata {
    Buffer buffer;
    String filename;
}
