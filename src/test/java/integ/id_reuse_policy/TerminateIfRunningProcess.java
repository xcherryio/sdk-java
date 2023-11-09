package integ.id_reuse_policy;

import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.process.ProcessStartConfig;
import io.xdb.gen.models.ProcessIdReusePolicy;
import org.springframework.stereotype.Component;

@Component
public class TerminateIfRunningProcess implements Process {

    @Override
    public ProcessOptions getOptions() {
        return ProcessOptions
            .builder(TerminateIfRunningProcess.class)
            .processStartConfig(
                ProcessStartConfig.builder().processIdReusePolicy(ProcessIdReusePolicy.TERMINATE_IF_RUNNING).build()
            )
            .build();
    }
}
