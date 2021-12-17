package org.dcsa.jit.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("payload")
public class Payload implements Persistable<UUID> {

    @Id
    @Column("payload_id")
    private UUID payloadID;

    @Column("payload")
    private byte[] payload;

    @CreatedDate
    @Column("created_at")
    private OffsetDateTime createdAt;

    @Transient
    private transient boolean isNew;

    public UUID getId() {
        return payloadID;
    }

    public static Payload of(byte[] data) {
        Payload payload = new Payload();
        payload.setPayloadID(UUID.randomUUID());
        payload.setPayload(data);
        payload.setCreatedAt(OffsetDateTime.now());
        payload.setNew(true);
        return payload;
    }
}
