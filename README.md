# Репозиторий для проекта  Explore-with-me

### <a href="https://github.com/IgorAtafev/java-explore-with-me/pull/5">Pull request</a>

### Фича "Комментирование"

#### Private
**POST /users/{userId}/comments** — Добавление текущим пользователем нового комментария к событию  
**PATCH /users/{userId}/comments/{commentId}** — Изменение текущим пользователем комментария к событию  
**DELETE /users/{userId}/comments/{commentId}** — Удаление текущим пользователем комментария к событию  
**GET /users/{userId}/comments** — Получение текущим пользователем своих комментариев  
**GET /users/{userId}/comments/{commentId}** — Получение текущим пользователем полной информации о своем комментарии  

#### Admin
**DELETE /admin/comments/{commentId}** — Удаление админом комментария к событию  
**GET /admin/comments** — Поиск админом комментариев  

#### Public  
**GET /events/{eventId}/comments** — Получение комментариев к событию  
**GET /events/{eventId}/comments/{commentId}** — Получение информации о комментарии к событию  