import React from 'react'
import axios from "axios";
const URL = process.env.REACT_APP_URL;

const handleLoginButtonClick = () => {
    let storedData = null;
    try {
        storedData = JSON.parse(localStorage.getItem("objectData"));
        console.log("data:");
        console.log(storedData);
    } catch (error) {
        console.error("Error parsing objectData from localStorage:", error);
    }

    // checks if the data is present in local storage, preventing unnecessary API calls
    if(!storedData) {
        axios.get(`${URL}/api/login`).then((res) => {
            // Set window to Spotify Auth Request URI
            console.log("hi");
            console.log(res.data);
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
    else{
        window.location = "/dash";
    }
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
                                Visualize your Spotify data
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
