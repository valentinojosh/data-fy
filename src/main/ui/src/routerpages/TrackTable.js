import React from 'react';

const TrackTable = (props) => {
    return (
        <table>
            <tbody>
            {props.data.map((item, index) => (
                <tr key={item.id}
                    style={{
                        border: "1px solid black",
                        backgroundColor: index % 2 === 0 ? "lightgrey" : "white"
                    }}>
                    <td>{item.album.images && item.album.images.length > 0 && <img src={item.album.images[0].url} alt={item.name}/>}</td>
                    <td>{index+1}. {item.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default TrackTable;