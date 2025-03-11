package Hello.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StringToAtomicIntegerConverter implements Converter<String, AtomicInteger> {
    @Override
    public AtomicInteger convert(String source) {
        return new AtomicInteger(Integer.parseInt(source));
    }
}
