package io.xcherry.core.exception.status;

import feign.FeignException;
import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.core.exception.HttpException;

public class ProcessNotFoundException extends HttpException {

    public ProcessNotFoundException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        super(objectEncoder, exception);
    }
}
