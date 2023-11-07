package io.xdb.core.communication;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommunicationSchema {

    private final List<LocalQueueDef> localQueueDefs;
}
