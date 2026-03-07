package demago.khjv2.domain.studentDetail.entity;

import demago.khjv2.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudentDetails {

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_student_detail_student"))
    private Student student;

    @Column(nullable = false)
    private Integer tier;

    @Column(name = "solved_total", nullable = false)
    private Integer solvedTotal;

    @Column(name = "solved_today", nullable = false)
    private Integer solvedToday;

    @Column(name = "accuracy_pct", nullable = false)
    private Integer accuracyPct;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak;

    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak;

    @Column(nullable = false)
    private Integer flame;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static StudentDetails empty(Student student) {
        return StudentDetails.builder()
                .student(student)
                .tier(0)
                .solvedTotal(0)
                .solvedToday(0)
                .accuracyPct(0)
                .currentStreak(0)
                .longestStreak(0)
                .flame(0)
                .build();
    }

    public void applyFromApi(Integer tier,
                             Integer solvedTotal,
                             Integer solvedToday,
                             Integer accuracyPct,
                             Integer currentStreak,
                             Integer longestStreak,
                             Integer flame) {
        this.tier = tier;
        this.solvedTotal = solvedTotal;
        this.solvedToday = solvedToday;
        this.accuracyPct = accuracyPct;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.flame = flame;
    }
}
