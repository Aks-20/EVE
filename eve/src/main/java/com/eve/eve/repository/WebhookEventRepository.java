package com.eve.eve.repository;





import com.eve.eve.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO webhook_events
            (event_id, event_type, payload)
        VALUES
            (:eventId, :eventType, :payload)
        ON CONFLICT (event_id) DO NOTHING
        """, nativeQuery = true)
    int insertIfNew(
            @Param("eventId") String eventId,
            @Param("eventType") String eventType,
            @Param("payload") String payload
    );
}