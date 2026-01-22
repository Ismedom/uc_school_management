package com.example.school_mangement_system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.school_mangement_system.config.SecurityConfig;
import com.example.school_mangement_system.controller.*;
import com.example.school_mangement_system.dto.*;
import com.example.school_mangement_system.entity.*;
import com.example.school_mangement_system.repository.*;
import com.example.school_mangement_system.service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

class TestDataFactory {

    static SchoolClass schoolClass(Long id, String name) {
        SchoolClass sc = new SchoolClass();
        sc.setId(id);
        sc.setName(name);
        sc.setCreatedAt(LocalDateTime.now());
        sc.setUpdatedAt(LocalDateTime.now());
        return sc;
    }

    static Section section(Long id, String name, SchoolClass sc) {
        Section s = new Section();
        s.setId(id);
        s.setName(name);
        s.setSchoolClass(sc);
        return s;
    }

    static User user(Long id, String username, boolean active) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPassword("enc");
        u.setRole(Role.STUDENT);
        u.setActive(active);
        return u;
    }

    static Student student(Long id, String name, Section section, User user) {
        Student st = new Student();
        st.setId(id);
        st.setUser(user);
        st.setName(name);
        st.setSection(section);
        st.setActive(true);
        return st;
    }

    static Subject subject(Long id, String name, SchoolClass sc, Teacher teacher) {
        Subject s = Subject.builder().id(id).name(name).schoolClass(sc).teacher(teacher).build();
        return s;
    }

    static Teacher teacher(Long id, String name) {
        Teacher t = new Teacher();
        t.setId(id);
        t.setName(name);
        return t;
    }

    static Exam exam(Long id, String name, SchoolClass sc) {
        Exam e = Exam.builder().id(id).name(name).schoolClass(sc).build();
        return e;
    }

    static Marks marks(
        Long id,
        Exam exam,
        Subject subject,
        Student student,
        double obtained,
        double max,
        String grade
    ) {
        Marks m = new Marks();
        m.setId(id);
        m.setExam(exam);
        m.setSubject(subject);
        m.setStudent(student);
        m.setMarksObtained(obtained);
        m.setMaxMarks(max);
        m.setGrade(grade);
        return m;
    }

    static Attendance attendance(Long id, Student student, LocalDate date, AttendanceStatus status, String remarks) {
        Attendance a = new Attendance();
        a.setId(id);
        a.setStudent(student);
        a.setDate(date);
        a.setStatus(status);
        a.setRemarks(remarks);
        return a;
    }
}

// =================== Service unit tests (with mocked repositories) ===================

class SubjectServiceTest {

    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final SchoolClassRepository schoolClassRepository = mock(SchoolClassRepository.class);
    private final TeacherRepository teacherRepository = mock(TeacherRepository.class);

    private final SubjectService service = new SubjectService(
        subjectRepository,
        schoolClassRepository,
        teacherRepository
    );

    @Test
    @DisplayName("getAllSubjects maps to response")
    void getAllSubjects() {
        SchoolClass sc = TestDataFactory.schoolClass(1L, "Class 1");
        Teacher t = TestDataFactory.teacher(2L, "Mr T");
        Subject subj = TestDataFactory.subject(3L, "Math", sc, t);
        when(subjectRepository.findAll()).thenReturn(List.of(subj));

        List<SubjectResponse> responses = service.getAllSubjects();

        assertThat(responses).hasSize(1);
        SubjectResponse r = responses.get(0);
        assertThat(r.getId()).isEqualTo(3L);
        assertThat(r.getClassId()).isEqualTo(1L);
        assertThat(r.getTeacherName()).isEqualTo("Mr T");
    }

    @Test
    @DisplayName("createSubject saves with class and optional teacher")
    void createSubject() {
        SchoolClass sc = TestDataFactory.schoolClass(1L, "Class 1");
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(sc));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(TestDataFactory.teacher(2L, "Mr T")));

        SubjectRequest req = SubjectRequest.builder().name("Math").classId(1L).teacherId(2L).build();
        service.createSubject(req);

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(captor.capture());
        Subject saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Math");
        assertThat(saved.getSchoolClass().getId()).isEqualTo(1L);
        assertThat(saved.getTeacher().getId()).isEqualTo(2L);
    }
}

class SchoolClassServiceTest {

    private final SchoolClassRepository repo = mock(SchoolClassRepository.class);
    private final SectionRepository sectionRepo = mock(SectionRepository.class);
    private final StudentRepository studentRepo = mock(StudentRepository.class);
    private final SchoolClassService service = new SchoolClassService(repo, sectionRepo, studentRepo);

    @Test
    void createAndUpdateAndDelete() {
        SchoolClassRequest req = SchoolClassRequest.builder().name("JSS1").build();
        service.createClass(req);
        verify(repo).save(any(SchoolClass.class));

        SchoolClass existing = TestDataFactory.schoolClass(10L, "JSS1");
        when(repo.findById(10L)).thenReturn(Optional.of(existing));
        service.updateClass(10L, SchoolClassRequest.builder().name("JSS2").build());
        assertThat(existing.getName()).isEqualTo("JSS2");
        verify(repo, times(2)).save(any(SchoolClass.class));

        service.deleteClass(10L);
        verify(repo).deleteById(10L);
    }
}

class ExamServiceTest {

    private final ExamRepository examRepository = mock(ExamRepository.class);
    private final SchoolClassRepository classRepository = mock(SchoolClassRepository.class);
    private final ExamService service = new ExamService(examRepository, classRepository);

    @Test
    void createAndUpdateExam() {
        SchoolClass sc = TestDataFactory.schoolClass(1L, "C1");
        when(classRepository.findById(1L)).thenReturn(Optional.of(sc));
        ExamRequest req = ExamRequest.builder().name("Midterm").classId(1L).build();
        service.createExam(req);
        verify(examRepository).save(any(Exam.class));

        Exam existing = TestDataFactory.exam(5L, "Midterm", sc);
        when(examRepository.findById(5L)).thenReturn(Optional.of(existing));
        service.updateExam(5L, ExamRequest.builder().name("Final").classId(1L).build());
        assertThat(existing.getName()).isEqualTo("Final");
        verify(examRepository, times(2)).save(any(Exam.class));
    }
}

class AttendanceServiceTest {

    private final AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final AttendanceService service = new AttendanceService(
        attendanceRepository,
        studentRepository,
        subjectRepository
    );

    @Test
    void getAttendanceBySectionAndDate_mapsExistingAndNulls() {
        SchoolClass sc = TestDataFactory.schoolClass(1L, "C1");
        Section sec = TestDataFactory.section(2L, "A", sc);
        Student s1 = TestDataFactory.student(3L, "Alice", sec, TestDataFactory.user(100L, "alice", true));
        Student s2 = TestDataFactory.student(4L, "Bob", sec, TestDataFactory.user(101L, "bob", true));
        when(studentRepository.findBySectionId(2L)).thenReturn(List.of(s1, s2));

        LocalDate date = LocalDate.now();
        Attendance a1 = TestDataFactory.attendance(10L, s1, date, AttendanceStatus.PRESENT, "On time");
        when(attendanceRepository.findByStudentIdAndDate(3L, date)).thenReturn(Optional.of(a1));
        when(attendanceRepository.findByStudentIdAndDate(4L, date)).thenReturn(Optional.empty());

        List<AttendanceResponse> list = service.getAttendanceBySectionAndDate(2L, date);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(list.get(1).getStatus()).isNull();
    }

    @Test
    void saveBulkAttendance_upsertsRecords() {
        AttendanceBulkRequest req = new AttendanceBulkRequest();
        req.setSectionId(2L);
        LocalDate date = LocalDate.now();
        req.setDate(date);

        AttendanceBulkRequest.StudentAttendanceRequest r1 = new AttendanceBulkRequest.StudentAttendanceRequest();
        r1.setStudentId(3L);
        r1.setStatus(AttendanceStatus.ABSENT);
        r1.setRemarks("Sick");
        req.setRecords(List.of(r1));

        when(attendanceRepository.findByStudentIdAndDate(3L, date)).thenReturn(Optional.empty());
        when(studentRepository.findById(3L)).thenReturn(
            Optional.of(TestDataFactory.student(3L, "Alice", null, TestDataFactory.user(1L, "alice", true)))
        );

        service.saveBulkAttendance(req);
        verify(attendanceRepository).save(any(Attendance.class));
    }
}

class MarksServiceTest {

    private final MarksRepository marksRepository = mock(MarksRepository.class);
    private final ExamRepository examRepository = mock(ExamRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final MarksService service = new MarksService(
        marksRepository,
        examRepository,
        studentRepository,
        subjectRepository
    );

    @Test
    void saveBulkMarks_calculatesGrades() {
        Exam exam = TestDataFactory.exam(1L, "Midterm", null);
        Subject subject = TestDataFactory.subject(2L, "Math", null, null);
        Student student = TestDataFactory.student(3L, "Alice", null, TestDataFactory.user(1L, "alice", true));
        when(examRepository.findById(1L)).thenReturn(Optional.of(exam));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(studentRepository.findById(3L)).thenReturn(Optional.of(student));
        when(marksRepository.findAll()).thenReturn(Collections.emptyList());

        MarksRequest mr = MarksRequest.builder().studentId(3L).marksObtained(85.0).maxMarks(100.0).build();
        MarksBulkRequest br = new MarksBulkRequest();
        br.setExamId(1L);
        br.setSubjectId(2L);
        br.setMarks(List.of(mr));

        service.saveBulkMarks(br);

        ArgumentCaptor<Marks> captor = ArgumentCaptor.forClass(Marks.class);
        verify(marksRepository).save(captor.capture());
        Marks saved = captor.getValue();
        assertThat(saved.getGrade()).isEqualTo("A");
    }
}

// =================== Controller MVC tests ===================

@WebMvcTest(controllers = SubjectController.class)
@Import(SecurityConfig.class)
class SubjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SubjectService subjectService;

    @MockBean
    SchoolClassService schoolClassService;

    @MockBean
    TeacherService teacherService;

    @Test
    void listSubjects_requiresAuth_thenOk() throws Exception {
        given(subjectService.getAllSubjects()).willReturn(Collections.emptyList());
        mockMvc
            .perform(get("/subjects").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("subjects/list"));
    }

    @Test
    void createSubject_redirects() throws Exception {
        mockMvc
            .perform(
                post("/subjects/create").with(user("admin").roles("ADMIN")).param("name", "Math").param("classId", "1")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects"));
        verify(subjectService).createSubject(any(SubjectRequest.class));
    }
}

@WebMvcTest(controllers = SchoolClassController.class)
@Import(SecurityConfig.class)
class SchoolClassControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SchoolClassService schoolClassService;

    @Test
    void listClasses_ok() throws Exception {
        given(schoolClassService.getAllClasses()).willReturn(Collections.emptyList());
        mockMvc
            .perform(get("/classes").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("classes/list"));
    }

    @Test
    void createClass_redirects() throws Exception {
        mockMvc
            .perform(post("/classes/create").with(user("admin").roles("ADMIN")).param("name", "JSS1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/classes"));
        verify(schoolClassService).createClass(any(SchoolClassRequest.class));
    }
}

@WebMvcTest(controllers = ExamController.class)
@Import(SecurityConfig.class)
class ExamControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ExamService examService;

    @MockBean
    MarksService marksService;

    @MockBean
    SchoolClassRepository schoolClassRepository;

    @MockBean
    SubjectRepository subjectRepository;

    @MockBean
    StudentRepository studentRepository;

    @Test
    void listExams_ok() throws Exception {
        given(examService.getAllExams()).willReturn(Collections.emptyList());
        mockMvc
            .perform(get("/exams").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("exams/index"));
    }

    @Test
    void createExam_redirects() throws Exception {
        mockMvc
            .perform(post("/exams/create").with(user("admin").roles("ADMIN")).param("name", "Midterm"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/exams"));
        verify(examService).createExam(any(ExamRequest.class));
    }
}

@WebMvcTest(controllers = AttendanceController.class)
@Import(SecurityConfig.class)
class AttendanceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AttendanceService attendanceService;

    @MockBean
    SectionRepository sectionRepository;

    @Test
    void showAttendanceSelector_ok() throws Exception {
        given(sectionRepository.findAll()).willReturn(Collections.emptyList());
        mockMvc
            .perform(get("/attendance").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("attendance/index"));
    }

    @Test
    void markAttendance_ok() throws Exception {
        given(attendanceService.getAttendanceBySectionAndDate(anyLong(), any(LocalDate.class))).willReturn(
            Collections.emptyList()
        );
        given(sectionRepository.findById(1L)).willReturn(Optional.empty());
        mockMvc
            .perform(
                get("/attendance/mark")
                    .with(user("admin").roles("ADMIN"))
                    .param("sectionId", "1")
                    .param("date", LocalDate.now().toString())
            )
            .andExpect(status().isOk())
            .andExpect(view().name("attendance/mark"));
    }

    @Test
    void saveAttendance_redirects() throws Exception {
        mockMvc
            .perform(post("/attendance/mark").with(user("admin").roles("ADMIN")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/attendance?success=true"));
        verify(attendanceService).saveBulkAttendance(any(AttendanceBulkRequest.class));
    }
}

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserService userService;

    @Test
    void signup_ok() throws Exception {
        mockMvc
            .perform(
                post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"u\",\"password\":\"p\"}")
            )
            .andExpect(status().isOk());
        verify(userService).registerUser(any(SignUpRequest.class));
    }

    @Test
    void login_ok() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("u", "p", List.of());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"u\",\"password\":\"p\"}")
            )
            .andExpect(status().isOk());
        verify(authenticationManager).authenticate(any(Authentication.class));
    }
}
