package io.xcherry.core.worker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WorkerServiceResponseEntity {

    public static final int HTTP_STATUS_FAILED_DEPENDENCY = 424;

    private final int statusCode;
    private final Object body;

    public static final WorkerServiceResponseEntity ok(final Object body) {
        return new WorkerServiceResponseEntity(200, body);
    }
}
