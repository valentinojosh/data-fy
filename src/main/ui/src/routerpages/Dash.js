import React, {useEffect, useState} from "react";
import axios from "axios";
import ArtistTable from "./ArtistTable";
import Genres from "./Genres";
import TrackTable from "./TrackTable";
import Wrapped from "./Wrapped";

export default function Dash() {
    const [objectData, setObjectData] = useState([]);
    const [artists, setArtists] = useState([]);
    const [genres, setGenres] = useState([]);
    const [tracks, setTracks] = useState([]);
    const [recommendations, setRecommendations] = useState([]);
    const [dataSelection, setDataSelection] = useState('btngenres');
    const [artistSelection, setArtistSelection] = useState('Short');
    const [trackSelection, setTrackSelection] = useState('Short');
    const [minutes, setMinutes] = useState(0);

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

    const handleObjectData = (data) => {
        setObjectData(data)
        setArtists(data['artistsShort'])
        setGenres(data['topSixGenres'])
        setTracks(data['tracksShort'])
        setRecommendations(data['recommendations'])
        setMinutes(data['minutes'])
    }

    // Initial use effect upon load of dash
    useEffect(() => {
        //api call to check auth status
        axios
            .get("http://https://data-fy.uc.r.appspot.com/api/dash",{ withCredentials: true })
            .then(res => {
                let storedData = null;
                try {
                    storedData = JSON.parse(localStorage.getItem("objectData"));
                } catch (error) {
                    console.error("Error parsing objectData from localStorage:", error);
                }

                if(!storedData){
                    //api call to fetch data
                    axios
                        .get("http://https://data-fy.uc.r.appspot.com/api/data",{ withCredentials: true })
                        .then(res => {
                            localStorage.setItem("objectData", JSON.stringify(res.data));
                            console.log(res.data)
                            handleObjectData(res.data);
                        })
                        .catch((error) => {
                            console.log("error in getting object data")
                            console.error(error)
                        })
                }
                else{
                    console.log(storedData);
                    handleObjectData(storedData);
                }
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    window.location = "/"; // Redirect to login
                }
            })
    },[])

    return  (
        <div>
            {!localStorage.getItem("objectData") ?
                <div className="loading-container"><div className="customize">Loading...</div></div>:
                <div className="content-container">
                    <div className="left-content">
                        {dataSelection === 'btnartists' ? <ArtistTable data={artists}/> :
                            dataSelection === 'btngenres' ? <Genres data={genres}/> :
                                dataSelection === 'btnsongs' ? <TrackTable data={tracks}/> :
                                    <Wrapped artists={artists} tracks={recommendations} minutes={minutes}/>}
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
                </div>}
        </div>
    )
}