package integ.id_reuse_policy;

import io.xdb.core.process.Process;
import org.springframework.stereotype.Component;

@Component
public class AllowIfNoRunningProcess implements Process {}
