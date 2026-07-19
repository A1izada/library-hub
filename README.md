# LibraryHub

Mikroservis arxitekturası əsasında qurulmuş kitabxana idarəetmə sistemi. Layihə iki müstəqil Spring Boot servisindən ibarətdir: **user-service** (istifadəçi idarəetməsi, autentifikasiya) və **book-service** (kitab kataloqu, ödünc alma məntiqi).

## Arxitektura

```
library-hub/
├── user-service/     → İstifadəçilər, autentifikasiya, JWT (port 8081)
├── book-service/      → Kitablar, kateqoriyalar, borrow məntiqi (port 8082)
└── docker-compose.yml → PostgreSQL x2, RabbitMQ, Redis
```

**İstifadə olunan texnologiyalar:**
- Java 21, Spring Boot 4.1
- Spring Security + JWT (autentifikasiya, rol əsaslı icazə)
- Spring Data JPA + Liquibase (verilənlər bazası, migration idarəetməsi)
- PostgreSQL (hər servisin öz bazası — Database per Service prinsipi)
- Redis (kitab məlumatlarının keşlənməsi)
- RabbitMQ (servislərarası asinxron hadisə ötürülməsi)
- Spring AOP (loglama)
- Spring Scheduling (cron job-lar — overdue yoxlanışı, deaktiv istifadəçi hesabatı)
- Spock (Groovy) — unit testlər

## Quraşdırma və işə salma

### Ön şərtlər
- Java 21 (JDK)
- Docker Desktop
- Maven (IDE-nin daxili Maven-i kifayətdir)

### 1. İnfrastrukturu işə sal

Layihənin kök qovluğunda (`library-hub/`):

```bash
docker-compose up -d
docker ps
```

Bu, 4 konteyner işə salmalıdır: `postgres-users`, `postgres-books`, `rabbitmq`, `redis`.

### 2. user-service-i işə sal

`user-service/` qovluğunda, IDE-dən `UserServiceApplication`-ı Run et (və ya `mvn spring-boot:run`).

Servis `http://localhost:8081` ünvanında işə düşəcək. Liquibase, `users` və `borrow_history` cədvəllərini avtomatik yaradacaq.

### 3. book-service-i işə sal

`book-service/` qovluğunda, IDE-dən `BookServiceApplication`-ı Run et.

Servis `http://localhost:8082` ünvanında işə düşəcək. Liquibase, `categories`, `books`, `borrows` cədvəllərini avtomatik yaradacaq.

**Diqqət:** hər iki servisin `application.yml` faylında `jwt.secret` dəyəri **eyni** olmalıdır — user-service tokeni yaradır, book-service onu yoxlayır.

## API Endpoint-ləri

### user-service (`http://localhost:8081`)

| Metod | Endpoint | Təsvir | İcazə |
|---|---|---|---|
| POST | `/api/auth/register` | Qeydiyyat | Açıq |
| POST | `/api/auth/login` | Giriş, JWT token alma | Açıq |
| GET | `/api/users/me` | Öz profilini görmək | Giriş edilmiş |
| PUT | `/api/users/me` | Profili yeniləmək | Giriş edilmiş |
| GET | `/api/users/me/borrows` | Öz ödünc tarixçəsi | Giriş edilmiş |
| GET | `/api/users` | Bütün istifadəçilər (səhifələnmiş) | ADMIN |
| GET | `/api/users/{id}` | İstifadəçi məlumatı | ADMIN |
| DELETE | `/api/users/{id}` | İstifadəçini deaktiv etmək | ADMIN |

### book-service (`http://localhost:8082`)

| Metod | Endpoint | Təsvir | İcazə |
|---|---|---|---|
| GET | `/api/books/{id}` | Kitab məlumatı (Redis keşli) | Açıq |
| GET | `/api/books/search` | Kitab axtarışı (title/author/categoryId, səhifələnmiş) | Açıq |
| POST | `/api/books` | Yeni kitab | ADMIN |
| PUT | `/api/books/{id}` | Kitabı yeniləmək | ADMIN |
| DELETE | `/api/books/{id}` | Kitabı silmək | ADMIN |
| GET | `/api/categories` | Kateqoriyalar | Açıq |
| POST | `/api/categories` | Yeni kateqoriya | ADMIN |
| POST | `/api/borrows` | Kitab götürmək | Giriş edilmiş |
| PUT | `/api/borrows/{id}/return` | Kitabı qaytarmaq | Giriş edilmiş |
| GET | `/api/borrows/my` | Öz aktiv borrow-ları | Giriş edilmiş |

## Əsas biznes qaydaları

- Qeydiyyatda rol həmişə `USER`-dir; `ADMIN` yalnız bazada əl ilə təyin edilir.
- Bir istifadəçi eyni kitabı ikinci dəfə (əvvəlkini qaytarmadan) götürə bilməz — `409 Conflict`.
- Kitabın əlçatan nüsxəsi (`availableCopies`) 0-dırsa, borrow rədd edilir.
- Kitab götürülüb/qaytarılanda, book-service RabbitMQ vasitəsilə user-service-ə hadisə göndərir, user-service öz `borrow_history` cədvəlini avtomatik yeniləyir.
- Kitab məlumatları Redis-də keşlənir; yeniləmə/silmə/borrow əməliyyatlarında müvafiq keş qeydi təmizlənir (evict).

## Test etmək

Hər iki servisdə Spock (Groovy) ilə yazılmış unit testlər var:

```bash
# IDE-də, test faylının üzərindən Run et, və ya:
mvn test
```

- `user-service`: `UserServiceSpec` — qeydiyyat və giriş ssenariləri
- `book-service`: `BookServiceSpec` — kitab CRUD və tapılmama halları

## Fərdi qeyd

Bu layihə, backend inkişafında (Spring Boot, JWT, mikroservis kommunikasiyası, RabbitMQ, Redis) təcrübə qazanmaq məqsədilə addım-addım qurulub. Hər bir texnologiya seçimi (məsələn, servislərarası ID-lərin `@ManyToOne` əvəzinə sadə `Long` kimi saxlanması) məhz mikroservis arxitekturasının "hər servisin öz bazası" prinsipinə əsaslanır.
