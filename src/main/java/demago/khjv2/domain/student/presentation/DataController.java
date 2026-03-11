package demago.khjv2.domain.student.presentation;

import demago.khjv2.domain.student.presentation.dto.response.data.ClassCompetitionResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentDetailResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentGrassResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentListResponse;
import demago.khjv2.domain.student.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/data/students")
@RequiredArgsConstructor
public class DataController {

    private final StudentQueryService studentQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentDetailResponse> getStudentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(studentQueryService.getStudentDetail(id));
    }

    @GetMapping("/{id}/grass")
    public ResponseEntity<StudentGrassResponse> getStudentGrass(
            @PathVariable Long id,
            @RequestParam(name = "period", required = false) Integer period
    ) {
        return ResponseEntity.ok(studentQueryService.getStudentGrass(id, period));
    }

    @GetMapping
    public ResponseEntity<StudentListResponse> getStudents() {
        return ResponseEntity.ok(studentQueryService.getStudents());
    }

    @GetMapping("/class")
    public ResponseEntity<ClassCompetitionResponse> getClassCompetition() {
        return ResponseEntity.ok(studentQueryService.getClassCompetition());
    }
}
