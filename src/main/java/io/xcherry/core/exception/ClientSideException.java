package io.xcherry.core.exception;

import feign.FeignException;
import io.xcherry.core.encoder.base.ObjectEncoder;

public class ClientSideException extends HttpException {

    public ClientSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }
}
