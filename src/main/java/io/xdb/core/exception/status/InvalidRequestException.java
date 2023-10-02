package io.xdb.core.exception.status;

import feign.FeignException;
import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.core.exception.XDBHttpException;

public class InvalidRequestException extends XDBHttpException {

    public InvalidRequestException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        super(objectEncoder, exception);
    }
}
