import React, {useEffect, useState} from "react";
import axios from "axios";
import Table from "./Table";
import {Container} from "react-bootstrap";

// const handleTopFiveButton = () => {
//     axios.get('http://localhost:8080/api/topfive').then((res) => {
//         // Handle successful retrieval of data -> return a table component passing through data or something
//         console.log(res.data)
//     }).catch((error) => {
//         // Handle login error
//     });
// }
//
// const TopFiveButton = () => {
//     return (
//         <button className="btn btn-success btn-lg" onClick={handleTopFiveButton}>
//             Get Top 5 Artists
//         </button>
//     );
// }

export default function TempDash({ code }) {
    const [artists, setArtists] = useState([])

    //use effect to keep out of dash if not logged in
    useEffect(() => {
        axios
            .get("http://localhost:8080/api/dash")
            .then(res => {
                if(res.data){
                    window.location = "/temp"
                }
                console.log(res.data)
                //When we hit here trigger another get to get the dash information?
            })
            .catch(() => {
                window.location = "/"
            })
    },[])

    //use effect to get top five data upon initial load
    //mighte need to check timing on this as the data might not be ready by the time of redirect?
    useEffect(() => {
        axios
            .get("http://localhost:8080/api/topfive")
            .then(res => {
                console.log(res.data)
                setArtists(res.data)
            })
            .catch((error) => {
                console.log("error in getting top five - use effect TempDash")
                console.error(error)
            })
    },[])

    return  (
        <div>
            <Container
                className="d-flex justify-content-center align-items-center"
                style={{ minHeight: "100vh" }}
            >
                <h2>Dash:</h2>

                <div>Top 5:</div>

                {artists ? <Table data={artists}/> : <div>tempty?:</div>}

            </Container>
        </div>
    )
}