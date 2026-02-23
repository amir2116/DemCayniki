package com.example.demcayniki.domain.entity;

import com.example.demcayniki.model.constants.modifiable.FollowStatus;
import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name ="FOLLOWS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"FOLLOWER_ID", "FOLLOWED_ID"}
        )
)
public class Follow {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "ID")
    protected UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWER_ID", nullable = false)
    private ConsumerUser follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWED_ID", nullable = false)
    private ConsumerUser followed;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS",nullable = false)
    private FollowStatus status;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "LAST_INTERACTION_AT")
    private Instant lastInteractionAt;

}
