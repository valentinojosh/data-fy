import React, {useState} from 'react'
import axios from "axios";
const URL = process.env.REACT_APP_URL;

const handleLoginButtonClick = (setIsLoading, isLoading) => {
    if (isLoading) return;

    setIsLoading(true);
    const storedData = localStorage.getItem('objectData');
    if (storedData){
        setIsLoading(false);
        window.location = "/dash";
        return;
    }

    axios.get(`${URL}/api/login`).then((res) => {
        setIsLoading(false);

        if (res.status === 304) {
            window.location.replace('/error?message=304');
        } else {
            window.location.replace(res.data);
        }
    }).catch((error) => {
        setIsLoading(false);

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

const LoginButton = ({ setIsLoading, isLoading }) => {
    return (
        <button className="btn btn-success btn-lg s-green" onClick={() => handleLoginButtonClick(setIsLoading, isLoading)} disabled={isLoading}>
            {isLoading ? "Loading..." : (
                <>
                    Sign in with
                    <br/>
                    Spotify
                </>
            )}
        </button>
    );
}

export default function Login() {
    const [isLoading, setIsLoading] = useState(false);

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
                                <LoginButton setIsLoading={setIsLoading} isLoading={isLoading}/>
                                <div className="sign-in-policy">
                                    Please note: Google Cloud server is configured for cost-efficiency; on your first sign-in, loading may take up to 20 seconds while it initializes
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
        </div>
    )
}
