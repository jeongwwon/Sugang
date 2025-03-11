package Hello.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.concurrent.atomic.AtomicInteger;

@Converter(autoApply = true)
public class AtomicIntegerConverter implements AttributeConverter<AtomicInteger, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AtomicInteger attribute) {
        return (attribute != null) ? attribute.get() : null;
    }

    @Override
    public AtomicInteger convertToEntityAttribute(Integer dbData) {
        return (dbData != null) ? new AtomicInteger(dbData) : new AtomicInteger(0);
    }
}

