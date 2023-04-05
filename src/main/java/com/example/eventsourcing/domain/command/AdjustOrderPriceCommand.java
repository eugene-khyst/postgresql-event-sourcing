package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class AdjustOrderPriceCommand extends Command {

    private final BigDecimal newPrice;

    public AdjustOrderPriceCommand(UUID aggregateId,
                                   BigDecimal newPrice) {
        super(AggregateType.ORDER, aggregateId);
        this.newPrice = newPrice;
    }
}
