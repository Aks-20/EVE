package com.eve.eve.entity;



import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "webhook_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_webhook_event_id",
                        columnNames = "event_id"
                )
        }
)
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private String eventId;

    @Column(
            name = "event_type",
            nullable = false
    )
    private String eventType;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String payload;

    @Column(
            name = "processed_at",
            nullable = false
    )
    private LocalDateTime processedAt;

    protected WebhookEvent() {
    }

    public WebhookEvent(
            String eventId,
            String eventType,
            String payload
    ) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
    }

    @PrePersist
    protected void onCreate() {
        processedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
