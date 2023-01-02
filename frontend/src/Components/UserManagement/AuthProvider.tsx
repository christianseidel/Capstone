import {useNavigate} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import AuthContext from "./AuthContext";

interface Token {
    token: string;
}

interface Param {
    children?: any;
}

export default function AuthProvider({children}: Param) {

    const [token, setToken] = useState(localStorage.getItem('jwt') ?? '');

    const nav = useNavigate();

    useEffect(() =>{
        localStorage.setItem('jwt', token);
    }, [token])

    const register = (username: string, password: string, passwordAgain: string) => {
        return fetch (`${process.env.REACT_APP_BASE_URL}/api/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
                passwordAgain: passwordAgain
            })
        })
    }

    const login = (username: string, password: string) => {
        return fetch(`${process.env.REACT_APP_BASE_URL}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    throw Error ('Benutzername oder Passwort ist nicht korrekt');
                } else if (response.status !== 200) {
                    throw Error (response.statusText + '(error code: ' + response.status + ').')
                }
                return response.json();
            })
            .then((token: Token) => setToken(token.token))
            .then(() => localStorage.setItem('jwt', token))
            .then(() => localStorage.setItem('username', username))
    }

    const logout = () => {
        setToken('');
        localStorage.removeItem('jwt');
        localStorage.removeItem('username');
        nav("/users/logout");
    };

    return (
        <AuthContext.Provider
            value={{
                token,
                register,
                login,
                logout
            }} >
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)