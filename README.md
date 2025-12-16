# Skill Share Web Application

A web-based skill-sharing platform that allows users to list their skills, find people with the skills they need, and collaborate on learning and development. Users can also edit their profile, upload their profile image, and manage their listings.

## Features

* **User Registration & Authentication**: Allows users to register, log in, and manage their accounts.
* **Profile Management**: Users can update their personal information, contact details, and bio.
* **Skill Listings**: Users can create, view, and manage skill listings, including offered and needed skills.
* **Search & Matching**: Users can search for skill listings and find matching profiles based on skills.
* **File Uploads**: Allows users to upload profile images using Git Large File Storage (LFS).
* **Theme Toggle**: Users can switch between light and dark modes.

## Technologies Used

* Frontend*:

  * HTML, CSS, JavaScript
  * Frontend framework (if any)
  * Light / Dark theme toggle
  * Fetch API for handling requests
* Backend*:

  * Java (Spring Boot)
  * REST API endpoints for user management, listings, and file uploads
  * MySQL or PostgreSQL (Database of choice)
* File Storage*:

  * Git Large File Storage (LFS) for handling large files like profile images
* Version Control*: Git and GitHub for version control

## Installation

### Clone the repository

```bash
git clone https://github.com/NILESH-KUMAR-coder/SKILL-SHARE-WEB.git
cd SKILL-SHARE-WEB
```

### Backend Setup (Spring Boot)

1. Open the `skill-swap/backend` folder.
2. Build the project using Maven.

```bash
mvn clean install
```

3. Run the Spring Boot application.

```bash
mvn spring-boot:run
```

### Frontend Setup

1. Navigate to the frontend folder.
2. Install dependencies (if you have a package manager like `npm` or `yarn`).

```bash
npm install
```

3. Run the frontend app using the appropriate command (`npm start` or `live-server`).

### Running the Application

* After setting up both the frontend and backend, you can access the skill-sharing web app on your local machine.
* The app will be running at `http://localhost:8080` for the backend and on a local server for the frontend.

## Configuration

This project uses environment variables for sensitive values.

### Required Environment Variables

Before running the backend, make sure the following environment variables are set:

- **DB_PASSWORD** — Database password for MySQL/PostgreSQL
- **JWT_SECRET** — Secret key used for signing JWT tokens

Example (PowerShell on Windows):

```powershell
$env:DB_PASSWORD="your_database_password"
$env:JWT_SECRET="your_jwt_secret"


## Contributing

1. Fork this repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-name`).
6. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
