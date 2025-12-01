package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "dispatch_assignment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_batch",
                        columnNames = {"dispatch_order_id", "batch_no"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 출동명령의 배치인지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_order_id", nullable = false)
    private DispatchOrder order;

    /** 1차, 2차, 3차 ... 배치 번호 */
    @Column(name = "batch_no", nullable = false)
    private Integer batchNo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
