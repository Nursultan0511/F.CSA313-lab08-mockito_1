# F.CSA313-lab08-mockito_1

## Хэсэг A: Найман тест
Нийт тест: 10 (7 энгийн тест + A8 параметржүүлсэн тестийн 3 мөр). `mvn test`: Tests run: 10, Failures: 0, Errors: 0, Skipped: 0.

### A8: lenient() хэрэглэсэн шалтгаан
A8 параметржүүлсэн тестэд мөр бүр өөр stub тохируулдаг тул нэг мөрт ашиглагдаагүй stub үлдэж, Mockito-гийн strict stubs `UnnecessaryStubbingException` шидэх боломжтой. Тиймээс `lenient().when(...)` ашиглан тухайн сценарийд хэрэггүй stub-ыг зөвшөөрсөн.

## Хэсэг B: Тестээ тестлэх — мини мутаци

| № | Мутаци (src/main дээрх эвдрэл) | Failures | Errors | Унасан тестүүд | Гаралтын файл |
|---|---|---|---|---|---|
| 1 | Идэвхгүй оюутны шалгалтыг (`if (!student.active()) ...`) комментлов | 2 | 0 | `register_errorScenarios_returnExpectedCodeWithoutSaveOrEmail`, `register_inactiveStudent_returnsErrorInactiveAndNeverTouchesEmailSender` | `results/mvn-test-mutant-1.txt` |
| 2 | `repository.saveRegistration(...)` дуудалтыг комментлов | 2 | 0 | `register_activeStudentWithAllPrerequisites_returnsOkSavesAndSendsEmail`, `register_emailSenderThrows_stillReturnsOkAndSavesRegistration` | `results/mvn-test-mutant-2.txt` |
| 3 | Эхний if-ээс `|| courseId == null` хэсгийг устгав | 1 | 0 | `register_nullArguments_throwsIllegalArgumentAndTouchesNoDependency` | `results/mvn-test-mutant-3.txt` |

**Дүгнэлт:** гурван мутант бүгд баригдсан (BUILD FAILURE), үл баригдсан мутант байхгүй. Мутант 3-ыг A6 шууд барьсан: A6 нь `studentId`, `courseId` хоёр аргументын null-ийг тус тусад нь шалгадаг тул тестэд нэмэлт засвар хийх шаардлагагүй болсон. Бүх Failures нь assertion/verify-ийн алдаа бөгөөд UnnecessaryStubbingException (Errors) гараагүй.

Эх код бүх мутацийн дараа сэргээгдсэн. Эцсийн `results/mvn-test.txt`: Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, BUILD SUCCESS.
