import React from 'react'
import { Container } from "react-bootstrap"

//here i can change the redirect to a different url which will route to a different router page
const AUTH_URL= "https://accounts.spotify.com/authorize?client_id=1b24d7a17fea44f59005605f6cb96cf2&response_type=code&redirect_uri=http://localhost:3000/&scope=streaming%20user-read-email%20user-read-private%20user-library-read%20user-library-modify%20user-read-playback-state%20user-modify-playback-state"


export default function Login() {
    return(
        <div>
            <Container
                className="d-flex justify-content-center align-items-center"
                style={{ minHeight: "100vh" }}
            >
                <a className="btn btn-success btn-lg" href={AUTH_URL}>
                    Login With Spotify
                </a>

                <a className="btn btn-success btn-lg" href="/redi">
                    Test Endpoint Redirect
                </a>
            </Container>
        </div>
    )
}
