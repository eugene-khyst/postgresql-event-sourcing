package eventsourcing.postgresql.service;

import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.AggregateTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AggregateFactory {

    private final AggregateTypeMapper aggregateTypeMapper;

    @SneakyThrows(ReflectiveOperationException.class)
    @SuppressWarnings("unchecked")
    public <T extends Aggregate> T newInstance(String aggregateType, UUID aggregateId) {
        Class<? extends Aggregate> aggregateClass = aggregateTypeMapper.getClassByAggregateType(aggregateType);
        var constructor = aggregateClass.getDeclaredConstructor(UUID.class, Integer.TYPE);
        return (T) constructor.newInstance(aggregateId, 0);
    }
}
