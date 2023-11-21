package io.xcherry.core.state;

import com.google.common.base.Strings;
import io.xcherry.core.exception.ProcessDefinitionException;
import io.xcherry.core.persistence.schema_to_load.PersistenceSchemaToLoadData;
import io.xcherry.core.utils.ProcessUtil;
import io.xcherry.gen.models.RetryPolicy;
import io.xcherry.gen.models.StateFailureRecoveryOptions;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AsyncStateOptions {

    /**
     * Either stateClass or id must be set
     */
    private final Class<? extends AsyncState> stateClass;
    /**
     * Either stateClass or id must be set
     */
    private final String id;

    private int waitUntilApiTimeoutSeconds;
    private int executeApiTimeoutSeconds;
    private RetryPolicy waitUntilApiRetryPolicy;
    private RetryPolicy executeApiRetryPolicy;
    private StateFailureRecoveryOptions stateFailureRecoveryOptions;
    private PersistenceSchemaToLoadData persistenceSchemaToLoadData;

    public static AsyncStateOptionsBuilder builder(final Class<? extends AsyncState> stateClass) {
        return builder().stateClass(stateClass);
    }

    private static AsyncStateOptionsBuilder builder() {
        return new AsyncStateOptionsBuilder();
    }

    public String getId() {
        return Strings.isNullOrEmpty(id) ? ProcessUtil.getClassSimpleName(stateClass) : id;
    }

    public void validate() {
        if (stateClass == null && Strings.isNullOrEmpty(id)) {
            throw new ProcessDefinitionException("AsyncStateOptions: either stateClass or id must be set.");
        }
    }
}