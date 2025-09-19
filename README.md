# BakuBaku

BakuBaku is a full-stack application for validating XML documents against XSDs, with:
- Backend: JVM/Scala service (SBT project) using a standard `conf/` setup for configuration and routes.
- Frontend: React + TypeScript app powered by Vite, located in `ui/`, bundled for production and served by the backend in packaged builds.

The project is set up for local development, CI publishing, and Docker-based deployment.

---

## Features

- Modern React (TypeScript, Vite) frontend
- Backend service with typed configuration and conventional logging
- Integrated UI build into SBT packaging workflow
- Ready-to-use Dockerfile
- CI workflow for publishing

---

## Tech Stack

- Java 21 (JDK 21)
- Scala (SBT build)
- Node.js (npm), React 19, Vite 7, TypeScript 5.8
- ESLint 9 for linting
- Docker (optional for deployment)

---

## Repository Layout

- `app/`, `conf/`, `public/`: Backend source, configuration, and public assets
- `ui/`: Frontend (React + Vite + TypeScript)
- `test/`: Backend tests and sample resources
- `project/`: SBT project files and UI build hooks
- `.github/workflows/`: CI configuration
- `Dockerfile`: Container build
- `build.sbt`, `ui-build.sbt`: Build configuration

---

## Prerequisites

- Java 21 (JDK 21)
- SBT
- Node.js (LTS 18+ recommended) and npm

Optional:
- Docker

---

## Getting Started

### 1) Clone and install

```shell script
git clone https://github.com/megafarad/bakubaku.git
cd bakubaku
```


### 2) Run the backend (SBT)

```shell script
sbt run
```


This starts the backend service (commonly on http://localhost:9000 unless configured otherwise) and the frontend dev instance on http://localhost:5173.

---

## Configuration

- Application settings: `conf/application.conf`
- Logging: `conf/logback.xml`
- Routes: `conf/routes`

You can override configuration via environment variables or an external config file, depending on your runtime setup. For packaging/deployment, ensure any required secrets or environment-specific settings are provided at runtime.

---

## Building

### Frontend only

```shell script
cd ui
npm run build
```


### Backend (packaged)

From the project root:
```shell script
sbt clean compile
sbt test
sbt stage      # or sbt dist
```


The UI build is integrated into the SBT pipeline via the project’s UI build hooks, so packaged artifacts will include the compiled frontend assets.

---

## Running Tests

Backend tests:
```shell script
sbt test
```


Frontend linting:
```shell script
cd ui
npm run lint
```


---

## Docker

Build the image:
```shell script
docker build -t bakubaku:latest .
```


Run the container:
```shell script
docker run --rm -p 9000:9000 bakubaku:latest
```


Adjust ports and environment variables as needed for your deployment environment.

---

## Scripts Reference (Frontend)

From `ui/`:
- `npm run dev` – Run Vite dev server
- `npm run build` – TypeScript build and Vite production bundle
- `npm run preview` – Preview the production build locally
- `npm run lint` – Lint the frontend code

---

## Common Issues & Tips

- Port conflicts:
    - Backend commonly on 9000
    - Frontend dev server commonly on 5173
    - Close or reassign ports if already in use.
- Node binary mismatch:
    - Use an LTS Node version (18+). Clear `node_modules` and reinstall if needed.
- Integrated builds:
    - When using `sbt stage` or `sbt dist`, the UI is built and bundled; you don’t need to run the Vite dev server for production.

---

## Contributing

- Create a feature branch
- Commit with clear messages
- Ensure `sbt test` and `npm run lint` pass
- Open a pull request

---

## License

MIT