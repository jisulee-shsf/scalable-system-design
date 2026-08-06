package board.common.outboxmessagerelay;

import board.common.event.Event;
import board.common.event.EventPayload;
import board.common.event.EventType;
import board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRelayEventPublisher {

    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType eventType, EventPayload eventPayload, Long shardKey) {
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                eventType,
                Event.of(
                        eventIdSnowflake.nextId(),
                        eventType,
                        eventPayload
                ).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
