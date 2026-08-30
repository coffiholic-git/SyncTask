# SyncTask

The app is organized into `frontend` (the browser UI) and `backend` (Spring Boot API with MySQL and Redis).

1. Start Docker Desktop.
2. From `backend`, run `docker compose up --build`.
3. From `frontend`, run `python -m http.server 5500`.
4. Open `http://localhost:5500`.

On the first screen, choose **Sign in** or **Create account**. A successful sign-in opens the projects and tasks workspace. Use `admin@example.com` / `ChangeMe123!` for the seeded administrator account.
