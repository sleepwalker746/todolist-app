import { useState } from 'react'
import telegramLogo from '../assets/telegram-svgrepo-com.svg'
import githubLogo from '../assets/github-mark.svg'
import linkedinLogo from '../assets/Linkedin SVG Icon.svg'

function HomePage() {
    const [count, setCount] = useState(0)

    return (
        <>
            <div>
                <a href="https://t.me/s/ice_blue_0" target="_blank">
                    <img src={telegramLogo}
                         className="logo react"
                         alt="Telegram logo"
                         width="100"
                    />
                </a>
                <a href="https://github.com/sleepwalker746" target="_blank">
                    <img src={githubLogo}
                         className="logo react"
                         alt="GitHub logo"
                         width="100"/>
                </a>
                <a href="https://www.linkedin.com/in/arsenii-sidorovych-25603330b/" target="_blank">
                    <img src={linkedinLogo}
                         className="logo react"
                         alt="Linkedin logo"
                         width="100"/>
                </a>
            </div>
            <h1>Август + & Co </h1>
            <div className="card">
                <button onClick={() => setCount((count) => count + 1)}>
                    Счётчик {count}
                </button>
                <p>
                    Подробнее в <code>src/HomePage.jsx</code>
                </p>
            </div>
            <p className="read-the-docs">
                Никуда переходить не надо
            </p>
        </>
    )
}
export default HomePage;