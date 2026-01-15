import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

export default function RegistrationPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        fetch('/api/tasks')
            .then(res => {
                if (res.ok) {
                    navigate("/tasks");
                }
            })
    })

    const handleRegistration = async () => {
        console.log(username, password, confirmPassword);

        const loginData = {
            username: username,
            password: password,
            confirmPassword: confirmPassword
        };

        try {
            const response = await fetch('/api/auth/registration', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });
            if (response.ok) {
                const data = await response.json();
                if (data.token) {
                    localStorage.setItem('jwt_token', data.token);
                    navigate("/tasks")
                } else {
                    console.error("Токен не пришёл!", data)
                }
            } else {
                alert("Error occurred!");
            }
        } catch (error) {
            console.error("Ошибка сети: ", error);
        }
    }

    return (
        <div style={{padding: '20px', textAlign: 'center'}}>
            <h1>Registration</h1>

            <div style={{marginBottom: '10px'}}>
                <input
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>

            <div style={{marginBottom: '10px'}}>
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>

            <div style={{marginBottom: '10px'}}>
                <input
                    type="password"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
            </div>

            <button onClick={handleRegistration}>
                Sign up!
            </button>
        </div>
    )
}
