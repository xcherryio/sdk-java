package io.xcherry.core.rpc;

import io.xcherry.core.persistence.schema.PersistenceSchema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RPC {
    /**
     * RPC methods will time out after the specified number of seconds. Set to 0 to disable timeouts.
     *
     * @return the timeout duration in seconds.
     */
    int timeoutInSeconds() default 0;

    /**
     * Use the {@link RpcPersistenceReadRequest} with the same name to load persistence in RPC methods.
     * Load by default as specified in {@link PersistenceSchema}.
     *
     * @return the name as defined in a {@link RpcPersistenceReadRequest}.
     */
    String rpcPersistenceReadRequestName() default "";
}
