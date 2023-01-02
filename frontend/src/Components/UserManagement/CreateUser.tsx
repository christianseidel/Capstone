import {useNavigate} from "react-router-dom";
import React, {FormEvent, useEffect, useState} from "react";
import {useAuth} from "./AuthProvider";
import icon_eyes from "../../Media/Images/eyes.png";
import {useTranslation} from "react-i18next";
import deFlag from "../../Media/Images/de.png";
import enFlag from "../../Media/Images/en.png";
import i18n from "i18next";

function CreateUser() {

    const nav = useNavigate();
    const {t} = useTranslation();
    const {login} = useAuth();

    const [username, setUsername] = useState(localStorage.getItem('registerUsername') ?? '');
    const [passwordAgain, setPasswordAgain] = useState('');
    const [password, setPassword] = useState('');
    const [showPasswordToggle, setShowPasswordToggle] = useState('password');
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        if (localStorage.getItem('jwt')) {
            nav('/expenses')
        }
    }, [nav])

    useEffect(() => {
        localStorage.setItem('registerUsername', username);
    }, [username]);

    useEffect(() => {
        setErrorMessage('');}, [])

    const {register} = useAuth();

    function doRegister(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        register(username, password, passwordAgain)
            .then(response => {
                if (response.status === 400) {
                    setErrorMessage('Die Passwörter stimmen nicht überein.');
//                    clearForm();
//                    nav('/users/login');
                } else if (response.status === 409) {
                    setErrorMessage('Der gewählte Benutzername ist bereits vergeben.');
//                    clearForm();
//                    nav('/users/login');
                }
            })
           .then(clearForm)
           .then(() => login(username, password))
           .then(() => nav('/expenses'));
    }

    const clearForm = () => {
        localStorage.removeItem('registerUsername');
    }

    function cancelCreation() {
        clearForm();
        nav('/users/login');
    }

    function setLanguage() {
        if (localStorage.getItem('i18nextLng') === 'en') {
            i18n.changeLanguage('de');
        } else {
            i18n.changeLanguage('en');
        }
    }

    return (
        <div>
            <div className={'heading'}>
            <h2>{t('register-page_title')}</h2>
            <span><img
                src={(localStorage.getItem('i18nextLng') === 'en') ? deFlag : enFlag} width={'28px'} height={'28px'}
                alt={'set to English / Deutsch auswählen'} onClick={() => setLanguage()}/>
            </span>

            </div>

            {errorMessage && <div className="error">{errorMessage}</div>}

            <form onSubmit={ev => doRegister(ev)}>
                <input type="text" placeholder={t('input-form_name')} value={username} autoFocus required
                       onChange={ev => setUsername(ev.target.value)}/>
                <input type={showPasswordToggle} placeholder={t('input-form_password')} value={password} required
                       onChange={ev => setPassword(ev.target.value)}/>
                <input type={showPasswordToggle} placeholder={t('input-form_passwordAgain')} value={passwordAgain} required
                       onChange={ev => setPasswordAgain(ev.target.value)}/>

                <button id={"edit-button"} type="submit"> &#10004; {t('button_create')}</button>

            </form>

            <div>
                <button id={"showPassword-button"} type="submit"
                        onClick={() => ((showPasswordToggle==='text')
                            ? setShowPasswordToggle('password')
                            : setShowPasswordToggle("text"))}>
                    {showPasswordToggle==='text' && <> <span id={'hidePassword'}> &#10005;&#10005;&nbsp;</span> {t('button_hidePassword')}</>}
                    {showPasswordToggle==='password' && <><img id={'showPassword-button-icon'} src={icon_eyes} alt={'edit item'} /> {t('button_showPassword')}</>}
                </button>
            </div>

            <div>
                <button id={"cancel-button"} type="submit" onClick={cancelCreation}>
                    &#10008; {t('button_cancel')}
                </button>
            </div>

        </div>
    )
}

export default CreateUser