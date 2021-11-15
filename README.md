# тестовое javarush

Нужно дописать приложение для администратора сетевой ролевой игры, где он сможет редактировать
параметры персонажей (игроков), и раздавать баны. Должны быть реализованы следующие
возможности:
1. получать список всех зарегистрированных игроков;
2. создавать нового игрока;
3. редактировать характеристики существующего игрока;
4. удалять игрока;
5. получать игрока по id;
6. получать отфильтрованный список игроков в соответствии с переданными фильтрами;
7. получать количество игроков, которые соответствуют фильтрам.
Для этого необходимо реализовать REST API в соответствии с документацией.
В проекте должна использоваться сущность Player, которая имеет поля:
Long id ID игрока
String name Имя персонажа (до 12 знаков включительно)
String title Титул персонажа (до 30 знаков включительно)
Race race Расса персонажа
Profession profession Профессия персонажа
Integer experience Опыт персонажа. Диапазон значений 0..10,000,000
Integer level Уровень персонажа
Integer untilNextLevel Остаток опыта до следующего уровня
Date birthday Дата регистрации
Диапазон значений года 2000..3000 включительно
Boolean banned Забанен / не забанен
Также должна присутствовать бизнес-логика:
Перед сохранением персонажа в базу данных (при добавлении нового или при апдейте характеристик
существующего), должны высчитываться:
- текущий уровень персонажа
- опыт необходимый для достижения следующего уровня
и сохраняться в БД. Текущий уровень персонажа рассчитывается по формуле:
𝐿 =
√2500 + 200·exp − 50
100
,
где:
exp — опыт персонажа.
Опыт до следующего уровня рассчитывается по формуле:
𝑁 = 50 ∙ (𝑙𝑣𝑙 + 1) ∙ (𝑙𝑣𝑙 + 2) − 𝑒𝑥𝑝 ,
где:
lvl — текущий уровень персонажа;
exp — опыт персонажа.



URL /rest/players
Method GET
URL Params Optional:
String name,
String title,
Race race,
Profession profession,
Long after,
Long before,
Boolean banned,
Integer minExperience,
Integer maxExperience,
Integer minLevel,
Integer maxLevel,
PlayerOrder order,
Integer pageNumber,
Integer pageSize
Data Params None
Success Response Code: 200 OK
Content: [
{
 “id”:[Long],
 “name”:[String],
 “title”:[String],
 “race”:[Race],
 “profession”:[Profession],
 “birthday”:[Long],
 “banned”:[Boolean],
 “experience”:[Integer],
 “level”:[Integer],
 “untilNextLevel”:[Integer]
},
…
]
Notes Поиск по полям name и title происходить по частичному
соответствию. Например, если в БД есть игрок с именем
«Камираж», а параметр name задан как «ир» - такой игрок
должен отображаться в результатах (Камираж).
pageNumber – параметр, который отвечает за номер
отображаемой страницы при использовании пейджинга.
Нумерация начинается с нуля
pageSize – параметр, который отвечает за количество
результатов на одной странице при пейджинге
Get players count
URL /rest/players/count
Method GET
URL Params Optional:
String name,
String title,
Race race,
Profession profession,
Long after,
Long before,
Boolean banned,
Integer minExperience,
Integer maxExperience,
Integer minLevel,
Integer maxLevel
Data Params None
Success Response Code: 200 OK
Content: Integer
Notes
Create player
URL /rest/players
Method POST
URL Params None
Data Params {
 “name”:[String],
 “title”:[String],
 “race”:[Race],
 “profession”:[Profession],
 “birthday”:[Long],
 “banned”:[Boolean], --optional, default=false
 “experience”:[Integer]
}
Success Response Code: 200 OK
Content: {
 “id”:[Long],
 “name”:[String],
 “title”:[String],
 “race”:[Race],
 “profession”:[Profession],
 “birthday”:[Long],
 “banned”:[Boolean],
 “experience”:[Integer],
 “level”:[Integer],
 “untilNextLevel”:[Integer]
}
Notes Мы не можем создать игрока, если:
- указаны не все параметры из Data Params (кроме banned);
- длина значения параметра “name” или “title” превышает
размер соответствующего поля в БД (12 и 30 символов);
- значение параметра “name” пустая строка;
- опыт находится вне заданных пределов;
- “birthday”:[Long] < 0;
- дата регистрации находятся вне заданных пределов.
В случае всего вышеперечисленного необходимо ответить
ошибкой с кодом 400.
Get player
URL /rest/players/{id}
Method GET
URL Params id
Data Params None
Success Response Code: 200 OK
Content: {
 “id”:[Long],
 “name”:[String],
 “title”:[String],
 “race”:[Race],
 “profession”:[Profession],
 “birthday”:[Long],
 “banned”:[Boolean],
 “experience”:[Integer],
 “level”:[Integer],
 “untilNextLevel”:[Integer]
}
Notes Если игрок не найден в БД, необходимо ответить ошибкой с
кодом 404.
Если значение id не валидное, необходимо ответить ошибкой
с кодом 400.
Update player
URL /rest/players/{id}
Method POST
URL Params id
Data Params {
 “name”:[String], --optional
 “title”:[String], --optional
 “race”:[Race], --optional
 “profession”:[Profession], --optional
 “birthday”:[Long], --optional
 “banned”:[Boolean], --optional
 “experience”:[Integer] --optional
}
Success Response Code: 200 OK
Content: {
 “id”:[Long],
 “name”:[String],
 “title”:[String],
 “race”:[Race],
 “profession”:[Profession],
 “birthday”:[Long],
 “banned”:[Boolean],
 “experience”:[Integer],
 “level”:[Integer],
 “untilNextLevel”:[Integer]
}
Notes Обновлять нужно только те поля, которые не null.
Если игрок не найден в БД, необходимо ответить ошибкой с
кодом 404.
Если значение id не валидное, необходимо ответить ошибкой
с кодом 400.
Delete player
URL /rest/players/{id}
Method DELETE
URL Params id
Data Params
Success Response Code: 200 OK
Notes Если игрок не найден в БД, необходимо ответить ошибкой с
кодом 404.
Если значение id не валидное, необходимо ответить ошибкой
с кодом 400.
