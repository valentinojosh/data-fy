import React from 'react'
import { Container } from "react-bootstrap"

export default function Privacy() {
    return(
        <div>
            <Container
                className="container-one"

            >
                <div className="privacy-one">
                    <p className="privacy-header">Privacy Policy</p>
                    <br/>
                    <p>Data-fy uses the Spotify Web API to get data from your Spotify profile. By using Data-fy, you agree to the use of your account username and information about your listening history (such as top artists, songs, etc.) as stated by this policy.</p>
                    <br/>
                    <p>The account data used is only stored in local storage on your browser and is removed via logout (button in top right when on dashboard) or by clearing your browser cookies/cache. This data is not shared with any third parties. The information is processed by the server and sent to your local browser for display.</p>
                    <br/>
                    <p>If at any point you wish to remove the permissions for Spotify,
                        you can do so <a target="_blank" rel="noreferrer"
                                                         href="https://support.spotify.com/us/article/spotify-on-other-apps/"
                                                         className="text-blue-500 underline">here</a></p>

                    <br/>
                    <p>Please note: this is a personal project, not at all monetized, and is not well optimized for security. Use at your own discretion. To see a demo or check out the code, check out my <a target="_blank" rel="noreferrer" href="https://joshvalentino.com/projects"
                                                                                                                                                                                                               className="text-blue-500 underline">portfolio</a>!</p>
                </div>

            </Container>
        </div>
    )
}
