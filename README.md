Программное обеспечение biointerface_standalone предназначено для сбора, хранения, 
отображения и дальнейшей обработки данных полученных с контроллера мышенчной активности:
- [ПП контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_pcb),
- [ПО контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_embedded).

Приложение построено по 3-х слойной архитектуре:
1. Графический интерфейс (UI)
2. Сервисы(services) + обработчик хоста COM-порта(host)
3. Репазитории на основе ORM

В проекте использован стек:
- Java version 16
- UI: Java FX
- ORM: Hibernate
- Logger: slf4j + log4j
- BD: для тестов H2, для runtime SQLite
- COM-порт: jSerialComm
- тесты: junit-jupiter (в разработке)
