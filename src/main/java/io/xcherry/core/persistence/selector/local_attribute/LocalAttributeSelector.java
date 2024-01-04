package io.xcherry.core.persistence.selector.local_attribute;

import io.xcherry.core.encoder.base.ObjectEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalAttributeSelector {

    private final List<LocalAttributeKeyValueSelector> initialWrite;

    /**
     * Create a local attribute selector.
     *
     * @param initialWrite  a list of {@link LocalAttributeKeyValueSelector}.
     * @return the created local attribute selector.
     */
    public static LocalAttributeSelector create(final LocalAttributeKeyValueSelector... initialWrite) {
        return new LocalAttributeSelector(Arrays.stream(initialWrite).collect(Collectors.toList()));
    }

    public Set<String> getKeys() {
        return initialWrite.stream().map(LocalAttributeKeyValueSelector::getKey).collect(Collectors.toSet());
    }

    public io.xcherry.gen.models.LocalAttributeConfig toApiModel(final ObjectEncoder encoder) {
        if (initialWrite.isEmpty()) {
            return null;
        }

        return new io.xcherry.gen.models.LocalAttributeConfig()
            .initialWrite(
                initialWrite
                    .stream()
                    .map(keyValueSelector -> keyValueSelector.toApiModel(encoder))
                    .collect(Collectors.toList())
            );
    }
}
