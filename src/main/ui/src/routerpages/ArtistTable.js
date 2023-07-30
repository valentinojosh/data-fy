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
                    <td>{item.images && item.images.length > 0 && <img src={item.images[0].url} alt={item.name}/>}</td>
                    <td>{index+1}. {item.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default ArtistTable;