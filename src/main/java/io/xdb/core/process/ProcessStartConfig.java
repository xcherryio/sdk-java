package io.xdb.core.process;

import io.xdb.core.persistence.PersistenceTableRowToUpsert;
import io.xdb.gen.models.ProcessIdReusePolicy;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessStartConfig {

    private int timeoutSeconds;
    private ProcessIdReusePolicy processIdReusePolicy;
    private List<PersistenceTableRowToUpsert> globalAttributesToUpsert;
}
