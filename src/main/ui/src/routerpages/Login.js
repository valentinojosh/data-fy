import React from 'react'
import { Container } from "react-bootstrap"
import axios from "axios";

//here i can change the redirect to a different url which will route to a different router page
//need to get scope out of here possible later
//const AUTH_URL= "https://accounts.spotify.com/authorize?client_id=1b24d7a17fea44f59005605f6cb96cf2&response_type=code&redirect_uri=http://localhost:3000/&scope=streaming%20user-read-email%20user-read-private%20user-library-read%20user-library-modify%20user-read-playback-state%20user-modify-playback-state"

const handleLoginButtonClick = () => {
    axios.get('http://localhost:8080/api/login').then((res) => {
        // Handle successful login
        window.location.replace(res.data);
    }).catch((error) => {
        // Handle login error
    });
}

const LoginButton = () => {
    return (
        <button className="btn btn-success btn-lg" onClick={handleLoginButtonClick}>
            Login
        </button>
    );
}

export default function Login() {
    return(
        <div>
            <Container
                className="d-flex justify-content-center align-items-center"
                style={{ minHeight: "100vh" }}
            >
                <LoginButton/>
            </Container>
        </div>
    )
}
