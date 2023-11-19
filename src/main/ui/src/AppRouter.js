import {BrowserRouter, Routes, Route} from "react-router-dom";
import Login from "./routerpages/Login";
import Dash from "./routerpages/Dash";
import "./css.css"
import "./components/modal/modal.css"
import SpotifyLogo from "./Spotify_Logo_RGB_Black.png"
import React, {useState} from "react";
import {Container, Navbar} from "react-bootstrap";
import "./components/modal/modal.css"
import Privacy from "./routerpages/Privacy";
import Error from "./routerpages/Error";
import AuthCallbackComponent from "./routerpages/AuthCallbackComponent";

const handleLogoutButtonClick = () => {
    sessionStorage.clear();
    localStorage.clear();
    window.location.replace('/');
}


const AppRouter = () => {
    const [modal, setModal] = useState(false);

    const toggleModal = () => {
        setModal(!modal);
    };

    function navigateHome() {
        window.location.replace('/');
    }

    return (
        <div>
            {modal && (
                <div className="modal-one">
                    <div onClick={toggleModal} className="overlay"/>
                    <div className="modal-content-one">
                        <span style={{fontWeight:700}}>Data-fy</span>
                        <br/>
                        <p>
                            Data-fy is a Spotify data visualization app based on your personal account. You can customize parameters such as time range, top artists, top songs, etc.                        </p>
                        <br/>
                        <p>For information on how Data-fy handles your profile data, please refer to our <a
                            className="text-blue-500 underline cursor-pointer" target="_blank" href="/privacy">Privacy
                            Policy</a>
                        </p>
                        <br/>
                        <p>Back end designed, programmed, and tested by <a target="_blank" rel="noreferrer" href="https://linktr.ee/valentinojosh" className="text-blue-500 underline">Josh Valentino</a></p>
                        <p>Front end developed and tested by <a target="_blank" rel="noreferrer" href="https://linktr.ee/valentinojosh" className="text-blue-500 underline">Josh Valentino</a>, mimicking the design of <a target="_blank" rel="noreferrer" href="https://www.instafest.app/" className="text-blue-500 underline">InstaFest</a></p>


                        <div className="flex justify-center">
                            <button className="close-modal" onClick={toggleModal}>
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}


            <BrowserRouter>
                <Navbar className="nav-class px-2 sm:px-4 py-2">
                    <Container>

                        <Container className="d-flex justify-content-center align-items-center">
                            <button className="i-button" onClick={toggleModal}>
                                <svg stroke="currentColor" fill="currentColor" strokeWidth="0" viewBox="0 0 24 24" height="25" width="25">
                                    <path d="M12 2C6.486 2 2 6.486 2 12s4.486 10 10 10 10-4.486 10-10S17.514 2 12 2zm0 18c-4.411 0-8-3.589-8-8s3.589-8 8-8 8 3.589 8 8-3.589 8-8 8z"/>
                                    <path d="M11 11h2v6h-2zm0-4h2v2h-2z"/>
                                </svg>
                            </button>

                            <div className="flex-1 text-center font-['Ganache'] logo-text">
                                <span onClick={navigateHome} style={{ cursor: 'pointer' }}>Data-fy</span>
                            </div>

                            {((window.location.pathname !== "/") && (window.location.pathname !== "/privacy")) && (
                            <button className="logout-button" onClick={handleLogoutButtonClick}>
                                <svg stroke="currentColor" fill="currentColor" strokeWidth="0" viewBox="0 0 24 24" height="25" width="25">
                                    <path d="m2 12 5 4v-3h9v-2H7V8z"/>
                                    <path d="M13.001 2.999a8.938 8.938 0 0 0-6.364 2.637L8.051 7.05c1.322-1.322 3.08-2.051 4.95-2.051s3.628.729 4.95 2.051 2.051 3.08 2.051 4.95-.729 3.628-2.051 4.95-3.08 2.051-4.95 2.051-3.628-.729-4.95-2.051l-1.414 1.414c1.699 1.7 3.959 2.637 6.364 2.637s4.665-.937 6.364-2.637c1.7-1.699 2.637-3.959 2.637-6.364s-.937-4.665-2.637-6.364a8.938 8.938 0 0 0-6.364-2.637z"/>
                                </svg>
                            </button>
                            )}
                        </Container>

                    </Container>
                </Navbar>
                <hr className="one"/>

                <Routes>
                    <Route path="/" element={<Login/>} />
                    <Route path="/dash" element={<Dash/>}/>
                    <Route path="/privacy" element={<Privacy/>}/>
                    <Route path="/auth-callback" element={<AuthCallbackComponent/>}/>
                    <Route path="/error" element={<Error/>}/>
                    <Route path="/*" element={<Error/>}/>
                </Routes>

                <div className="footer">
                    <span className="span-class-one">
                        <img src={SpotifyLogo} alt="Spotify Logo" style={{ width: '110px', height: '33px' }}/>
                    </span>

                    <p className="mt-2">Made by <a target="_blank" rel="noreferrer" href="https://linktr.ee/valentinojosh" className="text-blue-500 underline">Josh Valentino</a></p>
                </div>
            </BrowserRouter>


        </div>
    );
}

export default AppRouter;