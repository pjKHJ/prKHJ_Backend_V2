package demago.khjv2.domain.student.presentation;

import demago.khjv2.domain.student.presentation.dto.request.StudentBulkCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentDeleteRequest;
import demago.khjv2.domain.student.presentation.dto.response.StudentBulkCreateResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentDeleteResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentResponse;
import demago.khjv2.domain.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2/management/students")
@RequiredArgsConstructor
public class ManagementController {

    private final StudentService studentService;


    //단건 학생 등록
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody final StudentCreateRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }


    //일괄 학생 등록
    @PostMapping("/bulk")
    public ResponseEntity<StudentBulkCreateResponse> createBulkStudents(
            @Valid @RequestBody final StudentBulkCreateRequest request
    ) {
        return ResponseEntity.ok(studentService.createBulkStudents(request));
    }

    //학생 삭제(ids 기반).
    @DeleteMapping
    public ResponseEntity<StudentDeleteResponse> deleteStudents(@Valid @RequestBody final StudentDeleteRequest request) {
        return ResponseEntity.ok(studentService.deleteStudents(request));
    }
}
