package demago.khjv2.domain.student.presentation;

import demago.khjv2.domain.student.presentation.dto.request.StudentBulkCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentDeleteRequest;
import demago.khjv2.domain.student.presentation.dto.response.ManagementStudentListResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentBulkCreateResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentDeleteResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentResponse;
import demago.khjv2.domain.student.service.StudentQueryService;
import demago.khjv2.domain.student.service.StudentService;
import demago.khjv2.domain.studentDetail.service.StudentDetailsSyncService;
import demago.khjv2.domain.studentGrass.entity.BojGrassHistory;
import demago.khjv2.domain.studentGrass.repository.BojGrassHistoryRepository;
import demago.khjv2.domain.studentGrass.service.StudentGrassSyncService;
import demago.khjv2.global.scheduler.BojDetailsScheduler;
import demago.khjv2.global.scheduler.BojGrassScheduler;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/management/students")
@RequiredArgsConstructor
public class ManagementController {

    private final StudentService studentService;
    private final StudentQueryService studentQueryService;

    private final StudentGrassSyncService studentGrassSyncService;
    private final StudentDetailsSyncService studentDetailsSyncService;

    @GetMapping
    public ResponseEntity<ManagementStudentListResponse> getStudents() {
        return ResponseEntity.ok(studentQueryService.getManagementStudents());
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody final StudentCreateRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<StudentBulkCreateResponse> createBulkStudents(
            @Valid @RequestBody final StudentBulkCreateRequest request
    ) {
        return ResponseEntity.ok(studentService.createBulkStudents(request));
    }

    @DeleteMapping
    public ResponseEntity<StudentDeleteResponse> deleteStudents(@Valid @RequestBody final StudentDeleteRequest request) {
        return ResponseEntity.ok(studentService.deleteStudents(request));
    }

    @GetMapping("/test")
    public void test() {
        studentDetailsSyncService.syncUserDetail();

        studentGrassSyncService.syncGrass();

        studentDetailsSyncService.flame();
    }
}