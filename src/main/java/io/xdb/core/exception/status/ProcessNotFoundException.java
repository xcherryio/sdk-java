package io.xdb.core.exception.status;

import feign.FeignException;
import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.core.exception.XDBHttpException;

public class ProcessNotFoundException extends XDBHttpException {

    public ProcessNotFoundException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        super(objectEncoder, exception);
    }
}
