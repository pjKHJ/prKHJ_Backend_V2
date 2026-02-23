package demago.khjv2.domain.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(
        name = "student",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_student_student_no", columnNames = {"student_no"}),
                @UniqueConstraint(name = "uk_student_boj_id", columnNames = {"boj_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Student {

    //공백 제거 하는 메소드
    //이 함수는 name/bojId같은거 들어오면 공백 제거해서 return 해주는 메소드이다
    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }


    //학생 추가 하기 전에 공백 제거하는 메소드
    @PrePersist
    @PreUpdate
    private void normalizeBeforeWrite() {
        this.name = normalize(this.name);
        this.bojId = normalize(this.bojId);
    }



    //학생의 고유 번호, 기본 키임
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //학생의 이름, 최대 10자
    @Column(length = 10, nullable = false)
    private String name;

    //학생의 학번
    //수정 못하게 만듬
    @Column(name = "student_no", nullable = false, updatable = false, columnDefinition = "MEDIUMINT")
    private Integer studentNo;

    //학생의 백준 ID
    @Column(name = "boj_id", length = 50, nullable = false)
    private String bojId;


    //신규 학생 생성
    public static Student create(String name, Integer studentNo, String bojId) {
        return Student.builder()
                .name(normalize(name))
                .studentNo(studentNo)
                .bojId(normalize(bojId))
                .build();
    }


    //두 Stduent 객체가 똑같은지 아닌지 판단하는 메소드이다
    @Override
    public final boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null) {
            return false;
        }
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Student other = (Student) o;
        return id != null && Objects.equals(id, other.id);
    }

    // 객체를 해시 값으로 변환해 컬렉션에서 빠른 검색을 위한 메서드
    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    //student 객체 출력할때 보기 좋게 만들어주는 메소드이다
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", studentNo=" + studentNo +
                ", bojId='" + bojId + '\'' +
                '}';
    }


}
//2. 관계 매핑 누락 (STUDENT_DETAIL 및 STUDENT_GRASS) 문제는 상관 없다고 함