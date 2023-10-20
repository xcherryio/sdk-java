package io.xdb.core.exception;

import feign.FeignException;
import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.core.exception.status.InvalidRequestException;
import io.xdb.core.exception.status.ProcessAlreadyStartedException;
import io.xdb.core.exception.status.ProcessNotFoundException;
import io.xdb.gen.models.ApiErrorResponse;
import io.xdb.gen.models.EncodedObject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class XDBHttpException extends RuntimeException {

    private final int statusCode;
    private ApiErrorResponse apiErrorResponse;

    public XDBHttpException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(exception);
        statusCode = exception.status();

        String decodeErrorMessage = "";
        final Optional<ByteBuffer> respBody = exception.responseBody();
        if (respBody.isPresent()) {
            final String data = StandardCharsets.UTF_8.decode(respBody.get()).toString();
            try {
                apiErrorResponse = objectEncoder.decode(new EncodedObject().data(data), ApiErrorResponse.class);
                return;
            } catch (final Exception e) {
                decodeErrorMessage = e.getMessage();
            }
        }

        // TODO
        apiErrorResponse =
            new ApiErrorResponse().detail("empty or unable to decode to apiErrorResponse: " + decodeErrorMessage);
    }

    public static XDBHttpException fromFeignException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        if (exception.status() >= 400 && exception.status() < 500) {
            switch (exception.status()) {
                case 400:
                    return new InvalidRequestException(objectEncoder, exception);
                case 404:
                    return new ProcessNotFoundException(objectEncoder, exception);
                case 409:
                    return new ProcessAlreadyStartedException(objectEncoder, exception);
                default:
                    return new ClientSideException(objectEncoder, exception);
            }
        } else {
            return new ServerSideException(objectEncoder, exception);
        }
    }
}
