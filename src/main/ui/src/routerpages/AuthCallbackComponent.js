import React, { useEffect } from 'react';
import axios from 'axios';

const AuthCallbackComponent = () => {

    useEffect(() => {
        const exchangeCodeForToken = async () => {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            const error = urlParams.get('error');
            console.log(code);
            console.log(error);
            const URL = process.env.REACT_APP_URL;

            if(!code){
                window.location.replace('/error?message=No callback made');
            }

            if(error){
                window.location.replace('/error?message=' + encodeURIComponent(error));
            }

                    if (code) {
                try {
                    const response = await axios.post(`${URL}/api/token`, { code });
                    // Save the tokens in local storage or state management
                    localStorage.setItem('spotify_token', JSON.stringify(response.data));
                    // Redirect to the dashboard or other page
                    window.location.replace('/dash');
                } catch (error) {
                    // console.error('Error exchanging the code for tokens:', error);
                    // Handle error or redirect to an error page
                    window.location.replace('/error?message=' + encodeURIComponent(error.message));
                }
            }
        };

        //IIFE (Immediately Invoked Function Expression) pattern adapted for async functions within useEffect
        exchangeCodeForToken().then(() => console.log('Code exchange completed'))
            .catch(error => window.location.replace('/error?message=' + encodeURIComponent(error.message)));
    }, []);

    // Render loading indicator while the exchange is happening
    return <div className="loading-container"><div className="customize">Loading...</div></div>;
};

export default AuthCallbackComponent;
