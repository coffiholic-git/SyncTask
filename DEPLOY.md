# Deploy SyncTask on Railway

This project is packaged as one web service: the Spring Boot app serves both the API and browser UI.  It still needs a MySQL database and Redis.

## What you need

1. A free GitHub account: https://github.com/signup
2. A Railway account, created with **Continue with GitHub**: https://railway.app

## Publish the code to GitHub

1. Create an empty GitHub repository named `synctask` (do not add a README or .gitignore).
2. In PowerShell, open this folder and run the three commands below, replacing `YOUR-USERNAME`:

```powershell
git init
git add .
git commit -m "Deploy SyncTask"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/synctask.git
git push -u origin main
```

## Create services in Railway

1. Select **New Project** → **Deploy from GitHub repo** → choose `synctask`.
2. Select **New** → **Database** → **MySQL**.
3. Select **New** → **Database** → **Redis**.
4. Open the app service’s **Variables** tab and add the following. Choose the corresponding values from the MySQL and Redis services’ **Variables** tabs:

| App variable | Value |
| --- | --- |
| `DB_HOST` | the MySQL host |
| `DB_PORT` | the MySQL port |
| `DB_NAME` | the MySQL database name |
| `DB_USERNAME` | the MySQL username |
| `DB_PASSWORD` | the MySQL password |
| `REDIS_HOST` | the Redis host |
| `REDIS_PORT` | the Redis port |
| `JWT_SECRET` | a unique random value at least 32 characters long |
| `BOOTSTRAP_ADMIN_PASSWORD` | a new strong password (not `ChangeMe123!`) |

5. Railway builds and deploys automatically. In the app service, select **Settings** → **Networking** → **Generate Domain**, then open that URL.

After publishing, sign in with `admin@example.com` and the password you set in `BOOTSTRAP_ADMIN_PASSWORD`.

## Local test

From `backend`, run `docker compose up --build`. In a second terminal, change to `frontend` and run `python -m http.server 5500`; then visit `http://localhost:5500`. The frontend is included in the deployed container, so the second step is only needed locally.
