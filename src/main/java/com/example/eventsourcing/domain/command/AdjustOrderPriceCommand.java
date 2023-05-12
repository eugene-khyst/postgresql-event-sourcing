package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;

import java.math.BigDecimal;
import java.util.UUID;

public final class AdjustOrderPriceCommand extends Command {

    private final BigDecimal newPrice;

    public AdjustOrderPriceCommand(UUID aggregateId,
                                   BigDecimal newPrice) {
        super(AggregateType.ORDER, aggregateId);
        this.newPrice = newPrice;
    }

    public BigDecimal getNewPrice() {
        return this.newPrice;
    }

    public String toString() {
        return "AdjustOrderPriceCommand(super=" + super.toString() + ", newPrice=" + this.getNewPrice() + ")";
    }
}
