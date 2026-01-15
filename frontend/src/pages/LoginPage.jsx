import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("jwt_token");
        if (token) {
            navigate('/tasks');
        }
    }, [navigate]);


    const handleLogin = async () => {
        console.log(username, password);

        const loginData = {
            username: username,
            password: password,
        };

        try {
            const response = await fetch('/api/auth/login', {
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
            console.error("Ошщибка сети: ", error);
        }
    }

        return (
            <div style={{padding: '20px', textAlign: 'center'}}>
                <h1>Вход в систему</h1>

                <div style={{marginBottom: '10px'}}>
                    <input
                        placeholder="Login"
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

                <button onClick={handleLogin}>
                    Войти
                </button>
            </div>
        )
}
