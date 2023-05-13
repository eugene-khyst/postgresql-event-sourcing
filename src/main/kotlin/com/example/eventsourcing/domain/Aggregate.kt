package com.example.eventsourcing.domain;

import com.example.eventsourcing.domain.command.Command;
import com.example.eventsourcing.domain.event.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Aggregate {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Aggregate.class);
    protected final UUID aggregateId;
    @JsonIgnore
    protected final List<Event> changes = new ArrayList<>();

    protected int version;
    @JsonIgnore
    protected int baseVersion;

    protected Aggregate(UUID aggregateId, int version) {
        this.aggregateId = aggregateId;
        this.baseVersion = this.version = version;
    }

    public abstract AggregateType getAggregateType();

    public void loadFromHistory(List<Event> events) {
        if (!changes.isEmpty()) {
            throw new IllegalStateException("Aggregate has non-empty changes");
        }
        events.forEach(event -> {
            if (event.getVersion() <= version) {
                throw new IllegalStateException(
                        "Event version %s <= aggregate base version %s".formatted(
                                event.getVersion(), getNextVersion()));
            }
            apply(event);
            baseVersion = version = event.getVersion();
        });
    }

    protected int getNextVersion() {
        return version + 1;
    }

    protected void applyChange(Event event) {
        if (event.getVersion() != getNextVersion()) {
            throw new IllegalStateException(
                    "Event version %s doesn't match expected version %s".formatted(
                            event.getVersion(), getNextVersion()));
        }
        apply(event);
        changes.add(event);
        version = event.getVersion();
    }

    private void apply(Event event) {
        log.debug("Applying event {}", event);
        invoke(event, "apply");
    }

    public void process(Command command) {
        log.debug("Processing command {}", command);
        invoke(command, "process");
    }

    private void invoke(Object o, String methodName) {
        try {
            Method method = this.getClass().getMethod(methodName, o.getClass());
            method.invoke(this, o);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Aggregate %s doesn't support %s(%s)".formatted(
                            this.getClass(), methodName, o.getClass().getSimpleName()),
                    e);
        }
    }

    public UUID getAggregateId() {
        return this.aggregateId;
    }

    public List<Event> getChanges() {
        return this.changes;
    }

    public int getVersion() {
        return this.version;
    }

    public int getBaseVersion() {
        return this.baseVersion;
    }

    public String toString() {
        return "Aggregate(aggregateId=" + this.getAggregateId() + ", changes=" + this.getChanges() + ", version=" + this.getVersion() + ", baseVersion=" + this.getBaseVersion() + ")";
    }
}
