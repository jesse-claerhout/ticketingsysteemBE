package be.optis.opticketapi.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class BarSeparatedStringToListConverter implements Converter<String, List<String>> {

    @Override
    public List<String> convert(String string) {
        if (string.isBlank()) return Collections.emptyList();
        return Arrays.asList(string.split("\\|"));
    }
}
