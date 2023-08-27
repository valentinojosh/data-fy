import React from 'react';

const ArtistTable = (props) => {
    return (
        <table>
            <tbody>
            {props.data.map((item, index) => (
                <tr key={item.id}
                    style={{
                        border: "1px solid black",
                        backgroundColor: index % 2 === 0 ? "lightgrey" : "white"
                    }}>
                    <td className="imageCell">{item.images && item.images.length > 0 && <a target="_blank" rel="noreferrer" href={item.externalUrls.externalUrls.spotify}><img src={item.images[0].url} alt={item.name}/></a>
                    }</td>
                    <td className="textCell">{index+1}. {item.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default ArtistTable;