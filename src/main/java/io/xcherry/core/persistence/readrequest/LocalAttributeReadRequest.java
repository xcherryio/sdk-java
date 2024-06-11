package io.xcherry.core.persistence.readrequest;

import com.google.common.collect.ImmutableSet;
import io.xcherry.gen.models.LockType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalAttributeReadRequest {

    private final LockType lockType;
    private final Set<String> keysToReadNoLock;
    private final Set<String> keysToReadWithLock;

    /**
     * Create a local attribute read request.
     *
     * @param lockType              locking type applied to the keysToReadWithLock.
     * @param keysToReadNoLock      keys to read without locking.
     * @param keysToReadWithLock    keys to read with the lockingType.
     * @return the created local attribute read request.
     */
    public static LocalAttributeReadRequest create(
        final LockType lockType,
        final Set<String> keysToReadNoLock,
        final Set<String> keysToReadWithLock
    ) {
        return new LocalAttributeReadRequest(lockType, keysToReadNoLock, keysToReadWithLock);
    }

    /**
     * Create a local attribute read request.
     *
     * @param lockType              locking type applied to the keysToReadWithLock.
     * @param keysToReadNoLock      keys to read without locking.
     * @return the created local attribute read request.
     */
    public static LocalAttributeReadRequest create(final LockType lockType, final String... keysToReadNoLock) {
        return LocalAttributeReadRequest.create(
            lockType,
            Arrays.stream(keysToReadNoLock).collect(Collectors.toSet()),
            ImmutableSet.of()
        );
    }

    public Set<String> getKeys() {
        final HashSet<String> keys = new HashSet<>(keysToReadNoLock);
        keys.addAll(keysToReadWithLock);
        return keys;
    }

    public io.xcherry.gen.models.LoadLocalAttributesRequest toApiModel() {
        return new io.xcherry.gen.models.LoadLocalAttributesRequest()
            .lockType(lockType)
            .keysToLoadNoLock(new ArrayList<>(keysToReadNoLock))
            .keysToLoadWithLock(new ArrayList<>(keysToReadWithLock));
    }
}
