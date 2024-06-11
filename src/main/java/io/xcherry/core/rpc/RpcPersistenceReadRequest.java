package io.xcherry.core.rpc;

import io.xcherry.core.persistence.readrequest.AppDatabaseReadRequest;
import io.xcherry.core.persistence.readrequest.LocalAttributeReadRequest;
import io.xcherry.gen.models.LockType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcPersistenceReadRequest {

    private final String name;
    private final AppDatabaseReadRequest appDatabaseReadRequest;
    private final LocalAttributeReadRequest localAttributeReadRequest;

    /**
     * Create a persistence read request used in @RPC methods.
     *
     * @param name                          the name to be used by @RPC methods.
     * @param appDatabaseReadRequest        app database read request.
     * @param localAttributeReadRequest     local attribute read request.
     * @return
     */
    public static RpcPersistenceReadRequest create(
        final String name,
        final AppDatabaseReadRequest appDatabaseReadRequest,
        final LocalAttributeReadRequest localAttributeReadRequest
    ) {
        return new RpcPersistenceReadRequest(name, appDatabaseReadRequest, localAttributeReadRequest);
    }

    /**
     * Create a persistence read request used in @RPC methods.
     *
     * @param name                          the name to be used by @RPC methods.
     * @param appDatabaseReadRequest        app database read request.
     * @return
     */
    public static RpcPersistenceReadRequest create(
        final String name,
        final AppDatabaseReadRequest appDatabaseReadRequest
    ) {
        return RpcPersistenceReadRequest.create(
            name,
            appDatabaseReadRequest,
            LocalAttributeReadRequest.create(LockType.NO_LOCKING)
        );
    }
}
