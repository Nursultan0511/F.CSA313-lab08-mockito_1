# F.CSA313-lab08-mockito_1

## Хэсэг A: Найман тест
Нийт тест: 10 (7 энгийн тест + A8 параметржүүлсэн тестийн 3 мөр). `mvn test`: Tests run: 10, Failures: 0, Errors: 0, Skipped: 0.

### A8: lenient() хэрэглэсэн шалтгаан
A8 параметржүүлсэн тестэд мөр бүр өөр stub тохируулдаг тул нэг мөрт ашиглагдаагүй stub үлдэж, Mockito-гийн strict stubs `UnnecessaryStubbingException` шидэх боломжтой. Тиймээс `lenient().when(...)` ашиглан тухайн сценарийд хэрэггүй stub-ыг зөвшөөрсөн.
