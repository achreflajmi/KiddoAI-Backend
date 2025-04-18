# 🚀 Welcome to KiddoAI-Backend!

This guide will help you pull and set up the KiddoAI-Backend project from GitHub.

## 📥 Clone the Repository from the Main Branch
To pull the project from the `main` branch, follow these steps:

### ✅ Step 1: Open your terminal or command prompt.
### ✅ Step 2: Run the following command:

git clone -b main https://github.com/achreflajmi/KiddoAI-Backend.git

### ✅ Step 3: Navigate into the project directory:

cd KiddoAI-Backend

---

## 🌿 Pull from a Specific Branch
If you want to pull the project from a different branch instead of `main`, follow these steps:

### ✅ Step 1: Clone the repository (without specifying a branch):

git clone https://github.com/achreflajmi/KiddoAI-Backend.git

### ✅ Step 2: Move into the project directory:

cd KiddoAI-Backend

### ✅ Step 3: List all available branches:

git branch -r

### ✅ Step 4: Checkout the specific branch you want:

git checkout <branch-name>
🔹 Replace `<branch-name>` with the actual branch name you need.

### ✅ Step 5: Pull the latest changes from the branch:

git pull origin <branch-name>

---

## ⚙️ Install Dependencies (For Spring Boot Project)
Since this is a Spring Boot project, follow these steps to install dependencies.

Run the following command to build and install dependencies:

mvn clean install

## ▶️ Run the Backend Server

For **Node.js**, run:
npm start

For **Python (Django/Flask)**, run:
python manage.py runserver  # Django
flask run  # Flask

🎉 **That's it! Now you're ready to work on the backend. Happy coding!** 🚀
