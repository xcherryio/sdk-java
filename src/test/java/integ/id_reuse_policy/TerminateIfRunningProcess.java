package integ.id_reuse_policy;

import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.gen.models.ProcessIdReusePolicy;
import io.xdb.gen.models.ProcessStartConfig;
import org.springframework.stereotype.Component;

@Component
public class TerminateIfRunningProcess implements Process {

    @Override
    public ProcessOptions getOptions() {
        return ProcessOptions
            .builder(TerminateIfRunningProcess.class)
            .processStartConfig(new ProcessStartConfig().idReusePolicy(ProcessIdReusePolicy.TERMINATE_IF_RUNNING))
            .build();
    }
}
