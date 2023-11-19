import React from "react";
import {useSearchParams} from "react-router-dom";

export default function Error() {
    let [searchParams] = useSearchParams();
    let message = searchParams.get('message') || "Unknown route";

    return  (
        <div>
                <div className="content-container" style={{ display: 'flex', flexDirection: 'column'}}>
                    <div className="d-flex align-items-center justify-content-center vh-75">
                        <div className="text-center">
                            <h1 className="display-1 fw-bold">Error</h1>
                            <p className="fs-3"><span className="text-danger">Oops!</span> There was an issue.</p>
                            <p className="lead">
                                Message: {message}
                            </p>
                            <a href="/" className="btn btn-success btn-lg s-green">Go Home</a>
                        </div>
                    </div>
                </div>
        </div>
    )
}