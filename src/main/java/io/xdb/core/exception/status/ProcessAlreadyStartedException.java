package io.xdb.core.exception.status;

import feign.FeignException;
import io.xdb.core.encoder.base.ObjectEncoder;
import io.xdb.core.exception.XDBHttpException;

public class ProcessAlreadyStartedException extends XDBHttpException {

    public ProcessAlreadyStartedException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        super(objectEncoder, exception);
    }
}
