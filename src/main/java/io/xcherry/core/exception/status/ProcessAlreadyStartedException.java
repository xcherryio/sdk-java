package io.xcherry.core.exception.status;

import feign.FeignException;
import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.core.exception.HttpException;

public class ProcessAlreadyStartedException extends HttpException {

    public ProcessAlreadyStartedException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        super(objectEncoder, exception);
    }
}
