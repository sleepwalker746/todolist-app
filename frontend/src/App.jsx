import { Routes, Route } from 'react-router-dom'
import './App.css'
import LoginPage from "./pages/LoginPage.jsx";
import HomePage from "./pages/HomePage.jsx";
import TasksPage from "./pages/TasksPage.jsx";
import RegistrationPage from "./pages/RegistrationPage.jsx";

function App() {

  return (
    <>
        <Routes>
            <Route path="/" element={<HomePage />} />

            <Route path="/login" element={<LoginPage />} />

            <Route path="/tasks" element={<TasksPage />} />

            <Route path="/registration" element={<RegistrationPage />} />

            <Route path="*" element={<h1> 404 - Not Found</h1>} />
        </Routes>
    </>
  )
}

export default App
