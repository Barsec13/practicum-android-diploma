# EmployMe

EmployMe - приложение для поиска вакансий c использованием [API сервиса HeadHunter](https://github.com/hhru/api).

Этот проект выполнен в качестве дипломной работы в процессе обучения по специальности Android разработчик под руководством наставника. В процессе выполнения проводились кросс-ревью и промежуточные ревью опытными разработчиками.

## Возможности приложения

### Поиск вакансий
Поиск выполняется по тексту, введенному в строку поиска.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/89e09151-a377-4aab-a2b1-10f4b17bac0c" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/a96d2db3-97dc-4c8b-b034-f8d7f43de2de" width=30% height=30%>

### Фильтры для поиска
Для более точного поиска можно настроить фильтрацию.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/f97fa024-10cc-4564-bd9f-aa40e4100bc0" width=30% height=30%>

Фильтрация по стране или по региону (в таком случае страна выбирается автоматически). Страну или регион можно выбрать вручную из списка или с помощью строки поиска.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/3e4d0a34-95db-4a3f-9a6a-c76f47401717" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/70958f59-e035-473a-a66c-475452692c48" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/e4dc28bf-bc72-49ea-8c54-be8da3c8ee0e" width=30% height=30%>

Фильтрация по отрасли. Отрасль можно выбрать вручную из списка или с помощью строки поиска.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/a9951e15-089f-40ea-8a2e-39193d3fcd4e" width=30% height=30%>

Фильтрация по заработной плате. Можно указать минимальную заработную плату, а также скрыть вакансии без указания заработной платы.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/8069e70f-5b43-425e-a353-6796a228333c" width=30% height=30%>

В результате список вакансий сокращается согласно фильтрам.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/5a8cf0ce-8d49-4008-8e49-e74eba03ed3d" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/bd028b60-32b6-4132-8024-0c94b059336b" width=30% height=30%>

### Просмотр вакансии

Если нажать на вакансии в списке, то откроется детальная информация с описанием вакансии и контактами. При необходимости можно поделиться ссылкой на вакансию.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/2e6495fe-c3b8-4dd1-ba99-49d7caf0979a" width=30% height=30%>

При отсутствии интернета отобразится только название вакансии и компании.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/b4e3a052-6694-4f7f-a996-9d754621e649" width=30% height=30%>

### Избранные вакансии

Если нажать на "Сердечко", вакансия добавится в список избранных. Избранные вакансии отображаются на вкладке "Избранное". При отсуствии интернета вся информация избранной вакансии останется доступной.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/9b1e9dc2-cd58-4fe0-915d-1bc9f8e8c8ba" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/d3ccdc36-009e-47ab-8cd2-d6256862da5f" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/668549b9-7dde-44bf-b02b-1e9768f70264" width=30% height=30%>

### Похожие вакансии
На экране просмотра вакансии можно выполнить поиск похожих вакансий.

<img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/5a0b2a68-a2ca-4086-8511-42b1700c2689" width=30% height=30%> <img src="https://github.com/Barsec13/practicum-android-diploma/assets/32902739/8ab52194-7a4d-46e8-b751-777fd6b11361" width=30% height=30%>

## Используемый стек
API hh.ru, XML, Retrofit, RecyclerView, SharedPreferences, SQLite, Room, Clean Architecture, MVVM, Coroutines Flow, Koin, Jetpack Navigation Component, Fragment, Okhttp, BottomNavigationView, Fragment, ViewPager2, TabLayout.


## Общие требования
- Приложение должно поддерживать устройства, начиная с Android 8.0 (minSdkVersion = 26)
- Приложение поддерживает только портретную ориентацию (portrait), при перевороте экрана ориентация не меняется.

## Над проектом работали:
Алексей Путилов,
Герман Заговенко,
Дмитрий Харакшинов,
Ринат Гиниятуллин.
