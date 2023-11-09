# tinkoff-course-work
Курсовая работа финтех: Java-разработчик  
  
## Настройки  
Для работы auth сервера надо в файле hosts добавить запись:  
``127.0.0.1 auth.localhost``  
После этого nginx будет проксировать запросы с auth.localhost/* на auth сервер  
  
## Запуск
Приложение собирается с помощью docker-compose  
``docker-compose up --build``  

## Полезное  
Swagger документация для auth сервера:  
``auth.localhost/swagger-ui.html``
