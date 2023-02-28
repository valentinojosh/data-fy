import React from 'react';

const Table = (props) => {
    return (
        <table>
            <tbody>
            {props.data.map((item) => (
                <tr key={item.id}>
                    <td>{item.name}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default Table;