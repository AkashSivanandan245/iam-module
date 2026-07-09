# LMS Module 01 — Identity & Access Management

Full-stack workspace for the Xebia LMS IAM module.

## Layout

```
lms-module1/
├── backend/    Spring Boot service (Java 21, PostgreSQL, Redis, JWT RS256)
└── frontend/   Vite + React 18 client
```

## Quick start

```bash
# 1. Start Postgres + Redis
cd backend
docker compose up -d db redis

# 2. Run the backend (port 8080)
./mvnw spring-boot:run

# 3. Run the frontend (port 5173) in a separate terminal
cd ../frontend
cp .env.example .env
npm install
npm run dev
```

Frontend dev server on `http://localhost:5173`, backend API on `http://localhost:8080`.
CORS between them is already configured in `backend/src/main/java/com/xebia/lms/config/SecurityConfig.java`.

## What's new in this build

- **Rate limiting** — Redis-backed fixed-window filter on all auth endpoints (`backend/src/main/java/com/xebia/lms/security/ratelimit/`)
- **Basic frontend** — login, OTP-based password reset, and a dashboard showing the current user and effective permissions
- **429 handling end-to-end** — backend returns `Retry-After`, frontend surfaces it in the UI

See each subfolder's own `README.md` / `HELP.md` for details.
