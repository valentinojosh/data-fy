import React, {useEffect, useState} from "react";
import axios from "axios";
import ArtistTable from "./ArtistTable";
import GTable from "./GTable";
import Genres from "./Genres";
import TrackTable from "./TrackTable";

export default function Dash() {
    const [objectData, setObjectData] = useState([])
    const [artists, setArtists] = useState([])
    const [genres, setGenres] = useState([])
    const [tracks, setTracks] = useState([])
    const [dataSelection, setDataSelection] = useState('btngenres')
    const [artistSelection, setArtistSelection] = useState('Short')
    const [trackSelection, setTrackSelection] = useState('Short')
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const handleDataSelection = (id) => {
        setDataSelection(id);
    }

    const handleArtistSelection = (id) => {
        let arrayName = `artists${id}`;
        setArtistSelection(id);
        setArtists(objectData[arrayName]);
    }

    const handleTrackSelection = (id) => {
        let arrayName = `tracks${id}`;
        setTrackSelection(id);
        setTracks(objectData[arrayName]);
    }

    //use effect to keep out of dash if not logged in
    useEffect(() => {
        axios
            .get("http://localhost:8080/api/dash")
            .then(res => {
                if(res.data){
                    window.location = "/"
                }
            })
            .catch(() => {
                window.location = "/"
            })
    },[])

    useEffect(() => {
        axios
            .post("http://localhost:8080/api/data", {},{ withCredentials: true })
            .then(() => {
                setIsAuthenticated(true);
                // Successfully sent the request, no need to do anything with the response.
            })
            .catch((error) => {
                console.log("error in getting base data - use effect TempDash")
                console.error(error)
            })
    },[])

    //use effect to get spotify object data upon initial load
    //mighte need to check timing on this as the data might not be ready by the time of redirect?
    useEffect(() => {
        if(isAuthenticated) {
            axios
                .get("http://localhost:8080/api/objectdata")
                .then(res => {
                    console.log(res.data)
                    setObjectData(res.data)
                    setArtists(res.data['artistsShort'])
                    setGenres(res.data['topSixGenres'])
                    setTracks(res.data['tracksShort'])
                })
                .catch((error) => {
                    console.log("error in getting object data - use effect TempDash")
                    console.error(error)
                })
        }
    },[isAuthenticated])

    return  (
        <div>
                <div className="content-container">
                    <div className="left-content">
                        {dataSelection === 'btnartists' ? <ArtistTable data={artists}/> :
                            dataSelection === 'btngenres' ? <GTable data={genres}/> :
                                dataSelection === 'btnsongs' ? <TrackTable data={tracks}/> :
                                    <Genres data={genres}/>}
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
                            {dataSelection === 'btnartists' ?
                            <div className="query-selection">
                                <div className="query-selection-header">
                                    Include top artists from
                                </div>
                                <div className="query-selection-options">
                                    <button id='Short' style={{textDecoration: artistSelection === 'Short' ? 'underline' : 'none'}}
                                            onClick={() => handleArtistSelection('Short')}>
                                        Last 4 weeks</button>
                                    <button id='Medium' style={{textDecoration: artistSelection === 'Medium' ? 'underline' : 'none'}}
                                            onClick={() => handleArtistSelection('Medium')}>
                                        Last 6 months</button>
                                    <button id='Long' style={{textDecoration: artistSelection === 'Long' ? 'underline' : 'none'}}
                                            onClick={() => handleArtistSelection('Long')}>
                                        All Time</button>
                                </div>
                            </div> :
                                dataSelection === 'btnsongs' ?
                                    <div className="query-selection">
                                        <div className="query-selection-header">
                                            Include top artists from
                                        </div>
                                        <div className="query-selection-options">
                                            <button id='Short' style={{textDecoration: trackSelection === 'Short' ? 'underline' : 'none'}}
                                                    onClick={() => handleTrackSelection('Short')}>
                                                Last 4 weeks</button>
                                            <button id='Medium' style={{textDecoration: trackSelection === 'Medium' ? 'underline' : 'none'}}
                                                    onClick={() => handleTrackSelection('Medium')}>
                                                Last 6 months</button>
                                            <button id='Long' style={{textDecoration: trackSelection === 'Long' ? 'underline' : 'none'}}
                                                    onClick={() => handleTrackSelection('Long')}>
                                                All Time</button>
                                        </div>
                                    </div> :
                                    <div/>}
                        </div>
                    </div>
                </div>
        </div>
    )
}