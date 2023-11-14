package io.xdb.core.exception;

import feign.FeignException;
import io.xdb.core.encoder.base.ObjectEncoder;

public class ClientSideException extends XDBHttpException {

    public ClientSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }
}
