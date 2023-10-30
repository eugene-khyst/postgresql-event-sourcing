package eventsourcing.postgresql.domain;

public interface AggregateTypeMapper {

    Class<? extends Aggregate> getClassByAggregateType(String aggregateType);
}
