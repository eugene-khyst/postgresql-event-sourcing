package eventsourcing.postgresql.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Validated
@Configuration
@ConfigurationProperties(prefix = "event-sourcing")
public class EventSourcingProperties {

    private static final SnapshottingProperties NO_SNAPSHOTTING = new SnapshottingProperties(false, 0);

    @Valid
    @NestedConfigurationProperty
    @Setter
    private Map<String, SnapshottingProperties> snapshotting = new HashMap<>();

    public SnapshottingProperties getSnapshotting(String aggregateType) {
        return snapshotting.getOrDefault(aggregateType, NO_SNAPSHOTTING);
    }

    public record SnapshottingProperties(
            boolean enabled,
            @Min(2)
            int nthEvent
    ) {
    }
}