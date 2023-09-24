import React from 'react'
import axios from "axios";

const handleLoginButtonClick = () => {
    axios.get('http://localhost:8080/api/login').then((res) => {
        // Handle successful login
        window.location.replace(res.data);
    }).catch((error) => {
            // Handle login error
        if (error.response) {
            window.location.replace('/error?message=' + encodeURIComponent(error.response.data.message));
        } else if (error.request) {
            // The request was made but no response was received
            window.location = "/error?message=Server is down";
        } else {
            // Something happened in setting up the request that triggered an Error
            window.location.replace('/error?message=' + encodeURIComponent(error.message));
        }
    });
}

const LoginButton = () => {
    return (
        <button className="btn btn-success btn-lg s-green" onClick={handleLoginButtonClick}>
            Sign in with
            <br/>
            Spotify
        </button>
    );
}

export default function Login() {
    return(
        <div>
            <div>
                <div className="content-container justify-center" style={{ display: 'flex', flexDirection: 'column' }}>
                        <div className="text-center">
                            <div className="sign-in-head">
                                Create a data lineup from your top artists.
                            </div>
                            <div className="query-selection">
                                <div className="sign-in">
                                    Sign in to get started.
                                </div>
                                <LoginButton></LoginButton>
                                <div className="sign-in-policy">
                                    Read our <a className="text-blue-500 underline cursor-pointer" target="_blank"
                                                href="/privacy">Privacy Policy</a>.
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
        </div>
    )
}
