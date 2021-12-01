# Технологии проекта:
Spring MVC, Maven, Hibernate, Git.

##Задание:
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


##Были реализованы следующие ключевые моменты:
В проекте используется сущность Player, которая имеет поля:
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
Бизнесс-логика сохранения персонажа:
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

##Проблемы и решения в проекте
