package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;
    CourseRegistrationService service;

    private static final String SID = "B232270039";
    private static final String COURSE = "F.CSA313";
    private static final String PREREQ = "F.CSA201";
    private static final String EMAIL = "student@must.edu.mn";

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    private Student student(boolean active, List<String> coursesTaken) {
        return new Student(SID, EMAIL, active, coursesTaken);
    }

    // A1
    @Test
    void register_activeStudentWithAllPrerequisites_returnsOkSavesAndSendsEmail() {
        when(repository.findStudent(SID)).thenReturn(student(true, List.of(PREREQ)));
        when(repository.courseExists(COURSE)).thenReturn(true);
        when(repository.prerequisites(COURSE)).thenReturn(List.of(PREREQ));

        String result = service.register(SID, COURSE);

        assertEquals("OK", result);
        verify(repository).saveRegistration(SID, COURSE);
        verify(emailSender).send(eq(EMAIL), anyString(), contains(COURSE));
    }

    // A2
    @Test
    void register_unknownStudent_returnsErrorNoStudentAndNeitherSavesNorEmails() {
        when(repository.findStudent(SID)).thenReturn(null);

        String result = service.register(SID, COURSE);

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    // A3
    @Test
    void register_inactiveStudent_returnsErrorInactiveAndNeverTouchesEmailSender() {
        when(repository.findStudent(SID)).thenReturn(student(false, List.of()));

        String result = service.register(SID, COURSE);

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
    }

    // A4
    @Test
    void register_missingPrerequisites_returnsErrorPrerequisitesAndDoesNotSave() {
        when(repository.findStudent(SID)).thenReturn(student(true, List.of()));
        when(repository.courseExists(COURSE)).thenReturn(true);
        when(repository.prerequisites(COURSE)).thenReturn(List.of(PREREQ));

        String result = service.register(SID, COURSE);

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }

    // A5
    @Test
    void register_emailSenderThrows_stillReturnsOkAndSavesRegistration() {
        when(repository.findStudent(SID)).thenReturn(student(true, List.of(PREREQ)));
        when(repository.courseExists(COURSE)).thenReturn(true);
        when(repository.prerequisites(COURSE)).thenReturn(List.of(PREREQ));
        doThrow(new RuntimeException("SMTP down"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        String result = service.register(SID, COURSE);

        assertEquals("OK", result);
        verify(repository).saveRegistration(SID, COURSE);
    }

    // A6
    @Test
    void register_nullArguments_throwsIllegalArgumentAndTouchesNoDependency() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, COURSE));
        assertThrows(IllegalArgumentException.class, () -> service.register(SID, null));

        verifyNoInteractions(repository, emailSender);
    }

    // A7
    @Test
    void register_success_emailBodyContainsCourseCode() {
        when(repository.findStudent(SID)).thenReturn(student(true, List.of(PREREQ)));
        when(repository.courseExists(COURSE)).thenReturn(true);
        when(repository.prerequisites(COURSE)).thenReturn(List.of(PREREQ));
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        service.register(SID, COURSE);

        verify(emailSender).send(eq(EMAIL), anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains(COURSE),
                "И-мэйлийн бие хичээлийн кодыг агуулсан байх ёстой");
    }

    // A8: 3 мөр = 3 тест
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "NO_STUDENT,ERROR_NO_STUDENT",
            "INACTIVE,ERROR_INACTIVE_STUDENT",
            "NO_PREREQ,ERROR_PREREQUISITES"
    })
    void register_errorScenarios_returnExpectedCodeWithoutSaveOrEmail(
            String scenario, String expectedCode) {

        switch (scenario) {
            case "NO_STUDENT" ->
                    lenient().when(repository.findStudent(SID)).thenReturn(null);
            case "INACTIVE" ->
                    lenient().when(repository.findStudent(SID))
                            .thenReturn(student(false, List.of()));
            case "NO_PREREQ" -> {
                lenient().when(repository.findStudent(SID))
                        .thenReturn(student(true, List.of()));
                lenient().when(repository.courseExists(COURSE)).thenReturn(true);
                lenient().when(repository.prerequisites(COURSE)).thenReturn(List.of(PREREQ));
            }
            default -> fail("Үл мэдэгдэх сценари: " + scenario);
        }

        assertEquals(expectedCode, service.register(SID, COURSE));
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }
}
