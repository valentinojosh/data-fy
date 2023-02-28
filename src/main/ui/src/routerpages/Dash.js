import React, {useEffect, useState} from "react";
import axios from "axios";
import Table from "./Table";

export default function Dash() {
    const [artists, setArtists] = useState([])
    const [genres, setGenres] = useState([])
    const [dataSelection, setDataSelection] = useState('btngenres')

    const handleDataSelection = (id) => {
        setDataSelection(id);
    }

    //use effect to keep out of dash if not logged in
    useEffect(() => {
        axios
            .get("http://localhost:8080/api/dash")
            .then(res => {
                if(res.data){
                    window.location = "/"
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

    //use effect to get top genres data upon initial load
    //mighte need to check timing on this as the data might not be ready by the time of redirect?
    useEffect(() => {
        axios
            .get("http://localhost:8080/api/genres")
            .then(res => {
                console.log(res.data)
                setGenres(res.data)
            })
            .catch((error) => {
                console.log("error in getting genres - use effect Dash")
                console.error(error)
            })
    },[])

    return  (
        <div>
                <div className="content-container">
                    <div className="left-content">
                        <div>Top 5:</div>
                        {dataSelection === 'btnartists' ? <Table data={artists}/> : dataSelection === 'btngenres' ? <div>genres:</div> : <div></div>}

                    </div>
                    <div className="right-content">
                        <div className="options-container">
                            <div className="customize">
                                Customize
                            </div>
                            <div className="query-selection">
                                <div className="query-selection-header">
                                    Data Selection
                                </div>
                                <div className="query-selection-options">
                                    <button id='btngenres' style={{textDecoration: dataSelection === 'btngenres' ? 'underline' : 'none'}}
                                            onClick={() => handleDataSelection('btngenres')}>
                                        Top Genres</button>
                                    <button id='btnartists' style={{textDecoration: dataSelection === 'btnartists' ? 'underline' : 'none'}}
                                                          onClick={() => handleDataSelection('btnartists')}>
                                        Top Artists</button>
                                    <button id='btnsongs' style={{textDecoration: dataSelection === 'btnsongs' ? 'underline' : 'none'}}
                                            onClick={() => handleDataSelection('btnsongs')}>
                                        Top Songs</button>
                                    <button id='btnwrapped' style={{textDecoration: dataSelection === 'btnwrapped' ? 'underline' : 'none'}}
                                            onClick={() => handleDataSelection('btnwrapped')}>
                                        Wrapped</button>
                                </div>
                            </div>
                            <div className="query-selection">
                                <div className="query-selection-header">
                                    Include top artists from
                                </div>
                                <div className="query-selection-options">
                                    <button>Last 4 weeks</button>
                                    <button>Last 6 months</button>
                                    <button>Last All Time</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
        </div>
    )
}