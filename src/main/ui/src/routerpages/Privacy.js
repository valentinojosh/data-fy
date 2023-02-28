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
                    <p>Logo uses the Spotify Web API to get data from your Spotify profile. By using Logo, you agree to the use of your account username and information about your top listened-to artists as stated by this policy.</p>
                    <br/>
                    <p>None of the account data used by Logo is stored or collected, and it is not shared with any third parties. The information is only used on your personal device to generate your data visualization.</p>
                    <br/>
                    <p>If at any point you wish to remove Logo's permissions to generate your graphic on Spotify,
                        you can do so <a target="_blank" rel="noreferrer"
                                                         href="https://support.spotify.com/us/article/spotify-on-other-apps/"
                                                         className="text-blue-500 underline">here</a></p>
                    <br/>
                    <p>For any questions, please <a target="_blank" rel="noreferrer" href="https://linktr.ee/valentinojosh"
                                                                    className="text-blue-500 underline">contact us</a>
                    </p>
                </div>

            </Container>
        </div>
    )
}
